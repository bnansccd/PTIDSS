/**
 * 菜单-权限码映射（对齐 docs/ddl/07_seed_data.sql sys_permission 种子与 OpenAPI V1.1 契约）：
 * - 路由 meta.permission 与侧边菜单共用本映射，任一权限码命中即可访问
 * - 空数组表示登录即可访问（无需权限码）
 * - 后端接口仍由方法级 AOP @RequiresPermissions 兜底（14003），前端过滤仅为体验层
 */
export const MENU_PERMISSIONS: Record<string, string[]> = {
  '/dashboard': [],
  '/market': ['menu:market'],
  '/trade': ['menu:trade'],
  '/decision': ['menu:decision'],
  '/settlement': ['menu:settlement'],
  '/data-manage': ['menu:report', 'menu:settlement'],
  '/intel': ['menu:intel'],
  '/policy': ['menu:policy'],
  '/message': ['menu:message'],
  '/data-platform': ['menu:data'],
  '/forecast': ['menu:forecast'],
  '/model': ['menu:model'],
  '/agent': ['menu:decision'],
  '/optimize': ['menu:optimize'],
  '/flow': ['menu:flow'],
  '/review': ['menu:review'],
  '/assess': ['menu:review'],
  '/report': ['menu:report'],
  '/admin': ['menu:admin'],
}

/** 判断用户权限码集合是否可访问指定路径 */
export function canAccess(path: string, permissions: string[] | undefined): boolean {
  const need = MENU_PERMISSIONS[path]
  if (!need || need.length === 0) return true
  const perms = new Set(permissions ?? [])
  return need.some((p) => perms.has(p))
}
