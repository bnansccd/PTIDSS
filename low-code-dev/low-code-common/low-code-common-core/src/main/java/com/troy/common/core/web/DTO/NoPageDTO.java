package com.troy.common.core.web.DTO;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/12 17:17:31
 * @Description: NoPageDto
 * @Version: 1.0.0
 */
@ApiModel(description = "不分页参数")
public class NoPageDTO implements Serializable {

    /**
     * 数据权限参数
     */
    private Map<String, String> params = new HashMap<>();

//    /**
//     * 排序参数
//     */
//    private List<OrderItem> orderItems = new ArrayList<>();

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

//    public List<OrderItem> getOrderItems() {
//        return orderItems;
//    }
//
//    public void setOrderItems(List<OrderItem> orderItems) {
//        this.orderItems = orderItems;
//    }
}
