<script setup lang="ts">
import { onBeforeMount, reactive, ref, onMounted } from "vue";
import { rules, ruleCode } from "./rules";
import { ElMessage } from "element-plus";
import type { FormInstance } from "element-plus";
import {
    User,
    Lock,
    ChatRound,
    Iphone,
    ChatDotSquare,
} from "@element-plus/icons-vue";
// import router from "@/router/index";
import $api from "@/api/Axios";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
import type { MenuResponseParams, Response } from "@/types/index";
import { apiDomain, apiCode, getPhoneCode } from "../api";
import { getUkey, validUkey } from "./ukey/ukey";
import { useFreelogin } from "../hooks/free-login";

const ApibaseURL = "/auth/api/web/v1";
const router = useRouter();
const loading = ref<boolean>(true);
const userStore = useUserStore();
const recordInfo = ref("");
const recordInfoUrl = ref("");
const ajaxParams = reactive({
    username: "",
    password: "",
    code: "",
    isSave: false,
    tenantId: "",
    phone: "",
    phoneCode: "",
    cert: "",
});

const refElForm = ref();
useFreelogin();

function getQueryString(name: string) {
    const reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    const r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return decodeURIComponent(r[2]);
    }
    return null;
}

const codeLoginParams: any = reactive({
    url: "",
    codeVal: "获取验证码",
    cookies: "",
});
// const showForm = ref(!!!(import.meta.env.MODE == "development"))
const showForm = ref(true);
const loginType = ref("dev"); // dev phone ukey
const lastRules = ref(showForm.value ? rules : ruleCode);
const codeDisabled = ref(false);
const codeLoading = ref(false);
const getPhotoCode = async () => {
    codeLoading.value = true;

    ajaxParams.phoneCode = "";
    refElForm.value?.resetFields("phoneCode");
    const res1 = await apiCode();
    try {
        codeLoginParams.cookies = res1.headers.get("requestid");
        const res = await res1.blob();
        const imageUrl = URL.createObjectURL(res);

        codeLoginParams.url = imageUrl;
        codeLoading.value = false;
    } catch (error) {
        console.error("Failed to fetch and display captcha image:", error);
        codeLoading.value = false;
    }
};
const sendCode = async () => {
    if (!ajaxParams.phoneCode || !ajaxParams.phone) {
        refElForm.value?.validateField("phoneCode");
        refElForm.value?.validateField("phone");
        return false;
    }
    codeDisabled.value = true;
    const res = await getPhoneCode({
        phone: ajaxParams.phone,
        code: ajaxParams.phoneCode,
        requestid: codeLoginParams.cookies,
    });
    if (!res) {
        getPhotoCode();
        ajaxParams.phoneCode = "";
        codeDisabled.value = false;
    } else {
        if (res === "10020") return false;
        let countdown = 60;
        codeLoginParams.codeVal = `${countdown} s`;
        const intervalId = setInterval(() => {
            countdown--;
            if (countdown <= 0) {
                clearInterval(intervalId);
                codeLoginParams.codeVal = "获取验证码";
                countdown = 60;
                codeDisabled.value = false;
            } else {
                codeLoginParams.codeVal = `${countdown} s`;
            }
        }, 1000);
    }
};

onBeforeMount(async () => {
    const url = window.location.href;
    // const url = "https://10.13.124.11/#/login?loginType=sso";
    const params = url.split("?")[1];
    if (params) {
        const firstParam = params.split("&")[0].split("=")[1];
        const firstParam11 = params.split("&")[0].split("=")[0];

        if (firstParam == "sso") {
            sessionStorage.removeItem("access_token");
            window.location.href = `https://sso.gazhcs.com/esc-sso/oauth2.0/authorize?client_id=df62729281241a4fe778&response_type=code&redirect_uri=${location.origin}/#/login`;
        } else {
            if (firstParam11 !== "loginType" && firstParam11 !== "code") {
                return null;
            }

            const response: Response = (await userStore.freelogin(
                firstParam
            )) as Response;
            const { code } = response;
            if (code == 200) {
                {
                    const respon: Response =
                        (await userStore.getUserInfo()) as Response;

                    const { code, data } = respon;
                    if (code == 200) {
                        const sysMenuVOS =
                            data.sysMenuVOS as Array<MenuResponseParams>;
                        if (
                            Array.isArray(sysMenuVOS) &&
                            sysMenuVOS.length > 0
                        ) {
                            let menu: MenuResponseParams = {
                                href: "",
                                icon: function (): void {
                                    throw new Error(
                                        "Function not implemented."
                                    );
                                },
                                id: "",
                                label: function () {
                                    throw new Error(
                                        "Function not implemented."
                                    );
                                },
                                menuType: "",
                            };
                            if (
                                Array.isArray(sysMenuVOS[0].children) &&
                                sysMenuVOS[0].children.length > 0
                            ) {
                                menu = sysMenuVOS[0].children[0];
                            } else {
                                menu = sysMenuVOS[0];
                            }
                            const sysConfig = useSysConfigStore();
                            router.push({ path: menu.href });
                            sysConfig.menuActiveKey = menu.href;
                            sysConfig.tabs = [
                                { name: menu.href, title: menu.menuName },
                            ];
                            sysConfig.tabsValue = menu.href;
                        } else {
                            router.push({ path: "/code403" });
                        }
                    } else {
                        !showForm.value && getPhotoCode();
                    }
                }

                {
                    const res: Response =
                        (await userStore.getSysConfig()) as Response;
                    const records = (res.data as Array<ConfigParams>) || [];
                    localStorage.setItem(
                        "system_config",
                        JSON.stringify(records)
                    );
                    const sysConfig = useSysConfigStore();
                    sysConfig.systemConfig = records;
                }
            } else {
                !showForm.value && getPhotoCode();
            }
        }
        const data = await apiDomain();
        if (data && data.recordInfo) {
            window.sessionStorage.setItem("recordInfo", data.recordInfo);
            recordInfo.value = data.recordInfo;
            recordInfoUrl.value = data.recordInfoUrl;
        }
    } else {
        !showForm.value && getPhotoCode();
    }
});

