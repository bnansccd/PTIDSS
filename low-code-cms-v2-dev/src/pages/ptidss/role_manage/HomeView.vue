<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import $api from "@/api/Axios";

// PTIDSS 角色管理（DDL 10.2 sys_role + sys_role_permission，固定 7 类角色，评审决议⑤）

interface RoleRow {
    id: number;
    roleCode: string;
    roleName: string;
    description?: string;
    status: string;
}

interface PermNode {
    id: number | string;
    label: string;
    children?: PermNode[];
}

const loading = ref(false);
const list = ref<RoleRow[]>([]);
const searchForm = reactive({ keyword: "", status: "" });

const fixedRoleCodes = [
    { value: "trader", label: "交易员" },
    { value: "analyst", label: "分析师" },
    { value: "settlement", label: "结算员" },
    { value: "admin", label: "管理员" },
    { value: "manager", label: "经理" },
    { value: "compliance", label: "合规专员" },
    { value: "mobile", label: "移动审批" },
];

const statusOptions = [
    { value: "active", label: "正常" },
    { value: "disabled", label: "停用" },
];

const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const form = reactive<any>({
    id: null,
    roleCode: "",
    roleName: "",
    description: "",
    status: "active",
});

const rules = {
    roleCode: [{ required: true, message: "请选择角色编码", trigger: "change" }],
    roleName: [{ required: true, message: "请输入角色名称", trigger: "blur" }],
};

// ---- 权限分配 ----
const permDialogVisible = ref(false);
const permTreeRef = ref();
const permTreeData = ref<PermNode[]>([]);
const currentRole = ref<RoleRow | null>(null);
const permSaving = ref(false);

const load = async () => {
    loading.value = true;
    try {
        const res: any = await $api.get(`/admin/roles`, { params: { ...searchForm } });
        if (res.code === 0) {
            list.value = res.data || [];
        }
    } finally {
        loading.value = false;
    }
};

const openCreate = () => {
    isEdit.value = false;
    Object.assign(form, {
        id: null,
        roleCode: "",
        roleName: "",
        description: "",
        status: "active",
    });
    dialogVisible.value = true;
};

const openEdit = (row: RoleRow) => {
    isEdit.value = true;
    Object.assign(form, {
        id: row.id,
        roleCode: row.roleCode,
        roleName: row.roleName,
        description: row.description || "",
        status: row.status,
    });
    dialogVisible.value = true;
};

