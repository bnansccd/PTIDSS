/**
 * 周期工具（V2.4 操作友好性：周期类时间"选择日期 + 周期类型"自动推导周期标识）：
 * - 用户只选起始日期与周期（日/周/月/季/年），系统自动计算报表/考核周期
 * - 兼容后端 period 字符串契约（daily→yyyy-MM-dd、monthly→yyyy-MM、…）
 */

export type PeriodUnit = 'day' | 'week' | 'month' | 'quarter' | 'year'

export const PERIOD_UNITS: Array<{ value: PeriodUnit; label: string }> = [
  { value: 'day', label: '日' },
  { value: 'week', label: '周' },
  { value: 'month', label: '月' },
  { value: 'quarter', label: '季' },
  { value: 'year', label: '年' },
]

function pad(n: number): string {
  return String(n).padStart(2, '0')
}

/** 由起始日期（yyyy-MM-dd）+ 周期类型推导周期标识（ISO 周编号：yyyy-ww） */
export function periodOf(date: string, unit: PeriodUnit): string {
  const m = /^(\d{4})-(\d{2})-(\d{2})/.exec(date)
  if (!m) return date
  const year = Number(m[1])
  const month = Number(m[2])
  const day = Number(m[3])
  switch (unit) {
    case 'day':
      return `${year}-${pad(month)}-${pad(day)}`
    case 'month':
      return `${year}-${pad(month)}`
    case 'quarter':
      return `${year}-Q${Math.ceil(month / 3)}`
    case 'year':
      return `${year}`
    case 'week': {
      const d = new Date(Date.UTC(year, month - 1, day))
      const dayNum = d.getUTCDay() || 7
      d.setUTCDate(d.getUTCDate() + 4 - dayNum)
      const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1))
      const week = Math.ceil(((d.getTime() - yearStart.getTime()) / 86400000 + 1) / 7)
      return `${d.getUTCFullYear()}-W${pad(week)}`
    }
    default:
      return date
  }
}

/** 当前日期字符串（yyyy-MM-dd） */
export function todayStr(): string {
  const d = new Date()
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

/** 当前周期标识（按周期类型） */
export function currentPeriod(unit: PeriodUnit = 'month'): string {
  return periodOf(todayStr(), unit)
}
