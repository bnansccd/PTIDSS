<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import $api from "@/api/Axios";

// PTIDSS 用户管理（DDL 10.1 sys_user + sys_user_region 区域授权，评审决议⑤）

interface UserRow {
    id: number;
    username: string;
    displayName: string;
    roleIds: number[];
    orgCode: string;
    phone: string;
    email: string;
    status: string;
    lastLoginAt?: string;
}

interface RoleOption {
    id: number;
    roleCode: string;
    roleName: string;
}

const loading = ref(false);
const list = ref<UserRow[]>([]);
const total = ref(0);
const pageQuery = reactive({ pageNum: 1, pageSize: 10, keyword: "", status: "" });

const roles = ref<RoleOption[]>([]);
const regionOptions = ref<string[]>([]);

const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const form = reactive<any>({
    id: null,
    username: "",
    displayName: "",
    password: "",
    roleIds: [],
    orgCode: "",
    phone: "",
    email: "",
    status: "active",
    regions: [],
});

const rules = {
    username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
    displayName: [{ required: true, message: "请输入显示名", trigger: "blur" }],
    roleIds: [{ required: true, type: "array", min: 1, message: "请选择角色", trigger: "change" }],
    regions: [{ required: true, type: "array", min: 1, message: "请授权至少一个区域", trigger: "change" }],
};

const roleNameMap = (ids: number[]) =>
    (ids || []).map((id) => roles.value.find((r) => r.id === id)?.roleName || id).join(" / ");

const load = async () => {
    loading.value = true;
    try {
        const res: any = await $api.get(`/admin/users`, { params: { ...pageQuery } });
        if (res.code === 0) {
            list.value = res.data.records || [];
            total.value = res.data.total || 0;
        }
    } finally {
        loading.value = false;
    }
};

const loadOptions = async () => {
    const [roleRes, regionRes] = await Promise.all([
        $api.get(`/admin/roles`),
        $api.get(`/admin/regions`),
    ]);
    if (roleRes.code === 0) roles.value = roleRes.data || [];
    if (regionRes.code === 0) {
        regionOptions.value = (regionRes.data || []).map((r: any) => r.regionCode);
    }
};

const openCreate = () => {
    isEdit.value = false;
    Object.assign(form, {
        id: null,
        username: "",
        displayName: "",
        password: "",
        roleIds: [],
        orgCode: "",
        phone: "",
        email: "",
        status: "active",
        regions: [],
    });
    dialogVisible.value = true;
};

const openEdit = async (row: UserRow) => {
    isEdit.value = true;
    const detailRes: any = await $api.get(`/admin/users/${row.id}`);
    const regionRes: any = await $api.get(`/admin/users/${row.id}/regions`);
    const detail = detailRes.code === 0 ? detailRes.data : {};
    Object.assign(form, {
        id: row.id,
        username: detail.username,
        displayName: detail.displayName,
        password: "",
        roleIds: [...(detail.roleIds || [])],
        orgCode: detail.orgCode || "",
        phone: detail.phone || "",
        email: detail.email || "",
        status: detail.status || "active",
        regions: [...(regionRes.code === 0 ? regionRes.data : [])],
    });
    dialogVisible.value = true;
};

