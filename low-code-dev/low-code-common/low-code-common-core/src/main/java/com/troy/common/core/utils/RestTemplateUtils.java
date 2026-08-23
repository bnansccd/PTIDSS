package com.troy.common.core.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @ClassName RestTemplateUtils
 * @Author ZhuQing
 * @Date: 2024/5/11  11:08
 * @Description:
 */
@Slf4j
@Component
public class RestTemplateUtils {

    private static RestTemplate restTemplate;

    @Autowired
    public RestTemplateUtils(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * post请求发送form-data格式的数据
     *
     * @param url        请求url
     * @param param      请求参数
     * @param headersMap 配置header
     * @param clazz      请求类型
     * @return
     */
    public <T> T doPostFormData(String url, Map<String, Object> param, Map<String, String> headersMap, Class<T> clazz) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        if (!CollectionUtils.isEmpty(headersMap)) {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                headers.set(entry.getKey(), entry.getValue());
            }
        }
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
        try {
            //表单中其他参数
            if (!CollectionUtils.isEmpty(param)) {
                for (Map.Entry<String, Object> entry : param.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof File) {
                        File file = (File) value;
                        FileSystemResource fileResource = new FileSystemResource(file);
                        body.add(entry.getKey(), fileResource);
                    } else {
                        body.add(entry.getKey(), value);
                    }
                }
            }

            //用HttpEntity封装整个请求报文
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
            // 执行POST请求
            ResponseEntity<String> res = restTemplate.postForEntity(url, entity, String.class);// 执行提交
            log.info("res:{}", res.getBody());
            return JSON.parseObject(res.getBody()).toJavaObject(clazz);
        } catch (Exception e) {
            log.error("调用HttpPost失败！", e);
        }
        return null;
    }

    /**
     * post请求发送form-data格式的数据
     *
     * @param url   请求url
     * @param param 请求参数
     * @param type  请求类型
     * @return
     */
    public <T> T doPostFormData(String url, Map<String, Object> param, Map<String, String> headersMap, TypeReference<T> type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        if (!CollectionUtils.isEmpty(headersMap)) {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                headers.set(entry.getKey(), entry.getValue());
            }
        }
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
        try {
            //表单中其他参数
            if (!CollectionUtils.isEmpty(param)) {
                for (Map.Entry<String, Object> entry : param.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof File) {
                        File file = (File) value;
                        FileSystemResource fileResource = new FileSystemResource(file);
                        body.add(entry.getKey(), fileResource);
                    } else {
                        body.add(entry.getKey(), value);
                    }
                }
            }

            //用HttpEntity封装整个请求报文
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
            // 执行POST请求
            ResponseEntity<String> res = restTemplate.postForEntity(url, entity, String.class);// 执行提交
            log.info("res:{}", res.getBody());
            return JSON.parseObject(res.getBody()).toJavaObject(type);
        } catch (Exception e) {
            log.error("调用HttpPost失败！", e);
        }
        return null;
    }

    /**
     * post请求发送json格式的数据
     *
     * @param url        请求url
     * @param param      请求参数
     * @param headersMap 配置header
     * @param clazz      请求类型
     * @return
     */
    public <T> T doPostJson(String url, Map<String, Object> param, Map<String, String> headersMap, Class<T> clazz) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!CollectionUtils.isEmpty(headersMap)) {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                headers.set(entry.getKey(), entry.getValue());
            }
        }
        try {

            //用HttpEntity封装整个请求报文
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity(param, headers);
            // 执行POST请求
            ResponseEntity<String> res = restTemplate.postForEntity(url, entity, String.class);// 执行提交
            log.info("res:{}", res.getBody());
            return JSON.parseObject(res.getBody()).toJavaObject(clazz);
        } catch (Exception e) {
            log.error("调用HttpPost失败！", e);
        }
        return null;
    }

    /**
     * post请求发送json格式的数据
     *
     * @param url   请求url
     * @param param 请求参数
     * @param type  请求类型
     * @return
     */
    public <T> T doPostJson(String url, Map<String, Object> param, Map<String, String> headersMap, TypeReference<T> type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!CollectionUtils.isEmpty(headersMap)) {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                headers.set(entry.getKey(), entry.getValue());
            }
        }
        try {

            //用HttpEntity封装整个请求报文
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity(param, headers);
            // 执行POST请求
            ResponseEntity<String> res = restTemplate.postForEntity(url, entity, String.class);// 执行提交
            log.info("res:{}", res.getBody());
            return JSON.parseObject(res.getBody()).toJavaObject(type);
        } catch (Exception e) {
            log.error("调用HttpPost失败！", e);
        }
        return null;
    }
}
