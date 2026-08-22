<script setup lang="ts">
import $api from "@/api/Axios";
const options = ref<any[]>([]);

const props = defineProps({
    type: { type: String, default: "select" },
    id: { type: String, default: "" },
});

const fetchDictData = async () => {
    // 模板遗留接口（low-code 后端 sysDepart），PTIDSS 未使用；类型按 ApiResponse 解包
    const response: any = await $api.get(`/system/api/web/v1/sysDepart`);
    const { code, data } = response;
    if (code === 0 || code === 200) {
        options.value = data || [];
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
        placeholder="Select"
        size="large"
        v-if="props.type == 'select'"
    >
        <el-option
            v-for="item in options"
            :key="item.id"
            :label="item.departName"
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
    title: "全局组织选择框",
    name: "GlobalDepartSelect",
});
</script>
