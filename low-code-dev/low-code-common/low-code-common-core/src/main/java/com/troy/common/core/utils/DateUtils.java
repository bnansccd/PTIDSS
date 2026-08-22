package com.troy.common.core.utils;

import com.troy.common.core.constant.Constants;
import com.troy.common.core.domain.LocalDateRangeVO;
import com.troy.common.core.domain.TimePeriodVO;
import com.troy.common.core.enums.TimeGranularity;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author ZhuQing
 * @Date: 2022/7/6  09:56
 * 时间工具类
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYY年MM月DD日 = "yyyy年MM月dd日";

    public static String YYYY年MM月 = "YYYY年MM月";

    public static String YYYY年 = "YYYY年";

    public static String YYYYMMDD = "yyyyMMdd";

    public static String YYYYMMDD1 = "yyyy/MM/dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static String HHMMSS = "HHmmss";

    public static String YYYY年MM月DD日_HH_MM_SS = "yyyy年MM月dd日 HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM", "yyyyMMdd", YYYY年MM月DD日, YYYY年MM月, YYYY年, "yyyy年MM月dd日 HH:mm:ss", "dd/MM/yyyy HH:mm:ss"};


    // 时间格式化器（SimpleDateFormat非线程安全，多线程需用ThreadLocal包装）
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        try {
            return new SimpleDateFormat(format).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 增加 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor) {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor) {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }


    /**
     * 字符串转日期
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static Date strToDate(String dateStr, String format) {
        try {
            if (StringUtils.isNotBlank(dateStr) && StringUtils.isNotBlank(format)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                return simpleDateFormat.parse(dateStr);
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static Date getStartTimeOfDay(Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            // 将时分秒毫秒字段清零
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            return c.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getEndTimeOfDay(Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            // 将时分秒毫秒字段设置为最大值
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
            c.set(Calendar.MILLISECOND, 999);

            return c.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isBetweenDate(Date date, Date minDate, Date maxDate) {
        try {
            return date.after(minDate) && date.before(maxDate);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取两个时间之间的所有月份
     *
     * @param minDate
     * @param maxDate
     * @return
     */
    public static List<Date> getAllMonthsBetweenTwoDate(Date minDate, Date maxDate) {
        List<Date> result = new ArrayList<Date>();
        Calendar min = Calendar.getInstance();   //获取最小日期
        Calendar max = Calendar.getInstance();    //获取最大日期
        min.setTime(minDate);
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);   //最小日期的1号
        max.setTime(maxDate);
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);  //最大日期2号
        Calendar curr = min;   //初始赋值，从最小的开始
        while (curr.before(max)) {   //判断 是否大于 最大日期2号
            result.add(curr.getTime());   //放入list
            curr.add(Calendar.MONTH, 1);   //月 + 1
        }
        return result;
    }

    /**
     * 获取两个时间之间的所有月份
     *
     * @param minDate
     * @param maxDate
     * @return
     */
    public static List<String> getAllMonthsBetweenTwoDate(Date minDate, Date maxDate, String format) {
        List<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat(format);//格式化为年月
        Calendar min = Calendar.getInstance();   //获取最小日期
        Calendar max = Calendar.getInstance();    //获取最大日期
        min.setTime(minDate);
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);   //最小日期的1号
        max.setTime(maxDate);
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);  //最大日期2号
        Calendar curr = min;   //初始赋值，从最小的开始
        while (curr.before(max)) {   //判断 是否大于 最大日期2号
            result.add(sdf.format(curr.getTime()));   //放入list
            curr.add(Calendar.MONTH, 1);   //月 + 1
        }  //看到这里，就知道为什么需要定义最大日期的2号开始
        return result;
    }


    /**
     * 获取两个日期之间的所有日期
     *
     * @param minDate
     * @param maxDate
     * @return
     */
    public static List<Date> getAllDaysBetweenTwoDate(Date minDate, Date maxDate) {
        List<Date> result = new ArrayList<Date>();
        Calendar min = Calendar.getInstance();   //获取最小日期
        Calendar max = Calendar.getInstance();    //获取最大日期
        min.setTime(minDate);
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), min.get(Calendar.DATE));
        max.setTime(maxDate);
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), max.get(Calendar.DATE));
        max.add(Calendar.DATE, 1);
        Calendar curr = min;   //初始赋值，从最小的开始
        while (curr.before(max)) {
            result.add(curr.getTime());
            curr.add(Calendar.DATE, 1);
        }
        return result;
    }

    /**
     * 获取两个日期之间的所有日期
     *
     * @param minDate
     * @param maxDate
     * @return
     */
    public static List<String> getAllDaysBetweenTwoDate(Date minDate, Date maxDate, String format) {
        List<String> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(format);//格式化
        Calendar min = Calendar.getInstance();   //获取最小日期
        Calendar max = Calendar.getInstance();    //获取最大日期
        min.setTime(minDate);
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), min.get(Calendar.DATE));
        max.setTime(maxDate);
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), max.get(Calendar.DATE));
        max.add(Calendar.DATE, 1);
        Calendar curr = min;   //初始赋值，从最小的开始
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.DATE, 1);
        }
        return result;
    }


    /**
     * 计算两时间相差多少秒
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static Long getTime(Date startTime, Date endTime) {
        long sTime = startTime.getTime();
        long eTime = endTime.getTime();
        long diff = (eTime - sTime) / 1000;
        return diff;
    }

    /**
     * 计算两时间相差多少天
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static Integer getDay(Date startTime, Date endTime) {
        Integer day = null;
        if (StringUtils.isNotNull(startTime) && StringUtils.isNotNull(endTime)) {
            long diff = Math.abs(endTime.getTime() - startTime.getTime());
            day = (int) (diff / (24 * 60 * 60 * 1000));
        }
        return day;
    }

    /**
     * 调整指定日期号数
     *
     * @param startTime
     * @param day
     * @return
     */
    public static Date adjustNumber(Date startTime, Integer day) {
        if (StringUtils.isNull(day)) {
            day = 0;
        }
        if (StringUtils.isNull(startTime)) {
            return startTime;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /**
     * 调整指定日期月数
     *
     * @param startTime
     * @param month
     * @return
     */
    public static Date adjustMonth(Date startTime, Integer month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    /**
     * 调整指定日期年
     *
     * @param startTime
     * @param year
     * @return
     */
    public static Date adjustYear(Date startTime, Integer year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * 星冠时间格式转化
     *
     * @param timeStr
     * @return
     */
    public static Date getTransDate(String timeStr) {
        //自定义入参的格式化方式
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/M/d H:m:ss");
        //将字符串格式化为LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(timeStr, df);

        Date date = Date.from(localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
        return date;
    }

    /**
     * 判断两个时间是不是同一天
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Boolean ifOneDay(Date startDate, Date endDate) {
        if (StringUtils.isNull(startDate) || StringUtils.isNull(endDate)) {
            return Boolean.FALSE;
        }
        return StringUtils.equals(DateUtils.parseDateToStr(YYYY_MM_DD, startDate), DateUtils.parseDateToStr(YYYY_MM_DD, endDate));
    }


    /**
     * 查询循环周期，通过开始时间和结束时间和要queryTime，去获取queryTime所在的时间周期
     *
     * @param startDate
     * @param endDate
     * @param queryDate
     * @return
     */
    public static LocalDateRangeVO getCycle(LocalDate startDate, LocalDate endDate, LocalDate queryDate) {
        LocalDate cycleStartTime = calculateCycleStartTime(startDate, endDate, queryDate);
        LocalDate cycleEndTime = cycleStartTime.plusDays(endDate.getDayOfMonth() - startDate.getDayOfMonth());
        LocalDateRangeVO vo = new LocalDateRangeVO();
        vo.setStartDate(cycleStartTime);
        vo.setEndDate(cycleEndTime);
        return vo;
    }

    public static LocalDate calculateCycleStartTime(LocalDate startTime, LocalDate endTime, LocalDate queryTime) {
        // 计算周期长度
        int cycleLength = endTime.getDayOfMonth() - startTime.getDayOfMonth() + 1;

        // 计算查询时间距离起始周期开始的天数
        long daysBetween = ChronoUnit.DAYS.between(startTime, queryTime);

        // 计算查询时间所在的周期数
        long cycleCount = daysBetween / cycleLength;

        // 计算查询时间所在周期的开始时间
        LocalDate cycleStartTime = startTime.plusDays(cycleCount * cycleLength);

        // 如果查询时间正好是周期结束时间，则返回下一个周期的开始时间
//        if (cycleStartTime.plusDays(cycleLength - 1).equals(queryTime)) {
//            cycleStartTime = cycleStartTime.plusDays(cycleLength);
//        }

        return cycleStartTime;
    }

    /**
     * java.util.Date 转 java.time.LocalTime
     *
     * @param date
     * @return
     */
    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 获取参数日期昨天
     *
     * @param date
     * @return
     */
    public static Date getYesterday(Date date) {
        LocalDate today = toLocalDate(date);
        LocalDate yesterday = today.minusDays(1);
        return toDate(yesterday);
    }

    /**
     * 获取昨天
     *
     * @return
     */
    public static Date getYesterday() {
        // 获取今天的日期
        LocalDate today = LocalDate.now();
        // 获取昨天的日期
        LocalDate yesterday = today.minusDays(1);
        return toDate(yesterday);
    }

    /**
     * 获取一周第一天（星期一）
     *
     * @return
     */
    public static Date getWeekFirstDay(Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * 获取一周最后一天（星期天）
     *
     * @return
     */
    public static Date getWeekLastDay(Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return adjustNumber(cal.getTime(), 7);
    }

    /**
     * 获取指定月的第一天
     *
     * @return
     */
    public static Date getMonthFirstDay(Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 将日期调整到上周一
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 获取指定月的最后一天
     *
     * @return
     */
    public static Date getMonthLastDay(Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 将日期调整到上周一
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * 获取指定年的第一天
     *
     * @return
     */
    public static Date getYearFirstDay(Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 将日期调整到上周一
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, 0);
        return cal.getTime();
    }

    /**
     * 获取指定年的最后一天
     *
     * @return
     */
    public static Date getYearLastDay(Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 将日期调整到上周一
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, 11);
        return cal.getTime();
    }

    /**
     * 获取指定时间是月的第几周
     *
     * @return
     */
    public static Integer monthByWeek(Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int i = cal.get(Calendar.DAY_OF_WEEK);
        if (i == Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        // 将日期调整到上周一
        return cal.get(Calendar.WEEK_OF_MONTH);
    }

    public static String monthByWeekStr(Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int i = cal.get(Calendar.DAY_OF_WEEK);
        if (i == Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        date = cal.getTime();
        Integer month = getMonth(date);
        String monthStr = month.toString();
        if (month < 10) {
            monthStr = "0" + month;
        }
        return getYear(date) + "年" + monthStr + "月第" + cal.get(Calendar.WEEK_OF_MONTH) + "周";
    }

    /**
     * 获取指定时间是月
     *
     * @return
     */
    public static Integer getMonth(Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 将日期调整到上周一
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 得到几号
     *
     * @param date
     * @return
     */
    public static Integer getMonthDay(Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 将日期调整到上周一
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定时间是年
     *
     * @return
     */
    public static Integer getYear(Date date) {
        if (StringUtils.isNull(date)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 将日期调整到上周一
        return cal.get(Calendar.YEAR);
    }

    public static Date weekToDate(String weekDateStr) {
        if (StringUtils.isBlank(weekDateStr)) {
            return null;
        }
        String rex = "\\d{4}年\\d{2}月第\\d{1}周";
        if (!weekDateStr.matches(rex)) {
            return null;
        }
        int year = Integer.parseInt(StringUtils.substring(weekDateStr, 0, 4));
        String monthStr = StringUtils.substring(weekDateStr, 5, 7);
        if (StringUtils.startsWith(monthStr, Constants.ZERO_STR)) {
            monthStr = StringUtils.replace(monthStr, Constants.ZERO_STR, "");
        }
        int month = Integer.parseInt(monthStr);
        int week = Integer.parseInt(StringUtils.substring(weekDateStr, 9, 10));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.WEEK_OF_MONTH, week);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }


    public static Date getNearestSmallerTime(Date inputTime) {
        // Create a Calendar instance for manipulation
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inputTime);

        // Subtract 1 millisecond from the given time
        calendar.add(Calendar.MILLISECOND, -1);

        // Return the adjusted time
        return calendar.getTime();
    }

    public static List<Date[]> splitTimeByHour(Date startTime, Date endTime) {
        List<Date[]> result = new ArrayList<>();
        // 将Date类型转换为LocalDateTime类型，方便进行时间计算
        LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime.getTime()), ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime.getTime()), ZoneId.systemDefault());

        while (start.isBefore(end)) {
            LocalDateTime segmentEnd = start.plusHours(1);
            // 如果划分的时间段终点超过了给定的结束时间，则以结束时间为准
            if (segmentEnd.isAfter(end)) {
                segmentEnd = end;
            }
            // 将LocalDateTime类型转换回Date类型
            Date[] segment = new Date[2];
            segment[0] = Date.from(segmentEnd.minusHours(1).atZone(ZoneId.systemDefault()).toInstant());
            segment[1] = Date.from(segmentEnd.atZone(ZoneId.systemDefault()).toInstant());
            result.add(segment);
            start = segmentEnd;
        }

        return result;
    }

    /**
     * 获取指定时间十分钟之前的时间
     *
     * @param date 输入的时间
     * @return 十分钟之前的时间
     */
    public static Date getTenMinutesBefore(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("输入的日期不能为空");
        }
        // 将 Date 转为 LocalDateTime
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        // 减去 10 分钟
        LocalDateTime tenMinutesBefore = localDateTime.minusMinutes(10);
        // 转回 Date 并返回
        return Date.from(tenMinutesBefore.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 验证时间有没有在指定时间范围内
     *
     * @param date
     * @param startHour
     * @param startMinute
     * @param endHour
     * @param endMinute
     * @return
     */
    public static boolean isBetweenTenPMAndSixAM(Date date, Integer startHour, Integer startMinute, Integer endHour, Integer endMinute) {
        // 将Date转换为LocalDateTime
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // 定义晚上20点和早上6点
        LocalTime tenPM = LocalTime.of(startHour, startMinute); // 20:00
        LocalTime sixAM = LocalTime.of(endHour, endMinute); // 06:00

        // 获取要检查的时间部分
        LocalTime timeToCheck = localDateTime.toLocalTime();

        // 判断时间是否在晚上20点到早上6点之间
        return timeToCheck.isAfter(tenPM) || timeToCheck.isBefore(sixAM);
    }

    /**
     * 判断指定时间不是不周末
     *
     * @param date
     * @return
     */
    public static boolean isWeekend(Date date) {
        // 将Date转换为LocalDate
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // 获取当前日期的星期几
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();

        // 判断是否是星期六或星期日
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    public static int daysDifference(Date inputDate) {
        // 将 java.util.Date 转换为 java.time.LocalDate
        LocalDate inputLocalDate = inputDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        // 计算相差的天数绝对值
        return Math.abs((int) (inputLocalDate.toEpochDay() - currentDate.toEpochDay()));
    }

    /**
     * 将秒转成 00:00:00这种格式时可以超100
     *
     * @param totalSeconds
     * @return
     */
    public static String secondsToHHMMSS(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        // 支持超过99小时
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    /**
     * 生成指定时间范围和粒度的时间段列表
     *
     * @param start       开始时间
     * @param end         结束时间
     * @param granularity 时间粒度（天/周/月/年）
     * @return 时间段列表
     */
    public static List<TimePeriodVO> generateTimePeriods(Date start, Date end, TimeGranularity granularity) {
        // 校验参数：开始时间不能晚于结束时间，否则交换
        if (start.after(end)) {
            Date temp = start;
            start = end;
            end = temp;
        }

        List<TimePeriodVO> result = new ArrayList<>();
        switch (granularity) {
            case DAY:
                result = generateDayPeriods(start, end);
                break;
            case WEEK:
                result = generateWeekPeriods(start, end);
                break;
            case MONTH:
                result = generateMonthPeriods(start, end);
                break;
            case YEAR:
                result = generateYearPeriods(start, end);
                break;
        }
        return result;
    }

    /**
     * 按「天」粒度生成时间段（当日00:00:00 ~ 23:59:59）
     */
    private static List<TimePeriodVO> generateDayPeriods(Date start, Date end) {
        List<TimePeriodVO> periods = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        // 初始化到开始时间的00:00:00
        cal.setTime(start);
        setTimeToStartOfDay(cal);
        Date currentDate = cal.getTime();

        // 结束时间的23:59:59
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        setTimeToEndOfDay(endCal);
        Date endDate = endCal.getTime();

        // 遍历每一天
        while (!currentDate.after(endDate)) {
            // 当天结束时间
            Calendar dayEndCal = Calendar.getInstance();
            dayEndCal.setTime(currentDate);
            setTimeToEndOfDay(dayEndCal);
            Date dayEndTime = dayEndCal.getTime();

            // 生成名称：2025年09月08日
            String periodName = String.format("%d年%02d月%02d日",
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1, // Calendar月份从0开始，+1修正
                    cal.get(Calendar.DAY_OF_MONTH));

            periods.add(new TimePeriodVO(periodName, currentDate, dayEndTime));

            // 日期+1天
            cal.add(Calendar.DAY_OF_MONTH, 1);
            currentDate = cal.getTime();
        }
        return periods;
    }

    /**
     * 按「周」粒度生成时间段（周一00:00:00 ~ 周日23:59:59）
     */
    private static List<TimePeriodVO> generateWeekPeriods(Date start, Date end) {
        List<TimePeriodVO> periods = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        // 关键：设置周一为一周的第一天（默认是周日）
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setMinimalDaysInFirstWeek(1);

        // 开始时间所在周的周一 00:00:00
        cal.setTime(start);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        setTimeToStartOfDay(cal);
        Date currentWeekMonday = cal.getTime();

        // 结束时间所在周的周日 23:59:59
        Calendar endCal = Calendar.getInstance();
        endCal.setFirstDayOfWeek(Calendar.MONDAY);
        endCal.setTime(end);
        endCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        setTimeToEndOfDay(endCal);
        Date endWeekSunday = endCal.getTime();

        // 遍历每一周
        while (!currentWeekMonday.after(endWeekSunday)) {
            // 本周周日（周一+6天）
            Calendar weekSundayCal = Calendar.getInstance();
            weekSundayCal.setTime(currentWeekMonday);
            weekSundayCal.add(Calendar.DAY_OF_MONTH, 6);
            setTimeToEndOfDay(weekSundayCal);
            Date weekSunday = weekSundayCal.getTime();

            // 计算当月第几周
            Calendar weekCal = Calendar.getInstance();
            weekCal.setTime(currentWeekMonday);
            weekCal.setFirstDayOfWeek(Calendar.MONDAY);
            int weekOfMonth = weekCal.get(Calendar.WEEK_OF_MONTH);
            // 生成名称：2025年09月第一周
            String weekName = String.format("%d年%02d月第%s周",
                    weekCal.get(Calendar.YEAR),
                    weekCal.get(Calendar.MONTH) + 1,
                    getChineseWeekNum(weekOfMonth));

            periods.add(new TimePeriodVO(weekName, currentWeekMonday, weekSunday));

            // 下一周
            cal.setTime(currentWeekMonday);
            cal.add(Calendar.DAY_OF_MONTH, 7);
            currentWeekMonday = cal.getTime();
        }
        return periods;
    }

    /**
     * 按「月」粒度生成时间段（当月1日00:00:00 ~ 月末最后一天23:59:59）
     */
    private static List<TimePeriodVO> generateMonthPeriods(Date start, Date end) {
        List<TimePeriodVO> periods = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        // 开始时间所在月的第一天 00:00:00
        cal.setTime(start);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        setTimeToStartOfDay(cal);
        Date currentMonthFirstDay = cal.getTime();

        // 结束时间所在月的最后一天 23:59:59
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        setTimeToEndOfDay(endCal);
        Date endMonthLastDay = endCal.getTime();

        // 遍历每个月
        while (!currentMonthFirstDay.after(endMonthLastDay)) {
            // 当月最后一天
            Calendar monthLastDayCal = Calendar.getInstance();
            monthLastDayCal.setTime(currentMonthFirstDay);
            monthLastDayCal.set(Calendar.DAY_OF_MONTH, monthLastDayCal.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndOfDay(monthLastDayCal);
            Date monthLastDay = monthLastDayCal.getTime();

            // 生成名称：2025年09月
            String monthName = String.format("%d年%02d月",
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1);

            periods.add(new TimePeriodVO(monthName, currentMonthFirstDay, monthLastDay));

            // 下一个月
            cal.setTime(currentMonthFirstDay);
            cal.add(Calendar.MONTH, 1);
            currentMonthFirstDay = cal.getTime();
        }
        return periods;
    }

    /**
     * 按「年」粒度生成时间段（当年1月1日00:00:00 ~ 12月31日23:59:59）
     */
    private static List<TimePeriodVO> generateYearPeriods(Date start, Date end) {
        List<TimePeriodVO> periods = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        // 开始时间所在年的第一天 00:00:00
        cal.setTime(start);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        setTimeToStartOfDay(cal);
        Date currentYearFirstDay = cal.getTime();

        // 结束时间所在年的最后一天 23:59:59
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        endCal.set(Calendar.MONTH, Calendar.DECEMBER);
        endCal.set(Calendar.DAY_OF_MONTH, 31);
        setTimeToEndOfDay(endCal);
        Date endYearLastDay = endCal.getTime();

        // 遍历每一年
        while (!currentYearFirstDay.after(endYearLastDay)) {
            // 当年最后一天
            Calendar yearLastDayCal = Calendar.getInstance();
            yearLastDayCal.setTime(currentYearFirstDay);
            yearLastDayCal.set(Calendar.MONTH, Calendar.DECEMBER);
            yearLastDayCal.set(Calendar.DAY_OF_MONTH, 31);
            setTimeToEndOfDay(yearLastDayCal);
            Date yearLastDay = yearLastDayCal.getTime();

            // 生成名称：2025年
            String yearName = String.format("%d年", cal.get(Calendar.YEAR));

            periods.add(new TimePeriodVO(yearName, currentYearFirstDay, yearLastDay));

            // 下一年
            cal.setTime(currentYearFirstDay);
            cal.add(Calendar.YEAR, 1);
            currentYearFirstDay = cal.getTime();
        }
        return periods;
    }

    // ==================== 通用工具方法 ====================

    /**
     * 设置为当天00:00:00
     */
    private static void setTimeToStartOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 设置为当天23:59:59
     */
    private static void setTimeToEndOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
    }

    /**
     * 数字周数转中文（1→第一，2→第二...）
     */
    private static String getChineseWeekNum(int weekNum) {
        String[] chineseNums = {"一", "二", "三", "四", "五"};
        if (weekNum >= 1 && weekNum <= chineseNums.length) {
            return chineseNums[weekNum - 1];
        }
        return String.valueOf(weekNum);
    }


    public static String formatTimeDiff(Date start, Date end) {
        long totalSeconds = Math.abs(end.getTime() - start.getTime()) / 1000;

        if (totalSeconds == 0) {
            return "0秒";
        }

        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (days > 0) return days + "天";
        if (hours > 0) return hours + "小时";
        if (minutes > 0) return minutes + "分钟";
        return seconds + "秒";
    }
}
