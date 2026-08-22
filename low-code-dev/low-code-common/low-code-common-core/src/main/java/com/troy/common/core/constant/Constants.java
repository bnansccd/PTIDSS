package com.troy.common.core.constant;

/**
 * @Author ZhuQing
 * @Date: 2022/7/5  17:17
 * 通用常量信息
 */
public class Constants {


    public static final String TRUE = "0";

    public static final String FALSE = "1";

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * RMI 远程方法调用
     */
    public static final String LOOKUP_RMI = "rmi:";

    /**
     * LDAP 远程方法调用
     */
    public static final String LOOKUP_LDAP = "ldap:";

    /**
     * LDAPS 远程方法调用
     */
    public static final String LOOKUP_LDAPS = "ldaps:";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;

    /**
     * 登录成功状态
     */
    public static final String LOGIN_SUCCESS_STATUS = "0";

    /**
     * 登录失败状态
     */
    public static final String LOGIN_FAIL_STATUS = "1";

    /**
     * 字符串0
     */
    public static final String ZERO_STR = "0";

    /**
     * 字符串1
     */
    public static final String ONE_STR = "1";

    /**
     * 字符串 2
     */
    public static final String TWO_STR = "2";

    /**
     * 字符串 3
     */
    public static final String THREE_STR = "3";

    /**
     * 字符串 4
     */
    public static final String FOUR_STR = "4";

    /**
     * 字符串 4
     */
    public static final String FIVE_STR = "5";

    /**
     * 字符串 4
     */
    public static final String SIX_STR = "6";


    /**
     * 字符串9
     */
    public static final String NINE_STR = "9";


    /**
     * 9
     */
    public static final Integer NINE = 9;

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 验证码有效期（分钟）
     */
    public static final long CAPTCHA_EXPIRATION = 2;


    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 用户管理 cache key
     */
    public static final String SYS_USER_KEY = "sys_user:";

    /**
     * 部门管理 cache key
     */
    public static final String SYS_DEPART_KEY = "sys_depart:";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * 定时任务白名单配置（仅允许访问的包名，如其他需要可以自行添加）
     */
    public static final String[] JOB_WHITELIST_STR = {"com.troy"};

    /**
     * 定时任务违规的字符
     */
    public static final String[] JOB_ERROR_STR = {"java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml",
            "org.springframework", "org.apache", "com.troy.common.core.utils.file"};

    public static final Integer MINUS_ONE = -1;

    public static final String MINUS_ONE_STR = "-1";

    public static final Integer ZERO = 0;

    public static final Integer ONE = 1;

    public static final Integer TWO = 2;

    public static final Integer THREE = 3;

    public static final Integer FOUR = 4;

    public static final Integer FIVE = 5;

    public static final Integer TEN = 10;

    public static final String TENSTR = "10";

    public static final String TWENTY_ONE_STR = "21";

    public static final Integer THIRTY = 30;

    public static final Integer FIFTY = 30;

    public static final Integer HUNDRED = 100;

    public static final Integer ONE_THOUSAND = 1000;

    public static final Integer TEN_THOUSAND = 10000;

    public static final Integer FIVE_THOUSAND = 5000;

    public static final String APP_TOKEN = "app_token:";

    public static final String DOMAIN_NAME = ".roadmaintain.cn";

    public static final String STRING = "string";

    public static final String LONG = "long";

    public static final String DECIMAL = "bigDecimal";

    public static final String BYTES = "bytes";

    public static final String DATE = "date";

    public static final String NO = "NO";

    public static final String YES = "YES";

    public static final String COMMA = ",";

    /**
     * 电子证照根代码
     */
    public static final String ROOT_CODE = "1.2.156.3005.2";

    public static final String DOT = ".";

    public static final String VALIDATE_TYPE_REGX = "regx";

    public static final String VALIDATE_TYPE_DICT = "dict";

    public static final String NEW = "new";

    public static final String CHANGE = "change";

    public static final String CANCEL = "cancel";

    public static final String SLASH = "/";

    public static final String CAR_INFO_CACHE = "CAR_INFO_CACHE";

    public static final String ALL = "all";

    public static final String H = "H";

    public static final String M = "M";

    public static final String T = "T";

}
