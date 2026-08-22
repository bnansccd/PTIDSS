package com.ptidss.flow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.SecurityUtils;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.decision.domain.DecisionSession;
import com.ptidss.decision.mapper.DecisionSessionMapper;
import com.ptidss.flow.domain.FlowDefinition;
import com.ptidss.flow.domain.FlowInstance;
import com.ptidss.flow.mapper.FlowDefinitionMapper;
import com.ptidss.flow.mapper.FlowInstanceMapper;
import com.ptidss.review.domain.AssessAppeal;
import com.ptidss.review.mapper.AssessAppealMapper;
import com.ptidss.settlement.domain.SettlementTicket;
import com.ptidss.settlement.mapper.SettlementTicketMapper;
import com.ptidss.trade.domain.Declaration;
import com.ptidss.trade.mapper.DeclarationMapper;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 审批流（对齐 OpenAPI V1.1 /flow/**；平台服务，M7 移动端审批依赖）
 * V2.2 产品化：流程定义（flow_definition）驱动——审批环节/环节角色/审批人可配置，
 * 客户可自定义流程与环节；发起按定义首环节指派处理人，推进按定义步骤顺序流转。
 * 业务规则：轻量状态机（running→completed/terminated），不引入 Flowable。
 */
@Service
public class FlowService {

    private final FlowInstanceMapper flowInstanceMapper;
    private final FlowDefinitionMapper flowDefinitionMapper;
    private final DeclarationMapper declarationMapper;
    private final DecisionSessionMapper decisionSessionMapper;
    private final SettlementTicketMapper settlementTicketMapper;
    private final AssessAppealMapper assessAppealMapper;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;

    public FlowService(FlowInstanceMapper flowInstanceMapper, FlowDefinitionMapper flowDefinitionMapper,
                       DeclarationMapper declarationMapper, DecisionSessionMapper decisionSessionMapper,
                       SettlementTicketMapper settlementTicketMapper, AssessAppealMapper assessAppealMapper,
                       SecurityUtils securityUtils, ObjectMapper objectMapper) {
        this.flowInstanceMapper = flowInstanceMapper;
        this.flowDefinitionMapper = flowDefinitionMapper;
        this.declarationMapper = declarationMapper;
        this.decisionSessionMapper = decisionSessionMapper;
        this.settlementTicketMapper = settlementTicketMapper;
        this.assessAppealMapper = assessAppealMapper;
        this.securityUtils = securityUtils;
        this.objectMapper = objectMapper;
    }

    // ---------- 流程定义（V2.2 可配置化） ----------

