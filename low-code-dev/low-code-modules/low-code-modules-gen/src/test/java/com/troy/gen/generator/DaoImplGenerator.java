package com.troy.gen.generator;

import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.PackageConfig;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.codegen.generator.IGenerator;
import com.troy.gen.config.DaoImplConfig;
import com.troy.gen.template.MyTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/11 11:11:46
 * @Description: Generator
 * @Version: 1.0.0
 */
public class DaoImplGenerator implements IGenerator {

    private String templatePath;

    public DaoImplGenerator() {
        this("/templates/enjoy/daoImpl.tpl");
    }

    public DaoImplGenerator(String templatePath) {
        this.templatePath = templatePath;
    }


    @Override
    public void generate(Table table, GlobalConfig globalConfig) {
        PackageConfig packageConfig = globalConfig.getPackageConfig();
        DaoImplConfig daoImplConfig = (DaoImplConfig) globalConfig.getCustomConfig("daoImpl");
        Map<String, Object> params = new HashMap<>(3);
        params.put("table", table);
        params.put("daoImplConfig", daoImplConfig);
        params.put("packageConfig", packageConfig);
        params.put("javadocConfig", globalConfig.getJavadocConfig());
        MyTemplate template = (MyTemplate) globalConfig.getTemplateConfig().setTemplate(new MyTemplate()).getTemplate();
//        ByteOutputStream out=new ByteOutputStream();
//        template.generate(params,templatePath,out);
    }

    @Override
    public String getTemplatePath() {
        return this.templatePath;
    }

    @Override
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }
}
