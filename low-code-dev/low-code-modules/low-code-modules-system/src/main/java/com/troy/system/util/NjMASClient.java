package com.troy.system.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

/**
 * 云MAS平台短信发送客户端 - Spring Boot版本
 */
@Slf4j
@Component
public class NjMASClient {

    @Value("${sms.api.url:http://112.33.46.17:37891/sms/tmpsubmit}")
    private String apiUrl;

    @Value("${sms.ec.name:四川恒创数字科技有限公司}")
    private String ecName;

    @Value("${sms.ap.id:njjt}")
    private String apId;

    @Value("${sms.secret.key:Szjt20260730.}")
    private String secretKey;

    @Value("${sms.sign:IgPXOhh3A}")
    private String sign;

    /**
     * 初始化日志打印配置信息（不打印敏感信息）
     */
    @PostConstruct
    public void init() {
        log.info("云MAS短信客户端初始化完成");
        log.info("API地址: {}", apiUrl);
        log.info("企业名称: {}", ecName);
        log.info("AP ID: {}", apId);
        log.info("签名: {}", sign);
        // 不打印secretKey
    }

    /**
     * 发送短信（有变量）
     */
    public String sendSmsWithParams(String templateId, String mobiles, List<String> params, String addSerial) throws Exception {
        // 构建请求JSON
        JSONObject requestJson = new JSONObject();
        requestJson.put("ecName", ecName);
        requestJson.put("apId", apId);
        requestJson.put("templateId", templateId);
        requestJson.put("mobiles", mobiles);

        // params: 将List转为JSON数组字符串
        String paramsStr = listToJsonArray(params);
        requestJson.put("params", paramsStr);

        requestJson.put("sign", sign);
        requestJson.put("addSerial", addSerial == null ? "" : addSerial);

        // 计算mac：拼接字符串
        String macStr = ecName + apId + secretKey + templateId + mobiles +
                paramsStr + sign + (addSerial == null ? "" : addSerial);
        String mac = md5(macStr);
        requestJson.put("mac", mac);

        // 获取原始JSON字符串
        String jsonData = requestJson.toString();

        // 调试日志（生产环境可调整为debug级别）
        log.debug("原始请求JSON: {}", jsonData);
        log.debug("MAC计算字符串: {}", macStr);
        log.debug("MAC值: {}", mac);

        // BASE64编码
        String base64Data = Base64.getEncoder().encodeToString(jsonData.getBytes(StandardCharsets.UTF_8));

        log.debug("BASE64编码后数据长度: {}", base64Data.length());

        // 发送请求
        String response = doPost(apiUrl, base64Data);
        log.info("短信发送响应: {}", response);

        return response;
    }

    /**
     * 发送短信（无变量）
     */
    public String sendSmsWithoutParams(String templateId, String mobiles, String addSerial) throws Exception {
        // 构建请求JSON
        JSONObject requestJson = new JSONObject();
        requestJson.put("ecName", ecName);
        requestJson.put("apId", apId);
        requestJson.put("templateId", templateId);
        requestJson.put("mobiles", mobiles);
        requestJson.put("params", "[]");  // 无变量传空数组
        requestJson.put("sign", sign);
        requestJson.put("addSerial", addSerial == null ? "" : addSerial);

        // 计算mac：拼接字符串
        String macStr = ecName + apId + secretKey + templateId + mobiles +
                "[]" + sign + (addSerial == null ? "" : addSerial);
        String mac = md5(macStr);
        requestJson.put("mac", mac);

        // 获取原始JSON字符串
        String jsonData = requestJson.toString();

        // 调试日志
        log.debug("原始请求JSON: {}", jsonData);
        log.debug("MAC计算字符串: {}", macStr);
        log.debug("MAC值: {}", mac);

        // BASE64编码
        String base64Data = Base64.getEncoder().encodeToString(jsonData.getBytes(StandardCharsets.UTF_8));

        log.debug("BASE64编码后数据长度: {}", base64Data.length());

        // 发送请求
        String response = doPost(apiUrl, base64Data);
        log.info("短信发送响应: {}", response);

        return response;
    }

    /**
     * 发送HTTP POST请求
     */
    private String doPost(String urlStr, String base64Data) throws Exception {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();

            // 设置请求头
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            // 发送BASE64编码后的数据
            try (OutputStream os = connection.getOutputStream()) {
                os.write(base64Data.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();

        } finally {
            if (reader != null) {
                try { reader.close(); } catch (Exception e) { /* ignore */ }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * MD5加密（32位小写）
     */
    private String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * 将List转为JSON数组字符串
     */
    private String listToJsonArray(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\"").append(list.get(i)).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 简易JSON对象
     */
    public static class JSONObject {
        private StringBuilder json = new StringBuilder("{");
        private boolean first = true;

        public void put(String key, String value) {
            if (!first) {
                json.append(",");
            }
            first = false;
            json.append("\"").append(key).append("\":\"");
            // 转义特殊字符
            String escaped = value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
            json.append(escaped).append("\"");
        }

        @Override
        public String toString() {
            json.append("}");
            return json.toString();
        }
    }
}