    /** 懒种子（与 10_platform_config.sql 种子一致，表空时写入 5 条默认定义，幂等） */
    public void ensureDefinitions() {
        Long count = flowDefinitionMapper.selectCount(new LambdaQueryWrapper<FlowDefinition>());
        if (count != null && count > 0) {
            return;
        }
        String[][] seeds = {
                {"decision_confirm", "决策方案确认", "decision",
                        "[{\"stepNo\":\"apply\",\"stepName\":\"发起申请\",\"approveMode\":\"any\",\"roleCodes\":[\"trader\"],\"userIds\":[],\"timeoutHours\":24}," +
                                "{\"stepNo\":\"review\",\"stepName\":\"主管复核\",\"approveMode\":\"any\",\"roleCodes\":[\"manager\"],\"userIds\":[],\"timeoutHours\":24}," +
                                "{\"stepNo\":\"approve\",\"stepName\":\"决策批准\",\"approveMode\":\"any\",\"roleCodes\":[\"manager\"],\"userIds\":[],\"timeoutHours\":48}]"},
                {"declaration_approve", "交易申报审批", "declaration",
                        "[{\"stepNo\":\"apply\",\"stepName\":\"发起申报\",\"approveMode\":\"any\",\"roleCodes\":[\"trader\"],\"userIds\":[],\"timeoutHours\":24}," +
                                "{\"stepNo\":\"review\",\"stepName\":\"申报审核\",\"approveMode\":\"any\",\"roleCodes\":[\"analyst\"],\"userIds\":[],\"timeoutHours\":24}]"},
                {"ticket_handle", "差异工单处理", "ticket",
                        "[{\"stepNo\":\"apply\",\"stepName\":\"工单发起\",\"approveMode\":\"any\",\"roleCodes\":[\"settlement\"],\"userIds\":[],\"timeoutHours\":24}," +
                                "{\"stepNo\":\"review\",\"stepName\":\"结算复核\",\"approveMode\":\"any\",\"roleCodes\":[\"settlement\"],\"userIds\":[],\"timeoutHours\":24}]"},
                {"appeal_review", "考核申诉评审", "appeal",
                        "[{\"stepNo\":\"apply\",\"stepName\":\"申诉发起\",\"approveMode\":\"any\",\"roleCodes\":[\"settlement\"],\"userIds\":[],\"timeoutHours\":24}," +
                                "{\"stepNo\":\"review\",\"stepName\":\"合规评审\",\"approveMode\":\"any\",\"roleCodes\":[\"compliance\"],\"userIds\":[],\"timeoutHours\":48}]"},
                {"settlement_ticket_review", "结算单复核", "ticket",
                        "[{\"stepNo\":\"apply\",\"stepName\":\"结算单提交\",\"approveMode\":\"any\",\"roleCodes\":[\"settlement\"],\"userIds\":[],\"timeoutHours\":24}," +
                                "{\"stepNo\":\"review\",\"stepName\":\"复核确认\",\"approveMode\":\"any\",\"roleCodes\":[\"manager\"],\"userIds\":[],\"timeoutHours\":24}]"},
        };
        long id = 90001;
        for (String[] s : seeds) {
            FlowDefinition d = new FlowDefinition();
            d.setId(id++);
            d.setProcessKey(s[0]);
            d.setProcessName(s[1]);
            d.setBizType(s[2]);
            d.setSteps(s[3]);
            d.setStatus("enabled");
            flowDefinitionMapper.insert(d);
        }
    }

    /** 流程定义列表（steps 解析为数组；供审批流管理页） */
    public List<Map<String, Object>> definitions() {
        ensureDefinitions();
        List<FlowDefinition> list = flowDefinitionMapper.selectList(new LambdaQueryWrapper<FlowDefinition>()
                .orderByAsc(FlowDefinition::getId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (FlowDefinition d : list) {
            result.add(toDefinitionView(d));
        }
        return result;
    }

    /** 新增流程定义（processKey 唯一；至少 1 个环节；环节结构校验） */
    public Map<String, Object> createDefinition(String processKey, String processName, String bizType,
                                                List<Map<String, Object>> steps) {
        if (StrUtils.isBlank(processKey) || StrUtils.isBlank(processName) || StrUtils.isBlank(bizType)) {
            throw new ServiceException("流程定义键/名称/业务类型不能为空");
        }
        if (steps == null || steps.isEmpty()) {
            throw new ServiceException("审批环节不能为空（至少定义 1 个环节）");
        }
        Long exists = flowDefinitionMapper.selectCount(new LambdaQueryWrapper<FlowDefinition>()
                .eq(FlowDefinition::getProcessKey, processKey));
        if (exists != null && exists > 0) {
            throw new ServiceException("流程定义键已存在：" + processKey);
        }
        FlowDefinition d = new FlowDefinition();
        d.setProcessKey(processKey);
        d.setProcessName(processName);
        d.setBizType(bizType);
        d.setSteps(toJson(steps));
        d.setStatus("enabled");
        flowDefinitionMapper.insert(d);
        return toDefinitionView(d);
    }

    /** 更新流程定义（环节/角色/用户可调整；status 切换启停） */
    public Map<String, Object> updateDefinition(Long id, String processName, List<Map<String, Object>> steps,
                                                String status) {
        FlowDefinition exist = flowDefinitionMapper.selectById(id);
        if (exist == null) {
            throw new ServiceException("流程定义不存在");
        }
        if (steps != null && steps.isEmpty()) {
            throw new ServiceException("审批环节不能为空（至少定义 1 个环节）");
        }
        FlowDefinition update = new FlowDefinition();
        update.setId(id);
        update.setProcessName(processName);
        if (steps != null) {
            update.setSteps(toJson(steps));
        }
        update.setStatus(status);
        flowDefinitionMapper.updateById(update);
        return toDefinitionView(flowDefinitionMapper.selectById(id));
    }

    // ---------- 流程实例（发起/详情/推进） ----------

    // 业务类型元数据（V2.4：发起流程前选业务类型 → 引入该类型已有单号或自动生成，规则按业务类型匹配）
    private static final Map<String, String[]> BIZ_TYPES = new LinkedHashMap<>();

    static {
        BIZ_TYPES.put("decision", new String[]{"决策确认", "SESS"});
        BIZ_TYPES.put("declaration", new String[]{"交易申报", "DECL"});
        BIZ_TYPES.put("ticket", new String[]{"差异工单", "TKT"});
        BIZ_TYPES.put("appeal", new String[]{"考核申诉", "APL"});
    }

    /** 业务类型字典（供发起页下拉：编码 + 名称） */
    public List<Map<String, Object>> bizTypes() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, String[]> e : BIZ_TYPES.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("bizType", e.getKey());
            item.put("bizName", e.getValue()[0]);
            item.put("autoPrefix", e.getValue()[1]);
            list.add(item);
        }
        return list;
    }

