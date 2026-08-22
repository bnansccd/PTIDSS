export const rules = {
    username: {
        required: true,
        message: "请输入账号",
        trigger: "change",
    },
    password: {
        required: true,
        message: "请输入密码",
        trigger: "change",
    },
    code: {
        required: true,
        message: "请输入验证码",
        trigger: "change",
    },
};
