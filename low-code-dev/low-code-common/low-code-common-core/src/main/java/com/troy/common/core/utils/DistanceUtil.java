package com.troy.common.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * @Description: GPS计算工具类
 * @Author: zhuQing
 * @Date: 2025/6/3 17:18
 * @Version: 1.0
 **/
public class DistanceUtil {


    private static final double EARTH_RADIUS = 6378.1;

    /**
     * 角度弧度计算公式
     * @param degree
     * @return
     */
    private static double getRadian(double degree) {
        return degree * Math.PI / 180.0;
    }

    /**
     * 依据经纬度计算两点之间的距离
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return 千米
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = getRadian(lat1);
        double radLat2 = getRadian(lat2);
        // 两点纬度差
        double a = radLat1 - radLat2;
        // 两点的经度差
        double b = getRadian(lng1) - getRadian(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(sin(a / 2), 2) + cos(radLat1)
                * cos(radLat2) * Math.pow(sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s;
    }

    /**
     * 经纬度转平面坐标系下的直角坐标系
     * @param lon
     * @param lat
     * @return
     */
    public static double[] lonLat2Mercator(double lon, double lat) {
        double[] xy = new double[2];
        double x = lon * 20037508.34 / 180;
        double y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
        y = y * 20037508.34 / 180;
        xy[0] = x;
        xy[1] = y;
        return xy;
    }

