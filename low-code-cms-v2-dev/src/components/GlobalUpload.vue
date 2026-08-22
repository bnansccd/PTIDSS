<script setup lang="ts">
import { baseUrl, baseStaticUrl } from "@/env/index";
import { Plus } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/modules/user";
import { ElMessage, type UploadProps, type UploadUserFile } from "element-plus";

const dialogImageUrl = ref("");
const dialogVisible = ref(false);

const uploadUrl = computed(() => baseUrl + "/file/api/web/v1/file");
const headers = computed(() => {
    const userStore = useUserStore();
    return {
        Authorization: userStore.access_token,
    };
});

const handleRemove: UploadProps["onRemove"] = (uploadFile, uploadFiles) => {
    console.log(uploadFile, uploadFiles);
};

const handlePictureCardPreview: UploadProps["onPreview"] = (uploadFile) => {
    dialogImageUrl.value = uploadFile.url!;
    dialogVisible.value = true;
};
const handleExceed = () => {
    ElMessage({
        type: "warning",
        message: "已达到上传数量限制",
    });
};
</script>

<template>
    <el-upload
        :="$attrs"
        :action="uploadUrl"
        :headers="headers"
        list-type="picture-card"
        :on-preview="handlePictureCardPreview"
        :on-remove="handleRemove"
        :on-exceed="handleExceed"
    >
        <el-icon><Plus /></el-icon>
    </el-upload>

    <el-dialog v-model="dialogVisible">
        <img w-full :src="dialogImageUrl" alt="Preview Image" />
    </el-dialog>
</template>

<style lang="scss">
.el-upload-list__item {
    background: rgb(105, 104, 104) !important;
}
.el-upload-list--picture-card .el-upload-list__item-actions span + span {
    margin-left: 8px !important;
}
</style>

<script lang="ts">
import { computed, defineComponent, ref, watch } from "vue";
export default defineComponent({
    title: "全局文件上传",
    name: "GlobalUpload",
});
</script>
