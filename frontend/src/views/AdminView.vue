<template>
  <div>
    <!-- 页头：Tab 导航（对齐原型 admin-system.html 五 Tab + 区域/权限扩展） -->
    <div class="card">
      <div class="form-row">
        <h3 style="margin: 0">系统管理（原型 admin-system · 机构/用户/角色/权限/审计/配置）</h3>
        <span class="muted">数据对接：/admin/users · /admin/roles · /admin/permissions · /admin/regions · /admin/logs</span>
      </div>
      <div class="form-row" style="margin-bottom: 0">
        <button v-for="t in tabs" :key="t.key" class="btn" :class="tab === t.key ? 'btn-primary' : ''" @click="switchTab(t.key)">{{ t.label }}</button>
      </div>
    </div>

    <!-- ═══ Tab 1 用户管理 ═══ -->
    <template v-if="tab === 'users'">
      <div class="card">
        <div class="form-row">
          <input v-model="userQuery.keyword" placeholder="用户名/显示名" style="width: 200px" @keyup.enter="loadUsers(1)" />
          <select v-model="userQuery.status" style="width: 110px">
            <option value="">全部状态</option>
            <option value="active">正常</option>
            <option value="locked">锁定</option>
            <option value="disabled">停用</option>
          </select>
          <button class="btn btn-primary" @click="loadUsers(1)">查询</button>
          <button class="btn" @click="openUserCreate">新建用户</button>
          <span class="badge badge-blue">共 {{ userTotal }} 人</span>
        </div>
        <table>
          <thead>
            <tr><th>用户名</th><th>显示名</th><th>角色</th><th>机构</th><th>手机号</th><th>状态</th><th>最近登录</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="users.length === 0"><td colspan="8" class="muted">暂无用户</td></tr>
            <tr v-for="u in users" :key="u.id">
              <td class="mono">{{ u.username }}</td>
              <td>{{ u.displayName }}</td>
              <td class="muted">{{ roleNames(u.roleIds) }}</td>
              <td class="mono">{{ u.orgCode || '-' }}</td>
              <td>{{ u.phone || '-' }}</td>
              <td><span class="badge" :class="statusClass(u.status)">{{ statusLabel(u.status) }}</span></td>
              <td class="mono">{{ u.lastLoginAt || '-' }}</td>
              <td>
                <button class="btn" @click="openUserEdit(u)">编辑</button>
                <button class="btn" @click="onResetPassword(u)">重置密码</button>
                <button class="btn" @click="onDeleteUser(u)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div class="form-row" style="margin-top: 10px">
          <button class="btn" :disabled="userPage <= 1" @click="loadUsers(userPage - 1)">上一页</button>
          <span class="muted">第 {{ userPage }} / {{ userTotalPages }} 页</span>
          <button class="btn" :disabled="userPage >= userTotalPages" @click="loadUsers(userPage + 1)">下一页</button>
        </div>
      </div>

      <!-- 用户新建/编辑弹窗 -->
      <div v-if="userDialog" class="modal-mask" @click.self="userDialog = false">
        <div class="modal-card">
          <h3>{{ userForm.id ? '编辑用户' : '新建用户' }}</h3>
          <div class="form-row">
            <label class="f">用户名</label>
            <input v-model="userForm.username" :disabled="!!userForm.id" placeholder="登录账号" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">显示名</label>
            <input v-model="userForm.displayName" placeholder="真实姓名" style="flex: 1" />
          </div>
          <div v-if="!userForm.id" class="form-row">
            <label class="f">初始密码</label>
            <input v-model="userForm.password" type="password" placeholder="不少于 6 位" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">角色（可多选）</label>
            <select v-model="userForm.roleIds" multiple size="3" style="flex: 1">
              <option v-for="r in roles" :key="r.id" :value="r.id">{{ r.roleName }}（{{ r.roleCode }}）</option>
            </select>
          </div>
          <div class="form-row">
            <label class="f">授权区域（多省）</label>
            <select v-model="userForm.regions" multiple size="2" style="flex: 1">
              <option v-for="rg in regions" :key="rg.regionCode" :value="rg.regionCode">{{ rg.regionName }}（{{ rg.regionCode }}）</option>
            </select>
          </div>
          <div class="form-row">
            <label class="f">机构编码</label>
            <input v-model="userForm.orgCode" placeholder="如 HQ / JS-CN32" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">手机号</label>
            <input v-model="userForm.phone" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">邮箱</label>
            <input v-model="userForm.email" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">状态</label>
            <select v-model="userForm.status" style="width: 140px">
              <option value="active">正常</option>
              <option value="locked">锁定</option>
              <option value="disabled">停用</option>
            </select>
          </div>
          <div class="form-row" style="justify-content: flex-end; margin-top: 16px">
            <button class="btn" @click="userDialog = false">取消</button>
            <button class="btn btn-primary" @click="submitUser">保存</button>
          </div>
        </div>
      </div>
    </template>

    <!-- ═══ Tab 2 角色权限 ═══ -->
    <template v-else-if="tab === 'roles'">
      <div class="card">
        <div class="form-row">
          <input v-model="roleQuery.keyword" placeholder="角色编码/名称" style="width: 200px" @keyup.enter="loadRoles" />
          <button class="btn btn-primary" @click="loadRoles">查询</button>
          <button class="btn" @click="openRoleCreate">新建角色</button>
          <span class="muted">角色编码受 DDL CHECK 约束（trader/analyst/settlement/admin/manager/compliance/mobile）</span>
        </div>
        <table>
          <thead>
            <tr><th>角色编码</th><th>角色名称</th><th>描述</th><th>状态</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="roles.length === 0"><td colspan="5" class="muted">暂无角色</td></tr>
            <tr v-for="r in roles" :key="r.id">
              <td class="mono">{{ r.roleCode }}</td>
              <td>{{ r.roleName }}</td>
              <td class="muted">{{ r.description || '-' }}</td>
              <td><span class="badge" :class="r.status === 'active' ? 'badge-green' : 'badge-gray'">{{ r.status === 'active' ? '启用' : '停用' }}</span></td>
              <td>
                <button class="btn" @click="openRoleEdit(r)">编辑</button>
                <button class="btn" @click="openPermAssign(r)">分配权限</button>
                <button class="btn" @click="onDeleteRole(r)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 角色新建/编辑弹窗 -->
      <div v-if="roleDialog" class="modal-mask" @click.self="roleDialog = false">
        <div class="modal-card">
          <h3>{{ roleForm.id ? '编辑角色' : '新建角色' }}</h3>
          <div class="form-row">
            <label class="f">角色编码</label>
            <input v-model="roleForm.roleCode" :disabled="!!roleForm.id" placeholder="如 trader / manager" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">角色名称</label>
            <input v-model="roleForm.roleName" placeholder="如 交易员" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">描述</label>
            <input v-model="roleForm.description" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">状态</label>
            <select v-model="roleForm.status" style="width: 140px">
              <option value="active">启用</option>
              <option value="disabled">停用</option>
            </select>
          </div>
          <div class="form-row" style="justify-content: flex-end; margin-top: 16px">
            <button class="btn" @click="roleDialog = false">取消</button>
            <button class="btn btn-primary" @click="submitRole">保存</button>
          </div>
        </div>
      </div>

      <!-- 权限分配弹窗（三级权限按类型分组勾选，全量覆盖保存） -->
      <div v-if="permDialog" class="modal-mask" @click.self="permDialog = false">
        <div class="modal-card">
          <h3>分配权限：{{ permTarget?.roleName }}（{{ permTarget?.roleCode }}）</h3>
          <div v-for="g in permGroups" :key="g.type" class="perm-group">
            <div class="perm-group-title">{{ permTypeLabel(g.type) }}（{{ g.items.length }}）</div>
            <label v-for="p in g.items" :key="p.id" class="perm-item">
              <input v-model="permChecked" type="checkbox" :value="p.id" />
              {{ p.permName }} <span class="mono muted">{{ p.permCode }}</span>
            </label>
          </div>
          <div class="form-row" style="justify-content: flex-end; margin-top: 16px">
            <button class="btn" @click="permDialog = false">取消</button>
            <button class="btn btn-primary" @click="savePerms">保存（{{ permChecked.length }} 项）</button>
          </div>
        </div>
      </div>
    </template>

    <!-- ═══ Tab 3 权限管理 ═══ -->
    <template v-else-if="tab === 'perms'">
      <div class="card">
        <div class="form-row">
          <input v-model="permQuery.keyword" placeholder="权限编码/名称" style="width: 200px" @keyup.enter="loadPerms" />
          <select v-model="permQuery.resourceType" style="width: 130px">
            <option value="">全部类型</option>
            <option value="menu">menu 菜单</option>
            <option value="api">api 接口</option>
            <option value="data">data 数据</option>
          </select>
          <button class="btn btn-primary" @click="loadPerms">查询</button>
          <button class="btn" @click="openPermCreate">新建权限</button>
          <span class="badge badge-blue">共 {{ perms.length }} 条</span>
        </div>
        <table>
          <thead>
            <tr><th>权限编码</th><th>权限名称</th><th>资源类型</th><th>资源模式</th><th>状态</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="perms.length === 0"><td colspan="6" class="muted">暂无权限</td></tr>
            <tr v-for="p in perms" :key="p.id">
              <td class="mono">{{ p.permCode }}</td>
              <td>{{ p.permName }}</td>
              <td><span class="badge" :class="permTypeClass(p.resourceType)">{{ p.resourceType }}</span></td>
              <td class="mono muted">{{ p.resourcePattern || '-' }}</td>
              <td><span class="badge" :class="p.status === 'active' ? 'badge-green' : 'badge-gray'">{{ enableStatusLabel(p.status) }}（{{ p.status }}）</span></td>
              <td>
                <button class="btn" @click="openPermEdit(p)">编辑</button>
                <button class="btn" @click="onDeletePerm(p)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 权限新建/编辑弹窗 -->
      <div v-if="permFormDialog" class="modal-mask" @click.self="permFormDialog = false">
        <div class="modal-card">
          <h3>{{ permForm.id ? '编辑权限' : '新建权限' }}</h3>
          <div class="form-row">
            <label class="f">权限编码</label>
            <input v-model="permForm.permCode" placeholder="如 menu:admin / api:user:create" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">权限名称</label>
            <input v-model="permForm.permName" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">资源类型</label>
            <select v-model="permForm.resourceType" style="flex: 1">
              <option value="menu">menu 菜单</option>
              <option value="api">api 接口</option>
              <option value="data">data 数据</option>
            </select>
          </div>
          <div class="form-row">
            <label class="f">资源模式</label>
            <input v-model="permForm.resourcePattern" placeholder="如 /admin/users/** 或 region:*" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">状态</label>
            <select v-model="permForm.status" style="width: 140px">
              <option value="active">启用</option>
              <option value="disabled">停用</option>
            </select>
          </div>
          <div class="form-row" style="justify-content: flex-end; margin-top: 16px">
            <button class="btn" @click="permFormDialog = false">取消</button>
            <button class="btn btn-primary" @click="submitPerm">保存</button>
          </div>
        </div>
      </div>
    </template>

    <!-- ═══ Tab 4 区域管理（多省配置化，评审决议⑤） ═══ -->
    <template v-else-if="tab === 'regions'">
      <div class="card">
        <div class="form-row">
          <input v-model="regionQuery.keyword" placeholder="区域编码/名称" style="width: 200px" @keyup.enter="loadRegions" />
          <button class="btn btn-primary" @click="loadRegions">查询</button>
          <button class="btn" @click="openRegionCreate">新建区域</button>
          <span class="badge badge-blue">共 {{ regions.length }} 省</span>
        </div>
        <table>
          <thead>
            <tr><th>区域编码</th><th>区域名称</th><th>市场品种</th><th>交易通道</th><th>结算周期</th><th>状态</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-if="regions.length === 0"><td colspan="7" class="muted">暂无区域</td></tr>
            <tr v-for="rg in regions" :key="rg.id">
              <td class="mono">{{ rg.regionCode }}</td>
              <td>{{ rg.regionName }}</td>
              <td class="muted">{{ arrayText(rg.marketSupport) }}</td>
              <td class="mono">{{ rg.exchangeChannel || '-' }}</td>
              <td class="mono">{{ rg.settlementPeriod || '-' }}</td>
              <td><span class="badge" :class="rg.status === 'active' ? 'badge-green' : 'badge-gray'">{{ enableStatusLabel(rg.status) }}（{{ rg.status }}）</span></td>
              <td>
                <button class="btn" @click="openRegionEdit(rg)">编辑</button>
                <button class="btn" @click="onDeleteRegion(rg)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 区域新建/编辑弹窗 -->
      <div v-if="regionDialog" class="modal-mask" @click.self="regionDialog = false">
        <div class="modal-card">
          <h3>{{ regionForm.id ? '编辑区域' : '新建区域' }}</h3>
          <div class="form-row">
            <label class="f">区域编码</label>
            <input v-model="regionForm.regionCode" :disabled="!!regionForm.id" placeholder="如 CN-32" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">区域名称</label>
            <input v-model="regionForm.regionName" placeholder="如 江苏省" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">市场品种</label>
            <input v-model="regionMarketText" placeholder="逗号分隔：现货,中长期" style="flex: 1" />
          </div>
          <div class="form-row">
            <label class="f">交易通道</label>
            <select v-model="regionForm.exchangeChannel" style="flex: 1">
              <option value="rest">rest</option>
              <option value="sftp">sftp</option>
              <option value="both">both</option>
            </select>
          </div>
          <div class="form-row">
            <label class="f">结算周期</label>
            <select v-model="regionForm.settlementPeriod" style="flex: 1">
              <option value="natural_month">natural_month</option>
              <option value="trading_month">trading_month</option>
            </select>
          </div>
          <div class="form-row">
            <label class="f">状态</label>
            <select v-model="regionForm.status" style="width: 140px">
              <option value="active">启用</option>
              <option value="disabled">停用</option>
            </select>
          </div>
          <div class="form-row" style="justify-content: flex-end; margin-top: 16px">
            <button class="btn" @click="regionDialog = false">取消</button>
            <button class="btn btn-primary" @click="submitRegion">保存</button>
          </div>
        </div>
      </div>
    </template>

    <!-- ═══ Tab 5 审计日志（等保三级审计，按省检索） ═══ -->
    <template v-else-if="tab === 'logs'">
      <div class="card">
        <div class="form-row">
          <input v-model="logQuery.username" placeholder="操作人" style="width: 140px" @keyup.enter="loadLogs(1)" />
          <input v-model="logQuery.action" placeholder="操作动作" style="width: 160px" @keyup.enter="loadLogs(1)" />
          <input v-model="logQuery.regionCode" placeholder="区域编码" style="width: 120px" @keyup.enter="loadLogs(1)" />
          <select v-model="logQuery.result" style="width: 110px">
            <option value="">全部结果</option>
            <option value="success">成功</option>
            <option value="fail">失败</option>
          </select>
          <button class="btn btn-primary" @click="loadLogs(1)">查询</button>
          <span class="badge badge-blue">共 {{ logTotal }} 条</span>
        </div>
        <table>
          <thead>
            <tr><th>时间</th><th>操作人</th><th>操作</th><th>对象类型</th><th>对象 ID</th><th>结果</th><th>IP</th><th>区域</th></tr>
          </thead>
          <tbody>
            <tr v-if="logs.length === 0"><td colspan="8" class="muted">暂无日志</td></tr>
            <tr v-for="l in logs" :key="l.id">
              <td class="mono">{{ l.createdAt }}</td>
              <td class="mono">{{ l.username || '-' }}</td>
              <td>{{ l.action || '-' }}</td>
              <td class="mono">{{ l.targetType || '-' }}</td>
              <td class="mono">{{ l.targetId || '-' }}</td>
              <td><span class="badge" :class="l.result === 'success' ? 'badge-green' : 'badge-red'">{{ l.result || '-' }}</span></td>
              <td class="mono">{{ l.ip || '-' }}</td>
              <td class="mono">{{ l.regionCode || '-' }}</td>
            </tr>
          </tbody>
        </table>
        <div class="form-row" style="margin-top: 10px">
          <button class="btn" :disabled="logPage <= 1" @click="loadLogs(logPage - 1)">上一页</button>
          <span class="muted">第 {{ logPage }} / {{ logTotalPages }} 页</span>
          <button class="btn" :disabled="logPage >= logTotalPages" @click="loadLogs(logPage + 1)">下一页</button>
        </div>
      </div>
    </template>

    <!-- ═══ Tab 6 系统配置（评审决议项 · 与 DDL v1.0 sys_region 对齐） ═══ -->
    <template v-else>
      <div class="card">
        <h3>系统配置（评审决议项 · 与 DDL v1.0 sys_region 对齐）</h3>
        <table>
          <thead>
            <tr><th>配置项</th><th>说明</th><th>当前值</th><th>枚举</th></tr>
          </thead>
          <tbody>
            <tr v-for="c in configs" :key="c.key">
              <td class="mono">{{ c.key }}</td>
              <td>{{ c.desc }}</td>
              <td><span class="badge badge-blue">{{ c.value }}</span></td>
              <td class="muted">{{ c.enum }}</td>
            </tr>
          </tbody>
        </table>
        <div class="muted" style="margin-top: 8px">
          正式实现：配置中心下发（exchange.channel / settlement.periodMode / region.mode），此处为骨架展示。
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRegionStore } from '@/stores/region'
import {
  createAdminPermission, createAdminRegion, createAdminRole, createAdminUser,
  deleteAdminPermission, deleteAdminRegion, deleteAdminRole, deleteAdminUser,
  getAdminPermissions, getAdminRegions, getAdminRolePermissions, getAdminRoles,
  getAdminUser, getAdminUserRegions, getAdminUsers, getAuditLogs,
  resetAdminUserPassword, saveAdminRolePermissions, updateAdminPermission,
  updateAdminRegion, updateAdminRole, updateAdminUser,
  type AdminPermission, type AdminRegion, type AdminRole, type AdminUser, type AuditLogItem,
} from '@/api/admin'

