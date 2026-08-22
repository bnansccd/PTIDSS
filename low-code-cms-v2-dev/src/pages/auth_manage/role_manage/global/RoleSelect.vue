<script setup lang="ts">
import { ref, reactive, onBeforeMount, nextTick } from "vue";

import { apiGetList } from "@/pages/auth_manage/role_manage/api";

const options = ref<any>([]);

onBeforeMount(async () => {
    let res = await apiGetList({
        current: 1,
        size: 20,
    });
    options.value = res.records;
});
</script>

<template>
    <el-select
        :="$attrs"
        multiple
        placeholder="请搜索后选择角色"
        style="width: 240px"
    >
        <el-option
            v-for="item in options"
            :key="item.id"
            value-key="id"
            :label="item.roleName"
            :value="item.id"
        />
    </el-select>
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "角色选择",
    name: "RoleSelect",
});
</script>
