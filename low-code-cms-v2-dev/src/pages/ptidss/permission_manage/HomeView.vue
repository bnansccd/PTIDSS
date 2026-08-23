<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import $api from "@/api/Axios";

// PTIDSS 权限管理（DDL 10.2 sys_permission，三级权限：menu/api/data，评审决议③）

interface PermRow {
    id: number;
    permCode: string;
    permName: string;
    resourceType: string;
    resourcePattern?: string;
    status: string;
}

const loading = ref(false);
const list = ref<PermRow[]>([]);
const searchForm = reactive({ keyword: "", resourceType: "" });

const typeOptions = [
    { value: "menu", label: "菜单权限" },
    { value: "api", label: "接口权限" },
    { value: "data", label: "数据权限" },
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
    permCode: "",
    permName: "",
    resourceType: "menu",
    resourcePattern: "",
    status: "active",
});

const rules = {
    permCode: [{ required: true, message: "请输入权限编码", trigger: "blur" }],
    permName: [{ required: true, message: "请输入权限名称", trigger: "blur" }],
    resourceType: [{ required: true, message: "请选择资源类型", trigger: "change" }],
};

const load = async () => {
    loading.value = true;
    try {
        const res: any = await $api.get(`/admin/permissions`, { params: { ...searchForm } });
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
        permCode: "",
        permName: "",
        resourceType: "menu",
        resourcePattern: "",
        status: "active",
    });
    dialogVisible.value = true;
};

const openEdit = (row: PermRow) => {
    isEdit.value = true;
    Object.assign(form, {
        id: row.id,
        permCode: row.permCode,
        permName: row.permName,
        resourceType: row.resourceType,
        resourcePattern: row.resourcePattern || "",
        status: row.status,
    });
    dialogVisible.value = true;
};

const submit = async () => {
    await formRef.value.validate();
    const payload: any = {
        permCode: form.permCode,
        permName: form.permName,
        resourceType: form.resourceType,
        resourcePattern: form.resourcePattern,
        status: form.status,
    };
    if (isEdit.value) {
        payload.id = form.id;
        const res: any = await $api.put(`/admin/permissions`, payload);
        if (res.code === 0) ElMessage.success("更新成功");
    } else {
        const res: any = await $api.post(`/admin/permissions`, payload);
        if (res.code === 0) ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    load();
};

const remove = (row: PermRow) => {
    ElMessageBox.confirm(
        `确定删除权限「${row.permName}」吗？删除后角色将失去该权限。`,
        "提示",
        { type: "warning" }
    ).then(async () => {
        const res: any = await $api.delete(`/admin/permissions/${row.id}`);
        if (res.code === 0) {
            ElMessage.success("删除成功");
            load();
        }
    });
};

onMounted(load);
</script>

<template>
    <div class="page">
        <el-card shadow="never">
            <div class="toolbar">
                <el-form inline @submit.prevent>
                    <el-form-item label="关键字">
                        <el-input
                            v-model="searchForm.keyword"
                            placeholder="权限编码/名称"
                            clearable
                            style="width: 180px"
                            @keyup.enter="load"
                        />
                    </el-form-item>
                    <el-form-item label="资源类型">
                        <el-select v-model="searchForm.resourceType" clearable placeholder="全部" style="width: 140px">
                            <el-option v-for="o in typeOptions" :key="o.value" :value="o.value" :label="o.label" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="load">查询</el-button>
                        <el-button @click="openCreate">新建权限</el-button>
                    </el-form-item>
                </el-form>
            </div>

            <el-table :data="list" v-loading="loading" border stripe>
                <el-table-column prop="permCode" label="权限编码" width="160" />
                <el-table-column prop="permName" label="权限名称" width="140" />
                <el-table-column label="资源类型" width="110">
                    <template #default="{ row }">
                        <el-tag :type="row.resourceType === 'menu' ? 'primary' : row.resourceType === 'api' ? 'warning' : 'success'" size="small">
                            {{ typeOptions.find((o) => o.value === row.resourceType)?.label }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="resourcePattern" label="资源匹配模式" min-width="200" show-overflow-tooltip />
                <el-table-column label="状态" width="90">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
                            {{ row.status === "active" ? "正常" : "停用" }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="140" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="openEdit(row as PermRow)">编辑</el-button>
                        <el-button type="danger" link @click="remove(row as PermRow)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑权限' : '新建权限'" width="520px" destroy-on-close>
            <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
                <el-form-item label="权限编码" prop="permCode">
                    <el-input v-model="form.permCode" placeholder="如 menu:market / api:declaration / data:region" :disabled="isEdit" />
                </el-form-item>
                <el-form-item label="权限名称" prop="permName">
                    <el-input v-model="form.permName" placeholder="权限中文名称" />
                </el-form-item>
                <el-form-item label="资源类型" prop="resourceType">
                    <el-select v-model="form.resourceType" style="width: 200px">
                        <el-option v-for="o in typeOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>
                <el-form-item label="资源匹配模式">
                    <el-input v-model="form.resourcePattern" placeholder="如 /market/**（api 类型时填写）" />
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
