<script setup lang="ts">
import MenuView from "../components/menu/HomeView.vue";
import DefaultHeader from "./header/HomeView.vue";
import DefaultBreadcrumb from "./main/DefaultBreadcrumb.vue";
</script>

<template>
    <el-container>
        <MenuView />

        <el-container class="container-content">
            <el-header>
                <DefaultHeader />
            </el-header>

            <el-main>
                <div>
                    <!-- 面包屑 tab标签页 -->
                    <!-- <DefaultBreadcrumb /> -->
                </div>
                <div class="main-content">
                    <router-view />
                </div>
            </el-main>
        </el-container>
    </el-container>
</template>

<style scoped lang="scss">
.el-container {
    height: 100%;
    overflow: hidden;
    display: flex;
    background-color: var(--sys-theme-background);
    .el-header {
        height: 90px;
        // background: rgb(149, 153, 145);
        padding: 0;
    }
    .container-content {
        height: calc(100% - 0px);
        .el-aside {
            // background-color: rgb(25, 238, 238);
            padding: 0;
            // width: 260px !important;
        }
        .el-main {
            //   background-color: rgb(255, 255, 255);
            padding: 0;
            display: flex;
            flex-direction: column;
            .main-content {
                flex-grow: 1;
            }
        }
    }
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "默认主题",
    name: "DefaultTheme",
});
</script>
