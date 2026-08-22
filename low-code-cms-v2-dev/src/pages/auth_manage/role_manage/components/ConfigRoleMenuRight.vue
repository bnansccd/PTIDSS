<script setup lang="ts">
import { reactive, ref, watch, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";
import { ElMessage } from "element-plus";

import { apiPostMenu, apiGetDetails } from "./api";
import { apiGetListTree } from "@/pages/auth_manage/menu_manage/api";
import { rules } from "./rules";

const options = ref<any>([]);

const props = defineProps<{
    id: string;
}>();

const loading = ref(false);
const ajaxParams = reactive({
    menuIds: [],
    checkIds: "",
});

onBeforeMount(async () => {
    loading.value = true;
    console.log(111);
    const data = await apiGetDetails(props.id);
    ajaxParams.menuIds = data;
    options.value = await apiGetListTree();
    loading.value = false;
});

watch(
    () => props.id,
    async (newVal: string) => {
        loading.value = true;
        const data = await apiGetDetails(props.id);
        ajaxParams.menuIds = data;
        options.value = await apiGetListTree();
        loading.value = false;
    }
);

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
const btnLoading = ref(false);
async function submit(refElForm: FormInstance | null) {
    if (!ajaxParams.checkIds) {
        ElMessage({
            message: "无数据变更",
            type: "warning",
            center: true,
            // duration: 0,
        });
        return false;
    }
    btnLoading.value = true;
    const bool = await apiPostMenu(ajaxParams, props.id);
    btnLoading.value = false;
    bool && emit("close");
    bool && emit("query");
}

const onCheck = (current: any, { checkedKeys }: any) => {
    console.log("getCheckedKeys");
    console.log(current);
    console.log(checkedKeys);
    ajaxParams.checkIds = checkedKeys;
};

const checkList = ref([]);
const treeRef = ref();
const checked1 = ref(true);
const checked2 = ref(false);
const checked3 = ref(false);

// 展开/收缩
const onChange = () => {
    console.log(treeRef.value.root.childNodes);
    setTreeNode(treeRef.value.root.childNodes, (node: any) => {
        node.expanded = checked1.value;
    });
};

// 全选/全不选
const onChange2 = () => {
    ajaxParams.checkIds = [];
    setTreeNode(treeRef.value.root.childNodes, (node: any) => {
        node.checked = checked2.value;
        if (checked2.value) {
            ajaxParams.checkIds.push(node.data.id);
        }
    });
};

const setTreeNode = (
    tree: any,
    callback: { (node: any): void; (arg0: any): void }
) => {
    if (!tree || !Array.isArray(tree)) {
        return;
    }

    tree.forEach((node, index) => {
        callback(node);
        if (node.childNodes && node.childNodes.length) {
            setTreeNode(node.childNodes, callback);
        }
    });
};

const onClose = () => {
    emit("close");
};
</script>

<template>
    <div class="config-role-menu">
        <div class="top-tool-box">
            <!-- <div style="margin-right: 16px">菜单权限:</div> -->

            <!-- <el-checkbox
                v-model="checked1"
                @change="onChange"
                label="展开/折叠"
                size="large"
            /> -->
            <el-checkbox
                v-model="checked2"
                @change="onChange2"
                label="全选/全不选"
                size="large"
            />
            <!-- <el-checkbox v-model="checked3" label="父子联动" size="large" /> -->
            <el-dropdown>
                <el-icon color="#5f5f5f" style="cursor: pointer" :size="20"
                    ><Setting
                /></el-icon>
                <template #dropdown>
                    <div class="dropdown-content">
                        <div style="height: 30px">
                            <el-checkbox
                                v-model="checked1"
                                @change="onChange"
                                label="展开/折叠"
                                size="large"
                            />
                        </div>
                        <div>
                            <el-checkbox
                                v-model="checked3"
                                label="父子联动"
                                size="large"
                            />
                        </div>
                    </div>
                </template>
            </el-dropdown>
        </div>
        <WrapScroll class="tree-box">
            <el-tree
                style="height: 300px"
                ref="treeRef"
                :default-checked-keys="ajaxParams.menuIds"
                :current-node-key="ajaxParams.checkIds"
                :data="options"
                multiple
                :check-strictly="!checked3"
                :render-after-expand="false"
                :default-expand-all="true"
                show-checkbox
                node-key="id"
                v-loading="loading"
                :props="{
                    label: 'menuName',
                    disabled: (data, node) => data?.menuType === '4',
                }"
                @check="onCheck"
            >
                <template #default="{ node, data }">
                    <div class="custom-tree-node">
                        <span>{{ node.label }}</span>
                        <el-tag
                            style="margin-left: 8px"
                            type="primary"
                            v-if="data?.menuType === '4'"
                            >应用</el-tag
                        >
                    </div>
                </template>
            </el-tree>
        </WrapScroll>
        <div class="btn-box">
            <el-button @click="onClose">取消</el-button>
            <el-button type="primary" :loading="btnLoading" @click="submit(refElForm)"
                >确定</el-button
            >
        </div>
    </div>
</template>

<style lang="scss" scoped>
.config-role-menu {
    height: calc(100vh - 200px);
    width: calc(100% - 20px);
    padding: 0px 0 20px 20px;
}
.top-tool-box {
    width: calc(100% - 20px);
    padding-right: 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;

    :deep(.el-button--primary) {
        background-color: var(--sys-theme-btn-primary-background) !important;
        --el-button-border-color: var(
            --sys-theme-btn-primary-border-color
        ) !important;
    }
    :deep(.el-button:hover) {
        border: 1px solid var(--sys-theme-btn-primary-hover-border-color) !important;
    }
    :deep(.el-button:focus, .el-button:hover) {
        border: 1px solid var(--sys-theme-btn-primary-hover-border-color) !important;
    }

    :deep(.el-checkbox) {
        color: var(--sys-theme-form-label-color);
    }
}
.tree-box {
    height: calc(100vh - 260px);
    overflow-y: auto;
    :deep(.el-tree) {
        color: var(--sys-theme-form-label-color);
    }
}
.btn-box {
    width: 100%;
    height: 50px;

    // background: #93b6e8;
    display: flex;
    align-items: flex-end;
    justify-content: center;
}
.dropdown-content {
    // background: #ad8282;
    // box-shadow: 0px 2px 6px 1px rgba(0, 0, 0, 0.1);
    border-radius: 8px 8px 8px 8px;
    padding: 0 8px;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "配置角色菜单",
    name: "ConfigRoleMenu",
});
</script>
