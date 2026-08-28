// PTIDSS 移动端工具函数：日期格式化 / 编码+名称映射（对齐 Web 端"编码+名称显示"要求）

function pad(n) {
  return n < 10 ? '0' + n : '' + n
}

/** yyyy-MM-dd */
function formatDate(d) {
  if (!d) return ''
  const t = typeof d === 'string' || typeof d === 'number' ? new Date(d) : d
  return t.getFullYear() + '-' + pad(t.getMonth() + 1) + '-' + pad(t.getDate())
}

/** yyyy-MM-dd HH:mm */
function formatDateTime(d) {
  if (!d) return ''
  const t = typeof d === 'string' || typeof d === 'number' ? new Date(d) : d
  return formatDate(t) + ' ' + pad(t.getHours()) + ':' + pad(t.getMinutes())
}

/** 今天 yyyy-MM-dd */
function today() {
  return formatDate(new Date())
}

/** 数字保留 2 位小数 */
function fixed2(v) {
  const n = Number(v)
  return isNaN(n) ? '-' : n.toFixed(2)
}

/** 万元格式化 */
function formatWan(v) {
  const n = Number(v)
  if (isNaN(n)) return '-'
  return (n / 10000).toFixed(2)
}

// ---------- 编码 → 名称（业务侧展示友好） ----------

/** 消息类型 */
function msgTypeName(t) {
  const map = {
    market_alert: '市场预警',
    forecast_summary: '预测摘要',
    decision_todo: '决策待办',
    settlement_diff: '结算差异',
    assess_reminder: '考核提醒'
  }
  return map[t] || t || '-'
}

/** 流程状态 */
function flowStatusName(s) {
  const map = { running: '进行中', completed: '已完成', terminated: '已终止' }
  return map[s] || s || '-'
}

/** 业务类型 */
function bizTypeName(t) {
  const map = { decision: '决策确认', declaration: '交易申报', ticket: '差异工单', appeal: '考核申诉' }
  return map[t] || t || '-'
}

/** 决策会话类型 */
function sessionTypeName(t) {
  const map = {
    rolling: '日滚动方案',
    spot_quote: '现货报价',
    joint_optimize: '联合优化'
  }
  return map[t] || t || '-'
}

/** 人审状态 */
function reviewStatusName(s) {
  const map = {
    pending: '待确认',
    confirmed: '已确认',
    modified: '已修改',
    rejected: '已驳回'
  }
  return map[s] || s || '-'
}

/** 复盘报告类型 */
function reportTypeName(t) {
  const map = { weekly: '周报', monthly: '月报', special: '专项' }
  return map[t] || t || '-'
}

/** 复盘报告状态 */
function reportStatusName(s) {
  const map = { draft: '草稿', published: '已发布', archived: '已归档' }
  return map[s] || s || '-'
}

/** 归因层级 */
function layerName(l) {
  const map = { forecast: '预测层', decision: '决策层', execution: '执行层' }
  return map[l] || l || '-'
}

/** 归因方向 */
function directionName(d) {
  const map = { positive: '正向影响', negative: '负向影响' }
  return map[d] || d || '-'
}

module.exports = {
  formatDate,
  formatDateTime,
  today,
  fixed2,
  formatWan,
  msgTypeName,
  flowStatusName,
  bizTypeName,
  sessionTypeName,
  reviewStatusName,
  reportTypeName,
  reportStatusName,
  layerName,
  directionName
}
