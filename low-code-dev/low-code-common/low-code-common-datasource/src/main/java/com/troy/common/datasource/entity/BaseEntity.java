package com.troy.common.datasource.entity;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.keygen.KeyGenerators;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/4 11:11:23
 * @Description: 实体基类
 * @Version: 1.0.0
 */
public class BaseEntity implements Serializable {

    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = "myIdWork")
    private Long id;

    /**
     * 创建人id
     */
    @Column(value = "create_id")
    private Long createId;

    /**
     * 创建人
     */
    @Column(ignore = true)
    private String createName;

    /**
     * 创建部门id
     */
    @Column(value = "create_depart_id")
    private Long createDepartId;

    /**
     * 创建部门
     */
    @Column(ignore = true)
    private String createDepartName;

    /**
     * 创建时间
     */
    @Column(value = "create_time")
    private Date createTime;

    /**
     * 修改人id
     */
    @Column(value = "modify_id")
    private Long modifyId;

    /**
     * 修改人
     */
    @Column(ignore = true)
    private String modifyName;

    /**
     * 修改部门id
     */
    @Column(value = "modify_depart_id")
    private Long modifyDepartId;

    /**
     * 修改部门
     */
    @Column(ignore = true)
    private String modifyDepartName;

    /**
     * 修改时间
     */
    @Column(value = "modify_time")
    private Date modifyTime;

    /**
     * 删除标识0有效数据1逻辑删除
     */
    @Column(value = "del_flag", isLogicDelete = true)
    private Integer delFlag = 0;

    /**
     * 乐观锁
     */
    @Column(version = true,onInsertValue = "0")
    private Long version = 0L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public Long getCreateDepartId() {
        return createDepartId;
    }

    public void setCreateDepartId(Long createDepartId) {
        this.createDepartId = createDepartId;
    }

    public String getCreateDepartName() {
        return createDepartName;
    }

    public void setCreateDepartName(String createDepartName) {
        this.createDepartName = createDepartName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getModifyId() {
        return modifyId;
    }

    public void setModifyId(Long modifyId) {
        this.modifyId = modifyId;
    }

    public String getModifyName() {
        return modifyName;
    }

    public void setModifyName(String modifyName) {
        this.modifyName = modifyName;
    }

    public Long getModifyDepartId() {
        return modifyDepartId;
    }

    public void setModifyDepartId(Long modifyDepartId) {
        this.modifyDepartId = modifyDepartId;
    }

    public String getModifyDepartName() {
        return modifyDepartName;
    }

    public void setModifyDepartName(String modifyDepartName) {
        this.modifyDepartName = modifyDepartName;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
