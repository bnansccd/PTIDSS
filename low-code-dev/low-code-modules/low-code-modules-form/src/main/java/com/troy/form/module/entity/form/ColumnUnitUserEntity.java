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
public class ColumnUnitUserEntity  {

    /**
     * 是否多选
     */
    private String isMultiple = Constants.FALSE;

    /**
     * 使用储存编码
     */
    private String storeCode;

    /**
     * 自定义查询SQL
     */
    private String customSql;

    /**
     * 自定义排序SQL
     */
    private String sortSql;

}
