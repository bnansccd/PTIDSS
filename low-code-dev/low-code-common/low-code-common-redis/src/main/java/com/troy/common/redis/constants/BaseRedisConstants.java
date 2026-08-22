package com.troy.common.redis.constants;

/**
 * @author chenxl
 * @date 2023/10/16
 */
public class BaseRedisConstants {


    /**
     * 代码系统前缀
     */
    public static final String LC = "lc:";

    /**
     * 域名前缀
     */
    public static final String DOMAIN = LC + "domain:";


    public static final String TENANT_DOMAIN = LC + "tenant_domain:";


    public static final String CONFIG = LC + "config:";


    public static final String MSG_TIME = LC + "MSG_TIME:";


    public static final String REQUEST_ID = LC + "REQUEST_ID:";


    /**
     * 电子证照处理数量
     */
    public static final String SOURCE_DATA_DEAL_WITH_COUNT = "dzzz:source:data:deal:with:count:";


    /**
     * 部级电子证照对接token
     */
    public static final String MINISTRY_LOGIN_TOKEN = "ministry:login:token";

    /**
     * 部级电子证照对接token锁
     */
    public static final String MINISTRY_LOGIN_TOKEN_LOCK = "ministry:login:token:lock";


    public static final String ORG_INFO = LC + "ORG_INFO:";

    /**
     * 电子证照二维码
     */
    public static final String DZZZ_FILE_QRCODE = "dzzz:file:qrCODE:";

    /**
     * 电子证照人员照片
     */
    public static final String DZZZ_FILE_PERSONIMG = "dzzz:file:personImg:";

    /**
     * 电子证照车辆照片
     */
    public static final String DZZZ_FILE_CARIMG = "dzzz:file:carImg:";

    /**
     * 电子证照附件
     */
    public static final String DZZZ_FILE = "dzzz:file:";

    /**
     * 三灰九证，没有签章的数据KEY
     */
    public static final String DZZZ_SLJZ_NO_SIGN = "dzzz:sljz:no:sign:";

    /**
     * 执法登录token
     */
    public static final String ZHI_FA_LOGIN_TOKEN = "zhi:fa:login:token:";


    public static final String LC_SYNC_ORG = "lc:org:";


    public static final String LC_SYNC_USER = "lc:user:";


    public static final String BUSINESS_SCOPE = "business:scope:";

    public static final String MINISTRY_JS1001 = "ministry:js1001:";

    public static final String DZZZ_CUSTOMIZE_PUSH_TRACK_ID = "dzzz:customizePush:trackId:";
}
