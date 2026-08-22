//package com.troy.form;
//
//
//import com.baomidou.mybatisplus.annotation.FieldFill;
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.generator.FastAutoGenerator;
//import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
//import com.baomidou.mybatisplus.generator.config.OutputFile;
//import com.baomidou.mybatisplus.generator.config.rules.DateType;
//import com.baomidou.mybatisplus.generator.fill.Column;
//import com.troy.common.datasource.entity.BaseEntity;
//
//import java.io.File;
//import java.util.Collections;
//
///**
// * @Auther: zhuqing
// * @Date: 2022/8/4 09:9:16
// * @Description: CodeGenerator
// * @Version: 1.0.0
// */
//// 演示例子，执行 main 方法控制台输入模块表名回车自动生成对应项目目录中
//public class CodeGenerator {
//
//    public static void main(String[] args) {
//        String projectPath = System.getProperty("user.dir");
//
//        // 1、数据源配置
//        DataSourceConfig.Builder datasourceBuilder = new DataSourceConfig.Builder(
//                "jdbc:mysql://192.168.88.193:3306/lc-gen?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8",
//                "root",
//                "zdwy123456"
//        );
//
//        // 2、创建代码生成器对象
//        FastAutoGenerator generator = FastAutoGenerator.create(datasourceBuilder);
//
//        // 3、全局配置
//        generator.globalConfig(builder -> {
//            // 设置作者
//            builder.author("zhuqing")
//                    // 定义生成的实体类中日期类型
//                    .dateType(DateType.ONLY_DATE)
//                    // 注释时间策略。
//                    .commentDate("yyyy/MM/dd HH:mm:ss")
//                    // 生成后是否打开资源管理器:否
//                    .disableOpenDir()
//                    // 开启 swagger 模式。如果开启，需要导入 swagger 的 pom 依赖
//                    //  .enableSwagger()
//                    // 指定输出目录
//                    .outputDir(projectPath + File.separator +
//                            "low-code-modules" + File.separator +
//                            "low-code-modules-gen" + File.separator + "src" + File.separator + "main" + File.separator + "java");
//        });
//
//        // 4、包配置
//        generator.packageConfig(builder -> {
//            // 父包名. ===========1.手动修改设置===========
//            builder.parent("com.troy")
//                    // 将要生成的模块名称. ===========2.手动修改设置===========
//                    .moduleName("gen")
//                    // 设置生成的 控制层 文件夹名称
//                    .controller("controller")
//                    // 设置生成的 实体类 文件夹名称
//                    .entity("entity")
//                    // 设置生成的 服务层 文件夹名称
//                    .service("service")
//                    // 设置生成的 映射层 文件夹名称
//                    .mapper("mapper")
//                    // mapper.xml 文件路径。单模块下，其他文件路径默认即可。 ;
//                    .pathInfo(Collections.singletonMap(OutputFile.xml, "E:\\ideaProject\\low-code\\low-code-modules\\low-code-modules-gen\\src\\main\\resources\\mapper"));
//        });
//
//        // 5、策略配置
//        generator.strategyConfig(builder -> {
//            // 设置数据库表名称. 如果不设置，则会将数据库中所有表都生成。（注意：需要与数据库中表名称一致，前缀也需添加）
//            // ===========3.手动修改设置。===========
//            builder.addInclude("t_gen_table_column","t_gen_table")
//                    // 过滤表前缀，生成的类名会去掉这个前缀
//                    .addTablePrefix("t_")
//
//                    // 第一阶段
//                    // 是否生成 entity：是
//                    .entityBuilder()
//                    // 开启lombok
//                    .enableLombok()
//                    .superClass(BaseEntity.class)
//                    .addSuperEntityColumns("id", "create_id", "create_time", "modify_id", "modify_time", "del_flag")
//                    // 设置生成的实体类名称. 默认配置不带 Entity 后缀，我习惯添加上
//                    .convertFileName(entityName -> entityName + "Entity")
//                    // 开启实体时字段注解。 会在生成的实体类的字段上，添加注解： @TableField("nickname")
//                    .enableTableFieldAnnotation()
//                    // 设置主键Id生成策略，设置为默认的雪花算法(ASSIGN_ID)
//                    .idType(IdType.ASSIGN_ID)
//                    // 逻辑删除字段名。（与数据库中字段对应）
//                    .logicDeleteColumnName("del_flag")
//                    // 逻辑删除属性名。（定义在实体中的属性）。 会在生成的实体类的字段上，添加注解：@TableLogic
//                    .logicDeletePropertyName("delFlag")
//                    // 会在实体类的该字段上追加注解[@TableField(value = "create_time", fill = FieldFill.INSERT)]
//                    .addTableFills(new Column("create_time", FieldFill.INSERT))
//                    .addTableFills(new Column("create_id", FieldFill.INSERT))
//                    // 会在实体类的该字段上追加注解[@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)]
//                    .addTableFills(new Column("modify_time", FieldFill.INSERT_UPDATE))
//                    .addTableFills(new Column("modify_id", FieldFill.INSERT_UPDATE))
//
//                    // 第二阶段
//                    .mapperBuilder()
//                    // 开启 @Mapper 注解。 会在生成的 mapper 类上，添加注解：@Mapper
//                    .enableMapperAnnotation()
//                    // 启用 BaseResultMap 生成。 会在 mapper.xml文件生成[通用查询映射结果]配置。
//                    .enableBaseResultMap()
//                    // 启用 BaseColumnList。 会在mapper.xml文件生成[通用查询结果列 ]配置
//                    .enableBaseColumnList()
//
//                    // 第三阶段
//                    .serviceBuilder()
//                    // 设置生成的实体类名称。 默认配置名称前有个I，我习惯添去掉
//                    .convertServiceFileName(serviceName -> serviceName + "Service")
//                    // 第四阶段
//                    .controllerBuilder()
//                    // 开启 @RestController 注解。 会在生成的 Controller 类上，添加注解：@RestController
//                    .enableRestStyle()
//
//                    // 开启驼峰转连字符
//                    .enableHyphenStyle();
//        });
//
//        // 6. 模板引擎配置，默认为 Velocity , 可选模板引擎 Freemarker 或 Beetl
//        // generator.templateEngine(new FreemarkerTemplateEngine());
//
//        generator.execute();
//    }
//
//}
