<script setup lang="ts">
import { onMounted, ref } from "vue";
import { apiGetList } from "../api";

interface ListItem {
    id: string;
    postName: string;
}

const props = defineProps({
    initOptions: { type: Array, default: () => [] },
    enable: {
        type: String,
        default: "",
    },
});

watch(
    () => props.initOptions,
    (val) => {
        options.value = [...val, ...options.value];
    }
);
const options = ref<any[]>([]);
const loading = ref(false);

onMounted(() => {
    remoteMethod("11");
});

const remoteMethod = (query: string) => {
    if (query) {
        loading.value = true;
        setTimeout(async () => {
            loading.value = false;
            const { records } = await apiGetList({
                current: 1,
                size: 100,
                name: "",
            });
            options.value = records;
        }, 200);
    } else {
        options.value = [];
    }
};
</script>

<template>
    <el-select
        :="$attrs"
        filterable
        remote
        value-key="id"
        reserve-keyword
        placeholder="请搜索后选择岗位"
        :loading="loading"
    >
        <el-option
            v-for="item in options"
            :key="item.id"
            :label="item.name"
            :value="item.id"
        />
    </el-select>
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { defineComponent, watch } from "vue";
import { Query } from "@vicons/carbon";
export default defineComponent({
    title: "岗位选择",
    name: "PostSelect",
});
</script>
