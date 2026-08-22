package com.ptidss;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 电力交易智能辅助决策系统（PTIDSS）服务端启动类
 * 架构参照 low-code-dev（low-code-common-security / low-code-auth）模式：
 * 统一鉴权（JWT + 令牌缓存）、注解式权限（@RequiresPermissions）、注解式审计（@Log）、
 * 三级权限（菜单/接口/数据）+ region 数据权限（评审决议⑤）
 */
@EnableAsync
@EnableScheduling
@MapperScan("com.ptidss.**.mapper")
@SpringBootApplication
public class PtidssApplication {

    public static void main(String[] args) {
        SpringApplication.run(PtidssApplication.class, args);
        System.out.println("===============================================");
        System.out.println("  PTIDSS Server 启动成功: http://localhost:9080/ptidss");
        System.out.println("===============================================");
    }
}