const toRegister = () => {
    router.push({ path: "/register" });
};
const toRecordInfoUrl = () => {
    if (recordInfoUrl.value) {
        window.open(recordInfoUrl.value);
    }
};

const btnLoading = ref<boolean>(false);
const isUkey = ref<boolean>(false);
function login(refElForm: FormInstance | null) {
    if (!refElForm) return;

    const data = { ...ajaxParams };
    const url = "/api/web/v1/login";

    refElForm.validate(async (valid, fields) => {
        if (valid) {
            btnLoading.value = true;
            let response: Response;
            try {
                response = (await userStore.login(data, url)) as Response;
                btnLoading.value = false;
                const { code } = response;

                if (code == 200) {
                    {
                        const respon: Response =
                            (await userStore.getUserInfo()) as Response;

                        const { code, data } = respon;
                        if (code == 200) {
                            const sysMenuVOS =
                                data.sysMenuVOS as Array<MenuResponseParams>;
                            if (
                                Array.isArray(sysMenuVOS) &&
                                sysMenuVOS.length > 0
                            ) {
                                let menu: MenuResponseParams = {
                                    href: "",
                                    icon: function (): void {
                                        throw new Error(
                                            "Function not implemented."
                                        );
                                    },
                                    id: "",
                                    label: function () {
                                        throw new Error(
                                            "Function not implemented."
                                        );
                                    },
                                    menuType: "",
                                };
                                if (
                                    Array.isArray(sysMenuVOS[0].children) &&
                                    sysMenuVOS[0].children.length > 0
                                ) {
                                    menu = sysMenuVOS[0].children[0];
                                } else {
                                    menu = sysMenuVOS[0];
                                }
                                const sysConfig = useSysConfigStore();
                                router.push({ path: menu.href });
                                sysConfig.menuActiveKey = menu.href;
                                sysConfig.tabs = [
                                    { name: menu.href, title: menu.menuName },
                                ];
                                sysConfig.tabsValue = menu.href;
                            } else {
                                router.push({ path: "/code403" });
                            }
                        }
                    }

                    {
                        const res: Response =
                            (await userStore.getSysConfig()) as Response;
                        const records = (res.data as Array<ConfigParams>) || [];
                        // let tempArr: Array<ConfigParams> = [];
                        // records.forEach((item) => {
                        //     tempArr = [
                        //         ...tempArr,
                        //         ...(item.list as Array<ConfigParams>),
                        //     ];
                        // });
                        // records = tempArr;
                        localStorage.setItem(
                            "system_config",
                            JSON.stringify(records)
                        );
                        const sysConfig = useSysConfigStore();
                        sysConfig.systemConfig = records;
                    }
                } else {
                    btnLoading.value = false;
                }
            } catch {
                btnLoading.value = false;
            }
        }
    });
}

