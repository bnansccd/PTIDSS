<script setup lang="ts">
import { ref, onBeforeMount } from "vue";
import { apiGetListTree } from "../api";

const options: any = ref([]);
const value = ref("");
async function getTreeList() {
    options.value = [];
    const records: any = await apiGetListTree();
    options.value = records;
}

onBeforeMount(async () => {
    const data = await getTreeList();
});

const emits = defineEmits(["change"]);

const onChange = (current: any, checkObj: any) => {
    emits("change", checkObj.checkedKeys);
};
</script>

<template>
    <el-tree-select
        ref="areaSelectRef"
        v-model="value"
        :data="options"
        :props="{
            label: 'departName',
            children: 'children',
        }"
        node-key="id"
        multiple
        collapse-tags
        collapse-tags-tooltip
        :render-after-expand="false"
        show-checkbox
        style="width: 240px"
        @check="onChange"
    />
</template>

<style scoped lang="scss">
.el-select-dropdown__item {
    padding: 0 32px 0 0 !important;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "父级组织选择",
    name: "ParentDepartSelect",
});
</script>