const submit = async () => {
    await formRef.value.validate();
    const payload: any = {
        roleCode: form.roleCode,
        roleName: form.roleName,
        description: form.description,
        status: form.status,
    };
    if (isEdit.value) {
        payload.id = form.id;
        const res: any = await $api.put(`/admin/roles`, payload);
        if (res.code === 0) ElMessage.success("更新成功");
    } else {
        const res: any = await $api.post(`/admin/roles`, payload);
        if (res.code === 0) ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    load();
};

const remove = (row: RoleRow) => {
    ElMessageBox.confirm(
        `确定删除角色「${row.roleName}」吗？`,
        "提示",
        { type: "warning" }
    ).then(async () => {
        const res: any = await $api.delete(`/admin/roles/${row.id}`);
        if (res.code === 0) {
            ElMessage.success("删除成功");
            load();
        }
    });
};

// ---- 权限分配 ----
const openPermDialog = async (row: RoleRow) => {
    currentRole.value = row;
    permTreeData.value = [];
    permDialogVisible.value = true;
    try {
        const [permRes, assignRes] = await Promise.all([
            $api.get(`/admin/permissions`),
            $api.get(`/admin/roles/${row.id}/permissions`),
        ]);
        if (permRes.code === 0) {
            const perms: any[] = permRes.data || [];
            const groups = [
                { key: "menu", label: "菜单权限" },
                { key: "api", label: "接口权限" },
                { key: "data", label: "数据权限" },
            ];
            permTreeData.value = groups
                .map((g) => ({
                    id: `group-${g.key}`,
                    label: g.label,
                    children: perms
                        .filter((p) => p.resourceType === g.key)
                        .map((p) => ({
                            id: p.id,
                            label: `${p.permName}（${p.permCode}）`,
                        })),
                }))
                .filter((g) => g.children.length > 0);
        }
        if (assignRes.code === 0) {
            setTimeout(() => {
                (assignRes.data || []).forEach((id: number) => {
                    permTreeRef.value?.setChecked(id, true, false);
                });
            }, 50);
        }
    } catch (e) {
        permDialogVisible.value = false;
    }
};

const savePerms = async () => {
    if (!currentRole.value || !permTreeRef.value) return;
    permSaving.value = true;
    try {
        const checked = permTreeRef.value.getCheckedKeys() as (number | string)[];
        const half = permTreeRef.value.getHalfCheckedKeys() as (number | string)[];
        // 后端 Long 序列化为字符串；排除分组占位节点（group-*）
        const permissionIds = [...checked, ...half].filter(
            (k) => typeof k !== "string" || !k.startsWith("group-")
        );
        const res: any = await $api.put(`/admin/roles/${currentRole.value.id}/permissions`, {
            permissionIds,
        });
        if (res.code === 0) {
            ElMessage.success("权限已保存");
            permDialogVisible.value = false;
        }
    } finally {
        permSaving.value = false;
    }
};

onMounted(load);
</script>

<template>
    <div class="page">
        <el-card shadow="never">
            <div class="toolbar">
                <el-form inline @submit.prevent>
                    <el-form-item label="角色名称">
                        <el-input
                            v-model="searchForm.keyword"
                            placeholder="输入角色名称检索"
                            clearable
                            style="width: 180px"
                            @keyup.enter="load"
                        />
                    </el-form-item>
                    <el-form-item label="状态">
                        <el-select v-model="searchForm.status" clearable placeholder="全部" style="width: 120px">
                            <el-option v-for="o in statusOptions" :key="o.value" :value="o.value" :label="o.label" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="load">查询</el-button>
                        <el-button @click="openCreate">新建角色</el-button>
                    </el-form-item>
                </el-form>
            </div>

            <el-table :data="list" v-loading="loading" border stripe>
                <el-table-column prop="roleCode" label="角色编码" width="120" />
                <el-table-column prop="roleName" label="角色名称" width="130" />
                <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
                <el-table-column label="状态" width="90">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
                            {{ row.status === "active" ? "正常" : "停用" }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="220" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
                        <el-button type="success" link @click="openPermDialog(row)">权限分配</el-button>
                        <el-button type="danger" link @click="remove(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新建角色'" width="520px" destroy-on-close>
            <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
                <el-form-item label="角色编码" prop="roleCode">
                    <el-select v-model="form.roleCode" :disabled="isEdit" style="width: 100%" placeholder="固定 7 类角色">
                        <el-option v-for="o in fixedRoleCodes" :key="o.value" :value="o.value" :label="`${o.label}（${o.value}）`" />
                    </el-select>
                </el-form-item>
                <el-form-item label="角色名称" prop="roleName">
                    <el-input v-model="form.roleName" placeholder="角色中文名称" />
                </el-form-item>
                <el-form-item label="描述">
                    <el-input v-model="form.description" type="textarea" :rows="3" placeholder="角色职责说明" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="form.status" style="width: 160px">
                        <el-option v-for="o in statusOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submit">保存</el-button>
            </template>
        </el-dialog>

        <el-dialog
            v-model="permDialogVisible"
            :title="`权限分配：${currentRole?.roleName || ''}`"
            width="560px"
            destroy-on-close
        >
            <el-alert
                title="三级权限体系：menu 菜单 / api 接口 / data 数据。勾选后全量覆盖保存。"
                type="info"
                :closable="false"
                show-icon
                style="margin-bottom: 12px"
            />
            <el-tree
                v-if="permDialogVisible"
                ref="permTreeRef"
                :data="permTreeData"
                node-key="id"
                show-checkbox
                default-expand-all
                :props="{ label: 'label', children: 'children' }"
            />
            <template #footer>
                <el-button @click="permDialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="permSaving" @click="savePerms">保存权限</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<style scoped lang="scss">
.page {
    padding: 16px;

    .toolbar {
        margin-bottom: 12px;
    }
}
</style>