const region = useRegionStore()

const tabs = [
  { key: 'users', label: '用户管理' },
  { key: 'roles', label: '角色权限' },
  { key: 'perms', label: '权限管理' },
  { key: 'regions', label: '区域管理' },
  { key: 'logs', label: '审计日志' },
  { key: 'config', label: '系统配置' },
]
const tab = ref('users')
function switchTab(k: string) {
  tab.value = k
  if (k === 'users') loadUsers(1)
  else if (k === 'roles') { loadRoles(); loadPerms() }
  else if (k === 'perms') loadPerms()
  else if (k === 'regions') loadRegions()
  else if (k === 'logs') loadLogs(1)
}

// ── 通用工具 ──
function toast(msg: string, type: 'success' | 'error' = 'success') {
  const el = document.createElement('div')
  el.className = `toast toast-${type}`
  el.textContent = msg
  document.body.appendChild(el)
  setTimeout(() => el.remove(), 2600)
}
function arrayText(v?: string[] | string | null): string {
  if (!v) return '-'
  if (Array.isArray(v)) return v.join('、') || '-'
  try {
    const parsed = JSON.parse(v) as unknown
    return Array.isArray(parsed) ? parsed.map(String).join('、') : String(parsed)
  } catch {
    return String(v)
  }
}
function statusLabel(s: string): string {
  return s === 'active' ? '正常' : s === 'locked' ? '锁定' : '停用'
}

