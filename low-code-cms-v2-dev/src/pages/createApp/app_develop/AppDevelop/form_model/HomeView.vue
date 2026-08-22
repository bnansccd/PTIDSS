<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import type { TabsPaneContext } from "element-plus";

const emits = defineEmits(["toBack"]);
const activeName = ref("first");
const activeName2 = ref("st");
const handleClick = (tab: TabsPaneContext, event: Event) => {
    console.log(tab, event);
};
const opInit = {
    show: false,
    code: "init", // "look" "edit" "add"
    id: "0", //id为0时表示添加
    roleName: "", // 角色名称
};
const opObject = reactive({
    ...opInit,
});

const tabList2 = ref([
    { tabLabel: "实体模型", tabName: "st" },
    { tabLabel: "ER模型", tabName: "er" },
]);
const reset = () => {};
const getList = () => {};
const toBack = () => {
    emits("toBack");
};
</script>

<template>
    <div>表单模型</div>
</template>

<style lang="scss" scoped>
.head-box {
    display: flex;
    align-items: center;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "表单模型",
    name: "FormModel",
});
</script>
