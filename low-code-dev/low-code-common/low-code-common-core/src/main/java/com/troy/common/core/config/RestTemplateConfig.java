package com.troy.common.core.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName RestTemplateConfig
 * @Author ZhuQing
 * @Date: 2024/5/11  11:03
 * @Description:
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 最大连接数
     */
    @Value("${http.maxTotal:100}")
    private Integer maxTotal;

    /**
     * 并发数
     */
    @Value("${http.defaultMaxPerRoute:20}")
    private Integer defaultMaxPerRoute;

    /**
     * 创建连接的最长时间
     */
    @Value("${http.connectTimeout:10000}")
    private Integer connectTimeout;

    /**
     * 从连接池中获取到连接的最长时间
     */
    @Value("${http.connectionRequestTimeout:500}")
    private Integer connectionRequestTimeout;

    /**
     * 数据传输的最长时间
     */
    @Value("${http.socketTimeout:10000}")
    private Integer socketTimeout;

    /**
     * 提交请求前测试连接是否可用（httpclient5 始终启用空闲连接校验，此配置仅保留兼容）
     */
    @Value("${http.staleConnectionCheckEnabled:true}")
    private boolean staleConnectionCheckEnabled;


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(httpRequestFactory());
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        // 最大连接数
        connectionManager.setMaxTotal(maxTotal);
        //单个路由最大连接数
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        // 最大空间时间
        connectionManager.setValidateAfterInactivity(TimeValue.ofMilliseconds(validateAfterInactivity));

        RequestConfig requestConfig = RequestConfig.custom()
                //服务器返回数据(response)的时间，超过抛出read timeout
                .setResponseTimeout(Timeout.ofMilliseconds(socketTimeout))
                //连接上服务器(握手成功)的时间，超出抛出connect timeout
                .setConnectTimeout(Timeout.ofMilliseconds(connectTimeout))
                //从连接池中获取连接的超时时间，超时间未拿到可用连接，会抛出ConnectionPoolTimeoutException
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionRequestTimeout))
                .build();

        //headers
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN"));
        headers.add(new BasicHeader("Connection", "Keep-Alive"));
        headers.add(new BasicHeader("Content-type", "application/json;charset=UTF-8"));

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .setDefaultHeaders(headers)
                // 保持长连接配置，需要在头添加Keep-Alive
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                //重试次数，默认是3次，没有开启
                .setRetryStrategy(new DefaultHttpRequestRetryStrategy(2, TimeValue.ofMilliseconds(100)))
                .build();
    }

    /**
     * 最大空间时间
     */
    @Value("${http.validateAfterInactivity:3000000}")
    private Integer validateAfterInactivity;
}
