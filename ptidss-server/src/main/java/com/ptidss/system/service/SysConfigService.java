package com.ptidss.system.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.utils.ConfigCryptoService;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.system.domain.SysConfig;
import com.ptidss.system.mapper.SysConfigMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 系统配置中心（DDL 17 sys_config；系统管理--系统配置 全面实现）
 * 对标 PRD："申报段数、限价参数可配置""规则参数化配置，快速适配各省规则变化"
 * 评审决议（通道/周期口径/多省模式）与等保三级安全参数全部参数化、可在线维护；
 * 敏感项（is_sensitive）value 以 {"secret": ...} 经 ConfigCryptoService 加密落库，
 * 列表/详情对外一律脱敏 ******，编辑回显 ****** 表示保留原值。
 */
@Service
public class SysConfigService {

    /** 敏感值回显占位（与 intel_source conn_config 编辑回显一致：提交 ****** = 保留原值） */
    private static final String MASK = "******";

    /** 配置类型合法性 */
    private static final Set<String> TYPES = new HashSet<>(Arrays.asList("string", "number", "boolean", "select", "json"));

    /** 分组白名单（DDL 17 九组） */
    private static final Set<String> GROUPS = new HashSet<>(Arrays.asList(
            "trade_rule", "settlement", "region", "optimize", "forecast", "model", "agent", "security", "notification"));

    /**
     * 内置种子（与 DDL 17 双保险：库表为空时自动写入；33 项 = PRD 规则参数化 + 评审决议 + 等保安全参数）
     * 列：configKey, configName, description, group, type, enumValues(逗号分隔或空), value, sortOrder
     */
    private static final String[][] SEED_CONFIG = {
            // 交易规则（PRD：申报段数/限价参数可配置；规则参数化快速适配各省规则变化）
            {"rule.declareSegments", "申报段数", "各省申报段数差异（现货/中长期），规则参数化适配各省", "trade_rule", "number", "", "3", "10"},
            {"rule.priceUpperLimit", "申报限价上限", "申报价格上限（元/MWh），按省规则可调", "trade_rule", "number", "", "1500", "20"},
            {"rule.priceLowerLimit", "申报限价下限", "申报价格下限（元/MWh）", "trade_rule", "number", "", "0", "30"},
            {"rule.bidStep", "报价最小单位", "申报价格最小变动单位（元/MWh）", "trade_rule", "number", "", "1", "40"},
            {"rule.tradeFeeRate", "交易手续费率", "交易手续费率（如 0.001 = 千分之一）", "trade_rule", "number", "", "0.001", "50"},
            {"rule.maxDeclareTime", "申报截止时间", "日申报截止时间（HH:mm），逾期不可申报", "trade_rule", "string", "", "10:00", "60"},
            // 结算（评审决议③：周期口径双口径）
            {"settlement.periodMode", "结算周期口径", "结算周期口径：natural_month 自然月 / trading_month 交易月（浙江等省）", "settlement", "select", "natural_month,trading_month", "natural_month", "10"},
            {"settlement.reconcileThreshold", "结算核对差异阈值", "结算核对差异容忍阈值（元），超出即偏差告警", "settlement", "number", "", "0.01", "20"},
            {"settlement.autoReconcile", "自动核对开关", "结算完成后自动执行核对（对账）", "settlement", "boolean", "", "true", "30"},
            {"settlement.reconcileNotify", "核对偏差通知角色", "结算偏差告警通知角色（逗号分隔角色编码）", "settlement", "string", "", "trader,manager", "40"},
            // 多省（评审决议⑤：多省模式与区域路由）
            {"region.mode", "多省模式", "多省模式：single 单省 / multi 多省", "region", "select", "single,multi", "multi", "10"},
            {"region.defaultRegionCode", "默认区域编码", "默认区域（单省模式/未路由时兜底）", "region", "string", "", "CN-33", "20"},
            {"region.sourcesQueryTimeout", "各省行情查询超时", "各省行情/情报源查询超时（毫秒）", "region", "number", "", "5000", "30"},
            // 优化（联合优化求解器与参数）
            {"optimize.solver", "联合优化求解器", "优化求解器：HiGHS 默认 / SCIP / Gurobi 兜底", "optimize", "select", "HiGHS,SCIP,Gurobi", "HiGHS", "10"},
            {"optimize.timeLimit", "求解时间上限", "联合优化求解时间上限（秒）", "optimize", "number", "", "120", "20"},
            {"optimize.gapTolerance", "求解精度", "求解最优性间隙容差（0~1）", "optimize", "number", "", "0.01", "30"},
            // 预测
            {"forecast.horizonDays", "预测天数", "负荷/价格预测展望天数", "forecast", "number", "", "7", "10"},
            {"forecast.retrainIntervalHours", "模型重训间隔", "预测模型自动重训间隔（小时）", "forecast", "number", "", "24", "20"},
            {"forecast.autoRun", "定时预测开关", "开启后按任务 cron 自动执行预测", "forecast", "boolean", "", "true", "30"},
            // 模型（算法包规范）
            {"model.uploadMaxSizeMB", "算法包上传上限", "算法包上传大小上限（MB）", "model", "number", "", "25", "10"},
            {"model.defaultTimeoutSec", "模型推理超时", "模型推理默认超时（秒）", "model", "number", "", "30", "20"},
            {"model.allowAutoAdapt", "算法包全自动适配", "开启后按算法包规范（manifest/同包 params.json）全自动适配", "model", "boolean", "", "true", "30"},
            // 智能体（PRD：智能体参数调优）
            {"agent.defaultTemperature", "LLM 默认温度", "智能体 LLM 推理默认温度（0~2）", "agent", "number", "", "0.70", "10"},
            {"agent.maxTokens", "LLM 最大令牌", "智能体 LLM 单次响应最大令牌数", "agent", "number", "", "2048", "20"},
            {"agent.autoReview", "情报重评开关", "新情报到达自动触发决策会话情报重评", "agent", "boolean", "", "true", "30"},
            // 安全（等保三级：身份鉴别/会话/审计）
            {"security.loginFailMax", "登录失败锁定次数", "连续登录失败达到该次数即锁定（等保 8.1.4.1）", "security", "number", "", "5", "10"},
            {"security.loginLockMinutes", "登录锁定分钟", "登录失败锁定持续时长（分钟），到期自动解锁", "security", "number", "", "10", "20"},
            {"security.sessionTimeoutMin", "会话超时（分钟）", "访问令牌有效期（分钟），超时需重新登录", "security", "number", "", "120", "30"},
            {"security.captchaEnabled", "验证码开关", "登录图形验证码（交付态开启）", "security", "boolean", "", "true", "40"},
            {"security.auditRetentionDays", "审计日志保留天数", "操作审计日志保留天数，超期归档清理", "security", "number", "", "365", "50"},
            // 通知
            {"notification.intelPushEnabled", "情报推送开关", "情报中心推送总开关（消息派发）", "notification", "boolean", "", "true", "10"},
            {"notification.smsChannelEnabled", "短信渠道开关", "短信渠道（high 级情报/告警实时推送）", "notification", "boolean", "", "false", "20"},
            {"notification.highPriorityDelaySec", "high 级推送延迟", "high 级情报推送延迟秒数（≤30s 实时）", "notification", "number", "", "30", "30"},
    };

