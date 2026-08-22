<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import { rules } from "./rules";
import type { FormInstance } from "element-plus";
import { User, Lock, ChatRound } from "@element-plus/icons-vue";
// import router from "@/router/index";
import $api from "@/api/Axios";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
import type { MenuResponseParams, Response } from "@/types/index";
import { apiDomain } from "./api";
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

// 检查IP并跳转的方法
const checkIpAndRedirect = () => {
    // 获取当前页面的hostname（IP或域名）
    const hostname = window.location.hostname;
    // 获取当前页面的完整URL
    const currentUrl = window.location.href;

    console.log(hostname, "hostname");

    // 判断当前访问的IP是否为 182.150.21.163
    if (hostname === "182.150.21.163" || hostname === "localhost") {
        // 构建跳转URL，保留当前路径和参数
        // 或者直接跳转到相对路径
        router.push({ path: "/loginAdmin" });
        // 或者使用window.location跳转
        return true;
    }
    return false;
};

onBeforeMount(async () => {
    // 检查IP并跳转
    if (checkIpAndRedirect()) {
        return;
    }
    getQueryVariable();
    // const data = await apiDomain();
    // if (data && data.recordInfo) {
    //     window.sessionStorage.setItem("recordInfo", data.recordInfo);
    //     recordInfo.value = data.recordInfo;
    //     recordInfoUrl.value = data.recordInfoUrl;
    // }

    const sysDomainNameVO = JSON.parse(
        window.localStorage.getItem("sysDomainNameVO") || "{}"
    );

    recordInfo.value = sysDomainNameVO.recordInfo || "";
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
        if (current?.menuType === "1") {
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

            // router.push({ path: "/navigation" });
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
                ajaxParams
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

const toLoginSystem = () => {
    const hostname = window.location.hostname;

    if (hostname === "10.0.38.73") {
        // 四川省电子政务外网用户
        window.location.href = "http://10.0.38.73:8900/login";
    } else if (hostname === "172.16.17.13") {
        // 厅机关、厅直各单位及市（州）交通运输局交通专网用户
        window.location.href = "http://172.16.17.13:8900/login";
    } else {
        // 厅机关、厅直各单位及市（州）交通运输局交通专网用户
        window.location.href = "http://172.16.17.13:8900/login";
    }

    console.log("loginUrl", loginUrl);
};
</script>

<template>
    <div class="login">
        <!-- <img src="../../assets/guanganLogo.jpg" class="logo-img" /> -->
        <div class="login-panel" v-if="!isAutoLogin">
            <div class="title">请从综合业务办理平台登录</div>

            <div class="btn-box">
                <el-button
                    type="primary"
                    :loading="btnLoading"
                    @click="toLoginSystem"
                    size="large"
                    style="width: 100%"
                    >跳转综合业务办理平台</el-button
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
