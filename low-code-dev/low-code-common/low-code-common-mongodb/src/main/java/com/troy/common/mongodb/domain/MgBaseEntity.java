package com.troy.common.mongodb.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.MongoId;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/16 11:11:40
 * @Description: MgBaseEntity
 * @Version: 1.0.0
 */
@Data
public class MgBaseEntity implements Serializable {

    /**
     * 主键
     */
    @MongoId
    private Long id;

    /**
     * 创建人id
     */
    private Long createId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人id
     */
    private Long modifyId;

    /**
     * 修改时间
     */
    private Date modifyTime;

    private Long createDepartId;
}
