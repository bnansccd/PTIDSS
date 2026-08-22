package com.troy.gen.template;

import com.jfinal.template.Engine;
import com.jfinal.template.expr.ast.FieldGetters;
import com.jfinal.template.source.ClassPathSource;
import com.jfinal.template.source.FileSource;
import com.jfinal.template.source.ISource;
import com.jfinal.template.source.ISourceFactory;
import com.mybatisflex.codegen.template.ITemplate;
import com.mybatisflex.codegen.template.impl.EnjoyTemplate;
import com.mybatisflex.core.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/11 14:14:38
 * @Description: MyTemplate
 * @Version: 1.0.0
 */
public class MyTemplate implements ITemplate {


    private static final String engineName = "mybatis-flex";
    private final Engine engine;

    public MyTemplate() {
        Engine engine = Engine.use(engineName);
        if (engine == null) {
            engine = Engine.create(engineName, e -> {
                e.addSharedStaticMethod(StringUtil.class);
                e.setSourceFactory(new EnjoyTemplate.FileAndClassPathSourceFactory());
            });
            // 以下配置将支持 user.girl 表达式去调用 user 对象的 boolean isGirl() 方法
            Engine.addFieldGetterToFirst(new FieldGetters.IsMethodFieldGetter());
        }
        this.engine = engine;
    }


    @Override
    public void generate(Map<String, Object> params, String templateFilePath, File generateFile) {
        if (!generateFile.getParentFile().exists() && !generateFile.getParentFile().mkdirs()) {
            throw new IllegalStateException("Can not mkdirs by dir: " + generateFile.getParentFile());
        }
        // 开始生成文件
        try (FileOutputStream fileOutputStream = new FileOutputStream(generateFile)) {
            engine.getTemplate(templateFilePath).render(params, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void generate(Map<String, Object> params, String templateFilePath, ByteOutputStream outputStream) {
//        // 开始生成文件
//        try {
//            engine.getTemplate(templateFilePath).render(params, outputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 从文件或者类路径读取模板。
     *
     * @author 王帅
     */
    public static class FileAndClassPathSourceFactory implements ISourceFactory {

        @Override
        public ISource getSource(String baseTemplatePath, String fileName, String encoding) {
            // 先从文件寻找资源，找不到再从类路径寻找资源
            if (new File(fileName).exists()) {
                return new FileSource(baseTemplatePath, fileName, encoding);
            }
            return new ClassPathSource(baseTemplatePath, fileName, encoding);
        }

    }
}