/** V2.4 编码+名称：权限/区域启用状态中文标签 */
function enableStatusLabel(s: string): string {
  return { active: '启用', enabled: '启用', disabled: '停用', pending: '待启用' }[s] ?? s
}
function statusClass(s: string): string {
  return s === 'active' ? 'badge-green' : s === 'locked' ? 'badge-orange' : 'badge-gray'
}

// ── Tab1 用户管理 ──
const users = ref<AdminUser[]>([])
const roles = ref<AdminRole[]>([])
const regions = ref<AdminRegion[]>([])
const userTotal = ref(0)
const userPage = ref(1)
const userTotalPages = ref(1)
const userQuery = reactive({ keyword: '', status: '' })
const userDialog = ref(false)
const userForm = reactive({
  id: null as number | null, username: '', displayName: '', password: '',
  roleIds: [] as number[], orgCode: '', phone: '', email: '', status: 'active', regions: [] as string[],
})

function roleNames(ids?: number[]): string {
  return (ids || []).map((id) => roles.value.find((r) => r.id === id)?.roleName || String(id)).join(' / ') || '-'
}

async function loadUsers(page: number) {
  try {
    const res = await getAdminUsers({ pageNum: page, pageSize: 10, ...userQuery })
    users.value = res.records || []
    userTotal.value = res.total || 0
    userPage.value = page
    userTotalPages.value = Math.max(1, Math.ceil(userTotal.value / 10))
  } catch {
    users.value = []
  }
}

