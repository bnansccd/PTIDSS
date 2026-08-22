package com.troy.system.domain.VO;

import com.troy.common.core.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author sym
 * @since 2024/10/30 09:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginTimesAndChainVO {

    @ApiModelProperty("登录次数")
    private Integer loginTimes = Constants.ZERO;

    @ApiModelProperty("登录次数环比(%)")
    private BigDecimal loginChain = BigDecimal.ZERO;


}
