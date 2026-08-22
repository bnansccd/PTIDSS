package com.ptidss.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ptidss.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LLM 模型配置（DDL 10.2 llm_model；V2.2 产品化：智能体可关联的生成式推理模型）
 * endpoint 为空时走内置模拟推理网关；api_key_ref 仅存密钥引用名，不落明文
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("llm_model")
public class LlmModel extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    /** 模型编码（智能体绑定用）：deepseek-v3/glm-4/qwen-plus 等 */
    private String modelCode;

    /** 模型名称 */
    private String modelName;

    /** 提供商：deepseek/glm/qwen/openai-compatible/local */
    private String provider;

    /** API 地址（空=内置模拟推理网关） */
    private String endpoint;

    /** 上游基础模型标识 */
    private String baseModel;

    /** 采样温度 */
    private java.math.BigDecimal temperature;

    /** 最大输出 token */
    private Integer maxTokens;

    /** 密钥引用名（配置项，不落明文） */
    private String apiKeyRef;

    /** 状态：enabled/disabled */
    private String status;
}
