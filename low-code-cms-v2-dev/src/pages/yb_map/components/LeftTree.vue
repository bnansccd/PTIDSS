<script setup lang="ts">
import { onBeforeMount, ref, watch } from "vue";
import MapWrapScroll from "./MapWrapScroll.vue";
import { ElMessage } from "element-plus";

import $api from "@/api/Axios";
import type { Response } from "@/types/index";
const treeRefLeft = ref();
const filterText = ref("");
const value = ref("");
const options = ref([]);
const checkedIds = ref("");

const emit = defineEmits(["change", "chose"]);
// 查询区域列表接口
async function apiGetAreaTree() {
    const response: Response = await $api.get(
        `/basics/api/web/v1/area/tree?type=3`
    );
    const { code, data } = response;
    if (code === 200) {
        return data;
    }
}
function recursion(arr: Array<any>) {
    arr.forEach((item) => {
        if (Array.isArray(item.children)) {
            item.children.unshift({
                code: item.code,
                id: item.id,
                meTotal: item.meTotal,
                total: item.total,
                name: `(本级)${item.name}`,
            });
            recursion(item.children);
        }
    });
}

// recursion(dataArray);

onBeforeMount(async () => {
    // const data = await apiGetAreaTree();
    // recursion(data);
    // options.value = data;

    options.value = [
        { name: "111", children: [{ name: "22", children: [] }] },
        { name: "111", children: [{ name: "2323", children: [] }] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
        { name: "111", children: [] },
    ];
});

watch(filterText, (val) => {
    treeRefLeft.value!.filter(val);
});

const filterNode = (value: string, data: any) => {
    if (!value) return true;
    return data.name.includes(value);
};

const onCheck = (current: any, { checkedKeys }: any) => {
    checkedIds.value = checkedKeys;
    console.log(checkedIds.value);
    emit("change", checkedIds.value);
};

const onNodeClick = (current: any) => {
    console.log(current, "current");
    if (Array.isArray(current.children) && current.children.length == 0) {
        console.log("current 子节点");
        emit("chose", current);
    }
};
</script>

<template>
    <div class="LeftCards">
        <div>
            <el-input
                v-model="filterText"
                :clearable="true"
                placeholder="请输入搜索内容"
            >
            </el-input>
            <el-button @click="filterText = ''">重置</el-button>
        </div>

        <MapWrapScroll class="WrapScroll">
            <el-tree
                style="width: 100%"
                ref="treeRefLeft"
                :props="{
                    label: 'name',
                    children: 'children',
                }"
                :filter-node-method="filterNode"
                :default-expand-all="true"
                :data="options"
                :current-node-key="checkedIds"
                node-key="id"
                @node-click="onNodeClick"
                @check="onCheck"
            >
                <template #default="{ node, data }">
                    <span class="tree-node">
                        <span style="display: flex; align-items: center"
                            ><div
                                class="node-icon"
                                v-if="
                                    Array.isArray(data.children) &&
                                    data.children.length == 0
                                "
                            ></div>
                            {{ data.name || node.label }}</span
                        >
                        &nbsp;
                        <span
                            v-if="
                                Array.isArray(data.children) &&
                                data.children.length != 0
                            "
                        >
                            <span style="color: #41c980"
                                >{{ data.meTotal || 0 }} |
                            </span>
                            <span style="color: #89909d">
                                {{ data.total || 0 }}</span
                            >
                        </span>
                    </span>
                </template>
            </el-tree>
        </MapWrapScroll>
    </div>
</template>

<style lang="scss" scoped>
.LeftCards {
    width: calc(100% - 4px);
    padding: 10px 0 0px 16px;
    // overflow: hidden;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    > div:nth-child(1) {
        display: flex;
        //  padding-right: 16px;
        // justify-content: space-between;
        margin-bottom: 10px;
        font-size: 16px;
        font-family: Source Han Sans CN-Medium, Source Han Sans CN;
        font-weight: 500;
        color: #000000;
        .el-input {
            margin-right: 10px;
        }
    }
    > div:nth-child(2) {
        display: flex;
        padding-right: 16px;
        justify-content: space-between;
        margin-bottom: 10px;
        .el-input {
            margin-right: 10px;
        }
    }

    :deep(.el-tree-node__label) {
        width: 100%;
    }
}

.WrapScroll {
    width: 100%;
    height: calc(100vh - 500px);
}

.tree-node {
    flex: 1;
    display: flex;
    justify-content: space-between;

    .node-icon {
        width: 6px;
        height: 6px;

        background: #41c980;
        border-radius: 50%;
        margin-right: 6px;
    }
}
</style>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
    title: "",
    name: "LeftCards",
});
</script>
