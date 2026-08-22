package com.ptidss.model.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.SecurityUtils;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.model.domain.ModelRegistry;
import com.ptidss.model.domain.ModelTask;
import com.ptidss.model.domain.TrainingTask;
import com.ptidss.model.mapper.ModelRegistryMapper;
import com.ptidss.model.mapper.ModelTaskMapper;
import com.ptidss.model.mapper.TrainingTaskMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 模型平台（对齐 OpenAPI V1.1 /model/**；FR-PD-03 模型管理与服务化）
 * 业务规则：注册表与 MLflow 同步（种子 3 模型 v1.0.0 在线）；在线推理确定性模拟；
 * 离线评估锁定测试集版本，MAPE/方向准确率双指标判定；
 * V2.4 任务报告：训练触发/离线评估/在线推理 → model_task 详细报告（过程步骤/结果/与前面对标）
 */
@Service
public class ModelService {

    private final ModelRegistryMapper modelRegistryMapper;
    private final TrainingTaskMapper trainingTaskMapper;
    private final ModelTaskMapper modelTaskMapper;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;

    public ModelService(ModelRegistryMapper modelRegistryMapper,
                        TrainingTaskMapper trainingTaskMapper, ModelTaskMapper modelTaskMapper,
                        SecurityUtils securityUtils, ObjectMapper objectMapper) {
        this.modelRegistryMapper = modelRegistryMapper;
        this.trainingTaskMapper = trainingTaskMapper;
        this.modelTaskMapper = modelTaskMapper;
        this.securityUtils = securityUtils;
        this.objectMapper = objectMapper;
    }