async function loadOptions() {
  try {
    const [r, rg] = await Promise.all([getAdminRoles(), getAdminRegions()])
    roles.value = r || []
    regions.value = rg || []
  } catch {
    roles.value = []
    regions.value = []
  }
}

function resetUserForm() {
  Object.assign(userForm, {
    id: null, username: '', displayName: '', password: '',
    roleIds: [], orgCode: '', phone: '', email: '', status: 'active', regions: [],
  })
}

function openUserCreate() {
  resetUserForm()
  userDialog.value = true
}

async function openUserEdit(u: AdminUser) {
  try {
    const [detail, userRegions] = await Promise.all([getAdminUser(u.id), getAdminUserRegions(u.id)])
    Object.assign(userForm, {
      id: u.id,
      username: detail.username,
      displayName: detail.displayName,
      password: '',
      roleIds: [...(detail.roleIds || [])],
      orgCode: detail.orgCode || '',
      phone: detail.phone || '',
      email: detail.email || '',
      status: detail.status || 'active',
      regions: [...(userRegions || [])],
    })
    userDialog.value = true
  } catch {
    toast('加载用户详情失败', 'error')
  }
}

async function submitUser() {
  if (!userForm.username || !userForm.displayName) {
    toast('用户名/显示名必填', 'error')
    return
  }
  if (userForm.roleIds.length === 0) {
    toast('请至少选择一个角色', 'error')
    return
  }
  if (userForm.regions.length === 0) {
    toast('请至少授权一个区域', 'error')
    return
  }
  try {
    const payload: Record<string, unknown> = {
      username: userForm.username,
      displayName: userForm.displayName,
      roleIds: userForm.roleIds,
      orgCode: userForm.orgCode,
      phone: userForm.phone,
      email: userForm.email,
      status: userForm.status,
      regions: userForm.regions,
    }
    if (userForm.id) {
      payload.id = userForm.id
      await updateAdminUser(payload)
      toast('用户已更新')
    } else {
      if (!userForm.password || userForm.password.length < 6) {
        toast('初始密码不少于 6 位', 'error')
        return
      }
      payload.password = userForm.password
      await createAdminUser(payload)
      toast('用户已创建')
    }
    userDialog.value = false
    await loadUsers(userPage.value)
  } catch (e) {
    toast((e as Error).message || '保存失败', 'error')
  }
}