    /**
     * 平面坐标系下的直角坐标系转经纬度
     * @param x
     * @param y
     * @return
     */
    public static double[] mercator2LonLat(double x, double y) {
        double[] lonLat = new double[2];
        double lon = x / 20037508.34 * 180;
        double lat = y / 20037508.34 * 180;
        lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180)) - Math.PI / 2);
        lonLat[0] = lon;
        lonLat[1] = lat;
        return lonLat;
    }

    /**
     * 计算点到线段的最短距离
     * @param point
     * @param lineStart
     * @param lineEnd
     * @return
     */
    public static double pointToLineDistance(double[] point, double[] lineStart, double[] lineEnd) {
        double distance = 0;
        double a, b, c;
        a = lineEnd[1] - lineStart[1];
        b = lineStart[0] - lineEnd[0];
        c = lineEnd[0] * lineStart[1] - lineStart[0] * lineEnd[1];
        distance = Math.abs(a * point[0] + b * point[1] + c) / Math.sqrt(a * a + b * b);
        return distance;
    }

    public static double pointOnLineDistance(double[] point, double[] lineStart, double[] lineEnd) {
        double[] foot = pointToLineFoot(point, lineStart, lineEnd);
        if (isPointOnLine(lineStart[0], lineStart[1], lineEnd[0], lineEnd[1], foot[0], foot[1])){
            return pointToLineDistance(point, lineStart, lineEnd);
        } else {
            double v1 = GeoUtil.getDistanceMeter(point[0], point[1], lineStart[0], lineStart[1]);
            double v2 = GeoUtil.getDistanceMeter(point[0], point[1], lineEnd[0], lineEnd[1]);
            if(v1 >= v2){
                return v2;
            } else {
                return v1;
            }
        }
    }

    /**
     * 计算点到线段垂线的垂足坐标
     * @param point
     * @param lineStart
     * @param lineEnd
     * @return
     */
    public static double[] pointToLineFoot(double[] point, double[] lineStart, double[] lineEnd) {
        double[] pointFoot = new double[2];
        double a, b, c;
        a = lineEnd[1] - lineStart[1];
        b = lineStart[0] - lineEnd[0];
        c = lineEnd[0] * lineStart[1] - lineStart[0] * lineEnd[1];
        pointFoot[0] = (b * b * point[0] - a * b * point[1] - a * c) / (a * a + b * b);
        pointFoot[1] = (a * a * point[1] - a * b * point[0] - b * c) / (a * a + b * b);
        return pointFoot;
    }

    /**
     * 计算点到线段垂线的垂足坐标
     * @param point
     * @param lineStart
     * @param lineEnd
     * @return
     */
    public static boolean isPointOnLine(double[] point, double[] lineStart, double[] lineEnd) {
        boolean onLine = false;
        // 判断点是否在线段的包围盒内
        double minX = Math.min(lineStart[0], lineEnd[0]);
        double maxX = Math.max(lineStart[0], lineEnd[0]);
        double minY = Math.min(lineStart[1], lineEnd[1]);
        double maxY = Math.max(lineStart[1], lineEnd[1]);
        if (point[0] >= minX && point[0] <= maxX && point[1] >= minY && point[1] <= maxY) {
            // 计算点到线段两个端点的向量以及点到线段两个端点的向量
            double[] vector1 = {lineEnd[0] - lineStart[0], lineEnd[1] - lineStart[1]};
            double[] vector2 = {point[0] - lineStart[0], point[1] - lineStart[1]};
            double[] vector3 = {point[0] - lineEnd[0], point[1] - lineEnd[1]};
            // 判断两个向量之间的夹角是否为0度
            double angle1 = Math.acos((vector1[0] * vector2[0] + vector1[1] * vector2[1]) / (Math.sqrt(vector1[0] * vector1[0] + vector1[1] * vector1[1]) * Math.sqrt(vector2[0] * vector2[0] + vector2[1] * vector2[1])));
            double angle2 = Math.acos((vector1[0] * vector3[0] + vector1[1] * vector3[1]) / (Math.sqrt(vector1[0] * vector1[0] + vector1[1] * vector1[1]) * Math.sqrt(vector3[0] * vector3[0] + vector3[1] * vector3[1])));
            if (Math.abs(angle1 + angle2 - Math.PI) < 1e-6) {
                onLine = true;
            }
        }
        return onLine;
    }

    /**
     * 计算初始方位角
     * @param startLat
     * @param startLng
     * @param endLat
     * @param endLng
     * @return
     */
    public static double getBearing(double startLng, double startLat, double endLng, double endLat) {
        double y = Math.sin(startLng-endLat) * Math.cos(endLat);
        double x = Math.cos(startLat)*Math.sin(endLat) -
                Math.sin(startLat)*Math.cos(endLat)*Math.cos(endLng-startLat);
        return Math.atan2(y, x);
    }


    public static double getNorthBear(double startLng, double startLat, double endLng, double endLat) {
        double deltaLon = endLng - startLng;
        double x = Math.cos(Math.toRadians(endLat)) * Math.sin(Math.toRadians(deltaLon));
        double y = Math.cos(Math.toRadians(startLat)) * Math.sin(Math.toRadians(endLat)) - Math.sin(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) * Math.cos(Math.toRadians(deltaLon));
        return Math.atan2(y, x) % (2 * Math.PI);
    }

    public static double getNorthBearing(double startLng, double startLat, double endLng, double endLat) {
        double rad = Math.PI / 180;
        double lat1 = startLat * rad;
        double lat2 = endLat * rad;
        double lon1 = startLng * rad;
        double lon2 = endLng * rad;
        double a = Math.sin(lon2 - lon1) * Math.cos(lat2);
        double b = Math.cos(lat1) * Math.sin(lat2) -
                Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1);

        return radiansToDegrees(Math.atan2(a, b));
    }

    /*
     * 弧度转换为角度
     */
    static double radiansToDegrees(double radians) {
        double degrees = radians % (2 * Math.PI);
        double v = degrees * 180 / Math.PI;
        return (v+360)%360;
    }

    /**
     * 计算目标坐标
     * @param startLat
     * @param startLng
     * @param distance
     * @param bearing
     * @return
     */
    public static double[] getDestination(double startLng, double startLat, double distance, double bearing) {
        // 地球半径，单位米
        double radius = 6371;
        double lat1 = Math.toRadians(startLat);
        double lon1 = Math.toRadians(startLng);
        double brng = Math.toRadians(bearing);
        double d = distance / radius;

        double latB = Math.asin(Math.sin(lat1) * Math.cos(d) + Math.cos(lat1) * Math.sin(d) * Math.cos(brng));
        double lonB = lon1 + Math.atan2(Math.sin(brng) * Math.sin(d) * Math.cos(lat1), Math.cos(d) - Math.sin(lat1) * Math.sin(latB));

        latB = Math.toDegrees(latB);
        lonB = Math.toDegrees(lonB);

        return new double[] {lonB, latB};
    }

    /**
     * 判断点是否在路线上
     * @param lonA
     * @param latA
     * @param lonB
     * @param latB
     * @param lonC
     * @param latC
     * @return
     */
    public static boolean isPointOnLine(double lonA, double latA, double lonB, double latB, double lonC, double latC) {
        double distanceACB = getDistance(lonA, latA, lonC, latC) + getDistance(lonB, latB, lonC, latC);
        double distanceAB = getDistance(lonA, latA, lonB, latB);
        if (Math.abs(distanceACB - distanceAB) < 0.001){
            System.out.println(distanceAB+"     "+distanceACB);
        }
        return Math.abs(distanceACB - distanceAB) < 0.001;
    }

    public static double[] getPointByLenAndPoint(double lon1, double lat1, double lon2, double lat2, double len){
        double distance = getDistance(lon1, lat1, lon2, lat2);
        double t = len / distance;

        double lat = lat1 + t * (lat2 - lat1);
        double lng = lon1 + t * (lon2 - lon1);
        return new double[]{lng, lat};
    }

    public static BigDecimal max(BigDecimal a, BigDecimal b){
        return (a.compareTo(b) >= 0) ? a : b;
    }

    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        return (a.compareTo(b) <= 0) ? a : b;
    }

    // 生成随机经度
    private static BigDecimal generateRandomLon(BigDecimal origLon, double len) {

        double maxOffset = Math.toDegrees(len / EARTH_RADIUS);

        Random rand = new Random();
        double offset = rand.nextDouble() * 2 * maxOffset - maxOffset;

        return origLon.add(new BigDecimal(offset));
    }

    // 生成随机纬度
    private static BigDecimal generateRandomLat(BigDecimal origLat, double len) {

        // 纬度需要除以cos(origLat)调整最大偏移
        double cosLat = Math.cos(Math.toRadians(origLat.doubleValue()));
        double maxOffset = Math.toDegrees(len / (EARTH_RADIUS * cosLat));

        Random rand = new Random();
        double offset = rand.nextDouble() * 2 * maxOffset - maxOffset;

        return origLat.add(new BigDecimal(offset));
    }

    /**
     * 随机生成
     * @param lon
     * @param lat
     * @param length 单位千米
     * @return
     */
    public static BigDecimal[] getRandomPosition(BigDecimal lon, BigDecimal lat, double length){
        BigDecimal lat1 = generateRandomLat(lat, length).setScale(8, RoundingMode.HALF_UP);
        BigDecimal lon1 = generateRandomLat(lon, length).setScale(8, RoundingMode.HALF_UP);
        return new BigDecimal[]{lon1, lat1};
    }
}