    /**
     * 业务单号选项（V2.4：按业务类型引入已有单号或自动生成）：
     * - declaration → 申报单号（declaration.declaration_no）
     * - decision → 决策会话号（decision_session.session_no）
     * - ticket → 差异工单关联核对 ID（settlement_ticket.reconcile_id）
     * - appeal → 考核申诉 ID（assess_appeal.id）
     * options 为 [{value, label}]，供发起页搜索选择；autoPrefix 供自动生成单号。
     */
    public Map<String, Object> bizOptions(String bizType) {
        Map<String, Object> resp = new LinkedHashMap<>();
        String[] meta = BIZ_TYPES.get(bizType);
        String bizName = meta == null ? bizType : meta[0];
        String autoPrefix = meta == null ? "BIZ" : meta[1];
        resp.put("bizType", bizType);
        resp.put("bizName", bizName);
        resp.put("allowAuto", true);
        resp.put("autoPrefix", autoPrefix);
        List<Map<String, Object>> options = new ArrayList<>();
        if ("declaration".equals(bizType)) {
            for (Declaration d : declarationMapper.selectList(new LambdaQueryWrapper<Declaration>()
                    .isNotNull(Declaration::getDeclarationNo).orderByDesc(Declaration::getId).last("LIMIT 50"))) {
                options.add(option(d.getDeclarationNo(), "申报单 " + d.getDeclarationNo() + "（" + d.getStatus() + "）"));
            }
        } else if ("decision".equals(bizType)) {
            for (DecisionSession s : decisionSessionMapper.selectList(new LambdaQueryWrapper<DecisionSession>()
                    .isNotNull(DecisionSession::getSessionNo).orderByDesc(DecisionSession::getId).last("LIMIT 50"))) {
                options.add(option(s.getSessionNo(), "决策会话 " + s.getSessionNo() + "（" + s.getSessionType() + "）"));
            }
        } else if ("ticket".equals(bizType)) {
            for (SettlementTicket t : settlementTicketMapper.selectList(new LambdaQueryWrapper<SettlementTicket>()
                    .isNotNull(SettlementTicket::getReconcileId).orderByDesc(SettlementTicket::getId).last("LIMIT 50"))) {
                options.add(option(String.valueOf(t.getReconcileId()), "差异工单 核对ID " + t.getReconcileId() + "（" + t.getStatus() + "）"));
            }
        } else if ("appeal".equals(bizType)) {
            for (AssessAppeal a : assessAppealMapper.selectList(new LambdaQueryWrapper<AssessAppeal>()
                    .orderByDesc(AssessAppeal::getId).last("LIMIT 50"))) {
                options.add(option(String.valueOf(a.getId()), "考核申诉 ID " + a.getId() + "（" + a.getStatus() + "）"));
            }
        }
        resp.put("options", options);
        return resp;
    }

