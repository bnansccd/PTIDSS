package com.ptidss.intel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.common.domain.Result;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.SecurityUtils;
import com.ptidss.common.utils.ConfigCryptoService;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.decision.service.DecisionService;
import com.ptidss.intel.domain.IntelNews;
import com.ptidss.intel.domain.IntelPushRule;
import com.ptidss.intel.domain.IntelSource;
import com.ptidss.intel.mapper.IntelNewsMapper;
import com.ptidss.intel.mapper.IntelPushRuleMapper;
import com.ptidss.intel.mapper.IntelSourceMapper;
import com.ptidss.message.domain.MessageRecord;
import com.ptidss.message.mapper.MessageRecordMapper;
import com.ptidss.system.domain.SysRole;
import com.ptidss.system.domain.SysUser;
import com.ptidss.system.mapper.SysRoleMapper;
import com.ptidss.system.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 情报中心（对齐 OpenAPI V1.1 /intel/**；FR-INT-04 情报中心 RE-01 P0）
 * 业务规则：情报源统一台账（60+ 源，种子代表性子集）；情报流归一化标签+重要度分级；
 * 推送规则（标签×重要度→角色/渠道，high 级实时推送 ≤30s）；区域数据权限（全国情报可见）
 */
@Slf4j
@Service
public class IntelService {

    /** 种子情报源：编码/名称/类型/采集方式/频率（与 07_seed_data.sql 第 8 节同源，10 源代表子集） */
    private static final String[][] SEED_SOURCES = {
            {"INTL-PROV-JS", "江苏省电力交易中心公告", "announcement", "api", "5 */1 * * *"},
            {"INTL-NEA", "国家能源局政策发布", "policy", "crawl", "0 8 * * *"},
            {"INTL-GRID-JS", "江苏电网调度信息披露", "supply_demand", "api", "*/10 * * * *"},
            {"INTL-MET-CMA", "中央气象台气象预警", "weather", "file", "0 */6 * * *"},
            {"INTL-PRICE-NEM", "国家电力交易中心-出清价格", "price", "api", "5 分钟"},
            {"INTL-PRICE-PROV", "省电力交易中心-现货出清", "price", "api", "15 分钟"},
            {"INTL-ANN-GRID", "电网公司-运行公告", "announcement", "api", "1 小时"},
            {"INTL-OPI-MEDIA", "行业媒体-市场舆情", "opinion", "crawl", "4 小时"},
            {"INTL-POL-ENERGY", "能源局-交易规则公告", "policy", "crawl", "每日"},
            {"INTL-SD-DEMAND", "负荷预测公告", "supply_demand", "api", "1 小时"},
    };

    private final IntelSourceMapper intelSourceMapper;
    private final IntelNewsMapper intelNewsMapper;
    private final IntelPushRuleMapper intelPushRuleMapper;
    private final MessageRecordMapper messageRecordMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserMapper sysUserMapper;
    private final DecisionService decisionService;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;
    private final ConfigCryptoService configCryptoService;

    public IntelService(IntelSourceMapper intelSourceMapper,
                        IntelNewsMapper intelNewsMapper,
                        IntelPushRuleMapper intelPushRuleMapper,
                        MessageRecordMapper messageRecordMapper,
                        SysRoleMapper sysRoleMapper,
                        SysUserMapper sysUserMapper,
                        DecisionService decisionService,
                        SecurityUtils securityUtils, ObjectMapper objectMapper,
                        ConfigCryptoService configCryptoService) {
        this.intelSourceMapper = intelSourceMapper;
        this.intelNewsMapper = intelNewsMapper;
        this.intelPushRuleMapper = intelPushRuleMapper;
        this.messageRecordMapper = messageRecordMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserMapper = sysUserMapper;
        this.decisionService = decisionService;
        this.securityUtils = securityUtils;
        this.objectMapper = objectMapper;
        this.configCryptoService = configCryptoService;
    }

    // ---------- 情报源台账 ----------

    /** 情报源表为空时写入种子台账（幂等） */
    private void ensureSources() {
        Long count = intelSourceMapper.selectCount(new LambdaQueryWrapper<IntelSource>());
        if (count != null && count > 0) {
            return;
        }
        for (String[] s : SEED_SOURCES) {
            IntelSource source = new IntelSource();
            source.setSourceCode(s[0]);
            source.setSourceName(s[1]);
            source.setIntelType(s[2]);
            source.setFetchMode(s[3]);
            source.setFrequency(s[4]);
            source.setStatus("enabled");
            intelSourceMapper.insert(source);
        }
    }

