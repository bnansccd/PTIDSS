package com.troy.sync.domain.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author chenxl
 * @description
 * @date 2024-06-19 16:14
 */
@Data
public class SyncScriptDTO {

    private String fromTarget;

    private String toTarget;

    private Long tenantId;

    /**
     * 脚本
     */
    private String script;

    private String toTable;

    private String[] AggregationParam;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

    /**
     * 使用默认值字段
     */
    private List<String> defaultAggregationParam;

}
