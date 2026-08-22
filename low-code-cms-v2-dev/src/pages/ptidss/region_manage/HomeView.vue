<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import $api from "@/api/Axios";

// PTIDSS 区域管理（DDL 10.5 sys_region，多省配置化核心：评审决议⑤）

interface Region {
    id: number;
    regionCode: string;
    regionName: string;
    marketSupport: string[];
    exchangeChannel: string;
    settlementPeriod: string;
    status: string;
    launchOrder: number;
    createdAt?: string;
}

const loading = ref(false);
const list = ref<Region[]>([]);
const searchForm = reactive({ keyword: "", status: "" });

const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const form = reactive<Partial<Region>>({
    regionCode: "",
    regionName: "",
    marketSupport: [],
    exchangeChannel: "both",
    settlementPeriod: "natural_month",
    status: "enabled",
    launchOrder: 0,
});

const rules = {
    regionCode: [{ required: true, message: "请输入区域编码", trigger: "blur" }],
    regionName: [{ required: true, message: "请输入区域名称", trigger: "blur" }],
    marketSupport: [
        { required: true, type: "array", min: 1, message: "请选择支持市场类型", trigger: "change" },
    ],
};

const marketOptions = [
    { value: "spot", label: "现货市场" },
    { value: "midlong", label: "中长期市场" },
    { value: "external", label: "外送市场" },
];

const channelOptions = [
    { value: "rest", label: "REST 接口" },
    { value: "sftp", label: "SFTP 文件" },
    { value: "both", label: "双通道" },
];

const periodOptions = [
    { value: "natural_month", label: "自然月" },
    { value: "trading_month", label: "交易月" },
];

const statusOptions = [
    { value: "enabled", label: "启用" },
    { value: "disabled", label: "停用" },
    { value: "pending", label: "待接入" },
];

const load = async () => {
    loading.value = true;
    try {
        const res: any = await $api.get(`/admin/regions`, {
            params: { ...searchForm },
        });
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
        regionCode: "",
        regionName: "",
        marketSupport: [],
        exchangeChannel: "both",
        settlementPeriod: "natural_month",
        status: "enabled",
        launchOrder: 0,
    });
    dialogVisible.value = true;
};

const openEdit = (row: Region) => {
    isEdit.value = true;
    Object.assign(form, {
        id: row.id,
        regionCode: row.regionCode,
        regionName: row.regionName,
        marketSupport: [...(row.marketSupport || [])],
        exchangeChannel: row.exchangeChannel,
        settlementPeriod: row.settlementPeriod,
        status: row.status,
        launchOrder: row.launchOrder,
    });
    dialogVisible.value = true;
};

const submit = async () => {
    await formRef.value.validate();
    const payload: any = { ...form };
    if (isEdit.value) {
        const res: any = await $api.put(`/admin/regions`, payload);
        if (res.code === 0) {
            ElMessage.success("更新成功");
        }
    } else {
        const res: any = await $api.post(`/admin/regions`, payload);
        if (res.code === 0) {
            ElMessage.success("创建成功");
        }
    }
    dialogVisible.value = false;
    load();
};

const remove = (row: Region) => {
    ElMessageBox.confirm(
        `确定删除区域「${row.regionName}」吗？`,
        "提示",
        { type: "warning" }
    ).then(async () => {
        const res: any = await $api.delete(`/admin/regions/${row.id}`);
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
                    <el-form-item label="区域名称">
                        <el-input
                            v-model="searchForm.keyword"
                            placeholder="输入区域名称检索"
                            clearable
                            style="width: 200px"
                            @keyup.enter="load"
                        />
                    </el-form-item>
                    <el-form-item label="状态">
                        <el-select
                            v-model="searchForm.status"
                            clearable
                            placeholder="全部"
                            style="width: 130px"
                        >
                            <el-option label="启用" value="enabled" />
                            <el-option label="停用" value="disabled" />
                            <el-option label="待接入" value="pending" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="load">查询</el-button>
                        <el-button @click="openCreate">新建区域</el-button>
                    </el-form-item>
                </el-form>
            </div>

            <el-table :data="list" v-loading="loading" border stripe>
                <el-table-column prop="regionCode" label="区域编码" width="110" />
                <el-table-column prop="regionName" label="区域名称" width="130" />
                <el-table-column label="支持市场" min-width="220">
                    <template #default="{ row }">
                        <el-tag
                            v-for="m in row.marketSupport || []"
                            :key="m"
                            size="small"
                            style="margin-right: 4px"
                        >
                            {{ marketOptions.find((o) => o.value === m)?.label || m }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="交易中心通道" width="120">
                    <template #default="{ row }">
                        {{ channelOptions.find((o) => o.value === row.exchangeChannel)?.label }}
                    </template>
                </el-table-column>
                <el-table-column label="结算周期" width="100">
                    <template #default="{ row }">
                        {{ periodOptions.find((o) => o.value === row.settlementPeriod)?.label }}
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="90">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 'enabled' ? 'success' : row.status === 'pending' ? 'warning' : 'info'" size="small">
                            {{ statusOptions.find((o) => o.value === row.status)?.label }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="launchOrder" label="接入顺序" width="90" />
                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
                        <el-button type="danger" link @click="remove(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog
            v-model="dialogVisible"
            :title="isEdit ? '编辑区域' : '新建区域'"
            width="560px"
            destroy-on-close
        >
            <el-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-width="120px"
            >
                <el-form-item label="区域编码" prop="regionCode">
                    <el-input v-model="form.regionCode" placeholder="如 CN-32" :disabled="isEdit" />
                </el-form-item>
                <el-form-item label="区域名称" prop="regionName">
                    <el-input v-model="form.regionName" placeholder="如 江苏" />
                </el-form-item>
                <el-form-item label="支持市场" prop="marketSupport">
                    <el-checkbox-group v-model="form.marketSupport">
                        <el-checkbox v-for="o in marketOptions" :key="o.value" :value="o.value">
                            {{ o.label }}
                        </el-checkbox>
                    </el-checkbox-group>
                </el-form-item>
                <el-form-item label="交易中心通道">
                    <el-select v-model="form.exchangeChannel">
                        <el-option v-for="o in channelOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>
                <el-form-item label="结算周期">
                    <el-select v-model="form.settlementPeriod">
                        <el-option v-for="o in periodOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="form.status">
                        <el-option v-for="o in statusOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>
                <el-form-item label="接入顺序">
                    <el-input-number v-model="form.launchOrder" :min="0" :max="999" />
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
