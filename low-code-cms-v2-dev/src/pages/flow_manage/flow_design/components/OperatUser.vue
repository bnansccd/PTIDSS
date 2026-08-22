<script setup lang="ts">
import { reactive, ref, onBeforeMount, computed } from "vue";
import type { FormInstance } from "element-plus";

const props = defineProps<{
    id: string;
    parentId: string;
}>();

const ajaxParams = reactive({
    href: "", // 路由地址
    icon: "",
    // isShow: "",
    menuCode: "", // 权限编码
    menuName: "", // 菜单名称
    parentId: props.parentId,
    menuType: "",
    sort: "",
    // status: "",
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
function submit(refElForm: FormInstance | null) {
    emit("query", ajaxParams.menuName);
}
</script>

<template>
    <GlobalElDialog :="$attrs" @close="emit('close')">
        <GlobalElForm ref="refElForm" label-width="5vw" :model="ajaxParams">
            <GlobalElFormItem label="用户名称" prop="menuName">
                <el-input
                    v-model="ajaxParams.menuName"
                    type="textarea"
                    :autosize="{ minRows: 2, maxRows: 20 }"
                    placeholder="用户名称"
                />
            </GlobalElFormItem>
        </GlobalElForm>

        <template #footer>
            <el-button type="primary" @click="submit(refElForm)"
                >保存</el-button
            >

            <el-button @click="$emit('close')">取消</el-button>
        </template>
    </GlobalElDialog>
</template>

<style lang="scss" scoped>
.icon-list-box {
    width: calc(100% - 100px);
    padding: 0 50px;
    height: 600px;
    overflow-y: scroll;
    display: flex;
    flex-wrap: wrap;

    .icon-box {
        width: 25px;
        height: 25px;
        margin: 10px;
        padding: 8px;
        cursor: pointer;
    }
    .icon-box:hover {
        padding: 8px;
        background: rgb(228, 225, 225);
        border-radius: 6px;
    }
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "数据操作",
    name: "OperatData",
});
</script>