    private final SysConfigMapper sysConfigMapper;
    private final ConfigCryptoService configCryptoService;

    /** 值读取缓存（V3.1 性能优化：配置读取高频路径，写操作失效；TTL 5 分钟兜底外部改库） */
    private final Cache<String, SysConfig> valueCache = Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    /** 种子懒加载完成标志（避免每次读取重复执行建表/计数 DDL） */
    private volatile boolean seedChecked = false;

    public SysConfigService(SysConfigMapper sysConfigMapper, ConfigCryptoService configCryptoService) {
        this.sysConfigMapper = sysConfigMapper;
        this.configCryptoService = configCryptoService;
    }

    // ── 管理端：列表 / 详情 / 新增 / 编辑 / 删除 ──────────────────────────

    /** 配置列表（按分组过滤；敏感项 value 一律脱敏 ******） */
    public List<SysConfig> list(String group, String keyword) {
        ensureConfigs();
        LambdaQueryWrapper<SysConfig> qw = new LambdaQueryWrapper<>();
        qw.eq(StrUtils.isNotBlank(group), SysConfig::getConfigGroup, group);
        if (StrUtils.isNotBlank(keyword)) {
            qw.and(w -> w.like(SysConfig::getConfigName, keyword).or().like(SysConfig::getConfigKey, keyword));
        }
        qw.orderByAsc(SysConfig::getConfigGroup).orderByAsc(SysConfig::getSortOrder).orderByAsc(SysConfig::getId);
        List<SysConfig> configs = sysConfigMapper.selectList(qw);
        configs.forEach(this::maskForView);
        return configs;
    }

    /** 详情（敏感项脱敏，编辑回显 ****** = 保留原值） */
    public SysConfig getById(Long id) {
        SysConfig config = sysConfigMapper.selectById(id);
        if (config == null) {
            throw new ServiceException("配置项不存在");
        }
        maskForView(config);
        return config;
    }

