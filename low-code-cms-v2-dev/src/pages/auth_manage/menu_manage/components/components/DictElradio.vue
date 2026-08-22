<script setup lang="ts">
import { ref, onBeforeMount } from "vue";
import { useDataDict } from "@/stores/modules/dataDict";

interface Dict {
    dictName: string;
    dictType: string;
    id: string;
    parent: string;
    parentId: string;
    parentType: string;
    remarks: string;
}

const options = ref<Array<Dict>>([]);

onBeforeMount(async () => {
    const dataDict = useDataDict();
    options.value = await dataDict.getDictList("MENU_TYPE");
    // 菜单类型（0左侧菜单1顶部菜单2按钮）
});
</script>

<template>
    <el-radio-group :="$attrs">
        <el-radio
            :label="item.dictType"
            v-for="item in options"
            :key="item.id"
            >{{ item.dictName }}</el-radio
        >
    </el-radio-group>
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "",
    name: "",
});
</script>