    /**
     * 情报源配置与状态（连接参数对外脱敏：敏感字段一律 ******，明文/密文均不外泄；
     * P2 字段级加密：保存时敏感字段加密存储，列表只暴露脱敏视图）
     */
    public List<Map<String, Object>> listSources() {
        ensureSources();
        List<IntelSource> list = intelSourceMapper.selectList(new LambdaQueryWrapper<IntelSource>()
                .orderByAsc(IntelSource::getIntelType)
                .orderByAsc(IntelSource::getSourceCode));
        List<Map<String, Object>> result = new ArrayList<>();
        for (IntelSource s : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", String.valueOf(s.getId()));
            item.put("sourceCode", s.getSourceCode());
            item.put("sourceName", s.getSourceName());
            item.put("intelType", s.getIntelType());
            item.put("fetchMode", s.getFetchMode());
            item.put("connType", s.getConnType() == null ? "api" : s.getConnType());
            item.put("connConfig", configCryptoService.maskFields(s.getConnConfig()));
            item.put("frequency", s.getFrequency());
            item.put("status", s.getStatus());
            // V2.5 采集状态（行情接口监测：最近成功/失败原因/连续失败）
            item.put("lastSuccessAt", s.getLastSuccessAt());
            item.put("lastError", s.getLastError());
            item.put("consecutiveFailures",
                    s.getConsecutiveFailures() == null ? 0 : s.getConsecutiveFailures());
            result.add(item);
        }
        return result;
    }

    /** 新增情报源（台账登记；编码唯一，类型/采集方式/对接方式/状态枚举校验；V2.2 支持连接配置，
     *  P2 连接参数敏感字段自动加密存储，返回脱敏视图） */
    public Map<String, Object> createSource(String sourceCode, String sourceName, String intelType,
                                            String fetchMode, String connType, String connConfig,
                                            String frequency, String status) {
        if (StrUtils.isBlank(sourceCode) || StrUtils.isBlank(sourceName) || StrUtils.isBlank(intelType)) {
            throw new ServiceException("情报源编码/名称/类型不能为空");
        }
        List<String> types = java.util.Arrays.asList("price", "weather", "supply_demand",
                "policy", "announcement", "opinion");
        if (!types.contains(intelType)) {
            throw new ServiceException("情报源类型不合法：" + intelType);
        }
        List<String> modes = java.util.Arrays.asList("api", "crawl", "file");
        if (StrUtils.isNotBlank(fetchMode) && !modes.contains(fetchMode)) {
            throw new ServiceException("采集方式不合法：" + fetchMode);
        }
        List<String> connTypes = java.util.Arrays.asList("api", "jwt", "oauth2", "basic", "file", "poll");
        if (StrUtils.isNotBlank(connType) && !connTypes.contains(connType)) {
            throw new ServiceException("对接方式不合法：" + connType);
        }
        Long exists = intelSourceMapper.selectCount(new LambdaQueryWrapper<IntelSource>()
                .eq(IntelSource::getSourceCode, sourceCode));
        if (exists != null && exists > 0) {
            throw new ServiceException("情报源编码已存在：" + sourceCode);
        }
        IntelSource src = new IntelSource();
        src.setSourceCode(sourceCode);
        src.setSourceName(sourceName);
        src.setIntelType(intelType);
        src.setFetchMode(StrUtils.isBlank(fetchMode) ? "api" : fetchMode);
        src.setConnType(StrUtils.isBlank(connType) ? "api" : connType);
        src.setConnConfig(configCryptoService.encryptFields(StrUtils.isBlank(connConfig) ? "{}" : connConfig));
        src.setFrequency(StrUtils.isBlank(frequency) ? "1 小时" : frequency);
        src.setStatus(StrUtils.isBlank(status) ? "enabled" : status);
        intelSourceMapper.insert(src);
        return toSourceView(src);
    }

