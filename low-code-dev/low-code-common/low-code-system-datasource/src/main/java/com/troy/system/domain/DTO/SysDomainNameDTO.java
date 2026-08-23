package com.troy.system.domain.DTO;

import com.troy.common.core.constant.RegexConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/8 14:14:09
 * @Description: DomainNameVO
 * @Version: 1.0.0
 */
@Data
@ApiModel(description = "域名管理")
public class SysDomainNameDTO implements Serializable {

    @ApiModelProperty(value = "域名（最大长度64）正则：（" + RegexConstants.DOMAIN_REGX + "）", required = true)
    @NotBlank(message = "请填写域名")
    @Length(max = 64, message = "域名最大长度不超过64")
    @Pattern(regexp = RegexConstants.DOMAIN_REGX, message = "请输入正确的域名")
    private String domainName;

    @ApiModelProperty(value = "泛域名（最大长度128）正则：（" + RegexConstants.DOMAIN_REGX + "）", required = true)
    @NotBlank(message = "请填写泛域名")
    @Length(max = 128, message = "泛域名最大长度不超过128")
    @Pattern(regexp = RegexConstants.DOMAIN_REGX, message = "请输入正确的泛域名")
    private String universalDomainName;

    @ApiModelProperty(value = "备案信息（最大长度255）")
    @Length(max = 255, message = "备案信息最大长度不超过255")
    private String recordInfo;

    @ApiModelProperty(value = "备案信息跳转地址（最大长度128）正则：（" + RegexConstants.URL_REGX + "）")
    @Length(max = 128, message = "备案信息跳转地址最大长度不超过128")
    @Pattern(regexp = RegexConstants.URL_REGX, message = "请输入正确的备案信息跳转地址")
    private String recordInfoUrl;

    @ApiModelProperty(value = "备注（最大长度255）")
    @Length(max = 255, message = "备备注最大长度不超过255")
    private String remarks;

}
