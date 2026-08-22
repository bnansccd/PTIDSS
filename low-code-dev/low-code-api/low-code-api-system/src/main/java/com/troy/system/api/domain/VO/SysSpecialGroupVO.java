
package com.troy.system.api.domain.VO;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 公司产品表
 * </p>
 *
 * @author chenxl
 * @since 2023-03-14
 */
@Data
public class SysSpecialGroupVO{

    private Long id;

    private String groupName;

    private String groupPhone;

    private String workContent;

    private List<SysPostVO> leaderPost;

    private List<SysUserDetailsVO> leaders;

    private List<SysUserDetailsVO> viceLeaders;

    private List<SysUserDetailsVO> members;

}
