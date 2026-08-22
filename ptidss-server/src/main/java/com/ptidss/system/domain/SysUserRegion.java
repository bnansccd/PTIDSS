package com.ptidss.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户-区域授权（DDL 10.2 sys_user_region，评审决议⑤：角色 × 区域双重授权）
 */
@Data
@TableName("sys_user_region")
public class SysUserRegion implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String regionCode;
}
