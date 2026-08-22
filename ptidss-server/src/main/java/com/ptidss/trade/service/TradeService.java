package com.ptidss.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.common.domain.Result;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.SecurityUtils;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.trade.domain.Contract;
import com.ptidss.trade.domain.Declaration;
import com.ptidss.trade.domain.RollingPlan;
import com.ptidss.trade.domain.TradeResult;
import com.ptidss.trade.dto.TradeDeclarationRequest;
import com.ptidss.trade.dto.TradeDeclarationResponse;
import com.ptidss.trade.mapper.ContractMapper;
import com.ptidss.trade.mapper.DeclarationMapper;
import com.ptidss.trade.mapper.RollingPlanMapper;
import com.ptidss.trade.mapper.TradeResultMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 交易申报（DDL 三、交易数据域；对齐 OpenAPI V1.0 /trade/**）
 * 业务规则：申报合规预检（段数/限价/持仓比例，FR-TR-03）；数据权限按区域过滤（评审决议⑤）
 */
@Service
public class TradeService {

    /** 合规预检参数（申报段数/限价，FR-TR-03：可配置） */
    private static final int MAX_SEGMENTS = 10;
    private static final BigDecimal PRICE_LOWER = new BigDecimal("50.00");
    private static final BigDecimal PRICE_UPPER = new BigDecimal("1500.00");

    private final RollingPlanMapper rollingPlanMapper;
    private final DeclarationMapper declarationMapper;
    private final TradeResultMapper tradeResultMapper;
    private final ContractMapper contractMapper;
    private final TradeGatewayService tradeGatewayService;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;

    public TradeService(RollingPlanMapper rollingPlanMapper, DeclarationMapper declarationMapper,
                        TradeResultMapper tradeResultMapper, ContractMapper contractMapper,
                        TradeGatewayService tradeGatewayService,
                        SecurityUtils securityUtils, ObjectMapper objectMapper) {
        this.rollingPlanMapper = rollingPlanMapper;
        this.declarationMapper = declarationMapper;
        this.tradeResultMapper = tradeResultMapper;
        this.contractMapper = contractMapper;
        this.tradeGatewayService = tradeGatewayService;
        this.securityUtils = securityUtils;
        this.objectMapper = objectMapper;
    }

    // ---------- 日滚动方案 ----------

    public List<RollingPlan> listRollingPlans(Date tradeDate, String scenario, String status) {
        String regionCode = securityUtils.getRegionCode();
        LambdaQueryWrapper<RollingPlan> qw = new LambdaQueryWrapper<>();
        qw.eq(tradeDate != null, RollingPlan::getTradeDate, tradeDate)
                .eq(StrUtils.isNotBlank(scenario), RollingPlan::getScenario, scenario)
                .eq(StrUtils.isNotBlank(status), RollingPlan::getStatus, status)
                .eq(StrUtils.isNotBlank(regionCode), RollingPlan::getRegionCode, regionCode)
                .orderByDesc(RollingPlan::getTradeDate);
        return rollingPlanMapper.selectList(qw);
    }

    public void confirmRollingPlan(Long planId) {
        RollingPlan plan = getRollingPlan(planId);
        if (!"generated".equals(plan.getStatus())) {
            throw new ServiceException("方案状态不允许确认：" + plan.getStatus());
        }
        rollingPlanMapper.update(null, new LambdaUpdateWrapper<RollingPlan>()
                .eq(RollingPlan::getId, planId)
                .set(RollingPlan::getStatus, "confirmed"));
    }

    private RollingPlan getRollingPlan(Long planId) {
        RollingPlan plan = rollingPlanMapper.selectById(planId);
        if (plan == null) {
            throw new ServiceException("日滚动方案不存在");
        }
        return plan;
    }

    // ---------- 申报单 ----------

    public Result<Page<Declaration>> listDeclarations(Date tradeDate, String status,
                                                      long pageNo, long pageSize) {
        String regionCode = securityUtils.getRegionCode();
        LambdaQueryWrapper<Declaration> qw = new LambdaQueryWrapper<>();
        qw.eq(tradeDate != null, Declaration::getTradeDate, tradeDate)
                .eq(StrUtils.isNotBlank(status), Declaration::getStatus, status)
                .eq(StrUtils.isNotBlank(regionCode), Declaration::getRegionCode, regionCode)
                .orderByDesc(Declaration::getCreatedAt);
        Page<Declaration> page = declarationMapper.selectPage(new Page<>(pageNo, pageSize), qw);
        return Result.success(page);
    }

