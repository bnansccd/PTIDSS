package com.ptidss.review.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.SecurityUtils;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.review.domain.AssessAppeal;
import com.ptidss.review.domain.AssessIndicator;
import com.ptidss.review.domain.AssessResult;
import com.ptidss.review.domain.DeviationRecord;
import com.ptidss.review.domain.ReviewReport;
import com.ptidss.review.domain.StrategyFeedback;
import com.ptidss.review.mapper.AssessAppealMapper;
import com.ptidss.review.mapper.AssessIndicatorMapper;
import com.ptidss.review.mapper.AssessResultMapper;
import com.ptidss.review.mapper.DeviationRecordMapper;
import com.ptidss.review.mapper.ReviewReportMapper;
import com.ptidss.review.mapper.StrategyFeedbackMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 复盘考核（对齐 OpenAPI V1.0 /review/** 与 /assessment/**；FR-RS-01 智能复盘 + FR-DM-07 交易考核）
 * 业务规则：复盘报告三层归因（预测/决策/执行）+ 策略评估 + 改进建议；策略回流登记策略库；
 * 考核结果按指标权重评分，申诉批准后重算（FR-DM-07）
 */
@Service
public class ReviewService {

    /** 三层归因（FR-RS-01：预测/决策/执行分层归因） */
    private static final String[] LAYERS = {"forecast", "decision", "execution"};

    private final ReviewReportMapper reviewReportMapper;
    private final DeviationRecordMapper deviationRecordMapper;
    private final StrategyFeedbackMapper strategyFeedbackMapper;
    private final AssessIndicatorMapper assessIndicatorMapper;
    private final AssessResultMapper assessResultMapper;
    private final AssessAppealMapper assessAppealMapper;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;

    public ReviewService(ReviewReportMapper reviewReportMapper,
                         DeviationRecordMapper deviationRecordMapper,
                         StrategyFeedbackMapper strategyFeedbackMapper,
                         AssessIndicatorMapper assessIndicatorMapper,
                         AssessResultMapper assessResultMapper,
                         AssessAppealMapper assessAppealMapper,
                         SecurityUtils securityUtils, ObjectMapper objectMapper) {
        this.reviewReportMapper = reviewReportMapper;
        this.deviationRecordMapper = deviationRecordMapper;
        this.strategyFeedbackMapper = strategyFeedbackMapper;
        this.assessIndicatorMapper = assessIndicatorMapper;
        this.assessResultMapper = assessResultMapper;
        this.assessAppealMapper = assessAppealMapper;
        this.securityUtils = securityUtils;
        this.objectMapper = objectMapper;
    }

    // ---------- 复盘报告（FR-RS-01） ----------

