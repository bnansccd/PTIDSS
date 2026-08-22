<script setup lang="ts">
import { onBeforeMount, reactive, ref } from "vue";
import { rules, ruleCode } from "./rules";
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
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
import type { MenuResponseParams, Response } from "@/types/index";
import { apiDomain, apiCode, getPhoneCode, apiRandom, submitUsb } from "./api";

import { useFreelogin } from "./hooks/free-login";
import {
    SOF_GetUserList,
    SOF_Login,
    SOF_ExportUserCert,
    SOF_GetCertInfo,
    base64Decode,
    SOF_SignData,
    SOF_SignMessage,
    base64Encode,
    base64toHEX,
    _utf8_decode,
} from "./sccmPlugin.js";

const ApibaseURL = "/auth/api/web/v1";
const router = useRouter();
const loading = ref<boolean>(true);
const userStore = useUserStore();
const recordInfo = ref("");
const recordInfoUrl = ref("");
const refElForm = ref();
const ajaxParams = reactive({
    username: "",
    password: "",
    code: "",
    isSave: false,
    tenantId: "",
    phone: "",
    phoneCode: "",
    sn: "",
    cn: "",
    cert: "",
    randoms: "",
    randomCert: "",
    certReq: "",
});
const codeLoading = ref(false);
const codeLoginParams: any = reactive({
    url: "",
    codeVal: "获取验证码",
    cookies: "",
});
const usbData: any = reactive({
    useList: [],
});
const loginType = ref("code");
let timer: any = null;

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

const codeDisabled = ref(false);
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
const getRandom = async () => {
    const res: any = await apiRandom();
    console.log(res);
    if (res) {
        ajaxParams.randoms = res.data;
    }
    setTimeout(() => {
        ElMessage.closeAll();
    }, 2000);
};
const checkUsb = async () => {
    let i = 0;
    timer = setInterval(async () => {
        if (i > 9) {
            loginType.value = "code";
            clearInterval(timer);
            timer = null;
            getPhotoCode();
            return false;
        }
        i++;
        usbData.useList = await SOF_GetUserList();
        if (usbData.useList !== "null" && usbData.useList) {
            console.log(usbData.useList, "usb用户列表");
            ElMessage.success({
                icon: "loading",
                duration: 0,
                message: "检测到usb插入,正在加载usb数据",
            });
            loginType.value = "usb";
            clearInterval(timer);
            timer = null;
            ajaxParams.password = "";
            ajaxParams.username = usbData.useList.split("||")[0];
            ajaxParams.cn = ajaxParams.username;
            getRandom();
            // ElMessage.closeAll()
            // refElForm.value?.resetFields('password')
        } else {
            loginType.value = "code";
            clearInterval(timer);
            timer = null;
            getPhotoCode();
        }
    }, 1000);
};
const thirdLogin = async (params: any) => {
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
            initSystem();
        } else {
            checkUsb();
        }
    }
};
const judgeLoginType = () => {
    const url = window.location.href;
    const params = url.split("?")[1];
    getPhotoCode();
    if (params) {
        console.log(params, "params");
        // 切换账号密码入口
        if (params == "loginType=UserPwd") {
            loginType.value = "dev";
        } else {
            // 第三方登录
            thirdLogin(params);
        }
    } else {
        import.meta.env.MODE == "development"
            ? (loginType.value = "dev")
            : checkUsb();
            // checkUsb()
    }
};
judgeLoginType();
const btnLoading = ref<boolean>(false);
async function login(refElForm: FormInstance | null) {
    if (!refElForm) return;
    btnLoading.value = true;
    if (loginType.value == "usb") {
        const checkUsb = async () => {
            const containerName = usbData.useList.split("||")[1];
            const pin = ajaxParams.password;
            const res = await SOF_Login(containerName, pin);
            if (res) {
                ajaxParams.cert = await SOF_ExportUserCert(containerName);
                const sn = await SOF_GetCertInfo(
                    ajaxParams.cert,
                    "SGD_CERT_SUBJECT_SN"
                );
                if (sn) {
                    ajaxParams.sn = base64Decode(sn);
                    // ajaxParams.sn = ajaxParams.sn
                    ajaxParams.randomCert = await SOF_SignData(
                        containerName,
                        `${ajaxParams.randoms}`
                    );
                }
                console.log("checkUsb", ajaxParams);
                ajaxParams.certReq = ajaxParams.cert
                    .replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replaceAll("\r\n", "");
                return true;
            } else {
                ElMessage.warning({
                    icon: "none",
                    message: "密码错误,验证失败",
                });
                btnLoading.value = false;
                return false;
            }
        };
        const resUsb = await checkUsb();
        if (!resUsb) {
            return false;
        }
    }
    const getParam = (val: string) => {
        const requestParam: any = {
            code: { username: ajaxParams.phone, code: ajaxParams.code },
            dev: {
                username: ajaxParams.username,
                password: ajaxParams.password,
            },
            usb: {
                cert: ajaxParams.certReq,
                signData: ajaxParams.randomCert,
                inData: ajaxParams.randoms,
                sn: ajaxParams.sn,
                cn: ajaxParams.cn,
            },
        };
        const requestUrl: any = {
            code: "/api/web/v1/loginWithCode",
            dev: "/api/web/v1/login",
            usb: "/api/data/ukey/v1/login",
        };
        return {
            data: requestParam[val],
            url: requestUrl[val],
        };
    };
    const { data, url } = getParam(loginType.value);
    console.log("checkUsb", ajaxParams);

    refElForm.validate(async (valid, fields) => {
        if (valid) {
            btnLoading.value = true;
            let response: Response;
            try {
                response = (await userStore.login(data, url)) as Response;
                btnLoading.value = false;
                const { code } = response;

                if (code == 200) {
                    initSystem();
                } else {
                    btnLoading.value = false;
                }
            } catch {
                btnLoading.value = false;
            }
        }
    });
}
onBeforeMount(async () => {});
const toRecordInfoUrl = () => {
    if (recordInfoUrl.value) {
        window.open(recordInfoUrl.value);
    }
};