    private Map<String, Object> option(String value, String label) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("value", value);
        item.put("label", label);
        return item;
    }

    /** 自动生成业务单号：业务前缀 + yyyyMMddHHmmss + 4 位随机（与申报单 DECL 风格一致） */
    private String autoBizNo(String bizType) {
        String[] meta = BIZ_TYPES.get(bizType);
        String prefix = meta == null ? "BIZ" : meta[1];
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String rand = UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
        return prefix + time + rand;
    }

    /** 发起流程实例（定义驱动：校验定义键/启停；幂等；定位首个审批环节并指派处理人） */
    @SuppressWarnings("unchecked")
    public Map<String, Object> start(String processKey, String bizId, Map<String, Object> variables) {
        if (StrUtils.isBlank(processKey)) {
            throw new ServiceException("流程定义键不能为空");
        }
        FlowDefinition definition = flowDefinitionMapper.selectOne(new LambdaQueryWrapper<FlowDefinition>()
                .eq(FlowDefinition::getProcessKey, processKey).last("LIMIT 1"));
        if (definition == null) {
            throw new ServiceException("未注册的流程定义键：" + processKey + "（可在审批流管理页定义流程与环节）");
        }
        if (!"enabled".equals(definition.getStatus())) {
            throw new ServiceException("流程定义已停用：" + processKey);
        }
        // V2.4：业务单号为空时按业务类型自动生成（规则与业务类型匹配）
        if (StrUtils.isBlank(bizId)) {
            bizId = autoBizNo(definition.getBizType());
        }
        List<Map<String, Object>> steps = parseSteps(definition.getSteps());
        if (steps.isEmpty()) {
            throw new ServiceException("流程定义环节为空：" + processKey);
        }
        // 业务单据幂等：同定义键+单据号已有运行中实例则直接返回
        FlowInstance exist = flowInstanceMapper.selectOne(new LambdaQueryWrapper<FlowInstance>()
                .eq(FlowInstance::getProcessKey, processKey)
                .eq(FlowInstance::getBizId, bizId)
                .eq(FlowInstance::getStatus, "running")
                .last("LIMIT 1"));
        if (exist != null) {
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("instanceId", String.valueOf(exist.getId()));
            resp.put("status", exist.getStatus());
            resp.put("currentNode", exist.getCurrentNode());
            return resp;
        }
        // 首个审批环节：apply 视为发起环节（发起人自动完成），定位其后第一个环节
        Map<String, Object> firstApprove = steps.size() > 1 ? steps.get(1) : steps.get(0);
        String firstNode = String.valueOf(firstApprove.get("stepNo"));
        Map<String, Object> vars = new LinkedHashMap<>();
        if (variables != null) {
            vars.putAll(variables);
        }
        vars.put("source", "web");
        vars.put("definitionName", definition.getProcessName());
        vars.put("actions", new ArrayList<Map<String, Object>>());
        FlowInstance instance = new FlowInstance();
        instance.setInstanceNo("FLOW-" + System.currentTimeMillis());
        instance.setProcessKey(processKey);
        instance.setBizType(definition.getBizType());
        instance.setBizId(bizId);
        instance.setVariables(toJson(vars));
        instance.setStatus("running");
        instance.setCurrentNode(firstNode);
        instance.setCurrentAssignee(assignee(firstApprove));
        instance.setStartBy(securityUtils.getUsername());
        instance.setStartTime(new Date());
        flowInstanceMapper.insert(instance);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("instanceId", String.valueOf(instance.getId()));
        resp.put("status", instance.getStatus());
        resp.put("currentNode", instance.getCurrentNode());
        resp.put("currentAssignee", instance.getCurrentAssignee());
        return resp;
    }

    /** 流程实例详情（定义信息/状态/当前节点/步骤进度/审批留痕） */
    @SuppressWarnings("unchecked")
    public Map<String, Object> instanceDetail(Long instanceId) {
        FlowInstance instance = flowInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new ServiceException("流程实例不存在");
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("instanceId", String.valueOf(instance.getId()));
        resp.put("instanceNo", instance.getInstanceNo());
        resp.put("processKey", instance.getProcessKey());
        resp.put("bizType", instance.getBizType());
        resp.put("bizId", instance.getBizId());
        resp.put("status", instance.getStatus());
        resp.put("currentNode", instance.getCurrentNode());
        resp.put("currentAssignee", instance.getCurrentAssignee());
        resp.put("startBy", instance.getStartBy());
        resp.put("startTime", instance.getStartTime());
        resp.put("endTime", instance.getEndTime());
        Map<String, Object> variables = parseVariables(instance.getVariables());
        resp.put("variables", variables);
        // 定义步骤（供环节进度展示）
        FlowDefinition definition = flowDefinitionMapper.selectOne(new LambdaQueryWrapper<FlowDefinition>()
                .eq(FlowDefinition::getProcessKey, instance.getProcessKey()).last("LIMIT 1"));
        if (definition != null) {
            List<Map<String, Object>> steps = parseSteps(definition.getSteps());
            resp.put("definitionName", definition.getProcessName());
            resp.put("definitionSteps", steps);
            int current = -1;
            for (int i = 0; i < steps.size(); i++) {
                if (String.valueOf(steps.get(i).get("stepNo")).equals(instance.getCurrentNode())) {
                    current = i;
                    break;
                }
            }
            resp.put("currentStepIndex", current);
        }
        // 当前待办任务（运行中按节点生成；完成态为空列表）
        List<Map<String, Object>> tasks = new ArrayList<>();
        if ("running".equals(instance.getStatus())) {
            Map<String, Object> task = new LinkedHashMap<>();
            task.put("taskId", instance.getId() + "-" + instance.getCurrentNode());
            task.put("node", instance.getCurrentNode());
            task.put("assignee", instance.getCurrentAssignee());
            task.put("status", "todo");
            tasks.add(task);
        }
        resp.put("currentTasks", tasks);
        return resp;
    }

    /**
     * 环节推进（approve → 下一环节/完成；reject → 终止）。审批留痕追加至 variables.actions
     * （环节/处理人/意见/时间），客户自定义流程与环节后按同一定义驱动流转。
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> advance(Long instanceId, String action, String comment) {
        if (!"approve".equals(action) && !"reject".equals(action)) {
            throw new ServiceException("操作仅支持 approve/reject");
        }
        FlowInstance instance = flowInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new ServiceException("流程实例不存在");
        }
        if (!"running".equals(instance.getStatus())) {
            throw new ServiceException("流程实例已结束（status=" + instance.getStatus() + "）");
        }
        FlowDefinition definition = flowDefinitionMapper.selectOne(new LambdaQueryWrapper<FlowDefinition>()
                .eq(FlowDefinition::getProcessKey, instance.getProcessKey()).last("LIMIT 1"));
        if (definition == null) {
            throw new ServiceException("流程定义不存在：" + instance.getProcessKey());
        }
        List<Map<String, Object>> steps = parseSteps(definition.getSteps());
        Map<String, Object> variables = parseVariables(instance.getVariables());
        List<Map<String, Object>> actions = (List<Map<String, Object>>) variables.getOrDefault("actions", new ArrayList<>());
        Map<String, Object> record = new LinkedHashMap<>();
        record.put("node", instance.getCurrentNode());
        record.put("action", action);
        record.put("operator", securityUtils.getUsername());
        record.put("comment", comment == null ? "" : comment);
        record.put("time", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        actions.add(record);
        variables.put("actions", actions);
        FlowInstance update = new FlowInstance();
        update.setId(instanceId);
        update.setVariables(toJson(variables));
        if ("reject".equals(action)) {
            update.setStatus("terminated");
            update.setEndTime(new Date());
        } else {
            int idx = -1;
            for (int i = 0; i < steps.size(); i++) {
                if (String.valueOf(steps.get(i).get("stepNo")).equals(instance.getCurrentNode())) {
                    idx = i;
                    break;
                }
            }
            if (idx >= 0 && idx + 1 < steps.size()) {
                Map<String, Object> next = steps.get(idx + 1);
                update.setCurrentNode(String.valueOf(next.get("stepNo")));
                update.setCurrentAssignee(assignee(next));
            } else {
                update.setStatus("completed");
                update.setCurrentNode(null);
                update.setCurrentAssignee(null);
                update.setEndTime(new Date());
            }
        }
        flowInstanceMapper.updateById(update);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("instanceId", String.valueOf(instanceId));
        resp.put("status", update.getStatus() == null ? "running" : update.getStatus());
        resp.put("currentNode", update.getCurrentNode() == null ? instance.getCurrentNode() : update.getCurrentNode());
        resp.put("currentAssignee", update.getCurrentAssignee() == null ? instance.getCurrentAssignee() : update.getCurrentAssignee());
        resp.put("actions", actions);
        return resp;
    }

    /**
     * 流程实例列表（M7 移动端审批：scope=todo 我的待办 / started 我发起的 / all 全部；分页倒序）。
     * 待办匹配规则：运行中且当前处理人为当前用户名或当前用户角色之一（current_assignee 存用户名或角色编码）。
     */
    public Map<String, Object> listInstances(String scope, String status, long pageNo, long pageSize) {
        LambdaQueryWrapper<FlowInstance> wrapper = new LambdaQueryWrapper<>();
        if ("started".equals(scope)) {
            wrapper.eq(FlowInstance::getStartBy, securityUtils.getUsername());
            if (StrUtils.isNotBlank(status)) {
                wrapper.eq(FlowInstance::getStatus, status);
            }
        } else if ("todo".equals(scope)) {
            String username = securityUtils.getUsername();
            Set<String> roles = securityUtils.getLoginUser().getRoles() == null
                    ? new HashSet<>() : securityUtils.getLoginUser().getRoles();
            wrapper.eq(FlowInstance::getStatus, "running");
            wrapper.and(w -> {
                w.eq(FlowInstance::getCurrentAssignee, username);
                if (!roles.isEmpty()) {
                    w.or().in(FlowInstance::getCurrentAssignee, roles);
                }
            });
        } else if (StrUtils.isNotBlank(status)) {
            wrapper.eq(FlowInstance::getStatus, status);
        }
        wrapper.orderByDesc(FlowInstance::getStartTime);
        Page<FlowInstance> page = flowInstanceMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        List<Map<String, Object>> list = new ArrayList<>();
        for (FlowInstance f : page.getRecords()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("instanceId", String.valueOf(f.getId()));
            item.put("instanceNo", f.getInstanceNo());
            item.put("processKey", f.getProcessKey());
            item.put("bizType", f.getBizType());
            item.put("bizId", f.getBizId());
            item.put("status", f.getStatus());
            item.put("currentNode", f.getCurrentNode());
            item.put("currentAssignee", f.getCurrentAssignee());
            item.put("startBy", f.getStartBy());
            item.put("startTime", f.getStartTime());
            item.put("endTime", f.getEndTime());
            list.add(item);
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("list", list);
        resp.put("pageNo", page.getCurrent());
        resp.put("pageSize", page.getSize());
        resp.put("total", page.getTotal());
        return resp;
    }

    // ---------- 辅助 ----------

    /** 环节处理人指派：userIds 优先，否则 roleCodes 首角色（确定性） */
    @SuppressWarnings("unchecked")
    private String assignee(Map<String, Object> step) {
        List<String> userIds = step.get("userIds") == null ? new ArrayList<>() : (List<String>) step.get("userIds");
        if (!userIds.isEmpty()) {
            return userIds.get(0);
        }
        List<String> roleCodes = step.get("roleCodes") == null ? new ArrayList<>() : (List<String>) step.get("roleCodes");
        return roleCodes.isEmpty() ? "unassigned" : roleCodes.get(0);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseSteps(String stepsJson) {
        if (StrUtils.isBlank(stepsJson)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(stepsJson, new TypeReference<List<Map<String, Object>>>() { });
        } catch (Exception e) {
            throw new ServiceException("审批环节解析失败");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseVariables(String variablesJson) {
        if (StrUtils.isBlank(variablesJson)) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(variablesJson, new TypeReference<Map<String, Object>>() { });
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    private Map<String, Object> toDefinitionView(FlowDefinition d) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", String.valueOf(d.getId()));
        item.put("processKey", d.getProcessKey());
        item.put("processName", d.getProcessName());
        item.put("bizType", d.getBizType());
        item.put("steps", parseSteps(d.getSteps()));
        item.put("status", d.getStatus());
        item.put("updatedAt", d.getUpdatedAt());
        return item;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new ServiceException("JSON 序列化失败");
        }
    }
}