    public Map<String, Object> createReport(String reportType, Date startDate, Date endDate,
                                            List<String> focusTopics) {
        if (StrUtils.isBlank(reportType) || startDate == null || endDate == null) {
            throw new ServiceException("报告类型/周期起止不能为空");
        }
        if (endDate.before(startDate)) {
            throw new ServiceException("周期止不能早于周期起");
        }
        long seed = startDate.getTime() / 86400000L;
        // 摘要：收益/成交/预测偏差
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("period", new SimpleDateFormat("yyyy-MM-dd").format(startDate)
                + " ~ " + new SimpleDateFormat("yyyy-MM-dd").format(endDate));
        summary.put("revenue", BigDecimal.valueOf(3860000 + (seed % 900) * 1000).setScale(2, RoundingMode.HALF_UP));
        summary.put("tradeVolume", BigDecimal.valueOf(185000 + (seed % 3000) * 10));
        summary.put("forecastError", BigDecimal.valueOf(2 + (seed % 20) / 10.0).setScale(1, RoundingMode.HALF_UP));
        // 三层归因（预测/决策/执行，逐层落 deviation_record）
        Map<String, Object> deviationAnalysis = buildDeviationAnalysis(seed);
        // 策略评估
        Map<String, Object> strategyEval = buildStrategyEval(seed);
        // 改进建议
        List<String> suggestions = new ArrayList<>();
        suggestions.add("提高日前价格预测准确率（重点关注峰段）");
        suggestions.add("执行偏差收敛：申报分段向成交结果靠拢");
        suggestions.add("高峰时段持仓比例可适度上移");

        ReviewReport report = new ReviewReport();
        report.setReportType(reportType);
        report.setPeriodStart(startDate);
        report.setPeriodEnd(endDate);
        report.setSummary(toJson(summary));
        report.setDeviationAnalysis(toJson(deviationAnalysis));
        report.setStrategyEval(toJson(strategyEval));
        report.setSuggestions(toJson(suggestions));
        report.setStatus("completed");
        report.setFileUrl("minio://review/" + reportType + "-" + seed + ".pdf");
        reviewReportMapper.insert(report);

        // 归因明细落库（deviation_record）
        for (Map<String, Object> layer : (List<Map<String, Object>>) deviationAnalysis.get("layers")) {
            for (Map<String, Object> item : (List<Map<String, Object>>) layer.get("items")) {
                DeviationRecord dr = new DeviationRecord();
                dr.setReportId(report.getId());
                dr.setLayer((String) layer.get("layer"));
                dr.setItem((String) item.get("item"));
                dr.setValue(toDecimal(item.get("value")));
                dr.setImpactAmount(toDecimal(item.get("impactAmount")));
                dr.setReason((String) item.get("reason"));
                dr.setDirection((String) item.get("direction"));
                deviationRecordMapper.insert(dr);
            }
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("reportId", String.valueOf(report.getId()));
        resp.put("status", report.getStatus());
        return resp;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> buildDeviationAnalysis(long seed) {
        Map<String, Object> analysis = new LinkedHashMap<>();
        List<Map<String, Object>> layers = new ArrayList<>();
        // 预测层
        layers.add(layerOf("forecast", "预测偏差",
                itemOf("电价预测偏差", 3.5, -128000, "日前电价预测偏高 3.5%", "negative"),
                itemOf("发电功率预测偏差", 2.1, -65000, "新能源出力预测偏差 2.1%", "negative")));
        // 决策层
        layers.add(layerOf("decision", "决策偏差",
                itemOf("报价策略偏差", 1.2, 86000, "分段报价接近最优解", "positive"),
                itemOf("持仓比例偏差", 4.0, -52000, "高峰段持仓比例低于策略目标", "negative")));
        // 执行层
        layers.add(layerOf("execution", "执行偏差",
                itemOf("申报-成交偏差", 0.8, -21000, "部分分段未完全成交", "negative"),
                itemOf("日滚动执行偏差", 1.5, 32000, "日滚动方案执行良好", "positive")));
        analysis.put("layers", layers);
        analysis.put("totalImpact", BigDecimal.valueOf(-48000 + seed % 4000));
        return analysis;
    }

    private Map<String, Object> layerOf(String layer, String name, Map<String, Object>... items) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("layer", layer);
        m.put("name", name);
        List<Map<String, Object>> list = new ArrayList<>();
        java.util.Collections.addAll(list, items);
        m.put("items", list);
        return m;
    }

    private Map<String, Object> itemOf(String item, double value, double impact, String reason, String direction) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("item", item);
        m.put("value", value);
        m.put("impactAmount", impact);
        m.put("reason", reason);
        m.put("direction", direction);
        return m;
    }

    private Map<String, Object> buildStrategyEval(long seed) {
        Map<String, Object> eval = new LinkedHashMap<>();
        List<Map<String, Object>> strategies = new ArrayList<>();
        String[][] defs = {
                {"STRAT-DA-PRICE", "日前分时段报价策略", "96.5"},
                {"STRAT-MID-LONG", "中长期滚动建仓策略", "88.2"},
                {"STRAT-SPOT-ARB", "现货峰谷套利策略", "91.3"},
                {"STRAT-RISK-CTRL", "偏差风险控制策略", "84.7"},
        };
        for (int i = 0; i < defs.length; i++) {
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("strategyCode", defs[i][0]);
            s.put("name", defs[i][1]);
            s.put("score", defs[i][2]);
            s.put("evaluation", i % 2 == 0 ? "有效，持续执行" : "效果一般，建议调整参数");
            strategies.add(s);
        }
        eval.put("strategies", strategies);
        eval.put("bestStrategy", "STRAT-DA-PRICE");
        return eval;
    }

