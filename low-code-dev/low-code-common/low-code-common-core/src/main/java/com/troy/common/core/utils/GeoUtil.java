package com.troy.common.core.utils;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

/**
 * @Description:
 * @Author: zhuQing
 * @Date: 2025/6/3 17:20
 * @Version: 1.0
 **/
public class GeoUtil {

    /**
     * 获取距离
     * @param lonA
     * @param latA
     * @param lonB
     * @param latB
     * @return
     */
    public static double getDistanceMeter(double lonA, double latA, double lonB, double latB){
        GlobalCoordinates gpsFrom = new GlobalCoordinates(latA, lonA);
        GlobalCoordinates gpsTo = new GlobalCoordinates(latB, lonB);
        //创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.WGS84, gpsFrom, gpsTo);
        return geoCurve.getEllipsoidalDistance();
    }

    /**
     * 获取经纬度
     * @param longitudeFrom
     * @param latitudeFrom
     * @param startAngle
     * @param distance
     * @return
     */
    public static double[] getGlobalCoordinates(double longitudeFrom, double latitudeFrom, double startAngle, double distance){
        //经纬度对象
        GlobalCoordinates startGlobalCoordinates = new GlobalCoordinates(latitudeFrom, longitudeFrom);
        //计算的坐标对象
        GlobalCoordinates globalCoordinates = new GeodeticCalculator().calculateEndingGlobalCoordinates(Ellipsoid.WGS84, startGlobalCoordinates, startAngle, distance*1000);
        //获取纬度
        double latitude = globalCoordinates.getLatitude();
        //获取经度
        double longitude = globalCoordinates.getLongitude();
        return new double[]{longitude, latitude};
    }

    /**
     * 获取方位
     * @param lonA
     * @param latA
     * @param lonB
     * @param latB
     * @return
     */
    public static double getBear(double lonA, double latA, double lonB, double latB){
        GlobalCoordinates gpsFrom = new GlobalCoordinates(latA, lonA);
        GlobalCoordinates gpsTo = new GlobalCoordinates(latB, lonB);
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.WGS84, gpsFrom, gpsTo);
        return geoCurve.getAzimuth();
    }
}