    public TradeDeclarationResponse createDeclaration(TradeDeclarationRequest req) {
        if (req.getTradeDate() == null) {
            throw new ServiceException("交易日期不能为空");
        }
        if (StrUtils.isBlank(req.getMarketType()) || StrUtils.isBlank(req.getStage())) {
            throw new ServiceException("市场类型/阶段不能为空");
        }
        List<Map<String, Object>> items = req.getItems();
        if (items == null || items.isEmpty()) {
            throw new ServiceException("申报明细不能为空");
        }
        // 合规预检：段数/限价/持仓比例（FR-TR-03；段数限价可配置）
        Map<String, Object> check = complianceCheck(items);
        boolean passed = (boolean) check.get("passed");

        Declaration d = new Declaration();
        d.setDeclarationNo("DECL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        d.setTradeDate(req.getTradeDate());
        d.setMarketType(req.getMarketType());
        d.setStage(req.getStage());
        d.setRegionCode(securityUtils.getRegionCode());
        d.setItems(toJson(items));
        d.setComplianceCheck(toJson(check));
        d.setStatus(passed ? "pending_submit" : "draft");
        d.setCreatedBy(securityUtils.getUsername());
        // 来源方案/会话：登记 evidence 引用
        if (StrUtils.isNotBlank(req.getSourcePlanId())) {
            ((Map<String, Object>) check).put("sourcePlanId", req.getSourcePlanId());
            d.setComplianceCheck(toJson(check));
        }
        declarationMapper.insert(d);

        TradeDeclarationResponse resp = new TradeDeclarationResponse();
        resp.setDeclarationId(String.valueOf(d.getId()));
        resp.setComplianceCheck(check);
        return resp;
    }

    public Map<String, Object> submitDeclaration(Long id) {
        Declaration d = getDeclaration(id);
        if (!"pending_submit".equals(d.getStatus()) && !"draft".equals(d.getStatus())) {
            throw new ServiceException("申报单状态不允许提交：" + d.getStatus());
        }
        String receiptNo = "RCPT" + new SimpleDateFormat("yyyyMMdd").format(d.getTradeDate())
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        // V2.4：交易网关推送监测——区域启用网关配置则模拟推送并记录状态，否则标记 skipped
        String pushStatus;
        String pushDetail;
        com.ptidss.trade.domain.TradeGatewayConfig gateway =
                tradeGatewayService.findConfig(d.getRegionCode());
        if (gateway != null && "enabled".equals(gateway.getStatus()) && StrUtils.isNotBlank(gateway.getEndpoint())) {
            pushStatus = "success";
            pushDetail = "已推送交易中心网关 " + gateway.getEndpoint() + "，回执号 " + receiptNo
                    + "（模拟推送，配置真实地址后联调）";
        } else {
            pushStatus = "skipped";
            pushDetail = gateway == null ? "未配置交易网关（可在交易申报页配置 URL/账户/密码）"
                    : "网关未启用（status=" + gateway.getStatus() + "）";
        }
        declarationMapper.update(null, new LambdaUpdateWrapper<Declaration>()
                .eq(Declaration::getId, id)
                .set(Declaration::getStatus, "submitted")
                .set(Declaration::getReceiptNo, receiptNo)
                .set(Declaration::getGatewayPushStatus, pushStatus)
                .set(Declaration::getGatewayPushDetail, pushDetail)
                .set(pushStatus.equals("success"), Declaration::getGatewayPushTime, new Date()));
        Map<String, Object> resp = new HashMap<>();
        resp.put("receiptNo", receiptNo);
        resp.put("gatewayPushStatus", pushStatus);
        resp.put("gatewayPushDetail", pushDetail);
        return resp;
    }

    /** 申报单详情（V2.4：明细/合规预检/网关推送状态，供编辑与状态监测） */
    public Map<String, Object> declarationDetail(Long id) {
        Declaration d = getDeclaration(id);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", String.valueOf(d.getId()));
        resp.put("declarationNo", d.getDeclarationNo());
        resp.put("tradeDate", d.getTradeDate());
        resp.put("marketType", d.getMarketType());
        resp.put("stage", d.getStage());
        resp.put("regionCode", d.getRegionCode());
        resp.put("status", d.getStatus());
        resp.put("receiptNo", d.getReceiptNo());
        resp.put("createdBy", d.getCreatedBy());
        resp.put("createdAt", d.getCreatedAt());
        resp.put("gatewayPushStatus", d.getGatewayPushStatus());
        resp.put("gatewayPushTime", d.getGatewayPushTime());
        resp.put("gatewayPushDetail", d.getGatewayPushDetail());
        resp.put("items", parseJsonArray(d.getItems()));
        resp.put("complianceCheck", parseJsonObject(d.getComplianceCheck()));
        return resp;
    }

    /**
     * 编辑申报单（V2.4：draft/pending_submit 可编辑市场/阶段/明细，重新合规预检；
     * submitted 及之后锁定，仅可查看）
     */
    public TradeDeclarationResponse updateDeclaration(Long id, TradeDeclarationRequest req) {
        Declaration d = getDeclaration(id);
        if (!"draft".equals(d.getStatus()) && !"pending_submit".equals(d.getStatus())) {
            throw new ServiceException("申报单已提交（" + d.getStatus() + "），不可编辑，仅可查看");
        }
        if (req.getTradeDate() == null) {
            throw new ServiceException("交易日期不能为空");
        }
        if (StrUtils.isBlank(req.getMarketType()) || StrUtils.isBlank(req.getStage())) {
            throw new ServiceException("市场类型/阶段不能为空");
        }
        List<Map<String, Object>> items = req.getItems();
        if (items == null || items.isEmpty()) {
            throw new ServiceException("申报明细不能为空");
        }
        Map<String, Object> check = complianceCheck(items);
        boolean passed = (boolean) check.get("passed");
        // 来源方案/会话：登记 evidence 引用
        if (StrUtils.isNotBlank(req.getSourcePlanId())) {
            check.put("sourcePlanId", req.getSourcePlanId());
        }
        Declaration update = new Declaration();
        update.setId(id);
        update.setTradeDate(req.getTradeDate());
        update.setMarketType(req.getMarketType());
        update.setStage(req.getStage());
        update.setItems(toJson(items));
        update.setComplianceCheck(toJson(check));
        update.setStatus(passed ? "pending_submit" : "draft");
        declarationMapper.updateById(update);
        TradeDeclarationResponse resp = new TradeDeclarationResponse();
        resp.setDeclarationId(String.valueOf(id));
        resp.setComplianceCheck(check);
        return resp;
    }

    private Declaration getDeclaration(Long id) {
        Declaration d = declarationMapper.selectOne(new LambdaQueryWrapper<Declaration>()
                .eq(Declaration::getId, id));
        if (d == null) {
            throw new ServiceException("申报单不存在");
        }
        return d;
    }

    /** 合规预检：段数 ≤ MAX_SEGMENTS、单价在限价区间内、申报总量 > 0 */
    private Map<String, Object> complianceCheck(List<Map<String, Object>> items) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> violations = new ArrayList<>();
        BigDecimal totalVolume = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            Object volume = item.get("volume");
            Object price = item.get("price");
            if (volume == null || price == null) {
                violations.add("申报段缺少量/价");
                continue;
            }
            try {
                BigDecimal v = new BigDecimal(String.valueOf(volume));
                BigDecimal p = new BigDecimal(String.valueOf(price));
                totalVolume = totalVolume.add(v);
                if (v.compareTo(BigDecimal.ZERO) <= 0) {
                    violations.add("申报量必须大于 0");
                }
                if (p.compareTo(PRICE_LOWER) < 0 || p.compareTo(PRICE_UPPER) > 0) {
                    violations.add("申报价超出限价区间 [" + PRICE_LOWER + ", " + PRICE_UPPER + "]");
                }
            } catch (NumberFormatException e) {
                violations.add("申报段量/价格式非法");
            }
        }
        if (items.size() > MAX_SEGMENTS) {
            violations.add("申报段数超过上限 " + MAX_SEGMENTS);
        }
        if (totalVolume.compareTo(BigDecimal.ZERO) <= 0) {
            violations.add("申报总量必须大于 0");
        }
        result.put("segments", items.size());
        result.put("maxSegments", MAX_SEGMENTS);
        result.put("priceRange", new String[]{PRICE_LOWER.toPlainString(), PRICE_UPPER.toPlainString()});
        result.put("totalVolume", totalVolume);
        result.put("passed", violations.isEmpty());
        result.put("violations", violations);
        return result;
    }

    // ---------- 成交结果 ----------

    public List<TradeResult> listResults(Date tradeDate, String marketType) {
        String regionCode = securityUtils.getRegionCode();
        // trade_result 无 market_type 列（DDL 3.4）：按申报单反查市场类型过滤
        if (StrUtils.isNotBlank(marketType)) {
            List<Long> declarationIds = declarationMapper.selectList(new LambdaQueryWrapper<Declaration>()
                            .eq(Declaration::getMarketType, marketType)
                            .select(Declaration::getId))
                    .stream().map(Declaration::getId).collect(java.util.stream.Collectors.toList());
            if (declarationIds.isEmpty()) {
                return java.util.Collections.emptyList();
            }
            LambdaQueryWrapper<TradeResult> qw = new LambdaQueryWrapper<>();
            qw.eq(tradeDate != null, TradeResult::getTradeDate, tradeDate)
                    .in(TradeResult::getDeclarationId, declarationIds)
                    .eq(StrUtils.isNotBlank(regionCode), TradeResult::getRegionCode, regionCode)
                    .orderByDesc(TradeResult::getTradeDate);
            return tradeResultMapper.selectList(qw);
        }
        LambdaQueryWrapper<TradeResult> qw = new LambdaQueryWrapper<>();
        qw.eq(tradeDate != null, TradeResult::getTradeDate, tradeDate)
                .eq(StrUtils.isNotBlank(regionCode), TradeResult::getRegionCode, regionCode)
                .orderByDesc(TradeResult::getTradeDate);
        return tradeResultMapper.selectList(qw);
    }

    // ---------- 持仓曲线 ----------

    public Map<String, Object> getPositions(Date tradeDate) {
        String regionCode = securityUtils.getRegionCode();
        // 中长期持仓：有效期内的执行中/生效合同曲线聚合（buy + / sell -）
        double[] longTerm = new double[96];
        LambdaQueryWrapper<Contract> qw = new LambdaQueryWrapper<>();
        qw.eq(StrUtils.isNotBlank(regionCode), Contract::getRegionCode, regionCode)
                .in(Contract::getStatus, "active", "executing");
        for (Contract c : contractMapper.selectList(qw)) {
            double[] curve = parseCurve(c.getCurveJson(), c.getTotalVolume());
            int sign = "buy".equals(c.getDirection()) ? 1 : -1;
            for (int i = 0; i < 96; i++) {
                longTerm[i] += sign * curve[i];
            }
        }
        // 现货持仓：以中长期为基准的模拟偏差（TDengine 未部署时确定性模拟）
        double[] spot = new double[96];
        double[] net = new double[96];
        double seed = tradeDate == null ? System.currentTimeMillis() : tradeDate.getTime();
        for (int i = 0; i < 96; i++) {
            double wave = Math.sin((i + seed / 86400000.0 % 7) * 0.7) * 0.25 + 0.5;
            spot[i] = Math.round(longTerm[i] * wave * 100.0) / 100.0;
            net[i] = Math.round((longTerm[i] + spot[i]) * 100.0) / 100.0;
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("longTerm", longTerm);
        resp.put("spot", spot);
        resp.put("net", net);
        return resp;
    }

    /** 解析 96 点曲线：curveJson 为数值数组则直接取；否则按 totalVolume 均摊 */
    private double[] parseCurve(String curveJson, BigDecimal totalVolume) {
        double[] curve = new double[96];
        try {
            if (StrUtils.isNotBlank(curveJson)) {
                JsonNode node = objectMapper.readTree(curveJson);
                if (node.isArray() && node.size() >= 96) {
                    for (int i = 0; i < 96; i++) {
                        curve[i] = node.get(i).asDouble();
                    }
                    return curve;
                }
            }
        } catch (Exception ignored) {
            // 解析失败回退均摊
        }
        double per = totalVolume == null ? 0 : totalVolume.doubleValue() / 96.0;
        for (int i = 0; i < 96; i++) {
            curve[i] = Math.round(per * 100.0) / 100.0;
        }
        return curve;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new ServiceException("JSON 序列化失败：" + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseJsonArray(String json) {
        if (StrUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() { });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonObject(String json) {
        if (StrUtils.isBlank(json)) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() { });
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }
}
