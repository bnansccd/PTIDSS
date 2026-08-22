<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "全局表单",
    name: "GlobalElForm",
    methods: {
        validate(isValid: any, invalidFields: any) {
            (this.$refs.elForm as any).validate(isValid, invalidFields);
        },
        validateField(isValid: any, invalidFields: any) {
            (this.$refs.elForm as any).validateField(isValid, invalidFields);
        },
        resetFields() {
            (this.$refs.elForm as any).resetFields();
        },
        scrollToField(prop: any) {
            (this.$refs.elForm as any).scrollToField(prop);
        },
        clearValidate(props: any) {
            (this.$refs.elForm as any).clearValidate(props);
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

<!-- <script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "全局表单",
    name: "GlobalElForm",
});
</script> -->
