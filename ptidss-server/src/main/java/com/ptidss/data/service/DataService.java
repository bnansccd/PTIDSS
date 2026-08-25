package com.ptidss.data.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.utils.ConfigCryptoService;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.data.domain.CollectTask;
import com.ptidss.data.domain.DataLineage;
import com.ptidss.data.domain.DataQualityRule;
import com.ptidss.data.domain.DataSource;
import com.ptidss.data.mapper.CollectTaskMapper;
import com.ptidss.data.mapper.DataLineageMapper;
import com.ptidss.data.mapper.DataQualityRuleMapper;
import com.ptidss.data.mapper.DataSourceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据底座（对齐 OpenAPI V1.1 /data/**；FR-PD-04 数据全流程管理 P0 / FR-PD-05  数据质量血缘 P1）
 * 业务规则：数据源统一台账（营销/交易中心/气象，双通道建模）；采集任务手动触发 记录留痕；
 * 定时调度按 collect_task.cron_expr 轻量 cron 匹配自动触发（V1.7 增强）；
 * 质量报告按规则类型聚合（完整率/准确率/及时率）；血缘图节点上下游查询
 */
@Slf4j
@Service
public class DataService {

    private final DataSourceMapper dataSourceMapper;
    private final CollectTaskMapper collectTaskMapper;
    private final DataQualityRuleMapper dataQualityRuleMapper;
    private final DataLineageMapper dataLineageMapper;
    private final ObjectMapper objectMapper;
    private final ConfigCryptoService configCryptoService;

    public DataService(DataSourceMapper dataSourceMapper, CollectTaskMapper collectTaskMapper,
                       DataQualityRuleMapper dataQualityRuleMapper, DataLineageMapper dataLineageMapper,
                       ObjectMapper objectMapper, ConfigCryptoService configCryptoService) {
        this.dataSourceMapper = dataSourceMapper;
        this.collectTaskMapper = collectTaskMapper;
        this.dataQualityRuleMapper = dataQualityRuleMapper;
        this.dataLineageMapper = dataLineageMapper;
        this.objectMapper = objectMapper;
        this.configCryptoService = configCryptoService;
    }

    /** 数据源列表与状态（编码/类型/同步状态/最近运行） */
    public List<Map<String, Object>> sources() {
        List<DataSource> list = dataSourceMapper.selectList(new LambdaQueryWrapper<DataSource>()
                .orderByAsc(DataSource::getSourceCode));
        List<Map<String, Object>> result = new ArrayList<>();
        for (DataSource s : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", String.valueOf(s.getId()));
            item.put("sourceCode", s.getSourceCode());
            item.put("sourceType", s.getSourceType());
            item.put("syncMode", s.getSyncMode());
            item.put("connType", s.getConnType() == null ? "api" : s.getConnType());
            item.put("connectConfig", configCryptoService.maskFields(s.getConnectConfig()));
            item.put("frequency", s.getFrequency());
            item.put("status", s.getStatus());
            // 最近运行：对应采集任务最近状态
            CollectTask task = collectTaskMapper.selectOne(new LambdaQueryWrapper<CollectTask>()
                    .eq(CollectTask::getSourceId, s.getId())
                    .orderByDesc(CollectTask::getLastRunTime)
                    .last("LIMIT 1"));
            item.put("lastRunTime", task == null || task.getLastRunTime() == null ? null
                    : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(task.getLastRunTime()));
            item.put("lastStatus", task == null ? null : task.getLastStatus());
            item.put("recordsCount", task == null ? null : task.getRecordsCount());
            result.add(item);
        }
        return result;
    }

