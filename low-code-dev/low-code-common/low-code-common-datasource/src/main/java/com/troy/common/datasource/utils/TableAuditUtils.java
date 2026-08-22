package com.troy.common.datasource.utils;

import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.utils.SpringUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.datasource.entity.BaseEntity;
import com.troy.system.api.RemoteSysUserService;
import com.troy.system.api.domain.DTO.AuditDTO;
import com.troy.system.api.domain.VO.AuditVO;
import com.troy.system.api.domain.VO.SysDepartVO;
import com.troy.system.api.domain.VO.SysUserVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/18 13:13:41
 * @Description: 数据表审计信息设置
 * @Version: 1.0.0
 */
public class TableAuditUtils {

    /**
     * 设置多条数据审计
     *
     * @param baseEntities
     */
    public static void setAuditInfo(List<? extends BaseEntity> baseEntities) {
        if (StringUtils.isNotEmpty(baseEntities)) {
            List<Long> userIds = new ArrayList<>();
            List<Long> departIds = new ArrayList<>();
            for (BaseEntity baseEntity : baseEntities) {
                //创建人
                if (StringUtils.isNotNull(baseEntity.getCreateId()) && !userIds.contains(baseEntity.getCreateId())) {
                    userIds.add(baseEntity.getCreateId());
                }
                //创建部门
                if (StringUtils.isNotNull(baseEntity.getCreateDepartId()) && !departIds.contains(baseEntity.getCreateDepartId())) {
                    departIds.add(baseEntity.getCreateDepartId());
                }
                //修改人
                if (StringUtils.isNotNull(baseEntity.getModifyId()) && !userIds.contains(baseEntity.getModifyId())) {
                    userIds.add(baseEntity.getModifyId());
                }
                //创建部门
                if (StringUtils.isNotNull(baseEntity.getModifyDepartId()) && !userIds.contains(baseEntity.getModifyDepartId())) {
                    departIds.add(baseEntity.getModifyDepartId());
                }
            }
            AuditDTO dto = new AuditDTO();
            dto.setUserIds(userIds);
            dto.setDepartIds(departIds);
            ResultVO<AuditVO> resultVO = SpringUtils.getBean(RemoteSysUserService.class).findAuditInfo(dto, SecurityConstants.INNER);
            if (resultVO.getCode() == ResultVO.SUCCESS) {
                AuditVO vo = resultVO.getData();
                if (StringUtils.isNotNull(vo)) {
                    List<SysUserVO> sysUserVOS = vo.getSysUserVOS();
                    List<SysDepartVO> sysDepartVOS = vo.getSysDepartVOS();
                    for (BaseEntity baseEntity : baseEntities) {
                        //设置创建人与修改人
                        if (StringUtils.isNotNull(baseEntity.getCreateId()) || StringUtils.isNotNull(baseEntity.getModifyId())) {
                            for (SysUserVO sysUserVO : sysUserVOS) {
                                if (sysUserVO.getId().equals(baseEntity.getCreateId())) {
                                    baseEntity.setCreateName(sysUserVO.getRealName());
                                }
                                if (sysUserVO.getId().equals(baseEntity.getModifyId())) {
                                    baseEntity.setModifyName(sysUserVO.getRealName());
                                }
                            }
                        }
                        //设置创建部门与修改部门
                        if (StringUtils.isNotNull(baseEntity.getCreateDepartId()) || StringUtils.isNotNull(baseEntity.getModifyDepartId())) {
                            for (SysDepartVO sysDepartVO : sysDepartVOS) {
                                if (sysDepartVO.getId().equals(baseEntity.getCreateDepartId())) {
                                    baseEntity.setCreateDepartName(sysDepartVO.getDepartName());
                                }
                                if (sysDepartVO.getId().equals(baseEntity.getModifyDepartId())) {
                                    baseEntity.setModifyDepartName(sysDepartVO.getDepartName());
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 设置单条数据审计
     *
     * @param baseEntity
     */
    public static void setAuditInfo(BaseEntity baseEntity) {
        if (StringUtils.isNotNull(baseEntity)) {
            List<Long> userIds = new ArrayList<>();
            List<Long> departIds = new ArrayList<>();
            //创建人
            if (StringUtils.isNotNull(baseEntity.getCreateId()) && !userIds.contains(baseEntity.getCreateId())) {
                userIds.add(baseEntity.getCreateId());
            }
            //创建部门
            if (StringUtils.isNotNull(baseEntity.getCreateDepartId()) && !departIds.contains(baseEntity.getCreateDepartId())) {
                departIds.add(baseEntity.getCreateDepartId());
            }
            //修改人
            if (StringUtils.isNotNull(baseEntity.getModifyId()) && !userIds.contains(baseEntity.getModifyId())) {
                userIds.add(baseEntity.getModifyId());
            }
            //创建部门
            if (StringUtils.isNotNull(baseEntity.getModifyDepartId()) && !userIds.contains(baseEntity.getModifyDepartId())) {
                departIds.add(baseEntity.getModifyDepartId());
            }

            AuditDTO dto = new AuditDTO();
            dto.setUserIds(userIds);
            dto.setDepartIds(departIds);
            ResultVO<AuditVO> resultVO = SpringUtils.getBean(RemoteSysUserService.class).findAuditInfo(dto, SecurityConstants.INNER);
            if (resultVO.getCode() == ResultVO.SUCCESS) {
                AuditVO vo = resultVO.getData();
                if (StringUtils.isNotNull(vo)) {
                    List<SysUserVO> sysUserVOS = vo.getSysUserVOS();
                    List<SysDepartVO> sysDepartVOS = vo.getSysDepartVOS();
                    //设置创建人与修改人
                    if (StringUtils.isNotNull(baseEntity.getCreateId()) || StringUtils.isNotNull(baseEntity.getModifyId())) {
                        for (SysUserVO sysUserVO : sysUserVOS) {
                            if (sysUserVO.getId().equals(baseEntity.getCreateId())) {
                                baseEntity.setCreateName(sysUserVO.getRealName());
                            }
                            if (sysUserVO.getId().equals(baseEntity.getModifyId())) {
                                baseEntity.setModifyName(sysUserVO.getRealName());
                            }
                        }
                    }
                    //设置创建部门与修改部门
                    if (StringUtils.isNotNull(baseEntity.getCreateDepartId()) || StringUtils.isNotNull(baseEntity.getModifyDepartId())) {
                        for (SysDepartVO sysDepartVO : sysDepartVOS) {
                            if (sysDepartVO.getId().equals(baseEntity.getCreateDepartId())) {
                                baseEntity.setCreateDepartName(sysDepartVO.getDepartName());
                            }
                            if (sysDepartVO.getId().equals(baseEntity.getModifyDepartId())) {
                                baseEntity.setModifyDepartName(sysDepartVO.getDepartName());
                            }
                        }
                    }

                }
            }
        }
    }
}
