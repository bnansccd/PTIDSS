<script setup lang="ts">
import { onMounted, ref } from "vue";
import { apiGetList } from "../website_news/api";

interface ListItem {
    id: string;
    postName: string;
}

const options = ref<ListItem[]>([]);
const loading = ref(false);

onMounted(() => {});

const remoteMethod = (query: string) => {
    if (query) {
        loading.value = true;
        setTimeout(async () => {
            loading.value = false;
            let { records } = await apiGetList({
                current: 1,
                size: 10,
                postName: query,
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
        multiple
        filterable
        remote
        value-key="id"
        reserve-keyword
        placeholder="请搜索后选择岗位"
        :remote-method="remoteMethod"
        :loading="loading"
    >
        <el-option
            v-for="item in options"
            :key="item.id"
            :label="item.postName"
            :value="item"
        />
    </el-select>
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "岗位选择",
    name: "PostSelect",
});
</script>
