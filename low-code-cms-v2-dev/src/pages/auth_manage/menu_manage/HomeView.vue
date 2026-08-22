<script setup lang="ts">
import { ref, onMounted } from "vue";
import AppList from "@/pages/system_manage/app_manage/AppList.vue";
import MenuList from "./MenuList.vue";
import { Expand, Fold } from "@element-plus/icons-vue";

const currentAppId = ref("");

const onChose = (appId: string) => {
    currentAppId.value = appId;
};

// 控制左侧菜单是否展开
const isCollapse = ref(false);

// 切换菜单展开/收起状态
const toggleCollapse = () => {
    isCollapse.value = !isCollapse.value;
};

// 设置页面高度为100%
onMounted(() => {
    document.documentElement.style.height = "100%";
    document.body.style.height = "100%";
});
</script>

<template>
    <WrapLayout>
        <div class="container">
            <!-- 左侧面板 -->
            <div class="left-panel" :class="{ collapsed: isCollapse }">
                <div class="left-content">
                    <AppList @onChose="onChose" />
                </div>
                <!-- 折叠/展开按钮 -->
                <div class="toggle-btn" @click="toggleCollapse">
                    <el-icon>
                        <Expand v-if="isCollapse" />
                        <Fold v-else />
                    </el-icon>
                </div>
            </div>

            <!-- 右侧面板 -->
            <div class="right-panel">
                <!-- 右侧展开按钮 -->
                <div
                    class="right-toggle-btn"
                    @click="toggleCollapse"
                    v-if="isCollapse"
                >
                    <el-icon>
                        <Expand />
                    </el-icon>
                </div>
                <div class="right-content">
                    <MenuList :currentAppId="currentAppId" />
                </div>
            </div>
        </div>
    </WrapLayout>
</template>

<style scoped>
/* 容器样式 */
.container {
    display: flex;
    height: calc(100vh - 160px);
    width: 100vw;
    /* background-color: red; */
    overflow: hidden;
}

/* 左侧面板样式 */
.left-panel {
    width: 300px;
    height: 100%;
    /* background-color: #f0f2f5; */
    border-right: 1px solid #e0e0e0;
    display: flex;
    position: relative;
    transition: width 0.3s ease;
    overflow: hidden;
}

/* 左侧面板收起状态 */
.left-panel.collapsed {
    width: 0;
}

/* 左侧面板内容 */
.left-content {
    /* padding: 20px; */
    flex: 1;
    overflow-y: auto;
}

/* 折叠/展开按钮 */
.toggle-btn {
    position: absolute;
    right: -15px;
    top: 50%;
    transform: translateY(-50%);
    width: 30px;
    height: 60px;
    background-color: #50bd82;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    color: white;
    font-size: 16px;
    z-index: 10;
    transition: background-color 0.3s ease;
}

.toggle-btn:hover {
    background-color: #3c9365;
}

/* 右侧面板样式 */
.right-panel {
    flex: 1;
    height: 100%;
    background-color: #ffffff;
    transition: width 0.3s ease;
    overflow: hidden;
    position: relative;
}

/* 右侧展开按钮 */
.right-toggle-btn {
    position: absolute;
    left: -15px;
    top: 50%;
    transform: translateY(-50%);
    width: 30px;
    height: 60px;
    background-color: #50bd82;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    color: white;
    font-size: 16px;
    z-index: 10;
    transition: background-color 0.3s ease;
}

.right-toggle-btn:hover {
    background-color: #3c9365;
}

/* 右侧面板内容 */
.right-content {
    /* padding: 20px; */
    width: 100%;
    height: 100%;
    overflow-y: auto;
}

/* 确保内容区域高度自适应 */
.left-content,
.right-content {
    height: 100%;
    box-sizing: border-box;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
    title: "左右布局页面",
    name: "LeftRightLayout",
});
</script>
