package com.troy.common.rocketmq.constant;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/3 17:17:30
 * @Description: RocketMQ业务常量
 * @Version: 1.0.0
 */
public interface RocketMqBizConstant {

    /**
     * 车辆GPS处理
      */
    String VEHICLE_GPS_GROUP = "VEHICLE_GPS_GROUP";

    String VEHICLE_GPS_TOPIC = "VEHICLE_GPS_TOPIC";

    String VEHICLE_GPS_TAG = "VEHICLE_TAG";

    /**
     * 车辆GPS处理
     */
    String VEHICLE_ALARM_GROUP = "VEHICLE_ALARM_GROUP";

    String VEHICLE_ALARM_TOPIC= "VEHICLE_ALARM_TOPIC";

    String VEHICLE_ALARM_TAG = "VEHICLE_ALARM_TAG";


    /**
     * 车辆开始
     */
    String GPS_START_GROUP = "GPS_START_GROUP";

    String GPS_START_TOPIC= "GPS_START_TOPIC";

    String GPS_START_TAG = "GPS_START_TAG";


    /**
     * 结束
     */
    String GPS_END_GROUP = "GPS_END_GROUP";

    String GPS_END_TOPIC= "GPS_END_TOPIC";

    String GPS_END_TAG = "GPS_END_TAG";


}
