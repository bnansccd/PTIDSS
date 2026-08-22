package com.troy.auth.dto;

import lombok.Data;

/**
 * @author chenxl
 * @date 2024/1/10
 */
@Data
public class Result<T> {

    private String msg;

    private String code;

    private T data;

}
