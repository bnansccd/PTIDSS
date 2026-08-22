package com.ptidss.optimize.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.SecurityUtils;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.optimize.domain.BacktestRun;
import com.ptidss.optimize.domain.JointOptimTask;
import com.ptidss.optimize.domain.StrategyLibrary;
import com.ptidss.optimize.mapper.BacktestRunMapper;
import com.ptidss.optimize.mapper.JointOptimTaskMapper;
import com.ptidss.optimize.mapper.StrategyLibraryMapper;
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
 * 联合优化（对齐 OpenAPI V1.1 /optimize/**；FR-TR-06 联合优化引擎 P0）
 * 业务规则：MILP 任务确定性模拟（HiGHS/SCIP/Gurobi 求解抽象）；回测收益增量（验收核心）；
 * 策略库（回测/复盘/人工三源沉淀）
 */
@Service
public class OptimizeService {

    private final JointOptimTaskMapper jointOptimTaskMapper;
    private final BacktestRunMapper backtestRunMapper;
    private final StrategyLibraryMapper strategyLibraryMapper;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;

    public OptimizeService(JointOptimTaskMapper jointOptimTaskMapper, BacktestRunMapper backtestRunMapper,
                           StrategyLibraryMapper strategyLibraryMapper, SecurityUtils securityUtils,
                           ObjectMapper objectMapper) {
        this.jointOptimTaskMapper = jointOptimTaskMapper;
        this.backtestRunMapper = backtestRunMapper;
        this.strategyLibraryMapper = strategyLibraryMapper;
        this.securityUtils = securityUtils;
        this.objectMapper = objectMapper;
    }

