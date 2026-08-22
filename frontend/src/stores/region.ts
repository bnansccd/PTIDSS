/**
 * 多省区域上下文（评审决议⑤ / 多省 region 路由方案）：
 * - currentRegion 持久化于 localStorage，登录后从 user.regions 初始化
 * - 切换区域后所有请求自动携带 X-Region-Code 头
 */
import { defineStore } from 'pinia'

const REGION_KEY = 'ptidss_region_code'

/** 默认区域池：与 DDL v1.0 07_seed_data.sql 的 sys_region 种子对齐（契约 V1.0 无 regions 字段时的兜底） */
export const DEFAULT_REGIONS = ['CN-11', 'CN-31', 'CN-32', 'CN-33', 'CN-41']

export const useRegionStore = defineStore('region', {
  state: () => ({
    currentRegion: localStorage.getItem(REGION_KEY) ?? '',
    availableRegions: [] as string[],
  }),
  actions: {
    /** 登录成功后按用户授权区域初始化（空则用默认区域池兜底，默认首个启用区域） */
    init(regions: string[] = []) {
      this.availableRegions = regions.length > 0 ? regions : DEFAULT_REGIONS
      if (!this.currentRegion && this.availableRegions.length > 0) {
        this.currentRegion = this.availableRegions[0]
        localStorage.setItem(REGION_KEY, this.currentRegion)
      }
    },
    setRegion(code: string) {
      this.currentRegion = code
      localStorage.setItem(REGION_KEY, code)
    },
    reset() {
      this.currentRegion = ''
      this.availableRegions = []
      localStorage.removeItem(REGION_KEY)
    },
  },
})
