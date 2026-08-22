<script setup lang="ts">
import { reactive, ref, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import $api from "@/api/Axios";

// PTIDSS 机构管理（组织架构树：集团总部 HQ → 省区域 sys_region → 区域用户 sys_user.org_code）
// 参考 low-code-cms-v2-dev (2) organization_structure/depart_manage 树形组织交互，数据对接 ptidss-server /admin/**

interface Region {
    id: number;
    regionCode: string;
    regionName: string;
    marketSupport: string[];
    exchangeChannel: string;
    settlementPeriod: string;
    status: string;
    launchOrder: number;
}

interface UserRow {
    id: number;
    username: string;
    displayName: string;
    roleIds: number[];
    orgCode: string;
    phone: string;
    status: string;
}

const loading = ref(false);
const regions = ref<Region[]>([]);
const users = ref<UserRow[]>([]);

// ── 统计卡 ──
const stats = computed(() => {
    const enabled = regions.value.filter((r) => r.status === "enabled").length;
    const regionUsers = users.value.filter((u) => u.orgCode && u.orgCode !== "HQ").length;
    return {
        orgTotal: regions.value.length + 1, // 含集团总部
        enabled,
        userTotal: users.value.length,
        regionUsers,
    };
});

// ── 组织架构树（根=集团总部；一级=省区域；叶子=区域用户） ──
interface OrgNode {
    key: string;
    name: string;
    code: string;
    type: "hq" | "region" | "user";
    status?: string;
    marketSupport?: string[];
    phone?: string;
    roleText?: string;
    region?: Region;
    user?: UserRow;
    children?: OrgNode[];
}

const orgTree = ref<OrgNode[]>([]);

// org_code 匹配规则：JS-CN32 → 江苏（CN-32）；HQ → 总部
const buildTree = () => {
    const root: OrgNode = {
        key: "HQ",
        name: "集团总部",
        code: "HQ",
        type: "hq",
        children: regions.value.map((r) => {
            const node: OrgNode = {
                key: r.regionCode,
                name: r.regionName,
                code: r.regionCode,
                type: "region",
                status: r.status,
                marketSupport: r.marketSupport || [],
                region: r,
                children: users.value
                    .filter((u) => u.orgCode && u.orgCode.toUpperCase().endsWith(r.regionCode.toUpperCase()))
                    .map((u) => ({
                        key: `U${u.id}`,
                        name: u.displayName,
                        code: u.username,
                        type: "user" as const,
                        status: u.status,
                        phone: u.phone,
                        roleText: (u.roleIds || []).join(","),
                        user: u,
                    })),
            };
            return node;
        }),
    };
    orgTree.value = [root];
};

// 区域用户（org_code=HQ）挂总部节点下
const attachHqUsers = () => {
    const hq = orgTree.value[0];
    if (!hq) return;
    const list = users.value
        .filter((u) => !u.orgCode || u.orgCode === "HQ")
        .map((u) => ({
            key: `U${u.id}`,
            name: u.displayName,
            code: u.username,
            type: "user" as const,
            status: u.status,
            phone: u.phone,
            roleText: (u.roleIds || []).join(","),
            user: u,
        }));
    if (list.length > 0) hq.children = [...(hq.children || []), ...list];
};

// ── 加载 ──
const load = async () => {
    loading.value = true;
    try {
        const [regionRes, userRes] = await Promise.all([
            $api.get(`/admin/regions`),
            $api.get(`/admin/users`, { params: { pageNum: 1, pageSize: 500 } }),
        ]);
        if (regionRes.code === 0) regions.value = regionRes.data || [];
        if (userRes.code === 0) users.value = userRes.data.records || [];
        buildTree();
        attachHqUsers();
    } finally {
        loading.value = false;
    }
};

// ── 区域 CRUD（复用 /admin/regions） ──
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const form = reactive<any>({
    id: null,
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
    marketSupport: [{ required: true, type: "array", min: 1, message: "请选择支持市场", trigger: "change" }],
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

const openCreate = () => {
    isEdit.value = false;
    Object.assign(form, {
        id: null,
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

const openEdit = (node: OrgNode) => {
    const r = node.region;
    if (!r) return;
    isEdit.value = true;
    Object.assign(form, {
        id: r.id,
        regionCode: r.regionCode,
        regionName: r.regionName,
        marketSupport: [...(r.marketSupport || [])],
        exchangeChannel: r.exchangeChannel,
        settlementPeriod: r.settlementPeriod,
        status: r.status,
        launchOrder: r.launchOrder,
    });
    dialogVisible.value = true;
};

const submit = async () => {
    await formRef.value.validate();
    const payload: any = { ...form };
    if (isEdit.value) {
        const res: any = await $api.put(`/admin/regions`, payload);
        if (res.code === 0) ElMessage.success("更新成功");
    } else {
        const res: any = await $api.post(`/admin/regions`, payload);
        if (res.code === 0) ElMessage.success("创建成功");
    }
    dialogVisible.value = false;
    load();
};

const remove = (node: OrgNode) => {
    const r = node.region;
    if (!r) return;
    ElMessageBox.confirm(`确定删除机构「${r.regionName}」吗？`, "提示", { type: "warning" }).then(async () => {
        const res: any = await $api.delete(`/admin/regions/${r.id}`);
        if (res.code === 0) {
            ElMessage.success("删除成功");
            load();
        }
    });
};

const typeTag = (type: string) =>
    type === "hq" ? "danger" : type === "region" ? "primary" : "info";
const typeLabel = (type: string) =>
    type === "hq" ? "集团总部" : type === "region" ? "省级区域" : "用户";

onMounted(load);
</script>

<template>
    <div class="page">
        <!-- 统计卡（对齐原型 wf-stat 大数字样式） -->
        <el-row :gutter="16" class="stat-row">
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">机构总数</div>
                    <div class="stat-value">{{ stats.orgTotal }}</div>
                    <div class="stat-tip">集团总部 + 省级区域</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">启用区域</div>
                    <div class="stat-value">{{ stats.enabled }}</div>
                    <div class="stat-tip">status=enabled</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">用户总数</div>
                    <div class="stat-value">{{ stats.userTotal }}</div>
                    <div class="stat-tip">sys_user 全量</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">区域用户</div>
                    <div class="stat-value">{{ stats.regionUsers }}</div>
                    <div class="stat-tip">挂省区域机构下</div>
                </el-card>
            </el-col>
        </el-row>

        <el-card shadow="never">
            <div class="toolbar">
                <span class="tip-text">组织架构：集团总部 → 省区域 → 区域用户（org_code 匹配）</span>
                <div>
                    <el-button type="primary" @click="openCreate">新建区域机构</el-button>
                    <el-button @click="load">刷新</el-button>
                </div>
            </div>

            <el-table
                :data="orgTree"
                v-loading="loading"
                border
                row-key="key"
                default-expand-all
                :tree-props="{ children: 'children' }"
            >
                <el-table-column prop="name" label="机构名称" min-width="200">
                    <template #default="{ row }">
                        <span class="node-name">{{ row.name }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="code" label="机构编码" width="130">
                    <template #default="{ row }">
                        <span class="mono">{{ row.code }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="类型" width="110">
                    <template #default="{ row }">
                        <el-tag :type="typeTag(row.type)" size="small">{{ typeLabel(row.type) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="支持市场" min-width="180">
                    <template #default="{ row }">
                        <template v-if="row.marketSupport">
                            <el-tag
                                v-for="m in row.marketSupport"
                                :key="m"
                                size="small"
                                style="margin-right: 4px"
                            >
                                {{ marketOptions.find((o) => o.value === m)?.label || m }}
                            </el-tag>
                        </template>
                        <span v-else-if="row.type === 'user'" class="muted">{{ row.phone || '—' }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag
                            v-if="row.status"
                            :type="row.status === 'enabled' || row.status === 'active' ? 'success' : row.status === 'pending' ? 'warning' : 'info'"
                            size="small"
                        >
                            {{ row.status }}
                        </el-tag>
                        <span v-else class="muted">—</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="170" fixed="right">
                    <template #default="{ row }">
                        <el-button v-if="row.type === 'hq'" type="primary" link @click="openCreate">新建区域</el-button>
                        <template v-else-if="row.type === 'region'">
                            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
                            <el-button type="danger" link @click="remove(row)">删除</el-button>
                        </template>
                        <span v-else class="muted">—</span>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑区域机构' : '新建区域机构'" width="560px" destroy-on-close>
            <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
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

    .stat-row {
        margin-bottom: 16px;
    }
    .stat-card {
        .stat-label {
            font-size: 13px;
            color: #86909c;
        }
        .stat-value {
            font-size: 30px;
            font-weight: 700;
            color: #2f6fed;
            line-height: 1.4;
        }
        .stat-tip {
            font-size: 12px;
            color: #a9aeb8;
        }
    }
    .toolbar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .tip-text {
            color: #86909c;
            font-size: 13px;
        }
    }
    .node-name {
        font-weight: 500;
    }
    .mono {
        font-family: Consolas, monospace;
    }
    .muted {
        color: #a9aeb8;
    }
}
</style>
