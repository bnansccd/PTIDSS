<script setup lang="ts">
import $api from "@/api/Axios";
import type { Response } from "@/types/index";

export interface TenantParams {
    code: string;
    createDepartId: string;
    createDepartName: string;
    createId: string;
    createName: string;
    createTime: string;
    endTime: string;
    id: string;
    modifyDepartId: string;
    modifyDepartName: string;
    modifyId: string;
    modifyName: string;
    modifyTime: string;
    name: string;
    startTime: string;
    status: string;
    tenantId: string;
}

const options = ref<Array<TenantParams>>([]);

const props = defineProps({
    type: { type: String, default: "select" },
    id: { type: String, default: "" },
});

const fetchDictData = async () => {
    const response: Response = await $api.get(
        `/system/api/rpc/v1/sysTenant/list`
    );
    const { code, data } = response;
    if (code === 200) {
        options.value = data;
    }
};

fetchDictData();
const dictVal = computed(() => {
    let res = "";
    options.value.map((item: any) => {
        if (item.dictType == props.id) {
            res = item.dictName;
        }
    });
    return res;
});
</script>

<template>
    <el-select
        :="$attrs"
        class="m-2"
        placeholder="请选择租户"
        v-if="props.type == 'select'"
    >
        <el-option
            v-for="item in options"
            :key="item.id"
            :label="item.name"
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
    title: "全局数据字典选择框",
    name: "GlobalTenantSelect",
});
</script>