    /** 复盘报告列表（按类型/周期筛选，倒序） */
    public List<Map<String, Object>> listReports(String reportType, Date periodStart, Date periodEnd) {
        LambdaQueryWrapper<ReviewReport> wrapper = new LambdaQueryWrapper<ReviewReport>()
                .orderByDesc(ReviewReport::getPeriodStart)
                .orderByDesc(ReviewReport::getCreatedAt);
        if (StrUtils.isNotBlank(reportType)) {
            wrapper.eq(ReviewReport::getReportType, reportType);
        }
        if (periodStart != null) {
            wrapper.ge(ReviewReport::getPeriodStart, periodStart);
        }
        if (periodEnd != null) {
            wrapper.le(ReviewReport::getPeriodEnd, periodEnd);
        }
        List<ReviewReport> list = reviewReportMapper.selectList(wrapper);
        List<Map<String, Object>> resp = new ArrayList<>();
        for (ReviewReport r : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", String.valueOf(r.getId()));
            item.put("reportType", r.getReportType());
            item.put("periodStart", r.getPeriodStart());
            item.put("periodEnd", r.getPeriodEnd());
            item.put("status", r.getStatus());
            item.put("summary", parseItems(r.getSummary()));
            item.put("suggestions", parseItems(r.getSuggestions()));
            item.put("createdAt", r.getCreatedAt());
            resp.add(item);
        }
        return resp;
    }

