package com.ptidss.forecast.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.SecurityUtils;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.forecast.domain.ForecastResult;
import com.ptidss.forecast.domain.ForecastTask;
import com.ptidss.forecast.mapper.ForecastResultMapper;
import com.ptidss.forecast.mapper.ForecastTaskMapper;
import com.ptidss.model.domain.ModelRegistry;
import com.ptidss.model.domain.TrainingTask;
import com.ptidss.model.mapper.ModelRegistryMapper;
import com.ptidss.model.mapper.TrainingTaskMapper;
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
 * 预测中心（对齐 OpenAPI V1.1 /forecast/**；FR-TR-01~03 负荷/电价/新能源预测 P0，FR-TR-06 置信区间）
 * 业务规则：任务异步状态机 queued→running→success（确定性模拟，同一输入可复现）；
 * 96 点结果含 90% 置信区间；模型注册/训练与 model 域共用 model_registry/training_task
 */
@Service
public class ForecastService {

    private static final String[] MODEL_NAMES = {"现货价格预测模型", "负荷预测模型", "新能源出力预测模型"};

    private final ForecastTaskMapper forecastTaskMapper;
    private final ForecastResultMapper forecastResultMapper;
    private final ModelRegistryMapper modelRegistryMapper;
    private final TrainingTaskMapper trainingTaskMapper;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;

    public ForecastService(ForecastTaskMapper forecastTaskMapper, ForecastResultMapper forecastResultMapper,
                           ModelRegistryMapper modelRegistryMapper, TrainingTaskMapper trainingTaskMapper,
                           SecurityUtils securityUtils, ObjectMapper objectMapper) {
        this.forecastTaskMapper = forecastTaskMapper;
        this.forecastResultMapper = forecastResultMapper;
        this.modelRegistryMapper = modelRegistryMapper;
        this.trainingTaskMapper = trainingTaskMapper;
        this.securityUtils = securityUtils;
        this.objectMapper = objectMapper;
    }

    /** 创建预测任务（异步模拟；电价预测必填 marketType，负荷/新能源必填 regionCode） */
    public Map<String, Object> createTask(String modelCode, Date predictDate, String marketType, String regionCode) {
        if (StrUtils.isBlank(modelCode) || predictDate == null) {
            throw new ServiceException("模型编码/预测日期不能为空");
        }
        if (!"price".equals(modelCode) && !"load".equals(modelCode) && !"generation".equals(modelCode)) {
            throw new ServiceException("模型编码仅支持 price/load/generation");
        }
        if ("price".equals(modelCode) && StrUtils.isBlank(marketType)) {
            throw new ServiceException("电价预测必填市场类型（intra_province/inter_province）");
        }
        if (!"price".equals(modelCode) && StrUtils.isBlank(regionCode)) {
            throw new ServiceException("负荷/新能源预测必填区域");
        }
        String region = StrUtils.isBlank(regionCode) ? securityUtils.getRegionCode() : regionCode;
        ForecastTask task = new ForecastTask();
        task.setTaskNo("FT-" + System.currentTimeMillis());
        task.setModelCode(modelCode);
        task.setPredictDate(predictDate);
        task.setInputVersion("feat-" + new SimpleDateFormat("yyyyMMdd").format(new Date()));
        task.setRegionCode(region);
        task.setStatus("queued");
        forecastTaskMapper.insert(task);
        // 确定性模拟：queued→running→success（毫秒级完成，写结果）
        task.setStatus("running");
        task.setStartTime(new Date());
        forecastTaskMapper.updateById(task);
        simulateResults(task, marketType);
        task.setStatus("success");
        task.setEndTime(new Date());
        forecastTaskMapper.updateById(task);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("taskId", String.valueOf(task.getId()));
        resp.put("status", "queued");
        return resp;
    }

