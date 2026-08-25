/**
 * 契约类型（与 docs/openapi/openapi.yaml V1.0 对齐）
 * 统一响应：{ code, message, data, traceId }；code=0 成功；14001 未认证/凭证失效
 */

/** 统一响应包装 */
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
  traceId: string
}

/** 分页结果（PageResult：pageNo/pageSize/total） */
export interface PageResult<T = unknown> {
  list: T[]
  total: number
  pageNo: number
  pageSize: number
}

export interface PageParam {
  pageNo?: number
  pageSize?: number
}

export interface LoginResult {
  accessToken: string
  expiresIn: number
}

export interface CurrentUser {
  userId: string
  username: string
  displayName: string
  roles: string[]
  /** 多省授权区域（评审决议⑤；契约 V1.0 未定义，v1.1 待补，骨架用 DEFAULT_REGIONS 兜底） */
  regions?: string[]
  permissions: string[]
}

export interface PricePoint {
  ts: string
  price: number
  volume: number
  marketType?: string
  stage?: string
  regionCode?: string
}

export interface SupplyDemandPoint {
  ts: string
  loadValue: number
  availableCapacity: number
  renewableOutput: number
  supplyDemandRatio: number
  regionCode?: string
}

export interface Declaration {
  id: string
  declarationNo: string
  tradeDate: string
  marketType: string
  stage: string
  regionCode: string
  status: string
  createdAt?: string
  /** V2.4 网关推送状态监测（success/failed/pending/skipped） */
  gatewayPushStatus?: string
  gatewayPushTime?: string
  gatewayPushDetail?: string
}

export interface RollingPlan {
  id: string
  tradeDate: string
  scenario: string
  planType: string
  expectedRevenue: number
  status: string
}

export interface SettlementRecord {
  id: string
  settlementPeriod: string
  regionCode: string
  syncStatus: string
  totalAmount: number
}

export interface DecisionSession {
  id: string
  sessionNo: string
  sessionType: string
  tradeDate: string
  humanReviewStatus: string
}

export interface IntelNews {
  id: string
  title: string
  importance: string
  publishedAt: string
  regionCode?: string
  sourceCode?: string
  content?: string
  normalizedTags?: string[] | string
  pushStatus?: string
}

/** 情报源台账（GET /intel/sources） */
export interface IntelSource {
  id: string
  sourceCode: string
  sourceName: string
  intelType: string
  fetchMode: string
  connType?: string
  connConfig?: Record<string, unknown> | string
  frequency: string
  status: string
}

/** 推送规则（GET/POST /intel/push-rules） */
export interface IntelPushRule {
  id: string
  ruleName: string
  tagsFilter: string[] | string
  importanceFilter: string
  targetRoles: string[] | string
  channel: string[] | string
  silentPeriod?: string
  status: string
  createdAt?: string
}

/** 采集状态监测（GET /intel/fetch-status） */
export interface IntelFetchStatus {
  id: string
  sourceCode: string
  sourceName: string
  intelType: string
  fetchMode: string
  frequency: string
  status: string
  lastSuccessAt?: string | null
  lastError?: string | null
  consecutiveFailures: number
  frequencyMinutes?: string
  endpoint?: string
  mock?: boolean
  healthy: boolean
}

/** 报表模板（GET /report/templates） */
export interface ReportTemplate {
  id: string
  code: string
  name: string
  type: string
  periodType: string
  version?: string
  datasourceConfig?: string
  layout?: string
  headerConfig?: string
  status: string
}

/** 报表实例（GET/POST /report/instances） */
export interface ReportInstance {
  id: string
  templateId: string
  templateCode?: string
  period: string
  regionCode: string
  dataSnapshot?: string
  fileUrl?: string
  generateStatus: string
  pushStatus: string
  generatedAt?: string
  createdAt?: string
}

/** OCR 识别任务（GET /ocr/tasks；FR-DM-03 低置信人工复核） */
export interface OcrTask {
  id: string
  fileId: string
  templateId?: string
  status: string
  confidence: number
  fields?: Record<string, unknown>
  reviewStatus: string
  reviewer?: string
  reviewedAt?: string
  createdAt?: string
}

export interface JointOptimTask {
  id: string
  taskNo: string
  taskType: string
  status: string
  solver: string
  elapsedMs?: number
}

export interface BacktestRun {
  id: string
  strategyCode: string
  revenueDelta: number
  status: string
}

export interface StrategyItem {
  id: string
  strategyCode: string
  strategyName: string
  status: string
  source: string
}

/** 政策文件（GET /policy/list；FR-PD-01 政策研判 P0） */
export interface PolicyDocument {
  id: string
  title: string
  issuingBody: string
  category: string
  tags?: string[] | string
  versionNo?: number
  fileUrl?: string
  publishDate?: string
  effectiveDate?: string
  status: string
}

/** 解析条款（GET /policy/{id} articles） */
export interface PolicyArticle {
  id: string
  clauseType: string
  originalText: string
  parsedStructure?: string
  confidence: number
  relatedRuleId?: string
  reviewStatus: string
}

/** 影响研判（GET /policy/{id} analysis） */
export interface PolicyAnalysis {
  id: string
  changePoint: string
  affectedLink: string
  impactLevel: string
  analysisResult?: string
  analyst?: string
}

/** 政策详情（GET /policy/{id}，PolicyDetail 契约） */
export interface PolicyDetail extends PolicyDocument {
  articles: PolicyArticle[]
  analysis: PolicyAnalysis[]
  rules: RuleConfigItem[]
}

/** 沉淀规则（GET /policy/{id} rules） */
export interface RuleConfigItem {
  id: string
  ruleCode: string
  ruleName: string
  ruleType: string
  params?: string
  version: number
  status: string
}

