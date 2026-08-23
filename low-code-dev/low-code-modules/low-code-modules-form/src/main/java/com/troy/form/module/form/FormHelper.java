package com.troy.form.module.form;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.utils.StringUtils;
import com.troy.form.entity.DbColumnEntity;
import com.troy.form.module.entity.form.*;
import com.troy.form.module.entity.form.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author chenxl
 * @date 2023/11/7
 */
public class FormHelper {

    private static HashMap<String, Class> CLASSES = new HashMap<>();

    private static HashMap<String, String> TYPE = new HashMap<>();

    static {
        CLASSES.put("2", ColumnUnitSingleLineEntity.class);
        CLASSES.put("3", ColumnUnitMultilineEntity.class);
        CLASSES.put("4", ColumnUnitNumberEntity.class);
        CLASSES.put("5", ColumnUnitPullEntity.class);
        CLASSES.put("6", ColumnUnitRaidoEntity.class);
        CLASSES.put("7", ColumnUnitCheckBoxEntity.class);
        CLASSES.put("8", ColumnUnitMultilineEntity.class);
        CLASSES.put("9", ColumnUnitDateEntity.class);
        CLASSES.put("10", ColumnUnitTimeEntity.class);
        CLASSES.put("11", ColumnUnitScoreEntity.class);
        CLASSES.put("12", ColumnUnitSlideEntity.class);
        CLASSES.put("16", ColumnUnitFileEntity.class);
        CLASSES.put("17", ColumnUnitPhotoEntity.class);
        CLASSES.put("18", ColumnUnitUserEntity.class);
        CLASSES.put("19", ColumnUnitDepartEntity.class);
        CLASSES.put("20", ColumnUnitPopEntity.class);
        CLASSES.put("21", ColumnUnitSignalEntity.class);
        CLASSES.put("22", ColumnUnitTreeEntity.class);

        TYPE.put(DictValueEnums.VARCHAR.getCode(), "2");
        TYPE.put(DictValueEnums.TEXT.getCode(), "3");
        TYPE.put(DictValueEnums.NUMBER.getCode(), "4");
        TYPE.put(DictValueEnums.DATE.getCode(), "9");
    }

    public static Class getTypeClass(DbColumnEntity columnEntity){
        if (DictValueEnums.VARCHAR.getCode().equals(columnEntity.getSystemDataType()) || DictValueEnums.TEXT.getCode().equals(columnEntity.getSystemDataType())){
            return String.class;
        }
        if (DictValueEnums.DATE.getCode().equals(columnEntity.getSystemDataType())){
            return Date.class;
        }
        if (DictValueEnums.NUMBER.getCode().equals(columnEntity.getSystemDataType())){
            if (columnEntity.getNumericScale() == null || columnEntity.getNumericScale() == 0){
                return Long.class;
            } else {
                return BigDecimal.class;
            }
        }
        throw new ServiceException(ResultEnum.NOT_FOUND, "系统支持类型");
    }

    public static String getDefaultJSON(String columnType){
        String s = TYPE.get(columnType);
        if (s == null){
            throw new ServiceException(ResultEnum.NOT_FOUND, "系统支持类型");
        }

        Class clazz = CLASSES.get(s);
        if (clazz == null){
            return null;
        }
        try {
            Object instance = clazz.newInstance();
            return JSONObject.toJSONString(instance, JSONWriter.Feature.WriteMapNullValue);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 校验
     * @param enums
     * @param data
     * @return
     */
    public static boolean checkData(DictValueEnums enums, String data){
        switch (enums){
            case COLUMN_VALIDATE_EMAIL:
                return isValidEmail(data);
            case COLUMN_VALIDATE_PHONE:
                return isValidPhoneNumber(data);
            case COLUMN_VALIDATE_NUMBER:
                return isNumeric(data);
            case COLUMN_VALIDATE_LETTER_LINE:
                return isLetterAndLine(data);
            case COLUMN_VALIDATE_LETTER_NUMBER_LINE:
                return isLetterAndNumberAndLine(data);
            case COLUMN_VALIDATE_NETWORK:
                return isNetWork(data);
            case COLUMN_VALIDATE_CHINESE:
                return isChinese(data);
            case COLUMN_VALIDATE_QQ:
                return isQQ(data);
            case COLUMN_VALIDATE_START_WITH_LETTER:
                return isStartWithLetter(data);
            case COLUMN_VALIDATE_INTEGER:
                return isInteger(data);
            case COLUMN_VALIDATE_POSITIVE_INTEGER:
                return isPositiveInteger(data);
            case COLUMN_VALIDATE_DATE:
                return isDate(data);
            case COLUMN_VALIDATE_TIME:
                return isTime(data);
            case COLUMN_VALIDATE_EMAIL_CODE:
                return zipCode(data);
            case COLUMN_VALIDATE_ID_CARD:
                return isIdCard(data);
            case COLUMN_VALIDATE_FIXED_PHONE:
                return isFixedPhone(data);
            default:
                return true;
        }
    }

    public static boolean isValidEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
            return Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", email);
        }
        return false;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (StringUtils.isNotBlank(phoneNumber)) {
            return Pattern.matches("^1[3-9]\\d{9}$", phoneNumber);
        }
        return false;
    }

