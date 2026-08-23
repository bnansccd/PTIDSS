package com.troy.common.core.utils;


import com.alibaba.fastjson2.JSON;
import com.troy.common.core.exception.user.HttpException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chenxl
 * @date 2023/11/21
 */
public class HttpUtil {

    /**
     * get请求-params传参
     *
     * @param url
     * @param params
     * @param headParams
     * @return
     */
    public static String doGet(String url, Map<String, String> params, Map<String, String> headParams) {
        //1.获取httpclient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        return doGet(url, params, headParams, httpClient);
    }

    public static String doGetIgnoreSSL(String url, Map<String, String> params, Map<String, String> headParams) {
        //1.获取httpclient
        CloseableHttpClient httpClient = createSSLClientDefault();
        return doGet(url, params, headParams, httpClient);
    }


    private static @Nullable String doGet(String url, Map<String, String> params, Map<String, String> headParams, CloseableHttpClient httpClient) {
        String result = null;

        //接口返回结果
        CloseableHttpResponse response = null;
        String paramStr = null;
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(url);
            if (StringUtils.isNotEmpty(params)) {
                sb.append("?");
                for (String key : params.keySet()) {
                    sb.append(key).append("=").append(StringUtils.isNotBlank(params.get(key)) ? URLEncoder.encode(params.get(key), "UTF-8") : params.get(key)).append("&");
                }
            }
            //2.创建get请求
            HttpGet httpGet = new HttpGet(sb.toString());
            //3.设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
            httpGet.setConfig(requestConfig);
//            httpGet.addHeader("content-type","text/xml");
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            httpGet.addHeader("Accept", "application/json, text/plain, */*");
            httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
            httpGet.addHeader("Connection", "keep-alive");
            httpGet.addHeader("User-Agent", "Mozilla/5.0"); // 添加必要的请求头
            if (StringUtils.isNotEmpty(headParams)) {
                for (String head : headParams.keySet()) {
                    httpGet.addHeader(head, headParams.get(head));
                }
            }
            httpGet.setConfig(RequestConfig.custom().setSocketTimeout(1000000).setConnectTimeout(1000000).build());
            //4.提交参数
            response = httpClient.execute(httpGet);
            //5.得到响应信息
            int statusCode = response.getStatusLine().getStatusCode();
            //6.判断响应信息是否正确
            if (HttpStatus.SC_OK != statusCode) {
                //终止并抛出异常
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            //7.转换成实体类
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                result = EntityUtils.toString(entity);
            }
            EntityUtils.consume(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //8.关闭所有资源连接
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * http post 请求
     */
    public static String doPost(String url, Map<String, String> params, Map<String, String> headParams) {
        String result = null;
        //1. 获取httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            //2. 创建post请求
            HttpPost httpPost = new HttpPost(url);

            //3.设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(100000).setConnectTimeout(100000).build();
            httpPost.setConfig(requestConfig);


            //4.提交参数发送请求
            if (StringUtils.isNotEmpty(params)) {
                List<BasicNameValuePair> paramsList = new ArrayList<>();
                for (String key : params.keySet()) {
                    paramsList.add(new BasicNameValuePair(key, params.get(key)));
                }
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(paramsList, HTTP.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);
            }
            //设置请求头
            if (StringUtils.isNotEmpty(headParams)) {
                for (String head : headParams.keySet()) {
                    httpPost.addHeader(head, headParams.get(head));
                }
            }
            response = httpClient.execute(httpPost);

            //5.得到响应信息
            int statusCode = response.getStatusLine().getStatusCode();
            //6. 判断响应信息是否正确
            if (HttpStatus.SC_OK != statusCode) {
                //结束请求并抛出异常
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode + ",msg:" + response.getEntity().toString());
            }
            //7. 转换成实体类
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
            EntityUtils.consume(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //8. 关闭所有资源连接
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * post请求-body传参
     *
     * @param url
     * @param params
     * @param headParams
     * @return
     */
    public static String doPostJson(String url, String params, Map<String, String> headParams) {
        String result = "";
        //1. 获取httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        if (url.startsWith("https://")) {
            httpClient = createSSLClientDefault();
        }
        CloseableHttpResponse response = null;
        try {
            //2. 创建post请求
            HttpPost httpPost = new HttpPost(url);

            //3.设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(1000000).setConnectTimeout(1000000).build();
            httpPost.setConfig(requestConfig);

            //4.提交参数发送请求
            if (StringUtils.isNotBlank(params)) {
                httpPost.setEntity(new StringEntity(params, ContentType.create("application/json", "utf-8")));
            }

            //设置请求头
            if (StringUtils.isNotEmpty(headParams)) {
                for (String head : headParams.keySet()) {
                    httpPost.addHeader(head, headParams.get(head));
                }
            }

            response = httpClient.execute(httpPost);

            //5.得到响应信息
            int statusCode = response.getStatusLine().getStatusCode();
            //6. 判断响应信息是否正确
            if (HttpStatus.SC_OK != statusCode) {
                //结束请求并抛出异常
                httpPost.abort();
                throw new HttpException("HttpClient,error status code :" + statusCode);
            }
            //7. 转换成实体类
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
            EntityUtils.consume(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //8. 关闭所有资源连接
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static InputStream postDownloadFileAsStream(String url, Map<String,Object> params,
                                                   Map<String, String> header) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;

        try {
            if (StringUtils.startsWith(url, "https")){
                httpClient = createSSLClientDefault();
            }else {
                HttpClients.createDefault();
            }
            HttpPost httpPost = new HttpPost(url);

            // 设置请求头
            if (StringUtils.isNotEmpty( header)){
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpPost.setHeader(HttpHeaders.ACCEPT, "application/octet-stream");

            // 设置请求体
            if (StringUtils.isNotEmpty(params)) {
                String jsonBody = JSON.toJSONString(params);
                StringEntity entity = new StringEntity(jsonBody, "UTF-8");
                httpPost.setEntity(entity);
            }

            // 执行请求
            response = httpClient.execute(httpPost);

            // 检查响应状态
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                EntityUtils.consume(response.getEntity());
                throw new RuntimeException("请求失败，状态码: " + statusCode);
            }

            // 返回输入流
            return response.getEntity().getContent();

        } catch (Exception e) {
            // 清理资源
            try {
                if (response != null) response.close();
                if (httpClient != null) httpClient.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("下载文件流失败", e);
        }
    }

    // 创建自定义的 SSL 上下文，用于绕过证书验证
    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有证书
                public boolean isTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                    return true;
                }
            }).build();
            // 创建主机名验证器，用于绕过主机名验证
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            // 创建 SSL 连接套接字工厂，将自定义的 SSL 上下文和主机名验证器应用于 HTTPS 连接
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);


            // 创建自定义的 CloseableHttpClient 实例，将 SSL 连接套接字工厂应用于 HTTP 客户端
            return HttpClients.custom()
                    .setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }
}
