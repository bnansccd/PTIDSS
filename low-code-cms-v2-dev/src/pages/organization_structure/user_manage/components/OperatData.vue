<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import { apiPostAdd, apiGetDetails, apiPutEdit } from "./api";
import { rules } from "./rules";
import ParentDepartSelectV2 from "@/pages/organization_structure/depart_manage/global/ParentDepartSelectV2.vue";
import ParentDepartSelect from "@/pages/organization_structure/depart_manage/global/ParentDepartSelect.vue";
import PostSelect from "@/pages/organization_structure/post_manage/global/PostSelect.vue";
import RoleSelect from "@/pages/auth_manage/role_manage/global/RoleSelect.vue";
const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    departId: "",
    departName: "",
    // email: "",
    // headUrl: "",
    phone: "",

    postIds: [],
    roles: [],
    realName: "", // 真实姓名
    // sex: undefined,
    username: "", // 账号
    status: "",
    sysPostVOS: [],
    sysPostVOSOptions: [],
    sysRoleVOS: [],
});

onBeforeMount(async () => {
    if (props.id !== "0") {
        const data = await apiGetDetails(props.id);
        Object.keys(ajaxParams).forEach((item: string) => {
            ajaxParams[item] = data.sysUserVO[item];
        });
        ajaxParams.departId = data.sysDepartVO ? data.sysDepartVO.id : "";
        ajaxParams.departName = data.sysDepartVO
            ? data.sysDepartVO.departName
            : "";
        ajaxParams.roles = data.sysRoleVOS.map((item: any) => item.id);
        ajaxParams.sysPostVOSOptions = data.sysPostVOS;
        ajaxParams.postIds = data.sysPostVOS.map((item: any) => item.id);
    }
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
function submit(refElForm: FormInstance | null) {
    if (!refElForm) return;
    refElForm.validate(async (valid, fields) => {
        if (valid) {
            const params = JSON.parse(JSON.stringify(ajaxParams));
            // params.postIds = ajaxParams.postIds.map((item: any) => {
            //     return item.id;
            // });

            const bool =
                props.id === "0"
                    ? await apiPostAdd(params)
                    : await apiPutEdit(params, props.id);
            bool && emit("close");
            bool && emit("query");
        }
    });
}
</script>

<template>
    <GlobalElDialog :="$attrs" :width="1000" @close="emit('close')">
        <GlobalElForm
            ref="refElForm"
            :label-width="120"
            :model="ajaxParams"
            :rules="rules"
            :inline="true"
        >
            <GlobalElFormItem label="真实姓名" prop="realName">
                <el-input
                    v-model="ajaxParams.realName"
                    placeholder="请输入真实姓名"
                    style="width: 300px"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="账号" prop="username">
                <el-input
                    v-model="ajaxParams.username"
                    placeholder="请输入账号"
                    style="width: 300px"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="联系方式" prop="phone">
                <el-input
                    v-model="ajaxParams.phone"
                    placeholder="请输入联系方式"
                    style="width: 300px"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="组织" prop="departId">
                <!-- <ParentDepartSelect
                    v-model:departId="ajaxParams.departId"
                    v-model:departName="ajaxParams.departName"
                    placeholder="请输入组织"
                    width="300px"
                /> -->
                <ParentDepartSelectV2
                    v-model:departId="ajaxParams.departId"
                    v-model:departName="ajaxParams.departName"
                    width="200px"
                    placeholder="请选择部门"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="岗位" prop="postIds">
                <PostSelect
                    enable="0"
                    v-model="ajaxParams.postIds"
                    :initOptions="ajaxParams.sysPostVOSOptions"
                    style="width: 300px !important"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="角色" prop="roles">
                <RoleSelect
                    v-model="ajaxParams.roles"
                    style="width: 300px !important"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="状态" prop="status">
                <GlobalElSwitch
                    v-model="ajaxParams.status"
                    active-value="0"
                    inactive-value="1"
                    style="width: 300px !important"
                />
            </GlobalElFormItem>

            <!-- <GlobalElFormItem label="性别" prop="sex">
                <el-radio-group v-model="ajaxParams.sex">
                    <el-radio label="1">男</el-radio>
                    <el-radio label="0">女</el-radio>
                </el-radio-group>
            </GlobalElFormItem> -->
        </GlobalElForm>

        <template #footer>
            <el-button type="primary" @click="submit(refElForm)"
                >保存</el-button
            >

            <el-button @click="$emit('close')">取消</el-button>
        </template>
    </GlobalElDialog>
</template>

<style scoped></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "数据操作",
    name: "OperatData",
});
</script>