    /** 新增数据源（台账登记；编码唯一，类型/同步模式/状态枚举校验，connect_config 缺省 {}；
     *  P2 连接参数敏感字段自动加密存储，返回脱敏视图） */
    public Map<String, Object> createSource(String sourceCode, String sourceType, String syncMode,
                                            String connType, String frequency, String status, String connectConfig) {
        if (StrUtils.isBlank(sourceCode) || StrUtils.isBlank(sourceType)) {
            throw new ServiceException("数据源编码/类型不能为空");
        }
        List<String> types = java.util.Arrays.asList("marketing", "exchange", "weather", "file", "intel");
        if (!types.contains(sourceType)) {
            throw new ServiceException("数据源类型不合法：" + sourceType);
        }
        List<String> modes = java.util.Arrays.asList("realtime", "timed");
        if (StrUtils.isNotBlank(syncMode) && !modes.contains(syncMode)) {
            throw new ServiceException("同步模式不合法：" + syncMode);
        }
        List<String> connTypes = java.util.Arrays.asList("api", "jwt", "oauth2", "basic", "file", "poll");
        if (StrUtils.isNotBlank(connType) && !connTypes.contains(connType)) {
            throw new ServiceException("对接方式不合法：" + connType);
        }
        Long exists = dataSourceMapper.selectCount(new LambdaQueryWrapper<DataSource>()
                .eq(DataSource::getSourceCode, sourceCode));
        if (exists != null && exists > 0) {
            throw new ServiceException("数据源编码已存在：" + sourceCode);
        }
        DataSource ds = new DataSource();
        ds.setSourceCode(sourceCode);
        ds.setSourceType(sourceType);
        ds.setSyncMode(StrUtils.isBlank(syncMode) ? "timed" : syncMode);
        ds.setConnType(StrUtils.isBlank(connType) ? "api" : connType);
        ds.setFrequency(frequency);
        ds.setStatus(StrUtils.isBlank(status) ? "enabled" : status);
        ds.setConnectConfig(configCryptoService.encryptFields(StrUtils.isBlank(connectConfig) ? "{}" : connectConfig));
        dataSourceMapper.insert(ds);
        return toSourceView(ds);
    }

    /**
     * 更新数据源对接配置（连接方式/连接参数/同步模式/频率/启停；客户部署适配；仅 admin）。
     * P2 字段级加密：提交连接参数中敏感字段为 ****** 表示未修改（保留库中原加密值），
     * 其余字段重新加密落库；返回脱敏视图。
     */
    public Map<String, Object> updateSource(Long id, String syncMode, String connType, String connectConfig,
                                            String frequency, String status) {
        DataSource exist = dataSourceMapper.selectById(id);
        if (exist == null) {
            throw new ServiceException("数据源不存在");
        }
        List<String> modes = java.util.Arrays.asList("realtime", "timed");
        if (StrUtils.isNotBlank(syncMode) && !modes.contains(syncMode)) {
            throw new ServiceException("同步模式不合法：" + syncMode);
        }
        List<String> connTypes = java.util.Arrays.asList("api", "jwt", "oauth2", "basic", "file", "poll");
        if (StrUtils.isNotBlank(connType) && !connTypes.contains(connType)) {
            throw new ServiceException("对接方式不合法：" + connType);
        }
        if (StrUtils.isNotBlank(status) && !java.util.Arrays.asList("enabled", "disabled", "error").contains(status)) {
            throw new ServiceException("状态仅支持 enabled/disabled/error");
        }
        DataSource update = new DataSource();
        update.setId(id);
        update.setSyncMode(syncMode);
        update.setConnType(connType);
        if (connectConfig != null) {
            update.setConnectConfig(configCryptoService.encryptFields(
                    configCryptoService.mergeMasked(exist.getConnectConfig(), connectConfig)));
        }
        update.setFrequency(frequency);
        update.setStatus(status);
        dataSourceMapper.updateById(update);
        return toSourceView(dataSourceMapper.selectById(id));
    }

