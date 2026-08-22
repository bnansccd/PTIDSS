package com.troy.system.service.impl;//package com.troy.its.data.interceptor.service.impl;
//
//import com.troy.common.core.domain.ResultVO;
//import com.troy.common.core.enums.ResultEnum;
//import com.troy.common.core.exception.ServiceException;
//import com.troy.common.core.utils.StringUtils;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author sym
// * @description
// * @date 2023/12/1 17:28
// */
//
//public class TokenServiceImpl implements TokenService {
//
//
//    @Override
//    public void checkHeader(Map<String, String> httpHeaders) {
//        long timeStamp = Long.valueOf(httpHeaders.get("timestamp"));
//        String sign = httpHeaders.get("sign");
//        String orgId = httpHeaders.get("orgid");
//        if(StringUtils.isNull(orgId) || StringUtils.isNull(timeStamp) || StringUtils.isNull(sign)){
//            throw new ServiceException(ResultEnum.ERROR,"参数");
//        }
//        long reqeustInterval = System.currentTimeMillis() - Long.valueOf(timeStamp);
//        if(reqeustInterval > 5 * 60 * 1000){
//            throw new ServiceException(ResultEnum.EXPIRE,"timestamp");
//        }
//        // 1\. 根据appId查询数据库获取appSecret
//        ApiSecretEntity oneByOrgId = apiSecretService.getOneByOrgId(orgId);
//        if(oneByOrgId==null){
//            throw new ServiceException(ResultEnum.NOT_FOUND,"组织id");
//        }
//        // 2\. 校验签名
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("timeStamp", timeStamp);
//        hashMap.put("orgId",orgId);
//        hashMap.put("orgKey",oneByOrgId.getOrgKey());
//        String signature = MD5Util.mapEncry(hashMap);
//        log.info(signature);
//        if (!signature.equals(sign)) {
//            throw new ServiceException(ResultEnum.ERROR, "签名");
//        }
//        AccessToken token = getToken(hashMap);
//        return ResultVO.success(ResultEnum.SUCCESS,token);
//    }
//}