const initSystem = async () => {
    // 递归：只遍历数组第一项，查找这条链上最后一个 menuType ===1 的菜单
    const findLastValidMenuInFirstBranch = (menuList: any[]) => {
        if (!menuList || menuList.length === 0) return null;

        // 永远只取数组第一项
        const current = menuList[0];

        console.log("current", current);
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
        // console.log("data", data);
        const sysMenuVOS = data.sysMenuVOS[0]?.children || [];
        const targetMenu = findLastValidMenuInFirstBranch(sysMenuVOS);

        // console.log("targetMenu", targetMenu);

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
</script>

<template>
    <div class="login">
        <img src="../../assets/guanganLogo.jpg" class="logo-img" />
        <div class="login-panel">
            <div class="title">登录</div>
            <el-form
                ref="refElForm"
                size="large"
                label-position="right"
                label-width="0px"
                :model="ajaxParams"
                :rules="loginType == 'code' ? ruleCode : rules"
            >
                <div v-if="loginType == 'dev'">
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
                <div v-if="loginType == 'code'">
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

                            <el-image
                                @click.stop="getPhotoCode"
                                :src="codeLoginParams.url"
                                fit="fill"
                            />
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
                <div v-if="loginType == 'usb'">
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
                            name="randomName"
                            v-bind:autocomplete="'new-password'"
                            v-model="ajaxParams.password"
                            placeholder="请输入密码"
                        />
                    </el-form-item>
                    <!-- <el-form-item label="" prop="sn">
                        <el-input
                            :prefix-icon="Tickets"
                            v-model="ajaxParams.sn"
                            placeholder="输入密码获取证书sn"
                        />
                    </el-form-item> -->
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
            cursor: pointer;
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
