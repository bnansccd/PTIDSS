<script lang="ts">
import { defineComponent } from "vue";

// el-form 实例类型断言（模板 ref 推导为 unknown，G3 类型门禁兼容层）
type ElFormInstance = any;

export default defineComponent({
    title: "全局表单",
    name: "GlobalElForm",
    methods: {
        validate(isValid: any, invalidFields: any) {
            (this.$refs.elForm as ElFormInstance).validate(isValid, invalidFields);
        },
        validateField(isValid: any, invalidFields: any) {
            (this.$refs.elForm as ElFormInstance).validateField(isValid, invalidFields);
        },
        resetFields() {
            (this.$refs.elForm as ElFormInstance).resetFields();
        },
        scrollToField(prop: any) {
            (this.$refs.elForm as ElFormInstance).scrollToField(prop);
        },
        clearValidate(props: any) {
            (this.$refs.elForm as ElFormInstance).clearValidate(props);
        },
    },
});
</script>

<template>
    <el-form :="$attrs" ref="elForm">
        <slot></slot>
    </el-form>
</template>

<style lang="scss">
.el-form {
    .el-select {
        width: 100% !important;
    }
}
</style>
