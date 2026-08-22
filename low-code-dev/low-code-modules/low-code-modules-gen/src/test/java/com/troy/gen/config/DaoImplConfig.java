package com.troy.gen.config;

import com.troy.common.datasource.service.impl.BaseServiceImpl;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/11 13:13:13
 * @Description: 生成 DaoImpl 的配置。
 * @Version: 1.0.0
 */
public class DaoImplConfig {

    /**
     * ServiceImpl 类的前缀。
     */
    private String classPrefix = "";

    /**
     * ServiceImpl 类的后缀。
     */
    private String classSuffix = "DaoImpl";

    /**
     * 自定义 ServiceImpl 的父类。
     */
    private Class<?> superClass = BaseServiceImpl.class;

    /**
     * 是否覆盖之前生成的文件。
     */
    private boolean overwriteEnable;

    /**
     * 是否生成缓存样例代码。
     */
    private boolean cacheExample;

    public String buildSuperClassImport() {
        if (superClass == null) {
            return "com.troy.common.datasource.service.impl.BaseServiceImpl";
        }
        return superClass.getName();
    }

    public String buildSuperClassName() {
        if (superClass == null) {
            return "BaseServiceImpl";
        }
        return superClass.getSimpleName();
    }

    /**
     * 获取类前缀。
     */
    public String getClassPrefix() {
        return classPrefix;
    }

    /**
     * 设置类前缀。
     */
    public DaoImplConfig setClassPrefix(String classPrefix) {
        this.classPrefix = classPrefix;
        return this;
    }

    /**
     * 获取类后缀。
     */
    public String getClassSuffix() {
        return classSuffix;
    }

    /**
     * 设置类后缀。
     */
    public DaoImplConfig setClassSuffix(String classSuffix) {
        this.classSuffix = classSuffix;
        return this;
    }

    /**
     * 获取父类。
     */
    public Class<?> getSuperClass() {
        return superClass;
    }

    /**
     * 设置父类。
     */
    public DaoImplConfig setSuperClass(Class<?> superClass) {
        this.superClass = superClass;
        return this;
    }

    /**
     * 是否覆盖原有文件。
     */
    public boolean isOverwriteEnable() {
        return overwriteEnable;
    }

    /**
     * 设置是否覆盖原有文件。
     */
    public DaoImplConfig setOverwriteEnable(boolean overwriteEnable) {
        this.overwriteEnable = overwriteEnable;
        return this;
    }

    /**
     * 是否生成缓存例子。
     */
    public boolean isCacheExample() {
        return cacheExample;
    }

    /**
     * 设置生成缓存例子。
     */
    public DaoImplConfig setCacheExample(boolean cacheExample) {
        this.cacheExample = cacheExample;
        return this;
    }
}
