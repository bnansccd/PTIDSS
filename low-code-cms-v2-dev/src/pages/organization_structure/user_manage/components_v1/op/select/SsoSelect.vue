<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import $api from "@/api/Axios";

const options: any = ref([]);
const loading = ref<boolean>(false)
onBeforeMount(async () => {
    loading.value = true
    const { code, data } = await $api.get(`/system/api/web/v1/sso/thirdUsers`);
    if (code == 200) {
        options.value = data || [];
    }
    loading.value = false
});
</script>

<template>
    <el-select
        :="$attrs"
        placeholder="请选择第三方账号"
        style="width: 100%"
        filterable
        :loading="loading"
        value-key="idt_user__id"
    >
        <el-option
            v-for="item in options"
            :key="item.idt_user__id"
            :label="item.idt_user__tech_title + '-' + item.idt_user__user_name"
            :value="item"
        />
    </el-select>
</template>

<style scoped></style>
