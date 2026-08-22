package com.ptidss.decision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 发起决策会话请求（对齐 OpenAPI V1.0 /decision/sessions POST）
 */
@Data
public class DecisionSessionRequest {

    /** 类型：rolling/spot_quote/joint_optimize */
    private String sessionType;

    /** 交易日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date tradeDate;

    /** 情景：baseline/conservative/aggressive */
    private String scenario;

    /** 指定参与智能体，缺省全量 */
    private List<String> agents;
}
