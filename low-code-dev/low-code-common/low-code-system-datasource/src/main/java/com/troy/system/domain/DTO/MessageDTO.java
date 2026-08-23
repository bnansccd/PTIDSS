package com.troy.system.domain.DTO;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class MessageDTO {


    @NotBlank(message = "消息不能为空！")
    private String message;

    @NotEmpty(message = "手机号不能为空！")
    private List<String> phones;

}
