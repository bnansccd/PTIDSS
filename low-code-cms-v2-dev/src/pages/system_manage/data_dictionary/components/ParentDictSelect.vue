<script setup lang="ts">
import { ref, onBeforeMount } from "vue";
import { apiGetListTree } from "../api";

const records = ref<any>([]);

onBeforeMount(async () => {
    let data = await apiGetListTree();
    records.value = data;
});
</script>

<template>
    <el-select :="$attrs" filterable value-key="id">
        <el-option
            v-for="(item, index) in records"
            :key="index"
            :label="item.dictName"
            :value="item"
        >
        </el-option>
    </el-select>
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "父级字典选则",
    name: "ParentDictSelect",
});
</script>
