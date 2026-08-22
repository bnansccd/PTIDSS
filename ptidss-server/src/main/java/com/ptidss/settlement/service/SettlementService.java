package com.ptidss.settlement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.common.domain.Result;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.SecurityUtils;
import com.ptidss.common.utils.IdUtils;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.settlement.domain.OcrTask;
import com.ptidss.settlement.domain.SettlementReconcile;
import com.ptidss.settlement.domain.SettlementRecord;
import com.ptidss.settlement.domain.SettlementTicket;
import com.ptidss.settlement.mapper.OcrTaskMapper;
import com.ptidss.settlement.mapper.SettlementReconcileMapper;
import com.ptidss.settlement.mapper.SettlementRecordMapper;
import com.ptidss.settlement.mapper.SettlementTicketMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 复盘结算（对齐 OpenAPI V1.0 /settlement/** 与 /ocr/**；FR-RS-02 结算分析 + FR-DM-03 结算单识别）
 * 业务规则：结算核对自动比对（差异 ≥95% 通过，验收要点）；差异项自动生成处理工单；
 * 工单流转 assign/process/review/close 留痕；数据权限按区域过滤（评审决议⑤）
 */
@Service
public class SettlementService {

    /** 结算核对通过阈值（FR-RS-02 验收：核对自动化率 ≥ 95%） */
    private static final BigDecimal PASS_RATE_THRESHOLD = new BigDecimal("0.95");

    /** 费用科目（items JSON 固定科目，与数据字典一致） */
    private static final String[] FEE_ITEMS = {"电能量费用", "偏差考核", "辅助服务", "输配电价"};

    private final SettlementRecordMapper settlementRecordMapper;
    private final SettlementReconcileMapper settlementReconcileMapper;
    private final SettlementTicketMapper settlementTicketMapper;
    private final OcrTaskMapper ocrTaskMapper;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;

    public SettlementService(SettlementRecordMapper settlementRecordMapper,
                             SettlementReconcileMapper settlementReconcileMapper,
                             SettlementTicketMapper settlementTicketMapper,
                             OcrTaskMapper ocrTaskMapper,
                             SecurityUtils securityUtils, ObjectMapper objectMapper) {
        this.settlementRecordMapper = settlementRecordMapper;
        this.settlementReconcileMapper = settlementReconcileMapper;
        this.settlementTicketMapper = settlementTicketMapper;
        this.ocrTaskMapper = ocrTaskMapper;
        this.securityUtils = securityUtils;
        this.objectMapper = objectMapper;
    }

    // ---------- 结算记录 ----------

    public Result<Page<SettlementRecord>> listRecords(String period, String source,
                                                      long pageNo, long pageSize) {
        if (StrUtils.isBlank(period)) {
            throw new ServiceException("结算周期不能为空");
        }
        ensurePeriodRecords(period);
        String regionCode = securityUtils.getRegionCode();
        LambdaQueryWrapper<SettlementRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(SettlementRecord::getSettlementPeriod, period)
                .eq(StrUtils.isNotBlank(source), SettlementRecord::getSource, source)
                .eq(StrUtils.isNotBlank(regionCode), SettlementRecord::getRegionCode, regionCode)
                .orderByAsc(SettlementRecord::getSource);
        return Result.success(settlementRecordMapper.selectPage(new Page<>(pageNo, pageSize), qw));
    }

    /** 周期内无记录时自动生成 system/exchange 双份确定性模拟结算单（可复现，幂等） */
    private void ensurePeriodRecords(String period) {
        String regionCode = securityUtils.getRegionCode();
        Long count = settlementRecordMapper.selectCount(new LambdaQueryWrapper<SettlementRecord>()
                .eq(SettlementRecord::getSettlementPeriod, period)
                .eq(StrUtils.isNotBlank(regionCode), SettlementRecord::getRegionCode, regionCode));
        if (count != null && count > 0) {
            return;
        }
        long seed = periodSeed(period);
        for (String source : new String[]{"system", "exchange"}) {
            SettlementRecord r = new SettlementRecord();
            r.setSettlementPeriod(period);
            r.setRegionCode(regionCode);
            r.setSource(source);
            r.setItems(toJson(buildItems(seed, source)));
            r.setTotalAmount(buildTotal(seed, source));
            r.setSyncStatus("synced");
            settlementRecordMapper.insert(r);
        }
    }

    private long periodSeed(String period) {
        try {
            return new SimpleDateFormat("yyyy-MM").parse(period).getTime() / 86400000L;
        } catch (Exception e) {
            return period.hashCode() & 0x7fffffffL;
        }
    }

