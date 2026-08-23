package com.troy.camunda;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.servlet.ServletContext;
import java.io.InputStream;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/22 17:17:26
 * @Description: LowCodeCamundaApplicationTests
 * @Version: 1.0.0
 */
@SpringBootTest
public class LowCodeCamundaApplicationTests {

    @Autowired
    private ServletContext servletContext;

    @Test
    void context(){
        String path="/plugin/cockpit/app/plugin.css";
        InputStream resourceAsStream = servletContext.getResourceAsStream(path);
        System.err.println(resourceAsStream);
    }
}
