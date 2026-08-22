package com.troy.common.core.utils.poi;

/**
 * @Author ZhuQing
 * @Date: 2022/7/6  13:21
 * Excel数据格式处理适配器
 */
public interface ExcelHandlerAdapter {
    /**
     * 格式化
     *
     * @param value 单元格数据值
     * @param args  excel注解args参数组
     * @return 处理后的值
     */
    Object format(Object value, String[] args);
}
