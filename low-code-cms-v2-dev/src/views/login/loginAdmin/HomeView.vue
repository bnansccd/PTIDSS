<script setup lang="ts">
import { reactive, ref, onBeforeMount, computed } from "vue";
import { rules } from "./rules";
import type { FormInstance } from "element-plus";
import { User, Lock, ChatRound } from "@element-plus/icons-vue";
// import router from "@/router/index";
import $api from "@/api/Axios";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
import type { MenuResponseParams, Response } from "@/types/index";
import { apiDomain } from "./api";
import { baseStaticUrl } from "@/env/index";
const ApibaseURL = "/auth/api/web/v1";

const router = useRouter();
const loading = ref<boolean>(true);
const btnLoading = ref<boolean>(false);
const recordInfo = ref("");
const recordInfoUrl = ref("");
const userStore = useUserStore();
const ajaxParams = reactive({
    username: "",
    password: "",
    appId: import.meta.env.VITE_APP_ID,
    code: "",
    isSave: false,
});

const refElForm = ref();

const isAutoLogin = ref(true); // 是否自动登录

onBeforeMount(async () => {
    getQueryVariable();
    // const data = await apiDomain();
    // if (data && data.recordInfo) {
    //     window.sessionStorage.setItem("recordInfo", data.recordInfo);
    //     recordInfo.value = data.recordInfo;
    //     recordInfoUrl.value = data.recordInfoUrl;
    // }
});

const toRecordInfoUrl = () => {
    if (recordInfoUrl.value) {
        window.open(recordInfoUrl.value);
    }
};
const toRegister = () => {
    router.push({ path: "/register" });
};

const initSystem = async () => {
    // 递归：只遍历数组第一项，查找这条链上最后一个 menuType ===1 的菜单
    const findLastValidMenuInFirstBranch = (menuList: any[]) => {
        if (!menuList || menuList.length === 0) return null;

        // 永远只取数组第一项
        const current = menuList[0];
        let lastValidMenu = null;

        // 如果当前是页面，记录下来
        if (current?.menuType === "1" && current.isShow == '0') {
            lastValidMenu = current;
        }

        // 继续递归子菜单第一项（深度优先）
        const childMenu = findLastValidMenuInFirstBranch(current?.children);
        if (childMenu) {
            lastValidMenu = childMenu;
        }

        return lastValidMenu;
    };

    // 1. 用户信息 & 菜单处理
    const respon = await userStore.getUserInfo();
    const { code, data } = respon;
    if (code == 200) {
        const sysMenuVOS = data.sysMenuVOS[0]?.children || [];
        const targetMenu = findLastValidMenuInFirstBranch(sysMenuVOS);

        if (targetMenu) {
            console.log("找到的目标菜单：", targetMenu);
            // const sysConfig = useSysConfigStore();
            // router.push({ path: targetMenu.href });
            // sysConfig.menuActiveKey = targetMenu.href;
            // sysConfig.tabs = [
            //     { name: targetMenu.href, title: targetMenu.menuName },
            // ];
            // sysConfig.tabsValue = targetMenu.href;

            localStorage.setItem("firstMenu", JSON.stringify(targetMenu));
        }

        router.push({ path: "/navigation" });
    }

    // 2. 系统配置
    const res = await userStore.getSysConfig();
    const records = (res.data as Array<ConfigParams>) || [];
    localStorage.setItem("system_config", JSON.stringify(records));
    const sysConfig = useSysConfigStore();
    sysConfig.systemConfig = records;
};

function login(refElForm: FormInstance | null) {
    if (!refElForm) return;
    refElForm.validate(async (valid, fields) => {
        if (valid) {
            btnLoading.value = true;
            const response: Response = (await userStore.login(
                ajaxParams,
                "/api/web/v1/login"
            )) as Response;
            btnLoading.value = false;
            const { code } = response;
            if (code == 200) {
                initSystem();
            }
        }
    });
}

const getQueryVariable = async () => {
    let ticket = "";
    const queryArr = window.location.href.split("?");
    if (queryArr.length < 2) {
        isAutoLogin.value = false;
        return;
    }
    const query = queryArr[1];

    const vars = query.split("&");
    for (let i = 0; i < vars.length; i++) {
        const pair = vars[i].split("=");
        if (pair[0] == "ticket") {
            ticket = pair[1];
        }
    }

    const response: Response = (await userStore.autoLogin(ticket)) as Response;
    const { code } = response;
    if (code == 200) {
        initSystem();
    } else {
        isAutoLogin.value = false;
    }
};

