<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";

// PTIDSS 登录页：用户名 + 密码 + 图形验证码（对接 ptidss-server /auth/captcha、/auth/login）

const router = useRouter();
const userStore = useUserStore();

const loginForm = reactive({
    username: "",
    password: "",
    captchaKey: "",
    captchaCode: "",
});

const captchaImg = ref("");
const btnLoading = ref(false);
const formRef = ref();

const getCaptcha = async () => {
    try {
        const data = await userStore.getCaptcha();
        loginForm.captchaKey = data.captchaKey;
        captchaImg.value = data.image;
    } catch (e) {
        ElMessage.error("验证码获取失败，请检查后端服务");
    }
};

const handleLogin = async () => {
    if (!loginForm.username || !loginForm.password) {
        ElMessage.warning("请输入用户名和密码");
        return;
    }
    btnLoading.value = true;
    try {
        const response: any = await userStore.login(loginForm);
        if (response.code === 0) {
            ElMessage.success("登录成功");
            // 拉取当前用户（角色/权限/区域），随后进入首页
            await userStore.getUserInfo();
            router.push({ name: "dashboard" });
        } else {
            // 验证码一次性消费：失败后刷新
            getCaptcha();
        }
    } catch (e) {
        // 网络错误已由拦截器提示
    } finally {
        btnLoading.value = false;
    }
};

onMounted(() => {
    getCaptcha();
    document.title = "电力交易智能辅助决策系统（PTIDSS）";
});
</script>

<template>
    <div class="login-page">
        <div class="login-card">
            <div class="login-title">
                <h1>电力交易智能辅助决策系统</h1>
                <p>PTIDSS · 组织架构与用户权限管理</p>
            </div>

            <el-form
                ref="formRef"
                :model="loginForm"
                size="large"
                @keyup.enter="handleLogin"
            >
                <el-form-item>
                    <el-input
                        v-model="loginForm.username"
                        placeholder="用户名"
                        :prefix-icon="'User'"
                        clearable
                    />
                </el-form-item>

                <el-form-item>
                    <el-input
                        v-model="loginForm.password"
                        type="password"
                        placeholder="密码"
                        :prefix-icon="'Lock'"
                        show-password
                        clearable
                    />
                </el-form-item>

                <el-form-item>
                    <div class="captcha-row">
                        <el-input
                            v-model="loginForm.captchaCode"
                            placeholder="验证码"
                            :prefix-icon="'Key'"
                            clearable
                        />
                        <img
                            :src="captchaImg"
                            class="captcha-img"
                            alt="点击刷新"
                            title="点击刷新验证码"
                            @click="getCaptcha"
                        />
                    </div>
                </el-form-item>

                <el-form-item>
                    <el-button
                        type="primary"
                        class="login-btn"
                        :loading="btnLoading"
                        @click="handleLogin"
                    >
                        登 录
                    </el-button>
                </el-form-item>
            </el-form>

            <div class="login-tips">
                <p>初始账号：admin（系统管理员）/ trader01（交易员）</p>
                <p>初始密码：Ptidss@2026（首次登录后请及时修改）</p>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
.login-page {
    width: 100vw;
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #1f3b6b 0%, #2f6bb0 55%, #4a9bd8 100%);
    position: relative;
    overflow: hidden;
}

.login-card {
    width: 420px;
    padding: 48px 40px 32px;
    background: rgba(255, 255, 255, 0.96);
    border-radius: 12px;
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.28);
    z-index: 1;

    .login-title {
        text-align: center;
        margin-bottom: 36px;

        h1 {
            font-size: 24px;
            color: #1f3b6b;
            margin: 0 0 8px;
        }
        p {
            font-size: 13px;
            color: #8a93a5;
            margin: 0;
        }
    }

    .captcha-row {
        display: flex;
        gap: 12px;
        width: 100%;

        .el-input {
            flex: 1;
        }

        .captcha-img {
            width: 130px;
            height: 40px;
            border-radius: 4px;
            cursor: pointer;
            border: 1px solid #e4e7ed;
        }
    }

    .login-btn {
        width: 100%;
        font-size: 16px;
        letter-spacing: 6px;
    }

    .login-tips {
        margin-top: 20px;
        padding-top: 16px;
        border-top: 1px dashed #e4e7ed;
        text-align: center;

        p {
            margin: 4px 0;
            font-size: 12px;
            color: #a0a8b8;
        }
    }
}
</style>
