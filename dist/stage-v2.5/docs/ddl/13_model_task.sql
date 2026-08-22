-- =====================================================================
-- 13. 模型任务报告（V2.4）：训练触发/离线评估/在线推理 → 详细报告和过程，与前面对标及变化
-- 产品化诉求：用户理解模型平台任务执行过程（步骤时间线/结果指标/与上次任务对标变化）
-- 配套代码：ModelService（runTask/listTasks/taskDetail）/ ModelController（/model/tasks/**）
-- 执行方式：psql -h 127.0.0.1 -p 5432 -U ptidss -d ptidss -f 13_model_task.sql
-- =====================================================================

CREATE TABLE IF NOT EXISTS model_task (
  id                  BIGINT PRIMARY KEY,
  task_type           VARCHAR(16) NOT NULL CHECK (task_type IN ('train','evaluate','inference')),
  model_code          VARCHAR(64),                          -- 模型编码（price/load/generation 或算法编码）
  model_version       VARCHAR(32),                          -- 任务关联模型版本（推理/评估）
  task_name           VARCHAR(128),                         -- 任务名称（如 价格预测 日增量训练）
  input_json          JSONB NOT NULL DEFAULT '{}',          -- 任务输入快照（数据集区间/超参/测试集版本）
  process_steps       JSONB NOT NULL DEFAULT '[]',          -- 执行过程步骤 [{step,detail,timeMs}]
  result_json         JSONB NOT NULL DEFAULT '{}',          -- 结果（指标/序列摘要/回执）
  compare_json        JSONB NOT NULL DEFAULT '{}',          -- 与前面对标：{baselineTaskId, baselineMetrics, delta}
  status              VARCHAR(16) NOT NULL DEFAULT 'running' CHECK (status IN ('queued','running','success','failed')),
  latency_ms          INT,                                  -- 执行耗时（毫秒）
  created_by          VARCHAR(32),
  finished_at         TIMESTAMP,                            -- 完成时间（success/failed）
  created_at          TIMESTAMP NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP NOT NULL DEFAULT now(),
  version             INT NOT NULL DEFAULT 1,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX IF NOT EXISTS ix_model_task_type ON model_task(task_type, created_at);
CREATE INDEX IF NOT EXISTS ix_model_task_model ON model_task(model_code, task_type, created_at);