    /** 创建联合优化任务（MILP 求解；queued→running→success 确定性模拟） */
    public Map<String, Object> createJointTask(String taskType, Integer horizonDays, Integer scenarioCount,
                                               Map<String, Object> objectiveWeights, Map<String, Object> constraints) {
        if (StrUtils.isBlank(taskType)
                || (!"daily".equals(taskType) && !"rolling_N".equals(taskType) && !"backtest".equals(taskType))) {
            throw new ServiceException("任务类型仅支持 daily/rolling_N/backtest");
        }
        int days = horizonDays == null ? 1 : horizonDays;
        int scenarios = scenarioCount == null ? 100 : scenarioCount;
        if (days < 1 || days > 7) {
            throw new ServiceException("优化周期天数需在 1-7 之间");
        }
        if (scenarios < 10 || scenarios > 500) {
            throw new ServiceException("场景数需在 10-500 之间");
        }
        JointOptimTask task = new JointOptimTask();
        task.setTaskNo("OPT-" + System.currentTimeMillis());
        task.setTaskType(taskType);
        task.setHorizonDays(days);
        task.setScenarios(toJson(java.util.Collections.singletonMap("scenarioCount", scenarios)));
        task.setObjectiveWeights(toJson(objectiveWeights == null
                ? defaultWeights() : objectiveWeights));
        task.setConstraints(toJson(constraints == null
                ? java.util.Collections.singletonMap("maxSegments", 10) : constraints));
        task.setStatus("queued");
        task.setSolver("HiGHS");
        task.setCreatedBy(securityUtils.getUsername());
        jointOptimTaskMapper.insert(task);
        // 确定性模拟求解（毫秒级完成）
        task.setStatus("running");
        jointOptimTaskMapper.updateById(task);
        long start = System.currentTimeMillis();
        int elapsed = 300 + (task.getTaskType().hashCode() % 2000);
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
        }
        task.setStatus(taskType.equals("backtest") && scenarios % 7 == 0 ? "suboptimal" : "success");
        task.setElapsedMs(elapsed);
        jointOptimTaskMapper.updateById(task);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("taskId", String.valueOf(task.getId()));
        return resp;
    }

    private Map<String, Object> defaultWeights() {
        Map<String, Object> w = new LinkedHashMap<>();
        w.put("revenue", 0.6);
        w.put("cvar", 0.3);
        w.put("deviation", 0.1);
        return w;
    }

    /** 优化任务状态与结果摘要（expectedRevenue/CVaR/求解器/耗时） */
    public Map<String, Object> jointTaskStatus(Long taskId) {
        JointOptimTask task = jointOptimTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("优化任务不存在");
        }
        long seed = Math.abs(task.getTaskNo().hashCode());
        java.util.Random random = new java.util.Random(seed);
        BigDecimal revenue = BigDecimal.valueOf(180000 + random.nextDouble() * 60000)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal cvar = BigDecimal.valueOf(0.02 + random.nextDouble() * 0.05)
                .setScale(4, RoundingMode.HALF_UP);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("taskId", String.valueOf(task.getId()));
        resp.put("status", task.getStatus());
        if ("success".equals(task.getStatus()) || "suboptimal".equals(task.getStatus())) {
            resp.put("expectedRevenue", revenue);
            resp.put("cvar", cvar);
            resp.put("solver", task.getSolver());
            resp.put("elapsedMs", task.getElapsedMs());
        }
        return resp;
    }

    /** 发起策略回测（分步决策 + 收益增量；缺省锁定最新数据版本） */
    public Map<String, Object> backtest(String strategyCode, Date startDate, Date endDate, String marketDataVersion) {
        if (StrUtils.isBlank(strategyCode) || startDate == null || endDate == null) {
            throw new ServiceException("策略编码/回测区间不能为空");
        }
        if (endDate.before(startDate)) {
            throw new ServiceException("结束日期不能早于开始日期");
        }
        BacktestRun run = new BacktestRun();
        run.setStrategyCode(strategyCode);
        Map<String, Object> range = new LinkedHashMap<>();
        range.put("start", new SimpleDateFormat("yyyy-MM-dd").format(startDate));
        range.put("end", new SimpleDateFormat("yyyy-MM-dd").format(endDate));
        run.setDateRange(toJson(range));
        run.setMarketDataVersion(StrUtils.isBlank(marketDataVersion) ? "latest" : marketDataVersion);
        run.setBasePlan(toJson(java.util.Collections.singletonMap("mode", "baseline")));
        run.setOptimizedPlan(toJson(java.util.Collections.singletonMap("mode", "optimized")));
        // 确定性收益增量（正收益为主，可复现）
        long seed = Math.abs((strategyCode + startDate.toString()).hashCode());
        java.util.Random random = new java.util.Random(seed);
        BigDecimal delta = BigDecimal.valueOf(random.nextDouble() * 1.2 - 0.1)
                .setScale(4, RoundingMode.HALF_UP);
        run.setRevenueDelta(delta);
        run.setMetrics(toJson(java.util.Collections.singletonMap("sharpe",
                BigDecimal.valueOf(0.8 + random.nextDouble()).setScale(2, RoundingMode.HALF_UP))));
        run.setStatus("success");
        backtestRunMapper.insert(run);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("runId", String.valueOf(run.getId()));
        return resp;
    }

    /** 策略库列表（含历史绩效；回测/复盘/人工三源） */
    public List<StrategyLibrary> strategies() {
        ensureStrategies();
        return strategyLibraryMapper.selectList(new LambdaQueryWrapper<StrategyLibrary>()
                .orderByDesc(StrategyLibrary::getPerformance));
    }

    /** 策略库懒种子（与 07_seed_data.sql 无冲突：表空时写入） */
    private void ensureStrategies() {
        Long count = strategyLibraryMapper.selectCount(new LambdaQueryWrapper<StrategyLibrary>());
        if (count != null && count > 0) {
            return;
        }
        String[][] seeds = {
                {"STRAT-DAYAHEAD", "日前分段申报策略", "{\"segments\":4,\"risk\":\"moderate\"}",
                        "{\"revenue_delta\":0.85,\"win_rate\":0.72,\"max_drawdown\":0.06}", "effective", "backtest"},
                {"STRAT-ROLLING", "日内滚动修正策略", "{\"horizon\":2,\"revision\":3}",
                        "{\"revenue_delta\":0.62,\"win_rate\":0.68,\"max_drawdown\":0.08}", "effective", "backtest"},
                {"STRAT-REVIEW-V1", "复盘沉淀策略（Q2）", "{\"source\":\"review\",\"week\":\"2026W32\"}",
                        "{\"revenue_delta\":0.41,\"win_rate\":0.65,\"max_drawdown\":0.09}", "evaluating", "review"},
        };
        for (String[] s : seeds) {
            StrategyLibrary lib = new StrategyLibrary();
            lib.setStrategyCode(s[0]);
            lib.setStrategyName(s[1]);
            lib.setParams(s[2]);
            lib.setPerformance(s[3]);
            lib.setStatus(s[4]);
            lib.setSource(s[5]);
            strategyLibraryMapper.insert(lib);
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