const submit = async () => {
    await formRef.value.validate();
    const payload: any = {
        username: form.username,
        displayName: form.displayName,
        roleIds: form.roleIds,
        orgCode: form.orgCode,
        phone: form.phone,
        email: form.email,
        status: form.status,
        regions: form.regions,
    };
    if (isEdit.value) {
        payload.id = form.id;
        const res: any = await $api.put(`/admin/users`, payload);
        if (res.code === 0) ElMessage.success("更新成功");
    } else {
        payload.password = form.password;
        const res: any = await $api.post(`/admin/users`, payload);
        if (res.code === 0) ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    load();
};

const resetPassword = (row: UserRow) => {
    ElMessageBox.prompt(
        `为用户「${row.username}」设置新密码（不少于 6 位）`,
        "重置密码",
        { inputType: "password", inputPattern: /^.{6,}$/, inputErrorMessage: "密码长度不能少于 6 位" }
    ).then(async ({ value }) => {
        const res: any = await $api.put(`/admin/users/${row.id}/password`, {
            password: value,
        });
        if (res.code === 0) ElMessage.success("密码已重置");
    });
};

const remove = (row: UserRow) => {
    ElMessageBox.confirm(
        `确定删除用户「${row.username}」吗？`,
        "提示",
        { type: "warning" }
    ).then(async () => {
        const res: any = await $api.delete(`/admin/users/${row.id}`);
        if (res.code === 0) {
            ElMessage.success("删除成功");
            load();
        }
    });
};

const onPageChange = () => load();

onMounted(() => {
    load();
    loadOptions();
});
</script>

<template>
    <div class="page">
        <el-card shadow="never">
            <div class="toolbar">
                <el-form inline @submit.prevent>
                    <el-form-item label="关键字">
                        <el-input
                            v-model="pageQuery.keyword"
                            placeholder="用户名/显示名"
                            clearable
                            style="width: 180px"
                            @keyup.enter="pageQuery.pageNum = 1; load()"
                        />
                    </el-form-item>
                    <el-form-item label="状态">
                        <el-select v-model="pageQuery.status" clearable placeholder="全部" style="width: 120px">
                            <el-option label="正常" value="active" />
                            <el-option label="锁定" value="locked" />
                            <el-option label="停用" value="disabled" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="pageQuery.pageNum = 1; load()">查询</el-button>
                        <el-button @click="openCreate">新建用户</el-button>
                    </el-form-item>
                </el-form>
            </div>

            <el-table :data="list" v-loading="loading" border stripe>
                <el-table-column prop="username" label="用户名" width="120" />
                <el-table-column prop="displayName" label="显示名" width="130" />
                <el-table-column label="角色" min-width="180">
                    <template #default="{ row }">{{ roleNameMap(row.roleIds) }}</template>
                </el-table-column>
                <el-table-column prop="orgCode" label="组织编码" width="110" />
                <el-table-column prop="phone" label="手机号" width="140" />
                <el-table-column label="状态" width="90">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
                            {{ row.status === "active" ? "正常" : row.status === "locked" ? "锁定" : "停用" }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="lastLoginAt" label="最后登录" width="170" />
                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="openEdit(row as UserRow)">编辑</el-button>
                        <el-button type="warning" link @click="resetPassword(row as UserRow)">重置密码</el-button>
                        <el-button type="danger" link @click="remove(row as UserRow)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <el-pagination
                class="pager"
                v-model:current-page="pageQuery.pageNum"
                v-model:page-size="pageQuery.pageSize"
                :total="total"
                :page-sizes="[10, 20, 50]"
                layout="total, sizes, prev, pager, next"
                @change="onPageChange"
            />
        </el-card>

        <el-dialog
            v-model="dialogVisible"
            :title="isEdit ? '编辑用户' : '新建用户'"
            width="600px"
            destroy-on-close
        >
            <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="form.username" :disabled="isEdit" placeholder="登录账号" />
                </el-form-item>
                <el-form-item label="显示名" prop="displayName">
                    <el-input v-model="form.displayName" placeholder="真实姓名" />
                </el-form-item>
                <el-form-item v-if="!isEdit" label="初始密码" prop="password">
                    <el-input v-model="form.password" type="password" show-password placeholder="不少于 6 位" />
                </el-form-item>
                <el-form-item label="角色" prop="roleIds">
                    <el-select v-model="form.roleIds" multiple style="width: 100%">
                        <el-option
                            v-for="r in roles"
                            :key="r.id"
                            :value="r.id"
                            :label="`${r.roleName}（${r.roleCode}）`"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="授权区域" prop="regions">
                    <el-select v-model="form.regions" multiple style="width: 100%">
                        <el-option v-for="r in regionOptions" :key="r" :value="r" :label="r" />
                    </el-select>
                </el-form-item>
                <el-form-item label="组织编码">
                    <el-input v-model="form.orgCode" placeholder="如 HQ / JS-CN32" />
                </el-form-item>
                <el-form-item label="手机号">
                    <el-input v-model="form.phone" />
                </el-form-item>
                <el-form-item label="邮箱">
                    <el-input v-model="form.email" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="form.status" style="width: 200px">
                        <el-option label="正常" value="active" />
                        <el-option label="锁定" value="locked" />
                        <el-option label="停用" value="disabled" />
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

    .pager {
        margin-top: 14px;
        justify-content: flex-end;
    }
}
</style>
