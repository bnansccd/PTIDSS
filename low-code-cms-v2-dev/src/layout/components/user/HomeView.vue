<script setup lang="ts">
import { ref, computed } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import { useUserStore } from "@/stores/modules/user";
import { useRouter } from "vue-router";
import img from "../../../assets/avatar.png";

// PTIDSS 用户信息：显示名 + 角色 + 多省区域切换（评审决议⑤）+ 退出
const router = useRouter();
const userStore = useUserStore();

const userInfo: any = computed(() => userStore.userInfo || {});

const displayName = computed(() => {
    const info = userInfo.value;
    return info.displayName || info.username || "未登录";
});

const roleNames = computed(() => {
    const info = userInfo.value;
    const roles: string[] = info.roles || [];
    const map: Record<string, string> = {
        admin: "管理员",
        trader: "交易员",
        analyst: "分析师",
        settlement: "结算员",
        manager: "经理",
        compliance: "合规专员",
        mobile: "移动审批",
    };
    return roles.map((r) => map[r] || r).join(" / ");
});

const regionOptions = computed(() => {
    const info = userInfo.value;
    const regions: string[] = info.regions || [];
    return regions.map((code) => ({ value: code, label: code }));
});

const currentRegion = computed({
    get: () => userStore.currentRegion,
    set: (val: string) => userStore.setRegion(val),
});

const onRegionChange = (val: string) => {
    userStore.setRegion(val);
    ElMessage.success("已切换到区域 " + val + "，后续请求将按该区域数据权限执行");
    // 刷新当前用户信息以同步会话
    userStore.getUserInfo();
};

function logout() {
    ElMessageBox.confirm("确定要退出当前登录的用户吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        center: true,
    }).then(async () => {
        await userStore.logout();
        router.push({ name: "login" });
    });
}
</script>

<template>
    <el-popover
        placement="bottom"
        :width="220"
        trigger="click"
        :show-arrow="false"
        popper-class="UserInfo-popper-class"
    >
        <template #reference>
            <div class="UserInfo">
                <el-avatar :size="38" :src="img" />
                <div style="margin: 0 12px">
                    <div class="user-name">{{ displayName }}</div>
                    <div class="user-role">{{ roleNames }}</div>
                </div>
                <el-icon color="#838c99"><ArrowDown /></el-icon>
            </div>
        </template>

        <div class="UserInfo-region">
            <div class="region-label">会话区域（数据权限）</div>
            <el-select
                v-if="regionOptions.length > 1"
                v-model="currentRegion"
                size="small"
                @change="onRegionChange"
            >
                <el-option
                    v-for="opt in regionOptions"
                    :key="opt.value"
                    :value="opt.value"
                    :label="opt.label"
                />
            </el-select>
            <el-tag v-else size="small" type="info">{{
                currentRegion || "未授权区域"
            }}</el-tag>
        </div>

        <el-divider />

        <div class="UserInfo-item" @click="logout">
            <el-icon color="#F56C6C" :size="25">
                <SwitchButton />
            </el-icon>
            <el-button type="danger" link>退出</el-button>
        </div>
    </el-popover>
</template>

<style scoped lang="scss">
.UserInfo {
    cursor: pointer;
    display: flex;
    margin-right: 5px;
    padding-right: 30px;
    align-items: center;
    > div:nth-of-type(1) {
        margin-right: 5px;
    }
}

.user-name {
    font-size: 12px;
    font-family: Source Han Sans CN-Regular, Source Han Sans CN;
    font-weight: 400;
    color: #838c99;
}

.user-role {
    font-size: 12px;
    font-family: Source Han Sans CN-Medium, Source Han Sans CN;
    font-weight: 500;
    color: var(--sys-theme-form-label-color);
}
</style>
<style lang="scss">
.UserInfo-popper-class {
    .UserInfo-region {
        padding: 4px 8px;

        .region-label {
            font-size: 12px;
            color: #838c99;
            margin-bottom: 8px;
        }

        .el-select {
            width: 100%;
        }
    }
    .UserInfo-item {
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: space-between;
        .el-button {
            font-weight: bold;
        }
    }
    .el-divider {
        margin: 8px 0;
    }
}
</style>