function onResetPassword(u: AdminUser) {
  const pwd = window.prompt(`为用户「${u.username}」设置新密码（不少于 6 位）`)
  if (!pwd) return
  if (pwd.length < 6) {
    toast('密码长度不能少于 6 位', 'error')
    return
  }
  void resetAdminUserPassword(u.id, pwd).then(() => toast('密码已重置'))
}

function onDeleteUser(u: AdminUser) {
  if (!window.confirm(`确定删除用户「${u.username}」吗？`)) return
  void deleteAdminUser(u.id).then(() => {
    toast('用户已删除')
    void loadUsers(userPage.value)
  })
}

// ── Tab2 角色权限 ──
const roleQuery = reactive({ keyword: '' })
const roleDialog = ref(false)
const roleForm = reactive({ id: null as number | null, roleCode: '', roleName: '', description: '', status: 'active' })

async function loadRoles() {
  try {
    roles.value = (await getAdminRoles(roleQuery)) || []
  } catch {
    roles.value = []
  }
}

function resetRoleForm() {
  Object.assign(roleForm, { id: null, roleCode: '', roleName: '', description: '', status: 'active' })
}
function openRoleCreate() {
  resetRoleForm()
  roleDialog.value = true
}
function openRoleEdit(r: AdminRole) {
  Object.assign(roleForm, { id: r.id, roleCode: r.roleCode, roleName: r.roleName, description: r.description || '', status: r.status })
  roleDialog.value = true
}

