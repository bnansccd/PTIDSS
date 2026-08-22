<script setup lang="ts">
import $api from "@/api/Axios";
import { Search } from "@element-plus/icons-vue";
import { ElTree } from "element-plus";
import type { DepartParams, Response } from "@/types/index";
interface Tree {
    id: number;
    label: string;
    children?: Tree[];
}

const emit = defineEmits(["change"]);
const options = ref([]);

const props = defineProps({
    type: { type: String, default: "select" },
    id: { type: String, default: "" },
});
const loading = ref(false);
const filterText = ref("");
const treeRef = ref<InstanceType<typeof ElTree>>();

const defaultProps = {
    children: "children",
    label: "departName",
};

// watch(filterText, (val) => {
//     treeRef.value!.filter(val);
// });

const onQueryChanged = (query: string) => {
    treeRef.value!.filter(query);
};

const filterNode = (value: string, data: DepartParams) => {
    if (!value) return true;
    return data.departName.includes(value);
};

const treeData = ref<Array<DepartParams>>([]);
const fetchDictData = async () => {
    loading.value = true;
    const response: Response = await $api.get(
        `/system/api/web/v1/sysDepart/tree`
    );
    loading.value = false;
    const { code, data } = response;
    if (code === 200) {
        treeData.value = data;
        treeData.value = treeData.value.map((item, index) => {
            const colorIndex = index % 3;

            return Object.assign({ color: colorArr[colorIndex] }, item);
        });
        setTreeNodeColor(
            treeData.value,
            null,
            (node: DepartParams, color: string) => {
                node.color = color;
            }
        );
        console.log(treeData.value);
    }
};

const colorArr = ["#108CFB", "#03BC8D", "#FEB903"];

const setTreeNodeColor = (
    tree: Array<DepartParams>,
    color: string | null | undefined,
    callback: {
        (node: any, color: any): void;
        (arg0: DepartParams, arg1: any): void;
    }
) => {
    if (!tree || !Array.isArray(tree)) {
        return;
    }

    tree.forEach((node, index) => {
        callback(node, color ? color : node.color);
        if (node.children && node.children.length) {
            setTreeNodeColor(node.children, node.color, callback);
        }
    });
};

fetchDictData();

const onCurrentChange = (data: any, node: any) => {
    console.log(data.id);
    emit("change", data.id);
};

const leftHeight = computed(() => {
    console.log(window.innerHeight, "window.innerHeight");
    const height = window.innerHeight - 280;
    return height;
});
</script>

