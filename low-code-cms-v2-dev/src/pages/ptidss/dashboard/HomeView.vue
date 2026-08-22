<script setup lang="ts">
import { computed } from "vue";
import { useUserStore } from "@/stores/modules/user";

// PTIDSS 工作台：当前用户会话概览（角色/权限/区域）
const userStore = useUserStore();
const userInfo: any = computed(() => userStore.userInfo || {});

const roleNames = computed(() => {
    const map: Record<string, string> = {
        admin: "管理员",
        trader: "交易员",
        analyst: "分析师",
        settlement: "结算员",
        manager: "经理",
        compliance: "合规专员",
        mobile: "移动审批",
    };
    return (userInfo.value.roles || []).map((r: string) => map[r] || r);
});
</script>

<template>
    <div class="dashboard-page">
        <el-card shadow="never" class="welcome-card">
            <template #header>
                <span class="card-title">欢迎使用电力交易智能辅助决策系统（PTIDSS）</span>
            </template>
            <div class="user-info">
                <div class="info-item">
                    <span class="label">当前用户</span>
                    <span class="value">{{ userInfo.displayName || userInfo.username }}</span>
                </div>
                <div class="info-item">
                    <span class="label">角色</span>
                    <span class="value">
                        <el-tag
                            v-for="r in roleNames"
                            :key="r"
                            size="small"
                            style="margin-right: 6px"
                            >{{ r }}</el-tag
                        >
                    </span>
                </div>
                <div class="info-item">
                    <span class="label">权限（menu:*）</span>
                    <span class="value">
                        <el-tag
                            v-for="p in userInfo.permissions || []"
                            :key="p"
                            size="small"
                            type="info"
                            style="margin-right: 6px; margin-bottom: 4px"
                            >{{ p }}</el-tag
                        >
                    </span>
                </div>
                <div class="info-item">
                    <span class="label">授权区域</span>
                    <span class="value">
                        <el-tag
                            v-for="r in userInfo.regions || []"
                            :key="r"
                            size="small"
                            type="success"
                            style="margin-right: 6px"
                            :effect="r === userStore.currentRegion ? 'dark' : 'plain'"
                            >{{ r }}</el-tag
                        >
                    </span>
                </div>
            </div>
        </el-card>

        <el-card shadow="never" class="notice-card">
            <template #header>
                <span class="card-title">使用说明</span>
            </template>
            <el-alert
                title="本阶段交付范围：组织架构与用户权限管理（区域/用户/角色/权限/审计日志），系统管理菜单需 menu:admin 权限；业务模块（市场行情/交易申报/辅助决策/结算/审批/情报）按权限码规划中。"
                type="info"
                :closable="false"
                show-icon
            />
        </el-card>
    </div>
</template>

<style scoped lang="scss">
.dashboard-page {
    padding: 20px;

    .welcome-card,
    .notice-card {
        margin-bottom: 16px;
    }

    .card-title {
        font-weight: 600;
        color: #1f3b6b;
    }

    .user-info {
        .info-item {
            display: flex;
            align-items: flex-start;
            margin-bottom: 14px;

            .label {
                width: 90px;
                color: #838c99;
                flex-shrink: 0;
                line-height: 24px;
            }
            .value {
                color: #303133;
                line-height: 24px;
            }
        }
    }
}
</style>
