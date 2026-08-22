package com.troy.common.core.domain;

import com.troy.common.core.constant.Constants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.utils.StringUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author ZhuQing
 * @Date: 2022/7/5  17:27
 */
@Data
public class ResultVO<T> implements Serializable {

    private int code;

    private String msg;

    private T data;

    /**
     * 成功
     */
    public static final int SUCCESS = Constants.SUCCESS;

    /**
     * 失败
     */
    public static final int FAIL = Constants.FAIL;

    public ResultVO() {
    }

    public ResultVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultVO(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResultVO success() {
        return new ResultVO(SUCCESS, null);
    }

    public static <T> ResultVO<T> success(T data) {
        return new ResultVO(SUCCESS, null, data);
    }

    public static <T> ResultVO<T> success(String msg, T data) {
        return new ResultVO(SUCCESS, msg, data);
    }

    public static ResultVO success(ResultEnum resultEnum, Object... obj) {
        return new ResultVO(SUCCESS, ResultEnum.getMsg(resultEnum, obj).getMsg());
    }

    public static ResultVO success(ResultEnum resultEnum) {
        return new ResultVO(SUCCESS, ResultEnum.getMsg(resultEnum));
    }

    public static ResultVO success(ResultEnum resultEnum, Object data, Object... obj) {
        return new ResultVO(SUCCESS, ResultEnum.getMsg(resultEnum, obj).getMsg(), data);
    }

    public static ResultVO success(ResultEnum resultEnum, Object data) {
        return new ResultVO(SUCCESS, ResultEnum.getMsg(resultEnum), data);
    }

    public static ResultVO fail() {
        return new ResultVO(FAIL, null);
    }

    public static ResultVO fail(String msg) {
        return new ResultVO(FAIL, msg);
    }

    public static ResultVO fail(int code, String msg) {
        return new ResultVO(code, msg);
    }


    public static <T> ResultVO<T> fail(T data) {
        return new ResultVO(FAIL, null, data);
    }

    public static ResultVO fail(ResultEnum resultEnum, Object... obj) {
        return new ResultVO(FAIL, ResultEnum.getMsg(resultEnum, obj).getMsg());
    }

    public static ResultVO fail(ResultEnum resultEnum) {
        return new ResultVO(FAIL, ResultEnum.getMsg(resultEnum));
    }

    public static boolean isSuccess(ResultVO resultVO) {
        if (StringUtils.isNotNull(resultVO) && SUCCESS == resultVO.getCode()) {
            return true;
        }
        return false;
    }

}
