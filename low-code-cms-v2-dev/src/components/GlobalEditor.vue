<script setup lang="ts">
import "@wangeditor/editor/dist/css/style.css"; // 引入 css
import { useUserStore } from "@/stores/modules/user";
import { onBeforeUnmount, ref, shallowRef, onMounted } from "vue";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import { baseUrl, baseStaticUrl } from "@/env/index";

type InsertFnType = (url: string, alt: string, href: string) => void;
// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef();

// 内容 HTML
const valueHtml = ref("<p>hello</p>");

// 模拟 ajax 异步获取内容
onMounted(() => {
    setTimeout(() => {
        valueHtml.value = "<p>模拟 Ajax 异步设置内容</p>";
    }, 1500);
});

const userStore = useUserStore();
const toolbarConfig = {};
const editorConfig = {
    placeholder: "请输入信息",

    MENU_CONF: {
        uploadImage: {
            fieldName: "file",
            // server: "http://192.168.88.250:5000/uploads/file",
            server: `${baseUrl}/file/api/web/v1/file`,
            maxFileSize: 1024 * 1024 * 1024, // 1M

            headers: {
                Authorization: userStore.access_token,
            },

            customInsert: (res: any, insertFn: InsertFnType) => {
                console.log(res, insertFn);
                insertFn(
                    // "http://192.168.88.250:5000" + res.data.path,
                    baseStaticUrl + res.data.filePath,
                    "图片",
                    baseStaticUrl + res.data.filePath
                );
            },
        },
    },

    hoverbarKeys: {
        link: { menuKeys: ["editLink", "unLink", "viewLink"] },
        image: {
            menuKeys: [
                "imageWidth30",
                "imageWidth50",
                "imageWidth100",
                // "editImage",
                // "viewImageLink",
                "deleteImage",
            ],
        },
        video: { menuKeys: ["deleteVideo"] },
        pre: { menuKeys: ["codeBlock", "codeSelectLang"] },
        table: {
            menuKeys: [
                "tableHeader",
                "tableFullWidth",
                "insertTableRow",
                "deleteTableRow",
                "insertTableCol",
                "deleteTableCol",
                "deleteTable",
            ],
        },
        divider: { menuKeys: ["deleteDivider"] },
        text: {
            menuKeys: [
                // "headerSelect",
                // "insertLink",
                // "bulletedList",
                // "|",
                // "bold",
                // "through",
                // "color",
                // "bgColor",
                // "clearStyle",
            ],
        },
    },
};

const mode = ref("default");

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
    const editor = editorRef.value;
    if (editor == null) return;
    editor.destroy();
});

const handleCreated = (editor: any) => {
    editorRef.value = editor; // 记录 editor 实例，重要！
};
</script>

<template>
    <div style="border: 1px solid #ccc">
        <Toolbar
            style="border-bottom: 1px solid #ccc"
            :editor="editorRef"
            :defaultConfig="toolbarConfig"
            :mode="mode"
        />
        <Editor
            style="height: 300px; overflow-y: hidden"
            :="$attrs"
            :defaultConfig="editorConfig"
            :mode="mode"
            @onCreated="handleCreated"
        />
    </div>
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "全局富文本编辑",
    name: "GlobalEditor",
});
</script>