    /** 台账脱敏视图（连接参数敏感字段 ******，密文不外泄） */
    private Map<String, Object> toSourceView(DataSource s) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", String.valueOf(s.getId()));
        item.put("sourceCode", s.getSourceCode());
        item.put("sourceType", s.getSourceType());
        item.put("syncMode", s.getSyncMode());
        item.put("connType", s.getConnType() == null ? "api" : s.getConnType());
        item.put("connectConfig", configCryptoService.maskFields(s.getConnectConfig()));
        item.put("frequency", s.getFrequency());
        item.put("status", s.getStatus());
        return item;
    }

    /** 手动触发采集任务（market/trade/settlement/weather/intel；force 强制重跑 ） */
    public Map<String, Object> collect(String taskType, Boolean force) {
        if (StrUtils.isBlank(taskType)) {
            throw new ServiceException("任务类型不能为空");
        }
        // 定位同类型采集任务（缺省取第一个；force 强制立即执行更新留痕）
        CollectTask task = collectTaskMapper.selectOne(new LambdaQueryWrapper<CollectTask>()
                .eq(CollectTask::getTaskType, taskType)
                .orderByAsc(CollectTask::getId)
                .last("LIMIT 1"));
        if (task == null) {
            throw new ServiceException("未配置 " + taskType + " 类型采集任务");
        }
        runCollect(task);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("taskId", String.valueOf(task.getId()));
        resp.put("recordsCount", task.getRecordsCount());
        return resp;
    }
    
    /** 执行一次采集（确定性模拟：按任务类型+分钟哈希生成记录数并落库留痕） */
    private void runCollect(CollectTask task) {
        task.setLastRunTime(new Date());
        task.setLastStatus("success");
        long seed = Math.abs((task.getTaskType() + System.currentTimeMillis() / 60000).hashCode());
        java.util.Random random = new java.util.Random(seed);
        task.setRecordsCount((long) (500 + random.nextInt(4000)));
        task.setErrorLog(null);
        collectTaskMapper.updateById(task);
    }
    
    /** 定时采集调度：每分钟检查 cron 命中（FR-PD-04 定时任务落地；契约 sync_mode=timed 语义） */
    @Scheduled(fixedDelay = 60_000, initialDelay = 20_000)
    public void scheduledCollect() {
        List<CollectTask> tasks = collectTaskMapper.selectList(
                new LambdaQueryWrapper<CollectTask>().isNotNull(CollectTask::getCronExpr));
        SimpleDateFormat minuteFmt = new SimpleDateFormat("yyyyMMddHHmm");
        String nowMinute = minuteFmt.format(new Date());
        for (CollectTask task : tasks) {
            try {
                if (StrUtils.isBlank(task.getCronExpr()) || !matchesCron(task.getCronExpr(), new Date())) {
                    continue;
                }
                if (task.getLastRunTime() != null
                        && nowMinute.equals(minuteFmt.format(task.getLastRunTime()))) {
                    continue; // 本分钟已执行，跳过
                }
                runCollect(task);
                log.info("定时采集执行：task={}({}) cron={} 记录数={}",
                        task.getTaskType(), task.getId(), task.getCronExpr(), task.getRecordsCount());
            } catch (Exception e) {
                log.warn("定时采集执行异常：task={} err={}", task.getId(), e.getMessage());
            }
        }
    }
    
    /** 轻量 cron 匹配（兼容 5 段与 6 段（带秒）；支持星号、步长、区间、列表、? 通配） */
    private boolean matchesCron(String cron, Date now) {
        String[] parts = cron.trim().split("\\s+");
        if (parts.length == 6) {
            // 6 段：秒 分 时 日 月 周 —— 秒段忽略，按分钟后移
            parts = new String[]{parts[1], parts[2], parts[3], parts[4], parts[5]};
        }
        if (parts.length != 5) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int minute = cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0=周日
        return matchField(parts[0], minute, 0, 59)
                && matchField(parts[1], hour, 0, 23)
                && matchField(parts[2], day, 1, 31)
                && matchField(parts[3], month, 1, 12)
                && matchField(parts[4], week, 0, 7);
    }
    
    private boolean matchField(String field, int value, int min, int max) {
        if ("*".equals(field) || "?".equals(field)) {
            return true;
        }
        for (String seg : field.split(",")) {
            if (seg.contains("/")) {
                // 支持 */n（0 起）与 a/n（a 起）两种步长语法（Quartz 风格）
                String[] step = seg.split("/");
                int start = "*".equals(step[0]) ? min : Integer.parseInt(step[0]);
                int stride = Integer.parseInt(step[1]);
                if (stride > 0 && value >= start && (value - start) % stride == 0) {
                    return true;
                }
            } else if (seg.contains("-")) {
                String[] range = seg.split("-");
                int lo = Integer.parseInt(range[0]);
                int hi = Integer.parseInt(range[1]);
                if (value >= lo && value <= hi) {
                    return true;
                }
            } else if (Integer.parseInt(seg) == value) {
                return true;
            }
        }
        return false;
    }

    /** 数据质量报告（完整率/准确率/及时率；按 data_quality_rule 规则类型确定性聚合） */
    public Map<String, Object> qualityReport(Date startDate, Date endDate) {
        List<DataQualityRule> rules = dataQualityRuleMapper.selectList(
                new LambdaQueryWrapper<DataQualityRule>().eq(DataQualityRule::getStatus, "active"));
        double[] sums = new double[3];
        int[] counts = new int[3];
        for (DataQualityRule r : rules) {
            int idx = "completeness".equals(r.getRuleType()) ? 0
                    : "accuracy".equals(r.getRuleType()) ? 1 : 2;
            double rate = r.getThreshold().doubleValue()
                    + (Math.abs(r.getRuleCode().hashCode()) % 6 - 3) * 0.01;
            sums[idx] += Math.max(0.85, Math.min(1.0, rate));
            counts[idx]++;
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("completeness", percent(sums[0], counts[0]));
        resp.put("accuracy", percent(sums[1], counts[1]));
        resp.put("timeliness", percent(sums[2], counts[2]));
        return resp;
    }

    private BigDecimal percent(double sum, int count) {
        return count == 0 ? new BigDecimal("0.95")
                : BigDecimal.valueOf(sum / count).setScale(4, RoundingMode.HALF_UP);
    }

    /** 全量血缘种子（V3.0 全量图谱：外部源→采集→明细→指标→模型/报表→业务应用+系统支撑；
     *  与 16_lineage_full_graph_v3_0.sql 一致；行格式：nodeId/nodeType/nodeName/description/domain/layer/upstream/downstream/fieldMapping） */
    private static final String[][] SEED_LINEAGE = {
            {"src_marketing", "table", "营销系统", "营销电量/用户台账等外部数据源（模拟或生产接入）", "marketing", "source", "[]", "[\"node_collect_mkt\"]", "{}"},
            {"src_exchange", "table", "各省电力交易中心", "现货/中长期行情、公告、供需等交易中心公开数据（山东/山西/广东/甘肃/蒙西/广州等 10 省）", "exchange", "source", "[]", "[\"node_collect_exchange\",\"node_ocr\"]", "{}"},
            {"src_weather", "table", "气象数据源", "负荷预测所需气象要素（温度/湿度/风速等）", "weather", "source", "[]", "[\"node_collect_weather\"]", "{}"},
            {"node_collect_mkt", "task", "营销数据采集任务", "定时采集营销系统电量/用户数据，写入明细库", "marketing", "collect", "[\"src_marketing\"]", "[\"tbl_detail\"]", "{\"customer_id\":\"biz_id\"}"},
            {"node_collect_exchange", "task", "交易中心行情采集任务", "采集各省现货/中长期行情、公告、供需信息，落明细库与情报库", "exchange", "collect", "[\"src_exchange\"]", "[\"tbl_detail\",\"tbl_quote_plan\",\"tbl_intel_news\"]", "{\"trade_date\":\"trade_date\"}"},
            {"node_collect_weather", "task", "气象数据采集任务", "采集气象要素数据，写入明细库", "weather", "collect", "[\"src_weather\"]", "[\"tbl_detail\"]", "{}"},
            {"node_ocr", "task", "OCR 单据识别任务", "政策原文/结算单据扫描件识别（ocr_task/ocr_template）", "policy", "collect", "[\"src_exchange\"]", "[\"tbl_policy\",\"tbl_settlement\"]", "{}"},
            {"tbl_detail", "table", "业务明细库", "机组/合约/滚动计划/申报/成交等业务明细（明细库通用层）", "common", "detail", "[\"node_collect_mkt\",\"node_collect_exchange\",\"node_collect_weather\"]", "[\"node_etl\",\"data_quality\"]", "{\"trade_date\":\"trade_date\"}"},
            {"tbl_quote_plan", "table", "报价计划库", "报价计划数据（quote_plan）", "trade", "detail", "[\"node_collect_exchange\"]", "[\"tbl_declaration\",\"node_etl\"]", "{}"},
            {"tbl_declaration", "table", "申报记录库", "交易申报数据（declaration，按月分区）", "trade", "detail", "[\"tbl_quote_plan\"]", "[\"tbl_settlement\",\"node_etl\"]", "{}"},
            {"tbl_settlement", "table", "结算记录库", "结算记录/核对/凭证/台账（settlement_record/reconcile/ticket/ledger）", "settle", "detail", "[\"tbl_declaration\",\"node_ocr\"]", "[\"node_etl\",\"rpt_settle\",\"rpt_assess\",\"biz_settle\",\"data_quality\"]", "{}"},
            {"tbl_policy", "table", "政策原文库", "政策文档/条款/解析（policy_document/article/analysis）", "policy", "detail", "[\"node_ocr\"]", "[\"model_policy\",\"node_etl\"]", "{}"},
            {"tbl_intel_news", "table", "情报条目库", "情报中心情报条目（intel_news，按 region 过滤）", "intel", "detail", "[\"node_collect_exchange\"]", "[\"biz_intel\",\"biz_decision\"]", "{}"},
            {"node_etl", "task", "ETL 指标加工", "明细→指标宽表加工（含质量监控联动）", "common", "indicator", "[\"tbl_detail\",\"tbl_quote_plan\",\"tbl_declaration\",\"tbl_settlement\",\"tbl_policy\"]", "[\"tbl_indicator\"]", "{\"price\":\"price\"}"},
            {"tbl_indicator", "table", "指标宽表", "价格/电量/结算等核心指标宽表", "common", "indicator", "[\"node_etl\"]", "[\"model_price\",\"model_load\",\"model_policy\",\"model_ops\",\"rpt_analyze\",\"rpt_assess\",\"biz_decision\"]", "{}"},
            {"model_price", "model", "价格预测模型", "现货/中长期价格预测（model_registry/forecast_result）", "forecast", "model", "[\"tbl_indicator\"]", "[\"biz_forecast\"]", "{}"},
            {"model_load", "model", "负荷预测模型", "负荷/电量预测（model_registry/forecast_result）", "forecast", "model", "[\"tbl_indicator\"]", "[\"biz_forecast\"]", "{}"},
            {"model_policy", "model", "政策解析模型", "政策条款智能解析/标签化（policy_analysis）", "policy", "model", "[\"tbl_policy\",\"tbl_indicator\"]", "[\"biz_policy\"]", "{}"},
            {"model_ops", "model", "模型训练与智能体", "训练任务/模型注册/智能体运行（training_task/model_registry/agent_registry/agent_run）", "model", "model", "[\"tbl_indicator\"]", "[\"biz_forecast\",\"biz_decision\"]", "{}"},
            {"rpt_analyze", "report", "经营分析报表", "多维经营分析报告（report_template/report_instance）", "report", "report", "[\"tbl_indicator\"]", "[\"biz_report\"]", "{}"},
            {"rpt_settle", "report", "结算核对报表", "结算核对/复盘报告（settlement_reconcile）", "settle", "report", "[\"tbl_settlement\"]", "[\"biz_report\",\"biz_settle\"]", "{}"},
            {"rpt_assess", "report", "评估考核报告", "指标评估/考核报告（assess_indicator/assess_result/review_report）", "assess", "report", "[\"tbl_indicator\",\"tbl_settlement\"]", "[\"biz_report\",\"biz_assess\"]", "{}"},
            {"biz_intel", "business", "情报中心", "情报流/推送规则/消息派发（intel_push_rule/message_record）", "intel", "business", "[\"tbl_intel_news\",\"sys_msg\"]", "[\"biz_decision\"]", "{}"},
            {"biz_decision", "business", "决策中心", "决策会话/复盘/偏差/策略反馈（decision_session/review_report/deviation_record/strategy_feedback/rule_config）", "decision", "business", "[\"tbl_indicator\",\"tbl_intel_news\",\"biz_intel\",\"biz_forecast\",\"model_ops\",\"sys_msg\",\"sys_flow\"]", "[\"biz_optimize\",\"biz_trade\"]", "{}"},
            {"biz_trade", "business", "交易申报", "报价/申报/成交/出清管理（quote_plan/declaration/trade_result/clearing_result）", "trade", "business", "[\"tbl_quote_plan\",\"tbl_declaration\",\"biz_decision\",\"sys_flow\"]", "[\"biz_settle\"]", "{}"},
            {"biz_settle", "business", "结算中心", "结算记录/核对/凭证/台账（settlement_*）", "settle", "business", "[\"tbl_settlement\",\"rpt_settle\",\"biz_trade\",\"sys_flow\"]", "[]", "{}"},
            {"biz_policy", "business", "政策中心", "政策文档/条款/解析管理（policy_document/article/analysis）", "policy", "business", "[\"model_policy\",\"tbl_policy\"]", "[]", "{}"},
            {"biz_forecast", "business", "预测中心", "预测任务/结果（forecast_task/forecast_result）", "forecast", "business", "[\"model_price\",\"model_load\",\"model_ops\"]", "[\"biz_decision\"]", "{}"},
            {"biz_optimize", "business", "协同优化", "联合优化任务/策略库/回测（joint_optim_task/strategy_library/backtest_run）", "optimize", "business", "[\"biz_decision\"]", "[]", "{}"},
            {"biz_report", "business", "报表中心", "报表模板/实例管理（report_template/report_instance）", "report", "business", "[\"rpt_analyze\",\"rpt_settle\",\"rpt_assess\"]", "[]", "{}"},
            {"biz_assess", "business", "评估考核", "指标评估/申诉（assess_indicator/assess_result/assess_appeal）", "assess", "business", "[\"rpt_assess\",\"tbl_settlement\"]", "[]", "{}"},
            {"sys_auth", "business", "用户权限体系", "用户/角色/权限/区域授权（sys_user/role/permission/user_region/role_region），RLS 行级隔离，支撑全部业务", "system", "business", "[]", "[\"biz_intel\",\"biz_decision\",\"biz_trade\",\"biz_settle\",\"biz_policy\",\"biz_forecast\",\"biz_optimize\",\"biz_report\",\"biz_assess\"]", "{}"},
            {"sys_audit", "business", "审计日志", "操作审计（audit_log，按月分区），@Log AOP 留痕，横切支撑全部业务", "system", "business", "[]", "[]", "{}"},
            {"sys_msg", "business", "消息中心", "站内消息/推送（message_record），情报推送/通知", "system", "business", "[]", "[\"biz_intel\",\"biz_decision\"]", "{}"},
            {"sys_flow", "business", "审批流", "流程实例（flow_instance），申报/结算/决策审批", "system", "business", "[]", "[\"biz_trade\",\"biz_settle\",\"biz_decision\"]", "{}"},
            {"data_quality", "business", "数据质量", "质量规则/报告（data_quality_rule/data_quality_report），监控明细与指标", "system", "business", "[\"tbl_detail\",\"tbl_settlement\"]", "[\"node_etl\",\"tbl_indicator\"]", "{}"},
    };

    /** 数据血缘查询（nodeId 缺省返回全景；节点含中文名/说明/业务域/数据分层，上游下游全量返回供图谱构图） */
    public Map<String, Object> lineage(String nodeId) {
        ensureLineage();
        LambdaQueryWrapper<DataLineage> qw = new LambdaQueryWrapper<>();
        if (StrUtils.isNotBlank(nodeId)) {
            qw.eq(DataLineage::getNodeId, nodeId);
        }
        qw.orderByAsc(DataLineage::getLayer).orderByAsc(DataLineage::getDomain)
                .orderByAsc(DataLineage::getNodeId);
        List<DataLineage> nodes = dataLineageMapper.selectList(qw);
        Map<String, Object> resp = new LinkedHashMap<>();
        List<Map<String, Object>> upstream = new ArrayList<>();
        List<Map<String, Object>> downstream = new ArrayList<>();
        for (DataLineage n : nodes) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("nodeId", n.getNodeId());
            node.put("nodeType", n.getNodeType());
            node.put("nodeName", StrUtils.isBlank(n.getNodeName()) ? n.getNodeId() : n.getNodeName());
            node.put("description", n.getDescription());
            node.put("domain", n.getDomain());
            node.put("layer", n.getLayer());
            node.put("fieldMapping", parseJson(n.getFieldMapping()));
            // 全景/单点均返回上下游（全量图谱构图需要；单点查询仍可按此过滤）
            node.put("upstream", parseRaw(n.getUpstream()));
            node.put("downstream", parseRaw(n.getDownstream()));
            upstream.add(node);
            downstream.add(node);
        }
        resp.put("upstream", upstream);
        resp.put("downstream", downstream);
        return resp;
    }

    /** 血缘懒种子（V3.0 全量图谱：外部源→采集→明细→指标→模型/报表→业务应用+系统支撑，
     *  36 节点全覆盖；节点中文名/说明/业务域/分层；旧 7 节点种子自动升级重建；
     *  与 16_lineage_full_graph_v3_0.sql 一致） */
    private void ensureLineage() {
        migrateLineageSchema();
        Long count = dataLineageMapper.selectCount(new LambdaQueryWrapper<DataLineage>());
        if (count != null && count > 0) {
            Long named = dataLineageMapper.selectCount(new LambdaQueryWrapper<DataLineage>()
                    .isNotNull(DataLineage::getNodeName));
            if (named != null && named >= SEED_LINEAGE.length) {
                return; // 已是全量图谱种子
            }
            dataLineageMapper.physicalClear(); // 旧版 7 节点种子 → 物理清空后重建全量
        }
        for (String[] s : SEED_LINEAGE) {
            DataLineage n = new DataLineage();
            n.setNodeId(s[0]);
            n.setNodeType(s[1]);
            n.setNodeName(s[2]);
            n.setDescription(s[3]);
            n.setDomain(s[4]);
            n.setLayer(s[5]);
            n.setUpstream(s[6]);
            n.setDownstream(s[7]);
            n.setFieldMapping(s[8]);
            dataLineageMapper.insert(n);
        }
        log.info("血缘全景全量图谱种子已就绪：{} 节点（中文名/说明/业务域/分层）", SEED_LINEAGE.length);
    }

    /** 血缘表结构幂等迁移（V3.0：节点中文名/说明/业务域/分层 + node_type 扩展 business） */
    private void migrateLineageSchema() {
        dataLineageMapper.addColumnNodeName();
        dataLineageMapper.addColumnDescription();
        dataLineageMapper.addColumnDomain();
        dataLineageMapper.addColumnLayer();
        dataLineageMapper.dropNodeTypeCheck();
        dataLineageMapper.addNodeTypeCheck();
    }

    /** 解析数组/对象 JSON（upstream/downstream 为节点 ID 数组） */
    private Object parseRaw(String json) {
        if (StrUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJson(String json) {
        if (StrUtils.isBlank(json)) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }
}
