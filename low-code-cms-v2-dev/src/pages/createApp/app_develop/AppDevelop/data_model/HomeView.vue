<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import type { TabsPaneContext } from "element-plus";
import EntityModel from "./entity_model/HomeView.vue";
import ERModel from "./er_model/HomeView.vue";
const emits = defineEmits(["toBack"]);
const activeName = ref("st");
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
    <div>
        <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane
                :label="item.tabLabel"
                :name="item.tabName"
                v-for="(item, index) in tabList2"
                :key="index"
            ></el-tab-pane>
        </el-tabs>
        <div class="data-model-main-box" v-if="activeName == 'st'">
            <EntityModel />
        </div>

        <ERModel v-if="activeName == 'er'" />
    </div>
</template>

<style lang="scss" scoped>
.head-box {
    display: flex;
    align-items: center;
}

.data-model-main-box {
    height: calc(100vh - 270px);
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "数据模型",
    name: "DataModel",
});
</script>
