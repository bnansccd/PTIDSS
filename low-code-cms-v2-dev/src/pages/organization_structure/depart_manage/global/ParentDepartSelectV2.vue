<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { usedepartTreeStore } from "@/stores/modules/departTree";

const props = defineProps<{
    departId: string;
    width: string;
    departName: string;
    placeholder?: any;
}>();

const filterText = ref("");

const currentKeys = computed(() => {
    console.log(props.departId, "id");
    if (props.departId) {
        return props.departId;
    } else {
        return "";
    }
});
const emit = defineEmits(["update:departId", "update:departName"]);
const treeRef = ref("");
const options: any = ref([]);
const loading = ref<boolean>(true);

const show = ref<boolean>(false);

const departTreeStore = usedepartTreeStore();
async function getTreeList() {
    loading.value = true;
    const records: any = await departTreeStore.getDepartTree();
    loading.value = false;
    options.value = records;
    if (props.departId) {
        nextTick(() => {
            // treeRef.value?.setExpandedKeys([props.departId]);
            treeRef.value?.setCheckedKeys([props.departId]);
        });
    }
}

onBeforeMount(() => {
    getTreeList();
});

watch(
    () => props.departId,
    (newVal: string) => {
        console.log("id", newVal);
        nextTick(() => {
            treeRef.value?.setCheckedKeys([newVal]);
        });
    },
    {
        deep: true,
        immediate: true,
    }
);

watch(filterText, (val) => {
    treeRef.value!.filter(val);
});

const filterMethod = (query: string, node: TreeNodeData) =>
    node.departName!.includes(query);

const onCheck = (data: TreeNodeData, checked: boolean) => {
    console.log(data, "data");
    if (checked) {
        show.value = false;

        emit("update:departId", data?.id);
        emit("update:departName", data?.departName);
    } else {
        emit("update:departName", "");
        emit("update:departId", "");
    }
};
</script>

<template>
    <div :style="{ width: width }" class="my-input-box">
        <el-popover
            placement="bottom"
            v-model:visible="show"
            :width="360"
            trigger="click"
        >
            <template #reference>
                <div
                    :style="{ width: width }"
                    class="el-input__wrapper-my-text"
                >
                    <div
                        v-if="!props.departName"
                        style="
                            height: 32px;
                            line-height: 32px;
                            margin: 0 10px;
                            color: #909399;
                        "
                    >
                        {{ props.placeholder }}
                    </div>
                    <div
                        v-else
                        style="height: 32px; line-height: 32px; margin: 0 10px"
                    >
                        {{ props.departName }}
                    </div>
                </div>
            </template>
            <keep-alive>
                <div>
                    <div class="my-filter-box">
                        <el-input
                            v-model="filterText"
                            :clearable="true"
                            placeholder="请输入部门名称查询"
                        >
                        </el-input>
                        <el-button
                            style="margin-left: 8px"
                            plain
                            @click="filterText = ''"
                            >重置</el-button
                        >
                    </div>
                    <el-tree-v2
                        ref="treeRef"
                        v-loading="loading"
                        :data="options"
                        check-strictly
                        node-key="id"
                        :props="{
                            label: 'departName',
                            value: 'id',
                            children: 'children',
                        }"
                        :filter-method="filterMethod"
                        :render-after-expand="false"
                        style="width: 100%"
                        show-checkbox
                        @check-change="onCheck"
                    />
                </div>
            </keep-alive>
        </el-popover>
    </div>
</template>

<style scoped lang="scss">
.el-select-dropdown__item {
    padding: 0 32px 0 0 !important;
}

.my-input-box {
    position: relative;
    height: 32px;
}
.my-select-box {
    position: absolute;
    z-index: 999;
    top: 45px;
    left: 0px;
    border: 1px solid #dcdfe6;
    border-radius: 2px;
}

.el-input__wrapper-my-text {
    height: 100%;
    align-items: center;
    background-color: var(--el-input-bg-color, var(--el-fill-color-blank));
    background-image: none;
    border-radius: var(--el-input-border-radius, var(--el-border-radius-base));
    box-shadow: 0 0 0 1px var(--el-input-border-color, var(--el-border-color))
        inset;
    cursor: pointer;
    display: inline-flex;

    transform: translateZ(0);
    transition: var(--el-transition-box-shadow);
}

.my-filter-box {
    display: flex;
}
</style>

<script lang="ts">
import { defineComponent, onBeforeMount, watch, nextTick } from "vue";
import type { TreeNodeData } from "element-plus/es/components/tree-v2/src/types";
export default defineComponent({
    title: "父级组织选择",
    name: "ParentDepartSelectV2",
});
</script>