async function submitRole() {
  if (!roleForm.roleCode || !roleForm.roleName) {
    toast('角色编码/名称必填', 'error')
    return
  }
  try {
    const payload: Record<string, unknown> = { roleCode: roleForm.roleCode, roleName: roleForm.roleName, description: roleForm.description, status: roleForm.status }
    if (roleForm.id) {
      payload.id = roleForm.id
      await updateAdminRole(payload)
      toast('角色已更新')
    } else {
      await createAdminRole(payload)
      toast('角色已创建')
    }
    roleDialog.value = false
    await loadRoles()
  } catch (e) {
    toast((e as Error).message || '保存失败', 'error')
  }
}

function onDeleteRole(r: AdminRole) {
  if (!window.confirm(`确定删除角色「${r.roleName}」吗？`)) return
  void deleteAdminRole(r.id).then(() => {
    toast('角色已删除')
    void loadRoles()
  })
}

// ── 权限分配 ──
const perms = ref<AdminPermission[]>([])
const permDialog = ref(false)
const permTarget = ref<AdminRole | null>(null)
const permChecked = ref<number[]>([])
const permGroups = computed(() => {
  const types = ['menu', 'api', 'data']
  return types.map((t) => ({ type: t, items: perms.value.filter((p) => p.resourceType === t) }))
})
function permTypeLabel(t: string): string {
  return t === 'menu' ? '菜单权限' : t === 'api' ? '接口权限' : '数据权限'
}
function permTypeClass(t: string): string {
  return t === 'menu' ? 'badge-blue' : t === 'api' ? 'badge-purple' : 'badge-orange'
}