    public static boolean isNumeric(String str) {
        if (StringUtils.isNotBlank(str)) {
            return Pattern.matches("^[0-9]*$", str);
        }
        return false;
    }

    public static boolean isLetterAndLine(String str) {
        if (StringUtils.isNotBlank(str)) {
            return Pattern.matches("^[A-Za-z_]+$", str);
        }
        return false;
    }

    public static boolean isLetterAndNumberAndLine(String str) {
        if (StringUtils.isNotBlank(str)) {
            return Pattern.matches("^[[0-9a-zA-Z_]\\\\s*]+$", str);
        }
        return false;
    }

    public static boolean isNetWork(String str) {
        if (StringUtils.isNotBlank(str)) {
            return Pattern.matches("^(https?:\\\\/\\\\/)?((\\\\d{1,3}\\\\.){3}\\\\d{1,3}|([\\\\da-z\\\\.-]+))(\\\\:\\\\d{2,5})?([\\\\/\\\\w \\\\.-]*)*\\\\/?$", str);
        }
        return false;
    }

    public static boolean isChinese(String str) {
        if (StringUtils.isNotBlank(str)) {
            return Pattern.matches("[\\u4e00-\\u9fa5]+", str);
        }
        return false;
    }

    public static boolean isStartWithLetter(String str) {
        if (StringUtils.isNotBlank(str)) {
            return Pattern.matches("^[A-Za-z].*", str);
        }
        return false;
    }

    public static boolean isQQ(String str) {
        if (StringUtils.isNotBlank(str)) {
            return Pattern.matches("[1-9]\\\\d[5,19]", str);
        }
        return false;
    }

    public static boolean isInteger(String str) {
        try {
            int i = Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String str) {
        try {
            int i = Integer.parseInt(str);
            return i > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static Boolean isDate(String date){
        List<String> pattern = new ArrayList<>();
        pattern.add("yyyy-MM-dd HH:mm:ss");
        pattern.add("yyyy-MM-dd");
        pattern.add("yyyyMMdd");
        pattern.add("yyyyMM");
        pattern.add("yyyy/MM/dd HH:mm:ss");
        pattern.add("yyyy/MM/dd");
        pattern.add("yyyy/MM");
        pattern.add("yyyy");

        for (String p : pattern){
            SimpleDateFormat format = new SimpleDateFormat(p);
            format.setLenient(true);
            try {
                format.parse(date);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private static Boolean isTime(String date){
        List<String> pattern = new ArrayList<>();
        pattern.add("HH:mm:ss");
        pattern.add("hh:mm:ss");

        for (String p : pattern){
            SimpleDateFormat format = new SimpleDateFormat(p);
            format.setLenient(true);
            try {
                format.parse(date);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean zipCode(String str) {
        if (StringUtils.isNotBlank(str)) {
            return Pattern.matches("/^(0[1-7]|1[0-356]|2[0-7]|3[0-6]|4[0-7]|5[1-7]|6[1-7]|7[0-5]|8[013-6])\\d{4}$/", str);
        }
        return false;
    }

    public static boolean isIdCard(String str){
        if (StringUtils.isNotBlank(str)){
            return Pattern.matches("/(^\\d{8}(0\\d|10|11|12)([0-2]\\d|30|31)\\d{3}$)|(^\\d{6}(18|19|20)\\d{2}(0\\d|10|11|12)([0-2]\\d|30|31)\\d{3}(\\d|X|x)$)/", str);
        }
        return false;
    }


    public static boolean isFixedPhone(String str){
        if (StringUtils.isNotBlank(str)){
            return Pattern.matches("/\\d{3}-\\d{8}|\\d{4}-\\d{7}/", str);
        }
        return false;
    }
}
