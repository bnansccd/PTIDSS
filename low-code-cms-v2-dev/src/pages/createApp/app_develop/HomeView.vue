<script setup lang="ts">
const show = ref(true);

const onChose = () => {
    show.value = false;
};
</script>

<template>
    <AppManage v-if="show" @on-chose="onChose" />
    <AppDevelop v-else @to-back="show = true" />
</template>

<style lang="scss" scoped>
.role-manage-box {
    width: 100%;
    height: 100%;
    display: flex;
    overflow: hidden;
    .left-box {
        flex: 1;
    }
}

.vxe-table-row {
    background-color: red !important;
}

.tool-bar-box {
    margin: 10px 0;
}
</style>

<script lang="ts">
import { defineComponent, ref } from "vue";

import AppManage from "./AppManage.vue";
import AppDevelop from "./AppDevelop.vue";

export default defineComponent({
    title: "应用开发入口页",
    name: "AppDevelopIndex",
    components: { AppManage, AppDevelop },
});
</script>
