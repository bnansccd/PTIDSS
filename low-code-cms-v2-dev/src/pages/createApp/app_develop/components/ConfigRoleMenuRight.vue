<script setup lang="ts">
import { reactive, ref, watch, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import { apiPostMenu, apiGetDetails } from "./api";
import { apiGetListTree } from "@/pages/auth_manage/menu_manage/api";
import { rules } from "./rules";

const options = ref<any>([]);

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    menuIds: [],
    checkIds: "",
});

onBeforeMount(async () => {
    const data = await apiGetDetails(props.id);
    ajaxParams.menuIds = data.menuIds;
    options.value = await apiGetListTree();
});

watch(
    () => props.id,
    async (newVal: string) => {
        const data = await apiGetDetails(props.id);
        ajaxParams.menuIds = data.menuIds;
        options.value = await apiGetListTree();
    }
);

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
async function submit(refElForm: FormInstance | null) {
    const bool = await apiPostMenu(ajaxParams, props.id);
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
</script>

<template>
    <div class="config-role-menu">
        <div class="top-tool-box">
            <!-- <div style="margin-right: 16px">菜单权限:</div> -->

            <el-checkbox
                v-model="checked1"
                @change="onChange"
                label="展开/折叠"
                size="large"
            />
            <el-checkbox
                v-model="checked2"
                @change="onChange2"
                label="全选/全不选"
                size="large"
            />
            <el-checkbox v-model="checked3" label="父子联动" size="large" />
            <el-button
                style="margin-left: 16px"
                type="primary"
                @click="submit(refElForm)"
                >保存</el-button
            >
        </div>
        <div class="tree-box">
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
                :props="{
                    label: 'menuName',
                }"
                @check="onCheck"
            />
        </div>
    </div>
</template>

<style lang="scss" scoped>
.config-role-menu {
    height: calc(100vh - 200px);
    width: calc(100% - 40px);
    padding: 0 20px;
}
.top-tool-box {
    width: 100%;
    display: flex;
    align-items: center;

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
    height: calc(100vh - 250px);
    overflow-y: auto;
    :deep(.el-tree) {
        color: var(--sys-theme-form-label-color);
    }
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "配置角色菜单",
    name: "ConfigRoleMenu",
});
</script>