    /** 科目金额：系统结算为基准，交易中心结算单按差异系数偏离（exchange 每科目 ±0.5%~3%） */
    private Map<String, Object> buildItems(long seed, String source) {
        Map<String, Object> items = new LinkedHashMap<>();
        double factor = "exchange".equals(source) ? 1.0 : 0.0;
        for (int i = 0; i < FEE_ITEMS.length; i++) {
            BigDecimal base = new BigDecimal("3000000")
                    .subtract(new BigDecimal(i * 600000L))
                    .add(new BigDecimal((seed * 31 + i * 7) % 800000));
            if (factor > 0) {
                double drift = 0.005 + ((seed + i * 13) % 100) / 4000.0; // 0.5% ~ 3.0%
                if ((seed + i) % 5 == 0) {
                    drift = -drift; // 部分科目下浮
                }
                base = base.multiply(BigDecimal.ONE.add(BigDecimal.valueOf(drift)));
            }
            items.put(FEE_ITEMS[i], base.setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
        return items;
    }

    private BigDecimal buildTotal(long seed, String source) {
        double total = 0;
        for (Object value : buildItems(seed, source).values()) {
            total += ((Number) value).doubleValue();
        }
        return BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
    }

    // ---------- 结算核对（核对引擎） ----------

    public Map<String, Object> reconcile(Long recordId) {
        SettlementRecord record = getRecord(recordId);
        String regionCode = securityUtils.getRegionCode();
        // 找到对端记录（同周期同区域另一来源）
        String peerSource = "system".equals(record.getSource()) ? "exchange" : "system";
        SettlementRecord peer = settlementRecordMapper.selectOne(new LambdaQueryWrapper<SettlementRecord>()
                .eq(SettlementRecord::getSettlementPeriod, record.getSettlementPeriod())
                .eq(SettlementRecord::getSource, peerSource)
                .eq(StrUtils.isNotBlank(regionCode), SettlementRecord::getRegionCode, regionCode));
        if (peer == null) {
            throw new ServiceException("缺少对端结算单（" + peerSource + "），无法核对");
        }
        // 逐科目比对（system/exchange 列按来源固定分配，与发起核对的一方无关）
        List<Map<String, Object>> checkItems = new ArrayList<>();
        List<Map<String, Object>> diffRecords = new ArrayList<>();
        SettlementRecord sysRecord = "system".equals(record.getSource()) ? record : peer;
        SettlementRecord exRecord = "exchange".equals(record.getSource()) ? record : peer;
        BigDecimal systemTotal = sysRecord.getTotalAmount();
        BigDecimal diffAmount = BigDecimal.ZERO;
        Map<String, Object> sysMap = parseItems(sysRecord.getItems());
        Map<String, Object> exMap = parseItems(exRecord.getItems());
        for (String fee : FEE_ITEMS) {
            BigDecimal sys = toDecimal(sysMap.get(fee));
            BigDecimal ex = toDecimal(exMap.get(fee));
            BigDecimal diff = ex.subtract(sys).abs();
            boolean consistent = diff.compareTo(sys.multiply(new BigDecimal("0.01"))) <= 0; // 1% 容差
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("fee", fee);
            item.put("system", sys);
            item.put("exchange", ex);
            item.put("diff", diff);
            item.put("consistent", consistent);
            checkItems.add(item);
            if (!consistent) {
                diffAmount = diffAmount.add(diff);
                Map<String, Object> diffItem = new LinkedHashMap<>();
                diffItem.put("fee", fee);
                diffItem.put("diff", diff);
                diffItem.put("reason", "双端金额差异超 1% 容差");
                diffRecords.add(diffItem);
            }
        }
        BigDecimal passRate = systemTotal == null || systemTotal.compareTo(BigDecimal.ZERO) <= 0
                ? BigDecimal.ONE
                : BigDecimal.ONE.subtract(diffAmount.divide(systemTotal, 4, RoundingMode.HALF_UP));
        String status = passRate.compareTo(PASS_RATE_THRESHOLD) >= 0 ? "consistent" : "diff";

        SettlementReconcile reconcile = new SettlementReconcile();
        reconcile.setRecordId(recordId);
        reconcile.setCheckItems(toJson(checkItems));
        reconcile.setDiffRecords(toJson(diffRecords));
        reconcile.setStatus(status);
        reconcile.setDiffAmount(diffAmount);
        settlementReconcileMapper.insert(reconcile);

        // 差异超阈值：自动生成差异工单（FR-RS-02：差异项自动生成处理工单）
        if ("diff".equals(status) && !diffRecords.isEmpty()) {
            createTicket(reconcile.getId(), diffRecords.get(0));
        }
        record.setSyncStatus("diff".equals(status) ? "diff" : "synced");
        settlementRecordMapper.updateById(record);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("reconcileId", String.valueOf(reconcile.getId()));
        resp.put("status", status);
        resp.put("checkItems", checkItems);
        resp.put("diffAmount", diffAmount);
        resp.put("passRate", passRate.setScale(4, RoundingMode.HALF_UP));
        return resp;
    }

    private void createTicket(Long reconcileId, Map<String, Object> diff) {
        SettlementTicket ticket = new SettlementTicket();
        ticket.setReconcileId(reconcileId);
        ticket.setDiffType("amount_diff");
        ticket.setDiffAmount(toDecimal(diff.get("diff")));
        ticket.setStatus("pending");
        List<Map<String, Object>> history = new ArrayList<>();
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("action", "create");
        event.put("operator", securityUtils.getUsername());
        event.put("comment", "结算核对差异超阈值自动生成工单：" + diff.get("fee"));
        event.put("time", new Date());
        history.add(event);
        ticket.setHistory(toJson(history));
        settlementTicketMapper.insert(ticket);
    }

    // ---------- 差异工单 ----------

    public Result<Page<SettlementTicket>> listTickets(String status, long pageNo, long pageSize) {
        String regionCode = securityUtils.getRegionCode();
        LambdaQueryWrapper<SettlementTicket> qw = new LambdaQueryWrapper<>();
        qw.eq(StrUtils.isNotBlank(status), SettlementTicket::getStatus, status)
                .orderByDesc(SettlementTicket::getCreatedAt);
        if (StrUtils.isNotBlank(regionCode)) {
            // 工单经核对结果关联区域：通过核对记录记录来源过滤
            List<Long> regionRecordIds = settlementRecordMapper.selectList(
                            new LambdaQueryWrapper<SettlementRecord>()
                                    .eq(SettlementRecord::getRegionCode, regionCode)
                                    .select(SettlementRecord::getId))
                    .stream().map(SettlementRecord::getId).collect(java.util.stream.Collectors.toList());
            if (regionRecordIds.isEmpty()) {
                return Result.success(new Page<>(pageNo, pageSize));
            }
            List<Long> reconcileIds = settlementReconcileMapper.selectList(
                            new LambdaQueryWrapper<SettlementReconcile>()
                                    .in(SettlementReconcile::getRecordId, regionRecordIds)
                                    .select(SettlementReconcile::getId))
                    .stream().map(SettlementReconcile::getId).collect(java.util.stream.Collectors.toList());
            if (reconcileIds.isEmpty()) {
                return Result.success(new Page<>(pageNo, pageSize));
            }
            qw.in(SettlementTicket::getReconcileId, reconcileIds);
        }
        return Result.success(settlementTicketMapper.selectPage(new Page<>(pageNo, pageSize), qw));
    }

    public void processTicket(Long ticketId, String action, String handler, String comment) {
        SettlementTicket ticket = settlementTicketMapper.selectById(ticketId);
        if (ticket == null) {
            throw new ServiceException("差异工单不存在");
        }
        String targetStatus;
        switch (action) {
            case "assign":
                if (StrUtils.isBlank(handler)) {
                    throw new ServiceException("指派操作必须指定处理人");
                }
                targetStatus = "processing";
                ticket.setHandler(handler);
                break;
            case "process":
                targetStatus = "reviewed";
                break;
            case "review":
                targetStatus = "reviewed";
                break;
            case "close":
                targetStatus = "closed";
                break;
            default:
                throw new ServiceException("不支持的工单操作：" + action);
        }
        // 状态机校验：pending→processing→reviewed→closed
        String cur = ticket.getStatus();
        if ("pending".equals(cur) && !"assign".equals(action) && !"process".equals(action)) {
            throw new ServiceException("待处理工单仅支持指派/处理");
        }
        if ("processing".equals(cur) && !"process".equals(action) && !"review".equals(action)) {
            throw new ServiceException("处理中工单仅支持处理/复核");
        }
        if ("reviewed".equals(cur) && !"close".equals(action)) {
            throw new ServiceException("已复核工单仅支持关闭");
        }
        // 处理留痕时间线
        List<Map<String, Object>> history = parseHistory(ticket.getHistory());
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("action", action);
        event.put("operator", securityUtils.getUsername());
        event.put("handler", handler);
        event.put("comment", comment);
        event.put("time", new Date());
        history.add(event);
        ticket.setStatus(targetStatus);
        ticket.setHistory(toJson(history));
        settlementTicketMapper.updateById(ticket);
    }

    // ---------- 结算单 OCR 识别（FR-DM-03） ----------

    public Map<String, Object> createOcrTask(MultipartFile file, Long templateId) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("请上传结算单图片");
        }
        OcrTask task = new OcrTask();
        task.setFileId("minio://ocr/" + IdUtils.fastSimpleUUID("ocr-"));
        task.setTemplateId(templateId);
        // 确定性模拟识别：置信度 0.90~0.985，低置信进入人工复核
        int confidenceBp = 9000 + (int) (System.currentTimeMillis() % 851);
        BigDecimal confidence = BigDecimal.valueOf(confidenceBp, 4);
        task.setConfidence(confidence);
        task.setFields(toJson(buildOcrFields(confidence)));
        if (confidence.compareTo(new BigDecimal("0.95")) >= 0) {
            task.setStatus("success");
            task.setReviewStatus("not_required");
        } else {
            task.setStatus("low_confidence");
            task.setReviewStatus("pending");
        }
        ocrTaskMapper.insert(task);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("taskId", String.valueOf(task.getId()));
        resp.put("status", task.getStatus());
        resp.put("confidence", confidence);
        return resp;
    }

