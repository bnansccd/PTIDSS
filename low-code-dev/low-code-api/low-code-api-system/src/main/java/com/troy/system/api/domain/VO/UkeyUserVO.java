package com.troy.system.api.domain.VO;


import com.troy.common.core.anotation.SensitiveData;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sym
 * @since 2024/11/14 14:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UkeyUserVO {

    private Long id;

    @ApiModelProperty("证书持有人姓名")
    private String cn;


    @ApiModelProperty("证件号码")
    @SensitiveData(type = SensitiveData.SensitiveType.ID_CARD)
    private String sn;


    @ApiModelProperty("用户 id")
    private Long userId;


    @ApiModelProperty("用户信息")
    private SysUserVO userVO;

}