    /**
     * 更新情报源对接配置（连接方式/连接参数/频率/启停；客户部署适配；仅 admin）。
     * P2 字段级加密：提交连接参数中敏感字段为 ****** 表示未修改（保留库中原加密值），
     * 其余字段重新加密落库；返回脱敏视图。
     */
    public Map<String, Object> updateSource(Long id, String fetchMode, String connType, String connConfig,
                                            String frequency, String status) {
        IntelSource exist = intelSourceMapper.selectById(id);
        if (exist == null) {
            throw new ServiceException("情报源不存在");
        }
        List<String> modes = java.util.Arrays.asList("api", "crawl", "file");
        if (StrUtils.isNotBlank(fetchMode) && !modes.contains(fetchMode)) {
            throw new ServiceException("采集方式不合法：" + fetchMode);
        }
        List<String> connTypes = java.util.Arrays.asList("api", "jwt", "oauth2", "basic", "file", "poll");
        if (StrUtils.isNotBlank(connType) && !connTypes.contains(connType)) {
            throw new ServiceException("对接方式不合法：" + connType);
        }
        if (StrUtils.isNotBlank(status) && !"enabled".equals(status) && !"disabled".equals(status)) {
            throw new ServiceException("状态仅支持 enabled/disabled");
        }
        IntelSource update = new IntelSource();
        update.setId(id);
        update.setFetchMode(fetchMode);
        update.setConnType(connType);
        if (connConfig != null) {
            update.setConnConfig(configCryptoService.encryptFields(
                    configCryptoService.mergeMasked(exist.getConnConfig(), connConfig)));
        }
        update.setFrequency(frequency);
        update.setStatus(status);
        intelSourceMapper.updateById(update);
        return toSourceView(intelSourceMapper.selectById(id));
    }