async function loadPerms() {
  try {
    perms.value = (await getAdminPermissions(permQuery)) || []
  } catch {
    perms.value = []
  }
}

async function openPermAssign(r: AdminRole) {
  permTarget.value = r
  try {
    permChecked.value = (await getAdminRolePermissions(r.id)) || []
  } catch {
    permChecked.value = []
  }
  permDialog.value = true
}

async function savePerms() {
  if (!permTarget.value) return
  try {
    await saveAdminRolePermissions(permTarget.value.id, permChecked.value)
    permDialog.value = false
    toast(`「${permTarget.value.roleName}」权限已保存（${permChecked.value.length} 项）`)
  } catch (e) {
    toast((e as Error).message || '保存失败', 'error')
  }
}

// ── Tab3 权限管理 ──
const permQuery = reactive({ keyword: '', resourceType: '' })
const permFormDialog = ref(false)
const permForm = reactive({ id: null as number | null, permCode: '', permName: '', resourceType: 'menu', resourcePattern: '', status: 'active' })

function resetPermForm() {
  Object.assign(permForm, { id: null, permCode: '', permName: '', resourceType: 'menu', resourcePattern: '', status: 'active' })
}
function openPermCreate() {
  resetPermForm()
  permFormDialog.value = true
}
function openPermEdit(p: AdminPermission) {
  Object.assign(permForm, {
    id: p.id, permCode: p.permCode, permName: p.permName,
    resourceType: p.resourceType, resourcePattern: p.resourcePattern || '', status: p.status,
  })
  permFormDialog.value = true
}

async function submitPerm() {
  if (!permForm.permCode || !permForm.permName) {
    toast('权限编码/名称必填', 'error')
    return
  }
  try {
    const payload: Record<string, unknown> = {
      permCode: permForm.permCode, permName: permForm.permName,
      resourceType: permForm.resourceType, resourcePattern: permForm.resourcePattern, status: permForm.status,
    }
    if (permForm.id) {
      payload.id = permForm.id
      await updateAdminPermission(payload)
      toast('权限已更新')
    } else {
      await createAdminPermission(payload)
      toast('权限已创建')
    }
    permFormDialog.value = false
    await loadPerms()
  } catch (e) {
    toast((e as Error).message || '保存失败', 'error')
  }
}

