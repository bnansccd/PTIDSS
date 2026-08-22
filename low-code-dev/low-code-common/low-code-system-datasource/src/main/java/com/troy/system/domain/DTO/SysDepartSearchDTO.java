package com.troy.system.domain.DTO;

import com.troy.common.core.web.DTO.PageDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Classname: SysDepartDto
 * @Description:
 * @Date 2022/9/6
 * @Author: yzy
 * @Version
 **/
@Data
@ApiModel(description = "新增岗位")
public class SysDepartSearchDTO extends PageDTO {

    private String departName;

    private String enable;

}
