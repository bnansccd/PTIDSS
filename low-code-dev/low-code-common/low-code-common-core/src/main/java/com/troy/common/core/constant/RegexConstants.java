package com.troy.common.core.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * @Classname: RegexConstants
 * @Description: 正则表达式
 * @Date 2022/9/2
 * @Author: yzy
 * @Version
 **/
public class RegexConstants {
    public static final Logger LOGGER = LoggerFactory.getLogger(RegexConstants.class);

    /**
     * Z = 字母   S = 数字  T =特殊字符 非零开头包括0 正则
     */
    public static final String REGEX_ZST = "^(0|[1-9][0-9]*)$|^\\w+$";

    /**
     * Z = 字母   S = 数字 正则
     */

    public static final String REGEX_ZS = "[0-9A-Za-z]+";

    /**
     * Z = 字母
     */

    public static final String REGEX_Z = "[A-Za-z]+";

    /**
     * S = 数字 正则
     */

    public static final String REGEX_S = "[0-9]+";

    /**
     * S = 11位数字 正则
     */

    public static final String REGEX_S11 = "[0-9]{11}";
    /**
     * S =数字 非零开头包括0 正则
     */
    public static final String REGEX_SS = "[1-9]\\d*|0";

    /**
     * CHS = 汉字 正则
     */
    public static final String REGEX_CHS = "[\u4E00-\u9FA5]";

    /**
     * CHS = 手机号 正则
     */
    public static final String PHONE_CHS = "^1[1-9][0-9]{9}$";

    /**
     * CHS = 邮箱 正则
     */
    public static final String EMAIL_CHS = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * CHS = 身份证 正则
     */
    public static final String ID_CARD_CHS = "^[1-8][0-7]\\d{4}(?:19|20)\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])\\d{3}[\\dX]$";

    /**
     * 试题选项正则验证
     */
    public static final String ANSWER_REX = "^[a-zA-Z]{1}$";

    public static final String MULTI_ANSWER_REX = "^(?!.*((A.*){2,}|(B.*){2,}|(C.*){2,}|(D.*){2,})$)[A-D]{1,4}$";
    /**
     * 视频长度正则验证
     */
    public static final String VIDEO_LENGTH_REX = "^0[0-2]:[0-5][0-9]:[0-5][0-9]$";

    /**
     * 手机脱敏正则
     */
    public static final String DESENSITIZATION_REX = "(\\d{3})\\d{4}(\\d{4})";

    /**
     * 时间正则
     */
    public static final String TIME_TEX = "0[0-2]:[0-5][0-9]:[0-5][0-9]";

    /**
     * 日期正则
     */
    public static final String DATE_REX = "^[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30)))$";

    /**
     * 车牌号正则
     */
    public static final String CAR_NUM_REGEX = "^(([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5}[DF])|([DF]([A-HJ-NP-Z0-9])[0-9]{4})))|([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳使领]))$";

    /**
     * 驾驶证编号正则
     */
    public static final String DRIVE_CARD_REGEX = "[0-9]{12}";

    /**
     * 小数正则1位
     */
    public static final String SINOGRAM = "^(([1-9]\\d+)|([0]{1}))(\\.{0,1}([1-9]\\d){0,1})?$";

    /**
     * 小数正则1位null
     */
    public static final String SINOGRAMWITHNULL = "^\\s*$^(([1-9]\\d+)|([0]{1}))(\\.{0,1}([1-9]\\d){0,1})?$";

    /**
     * 小数正则2位
     */
    public static final String SINOGRAMTWO = "^(([1-9]\\d+)|([0]{1}))(\\.(\\d){0,2})?$";

    /**
     * cron表达式
     */
    public static final String CRON = "^\\s*($|#|\\w+\\s*=|(\\?|\\*|(?:[0-5]?\\d)(?:(?:-|\\/|\\,)(?:[0-5]?\\d))?(?:,(?:[0-5]?\\d)(?:(?:-|\\/|\\,)(?:[0-5]?\\d))?)*)\\s+(\\?|\\*|(?:[0-5]?\\d)(?:(?:-|\\/|\\,)(?:[0-5]?\\d))?(?:,(?:[0-5]?\\d)(?:(?:-|\\/|\\,)(?:[0-5]?\\d))?)*)\\s+(\\?|\\*|(?:[01]?\\d|2[0-3])(?:(?:-|\\/|\\,)(?:[01]?\\d|2[0-3]))?(?:,(?:[01]?\\d|2[0-3])(?:(?:-|\\/|\\,)(?:[01]?\\d|2[0-3]))?)*)\\s+(\\?|\\*|(?:0?[1-9]|[12]\\d|3[01])(?:(?:-|\\/|\\,)(?:0?[1-9]|[12]\\d|3[01]))?(?:,(?:0?[1-9]|[12]\\d|3[01])(?:(?:-|\\/|\\,)(?:0?[1-9]|[12]\\d|3[01]))?)*)\\s+(\\?|\\*|(?:[1-9]|1[012])(?:(?:-|\\/|\\,)(?:[1-9]|1[012]))?(?:L|W)?(?:,(?:[1-9]|1[012])(?:(?:-|\\/|\\,)(?:[1-9]|1[012]))?(?:L|W)?)*|\\?|\\*|(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?(?:,(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?)*)\\s+(\\?|\\*|(?:[0-6])(?:(?:-|\\/|\\,|#)(?:[0-6]))?(?:L)?(?:,(?:[0-6])(?:(?:-|\\/|\\,|#)(?:[0-6]))?(?:L)?)*|\\?|\\*|(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?(?:,(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?)*)(|\\s)+(\\?|\\*|(?:|\\d{4})(?:(?:-|\\/|\\,)(?:|\\d{4}))?(?:,(?:|\\d{4})(?:(?:-|\\/|\\,)(?:|\\d{4}))?)*))$";


    /**
     * url正则
     */
    public static final String URL_REGX = "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$";

    /**
     * 域名正则
     */
    public static final String DOMAIN_REGX = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";


    /**
     * 密码
     */
    public static final String PASSWORD_REGX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-.]).{8,}$";


    /**
     * 指定值是否与正则表达式匹配（完全相同）
     *
     * @param input
     * @param regex
     * @return
     */
    public static boolean matches(String input, String regex) {
        try {
            return Pattern.compile(regex).matcher(input).matches();
        } catch (Exception e) {
            LOGGER.error("正则表达式无效", e);
            return false;
        }

    }

    public static String[] split(String input, String regex) {
        try {
            return Pattern.compile(regex).split(input);
        } catch (Exception e) {
            LOGGER.error("正则表达式无效", e);
            return new String[0];
        }

    }
}