<template>
    <div class="depart-select" v-loading="loading">
        <el-input
            v-model="filterText"
            class="w-50 m-2 search-input"
            placeholder="请输入关键字过滤"
            :suffix-icon="Search"
            @input="onQueryChanged"
        />

        <div class="scroll-left-box">
            <el-scrollbar max-height="700px">
                <el-tree-v2
                    ref="treeRef"
                    class="filter-tree"
                    :data="treeData"
                    node-key="id"
                    :props="{
                        label: 'departName',
                        value: 'id',
                        children: 'children',
                    }"
                    :height="leftHeight"
                    style="width: 100%"
                    default-expand-all
                    :filter-method="filterNode"
                    @current-change="onCurrentChange"
                >
                    <template #default="{ node, data }">
                        <div class="tree-node">
                            <svg
                                t="1680770643370"
                                class="icon"
                                viewBox="0 0 1024 1024"
                                version="1.1"
                                xmlns="http://www.w3.org/2000/svg"
                                p-id="20726"
                                width="16"
                                height="16"
                                :fill="data.color"
                                v-if="data.children.length"
                            >
                                <path
                                    d="M901.333333 725.333333a5.333333 5.333333 0 0 1-5.333333-5.333333V480a5.333333 5.333333 0 0 0-5.333333-5.333333h-341.76a5.333333 5.333333 0 0 1-5.333334-5.333334V304a5.333333 5.333333 0 0 1 5.333334-5.333333h69.333333a5.333333 5.333333 0 0 0 5.333333-5.333334V176a5.333333 5.333333 0 0 0-5.333333-5.333333h-224a5.333333 5.333333 0 0 0-5.333333 5.333333v117.333333a5.333333 5.333333 0 0 0 5.333333 5.333334h69.333333a5.333333 5.333333 0 0 1 5.333334 5.333333v165.333333a5.333333 5.333333 0 0 1-5.333334 5.333334H122.666667a5.333333 5.333333 0 0 0-5.333334 5.333333v240a5.333333 5.333333 0 0 1-5.333333 5.333333H42.666667a5.333333 5.333333 0 0 0-5.333334 5.333334v117.333333a5.333333 5.333333 0 0 0 5.333334 5.333333h224a5.333333 5.333333 0 0 0 5.333333-5.333333v-117.333333a5.333333 5.333333 0 0 0-5.333333-5.333334H197.333333a5.333333 5.333333 0 0 1-5.333333-5.333333v-165.333333a5.333333 5.333333 0 0 1 5.333333-5.333334h266.293334a5.333333 5.333333 0 0 1 5.333333 5.333334v165.333333a5.333333 5.333333 0 0 1-5.333333 5.333333H394.666667a5.333333 5.333333 0 0 0-5.333334 5.333334v117.333333a5.333333 5.333333 0 0 0 5.333334 5.333333h224a5.333333 5.333333 0 0 0 5.333333-5.333333v-117.333333a5.333333 5.333333 0 0 0-5.333333-5.333334h-69.76a5.333333 5.333333 0 0 1-5.333334-5.333333v-165.333333a5.333333 5.333333 0 0 1 5.333334-5.333334H816a5.333333 5.333333 0 0 1 5.333333 5.333334v165.333333a5.333333 5.333333 0 0 1-5.333333 5.333333h-69.333333a5.333333 5.333333 0 0 0-5.333334 5.333334v117.333333a5.333333 5.333333 0 0 0 5.333334 5.333333h224a5.333333 5.333333 0 0 0 5.333333-5.333333v-117.333333a5.333333 5.333333 0 0 0-5.333333-5.333334z"
                                    p-id="20727"
                                ></path>
                            </svg>
                            <svg
                                t="1680771250757"
                                class="icon"
                                viewBox="0 0 1024 1024"
                                version="1.1"
                                xmlns="http://www.w3.org/2000/svg"
                                p-id="23581"
                                width="12"
                                height="12"
                                :fill="data.color"
                                v-else
                            >
                                <path
                                    d="M332.48 500.928a25.6 25.6 0 1 0 0-51.2H192.384v-184.96a115.2 115.2 0 0 0 89.6-112.128c0-63.488-51.712-115.2-115.2-115.2s-115.2 51.712-115.2 115.2a115.2 115.2 0 0 0 89.6 112.128v696.192a25.6 25.6 0 1 0 51.2 0V819.84c2.304 0.192 4.48 0.512 6.912 0.512h133.184a25.6 25.6 0 1 0 0-51.2H199.296c-3.456 0-5.504-0.448-6.08-0.256a29.184 29.184 0 0 1-0.896-8.576V500.864h140.16zM921.216 379.264h-486.4a32 32 0 0 0-32 32v128a32 32 0 0 0 32 32h486.4a32 32 0 0 0 32-32v-128a32 32 0 0 0-32-32zM921.216 698.816h-486.4a32 32 0 0 0-32 32v128a32 32 0 0 0 32 32h486.4a32 32 0 0 0 32-32v-128a32 32 0 0 0-32-32z"
                                    p-id="23582"
                                ></path>
                            </svg>
                            <span class="tree-node-label">{{
                                node.label
                            }}</span>
                        </div>
                    </template>
                </el-tree-v2>
            </el-scrollbar>
        </div>
    </div>
</template>

<style scoped lang="scss">
.depart-select {
    width: 300px;
    height: calc(100% - 20px);
    margin-left: 20px;
    background: var(--sys-theme-warp-background);
    border-radius: 12px 12px 12px 12px;
    padding: 0 12px;

    :deep(.el-tree) {
        background: var(--sys-theme-warp-background);
    }
    :deep(.el-tree-node__content:hover) {
        background: var(--sys-theme-table-row-hover);
    }
}

.search-input {
    width: 300px;
    margin-top: 50px;
}
.filter-tree {
    margin-top: 25px;
}

.tree-node {
    display: flex;
    align-items: center;

    .tree-node-label {
        margin-left: 4px;
        font-size: 12px;
        font-family: Source Han Sans CN-Regular, Source Han Sans CN;
        font-weight: 400;
        color: #838c99;
    }
}

.scroll-left-box {
    height: calc(100vh - 220px);
}
</style>

<script lang="ts">
import { computed, defineComponent, toRaw, ref, watch } from "vue";
export default defineComponent({
    title: "局部组织树形选择框",
    name: "DepartSelect",
});
</script>
