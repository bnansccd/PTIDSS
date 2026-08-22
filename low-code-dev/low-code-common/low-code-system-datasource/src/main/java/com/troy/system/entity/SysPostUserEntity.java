package com.troy.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 岗位与用户的关系表
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Data
@Table("t_sys_post_user")
public class SysPostUserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(value = "post_id")
    private Long postId;

    @Column(value = "user_id")
    private Long userId;
}