function onDeletePerm(p: AdminPermission) {
  if (!window.confirm(`确定删除权限「${p.permName}」吗？`)) return
  void deleteAdminPermission(p.id).then(() => {
    toast('权限已删除')
    void loadPerms()
  })
}

// ── Tab4 区域管理 ──
const regionQuery = reactive({ keyword: '' })
const regionDialog = ref(false)
const regionForm = reactive({
  id: null as number | null, regionCode: '', regionName: '', marketSupport: [] as string[],
  exchangeChannel: 'both', settlementPeriod: 'natural_month', status: 'active',
})
const regionMarketText = ref('')

function resetRegionForm() {
  Object.assign(regionForm, {
    id: null, regionCode: '', regionName: '', marketSupport: [],
    exchangeChannel: 'both', settlementPeriod: 'natural_month', status: 'active',
  })
  regionMarketText.value = ''
}

async function loadRegions() {
  try {
    regions.value = (await getAdminRegions(regionQuery)) || []
  } catch {
    regions.value = []
  }
}

function openRegionCreate() {
  resetRegionForm()
  regionDialog.value = true
}
function openRegionEdit(rg: AdminRegion) {
  Object.assign(regionForm, {
    id: rg.id, regionCode: rg.regionCode, regionName: rg.regionName,
    marketSupport: [...(rg.marketSupport || [])],
    exchangeChannel: rg.exchangeChannel || 'both',
    settlementPeriod: rg.settlementPeriod || 'natural_month',
    status: rg.status,
  })
  regionMarketText.value = arrayText(rg.marketSupport)
  regionDialog.value = true
}

async function submitRegion() {
  if (!regionForm.regionCode || !regionForm.regionName) {
    toast('区域编码/名称必填', 'error')
    return
  }
  try {
    const market = regionMarketText.value.split(/[,，]/).map((s) => s.trim()).filter(Boolean)
    const payload: Record<string, unknown> = {
      regionCode: regionForm.regionCode, regionName: regionForm.regionName, marketSupport: market,
      exchangeChannel: regionForm.exchangeChannel, settlementPeriod: regionForm.settlementPeriod,
      status: regionForm.status,
    }
    if (regionForm.id) {
      payload.id = regionForm.id
      await updateAdminRegion(payload)
      toast('区域已更新')
    } else {
      await createAdminRegion(payload)
      toast('区域已创建')
    }
    regionDialog.value = false
    await loadRegions()
  } catch (e) {
    toast((e as Error).message || '保存失败', 'error')
  }
}

function onDeleteRegion(rg: AdminRegion) {
  if (!window.confirm(`确定删除区域「${rg.regionName}」吗？`)) return
  void deleteAdminRegion(rg.id).then(() => {
    toast('区域已删除')
    void loadRegions()
  })
}

// ── Tab5 审计日志 ──
const logs = ref<AuditLogItem[]>([])
const logTotal = ref(0)
const logPage = ref(1)
const logTotalPages = ref(1)
const logQuery = reactive({ username: '', action: '', regionCode: '', result: '' })

async function loadLogs(page: number) {
  try {
    const res = await getAuditLogs({ pageNum: page, pageSize: 10, ...logQuery })
    logs.value = res.records || []
    logTotal.value = res.total || 0
    logPage.value = page
    logTotalPages.value = Math.max(1, Math.ceil(logTotal.value / 10))
  } catch {
    logs.value = []
  }
}

// ── Tab6 系统配置 ──
const configs = [
  { key: 'exchange.channel', desc: '交易中心对接通道（评审决议①）', value: 'both', enum: 'rest / sftp / both' },
  { key: 'settlement.periodMode', desc: '结算周期口径（评审决议③）', value: region.currentRegion === 'CN-33' ? 'trading_month' : 'natural_month', enum: 'natural_month / trading_month' },
  { key: 'region.mode', desc: '多省模式（评审决议⑤）', value: 'multi', enum: 'single / multi' },
  { key: 'optimize.solver', desc: '联合优化求解器（HiGHS 默认，Gurobi 兜底）', value: 'HiGHS', enum: 'HiGHS / SCIP / Gurobi' },
]

onMounted(() => {
  loadUsers(1)
  loadOptions()
})
</script>
