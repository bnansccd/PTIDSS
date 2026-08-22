package com.troy.system.service;

import java.util.Map;

/**
 * @author sym
 * @description
 * @date 2023/12/1 17:28
 */
public interface TokenService {

    void checkHeader(Map<String, String> httpHeaders);

}
