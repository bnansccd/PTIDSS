package com.troy.job.task;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.troy.common.core.utils.DateUtils;
import com.troy.job.domain.DTO.SyncDTO;
import com.troy.job.util.CronUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * @author chenxl
 * @description
 * @date 2024-06-19 9:19
 */
@Component("restTemplateTask")
public class RestTemplateTask {


    @Autowired
    private RestTemplate restTemplate;

    public String sendJsonMessage(String service, String url, String lessee, String json){
        String data = "http://"+service+url+"?lessee_x="+lessee;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;UTF-8"));
        HttpEntity<String> strEntity = new HttpEntity<String>(json, headers);

        return restTemplate.postForObject(data, strEntity, String.class);
    }

    public String sendGetMessage(String service, String url, String lessee, String urlParams){
        String data = "http://"+service+url+"?lessee_x="+lessee+"&"+urlParams;
        return restTemplate.getForObject(data, String.class);
    }


    public String sendJsonMessageAndTime(String service, String url, String lessee, String json, String corn)  {
        String data = "http://"+service+url+"?lessee_x="+lessee;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;UTF-8"));

//        String unescapedJson = json.replace("\\\"", "\"").replace("\\\\", "\\").replace("\\\"", "\"");
        SyncDTO dto = JSONObject.parseObject(json, SyncDTO.class);
        dto.setBeginTime(CronUtils.getLastExecution(corn));
        dto.setEndTime(CronUtils.getCurrentExecution(corn));

        HttpEntity<String> strEntity = new HttpEntity<String>(JSON.toJSONString(dto), headers);
        return restTemplate.postForObject(data, strEntity, String.class);
    }

    public String sendScriptJsonMessageAndTime(String service, String url, String lessee, String json, String corn)  {
        String data = "http://"+service+url+"?lessee_x="+lessee;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;UTF-8"));

//        String unescapedJson = json.replace("\\\"", "\"").replace("\\\\", "\\").replace("\\\"", "\"");
        Date lastExecution = CronUtils.getLastExecution(corn);
        Date currentExecution = CronUtils.getCurrentExecution(corn);

        json = json.replace("@BeginTime", DateUtils.parseDateToStr("yyyy-MM-dd", lastExecution));
        json = json.replace("@EndTime", DateUtils.parseDateToStr("yyyy-MM-dd", currentExecution));
        SyncDTO dto = JSONObject.parseObject(json, SyncDTO.class);
        dto.setBeginTime(lastExecution);
        dto.setEndTime(currentExecution);


        HttpEntity<String> strEntity = new HttpEntity<String>(JSON.toJSONString(dto), headers);
        return restTemplate.postForObject(data, strEntity, String.class);
    }

}
