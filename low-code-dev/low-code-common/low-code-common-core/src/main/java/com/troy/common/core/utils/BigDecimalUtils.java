package com.troy.common.core.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author: zhuQing
 * @date: 2021/1/28 0028 15:48
 * @describe:
 */
public class BigDecimalUtils {

    public static final DecimalFormat df = new DecimalFormat("0%");

    /**
     * 除法
     *
     * @param bigDecimal
     * @param bigDecimal1
     * @param scale
     * @param roundingMode
     * @return
     */
    public static BigDecimal divide(BigDecimal bigDecimal, BigDecimal bigDecimal1, int scale, int roundingMode) {
        if (StringUtils.isNotNull(bigDecimal) && StringUtils.isNotNull(bigDecimal1) && !bigDecimal1.stripTrailingZeros().equals(BigDecimal.ZERO)) {
            return bigDecimal.divide(bigDecimal1, scale, roundingMode).stripTrailingZeros();
        }
        return BigDecimal.ZERO;
    }

    /**
     * 乘法
     *
     * @param bigDecimal
     * @param bigDecimal1
     * @param scale
     * @param roundingMode
     * @return
     */
    public static BigDecimal multiply(BigDecimal bigDecimal, BigDecimal bigDecimal1, int scale, int roundingMode) {
        if (StringUtils.isNotNull(bigDecimal) && StringUtils.isNotNull(bigDecimal1)) {
            return bigDecimal.multiply(bigDecimal1).setScale(scale, roundingMode);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 加法
     *
     * @param bigDecimal
     * @param bigDecimal1
     * @param scale
     * @param roundingMode
     * @return
     */
    public static BigDecimal add(BigDecimal bigDecimal, BigDecimal bigDecimal1, int scale, int roundingMode) {
        if (StringUtils.isNotNull(bigDecimal) && StringUtils.isNotNull(bigDecimal)) {
            return bigDecimal.add(bigDecimal1).setScale(scale, roundingMode);
        }
        return BigDecimal.ZERO;
    }


    /**
     * 减法
     *
     * @param bigDecimal
     * @param bigDecimal1
     * @param scale
     * @param roundingMode
     * @return
     */
    public static BigDecimal subtract(BigDecimal bigDecimal, BigDecimal bigDecimal1, int scale, int roundingMode) {
        if (StringUtils.isNotNull(bigDecimal) && StringUtils.isNotNull(bigDecimal)) {
            return bigDecimal.subtract(bigDecimal1).setScale(scale, roundingMode);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 计算车速
     *
     * @param mileage 里程(km)
     * @param time    时间（秒）
     * @return
     */
    public static BigDecimal countCarSpeed(BigDecimal mileage, BigDecimal time) {
        if ((StringUtils.isNotNull(mileage) && mileage.compareTo(BigDecimal.ZERO) != 0) && (StringUtils.isNotNull(time) && time.compareTo(BigDecimal.ZERO) != 0)) {
            BigDecimal divide = mileage.divide(time, 2,BigDecimal.ROUND_DOWN);
            return divide;
        }
        return BigDecimal.ZERO;
    }

}