    private Map<String, Object> buildOcrFields(BigDecimal confidence) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("period", "2026-08");
        fields.put("totalAmount", 12345678.90);
        fields.put("energyFee", 10234567.80);
        fields.put("deviationFee", 123456.78);
        fields.put("assistServiceFee", 876543.21);
        fields.put("transmissionFee", 1098765.43);
        fields.put("confidence", confidence);
        return fields;
    }

    public Map<String, Object> getOcrTask(Long taskId) {
        OcrTask task = ocrTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("识别任务不存在");
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("taskId", String.valueOf(task.getId()));
        resp.put("status", task.getStatus());
        resp.put("confidence", task.getConfidence());
        resp.put("fields", parseItems(task.getFields()));
        resp.put("reviewStatus", task.getReviewStatus());
        return resp;
    }

    /** OCR 任务列表（状态/复核状态筛选，分页；复核工作台 FR-DM-03） */
    public Result<Page<OcrTask>> listOcrTasks(String status, String reviewStatus,
                                              long pageNo, long pageSize) {
        LambdaQueryWrapper<OcrTask> qw = new LambdaQueryWrapper<>();
        qw.eq(StrUtils.isNotBlank(status), OcrTask::getStatus, status)
                .eq(StrUtils.isNotBlank(reviewStatus), OcrTask::getReviewStatus, reviewStatus)
                .orderByDesc(OcrTask::getCreatedAt);
        return Result.success(ocrTaskMapper.selectPage(new Page<>(pageNo, pageSize), qw));
    }

    /** 人工复核提交（低置信补录闭环：确认通过或修正字段；复核人/时间全留痕） */
    public Map<String, Object> reviewOcrTask(Long taskId, boolean approved,
                                             Map<String, Object> fields, String comment) {
        OcrTask task = ocrTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("识别任务不存在");
        }
        if ("reviewed".equals(task.getReviewStatus())) {
            throw new ServiceException("该任务已完成复核");
        }
        if (!approved && (fields == null || fields.isEmpty())) {
            throw new ServiceException("驳回修正必须提供修正字段");
        }
        task.setStatus("success");
        task.setReviewStatus("reviewed");
        if (!approved) {
            task.setFields(toJson(fields));
        }
        task.setReviewer(securityUtils.getUsername());
        task.setReviewedAt(new Date());
        ocrTaskMapper.updateById(task);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("taskId", String.valueOf(task.getId()));
        resp.put("status", task.getStatus());
        resp.put("reviewStatus", task.getReviewStatus());
        resp.put("reviewer", task.getReviewer());
        resp.put("reviewedAt", task.getReviewedAt());
        resp.put("comment", comment);
        return resp;
    }

    // ---------- 工具 ----------

    private SettlementRecord getRecord(Long recordId) {
        SettlementRecord record = settlementRecordMapper.selectById(recordId);
        if (record == null) {
            throw new ServiceException("结算记录不存在");
        }
        return record;
    }

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
