package com.troy.common.security.utils;

import com.alibaba.fastjson2.JSONArray;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.utils.SpringUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.redis.service.RedisService;
import com.troy.system.api.RemoteDictService;
import com.troy.system.api.domain.VO.SysDictVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Auther: zhuqing
 * @Date: 2022/7/29 14:14:49
 * @Description: 字典工具类
 * @Version: 1.0.0
 */
public class DictUtils {

    /**
     * 设置字典缓存
     *
     * @param key       参数键
     * @param dictDatas 字典数据列表
     */
    public static void setDictCache(String key, List<SysDictVO> dictDatas) {
        SpringUtils.getBean(RedisService.class).setCacheObject(getCacheKey(key), dictDatas);
    }

    /**
     * 获取字典缓存
     *
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public static List<SysDictVO> getDictCache(String key) {
        List<SysDictVO> vos = new ArrayList<>();
        JSONArray arrayCache = SpringUtils.getBean(RedisService.class).getCacheObject(getCacheKey(key));
        if (StringUtils.isNotNull(arrayCache)) {
            vos = arrayCache.toList(SysDictVO.class);
        } else {
            ResultVO<List<SysDictVO>> resultVO = SpringUtils.getBean(RemoteDictService.class).getSysDictByParentType(key, SecurityConstants.INNER);
            if (resultVO.getCode() == ResultVO.SUCCESS) {
                vos = resultVO.getData();
            }
        }
        return vos;
    }

    /**
     * 删除指定字典缓存
     *
     * @param key 字典键
     */
    public static void removeDictCache(String key) {
        SpringUtils.getBean(RedisService.class).deleteObject(getCacheKey(key));
    }

    /**
     * 清空字典缓存
     */
    public static void clearDictCache() {
        Set<String> keys = SpringUtils.getBean(RedisService.class).keys(Constants.SYS_DICT_KEY + "*");
        SpringUtils.getBean(RedisService.class).deleteObject(keys);
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey) {
        return Constants.SYS_DICT_KEY + configKey;
    }

}
