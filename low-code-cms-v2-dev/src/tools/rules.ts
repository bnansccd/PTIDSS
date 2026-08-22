/**
 * 输入框提示必填
 * @param msg 提示信息
 * @returns
 */
export function requiredInput(msg?: string) {
    return {
        required: true,
        message: msg || "该项为必填项",
        trigger: "change",
    };
}

/**
 *
 * @returns 手机号验证
 */
export function verifyPhone(required:boolean) {
    return {
        trigger: "change",
        validator: (rule: any, value: any, callback: any) => {
            if (value === "") {
                if (required) {
                    callback(new Error("请输入正确的手机号"));
                } else {
                    callback();
                }
            } else if (!/^[1][3,4,5,7,8,9][0-9]{9}$/.test(value)) {
                callback(new Error("请输入正确的手机号"));
            } else {
                callback();
            }
        },
    };
}

/**
 *选项框提示必填
 * @returns
 */
export function requiredSelect() {
    return {
        required: true,
        message: "该项为必选项",
        trigger: "change",
    };
}

/**
 * 字符提示最大个数值
 * @param max
 * @returns
 */
export function charMax(max: number) {
    return {
        min: 0,
        max: max,
        message: `字符长度不超过${max}个字符`,
        trigger: "change",
    };
}

// 全局通用表单验证函数

/**
 * 排序规则验证
 * @param rule
 * @param value
 * @param callback
 */
export function sortFn() {
    return {
        trigger: "change",

        validator: (rule: any, value: any, callback: any) => {
            if (!/^\d*$/.test(value)) {
                callback(new Error("排序只能输入0或正整数"));
            } else {
                callback();
            }
        },
    };
}

/**
 * 域名验证
 * @param rule
 * @param value
 * @param callback
 */
export function domainFn() {
    return {
        trigger: "change",

        validator: (rule: any, value: any, callback: any) => {
            if (
                !/^((?!-)[A-Za-z0-9-]{1,63}(?<!-).)+[A-Za-z]{2,6}$/.test(value)
            ) {
                callback(new Error("请输入正确的域名"));
            } else {
                callback();
            }
        },
    };
}

/**
 * 网址验证
 * @param rule
 * @param value
 * @param callback
 */
export function websiteUrlFn() {
    return {
        trigger: "change",

        validator: (rule: any, value: any, callback: any) => {
            if (value == "" || value == null) {
                callback();
            }
            if (!/(http|https):\/\/[^\s/$.?#].[^\s]*$/.test(value)) {
                callback(new Error("请输入正确的网址"));
            } else {
                callback();
            }
        },
    };
}

/**
 * 泛域名验证
 * @param rule
 * @param value
 * @param callback
 */
export function universalDomain() {
    return {
        trigger: "change",

        validator: (rule: any, value: any, callback: any) => {
            if (!/.roadmaintain\.cn/.test(value)) {
                callback(new Error("请输入正确的泛域名域名"));
            } else {
                callback();
            }
        },
    };
}

/**
 *
 * @param max 验证最大排序号
 * @returns
 */
export function sortMax(max: number) {
    return {
        trigger: "change",

        validator: (rule: any, value: any, callback: any) => {
            if (value > 100000) {
                callback(new Error("排序数最大不能超过100000"));
            } else {
                callback();
            }
        },
    };
}

export function phoneFn() {
    return {
        trigger: "change",

        validator: (rule: any, value: any, callback: any) => {
            if (!/^1[1-9][0-9]{9}$/.test(value)) {
                callback(new Error("请输入正确的手机号"));
            } else {
                callback();
            }
        },
    };
}
