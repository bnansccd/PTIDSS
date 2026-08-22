package com.troy.common.swagger.annotation;

import com.troy.common.swagger.config.SwaggerAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/1 16:16:07
 * @Description: EnableCustomSwagger2
 * @Version: 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({SwaggerAutoConfiguration.class})
public @interface EnableCustomSwagger2 {

}
