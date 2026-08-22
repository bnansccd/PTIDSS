package com.troy.common.core.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author sym
 * @since 2024/11/13 17:08
 */
@Slf4j
public class OkHttpUtil {


    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded");
    private static final int CONNECT_TIMEOUT = 300;
    private static final int READ_TIMEOUT = 300;
    private static final int WRITE_TIMEOUT = 300;

    private static final OkHttpClient client;
    private static final OkHttpClient httpsClient; // 用于 HTTPS 请求

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
        httpsClient = createSSLClient();
    }

    /**
     * 发送 GET 请求（支持查询参数和请求头）
     *
     * @param url     请求地址
     * @param headers 请求头（可为 null）
     * @param params  查询参数（可为 null，会自动拼接到 URL 并编码）
     * @return 响应字符串
     */
    @SneakyThrows
    public static String get(String url, Map<String, String> headers, Map<String, String> params){
        // 1. 构建带查询参数的 URL
        String finalUrl = buildUrlWithParams(url, params);

        // 2. 构建 GET 请求
        Request.Builder requestBuilder = new Request.Builder()
                .url(finalUrl)
                .get(); // 指定 GET 方法

        // 3. 添加请求头
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // 4. 执行请求（复用客户端选择逻辑）
        OkHttpClient clientToUse = finalUrl.startsWith("https") ? httpsClient : client;
        try (Response response = clientToUse.newCall(requestBuilder.build()).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("GET 请求失败: " + response.code() + " " + response.message());
            }
            ResponseBody responseBody = response.body();
            return responseBody != null ? responseBody.string() : "";
        }
    }

    /**
     * 辅助方法：拼接 URL 和查询参数（自动编码特殊字符）
     */
    private static String buildUrlWithParams(String baseUrl, Map<String, String> params) throws UnsupportedEncodingException {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }

        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        // 判断 URL 中是否已有 "?"
        boolean hasQuery = baseUrl.contains("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() == null ? "" : entry.getValue();
            // 编码参数（避免特殊字符导致的 URL 错误）
            String encodedKey = URLEncoder.encode(key, "UTF-8");
            String encodedValue = URLEncoder.encode(value, "UTF-8");

            urlBuilder.append(hasQuery ? "&" : "?")
                    .append(encodedKey)
                    .append("=")
                    .append(encodedValue);
            hasQuery = true;
        }
        return urlBuilder.toString();
    }

    /**
     * 发送表单 POST 请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @param params  请求参数
     * @return 响应字符串
     * @throws IOException 网络异常
     */
    @SneakyThrows
    public static String postForm(String url, Map<String, Object> params, Map<String, String> headers) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                formBuilder.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        RequestBody formBody = formBuilder.build();
        return executeRequest(url, formBody, headers);
    }


    /**
     * 发送 JSON POST 请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @param params  请求参数
     * @return 响应字符串
     */
    @SneakyThrows
    public static String postJson(String url, Map<String, Object> params, Map<String, String> headers) {
        String jsonString = com.alibaba.fastjson2.JSON.toJSONString(params);
        RequestBody body = RequestBody.create(JSON, jsonString);
        return executeRequest(url, body, headers);
    }

    /**
     * 发送 JSON POST 请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @param jsonString  请求参数
     * @return 响应字符串
     */
    @SneakyThrows
    public static String postJson(String url, String jsonString, Map<String, String> headers) {
        RequestBody body = RequestBody.create(JSON, jsonString);
        return executeRequest(url, body, headers);
    }




    /**
     * 执行 HTTP/HTTPS 请求
     *
     * @param url         请求URL
     * @param requestBody 请求体
     * @param headers     请求头
     * @return 响应字符串
     * @throws IOException 网络异常
     */
    private static String executeRequest(String url, RequestBody requestBody, Map<String, String> headers) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);

        // 添加请求头
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder.build();
        OkHttpClient clientToUse = url.startsWith("https") ? httpsClient : client;

        try (Response response = clientToUse.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            ResponseBody responseBody = response.body();
            return responseBody != null ? responseBody.string() : "";
        }
    }


    @SneakyThrows
    public static OkHttpClient createSSLClient(String url) {
        if (StringUtils.startsWithIgnoreCase(url, "https")) {
            // 创建信任管理器
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }};
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return new OkHttpClient().newBuilder()
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(300, TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostName, session) -> true).build();
        }
        return new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS) // 连接超时时间
                .readTimeout(300, TimeUnit.SECONDS)    // 读取超时时间
                .writeTimeout(300, TimeUnit.SECONDS)   // 写入超时时间
                .retryOnConnectionFailure(true)       // 是否在连接失败时重试
                .build();

    }

    /**
     * 发送POST请求 FORM DATA
     *
     * @param url
     * @param params
     * @param headerMap
     * @return
     */
    public static String sendPost(String url,  Map<String, String> params, Map<String, String> headerMap) {
        String result = null;
        OkHttpClient client = createSSLClient(url);
        if (StringUtils.isEmpty(headerMap)) {
            headerMap = new HashMap<>();
        }
        FormBody.Builder formBody = new FormBody.Builder();
        if (StringUtils.isNotEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formBody.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .headers(Headers.of(headerMap))
                .build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
            log.info("发送请求, url: {}, params: {}, result: {}", url, com.alibaba.fastjson2.JSON.toJSONString(params), result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送请求失败, url: {}, params: {}, error: {}", url, com.alibaba.fastjson2.JSON.toJSONString(params), e.getMessage());
        }
        return result;
    }

    /**
     * 发送GET请求
     *
     * @param url
     * @param headerMap
     * @return
     */
    public static String sendGet(String url, Map<String, String> headerMap) {
        String result = null;
        try {
            OkHttpClient client = createSSLClient(url);
            if (StringUtils.isEmpty(headerMap)) {
                headerMap = new HashMap<>();
            }
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .headers(Headers.of(headerMap))
                    .build();
            Response response = client.newCall(request).execute();
            result = response.body().string();
            log.info("发送请求, url: {}, result: {}", url, result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送请求失败, url: {}, error: {}", url, e.getMessage());
        }
        return result;
    }


    @SneakyThrows
    public static OkHttpClient createSSLClient(){
        // 创建信任管理器
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
        }};
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        return new OkHttpClient().newBuilder()
                .sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0])
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .hostnameVerifier((hostName, session) -> true).build();
    }


}
