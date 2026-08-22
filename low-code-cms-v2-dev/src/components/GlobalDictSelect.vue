<script setup lang="ts">
import $api from "@/api/Axios";
import type { Response } from "@/types/index";

export interface DictParams {
    ancestors: null | string;
    children: Array<DictParams>;
    createId: string;
    createTime: string;
    delFlag: number;
    dictName: string;
    dictType: string;
    id: string;
    modifyId: string;
    modifyTime: string;
    parent: null | string;
    parentId: string;
    parentType: string;
    remarks: string;
    sort: number;
}

const options = ref<Array<DictParams>>([]);

const props = defineProps({
    parentId: { type: String, required: true },
    type: { type: String, default: "select" },
    id: { type: String, default: "" },
});

const fetchDictData = async (parentId: string) => {
    const response: Response = await $api.get(
        `/system/api/web/v1/sysDict/parentType/${parentId}`
    );
    const { code, data } = response;
    if (code === 200) {
        console.log(data);
        options.value = data;
    }
};

watch(
    () => props.parentId,
    (newValue, oldValue) => {
        console.log(newValue);
        fetchDictData(newValue);
    },
    { deep: true, immediate: true }
);

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
        placeholder="Select"
        v-if="props.type == 'select'"
    >
        <el-option
            v-for="item in options"
            :key="item.dictType"
            :label="item.dictName"
            :value="item.dictType"
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
    name: "GlobalDictSelect",
});
</script>
