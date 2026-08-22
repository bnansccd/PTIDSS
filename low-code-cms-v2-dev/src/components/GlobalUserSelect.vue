<script setup lang="ts">
import $api from "@/api/Axios";
import type { Response } from "@/types/index";
const options = ref<any[]>([]);

const allOptions = ref<any[]>([]);
const loading = ref(false);
const props = defineProps({
    type: { type: String, default: "select" },
    userName: { type: String, default: "" },
});

const fetchDictData = async (userName: string) => {
    const response: any = await $api.get(
        `/system/api/web/v1/sysUser/list`,
        {
            params: {
                username: userName,
            },
        }
    );
    const { code, data } = response;
    if (code === 0 || code === 200) {
        console.log(data);
        options.value = data;
        allOptions.value = data;
    }
};

fetchDictData("");
const remoteMethod = async (query: string) => {
    // if (query) {
    //     loading.value = true;
    //     await fetchDictData(query);
    //     loading.value = false;
    // } else {
    //     options.value = [];
    // }
    // loading.value = true;
    // await fetchDictData(query);
    // loading.value = false;

    options.value = allOptions.value.filter((item: any) =>
        item.realName.includes(query)
    );
};

watch(
    () => props.userName,
    (newVal) => {
        fetchDictData(newVal);
    }
);

const dictVal = computed(() => {
    let res = "";
    options.value.map((item: any) => {
        if (item.userName == props.userName) {
            res = item.realName;
        }
    });
    return res;
});
</script>

<template>
    <el-select
        :="$attrs"
        class="m-2"
        filterable
        remote
        :remote-method="remoteMethod"
        :loading="loading"
        placeholder="Select"
        v-if="props.type == 'select'"
    >
        <el-option
            v-for="item in options"
            :key="item.id"
            :label="item.realName"
            :value="item.id"
        />
    </el-select>
    <div v-else>
        <div>{{ dictVal }}</div>
    </div>
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { computed, defineComponent, ref, watch } from "vue";
export default defineComponent({
    title: "全局用户选择框",
    name: "GlobalUserSelect",
});
</script>
