package com.troy.job.util;

import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

/**
 * @Auther: zhuqing
 * @Date: 2022/9/28 15:15:54
 * @Description: cron表达式工具类
 * @Version: 1.0.0
 */
public class CronUtils {

    /**
     * 返回一个布尔值代表一个给定的Cron表达式的有效性
     *
     * @param cronExpression Cron表达式
     * @return boolean 表达式是否有效
     */
    public static boolean isValid(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }

    /**
     * 返回一个字符串值,表示该消息无效Cron表达式给出有效性
     *
     * @param cronExpression Cron表达式
     * @return String 无效时返回表达式错误描述,如果有效返回null
     */
    public static String getInvalidMessage(String cronExpression) {
        try {
            new CronExpression(cronExpression);
            return null;
        } catch (ParseException pe) {
            return pe.getMessage();
        }
    }

    /**
     * 返回下一个执行时间根据给定的Cron表达式
     *
     * @param cronExpression Cron表达式
     * @return Date 下次Cron表达式执行时间
     */
    public static Date getNextExecution(String cronExpression) {
        try {
            CronExpression cron = new CronExpression(cronExpression);
            return cron.getNextValidTimeAfter(new Date(System.currentTimeMillis()));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static Date getLastExecution(String cronExpression){
        CronExpression cron = null;
        try {
            cron = new CronExpression(cronExpression);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        //下次预计的执行时间
        Date next1 = cron.getNextValidTimeAfter(new Date());
        //下下次预计的执行时间
        Date next2 = cron.getNextValidTimeAfter(next1);
        //获取执行间隔
        long between = next2.getTime() - next1.getTime();
        // 返回 A 时间减去差值后的时间
        return new Date(next1.getTime() - 2 * between);
    }

    public static Date getCurrentExecution(String cronExpression){
        CronExpression cron = null;
        try {
            cron = new CronExpression(cronExpression);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        //下次预计的执行时间
        Date next1 = cron.getNextValidTimeAfter(new Date());
        //下下次预计的执行时间
        Date next2 = cron.getNextValidTimeAfter(next1);
        //获取执行间隔
        long between = next2.getTime() - next1.getTime();
        // 返回 A 时间减去差值后的时间
        return new Date(next1.getTime() - between);
    }
}
