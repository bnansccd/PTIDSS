package com.troy.common.oauth2.util;

import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author sym
 * @description
 * @date 2023/12/1 15:15
 */

public class HttpUtil {

    @SneakyThrows
    public static String getRequestBody(HttpServletRequest request){
        InputStream inputStream = request.getInputStream();
        byte[] buffer = new byte[1024];
        int bytesRead = -1;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toString();
    }

}
