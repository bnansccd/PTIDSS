package com.troy.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.troy.common.datasource.entity.BaseEntity;
import lombok.Data;

/**
 * @Auther: zhuqing
 * @Date: 2023/11/16 19:19:50
 * @Description: SysAreaEntity
 * @Version: 1.0.0
 */
@Data
@Table(value = "t_sys_area")
public class SysAreaEntity extends BaseEntity {

    /**
     * 城市编码
     */
    @Column("citycode")
    private String citycode;

    /**
     * 区域编码(街道没有独有的adcode，均继承父类（区县）的adcode)
     */
    @Column("adcode")
    private String adcode;

    /**
     * 行政区名称
     */
    @Column("name")
    private String name;

    /**
     * 行政区边界坐标点(当一个行政区范围，由完全分隔两块或者多块的地块组成，每块地的 polyline 坐标串以 | 分隔 。如北京 的 朝阳区)
     */
    @Column("polyline")
    private String polyline;

    /**
     * 经度
     */
    @Column("lon")
    private String lon;

    /**
     * 纬度
     */
    @Column("lat")
    private String lat;

    /**
     * country:国家province:省份（直辖市会在province显示）city:市（直辖市会在province显示）district:区县street:街道
     */
    @Column("level")
    private String level;

    /**
     * 父级编码
     */
    @Column("parent_code")
    private String parentCode;

    /**
     * 父级id
     */
    @Column("parent_id")
    private Long parentId;

    /**
     * 祖级编码，逗号隔开
     */
    @Column("ancestors_code")
    private String ancestorsCode;

    /**
     * 祖级id，逗号隔开
     */
    @Column("ancestors")
    private String ancestors;

    /**
     * 排序
     */
    @Column("sort")
    private Integer sort;
}
