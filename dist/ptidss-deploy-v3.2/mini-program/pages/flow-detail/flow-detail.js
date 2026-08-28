// 审批详情：GET /flow/instances/{id}（状态/当前环节/步骤进度/审批留痕）
// 办理：POST /flow/instances/{id}/advance（approve → 下一环节/完成；reject → 终止，留痕）
const { request } = require('../../utils/request')
const util = require('../../utils/util')

Page({
  data: {
    instanceId: '',
    detail: null,
    steps: [],
    stepNames: [],
    stepStatus: [],
    currentStepIndex: -1,
    actions: [],
    showAction: false,
    actionType: '', // approve/reject
    comment: '',
    submitting: false
  },

  onLoad(options) {
    this.setData({ instanceId: options.id || '' })
    this.load()
  },

  onPullDownRefresh() {
    this.load().finally(() => wx.stopPullDownRefresh())
  },

  load() {
    const { instanceId } = this.data
    if (!instanceId) return Promise.resolve()
    return request({ url: '/flow/instances/' + instanceId })
      .then((d) => {
        const steps = (d && d.definitionSteps) || []
        const currentIdx = d && typeof d.currentStepIndex === 'number' ? d.currentStepIndex : -1
        this.setData({
          detail: {
            instanceNo: d.instanceNo,
            processName: d.definitionName || d.processKey,
            bizTypeName: util.bizTypeName(d.bizType),
            bizId: d.bizId,
            status: d.status,
            statusName: util.flowStatusName(d.status),
            currentNode: d.currentNode,
            currentAssignee: d.currentAssignee,
            startBy: d.startBy,
            startTime: util.formatDateTime(d.startTime),
            endTime: util.formatDateTime(d.endTime)
          },
          steps: steps,
          stepNames: steps.map((s) => s.stepName || s.stepNo),
          currentStepIndex: currentIdx,
          actions: ((d && d.variables && d.variables.actions) || []).map((a) => ({
            node: a.node,
            action: a.action,
            actionName: a.action === 'approve' ? '通过' : '驳回',
            operator: a.operator,
            comment: a.comment,
            time: a.time
          }))
        })
        // 步骤状态：已过节点 completed / 当前节点 current / 未到 pending
        const stepStatus = steps.map((s, i) =>
          i < currentIdx ? 'completed' : i === currentIdx ? 'current' : 'pending'
        )
        this.setData({ stepStatus: stepStatus })
      })
  },

  /** 是否可办理：运行中（菜单权限不足时 advance 会被服务端拒绝并提示） */
  canAct() {
    return this.data.detail && this.data.detail.status === 'running'
  },

  openApprove() {
    if (!this.canAct()) return
    this.setData({ showAction: true, actionType: 'approve', comment: '' })
  },

  openReject() {
    if (!this.canAct()) return
    this.setData({ showAction: true, actionType: 'reject', comment: '' })
  },

  closeAction() {
    this.setData({ showAction: false })
  },

  onComment(e) {
    this.setData({ comment: e.detail.value })
  },

  /** 提交办理意见（驳回时意见必填） */
  submitAction() {
    const { instanceId, actionType, comment, submitting } = this.data
    if (submitting) return
    if (actionType === 'reject' && !comment.trim()) {
      wx.showToast({ title: '驳回必须填写原因', icon: 'none' })
      return
    }
    this.setData({ submitting: true })
    request({
      url: '/flow/instances/' + instanceId + '/advance',
      method: 'POST',
      data: { action: actionType, comment: comment }
    })
      .then((resp) => {
        wx.showToast({ title: actionType === 'approve' ? '已通过' : '已驳回', icon: 'success' })
        this.setData({ showAction: false })
        this.load()
      })
      .catch(() => {})
      .finally(() => this.setData({ submitting: false }))
  }
})
