<script setup lang="ts">
import type { UploadUserFile } from "element-plus";
const props = defineProps({
    fileList: { type: Array<UploadUserFile>, required: true },
});

const srcList = computed(() => {
    return props.fileList.map((item) => item.url) as string[];
});
</script>

<template>
    <div v-for="(item, index) in fileList" :key="index">
        <el-image
            style="width: 100px; height: 100px; margin: 0 10px 10px 0"
            :src="item.url"
            :zoom-rate="1.2"
            :preview-src-list="srcList"
            :initial-index="index"
            fit="cover"
        />
    </div>
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { computed, defineComponent, ref, watch } from "vue";
export default defineComponent({
    title: "全局文件上传预览",
    name: "GlobalUploadPreview",
});
</script>
