package com.troy.system.api.domain.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * @author sym
 * @since 2024/11/14 15:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UkeyUserDTO {

    @ApiModelProperty("证书持有人姓名")
    @NotEmpty
    private String cn;


    @ApiModelProperty("证件号码")
    @NotEmpty
    private String sn;


    @ApiModelProperty("用户 id")
    @NotNull
    private Long userId;

}