const logoUrl = computed(() => {
    const sysConfigStr = localStorage.getItem("system_config");
    if (sysConfigStr) {
        const sysConfig = JSON.parse(sysConfigStr) as Array<ConfigParams>;
        const systemLittleLogo = sysConfig.find(
            (item) => item.configKey === "system_logo"
        );
        if (systemLittleLogo) {
            return `${baseStaticUrl}${systemLittleLogo.configValue}`;
        }
    }
    return null;
});
</script>

<template>
    <div class="login">
        <img :src="logoUrl ? logoUrl : ''" class="logo-img" />
        <div class="login-panel" v-if="!isAutoLogin">
            <div class="title">登录</div>
            <el-form
                ref="refElForm"
                size="large"
                label-position="right"
                label-width="0px"
                :model="ajaxParams"
                :rules="rules"
            >
                <el-form-item label="" prop="username">
                    <el-input
                        v-model="ajaxParams.username"
                        placeholder="请输入账号"
                        :prefix-icon="User"
                    />
                </el-form-item>
                <el-form-item label="" prop="password">
                    <el-input
                        :prefix-icon="Lock"
                        type="password"
                        show-password
                        v-model="ajaxParams.password"
                        placeholder="请输入密码"
                    />
                </el-form-item>
                <!-- <el-form-item label="" prop="">
                    <div class="code-img">
                        <el-input
                            :prefix-icon="ChatRound"
                            v-model="ajaxParams.code"
                            placeholder="请输入验证码"
                        />

                        <el-image src="" fit="fill" />
                    </div>
                </el-form-item> -->

                <!-- <el-form-item label="">
                    <el-checkbox
                        v-model="ajaxParams.isSave"
                        label="记住密码"
                        size="large"
                    />
                </el-form-item> -->
            </el-form>

            <div class="btn-box">
                <el-button
                    type="primary"
                    :loading="btnLoading"
                    @click="login(refElForm)"
                    size="large"
                    style="width: 100%"
                    >登录</el-button
                >
                <!-- <el-button
                    style="width: 45%"
                    type="primary"
                    plain
                    @click="toRegister"
                    >注册</el-button
                > -->
            </div>
            <div class="tip-info">涉密信息不上网，上网信息不涉密</div>
        </div>

        <div
            v-else
            style="width: 100%"
            v-loading="isAutoLogin"
            element-loading-text=""
        ></div>

        <div class="copyRight-info" @click="toRecordInfoUrl">
            {{ recordInfo }}
        </div>
    </div>
</template>

<style scoped lang="scss">
.login {
    height: 100%;
    background-image: url("./imgs/bg.png");
    background-repeat: repeat;
    background-size: cover;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;

    .logo-img {
        margin: 50px 0;
        width: 260px;
    }
    .login-panel {
        background-color: #ffffff;
        border-radius: 15px;
        width: 450px;
        height: 400px;
        box-sizing: border-box;
        padding: 0 60px;
        position: relative;

        .btn-box {
            width: 100%;
            display: flex;
            justify-content: space-between;
        }

        .title {
            width: 100%;
            text-align: center;
            font-size: 18px;
            font-family: Source Han Sans CN-Regular, Source Han Sans CN;
            font-weight: 400;
            color: #051838;
            margin: 30px 0;
        }
    }
    .code-img {
        display: flex;
        justify-content: space-between;
        width: 100%;
        .el-image {
            margin-left: 10px;
            width: 100px;
            height: 40px;
        }
    }
    .copyRight-info {
        color: #ffffff;
        position: absolute;
        bottom: 20px;
        cursor: pointer;
    }
}

.tip-info {
    color: #e81717;
    font-size: 20px;
    position: absolute;
    bottom: 20px;
    width: 330px;
    text-align: center;
    // margin-top: 60px;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
import { useSysConfigStore } from "@/stores/modules/sysConfig";
import type { ConfigParams } from "@/types";
export default defineComponent({
    title: "登录",
    name: "LoginView",
});
</script>