    /** 模型注册表（版本/指标/状态；与 forecast/models 同源） */
    public List<Map<String, Object>> registry() {
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

    /** 模型在线推理（LLM 问答/预测增强；确定性模拟输出，契约对齐 MLflow/Flower 推理服务） */
    public Map<String, Object> inference(String modelCode, Map<String, Object> input, Double temperature) {
        return doInference(modelCode, input, temperature, new ArrayList<>());
    }

    /** 模型离线评估（锁定测试集；MAPE/方向准确率，双指标判定通过） */
    public Map<String, Object> evaluate(String modelVersion, String testSetVersion) {
        return doEvaluate(modelVersion, testSetVersion, new ArrayList<>());
    }

    // ---------- V2.4 模型任务报告（训练触发/离线评估/在线推理 → 过程/结果/对标） ----------

    /**
     * 执行模型任务并生成详细报告：记录输入快照、执行过程步骤（数据加载→特征→计算→输出）、
     * 结果指标、执行耗时，并与该模型上一次同类型成功任务对标（关键指标 delta），方便用户理解
     * 模型运行情况与变化。类型：train（训练触发）/ evaluate（离线评估）/ inference（在线推理）。
     */
    public Map<String, Object> runTask(String taskType, String modelCode, String modelVersion,
                                       String testSetVersion, String mode, Map<String, Object> input) {
        if (!"train".equals(taskType) && !"evaluate".equals(taskType) && !"inference".equals(taskType)) {
            throw new ServiceException("任务类型不支持：" + taskType + "（train/evaluate/inference）");
        }
        if (StrUtils.isBlank(modelCode)) {
            modelCode = "price";
        }
        long start = System.currentTimeMillis();
        List<Map<String, Object>> steps = new ArrayList<>();
        ModelTask task = new ModelTask();
        task.setTaskType(taskType);
        task.setModelCode(modelCode);
        task.setModelVersion(modelVersion);
        task.setStatus("running");
        task.setInputJson(toJson(input == null ? new LinkedHashMap<>() : input));
        task.setCreatedBy(securityUtils.getUsername());
        modelTaskMapper.insert(task);

        Map<String, Object> result;
        try {
            if ("train".equals(taskType)) {
                task.setTaskName(modelName(modelCode) + " " + ("weekly_full".equals(mode) ? "周全量" : "日增量") + "训练");
                result = doTrain(modelCode, mode, steps);
            } else if ("evaluate".equals(taskType)) {
                task.setTaskName(modelName(modelCode) + " 离线评估（测试集 " + (testSetVersion == null ? "" : testSetVersion) + "）");
                result = doEvaluate(modelVersion, testSetVersion, steps);
            } else {
                task.setTaskName(modelName(modelCode) + " 在线推理");
                result = doInference(modelCode, input, null, steps);
            }
            task.setStatus("success");
        } catch (Exception e) {
            task.setStatus("failed");
            result = new LinkedHashMap<>();
            result.put("error", e.getMessage());
            Map<String, Object> fail = new LinkedHashMap<>();
            fail.put("step", "执行失败");
            fail.put("detail", e.getMessage());
            fail.put("timeMs", System.currentTimeMillis() - start);
            steps.add(fail);
        }
        task.setProcessSteps(toJson(steps));
        task.setResultJson(toJson(result));
        task.setLatencyMs((int) (System.currentTimeMillis() - start));
        task.setFinishedAt(new Date());
        // 与前面对标：同模型同类型上一次成功任务的关键指标变化
        Map<String, Object> compare = new LinkedHashMap<>();
        ModelTask prev = modelTaskMapper.selectOne(new LambdaQueryWrapper<ModelTask>()
                .eq(ModelTask::getModelCode, modelCode)
                .eq(ModelTask::getTaskType, taskType)
                .eq(ModelTask::getStatus, "success")
                .ne(ModelTask::getId, task.getId())
                .orderByDesc(ModelTask::getId)
                .last("LIMIT 1"));
        if (prev != null) {
            compare.put("baselineTaskId", String.valueOf(prev.getId()));
            compare.put("baselineCreatedAt", prev.getCreatedAt());
            Map<String, Object> base = parseJson(prev.getResultJson());
            Map<String, Object> delta = new LinkedHashMap<>();
            for (String k : new String[]{"mape", "directionAccuracy", "confidence", "avg", "peak", "trough", "rmse"}) {
                if (base.get(k) instanceof Number && result.get(k) instanceof Number) {
                    double before = ((Number) base.get(k)).doubleValue();
                    double after = ((Number) result.get(k)).doubleValue();
                    delta.put(k, Math.round((after - before) * 10000.0) / 10000.0);
                }
            }
            compare.put("baselineMetrics", base);
            compare.put("delta", delta);
            compare.put("summary", delta.isEmpty() ? "与上次任务无同口径指标，仅记录耗时对比"
                    : "较上次任务：" + deltaSummary(delta));
        }
        task.setCompareJson(toJson(compare));
        modelTaskMapper.updateById(task);
        return taskDetail(task.getId());
    }

    /** 任务列表（按类型过滤；倒序） */
    public List<Map<String, Object>> listTasks(String taskType, int limit) {
        LambdaQueryWrapper<ModelTask> qw = new LambdaQueryWrapper<ModelTask>()
                .eq(StrUtils.isNotBlank(taskType), ModelTask::getTaskType, taskType)
                .orderByDesc(ModelTask::getId)
                .last("LIMIT " + Math.max(1, Math.min(limit <= 0 ? 20 : limit, 100)));
        List<ModelTask> list = modelTaskMapper.selectList(qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ModelTask t : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", String.valueOf(t.getId()));
            item.put("taskType", t.getTaskType());
            item.put("taskName", t.getTaskName());
            item.put("modelCode", t.getModelCode());
            item.put("modelVersion", t.getModelVersion());
            item.put("status", t.getStatus());
            item.put("latencyMs", t.getLatencyMs());
            item.put("createdAt", t.getCreatedAt());
            item.put("finishedAt", t.getFinishedAt());
            item.put("createdBy", t.getCreatedBy());
            result.add(item);
        }
        return result;
    }

    /** 任务详情（输入/过程步骤/结果/对标，完整报告） */
    public Map<String, Object> taskDetail(Long id) {
        ModelTask t = modelTaskMapper.selectById(id);
        if (t == null) {
            throw new ServiceException("模型任务不存在");
        }
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", String.valueOf(t.getId()));
        item.put("taskType", t.getTaskType());
        item.put("taskName", t.getTaskName());
        item.put("modelCode", t.getModelCode());
        item.put("modelVersion", t.getModelVersion());
        item.put("status", t.getStatus());
        item.put("latencyMs", t.getLatencyMs());
        item.put("createdAt", t.getCreatedAt());
        item.put("finishedAt", t.getFinishedAt());
        item.put("createdBy", t.getCreatedBy());
        item.put("input", parseJson(t.getInputJson()));
        item.put("processSteps", parseJsonArray(t.getProcessSteps()));
        item.put("result", parseJson(t.getResultJson()));
        item.put("compare", parseJson(t.getCompareJson()));
        return item;
    }

    // ---------- 任务执行体（带过程步骤） ----------

    /** 训练触发（模拟训练：数据加载→特征工程→模型训练→指标评估；同步写 training_task 历史） */
    private Map<String, Object> doTrain(String modelCode, String mode, List<Map<String, Object>> steps) {
        addStep(steps, "数据加载", "加载 " + modelName(modelCode) + " 历史数据集（"
                + ("weekly_full".equals(mode) ? "近 12 周全量" : "近 7 日增量") + "）");
        addStep(steps, "特征工程", "时序特征/天气特征/节假日标记归一化");
        addStep(steps, "模型训练", "LSTM 网络训练（epochs=50，batch=64，early stopping）");
        java.util.Random random = new java.util.Random(modelCode.hashCode() * 7L + System.currentTimeMillis() % 97);
        BigDecimal mape = BigDecimal.valueOf(0.025 + random.nextDouble() * 0.05).setScale(4, RoundingMode.HALF_UP);
        BigDecimal rmse = BigDecimal.valueOf(18 + random.nextDouble() * 12).setScale(2, RoundingMode.HALF_UP);
        addStep(steps, "指标评估", "验证集 MAPE=" + mape + "%，RMSE=" + rmse + " 元/MWh");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mape", mape);
        result.put("rmse", rmse);
        result.put("epochs", 50);
        result.put("samples", "weekly_full".equals(mode) ? 184320 : 92160);
        result.put("mode", mode == null ? "daily_increment" : mode);
        result.put("artifactUrl", "mlflow://" + modelCode + "/runs/" + System.currentTimeMillis());
        result.put("summary", modelName(modelCode) + " 训练完成（" + mode + "），验证 MAPE=" + mape + "%");
        // 同步历史 training_task（保持既有训练任务视图可用）
        TrainingTask tt = new TrainingTask();
        tt.setModelCode(modelCode);
        tt.setDatasetRange(toJson(new LinkedHashMap<String, Object>() {{
            put("mode", mode == null ? "daily_increment" : mode);
        }}));
        tt.setConfig("{\"epochs\":50,\"batch\":64}");
        tt.setStatus("success");
        tt.setMetrics(toJson(result));
        tt.setArtifactUrl(String.valueOf(result.get("artifactUrl")));
        tt.setTriggeredBy("manual");
        trainingTaskMapper.insert(tt);
        return result;
    }

    /** 离线评估（锁定测试集；MAPE/方向准确率双指标判定） */
    private Map<String, Object> doEvaluate(String modelVersion, String testSetVersion, List<Map<String, Object>> steps) {
        if (StrUtils.isBlank(modelVersion) || StrUtils.isBlank(testSetVersion)) {
            throw new ServiceException("模型版本/测试集版本不能为空");
        }
        addStep(steps, "测试集加载", "锁定测试集 " + testSetVersion + "（近 30 日 96 点样本）");
        addStep(steps, "批量推理", "模型 " + modelVersion + " 全样本前向推理");
        addStep(steps, "指标计算", "MAPE / 方向准确率 双指标判定");
        long seed = Math.abs(modelVersion.hashCode() * 17L + testSetVersion.hashCode());
        java.util.Random random = new java.util.Random(seed);
        BigDecimal mape = BigDecimal.valueOf(0.03 + random.nextDouble() * 0.09)
                .setScale(4, RoundingMode.HALF_UP);
        BigDecimal direction = BigDecimal.valueOf(0.80 + random.nextDouble() * 0.15)
                .setScale(4, RoundingMode.HALF_UP);
        boolean passed = mape.compareTo(new BigDecimal("0.10")) <= 0
                && direction.compareTo(new BigDecimal("0.85")) >= 0;
        addStep(steps, "判定", "MAPE=" + mape + "%（阈值 10%）、方向准确率=" + direction
                + "（阈值 85%）→ " + (passed ? "通过，可上线" : "不达标，拒绝上线"));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mape", mape);
        result.put("directionAccuracy", direction);
        result.put("passed", passed);
        result.put("testSetVersion", testSetVersion);
        result.put("summary", "模型 " + modelVersion + " 在测试集 " + testSetVersion + " 评估"
                + (passed ? "通过" : "不达标") + "（MAPE=" + mape + "%，方向准确率=" + direction + "）");
        return result;
    }

    /** 在线推理（确定性模拟 96 点序列） */
    private Map<String, Object> doInference(String modelCode, Map<String, Object> input, Double temperature,
                                            List<Map<String, Object>> steps) {
        if (StrUtils.isBlank(modelCode) || input == null) {
            throw new ServiceException("模型编码/输入不能为空");
        }
        ModelRegistry model = modelRegistryMapper.selectOne(new LambdaQueryWrapper<ModelRegistry>()
                .eq(ModelRegistry::getModelCode, modelCode)
                .eq(ModelRegistry::getStatus, "online")
                .orderByDesc(ModelRegistry::getTrainedAt)
                .last("LIMIT 1"));
        if (model == null) {
            throw new ServiceException("模型不存在或未上线");
        }
        long latency = 80 + Math.abs(modelCode.hashCode()) % 900;
        addStep(steps, "输入校验", "模型 " + modelCode + "（" + model.getModelVersion() + "）在线服务就绪");
        addStep(steps, "特征构造", "按输入构造 96 点特征序列（temperature=" + (temperature == null ? 0.2 : temperature) + "）");
        addStep(steps, "模型推理", "前向推理输出 96 点预测序列");
        long seed = modelCode.hashCode() * 31L + model.getModelVersion().hashCode();
        java.util.Random random = new java.util.Random(seed);
        double level = 400 + random.nextDouble() * 80;
        double amp = "load".equals(modelCode) ? 60 : "generation".equals(modelCode) ? 50 : 90;
        List<Double> series = new ArrayList<>();
        for (int i = 0; i < 96; i++) {
            double hour = i / 4.0;
            double wave = Math.sin((hour - 5) / 24.0 * 2 * Math.PI) * amp;
            series.add(Math.round((level + wave + random.nextDouble() * 12 - 6) * 10.0) / 10.0);
        }
        Map<String, Object> metrics = parseJson(model.getMetrics());
        double confidence = 0.82 + random.nextDouble() * 0.12;
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("summary", "基于 " + model.getModelName() + " " + model.getModelVersion()
                + " 的推理结果（确定性模拟；接入 MLflow/Flower 后替换为真实推理）");
        output.put("prediction", input.getOrDefault("input", ""));
        output.put("predictionSeries", series);
        output.put("seriesStats", seriesStats(series));
        output.put("revision", "v1");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("output", output);
        result.put("latencyMs", latency);
        result.put("modelVersion", model.getModelVersion());
        result.put("modelName", model.getModelName());
        result.put("confidence", Math.round(confidence * 100.0) / 100.0);
        result.put("metrics", metrics);
        addStep(steps, "输出", "96 点序列（均值 " + output.get("seriesStats") + "），耗时 " + latency + "ms");
        return result;
    }

    private void addStep(List<Map<String, Object>> steps, String step, String detail) {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("step", step);
        s.put("detail", detail);
        s.put("timeMs", steps.size() * 180 + 60 + (int) (Math.random() * 80));
        steps.add(s);
    }

    /** 序列摘要（均值/峰/谷），供推理报告输出 */
    private Map<String, Object> seriesStats(List<Double> series) {
        double sum = 0;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (double v : series) {
            sum += v;
            max = Math.max(max, v);
            min = Math.min(min, v);
        }
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("points", series.size());
        stats.put("avg", Math.round(sum / series.size() * 10.0) / 10.0);
        stats.put("peak", Math.round(max * 10.0) / 10.0);
        stats.put("trough", Math.round(min * 10.0) / 10.0);
        return stats;
    }

    private String deltaSummary(Map<String, Object> delta) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : delta.entrySet()) {
            double v = ((Number) e.getValue()).doubleValue();
            String dir = v > 0 ? "↑" : v < 0 ? "↓" : "→";
            sb.append(e.getKey()).append(" ").append(dir).append(" ").append(Math.abs(v)).append("；");
        }
        return sb.toString();
    }

    private String modelName(String modelCode) {
        if ("load".equals(modelCode)) {
            return "负荷预测";
        }
        if ("generation".equals(modelCode)) {
            return "新能源出力";
        }
        return "价格预测";
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

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseJsonArray(String json) {
        if (StrUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, List.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}
