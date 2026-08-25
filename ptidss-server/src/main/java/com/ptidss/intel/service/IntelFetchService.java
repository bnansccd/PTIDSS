package com.ptidss.intel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.intel.domain.IntelNews;
import com.ptidss.intel.domain.IntelSource;
import com.ptidss.intel.mapper.IntelNewsMapper;
import com.ptidss.intel.mapper.IntelSourceMapper;
import com.ptidss.common.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 行情/情报采集服务（V2.5 遗留建议①：各省行情接口接入 + 重试/降级 + 状态监测）：
 * 1) 定时扫描 enabled 情报源，按 conn_config.frequencyMinutes 判断采集到期；
 * 2) 真实 HTTP 拉取：连接超时 timeoutMs → 失败重试 retries 次（指数退避 1s/2s/4s）；
 * 3) 降级：api 失败 → fallbackUrl（crawl 兜底）→ 仍失败 → 状态留痕；
 * 4) 状态监测：lastSuccessAt/lastError/consecutiveFailures（≥10 自动 disabled 并告警）；
 * 5) 测试/演示（mock=true 或未配置 endpoint）：确定性规则生成模拟行情，保证可观测性；
 *    生产环境将 conn_config.mock 置 false 并配置真实 endpoint 后自动走真实拉取。
 */
@Slf4j
@Service
public class IntelFetchService {

    /** 连续失败上限：达到后自动停用并告警 */
    private static final int MAX_CONSECUTIVE_FAILURES = 10;

    private final IntelSourceMapper intelSourceMapper;
    private final IntelNewsMapper intelNewsMapper;
    private final ObjectMapper objectMapper;

    public IntelFetchService(IntelSourceMapper intelSourceMapper,
                             IntelNewsMapper intelNewsMapper,
                             ObjectMapper objectMapper) {
        this.intelSourceMapper = intelSourceMapper;
        this.intelNewsMapper = intelNewsMapper;
        this.objectMapper = objectMapper;
    }

    /** 定时采集：每 60s 扫描一次，按各源频率判断到期（与情报推送 30s 兜底互不干扰） */
    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void scheduledFetch() {
        try {
            fetchAll(false);
        } catch (Exception e) {
            log.warn("行情定时采集异常：{}", e.getMessage());
        }
    }

    /** 全量采集（force=true 忽略频率立即重跑；逐源隔离异常，单源失败不阻断其他源） */
    public Map<String, Object> fetchAll(boolean force) {
        List<IntelSource> sources = intelSourceMapper.selectList(
                new LambdaQueryWrapper<IntelSource>().eq(IntelSource::getStatus, "enabled"));
        int due = 0, success = 0, failed = 0, skipped = 0;
        long now = System.currentTimeMillis();
        for (IntelSource source : sources) {
            if (!force && !isDue(source, now)) {
                skipped++;
                continue;
            }
            due++;
            try {
                if (fetchSource(source)) {
                    success++;
                } else {
                    failed++;
                }
            } catch (Exception e) {
                failed++;
                markFailed(source, "采集异常：" + e.getMessage());
                log.warn("情报源采集异常：{} {}", source.getSourceCode(), e.getMessage());
            }
        }
        log.info("行情采集执行完成：到期 {} 个（成功 {} / 失败 {} / 跳过未到期 {}）",
                due, success, failed, skipped);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("total", sources.size());
        resp.put("due", due);
        resp.put("success", success);
        resp.put("failed", failed);
        resp.put("skipped", skipped);
        return resp;
    }

