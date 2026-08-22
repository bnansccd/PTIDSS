<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import {
    CirclePlus, //添加
    Delete, // 删除
} from "@element-plus/icons-vue";
import FcDesigner from "@form-create/designer";
</script>

<template>
    <div class="create-from-box">
        <fc-designer ref="designer"> </fc-designer>
    </div>
</template>

<style lang="scss" scoped>
.create-from-box {
    height: calc(100% - 60px);
    overflow: hidden;
    padding: 20px;
    display: flex;
    flex-direction: column;
    background: var(--sys-theme-warp-background);
    border-radius: 12px 12px 12px 12px;
    margin: 0px 20px 0 20px;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "岗位管理",
    name: "PostManage",
});
</script>