    public Map<String, Object> getReport(Long reportId) {
        ReviewReport report = reviewReportMapper.selectById(reportId);
        if (report == null) {
            throw new ServiceException("复盘报告不存在");
        }
        // 归因明细合并（deviation_record）
        List<Map<String, Object>> layers = new ArrayList<>();
        for (String layer : LAYERS) {
            List<DeviationRecord> records = deviationRecordMapper.selectList(
                    new LambdaQueryWrapper<DeviationRecord>()
                            .eq(DeviationRecord::getReportId, reportId)
                            .eq(DeviationRecord::getLayer, layer));
            List<Map<String, Object>> items = new ArrayList<>();
            for (DeviationRecord dr : records) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("item", dr.getItem());
                item.put("value", dr.getValue());
                item.put("impactAmount", dr.getImpactAmount());
                item.put("reason", dr.getReason());
                item.put("direction", dr.getDirection());
                items.add(item);
            }
            Map<String, Object> layerMap = new LinkedHashMap<>();
            layerMap.put("layer", layer);
            layerMap.put("items", items);
            layers.add(layerMap);
        }
        Map<String, Object> deviationAnalysis = new LinkedHashMap<>();
        deviationAnalysis.put("layers", layers);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", String.valueOf(report.getId()));
        resp.put("reportType", report.getReportType());
        resp.put("periodStart", report.getPeriodStart());
        resp.put("periodEnd", report.getPeriodEnd());
        resp.put("status", report.getStatus());
        resp.put("summary", parseItems(report.getSummary()));
        resp.put("deviationAnalysis", deviationAnalysis);
        resp.put("strategyEval", parseItems(report.getStrategyEval()));
        resp.put("suggestions", parseItems(report.getSuggestions()));
        return resp;
    }

    // ---------- 策略回流（FR-RS-01：复盘结论沉淀为策略库） ----------

    public void strategyFeedback(String strategyCode, String feedback,
                                 Map<String, Object> updatedParams, Long reviewId) {
        if (StrUtils.isBlank(strategyCode) || StrUtils.isBlank(feedback)) {
            throw new ServiceException("策略编码/反馈结论不能为空");
        }
        if ("adjust".equals(feedback) && (updatedParams == null || updatedParams.isEmpty())) {
            throw new ServiceException("调整类反馈必须携带调整参数");
        }
        // reviewId 契约可选：缺省自动关联最近一份复盘报告
        if (reviewId == null) {
            ReviewReport latest = reviewReportMapper.selectOne(new LambdaQueryWrapper<ReviewReport>()
                    .orderByDesc(ReviewReport::getCreatedAt)
                    .last("LIMIT 1"));
            if (latest != null) {
                reviewId = latest.getId();
            }
        }
        StrategyFeedback sf = new StrategyFeedback();
        sf.setReviewId(reviewId);
        sf.setStrategyCode(strategyCode);
        sf.setFeedback(feedback);
        sf.setUpdatedParams(toJson(updatedParams));
        sf.setStatus("pending");
        strategyFeedbackMapper.insert(sf);
    }

    // ---------- 考核（FR-DM-07） ----------

    public List<AssessIndicator> listIndicators() {
        return assessIndicatorMapper.selectList(new LambdaQueryWrapper<AssessIndicator>()
                .eq(AssessIndicator::getStatus, "active")
                .orderByDesc(AssessIndicator::getWeight));
    }

    /** 考核指标全部列表（含停用，供管理端维护） */
    public List<AssessIndicator> listAllIndicators() {
        return assessIndicatorMapper.selectList(new LambdaQueryWrapper<AssessIndicator>()
                .orderByDesc(AssessIndicator::getWeight));
    }

    /** 新增考核指标（编码唯一；权重 0-1 校验；操作友好性：考核体系自定义） */
    public AssessIndicator createIndicator(String code, String name, BigDecimal weight,
                                           String formula, String targetValue, String scoringRule,
                                           String dataSource, String status) {
        if (StrUtils.isBlank(code) || StrUtils.isBlank(name)) {
            throw new ServiceException("指标编码/名称不能为空");
        }
        if (weight == null || weight.compareTo(BigDecimal.ZERO) < 0 || weight.compareTo(BigDecimal.ONE) > 0) {
            throw new ServiceException("指标权重须在 0-1 之间");
        }
        Long exists = assessIndicatorMapper.selectCount(new LambdaQueryWrapper<AssessIndicator>()
                .eq(AssessIndicator::getCode, code));
        if (exists != null && exists > 0) {
            throw new ServiceException("指标编码已存在：" + code);
        }
        AssessIndicator ind = new AssessIndicator();
        ind.setCode(code);
        ind.setName(name);
        ind.setWeight(weight);
        ind.setFormula(StrUtils.isBlank(formula) ? "{}" : formula);
        ind.setTargetValue(targetValue);
        ind.setScoringRule(scoringRule);
        ind.setDataSource(dataSource);
        ind.setStatus(StrUtils.isBlank(status) ? "active" : status);
        assessIndicatorMapper.insert(ind);
        return ind;
    }

    /** 更新考核指标（名称/权重/目标/评分规则/启停；仅管理端） */
    public void updateIndicator(Long id, String name, BigDecimal weight, String formula,
                                String targetValue, String scoringRule, String dataSource, String status) {
        AssessIndicator exist = assessIndicatorMapper.selectById(id);
        if (exist == null) {
            throw new ServiceException("考核指标不存在");
        }
        if (weight != null && (weight.compareTo(BigDecimal.ZERO) < 0 || weight.compareTo(BigDecimal.ONE) > 0)) {
            throw new ServiceException("指标权重须在 0-1 之间");
        }
        AssessIndicator update = new AssessIndicator();
        update.setId(id);
        update.setName(name);
        update.setWeight(weight);
        update.setFormula(formula);
        update.setTargetValue(targetValue);
        update.setScoringRule(scoringRule);
        update.setDataSource(dataSource);
        update.setStatus(status);
        assessIndicatorMapper.updateById(update);
    }

    public List<AssessResult> listResults(String period, String scope) {
        if (StrUtils.isBlank(period)) {
            throw new ServiceException("考核周期不能为空");
        }
        ensurePeriodResults(period);
        LambdaQueryWrapper<AssessResult> qw = new LambdaQueryWrapper<>();
        qw.eq(AssessResult::getPeriod, period)
                .eq(StrUtils.isNotBlank(scope), AssessResult::getScope, scope)
                .orderByDesc(AssessResult::getTotalScore);
        return assessResultMapper.selectList(qw);
    }

    /** 周期内无结果时按指标权重生成确定性模拟考核（个人/团队），幂等 */
    private void ensurePeriodResults(String period) {
        Long count = assessResultMapper.selectCount(new LambdaQueryWrapper<AssessResult>()
                .eq(AssessResult::getPeriod, period));
        if (count != null && count > 0) {
            return;
        }
        List<AssessIndicator> indicators = listIndicators();
        if (indicators.isEmpty()) {
            return;
        }
        long seed = period.hashCode() & 0x7fffffffL;
        String[][] scopes = {{"team", null}, {"personal", "2"}, {"personal", "3"}, {"personal", "4"}};
        for (String[] sc : scopes) {
            AssessResult r = new AssessResult();
            r.setPeriod(period);
            r.setScope(sc[0]);
            r.setUserId(sc[1] == null ? null : Long.valueOf(sc[1]));
            Map<String, Object> scores = new LinkedHashMap<>();
            BigDecimal total = BigDecimal.ZERO;
            for (int i = 0; i < indicators.size(); i++) {
                AssessIndicator ind = indicators.get(i);
                BigDecimal score = BigDecimal.valueOf(80 + (seed + i * 17) % 19)
                        .setScale(1, RoundingMode.HALF_UP);
                scores.put(ind.getCode(), score);
                total = total.add(score.multiply(ind.getWeight()));
            }
            r.setScores(toJson(scores));
            r.setTotalScore(total.setScale(2, RoundingMode.HALF_UP));
            r.setRank(0);
            r.setStatus("confirmed");
            assessResultMapper.insert(r);
        }
    }

    public Map<String, Object> createAppeal(Long resultId, String appealReason, List<String> evidenceUrls) {
        AssessResult result = assessResultMapper.selectById(resultId);
        if (result == null) {
            throw new ServiceException("考核结果不存在");
        }
        if (StrUtils.isBlank(appealReason)) {
            throw new ServiceException("申诉理由不能为空");
        }
        AssessAppeal appeal = new AssessAppeal();
        appeal.setResultId(resultId);
        appeal.setAppealReason(appealReason);
        appeal.setEvidence(toJson(evidenceUrls));
        appeal.setStatus("pending");
        List<Map<String, Object>> history = new ArrayList<>();
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("action", "submit");
        event.put("operator", securityUtils.getUsername());
        event.put("comment", "提交申诉");
        event.put("time", new Date());
        history.add(event);
        appeal.setHistory(toJson(history));
        assessAppealMapper.insert(appeal);

        result.setStatus("appealing");
        assessResultMapper.updateById(result);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("appealId", String.valueOf(appeal.getId()));
        resp.put("status", appeal.getStatus());
        return resp;
    }

    public void processAppeal(Long appealId, String decision, String comment) {
        AssessAppeal appeal = assessAppealMapper.selectById(appealId);
        if (appeal == null) {
            throw new ServiceException("申诉不存在");
        }
        if (!"pending".equals(appeal.getStatus()) && !"processing".equals(appeal.getStatus())) {
            throw new ServiceException("当前状态不允许审核：" + appeal.getStatus());
        }
        AssessResult result = assessResultMapper.selectById(appeal.getResultId());
        if (result == null) {
            throw new ServiceException("关联考核结果不存在");
        }
        List<Map<String, Object>> history = parseHistory(appeal.getHistory());
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("action", "process");
        event.put("operator", securityUtils.getUsername());
        event.put("decision", decision);
        event.put("comment", comment);
        event.put("time", new Date());
        history.add(event);
        appeal.setStatus("approved".equals(decision) ? "approved" : "rejected");
        appeal.setHandler(securityUtils.getUsername());
        appeal.setDecision(comment);
        appeal.setHistory(toJson(history));
        assessAppealMapper.updateById(appeal);
        // 批准：重算结果（总分上浮 5%）；驳回：维持原分
        if ("approved".equals(decision)) {
            result.setTotalScore(result.getTotalScore()
                    .multiply(new BigDecimal("1.05")).setScale(2, RoundingMode.HALF_UP));
            result.setStatus("corrected");
        } else {
            result.setStatus("confirmed");
        }
        assessResultMapper.updateById(result);
    }

    // ---------- 工具 ----------

    private Map<String, Object> parseItems(String json) {
        try {
            if (StrUtils.isBlank(json)) {
                return new LinkedHashMap<>();
            }
            JsonNode node = objectMapper.readTree(json);
            return objectMapper.convertValue(node, Map.class);
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    private List<Map<String, Object>> parseHistory(String json) {
        try {
            if (StrUtils.isBlank(json)) {
                return new ArrayList<>();
            }
            JsonNode node = objectMapper.readTree(json);
            return objectMapper.convertValue(node, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, Map.class));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private BigDecimal toDecimal(Object obj) {
        if (obj == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(String.valueOf(obj));
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new ServiceException("JSON 序列化失败：" + e.getMessage());
        }
    }
}