    /** 台账脱敏视图（连接参数敏感字段 ******，密文不外泄） */
    private Map<String, Object> toSourceView(IntelSource s) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", String.valueOf(s.getId()));
        item.put("sourceCode", s.getSourceCode());
        item.put("sourceName", s.getSourceName());
        item.put("intelType", s.getIntelType());
        item.put("fetchMode", s.getFetchMode());
        item.put("connType", s.getConnType() == null ? "api" : s.getConnType());
        item.put("connConfig", configCryptoService.maskFields(s.getConnConfig()));
        item.put("frequency", s.getFrequency());
        item.put("status", s.getStatus());
        return item;
    }

    // ---------- 情报流 ----------

    /** 情报流懒生成（确定性模拟，seed=周期哈希可复现；全国情报可见） */
    private void ensureNews() {
        String regionCode = securityUtils.getRegionCode();
        Long count = intelNewsMapper.selectCount(new LambdaQueryWrapper<IntelNews>());
        if (count != null && count > 0) {
            return;
        }
        String[] types = {"price", "weather", "supply_demand", "policy", "announcement", "opinion"};
        String[][] headlines = {
                {"现货市场日前出清价格发布：均价 {V} 元/MWh", "省间现货交易价格波动提示"},
                {"未来三天区域气温偏高，负荷预计攀升", "流域来水偏丰，水电出力增加"},
                {"全网最大负荷预测 {V} 万千瓦", "局部地区供需偏紧预警"},
                {"电力现货市场交易规则修订征求意见", "峰谷分时电价政策调整通知"},
                {"本月市场化交易电量同比增长 {V}%", "绿电交易规模再创新高"},
                {"多家机构看好下半年电力需求复苏", "市场分析：现货价差套利空间收窄"},
        };
        String[] typeSources = {"INTL-PRICE-NEM", "INTL-MET-CMA", "INTL-GRID-JS",
                "INTL-NEA", "INTL-PROV-JS", "INTL-OPI-MEDIA"};
        java.util.Random random = new java.util.Random(1125899906842597L);
        for (String type : types) {
            int idx = java.util.Arrays.asList(types).indexOf(type);
            for (int i = 0; i < 3; i++) {
                IntelNews news = new IntelNews();
                news.setSourceCode(typeSources[idx]);
                news.setTitle(headlines[idx][i % 2].replace("{V}", String.valueOf(300 + random.nextInt(900))));
                news.setContent("【" + type + "】" + headlines[idx][i % 2] + "。本条为情报中心模拟数据，"
                        + "归一化标签：市场/品种/影响，供联调演示。");
                news.setRegionCode(random.nextBoolean() ? null : (i % 2 == 0 ? "CN-31" : "CN-32"));
                List<String> tags = new ArrayList<>();
                tags.add("现货");
                tags.add("省内");
                if (i % 2 == 0) {
                    tags.add("价格");
                }
                news.setNormalizedTags(toJson(tags));
                news.setImportance(i == 0 ? "high" : (i == 1 ? "medium" : "low"));
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR, -(idx * 3 + i));
                news.setPublishedAt(cal.getTime());
                news.setPushStatus("none");
                intelNewsMapper.insert(news);
            }
        }
    }

    /** 情报流（归一化标签/重要度筛选，分页；区域：当前省 + 全国） */
    public Result<Page<IntelNews>> listNews(String importance, String intelType,
                                            long pageNo, long pageSize) {
        ensureNews();
        String regionCode = securityUtils.getRegionCode();
        LambdaQueryWrapper<IntelNews> qw = new LambdaQueryWrapper<>();
        if (StrUtils.isNotBlank(intelType)) {
            // intel_news 无类型列（DDL v1.0.2 条目 8：应用层校验），经情报源台账映射
            List<String> codes = intelSourceMapper.selectList(
                            new LambdaQueryWrapper<IntelSource>()
                                    .eq(IntelSource::getIntelType, intelType)
                                    .select(IntelSource::getSourceCode))
                    .stream().map(IntelSource::getSourceCode)
                    .collect(java.util.stream.Collectors.toList());
            qw.in(StrUtils.isNotEmpty(codes), IntelNews::getSourceCode, codes);
        }
        qw.eq(StrUtils.isNotBlank(importance), IntelNews::getImportance, importance)
                .and(StrUtils.isNotBlank(regionCode), w ->
                        w.eq(IntelNews::getRegionCode, regionCode).or().isNull(IntelNews::getRegionCode))
                .orderByDesc(IntelNews::getPublishedAt);
        return Result.success(intelNewsMapper.selectPage(new Page<>(pageNo, pageSize), qw));
    }

    // ---------- 推送规则 ----------

    /** 配置情报推送规则（标签+重要度→角色/渠道；high 级实时推送 ≤30s） */
    public Map<String, Object> createPushRule(String ruleName, List<String> matchTags,
                                              String importance, List<String> targets) {
        if (StrUtils.isBlank(ruleName) || matchTags == null || matchTags.isEmpty()
                || StrUtils.isBlank(importance)) {
            throw new ServiceException("规则名称/匹配标签/重要度不能为空");
        }
        IntelPushRule rule = new IntelPushRule();
        rule.setRuleName(ruleName);
        rule.setTagsFilter(toJson(matchTags));
        rule.setImportanceFilter(importance);
        List<String> roles = targets == null || targets.isEmpty()
                ? new ArrayList<>(java.util.Collections.singletonList("trader"))
                : targets;
        rule.setTargetRoles(toJson(roles));
        List<String> channels = new ArrayList<>();
        channels.add("web");
        if ("high".equals(importance)) {
            channels.add("sms");
            channels.add("miniapp");
        }
        rule.setChannel(toJson(channels));
        rule.setStatus("active");
        intelPushRuleMapper.insert(rule);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("ruleId", String.valueOf(rule.getId()));
        resp.put("channels", channels);
        return resp;
    }

    /** 推送规则列表 */
    public List<IntelPushRule> listPushRules() {
        return intelPushRuleMapper.selectList(new LambdaQueryWrapper<IntelPushRule>()
                .orderByDesc(IntelPushRule::getCreatedAt));
    }

    // ---------- 推送规则执行器（情报→消息中心联动，FR-INT-04 / 评审决议④） ----------

    /**
     * 执行全部 active 推送规则：匹配未推送情报（标签交集 × 重要度一致）→ 按目标角色
     * 派发个人消息（msg_type=intel_push，receiver_id=角色下全部用户）→ 更新情报推送状态。
     * 幂等：同情报同用户已派发则跳过（biz_ref=INTEL-{newsId}）
     */
    public synchronized Map<String, Object> executePushRules() {
        List<IntelPushRule> rules = intelPushRuleMapper.selectList(
                new LambdaQueryWrapper<IntelPushRule>().eq(IntelPushRule::getStatus, "active"));
        int matchedNews = 0;
        int pushedMessages = 0;
        boolean highIntelPushed = false;
        for (IntelPushRule rule : rules) {
            List<String> ruleTags = parseJsonArray(rule.getTagsFilter());
            List<String> channels = parseJsonArray(rule.getChannel());
            if (channels.isEmpty()) {
                channels = Collections.singletonList("web");
            }
            List<IntelNews> candidates = intelNewsMapper.selectList(
                    new LambdaQueryWrapper<IntelNews>()
                            .eq(IntelNews::getPushStatus, "none")
                            .eq(IntelNews::getImportance, rule.getImportanceFilter()));
            for (IntelNews news : candidates) {
                if (Collections.disjoint(ruleTags, parseJsonArray(news.getNormalizedTags()))) {
                    continue; // 标签无交集，不推送
                }
                matchedNews++;
                List<SysUser> receivers = findUsersByTargetRoles(parseJsonArray(rule.getTargetRoles()));
                for (SysUser user : receivers) {
                    Long exists = messageRecordMapper.selectCount(new LambdaQueryWrapper<MessageRecord>()
                            .eq(MessageRecord::getReceiverId, user.getId())
                            .eq(MessageRecord::getBizRef, "INTEL-" + news.getId()));
                    if (exists != null && exists > 0) {
                        continue; // 幂等：已派发过
                    }
                    MessageRecord msg = new MessageRecord();
                    msg.setMsgType("intel_push");
                    msg.setReceiverId(user.getId());
                    msg.setTitle(news.getTitle());
                    msg.setContent(news.getContent());
                    msg.setChannel(toJson(channels));
                    msg.setReadStatus("unread");
                    msg.setBizRef("INTEL-" + news.getId());
                    messageRecordMapper.insert(msg);
                    pushedMessages++;
                }
                news.setPushStatus("pushed");
                intelNewsMapper.updateById(news);
                highIntelPushed = highIntelPushed
                        || "high".equalsIgnoreCase(news.getImportance());
            }
        }
        // 情报触发式重算（FR-INT-04 深化）：本轮推送含 high 重要度情报时，对近 24h 待审决策会话
        // 批量刷新情报评分快照（供人工确认前感知情报变化）；异常不阻断推送主流程
        int reassessed = 0;
        if (highIntelPushed) {
            try {
                reassessed = decisionService.reassessPendingSessions();
            } catch (Exception e) {
                log.warn("情报推送触发决策会话重评失败：{}", e.getMessage());
            }
        }
        log.info("情报推送执行完成：匹配情报 {} 条，派发消息 {} 条，触发决策会话情报重评 {} 个",
                matchedNews, pushedMessages, reassessed);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("matchedNews", matchedNews);
        resp.put("pushedMessages", pushedMessages);
        resp.put("intelReassessed", reassessed);
        return resp;
    }

    /** 定时兜底：每 30s 扫描一次（契约 high 级实时推送 ≤30s 语义） */
    @Scheduled(fixedDelay = 30_000, initialDelay = 10_000)
    public void scheduledPush() {
        try {
            executePushRules();
        } catch (Exception e) {
            log.warn("定时推送执行异常：{}", e.getMessage());
        }
    }

    /** 按目标角色编码集合查用户（role_ids JSONB 包含匹配） */
    private List<SysUser> findUsersByTargetRoles(List<String> roleCodes) {
        if (roleCodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<SysRole> roles = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getRoleCode, roleCodes));
        List<SysUser> users = new ArrayList<>();
        for (SysRole role : roles) {
            users.addAll(sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                    .apply("role_ids @> {0}::jsonb", "[" + role.getId() + "]")));
        }
        return users;
    }

    /** JSONB 数组字符串 → List<String>（兼容历史对象数组 [{"role":"trader"}]） */
    private List<String> parseJsonArray(String json) {
        if (StrUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        try {
            List<?> list = objectMapper.readValue(json, List.class);
            List<String> out = new ArrayList<>();
            for (Object o : list) {
                if (o instanceof String) {
                    out.add((String) o);
                } else if (o instanceof Map) {
                    Object role = ((Map<?, ?>) o).get("role");
                    if (role != null) {
                        out.add(String.valueOf(role));
                    }
                }
            }
            return out;
        } catch (Exception e) {
            log.warn("JSON 数组解析失败：{}，原始值：{}", e.getMessage(), json);
            return new ArrayList<>();
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new ServiceException("JSON 序列化失败");
        }
    }
}