    /** 对外展示规范化：敏感项脱敏；string/select 值去 JSON 引号；number/boolean/json 原样 */
    private void maskForView(SysConfig c) {
        if (Boolean.TRUE.equals(c.getIsSensitive())) {
            c.setValue(MASK);
            return;
        }
        if (StrUtils.equalsAny(c.getConfigType(), "string", "select")) {
            c.setValue(rawText(c.getValue()));
        }
    }

    /** 新增配置（key 唯一；类型/枚举校验；敏感项加密落库） */
    public void create(SysConfig config) {
        ensureConfigs();
        validate(config);
        Long exists = sysConfigMapper.selectCount(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, config.getConfigKey()));
        if (exists != null && exists > 0) {
            throw new ServiceException("配置键已存在：" + config.getConfigKey());
        }
        if (config.getIsSensitive() == null) {
            config.setIsSensitive(false);
        }
        if (config.getIsBuiltin() == null) {
            config.setIsBuiltin(false);
        }
        if (StrUtils.isBlank(config.getStatus())) {
            config.setStatus("enabled");
        }
        if (Boolean.TRUE.equals(config.getIsSensitive())) {
            config.setValue(encryptValue(config.getValue()));
        } else {
            config.setValue(normalizeValue(config.getValue(), config.getConfigType()));
        }
        sysConfigMapper.insert(config);
        valueCache.invalidate(config.getConfigKey());
    }

    /** 编辑配置（内置项 key 不可改、可改值；敏感项 ****** 保留原值；类型/枚举校验） */
    public void update(SysConfig config) {
        SysConfig exist = sysConfigMapper.selectById(config.getId());
        if (exist == null) {
            throw new ServiceException("配置项不存在");
        }
        validate(config);
        if (Boolean.TRUE.equals(exist.getIsBuiltin())
                && !exist.getConfigKey().equals(config.getConfigKey())) {
            throw new ServiceException("系统内置配置键为代码读取标识，不可修改（可修改名称/说明/值/状态）");
        }
        if (Boolean.TRUE.equals(exist.getIsSensitive())) {
            // 敏感项回显为 ******：表示未修改，保留库中原值
            if (MASK.equals(config.getValue())) {
                config.setValue(exist.getValue());
            } else {
                config.setValue(encryptValue(config.getValue()));
            }
        } else {
            config.setValue(normalizeValue(config.getValue(), config.getConfigType()));
        }
        sysConfigMapper.updateById(config);
        valueCache.invalidate(exist.getConfigKey());
        valueCache.invalidate(config.getConfigKey());
    }

    /** 删除配置（内置项禁止删除，可禁用；软删除） */
    public void delete(Long id) {
        SysConfig exist = sysConfigMapper.selectById(id);
        if (exist == null) {
            throw new ServiceException("配置项不存在");
        }
        if (Boolean.TRUE.equals(exist.getIsBuiltin())) {
            throw new ServiceException("系统内置配置不可删除（PRD 基线项），可将状态置为禁用");
        }
        sysConfigMapper.deleteById(id);
        valueCache.invalidate(exist.getConfigKey());
    }

    // ── 业务读取（配置中心下发，enabled 才生效，缺失/禁用回退默认值） ──────

    /** 读取字符串配置（敏感项解密返回明文） */
    public String getString(String key, String defaultValue) {
        SysConfig config = findEnabled(key);
        if (config == null) {
            return defaultValue;
        }
        return Boolean.TRUE.equals(config.getIsSensitive()) ? decryptValue(config.getValue()) : rawText(config.getValue());
    }

    /** 读取整数配置 */
    public Integer getInt(String key, Integer defaultValue) {
        String v = getString(key, defaultValue == null ? null : String.valueOf(defaultValue));
        if (StrUtils.isBlank(v)) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(v);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** 读取布尔配置 */
    public Boolean getBool(String key, Boolean defaultValue) {
        String v = getString(key, defaultValue == null ? null : String.valueOf(defaultValue));
        if (StrUtils.isBlank(v)) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(v) || "1".equals(v);
    }

    // ── 内部工具 ──────────────────────────────────────────────────────────

    private SysConfig findEnabled(String key) {
        SysConfig cached = valueCache.getIfPresent(key);
        if (cached != null) {
            return cached;
        }
        ensureConfigs();
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, key)
                .eq(SysConfig::getStatus, "enabled")
                .last("LIMIT 1"));
        if (config != null) {
            valueCache.put(key, config);
        }
        return config;
    }

    /** 值规范化：string/select 去 JSON 引号；其余原样（number/boolean/json） */
    private String rawText(String value) {
        if (StrUtils.isBlank(value)) {
            return value;
        }
        String v = value.trim();
        if ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'"))) {
            return v.substring(1, v.length() - 1);
        }
        return v;
    }

    /** 敏感值落库：{"secret": raw} → encryptFields（secret 命中敏感字段白名单） */
    private String encryptValue(String raw) {
        JSONObject wrap = new JSONObject();
        wrap.put("secret", raw);
        return configCryptoService.encryptFields(wrap.toJSONString());
    }

    /**
     * 值按类型序列化为合法 JSON 文本（value 列为 JSONB）：
     * string/select → JSON 字符串（如 10:00 → "10:00"）；number → JSON 数字；boolean → true/false；json → 原样。
     */
    private String normalizeValue(String raw, String type) {
        if (StrUtils.isBlank(raw)) {
            return raw;
        }
        String v = raw.trim();
        switch (type) {
            case "string":
            case "select":
                return JSON.toJSONString(rawText(v));
            case "number":
                // BigDecimal 规范化：1500 → "1500"，0.001 → "0.001"（避免 Double 尾零 1500.0）
                return new BigDecimal(rawText(v)).stripTrailingZeros().toPlainString();
            case "boolean":
                return JSON.toJSONString(Boolean.parseBoolean(rawText(v)));
            default: // json：要求已是合法 JSON
                return v;
        }
    }

    /** 敏感值读取：decryptFields 解出明文 */
    private String decryptValue(String stored) {
        String decrypted = configCryptoService.decryptFields(stored);
        try {
            JSONObject obj = JSON.parseObject(decrypted);
            return obj == null ? stored : obj.getString("secret");
        } catch (Exception e) {
            return rawText(stored);
        }
    }

    /** 类型/分组/枚举/值合法性校验 */
    private void validate(SysConfig config) {
        if (StrUtils.isBlank(config.getConfigKey()) || StrUtils.isBlank(config.getConfigName())) {
            throw new ServiceException("配置键与配置名称必填");
        }
        if (!TYPES.contains(config.getConfigType())) {
            throw new ServiceException("非法配置类型：" + config.getConfigType() + "（string/number/boolean/select/json）");
        }
        if (!GROUPS.contains(config.getConfigGroup())) {
            throw new ServiceException("非法配置分组：" + config.getConfigGroup());
        }
        if (StrUtils.isBlank(config.getValue())) {
            throw new ServiceException("配置值必填");
        }
        if ("select".equals(config.getConfigType())) {
            List<String> enums = config.getEnumValues();
            if (enums == null || enums.isEmpty()) {
                throw new ServiceException("select 类型必须提供枚举候选 enumValues");
            }
            if (!enums.contains(rawText(config.getValue()))) {
                throw new ServiceException("配置值不在枚举范围内：" + enums);
            }
        }
        if ("number".equals(config.getConfigType())) {
            try {
                Double.parseDouble(rawText(config.getValue()));
            } catch (NumberFormatException e) {
                throw new ServiceException("配置值必须为数字：" + config.getValue());
            }
        }
        if ("boolean".equals(config.getConfigType())
                && !("true".equals(rawText(config.getValue())) || "false".equals(rawText(config.getValue())))) {
            throw new ServiceException("boolean 类型配置值必须为 true/false");
        }
    }

    /** 种子懒加载：幂等建表后，表为空时写入 33 项内置配置（与 DDL 17 双保险；用户删除后不重生） */
    private void ensureConfigs() {
        if (seedChecked) {
            return;
        }
        sysConfigMapper.createTableIfNotExists();
        sysConfigMapper.createUniqueIndex();
        Long count = sysConfigMapper.selectCount(new LambdaQueryWrapper<SysConfig>());
        if (count != null && count > 0) {
            seedChecked = true;
            return;
        }
        Long deletedCount = sysConfigMapper.selectCount(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getDeleted, true));
        if (deletedCount != null && deletedCount > 0) {
            seedChecked = true;
            return;
        }
        for (String[] s : SEED_CONFIG) {
            SysConfig c = new SysConfig();
            c.setConfigKey(s[0]);
            c.setConfigName(s[1]);
            c.setDescription(s[2]);
            c.setConfigGroup(s[3]);
            c.setConfigType(s[4]);
            if (!s[5].isEmpty()) {
                c.setEnumValues(Arrays.asList(s[5].split(",")));
            }
            c.setValue(normalizeValue(s[6], s[4]));
            c.setIsSensitive(false);
            c.setIsBuiltin(true);
            c.setStatus("enabled");
            c.setSortOrder(Integer.valueOf(s[7]));
            sysConfigMapper.insert(c);
        }
        seedChecked = true;
    }
}
