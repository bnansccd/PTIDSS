package com.troy.common.core.enums;

import com.troy.common.core.utils.StringUtils;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/15 10:10:07
 * @Description: MessageEnum
 * @Version: 1.0.0
 */
public enum ResultEnum {

    SUCCESS(200, "操作成功！"),

    FAIL(-1, "操作失败！"),

    NOT_FOUND(10000, "操作失败：{}不存在！"),

    EXIST(10001, "操作失败：{}已经存在！"),

    EXPIRE(10002, "操作失败：{}已过期！"),

    NOT_EFFECTIVE(10003, "操作失败：{}未生效！"),

    NOT_SUPPORT_OPERATE(10004, "操作失败：{}不允许此操作！"),

    EXIST_CHILD(10005, "操作失败：{}存在子级！"),

    PARENT_NOT_FOUND(10006, "操作失败：{}父级不存在！"),

    PLEASE_SELECT(10007, "操作失败：请选择{}！"),

    ERROR(10008, "操作失败：{}错误！"),

    NOT_ACCESSIBLE(10009, "操作失败：{}不允许访问！"),

    BE_CURRENT(10010, "{}"),

    RENDER_TEMPLATE_FAIL(10011, "渲染模板失败，表名：{}"),

    OPERATE_FAIL(10012, "{}失败！"),

    NO_RANGE(10013, "操作失败：{}不在指定范围！"),

    OPERATE_SUCCESS(200, "{}成功！"),

    STOP(10014, "操作失败：{}停用！"),

    NOT_ACTIVATION(10015, "操作失败：{}未激活,请激活!"),

    ILLEGAL_LINK(10016, "非法链接不允许访问!"),

    DATA_MUST_FILL(10017, "参数{}为空"),

    DATA_INVALID(10018, "参数格式不正确"),

    DATA_LENGTH_INVALID(10019, "数据长度不正确"),

    MSG_NOT_OUT_TIME(10020, "验证码未过期，请检查手机短信"),

    MSG_OUT_OF_DATE(10021, "验证码已过期"),

    NOT_PHONE_MATCH(10022, "未匹配到手机号"),

    ENCRYPT_ERROR(10023, "解密异常"),

    ENCRYPT_BE_UPDATE_ERROR(10024, "数据被篡改"),

    PROMPT(0, ""),

    DZZZ_SELF_20001(20001,"验证不通过"),
    ;


    private String msg;

    private Integer code;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public static String getMsg(ResultEnum resultEnum) {
        return resultEnum.msg;
    }

    public static ResultEnum getMsg(ResultEnum resultEnum, Object... obj) {
        String format = StringUtils.format(resultEnum.getMsg(), obj);
        ResultEnum prompt = PROMPT;
        prompt.setCode(resultEnum.getCode());
        prompt.setMsg(format);
        return prompt;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
