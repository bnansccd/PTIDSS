package com.troy.system.domain.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.troy.system.api.domain.VO.SysUserVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author chenxl
 * @description
 * @date 2024-05-29 14:43
 */
@Data
@ApiModel(description = "值班信息")
public class DutyInfoVO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date dutyDate;

    @ApiModelProperty(value = "单独人员")
    private List<SysUserVO> list;

    @ApiModelProperty(value = "分组人员")
    private List<Group> groupsList;

    @Data
    public static class Group{

        private Long id;


        @ApiModelProperty(value = "分组人员")
        private List<GroupUser> list;

    }

    @Data
    public static class GroupUser{

        @ApiModelProperty(value = "人员")
        private SysUserVO userVO;

        @ApiModelProperty(value = "等级")
        private String level;

    }

}
