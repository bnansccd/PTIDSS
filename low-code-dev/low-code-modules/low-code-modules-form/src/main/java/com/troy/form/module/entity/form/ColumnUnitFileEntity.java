package com.troy.form.module.entity.form;

import com.troy.common.core.constant.Constants;
import lombok.Data;

/**
 *  实体类。
 *
 * @author chenxl
 * @since 2023-11-02 15:42:17
 */
@Data
public class ColumnUnitFileEntity  {

    /**
     * 是否可以多选

     */
    private String isMultiple = Constants.FALSE;

    /**
     * 是否允许移动
     */
    private String isMove;

    /**
     * 最大数据量
     */
    private Integer maxNum;

    /**
     * 存储目录
     */
    private String storeUrl;

}
