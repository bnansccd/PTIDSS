package com.troy.gen.generator;

import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.PackageConfig;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.codegen.generator.IGenerator;
//import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.troy.gen.config.DaoConfig;
import com.troy.gen.template.MyTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/11 11:11:46
 * @Description: Generator
 * @Version: 1.0.0
 */
public class DaoGenerator implements IGenerator {

    private String templatePath;

    public DaoGenerator() {
        this("/templates/enjoy/dao.tpl");
    }

    public DaoGenerator(String templatePath) {
        this.templatePath = templatePath;
    }


    @Override
    public void generate(Table table, GlobalConfig globalConfig) {
        PackageConfig packageConfig = globalConfig.getPackageConfig();
        DaoConfig daoConfig = (DaoConfig) globalConfig.getCustomConfig("dao");
        Map<String, Object> params = new HashMap<>(3);
        params.put("table", table);
        params.put("daoConfig", daoConfig);
        params.put("packageConfig", packageConfig);
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
