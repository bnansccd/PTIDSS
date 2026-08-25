/**
 * 系统管理 API（对齐后端 /admin/**：用户/角色/权限/区域/审计日志）
 * 生产前端此前仅有静态骨架（AdminView），此处补齐全部管理端点封装。
 */
import { request } from './http'

export interface PageResult<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
}

// ── 用户管理 ──
export interface AdminUser {
  id: number
  username: string
  displayName: string
  roleIds: number[]
  orgCode?: string
  phone?: string
  email?: string
  status: string
  lastLoginAt?: string
}

export function getAdminUsers(params: { pageNum?: number; pageSize?: number; keyword?: string; status?: string }) {
  return request<PageResult<AdminUser>>({ url: '/admin/users', method: 'get', params })
}

export function getAdminUser(id: number) {
  return request<AdminUser>({ url: `/admin/users/${id}`, method: 'get' })
}

export function getAdminUserRegions(id: number) {
  return request<string[]>({ url: `/admin/users/${id}/regions`, method: 'get' })
}

export function createAdminUser(payload: Record<string, unknown>) {
  return request<unknown>({ url: '/admin/users', method: 'post', data: payload })
}

export function updateAdminUser(payload: Record<string, unknown>) {
  return request<unknown>({ url: '/admin/users', method: 'put', data: payload })
}

export function resetAdminUserPassword(id: number, password: string) {
  return request<unknown>({ url: `/admin/users/${id}/password`, method: 'put', data: { password } })
}

export function deleteAdminUser(id: number) {
  return request<unknown>({ url: `/admin/users/${id}`, method: 'delete' })
}

// ── 角色管理 ──
export interface AdminRole {
  id: number
  roleCode: string
  roleName: string
  description?: string
  status: string
  regionCodes?: string[]
}

export function getAdminRoles(params?: { keyword?: string; status?: string }) {
  return request<AdminRole[]>({ url: '/admin/roles', method: 'get', params })
}

export function getAdminRole(id: number) {
  return request<AdminRole>({ url: `/admin/roles/${id}`, method: 'get' })
}

export function getAdminRolePermissions(id: number) {
  return request<number[]>({ url: `/admin/roles/${id}/permissions`, method: 'get' })
}

export function getAdminRoleRegions(id: number) {
  return request<string[]>({ url: `/admin/roles/${id}/regions`, method: 'get' })
}

export function saveAdminRolePermissions(id: number, permissionIds: number[]) {
  return request<unknown>({ url: `/admin/roles/${id}/permissions`, method: 'put', data: { permissionIds } })
}

export function createAdminRole(payload: Record<string, unknown>) {
  return request<unknown>({ url: '/admin/roles', method: 'post', data: payload })
}

export function updateAdminRole(payload: Record<string, unknown>) {
  return request<unknown>({ url: '/admin/roles', method: 'put', data: payload })
}

export function deleteAdminRole(id: number) {
  return request<unknown>({ url: `/admin/roles/${id}`, method: 'delete' })
}

// ── 权限管理（三级权限：menu/api/data） ──
export interface AdminPermission {
  id: number
  permCode: string
  permName: string
  resourceType: string
  resourcePattern?: string
  status: string
}

export function getAdminPermissions(params?: { keyword?: string; resourceType?: string }) {
  return request<AdminPermission[]>({ url: '/admin/permissions', method: 'get', params })
}

export function createAdminPermission(payload: Record<string, unknown>) {
  return request<unknown>({ url: '/admin/permissions', method: 'post', data: payload })
}

export function updateAdminPermission(payload: Record<string, unknown>) {
  return request<unknown>({ url: '/admin/permissions', method: 'put', data: payload })
}

export function deleteAdminPermission(id: number) {
  return request<unknown>({ url: `/admin/permissions/${id}`, method: 'delete' })
}

// ── 区域管理（多省配置化） ──
export interface AdminRegion {
  id: number
  regionCode: string
  regionName: string
  marketSupport?: string[]
  exchangeChannel?: string
  settlementPeriod?: string
  status: string
  launchOrder?: number
}

export function getAdminRegions(params?: { keyword?: string; status?: string }) {
  return request<AdminRegion[]>({ url: '/admin/regions', method: 'get', params })
}

export function createAdminRegion(payload: Record<string, unknown>) {
  return request<unknown>({ url: '/admin/regions', method: 'post', data: payload })
}

export function updateAdminRegion(payload: Record<string, unknown>) {
  return request<unknown>({ url: '/admin/regions', method: 'put', data: payload })
}

export function deleteAdminRegion(id: number) {
  return request<unknown>({ url: `/admin/regions/${id}`, method: 'delete' })
}

// ── 审计日志 ──
export interface AuditLogItem {
  id: number
  username?: string
  action?: string
  targetType?: string
  targetId?: string
  ip?: string
  result?: string
  regionCode?: string
  createdAt?: string
}

export function getAuditLogs(params: { pageNum?: number; pageSize?: number; action?: string; username?: string; regionCode?: string; result?: string }) {
  return request<PageResult<AuditLogItem>>({ url: '/admin/logs', method: 'get', params })
}

// ── 系统配置（DDL 17 sys_config，系统管理--系统配置 全面实现） ──
export interface SysConfigItem {
  id: number
  configKey: string
  configName: string
  description?: string
  configGroup: string
  configType: string // string/number/boolean/select/json
  enumValues?: string[]
  value?: string
  isSensitive?: boolean
  isBuiltin?: boolean
  status?: string
  sortOrder?: number
  createdAt?: string
  updatedAt?: string
}

/** 配置列表（可按分组/关键字过滤；敏感项脱敏 ******） */
export function getSysConfigs(params: { group?: string; keyword?: string }) {
  return request<SysConfigItem[]>({ url: '/admin/configs', method: 'get', params })
}

/** 新增配置（admin；key 唯一；敏感项加密落库） */
export function createSysConfig(payload: Record<string, unknown>) {
  return request<unknown>({ url: '/admin/configs', method: 'post', data: payload })
}

/** 编辑配置（admin；内置项 key 不可改；敏感项 ****** 保留原值） */
export function updateSysConfig(id: number, payload: Record<string, unknown>) {
  return request<unknown>({ url: `/admin/configs/${id}`, method: 'put', data: payload })
}

/** 删除配置（admin；内置项禁删，可禁用） */
export function deleteSysConfig(id: number) {
  return request<unknown>({ url: `/admin/configs/${id}`, method: 'delete' })
}
