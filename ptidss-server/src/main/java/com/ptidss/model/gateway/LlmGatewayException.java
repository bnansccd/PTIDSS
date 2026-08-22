package com.ptidss.model.gateway;

/**
 * LLM 外部通道调用异常（连接失败/超时/非 2xx/响应结构异常）。
 * 由 LlmModelService 捕获并降级内置模拟推理，保证决策链路不中断。
 */
public class LlmGatewayException extends RuntimeException {

    public LlmGatewayException(String message) {
        super(message);
    }

    public LlmGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