    /** 各源采集状态视图（状态监测：最近成功/失败原因/连续失败/频率/端点域名脱敏） */
    public List<Map<String, Object>> fetchStatus() {
        List<IntelSource> sources = intelSourceMapper.selectList(
                new LambdaQueryWrapper<IntelSource>().orderByAsc(IntelSource::getSourceCode));
        List<Map<String, Object>> result = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (IntelSource s : sources) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", String.valueOf(s.getId()));
            item.put("sourceCode", s.getSourceCode());
            item.put("sourceName", s.getSourceName());
            item.put("intelType", s.getIntelType());
            item.put("fetchMode", s.getFetchMode());
            item.put("frequency", s.getFrequency());
            item.put("status", s.getStatus());
            item.put("lastSuccessAt", s.getLastSuccessAt() == null ? null : fmt.format(s.getLastSuccessAt()));
            item.put("lastError", s.getLastError());
            item.put("consecutiveFailures", s.getConsecutiveFailures() == null ? 0 : s.getConsecutiveFailures());
            Map<String, Object> cfg = parseConfig(s);
            item.put("frequencyMinutes", cfg.getOrDefault("frequencyMinutes", ""));
            item.put("endpoint", hostOf(String.valueOf(cfg.getOrDefault("endpoint", ""))));
            item.put("mock", isMock(cfg));
            item.put("healthy", "enabled".equals(s.getStatus())
                    && (s.getConsecutiveFailures() == null || s.getConsecutiveFailures() < MAX_CONSECUTIVE_FAILURES));
            result.add(item);
        }
        return result;
    }

    // ---------- 单源采集 ----------

    /** 采集单个情报源；true=成功（真实拉取/降级/模拟任一路径），false=失败留痕 */
    private boolean fetchSource(IntelSource source) {
        Map<String, Object> cfg = parseConfig(source);
        boolean mock = isMock(cfg);
        String endpoint = String.valueOf(cfg.getOrDefault("endpoint", ""));
        int timeoutMs = intOf(cfg.get("timeoutMs"), 5000);
        int retries = intOf(cfg.get("retries"), 2);
        String fallbackUrl = String.valueOf(cfg.getOrDefault("fallbackUrl", ""));
        String regionCode = String.valueOf(cfg.getOrDefault("regionCode", ""));

        // 路径1：模拟模式（测试/演示；endpoint 未配置或 mock=true）
        if (mock || StrUtils.isBlank(endpoint)) {
            buildMockNews(source, regionCode);
            markSuccess(source);
            log.info("情报源模拟采集：{} 生成模拟情报", source.getSourceCode());
            return true;
        }
        // 路径2：真实 HTTP 拉取（失败重试 retries 次，指数退避）
        String body = httpGetWithRetry(endpoint, timeoutMs, retries);
        if (StrUtils.isNotBlank(body)) {
            buildFetchedNews(source, regionCode, body, false);
            markSuccess(source);
            log.info("情报源采集成功：{} 响应 {} 字节", source.getSourceCode(), body.length());
            return true;
        }
        // 路径3：降级（fallbackUrl crawl 兜底）
        if (StrUtils.isNotBlank(fallbackUrl)) {
            String fallback = httpGetWithRetry(fallbackUrl, timeoutMs, 1);
            if (StrUtils.isNotBlank(fallback)) {
                buildFetchedNews(source, regionCode, fallback, true);
                markSuccess(source);
                log.warn("情报源降级采集：{} 经 fallbackUrl 兜底成功", source.getSourceCode());
                return true;
            }
        }
        // 全路径失败：状态留痕（连续失败 ≥10 自动停用并告警）
        markFailed(source, "主端点与降级端点均拉取失败（endpoint=" + endpoint + "）");
        return false;
    }

    /** 到期判断：无成功记录立即采集；否则按 frequencyMinutes（缺省 60）判断 */
    private boolean isDue(IntelSource source, long now) {
        Date last = source.getLastSuccessAt();
        if (last == null) {
            return true;
        }
        int minutes = intOf(parseConfig(source).get("frequencyMinutes"), 60);
        return now - last.getTime() >= minutes * 60_000L;
    }

    /** 模拟情报生成（确定性：源编码 + 分钟窗口哈希 → 价格 300~800 元/MWh） */
    private void buildMockNews(IntelSource source, String regionCode) {
        String type = source.getIntelType() == null ? "price" : source.getIntelType();
        long window = System.currentTimeMillis() / 60000L;
        java.util.Random random = new java.util.Random(source.getSourceCode().hashCode() ^ window);
        String now = new SimpleDateFormat("HH:mm").format(new Date());
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String title;
        String content;
        List<String> tags = new ArrayList<>();
        tags.add("现货");
        if ("price".equals(type)) {
            int price = 300 + random.nextInt(500);
            title = "【" + shortName(source) + "现货】" + now + " 出清均价 " + price + " 元/MWh";
            content = "【价格】" + shortName(source) + "电力现货市场 " + date + " " + now + " 最新出清均价 "
                    + price + " 元/MWh，较昨日同时段" + (random.nextBoolean() ? "上涨" : "回落")
                    + " " + random.nextInt(80) + " 元/MWh。本条为行情接入模拟数据（mock=true），"
                    + "生产环境配置真实 endpoint 后自动切换真实拉取。";
            tags.add("价格");
        } else if ("supply_demand".equals(type)) {
            int load = 3000 + random.nextInt(4000);
            title = "【" + shortName(source) + "电网】供需披露更新：预测最高负荷 " + load + " 万千瓦";
            content = "【供需】" + shortName(source) + "电网 " + date + " 供需披露：预测最高负荷 "
                    + load + " 万千瓦，可调容量 " + (load / 10) + " 万千瓦，供需形势"
                    + (random.nextBoolean() ? "偏紧" : "宽松") + "。本条为行情接入模拟数据。";
            tags.add("供需");
        } else if ("announcement".equals(type)) {
            title = "【" + shortName(source) + "】运行公告更新（" + date + "）";
            content = "【公告】" + shortName(source) + "运行公告于 " + date + " " + now
                    + " 更新，请关注机组检修、输电通道及市场运行安排。本条为行情接入模拟数据。";
            tags.add("公告");
        } else {
            title = "【" + shortName(source) + "】现货交易规则公告更新（" + date + "）";
            content = "【政策】" + shortName(source) + "现货交易相关规则公告于 " + date
                    + " 更新，请以交易中心正式发布为准。本条为行情接入模拟数据。";
            tags.add("政策");
        }
        insertNews(source, title, content, regionCode, tags, "medium");
    }

    /** 真实拉取响应落情报（降级标记写入正文） */
    private void buildFetchedNews(IntelSource source, String regionCode, String body, boolean degraded) {
        String compact = body.replace('\r', ' ').replace('\n', ' ').replaceAll("\\s+", " ").trim();
        if (compact.length() > 300) {
            compact = compact.substring(0, 300) + "…";
        }
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String title = source.getSourceName() + "（" + date + "）";
        String content = "【" + typeName(source.getIntelType()) + "】" + compact
                + (degraded ? "（本条经降级端点兜底采集）" : "");
        List<String> tags = new ArrayList<>();
        tags.add("现货");
        if (source.getIntelType() != null) {
            tags.add(typeName(source.getIntelType()));
        }
        insertNews(source, title, content, regionCode, tags, "medium");
    }

    private void insertNews(IntelSource source, String title, String content, String regionCode,
                            List<String> tags, String importance) {
        // 幂等查重（V3.1 性能/数据治理优化）：同一源近 3 分钟窗口内同标题已入库则跳过，
        // 避免 mock/真实端点 60s 轮询在分钟窗口内重复插入造成情报表膨胀
        Long exists = intelNewsMapper.selectCount(new LambdaQueryWrapper<IntelNews>()
                .eq(IntelNews::getSourceCode, source.getSourceCode())
                .eq(IntelNews::getTitle, title)
                .gt(IntelNews::getPublishedAt, new Date(System.currentTimeMillis() - 180_000L)));
        if (exists != null && exists > 0) {
            log.debug("情报重复跳过：{} {}", source.getSourceCode(), title);
            return;
        }
        IntelNews news = new IntelNews();
        news.setSourceCode(source.getSourceCode());
        news.setTitle(title);
        news.setContent(content);
        news.setRegionCode(StrUtils.isBlank(regionCode) ? null : regionCode);
        news.setNormalizedTags(toJson(tags));
        news.setImportance(importance);
        news.setPublishedAt(new Date());
        news.setPushStatus("none");
        intelNewsMapper.insert(news);
    }

    // ---------- 状态留痕 ----------

    private void markSuccess(IntelSource source) {
        IntelSource update = new IntelSource();
        update.setId(source.getId());
        update.setLastSuccessAt(new Date());
        update.setLastError(null);
        update.setConsecutiveFailures(0);
        intelSourceMapper.updateById(update);
    }

    private void markFailed(IntelSource source, String error) {
        int failures = (source.getConsecutiveFailures() == null ? 0 : source.getConsecutiveFailures()) + 1;
        IntelSource update = new IntelSource();
        update.setId(source.getId());
        update.setLastError(error.length() > 500 ? error.substring(0, 500) : error);
        update.setConsecutiveFailures(failures);
        if (failures >= MAX_CONSECUTIVE_FAILURES) {
            update.setStatus("disabled");
            log.error("情报源连续失败 {} 次已自动停用：{}（{}）——请检查接口配置或联系数据方",
                    failures, source.getSourceCode(), source.getSourceName());
        }
        intelSourceMapper.updateById(update);
    }

    // ---------- HTTP 拉取 ----------

    /** HTTP GET + 指数退避重试（1s/2s/4s）；成功返回响应体（≤256KB），失败返回 null */
    private String httpGetWithRetry(String url, int timeoutMs, int retries) {
        for (int attempt = 0; attempt <= retries; attempt++) {
            String body = httpGet(url, timeoutMs);
            if (body != null) {
                return body;
            }
            if (attempt < retries) {
                try {
                    Thread.sleep(1000L << attempt);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        return null;
    }

    /** 单次 HTTP GET：连接/读超时 timeoutMs，响应体 ≤256KB，非 2xx 或异常返回 null */
    private String httpGet(String url, int timeoutMs) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(timeoutMs);
            conn.setReadTimeout(timeoutMs);
            conn.setRequestProperty("User-Agent", "PTIDSS-IntelFetcher/2.5");
            conn.setRequestProperty("Accept", "application/json, text/plain, */*");
            int code = conn.getResponseCode();
            if (code < 200 || code >= 300) {
                log.debug("行情拉取非 2xx：{} → {}", url, code);
                return null;
            }
            try (InputStream in = conn.getInputStream()) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                long read = 0;
                int n;
                while ((n = in.read(buf)) != -1 && read < 256L * 1024) {
                    out.write(buf, 0, n);
                    read += n;
                }
                return new String(out.toByteArray(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.debug("行情拉取失败：{} → {}", url, e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // ---------- 工具 ----------

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseConfig(IntelSource source) {
        if (StrUtils.isBlank(source.getConnConfig())) {
            return new LinkedHashMap<>();
        }
        try {
            Object parsed = objectMapper.readValue(source.getConnConfig(), Object.class);
            return parsed instanceof Map ? (Map<String, Object>) parsed : new LinkedHashMap<>();
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    /** 模拟开关：conn_config.mock 显式置 false 走真实拉取；缺省（未配置 endpoint）视为模拟 */
    private boolean isMock(Map<String, Object> cfg) {
        Object mock = cfg.get("mock");
        if (mock != null) {
            return Boolean.parseBoolean(String.valueOf(mock));
        }
        return StrUtils.isBlank(String.valueOf(cfg.getOrDefault("endpoint", "")));
    }

    private int intOf(Object value, int defaultValue) {
        if (value == null || StrUtils.isBlank(String.valueOf(value))) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** 端点脱敏：仅保留协议 + 主机（状态视图不回显完整 URL） */
    private String hostOf(String url) {
        if (StrUtils.isBlank(url)) {
            return "";
        }
        try {
            URL u = new URL(url);
            return u.getProtocol() + "://" + u.getHost();
        } catch (Exception e) {
            return url.length() > 60 ? url.substring(0, 60) : url;
        }
    }

    /** 源名称压缩：取"xx电力交易中心"中的省份短名（如 山东电力交易中心 → 山东） */
    private String shortName(IntelSource source) {
        String name = source.getSourceName() == null ? source.getSourceCode() : source.getSourceName();
        String region = name.replace("电力交易中心", "").replace("电网", "")
                .replace("现货出清价格", "").replace("运行公告", "")
                .replace("供需披露", "").replace("现货交易规则公告", "")
                .replace("电力市场", "").replace("-", "").trim();
        return StrUtils.isBlank(region) ? name : region;
    }

    private String typeName(String type) {
        if ("price".equals(type)) {
            return "价格";
        }
        if ("supply_demand".equals(type)) {
            return "供需";
        }
        if ("announcement".equals(type)) {
            return "公告";
        }
        if ("policy".equals(type)) {
            return "政策";
        }
        return "情报";
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }
}