const getQueryVariable = async () => {
    let appId = "";
    let token = "";
    const queryArr = window.location.href.split("?");
    if (queryArr.length < 2) {
        loading.value = false;
        return;
    }
    const query = queryArr[1];
    // console.log("window.location.search", window.location.href);
    // console.log(query);
    const vars = query.split("&");
    for (let i = 0; i < vars.length; i++) {
        const pair = vars[i].split("=");
        if (pair[0] == "appId") {
            appId = pair[1];
        }
        if (pair[0] == "token") {
            token = pair[1];
        }
    }

    // console.log(appId + "," + token);
    if (appId != "" && token != "") {
        const response: Response = await $api.get(
            `${ApibaseURL}/app/${appId}?token=${token}`
        );
        const { code, data } = response;
        if (code === 200) {
            userStore.access_token = data.access_token;
            sessionStorage.setItem("access_token", data.access_token);

            {
                const respon: Response =
                    (await userStore.getUserInfo()) as Response;

                const { code, data } = respon;

                if (code == 200) {
                    const sysMenuVOS =
                        data.sysMenuVOS as Array<MenuResponseParams>;
                    if (Array.isArray(sysMenuVOS) && sysMenuVOS.length > 0) {
                        let menu: MenuResponseParams = {
                            href: "",
                            icon: function (): void {
                                throw new Error("Function not implemented.");
                            },
                            id: "",
                            label: function () {
                                throw new Error("Function not implemented.");
                            },
                            menuType: "",
                        };
                        if (
                            Array.isArray(sysMenuVOS[0].children) &&
                            sysMenuVOS[0].children.length > 0
                        ) {
                            menu = sysMenuVOS[0].children[0];
                        } else {
                            menu = sysMenuVOS[0];
                        }
                        const sysConfig = useSysConfigStore();
                        router.push({ path: menu.href });
                        sysConfig.menuActiveKey = menu.href;
                        sysConfig.tabs = [
                            { name: menu.href, title: menu.menuName },
                        ];
                        sysConfig.tabsValue = menu.href;
                    } else {
                        router.push({ path: "/code403" });
                    }
                }
            }

            {
                const res: Response =
                    (await userStore.getSysConfig()) as Response;
                const records = (res.data.records as Array<ConfigParams>) || [];
                // let tempArr: Array<ConfigParams> = [];
                // records.forEach((item) => {
                //     tempArr = [
                //         ...tempArr,
                //         ...(item.list as Array<ConfigParams>),
                //     ];
                // });
                // records = tempArr;
                localStorage.setItem("system_config", JSON.stringify(records));
                const sysConfig = useSysConfigStore();
                sysConfig.systemConfig = records;
            }
        } else {
            loading.value = false;
            !showForm.value && getPhotoCode();
        }
    } else {
        loading.value = false;
        !showForm.value && getPhotoCode();
    }
};

getQueryVariable();

// 自动登录
onMounted(() => {
    // login(refElForm.value);
});
</script>

<template>
    <div
        class="login"
        v-loading="loading"
        element-loading-background="rgba(255, 255, 255, 1)"
        v-if="!loading"
    >
        <img src="../../assets/newlogo.png" class="logo-img" />
        <div class="login-panel">
            <div class="title" @click="getQueryVariable">登录</div>
            <el-form
                ref="refElForm"
                size="large"
                label-position="right"
                label-width="0px"
                :model="ajaxParams"
                :rules="lastRules"
            >
                <div v-if="showForm && !isUkey">
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
                </div>
                <div v-if="!showForm">
                    <el-form-item label="" prop="phone">
                        <el-input
                            v-model="ajaxParams.phone"
                            placeholder="请输入手机号"
                            :prefix-icon="Iphone"
                        />
                    </el-form-item>
                    <el-form-item label="" prop="phoneCode">
                        <div class="code-img">
                            <el-input
                                :prefix-icon="ChatRound"
                                v-model="ajaxParams.phoneCode"
                                placeholder="请输入图片验证码"
                            />
                            <div class="photo-code" @click="getPhotoCode">
                                <el-image
                                    :src="codeLoginParams.url"
                                    fit="fill"
                                />
                            </div>
                        </div>
                    </el-form-item>
                    <el-form-item label="" prop="code">
                        <div class="code-img">
                            <el-input
                                :prefix-icon="ChatDotSquare"
                                v-model="ajaxParams.code"
                                placeholder="请输入短信验证码"
                            >
                            </el-input>
                            <div class="code-code" @click="sendCode">
                                {{ codeLoginParams.codeVal }}
                            </div>
                        </div>
                    </el-form-item>
                </div>
                <div v-if="showForm && isUkey">
                    <el-form-item label="" prop="cert">
                        <el-input
                            v-model="ajaxParams.cert"
                            placeholder="请输入口令"
                            :prefix-icon="Lock"
                        />
                    </el-form-item>
                </div>
                <!-- <el-form-item label="" prop="tenantId">
                    <GlobalTenantSelect v-model="ajaxParams.tenantId">
                    </GlobalTenantSelect>
                </el-form-item> -->

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
                    @click="login(refElForm)"
                    style="width: 100%"
                    size="large"
                    :loading="btnLoading"
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
        </div>

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
    position: relative;

    .logo-img {
        margin: 30px 0;
        width: 380px;
    }
    .login-panel {
        background-color: #ffffff;
        border-radius: 15px;
        width: 450px;
        height: 400px;
        box-sizing: border-box;
        padding: 0 60px;

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
        .photo-code {
            width: 100px;
            height: 40px;
            cursor: pointer;
            .el-image {
                margin-left: 10px;
                width: 100px;
                height: 40px;
            }
        }
        .code-code {
            margin-left: 10px;
            width: 100px;
            text-align: center;
            height: 40px;
            color: #41c980;
            cursor: pointer;
        }
    }

    .copyRight-info {
        color: #ffffff;
        position: absolute;
        bottom: 20px;
        cursor: pointer;
    }
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
