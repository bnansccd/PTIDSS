package com.troy.gen.generator;

import com.mybatisflex.codegen.config.ControllerConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.PackageConfig;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.codegen.generator.IGenerator;
import com.troy.common.core.utils.IdWorkUtils;
import com.troy.gen.template.MyTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/13 16:16:19
 * @Description: ControllerGenerator
 * @Version: 1.0.0
 */
public class ControllerGenerator implements IGenerator
{
    private String templatePath;

    public ControllerGenerator() {
        this("/templates/enjoy/daoImpl.tpl");
    }

    public ControllerGenerator(String templatePath) {
        this.templatePath = templatePath;
    }


    @Override
    public void generate(Table table, GlobalConfig globalConfig) {
        PackageConfig packageConfig = globalConfig.getPackageConfig();
        ControllerConfig controllerConfig = (ControllerConfig) globalConfig.getCustomConfig("myController");
        Map<String, Object> params = new HashMap<>();

        params.put("table", table);
        params.put("packageConfig", packageConfig);
        params.put("controllerConfig", controllerConfig);
        params.put("javadocConfig", globalConfig.getJavadocConfig());
        params.put("withSwagger", globalConfig.isEntityWithSwagger());
        params.put("swaggerVersion", globalConfig.getSwaggerVersion());

        MyTemplate template = (MyTemplate) globalConfig.getTemplateConfig().setTemplate(new MyTemplate()).getTemplate();
//        ByteOutputStream out = new ByteOutputStream();
//        template.generate(params, templatePath, out);
    }

    @Override
    public String getTemplatePath() {
        return this.templatePath;
    }

    @Override
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public static void main(String[] args) {
        IdWorkUtils instance = IdWorkUtils.getInstance();
        for (int i = 0; i < 26; i++) {
            System.err.println(instance.nextId());
        }
    }
}
