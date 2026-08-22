<script setup lang="ts">
import { register } from "./api";

import { reactive } from "vue";
import { rules } from "./rules";
import { User, Lock, ChatRound } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
const router = useRouter();
const ajaxParams = reactive({
    username: "",
    password: "",
    code: "",
    checked: false,
});

function handleRegister() {
    register(ajaxParams);
}
const toLogin = () => {
    router.push({ path: "/login" });
};
</script>

<template>
    <div class="login">
        <img src="../../assets/guanganLogo.jpg" class="logo-img" />
        <div class="login-panel">
            <div class="title">注册</div>
            <el-form
                ref="ruleFormRef"
                size="large"
                label-position="right"
                label-width="0px"
                :model="ajaxParams"
                :rules="rules"
            >
                <el-form-item label="" prop="name">
                    <el-input
                        v-model="ajaxParams.username"
                        placeholder="请输入账号"
                        :prefix-icon="User"
                    />
                </el-form-item>
                <el-form-item label="" prop="age">
                    <el-input
                        :prefix-icon="Lock"
                        type="password"
                        show-password
                        v-model="ajaxParams.password"
                        placeholder="请输入密码"
                    />
                </el-form-item>
                <el-form-item label="" prop="">
                    <div class="code-img">
                        <el-input
                            :prefix-icon="ChatRound"
                            v-model="ajaxParams.code"
                            placeholder="请输入验证码"
                        />

                        <el-image src="" fit="fill" />
                    </div>
                </el-form-item>

                <el-form-item label="">
                    <el-checkbox
                        v-model="ajaxParams.checked"
                        label="记住密码"
                        size="large"
                    />
                </el-form-item>
            </el-form>

            <div class="btn-box">
                <el-button
                    @click="handleRegister"
                    type="primary"
                    style="width: 45%"
                    >注册</el-button
                >
                <el-button
                    @click="toLogin"
                    style="width: 45%"
                    type="primary"
                    plain
                    >返回登录</el-button
                >
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
.login {
    height: 100%;
    background-image: url("../login/imgs/bg.png");
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
        padding: 0px 60px;

        .title {
            width: 100%;
            text-align: center;
            font-size: 18px;
            font-family: Source Han Sans CN-Regular, Source Han Sans CN;
            font-weight: 400;
            color: #051838;
            margin: 30px 0;
        }
        .btn-box {
            width: 100%;
            display: flex;
            justify-content: space-between;
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
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "注册",
    name: "RegisterView",
});
</script>