/** 解析任务结果（POST /policy/parse） */
export interface PolicyParseResult {
  taskId: string
  articlesParsed: number
  ruleCandidates: number
  avgConfidence: number
  analyses?: number
}

/** 我的消息（GET /message/list；msg_type 分类/未读筛选） */
export interface MessageRecord {
  id: string
  msgType: string
  receiverId?: string
  title: string
  content: string
  channel?: string[] | string
  readStatus: string
  bizRef?: string
  createdAt?: string
}

/* ==================== V1.6.6 数据底座（WBS 3.0）/data/** ==================== */

/** 数据源台账（GET /data/sources） */
export interface DataSourceInfo {
  id: string
  sourceCode: string
  sourceType: string
  syncMode: string
  connType?: string
  connectConfig?: Record<string, unknown> | string
  frequency: string
  status: string
  lastRunTime?: string | null
  lastStatus?: string | null
  recordsCount?: string | number | null
}

/** 采集任务结果（POST /data/collect-tasks） */
export interface CollectTaskResult {
  taskId: string
  recordsCount: number
}

/** 数据质量报告（GET /data/quality/report） */
export interface QualityReport {
  completeness: number
  accuracy: number
  timeliness: number
}

/** 血缘节点（GET /data/lineage；V3.0 全量图谱：中文名/说明/业务域/数据分层） */
export interface LineageNode {
  nodeId: string
  nodeType: string
  /** V3.0 节点中文名 */
  nodeName?: string
  /** V3.0 中文说明 */
  description?: string
  /** V3.0 业务域（marketing/exchange/weather/common/trade/settle/policy/intel/forecast/decision/optimize/report/assess/model/system） */
  domain?: string
  /** V3.0 数据分层（source/collect/detail/indicator/model/report/business） */
  layer?: string
  fieldMapping?: Record<string, unknown> | string
  upstream?: LineageNode[] | string[]
  downstream?: LineageNode[] | string[]
}

export interface LineageResult {
  upstream: LineageNode[]
  downstream: LineageNode[]
}

/** 验证码（GET /auth/captcha；键 + 图片 Base64，5 分钟有效一次性消费） */
export interface CaptchaResult {
  captchaKey: string
  image: string
}

/* ==================== V1.6.6 预测中心（WBS 4.0）/forecast/** ==================== */

/** 模型注册信息（GET /forecast/models、GET /model/registry） */
export interface ModelInfo {
  modelCode: string
  modelName: string
  version: string
  framework: string
  metrics?: Record<string, unknown> | string
  status: string
}

/** 预测任务响应（POST /forecast/tasks） */
export interface ForecastTaskResp {
  taskId: string
  status?: string
}

/** 预测任务状态（GET /forecast/tasks/{taskId}） */
export interface ForecastTaskStatus {
  taskId: string
  status: string
  modelVersion?: string
  elapsedMs?: number
  errorMsg?: string
}

/** 96 点预测点（GET /forecast/results） */
export interface ForecastPoint {
  pointTime: string
  value: number
  lowerBound: number
  upperBound: number
  confidence: number
}

/** 训练任务响应（POST /forecast/models/train） */
export interface TrainTaskResp {
  taskId: string
}

/* ==================== V1.6.6 联合优化（WBS 5.0）/optimize/** ==================== */

/** 联合优化任务响应（POST /optimize/joint-tasks） */
export interface JointTaskResp {
  taskId: string
}

/** 优化任务状态（GET /optimize/joint-tasks/{taskId}） */
export interface JointTaskStatus {
  taskId: string
  status: string
  expectedRevenue?: number
  cvar?: number
  solver?: string
  elapsedMs?: number
}

/** 回测响应（POST /optimize/backtests） */
export interface BacktestResult {
  runId: string
}

/** 策略库条目（GET /optimize/strategies） */
export interface StrategyInfo {
  id: string
  strategyCode: string
  strategyName: string
  params?: string
  performance?: string
  status: string
  source: string
}

/* ==================== V1.6.6 模型平台 /model/** ==================== */

/** 在线推理结果（POST /model/inference） */
export interface InferenceResult {
  output: Record<string, unknown>
  latencyMs: number
  modelVersion: string
}

/** 离线评估结果（POST /model/evaluate） */
export interface EvaluateResult {
  mape: number
  directionAccuracy: number
  passed: boolean
}

/* ==================== V1.6.6 审批流（M7 移动端依赖）/flow/** ==================== */

/** 发起流程响应（POST /flow/start） */
export interface FlowStartResp {
  instanceId: string
  status?: string
  currentNode?: string
}

/** 流程待办任务（GET /flow/instances/{instanceId} currentTasks） */
export interface FlowTask {
  taskId: string
  node: string
  assignee?: string
  status: string
}

/** 流程实例详情（GET /flow/instances/{instanceId}） */
export interface FlowInstanceDetail {
  instanceId: string
  instanceNo?: string
  processKey?: string
  bizType?: string
  bizId?: string
  status: string
  currentNode?: string
  currentAssignee?: string
  startBy?: string
  startTime?: string
  endTime?: string | null
  currentTasks: FlowTask[]
  definitionName?: string
  definitionSteps?: FlowStep[]
  currentStepIndex?: number
  variables?: Record<string, unknown>
}

/** 流程定义（GET/POST/PUT /flow/definitions） */
export interface FlowDefinition {
  id: string
  processKey: string
  processName: string
  bizType: string
  steps: FlowStep[]
  status: string
  updatedAt?: string
}

/** 审批环节（流程定义 steps 元素） */
export interface FlowStep {
  stepNo: string
  stepName: string
  approveMode?: 'any' | 'all'
  roleCodes?: string[]
  userIds?: string[]
  timeoutHours?: number
}