    /** 模拟 96 点结果写入（确定性：modelCode+predictDate 哈希可复现；置信区间 = 值 ± 比例） */
    private void simulateResults(ForecastTask task, String marketType) {
        ModelRegistry model = modelRegistryMapper.selectOne(new LambdaQueryWrapper<ModelRegistry>()
                .eq(ModelRegistry::getModelCode, task.getModelCode())
                .eq(ModelRegistry::getStatus, "online")
                .orderByDesc(ModelRegistry::getTrainedAt)
                .last("LIMIT 1"));
        String modelVersion = model == null ? "v1.0.0" : model.getModelVersion();
        long seed = Math.abs(task.getModelCode().hashCode() * 31L + task.getPredictDate().getTime() / 86400000L);
        java.util.Random random = new java.util.Random(seed);
        BigDecimal base = "price".equals(task.getModelCode()) ? new BigDecimal("400")
                : "load".equals(task.getModelCode()) ? new BigDecimal("28000") : new BigDecimal("12000");
        for (int i = 0; i < 96; i++) {
            double wave = 0.6 + 0.4 * Math.sin(i / 12.0 * Math.PI);   // 峰谷形态
            BigDecimal value = base.multiply(BigDecimal.valueOf(wave))
                    .add(BigDecimal.valueOf(random.nextGaussian() * base.doubleValue() * 0.03))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal band = value.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
            ForecastResult r = new ForecastResult();
            r.setTaskId(task.getId());
            r.setModelVersion(modelVersion);
            r.setPredictType(task.getModelCode());
            r.setMarketType(marketType);
            r.setRegionCode(task.getRegionCode());
            Calendar cal = Calendar.getInstance();
            cal.setTime(task.getPredictDate());
            cal.set(Calendar.HOUR_OF_DAY, i / 4);
            cal.set(Calendar.MINUTE, (i % 4) * 15);
            cal.set(Calendar.SECOND, 0);
            r.setTradeDate(cal.getTime());
            r.setValue(value);
            r.setLowerBound(value.subtract(band));
            r.setUpperBound(value.add(band));
            r.setConfidence(new BigDecimal("0.90"));
            forecastResultMapper.insert(r);
        }
    }

    /** 预测任务状态（含模型版本/耗时/错误信息） */
    public Map<String, Object> taskStatus(Long taskId) {
        ForecastTask task = forecastTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("预测任务不存在");
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("taskId", String.valueOf(task.getId()));
        resp.put("status", task.getStatus());
        resp.put("modelVersion", "v" + task.getInputVersion() + "-" + task.getModelCode());
        long elapsed = task.getStartTime() == null ? 0
                : (task.getEndTime() == null ? System.currentTimeMillis() - task.getStartTime().getTime()
                : task.getEndTime().getTime() - task.getStartTime().getTime());
        resp.put("elapsedMs", elapsed);
        if (StrUtils.isNotBlank(task.getErrorMsg())) {
            resp.put("errorMsg", task.getErrorMsg());
        }
        return resp;
    }

    /** 96 点预测结果（含 90% 置信区间；predictType/tradeDate 必填） */
    public List<Map<String, Object>> results(String predictType, Date tradeDate, String modelVersion) {
        if (StrUtils.isBlank(predictType) || tradeDate == null) {
            throw new ServiceException("预测类型/交易日期不能为空");
        }
        LambdaQueryWrapper<ForecastResult> qw = new LambdaQueryWrapper<ForecastResult>()
                .eq(ForecastResult::getPredictType, predictType)
                .eq(ForecastResult::getTradeDate, tradeDate)
                .eq(StrUtils.isNotBlank(modelVersion), ForecastResult::getModelVersion, modelVersion)
                .orderByAsc(ForecastResult::getTradeDate);
        List<ForecastResult> list = forecastResultMapper.selectList(qw);
        List<Map<String, Object>> points = new ArrayList<>();
        for (ForecastResult r : list) {
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("pointTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(r.getTradeDate()));
            p.put("value", r.getValue());
            p.put("lowerBound", r.getLowerBound());
            p.put("upperBound", r.getUpperBound());
            p.put("confidence", r.getConfidence());
            points.add(p);
        }
        return points;
    }

    /** 模型注册列表与在线状态（与 model 域共用 model_registry） */
    public List<Map<String, Object>> models() {
        List<ModelRegistry> list = modelRegistryMapper.selectList(new LambdaQueryWrapper<ModelRegistry>()
                .orderByAsc(ModelRegistry::getModelCode)
                .orderByDesc(ModelRegistry::getVersion));
        List<Map<String, Object>> result = new ArrayList<>();
        for (ModelRegistry m : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("modelCode", m.getModelCode());
            item.put("modelName", m.getModelName());
            item.put("version", m.getModelVersion());
            item.put("framework", m.getFramework());
            item.put("metrics", parseJson(m.getMetrics()));
            item.put("status", m.getStatus());
            result.add(item);
        }
        return result;
    }

    /** 触发模型训练（daily_increment/weekly_full；写 training_task，确定性模拟） */
    public Map<String, Object> train(String modelCode, String mode) {
        if (StrUtils.isBlank(modelCode) || (mode != null && !"daily_increment".equals(mode) && !"weekly_full".equals(mode))) {
            throw new ServiceException("模型编码/训练模式不合法");
        }
        TrainingTask t = new TrainingTask();
        t.setModelCode(modelCode);
        t.setDatasetRange(toJson(java.util.Collections.singletonMap("mode",
                mode == null ? "daily_increment" : mode)));
        t.setConfig(toJson(java.util.Collections.singletonMap("epochs", mode == null ? 3 : 10)));
        t.setStatus("queued");
        t.setTriggeredBy(mode == null ? "daily_increment" : mode);
        trainingTaskMapper.insert(t);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("taskId", String.valueOf(t.getId()));
        return resp;
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

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new ServiceException("JSON 序列化失败");
        }
    }
}
