package com.troy.common.core.enums;

import com.troy.common.core.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname: DictValueEnums
 * @Description:
 * @Date 2022/9/2
 * @Author: yzy
 * @Version
 **/
public enum DictValueEnums {

    // ----------------------------------------    基础类型   -----------------------------------------------

    TRUE(DictTypeEnums.TRUE_FALSE, "0", "是"),

    FALSE(DictTypeEnums.TRUE_FALSE, "1", "否"),

    MAN(DictTypeEnums.SEX, "1", "男"),

    WOMAN(DictTypeEnums.SEX, "0", "女"),

    ON_STATUS(DictTypeEnums.STATUS_TYPE, "0", "启用"),

    OFF_STATUS(DictTypeEnums.STATUS_TYPE, "1", "停用"),

    LEFT(DictTypeEnums.MENU_TYPE, "0", "左侧菜单"),

    TOP(DictTypeEnums.MENU_TYPE, "1", "顶部菜单"),

    BUTTON(DictTypeEnums.MENU_TYPE, "2", "按钮"),

    LINK(DictTypeEnums.MENU_TYPE, "3", "外链"),

    APPLY(DictTypeEnums.MENU_TYPE, "4", "应用"),

    INSIDE_WINDOW(DictTypeEnums.FRAME_TYPE, "1", "嵌套"),

    NEW_WINDOW(DictTypeEnums.FRAME_TYPE, "2", "新窗口"),

    OTHER_BUSINESS(DictTypeEnums.BUSINESS_TYPE, "0", "其它"),

    INSERT_BUSINESS(DictTypeEnums.BUSINESS_TYPE, "1", "新增"),

    UPDATE_BUSINESS(DictTypeEnums.BUSINESS_TYPE, "2", "修改"),

    DELETE_BUSINESS(DictTypeEnums.BUSINESS_TYPE, "3", "删除"),

    OTHER_USER(DictTypeEnums.OPERATOR_TYPE, "0", "其它"),

    WEB_USER(DictTypeEnums.OPERATOR_TYPE, "1", "后台用户"),

    APP_USER(DictTypeEnums.OPERATOR_TYPE, "2", "手机端用户"),

    GET(DictTypeEnums.REQUEST_METHOD, "0", "get"),

    POST(DictTypeEnums.REQUEST_METHOD, "1", "post"),

    PUT(DictTypeEnums.REQUEST_METHOD, "2", "put"),

    DELETE(DictTypeEnums.REQUEST_METHOD, "3", "delete"),

    PATCH(DictTypeEnums.REQUEST_METHOD, "4", "patch"),

    INTERNAL_APP(DictTypeEnums.APP_TYPE, "1", "内部应用"),

    EXTERNAL_APP(DictTypeEnums.APP_TYPE, "2", "外部应用"),

    DATA_SCOPE_ALL(DictTypeEnums.DATA_RANGE, "1", "所有数据权限"),

    DATA_SCOPE_CUSTOM(DictTypeEnums.DATA_RANGE, "2", "自定义数据权限"),

    DATA_SCOPE_DEPT(DictTypeEnums.DATA_RANGE, "3", "本部门数据权限"),

    DATA_SCOPE_DEPT_AND_CHILD(DictTypeEnums.DATA_RANGE, "4", "本部门及以下数据权限"),

    DATA_SCOPE_SELF(DictTypeEnums.DATA_RANGE, "5", "仅本人数据权限"),

    GT(DictTypeEnums.COMPARISON_OPERATOR, "gt", "大于"),

    GTE(DictTypeEnums.COMPARISON_OPERATOR, "gte", "大于等于"),

    LT(DictTypeEnums.COMPARISON_OPERATOR, "lt", "小于"),

    LTE(DictTypeEnums.COMPARISON_OPERATOR, "lte", "小于等于"),

    EQ(DictTypeEnums.COMPARISON_OPERATOR, "eq", "等于"),

    // ----------------------------------------    市级平台   -----------------------------------------------

    GEO_IMPORT(DictTypeEnums.GEO_JSON_STATUS, "1", "已导入"),

    GEO_COMPLETE(DictTypeEnums.GEO_JSON_STATUS, "2", "已生成"),

    GEO_UPDATE(DictTypeEnums.GEO_JSON_STATUS, "3", "需要更新"),

    GEO_SIDE(DictTypeEnums.GEO_JSON_TYPE, "1", "区域边界"),

    GEO_ROUTE(DictTypeEnums.GEO_JSON_TYPE, "2", "路线"),

    GEO_BRIDGE(DictTypeEnums.GEO_JSON_TYPE, "3", "桥梁"),

    GEO_TUNNEL(DictTypeEnums.GEO_JSON_TYPE, "4", "隧道"),

    GEO_CULVERT(DictTypeEnums.GEO_JSON_TYPE, "5", "涵洞"),

    GEO_SLOPE(DictTypeEnums.GEO_JSON_TYPE, "6", "边坡"),

    GEO_FACILITIES(DictTypeEnums.GEO_JSON_TYPE, "7", "交通设施"),

    GEO_SECTION(DictTypeEnums.GEO_JSON_TYPE, "8", "路段"),

    GEO_HIGHWAY(DictTypeEnums.GEO_JSON_TYPE, "9", "高速"),

    GEO_CHANNEL(DictTypeEnums.GEO_JSON_TYPE, "10", "航道"),

    GEO_TRAIN(DictTypeEnums.GEO_JSON_TYPE, "11", "铁路"),

    GEO_SERVICE_AREA(DictTypeEnums.GEO_JSON_TYPE, "12", "服务区"),

    GEO_TOLL_STATION(DictTypeEnums.GEO_JSON_TYPE, "13", "收费站"),

    GEO_SIGN(DictTypeEnums.GEO_JSON_TYPE, "14", "警示标志标牌"),

    GEO_LINE(DictTypeEnums.GEO_JSON_TYPE, "15", "路面标线"),

    GEO_RAILINGS(DictTypeEnums.GEO_JSON_TYPE, "16", "护栏"),

    GEO_NATURAL_DISASTERS(DictTypeEnums.GEO_JSON_TYPE, "17", "自然风险综合"),

    GEO_FLOOD_SEASON(DictTypeEnums.GEO_JSON_TYPE, "18", "汛期重点防范路段"),

    GEO_OVER_ROAD(DictTypeEnums.GEO_JSON_TYPE, "19", "上跨路段"),

    GEO_MAINTENANCE_CENTER(DictTypeEnums.GEO_JSON_TYPE, "GEO_MAINTENANCE_CENTER", "养护和应急保通中心"),

    GEO_ROAD_SERVICE_AREA(DictTypeEnums.GEO_JSON_TYPE, "GEO_ROAD_SERVICE_AREA", "普通公路服务区"),
    GEO_ROAD_PARKING_AREA(DictTypeEnums.GEO_JSON_TYPE, "GEO_ROAD_PARKING_AREA", "普通公路停车区"),
    GEO_ROAD_MAINTENANCE_STATION(DictTypeEnums.GEO_JSON_TYPE, "GEO_ROAD_MAINTENANCE_STATION", "普通公路养护站"),
    GEO_ROAD_CROSSING(DictTypeEnums.GEO_JSON_TYPE, "GEO_ROAD_CROSSING", "普通公路道口"),
    GEO_ROAD_GREEN(DictTypeEnums.GEO_JSON_TYPE, "GEO_ROAD_GREEN", "普通公路绿化带"),
    GEO_ROAD_MAINTENANCE_EQUIPMENT(DictTypeEnums.GEO_JSON_TYPE, "GEO_ROAD_MAINTENANCE_EQUIPMENT", "普通公路养护设备"),
    GEO_EMERGENCY_EXPERTS(DictTypeEnums.GEO_JSON_TYPE, "GEO_EMERGENCY_EXPERTS", "应急专家"),

    GEO_EVENT_ASSESSMENT_SUMMARY(DictTypeEnums.GEO_JSON_TYPE, "GEO_EVENT_ASSESSMENT_SUMMARY", "事件评估总结"),

    GEO_BLOCKING_EVENT(DictTypeEnums.GEO_JSON_TYPE, "GEO_BLOCKING_EVENT", "交通拥堵事件"),

    GEO_MAINTENANCE_PROJECT(DictTypeEnums.GEO_JSON_TYPE, "GEO_MAINTENANCE_PROJECT", "养护工程管理"),

    GEO_BRIDGE_PATROL(DictTypeEnums.GEO_JSON_TYPE, "GEO_BRIDGE_PATROL", "桥梁巡查"),
    GEO_BRIDGE_DISEASE(DictTypeEnums.GEO_JSON_TYPE, "GEO_BRIDGE_DISEASE", "桥梁病害"),

    EXTRA_LARGE_BRIDGE(DictTypeEnums.BRIDGE_LEVEL, "1", "特大桥"),

    LARGE_BRIDGE(DictTypeEnums.BRIDGE_LEVEL, "2", "大桥"),

    MIDDLE_BRIDGE(DictTypeEnums.BRIDGE_LEVEL, "3", "中桥"),

    SMALL_BRIDGE(DictTypeEnums.BRIDGE_LEVEL, "4", "小桥"),


    QIAO_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "10", "桥梁"),
    KONG_XIN_BAN_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "11", "空心板梁"),
    ZHENG_TI_QING_JIAO_BAN(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "12", "整体现浇板"),
    T_XING_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "13", "T型梁"),
    I_XING_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "14", "I型梁"),
    II_XING_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "15", "II型梁"),
    XIANG_XING_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "16", "箱型梁"),
    HENG_JIA_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "17", "桁架梁"),
    SHI_XIN_BAN_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "18", "实心板梁"),
    LEI_BAN_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "19", "肋板梁"),
    ZU_HE_SHI_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "20", "组合式梁"),
    LIAN_XU_T_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "21", "连续T梁"),
    LIAN_XU_XIANG_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "22", "连续箱梁"),
    XUAN_BI_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "30", "悬臂梁"),
    OTHER_QIAO_LIANG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "39", "其他桥梁"),
    GONG_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "40", "拱桥"),
    BAN_GONG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "41", "板拱"),
    LEI_GONG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "42", "肋拱"),
    SHUANG_QU_GONG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "43", "双曲拱"),
    XIANG_XING_GONG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "44", "箱型拱"),
    HENG_JIA_GONG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "45", "桁架拱"),
    GANG_JIA_GONG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "46", "钢架拱"),
    XI_GAN_GONG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "47", "系杆拱"),
    OTHER_GONG_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "48", "其他拱桥"),
    GANG_GOU_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "50", "刚构桥"),
    MEN_SHI_GANG_GOU(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "51", "门式刚构"),
    XIE_TUI_GANG_GOU(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "52", "斜腿刚构"),
    T_XING_GANG_GOU(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "53", "T型刚构"),
    LIAN_XU_GANG_GOU(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "54", "连续刚构"),
    OTHER_GANG_GOU_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "59", "其他刚构桥"),
    XUAN_SUO_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "60", "悬索桥"),
    DI_MAO_SHI_XUAN_SUO_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "61", "地锚式(外锚式)悬索桥"),
    ZI_MAO_XUAN_SUO_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "62", "自锚悬索桥"),
    XIE_LA_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "70", "斜拉桥"),
    ZU_HE_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "80", "组合桥"),
    XIE_LA_XUAN_SUO_ZU_HE_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "81", "斜拉\\悬索组合桥"),
    WEI_WAN_BAN_ZU_HE_GONG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "82", "微弯板组合工字梁(或拱)"),
    LEI_YE_BAN_ZU_HE_GONG(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "83", "肋腋板组合工字梁(或拱)"),
    OTHER_ZU_HE_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "89", "其他组合桥"),
    OTHER_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "90", "其他桥"),
    BO_KE_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "91", "薄壳桥"),
    FU_QIAO(DictTypeEnums.BRIDGE_ZQSBGZJGSS, "92", "浮桥"),


    // Warning Signs
    WARNING_SIGN(DictTypeEnums.SIGN_TYPE, "100", "警告标志"),
    DANGER_SIGN(DictTypeEnums.SIGN_TYPE, "134", "注意危险标志"),
    INTERSECTION_SIGN(DictTypeEnums.SIGN_TYPE, "101", "交叉路口标志"),
    CONSTRUCTION_SIGN(DictTypeEnums.SIGN_TYPE, "135", "施工标志"),
    SHARP_TURN_SIGN(DictTypeEnums.SIGN_TYPE, "102", "急弯路标志"),
    SUGGESTED_SPEED_SIGN(DictTypeEnums.SIGN_TYPE, "136", "建议速度标志"),
    REVERSE_TURN_SIGN(DictTypeEnums.SIGN_TYPE, "103", "反向弯路标志"),
    TUNNEL_LIGHT_SIGN(DictTypeEnums.SIGN_TYPE, "137", "隧道开车灯标志"),
    CONTINUOUS_CURVE_SIGN(DictTypeEnums.SIGN_TYPE, "104", "连续弯路标志"),
    TIDAL_LANE_SIGN(DictTypeEnums.SIGN_TYPE, "138", "注意潮汐车道标志"),
    STEEP_SLOPE_SIGN(DictTypeEnums.SIGN_TYPE, "105", "陡坡标志"),
    KEEP_DISTANCE_SIGN(DictTypeEnums.SIGN_TYPE, "139", "注意保持车距标志"),
    CONTINUOUS_DOWNHILL_SIGN(DictTypeEnums.SIGN_TYPE, "106", "连续下坡标志"),
    SEPARATED_ROAD_SIGN(DictTypeEnums.SIGN_TYPE, "140", "注意分离式道路标志"),
    NARROW_ROAD_SIGN(DictTypeEnums.SIGN_TYPE, "107", "窄路标志"),
    MERGING_LANES_SIGN(DictTypeEnums.SIGN_TYPE, "141", "注意合流标志"),
    NARROW_BRIDGE_SIGN(DictTypeEnums.SIGN_TYPE, "108", "窄桥标志"),
    ESCAPE_LANE_SIGN(DictTypeEnums.SIGN_TYPE, "142", "避险车道标志"),
    TWO_WAY_TRAFFIC_SIGN(DictTypeEnums.SIGN_TYPE, "109", "双向交通标志"),
    WEATHER_CONDITION_SIGN(DictTypeEnums.SIGN_TYPE, "143", "注意不利气象条件的标志"),
    CARE_PEDESTRIAN_SIGN(DictTypeEnums.SIGN_TYPE, "110", "注意行人标志"),
    QUEUING_VEHICLES_SIGN(DictTypeEnums.SIGN_TYPE, "144", "注意前方车辆排队标志"),
    CHILDREN_SIGN(DictTypeEnums.SIGN_TYPE, "111", "注意儿童标志"),
    LIVESTOCK_SIGN(DictTypeEnums.SIGN_TYPE, "112", "注意牲畜标志"),
    WILD_ANIMALS_SIGN(DictTypeEnums.SIGN_TYPE, "113", "注意野生动物标志"),
    TRAFFIC_LIGHT_SIGN(DictTypeEnums.SIGN_TYPE, "114", "注意信号灯标志"),
    FALLING_ROCK_SIGN(DictTypeEnums.SIGN_TYPE, "115", "注意落石标志"),
    CROSSWIND_SIGN(DictTypeEnums.SIGN_TYPE, "116", "注意横风标志"),
    SLIPPERY_ROAD_SIGN(DictTypeEnums.SIGN_TYPE, "117", "易滑标志"),
    MOUNTAIN_ROAD_SIGN(DictTypeEnums.SIGN_TYPE, "118", "傍山险路标志"),
    DIKE_ROAD_SIGN(DictTypeEnums.SIGN_TYPE, "119", "堤坝路标志"),
    VILLAGE_SIGN(DictTypeEnums.SIGN_TYPE, "120", "村庄标志"),
    TUNNEL_SIGN(DictTypeEnums.SIGN_TYPE, "121", "隧道标志"),
    FERRY_SIGN(DictTypeEnums.SIGN_TYPE, "122", "渡口标志"),

    // Prohibition Signs
    PROHIBITION_SIGN(DictTypeEnums.SIGN_TYPE, "200", "禁令标志"),
    STOP_YIELD_SIGN(DictTypeEnums.SIGN_TYPE, "201", "停车让行标志"),
    SLOW_YIELD_SIGN(DictTypeEnums.SIGN_TYPE, "202", "减速让行标志"),
    MEETING_YIELD_SIGN(DictTypeEnums.SIGN_TYPE, "203", "会车让行标志"),
    NO_ENTRY_SIGN(DictTypeEnums.SIGN_TYPE, "204", "禁止通行标志"),
    NO_ENTRY_VEHICLES_SIGN(DictTypeEnums.SIGN_TYPE, "205", "禁止驶入标志"),
    NO_ENTRY_MOTOR_VEHICLES_SIGN(DictTypeEnums.SIGN_TYPE, "206", "禁止机动车驶入标志"),
    NO_ENTRY_TRUCKS_SIGN(DictTypeEnums.SIGN_TYPE, "207", "禁止载货汽车驶入标志"),
    NO_ENTRY_ELECTRIC_TRICYCLES_SIGN(DictTypeEnums.SIGN_TYPE, "208", "禁止电动三轮车驶入标志"),
    NO_ENTRY_LARGE_SMALL_BUSES_SIGN(DictTypeEnums.SIGN_TYPE, "209", "禁止大型(或小型)客车驶入标志"),
    NO_ENTRY_TRAILERS_SIGN(DictTypeEnums.SIGN_TYPE, "210", "禁止挂车、半挂车驶入标志"),
    NO_ENTRY_TRACTORS_SIGN(DictTypeEnums.SIGN_TYPE, "211", "禁止拖拉机驶入标志"),
    NO_STRAIGHT_LEFT_TURN_SIGN(DictTypeEnums.SIGN_TYPE, "223", "禁止直行和向左转弯(向右转弯或直行)标志"),
    NO_U_TURN_SIGN(DictTypeEnums.SIGN_TYPE, "224", "禁止掉头标志"),
    NO_OVERTAKING_SIGN(DictTypeEnums.SIGN_TYPE, "225", "禁止超车标志"),
    OVERTAKING_ALLOWED_SIGN(DictTypeEnums.SIGN_TYPE, "226", "解除禁止超车标志"),
    NO_PARKING_SIGN(DictTypeEnums.SIGN_TYPE, "227", "禁止停车标志"),
    NO_LONG_PARKING_SIGN(DictTypeEnums.SIGN_TYPE, "228", "禁止长时停车标志"),
    NO_HONKING_SIGN(DictTypeEnums.SIGN_TYPE, "229", "禁止鸣喇叭标志"),
    WIDTH_LIMIT_SIGN(DictTypeEnums.SIGN_TYPE, "230", "限制宽度标志"),
    HEIGHT_LIMIT_SIGN(DictTypeEnums.SIGN_TYPE, "231", "限制高度标志"),
    WEIGHT_LIMIT_SIGN(DictTypeEnums.SIGN_TYPE, "232", "限制质量标志"),
    AXLE_WEIGHT_LIMIT_SIGN(DictTypeEnums.SIGN_TYPE, "233", "限制轴重标志"),
    SPEED_LIMIT_SIGN(DictTypeEnums.SIGN_TYPE, "234", "限制速度标志"),
    SPEED_LIMIT_LIFTED_SIGN(DictTypeEnums.SIGN_TYPE, "235", "解除限制速度标志"),
    STOP_INSPECTION_SIGN(DictTypeEnums.SIGN_TYPE, "236", "停车检查标志"),
    NO_HAZARDOUS_VEHICLES_SIGN(DictTypeEnums.SIGN_TYPE, "237", "禁止运输危险物品车辆驶入标志"),
    CUSTOMS_SIGN(DictTypeEnums.SIGN_TYPE, "238", "海关标志"),
    AREA_PROHIBITION_SIGN(DictTypeEnums.SIGN_TYPE, "239", "区域禁止及解除标志"),

    // Mandatory Signs
    MANDATORY_SIGN(DictTypeEnums.SIGN_TYPE, "300", "指示标志"),
    STRAIGHT_AHEAD_SIGN(DictTypeEnums.SIGN_TYPE, "301", "直行标志"),
    LEFT_RIGHT_TURN_SIGN(DictTypeEnums.SIGN_TYPE, "302", "向左(或向右)转弯标志"),
    STRAIGHT_LEFT_RIGHT_TURN_SIGN(DictTypeEnums.SIGN_TYPE, "303", "直行和向左转弯(或直行和向右转弯)标志"),
    LEFT_RIGHT_TURN_ONLY_SIGN(DictTypeEnums.SIGN_TYPE, "304", "向左和向右转弯标志"),
    KEEP_RIGHT_LEFT_SIGN(DictTypeEnums.SIGN_TYPE, "305", "靠右侧(或靠左侧)道路行驶标志"),
    GRADE_SEPARATED_ROAD_SIGN(DictTypeEnums.SIGN_TYPE, "306", "立体交叉行驶路线标志"),
    ROUNDABOUT_SIGN(DictTypeEnums.SIGN_TYPE, "307", "环岛行驶标志"),
    ONE_WAY_ROAD_SIGN(DictTypeEnums.SIGN_TYPE, "308", "单行路标志"),
    PEDESTRIAN_SIGN(DictTypeEnums.SIGN_TYPE, "309", "步行标志"),
    HORN_SIGN(DictTypeEnums.SIGN_TYPE, "310", "鸣喇叭标志"),
    MINIMUM_SPEED_SIGN(DictTypeEnums.SIGN_TYPE, "311", "最低限速标志"),
    PRIORITY_SIGN(DictTypeEnums.SIGN_TYPE, "312", "路口优先通行标志"),
    GIVE_WAY_SIGN(DictTypeEnums.SIGN_TYPE, "313", "会车先行标志"),
    PEDESTRIAN_CROSSING_SIGN(DictTypeEnums.SIGN_TYPE, "314", "人行横道标志"),
    LANE_DIRECTION_SIGN(DictTypeEnums.SIGN_TYPE, "315", "车道行驶方向标志"),
    EXCLUSIVE_LANE_SIGN(DictTypeEnums.SIGN_TYPE, "316", "专用道路和车道标志"),
    PARKING_SPOT_SIGN(DictTypeEnums.SIGN_TYPE, "317", "停车位标志"),
    U_TURN_ALLOWED_SIGN(DictTypeEnums.SIGN_TYPE, "318", "允许掉头标志"),

    // Directional Signs
    DIRECTION_SIGN(DictTypeEnums.SIGN_TYPE, "400", "指路标志"),
    HIGHWAY_SIGN(DictTypeEnums.SIGN_TYPE, "445", "高速公路、城市快速路指路标志"),
    DIRECTIONAL_SIGN(DictTypeEnums.SIGN_TYPE, "490", "方向标志"),

    // Tourist Signs
    TOURIST_SIGN(DictTypeEnums.SIGN_TYPE, "500", "旅游区标志"),
    GUIDANCE_SIGN(DictTypeEnums.SIGN_TYPE, "501", "指引标志"),
    TOURISM_SYMBOL_SIGN(DictTypeEnums.SIGN_TYPE, "520", "旅游符号"),

    // Other Signs
    OTHER_SIGN(DictTypeEnums.SIGN_TYPE, "900", "其他标志"),
    WORK_ZONE_SIGN(DictTypeEnums.SIGN_TYPE, "901", "作业区标志"),
    SUPPLEMENTARY_SIGN(DictTypeEnums.SIGN_TYPE, "910", "辅助标志"),
    NOTICE_SIGN(DictTypeEnums.SIGN_TYPE, "930", "告示标志"),

    //按导入数据新增

    SIGN_TYPE_1001(DictTypeEnums.SIGN_TYPE, "1001", "T型交叉"),
    SIGN_TYPE_1002(DictTypeEnums.SIGN_TYPE, "1002", "T型岔路口"),
    SIGN_TYPE_1003(DictTypeEnums.SIGN_TYPE, "1003", "慢行"),
    SIGN_TYPE_1004(DictTypeEnums.SIGN_TYPE, "1004", "限速"),
    SIGN_TYPE_1005(DictTypeEnums.SIGN_TYPE, "1005", "Y型交叉"),
    SIGN_TYPE_1006(DictTypeEnums.SIGN_TYPE, "1006", "岔路"),
    SIGN_TYPE_1007(DictTypeEnums.SIGN_TYPE, "1007", "多雾路段"),
    SIGN_TYPE_1008(DictTypeEnums.SIGN_TYPE, "1008", "高填方沉降"),
    SIGN_TYPE_1009(DictTypeEnums.SIGN_TYPE, "1009", "减速"),
    SIGN_TYPE_1010(DictTypeEnums.SIGN_TYPE, "1010", "十字交叉"),
    SIGN_TYPE_1011(DictTypeEnums.SIGN_TYPE, "1011", "测速"),
    SIGN_TYPE_1012(DictTypeEnums.SIGN_TYPE, "1012", "禁左弯"),
    SIGN_TYPE_1013(DictTypeEnums.SIGN_TYPE, "1013", "禁令"),
    SIGN_TYPE_1014(DictTypeEnums.SIGN_TYPE, "1014", "禁止右转"),
    SIGN_TYPE_1015(DictTypeEnums.SIGN_TYPE, "1015", "禁止超车"),
    SIGN_TYPE_1016(DictTypeEnums.SIGN_TYPE, "1016", "禁止掉头"),
    SIGN_TYPE_1017(DictTypeEnums.SIGN_TYPE, "1017", "警告"),
    SIGN_TYPE_1018(DictTypeEnums.SIGN_TYPE, "1018", "警告（前方学校慢行）"),
    SIGN_TYPE_1019(DictTypeEnums.SIGN_TYPE, "1019", "警告（雨雾冰雪慢行）"),
    SIGN_TYPE_1020(DictTypeEnums.SIGN_TYPE, "1020", "警告（注意落石）"),
    SIGN_TYPE_1021(DictTypeEnums.SIGN_TYPE, "1021", "超限监测点"),
    SIGN_TYPE_1022(DictTypeEnums.SIGN_TYPE, "1022", "夜间报闪灯"),
    SIGN_TYPE_1023(DictTypeEnums.SIGN_TYPE, "1023", "全短路禁停"),
    SIGN_TYPE_1024(DictTypeEnums.SIGN_TYPE, "1024", "施工（慢行）"),
    SIGN_TYPE_1025(DictTypeEnums.SIGN_TYPE, "1025", "事故多发段"),
    SIGN_TYPE_1026(DictTypeEnums.SIGN_TYPE, "1026", "让行"),
    SIGN_TYPE_1027(DictTypeEnums.SIGN_TYPE, "1027", "事故路段"),
    SIGN_TYPE_1028(DictTypeEnums.SIGN_TYPE, "1028", "提示"),
    SIGN_TYPE_1029(DictTypeEnums.SIGN_TYPE, "1029", "提示（沉降观测段）"),
    SIGN_TYPE_1030(DictTypeEnums.SIGN_TYPE, "1030", "提示（多雾慢行）"),
    SIGN_TYPE_1031(DictTypeEnums.SIGN_TYPE, "1031", "提示（高填方沉降段）"),
    SIGN_TYPE_1032(DictTypeEnums.SIGN_TYPE, "1032", "提示（减速慢行）"),
    SIGN_TYPE_1033(DictTypeEnums.SIGN_TYPE, "1033", "提示（前方测速）"),
    SIGN_TYPE_1034(DictTypeEnums.SIGN_TYPE, "1034", "提示（下坡）"),
    SIGN_TYPE_1035(DictTypeEnums.SIGN_TYPE, "1035", "停"),
    SIGN_TYPE_1036(DictTypeEnums.SIGN_TYPE, "1036", "停车区"),
    SIGN_TYPE_1037(DictTypeEnums.SIGN_TYPE, "1037", "下陡坡"),
    SIGN_TYPE_1038(DictTypeEnums.SIGN_TYPE, "1038", "限高"),
    SIGN_TYPE_1039(DictTypeEnums.SIGN_TYPE, "1039", "限重"),
    SIGN_TYPE_1040(DictTypeEnums.SIGN_TYPE, "1040", "向右急弯"),
    SIGN_TYPE_1041(DictTypeEnums.SIGN_TYPE, "1041", "易结冰路段"),
    SIGN_TYPE_1042(DictTypeEnums.SIGN_TYPE, "1042", "雨雾冰雪段"),
    SIGN_TYPE_1043(DictTypeEnums.SIGN_TYPE, "1043", "临时施工（变窄）"),
    SIGN_TYPE_1044(DictTypeEnums.SIGN_TYPE, "1044", "指路（高速路口）"),
    SIGN_TYPE_1045(DictTypeEnums.SIGN_TYPE, "1045", "学校"),

    MONITOR_ALARM_EVENT_STATUS_1(DictTypeEnums.MONITOR_ALARM_EVENT_STATUS, "1", "未处置"),
    MONITOR_ALARM_EVENT_STATUS_2(DictTypeEnums.MONITOR_ALARM_EVENT_STATUS, "2", "进入处置"),
    MONITOR_ALARM_EVENT_STATUS_3(DictTypeEnums.MONITOR_ALARM_EVENT_STATUS, "3", "误报，关闭"),
    MONITOR_ALARM_EVENT_STATUS_4(DictTypeEnums.MONITOR_ALARM_EVENT_STATUS, "4", "非交通类，线下转派"),
    MONITOR_ALARM_EVENT_STATUS_5(DictTypeEnums.MONITOR_ALARM_EVENT_STATUS, "5", "处置完成"),

    ST_HAZE(DictTypeEnums.MONITOR_ALARM_EVENT_CODE, "ST_HAZE", "团雾"),
    ST_CITY_FIRE(DictTypeEnums.MONITOR_ALARM_EVENT_CODE, "ST_CITY_FIRE", "烟火"),
    ST_CARS_DROPPING_TRASH(DictTypeEnums.MONITOR_ALARM_EVENT_CODE, "ST_CARS_DROPPING_TRASH", "车辆抛洒"),
    ROAD_CONGESTION(DictTypeEnums.MONITOR_ALARM_EVENT_CODE, "ROAD_CONGESTION", "拥堵"),
    ST_MAINTENANCE(DictTypeEnums.MONITOR_ALARM_EVENT_CODE, "ST_MAINTENANCE", "养护施工"),
    PEDESTRIAN_RIDER_INTRUDE_ROAD(DictTypeEnums.MONITOR_ALARM_EVENT_CODE, "PEDESTRIAN_RIDER_INTRUDE_ROAD", "人非闯入"),


    PIER_TYPE(DictTypeEnums.GUARDRAIL, "1", "墩式"),
    PILE_TYPE(DictTypeEnums.GUARDRAIL, "2", "桩式"),
    WALL_TYPE(DictTypeEnums.GUARDRAIL, "3", "墙式"),
    NET_TYPE(DictTypeEnums.GUARDRAIL, "4", "网式"),
    MOBILE_TYPE(DictTypeEnums.GUARDRAIL, "5", "活动式"),
    GUARDRAIL_OTHER(DictTypeEnums.GUARDRAIL, "9", "其他"),
    TWO_WAVES_TYPE(DictTypeEnums.GUARDRAIL, "6", "两波"),
    THREE_WAVES_TYPE(DictTypeEnums.GUARDRAIL, "7", "三波"),

    LEFT_SIDE_DOWNHILL(DictTypeEnums.SIGN_POSITION, "01", "左侧(下行方向)"),
    RIGHT_SIDE_UPHILL(DictTypeEnums.SIGN_POSITION, "02", "右侧(上行方向)"),
    ABOVE_OVERPASS(DictTypeEnums.SIGN_POSITION, "03", "上方(跨越,向上)"),
    BELOW_UNDERGROUND(DictTypeEnums.SIGN_POSITION, "04", "下方(向下、地下)"),
    ROAD_CENTER(DictTypeEnums.SIGN_POSITION, "05", "路中"),
    CROSS_MIDDLE(DictTypeEnums.SIGN_POSITION, "06", "穿越(中穿)"),
    BOTH_SIDES(DictTypeEnums.SIGN_POSITION, "07", "两侧"),
    CENTRAL_MEDIAN(DictTypeEnums.SIGN_POSITION, "08", "中央绿化带"),
    LEFT_MEDIAN(DictTypeEnums.SIGN_POSITION, "09", "左隔离带"),
    MIDDLE_MEDIAN(DictTypeEnums.SIGN_POSITION, "10", "中隔离带"),
    RIGHT_MEDIAN(DictTypeEnums.SIGN_POSITION, "11", "右隔离带"),
    OTHER_POSITION(DictTypeEnums.SIGN_POSITION, "19", "其他"),


    HIGH_SLOPE(DictTypeEnums.NATURAL_DISASTERS, "1", "公路高边坡"),
    COLLAPSE(DictTypeEnums.NATURAL_DISASTERS, "2", "崩塌"),
    LANDSLIDE(DictTypeEnums.NATURAL_DISASTERS, "3", "滑坡"),
    MUDSLIDE(DictTypeEnums.NATURAL_DISASTERS, "4", "泥石流"),
    SUBSIDENCE_AND_COLLAPSE(DictTypeEnums.NATURAL_DISASTERS, "5", "沉陷与塌陷"),
    WATER_DAMAGE(DictTypeEnums.NATURAL_DISASTERS, "6", "水毁"),
    OTHER_DISASTERS(DictTypeEnums.NATURAL_DISASTERS, "9", "其他"),

    SFWCFLD_TRUE(DictTypeEnums.SFWCFLD, "1", "是"),

    SFWCFLD_FALSE(DictTypeEnums.SFWCFLD, "2", "否"),

    NATIONAL_HIGHWAY(DictTypeEnums.ADMINISTRATION_GRADE, "G", "国道"),

    PROVINCIAL_HIGHWAY(DictTypeEnums.ADMINISTRATION_GRADE, "S", "省道"),

    COUNTY_HIGHWAY(DictTypeEnums.ADMINISTRATION_GRADE, "X", "县道"),

    COUNTRY_HIGHWAY(DictTypeEnums.ADMINISTRATION_GRADE, "Y", "乡道"),

    VILLAGE_HIGHWAY(DictTypeEnums.ADMINISTRATION_GRADE, "C", "村道"),

    COMBINED_HIGHWAY(DictTypeEnums.ADMINISTRATION_GRADE, "W", "通组路"),

    DEDICATED_HIGHWAY(DictTypeEnums.ADMINISTRATION_GRADE, "Z", "专用公路"),

    SAND_STONE_PAVEMENT(DictTypeEnums.PAVEMENT_TYPE, "31", "碎、砾石( 泥结或级配)"),

    BITUMINOUS_CONCRETE(DictTypeEnums.PAVEMENT_TYPE, "11", "沥青混凝土"),

    CEMENT_CONCRETE(DictTypeEnums.PAVEMENT_TYPE, "12", "水泥混凝土"),

    ASPHALT_PENETRATION(DictTypeEnums.PAVEMENT_TYPE, "21", "沥青贯入式"),

    ASPHALTIC_MACADAM(DictTypeEnums.PAVEMENT_TYPE, "22", "沥青碎石"),

    BITUMINOUS_SURFACE_TREATMENT(DictTypeEnums.PAVEMENT_TYPE, "23", "沥青表名处治"),

    SUI_OR_LI(DictTypeEnums.PAVEMENT_TYPE, "31", "碎、砾石（泥结或级配）"),

    SEMI_NEAT_STONE(DictTypeEnums.PAVEMENT_TYPE, "32", "半整齐石块"),

    OTHER_GRANULAR_MATERIALS(DictTypeEnums.PAVEMENT_TYPE, "33", "其他粒料"),

    GRANULAR_REINFORCED_SOIL(DictTypeEnums.PAVEMENT_TYPE, "41", "粒料加固土"),

    LOCAL_MATERIALS_OR_IMPROVE_SOIL(DictTypeEnums.PAVEMENT_TYPE, "42", "其他当地材料加固或改善土"),

    NO_PAVEMENT(DictTypeEnums.PAVEMENT_TYPE, "50", "无路面(未铺装的路面)"),

    OTHER(DictTypeEnums.PAVEMENT_TYPE, "90", "其他"),

    EXPRESSWAY(DictTypeEnums.TECHNICAL_GRADE, "10", "高速公路"),

    FIRST_CLASS_ROAD(DictTypeEnums.TECHNICAL_GRADE, "11", "一级公路"),

    SECONDARY_ROAD(DictTypeEnums.TECHNICAL_GRADE, "12", "二级公路"),

    TERTIARY_ROAD(DictTypeEnums.TECHNICAL_GRADE, "13", "三级公路"),

    FOURTH_CLASS_ROAD(DictTypeEnums.TECHNICAL_GRADE, "14", "四级公路"),

    OUTER_ROAD(DictTypeEnums.TECHNICAL_GRADE, "30", "等外公路"),

    Highway_I(DictTypeEnums.DESIGN_LOAD_LEVEL, "1", "公路-I"),

    Highway_II(DictTypeEnums.DESIGN_LOAD_LEVEL, "2", "公路-II"),

    CAR_OVER_20_LEVELS(DictTypeEnums.DESIGN_LOAD_LEVEL, "3", "汽车超20级"),

    CAR_20_LEVELS(DictTypeEnums.DESIGN_LOAD_LEVEL, "4", "汽车-20级"),

    CAR_15_LEVELS(DictTypeEnums.DESIGN_LOAD_LEVEL, "5", "汽车-15级"),

    CAR_13_LEVELS(DictTypeEnums.DESIGN_LOAD_LEVEL, "6", "汽车-13级"),

    CAR_10_LEVELS(DictTypeEnums.DESIGN_LOAD_LEVEL, "7", "汽车-10级"),

    CAR_BELOW_10_LEVELS(DictTypeEnums.DESIGN_LOAD_LEVEL, "9", "低于汽车-10级"),

    FIRST_TYPE(DictTypeEnums.LEVEL_TECHNICAL_BRIDGE, "1", "一类"),

    SECOND_TYPE(DictTypeEnums.LEVEL_TECHNICAL_BRIDGE, "2", "二类"),

    THIRD_TYPE(DictTypeEnums.LEVEL_TECHNICAL_BRIDGE, "3", "三类"),

    FOURTH_TYPE(DictTypeEnums.LEVEL_TECHNICAL_BRIDGE, "4", "四类"),

    FIFTH_TYPE(DictTypeEnums.LEVEL_TECHNICAL_BRIDGE, "5", "五类"),

    UNRATED(DictTypeEnums.LEVEL_TECHNICAL_BRIDGE, "9", "未评级"),

    NOT_NAVIGABLE(DictTypeEnums.NAVIGABLE_LEVEL, "10", "不通航"),

    First_CLASS_navigation(DictTypeEnums.NAVIGABLE_LEVEL, "11", "一级通航"),

    SECOND_CLASS_NAVIGATION(DictTypeEnums.NAVIGABLE_LEVEL, "12", "二级通航"),

    TERTIARY_NAVIGATION(DictTypeEnums.NAVIGABLE_LEVEL, "13", "三级通航"),

    FOURTH_CLASS_NAVIGABLE(DictTypeEnums.NAVIGABLE_LEVEL, "14", "四级通航"),

    FIFTH_CLASS_NAVIGABLE(DictTypeEnums.NAVIGABLE_LEVEL, "15", "五级通航"),

    SIXTH_CLASS_NAVIGABLE(DictTypeEnums.NAVIGABLE_LEVEL, "16", "六级通航"),

    SEVENTH_CLASS_NAVIGABLE(DictTypeEnums.NAVIGABLE_LEVEL, "17", "七级通航"),

    BELOW_SEVENTH_CLASS_NAVIGATION(DictTypeEnums.NAVIGABLE_LEVEL, "18", "七级通航以下"),

    PIPE_CULVERT(DictTypeEnums.CULVERT_TYPE, "1", "管涵"),

    ARCH_CULVERT(DictTypeEnums.CULVERT_TYPE, "2", "拱涵"),

    BOX_CULVERT(DictTypeEnums.CULVERT_TYPE, "3", "箱涵"),

    BOARD_CULVERT(DictTypeEnums.CULVERT_TYPE, "4", "板涵"),

    OTHER_CULVERT(DictTypeEnums.CULVERT_TYPE, "5", "其他"),

    WING_WALL_ORTHOGONAL_CAVITY(DictTypeEnums.CONSTRUCTION_TYPE_OF_ENTRANCE, "1", "翼墙式正交洞口"),

    WING_WALL_INTERSECTION_CAVITY(DictTypeEnums.CONSTRUCTION_TYPE_OF_ENTRANCE, "2", "翼墙式斜交洞口"),

    NO_WING_WALL_ORTHOGONAL(DictTypeEnums.CONSTRUCTION_TYPE_OF_ENTRANCE, "3", "无翼墙正交洞口"),

    NO_WING_WALL_INTERSECTION(DictTypeEnums.CONSTRUCTION_TYPE_OF_ENTRANCE, "4", "无翼墙斜交洞口"),

    END_WALL_CAVITY(DictTypeEnums.CONSTRUCTION_TYPE_OF_ENTRANCE, "5", "端墙式洞口"),

    PILLAR_TYPE_CAVITY(DictTypeEnums.CONSTRUCTION_TYPE_OF_ENTRANCE, "6", "柱式洞口"),

    STEP_HOLE(DictTypeEnums.CONSTRUCTION_TYPE_OF_ENTRANCE, "7", "台阶式洞口"),

    RING_FRAME_CAVITY(DictTypeEnums.CONSTRUCTION_TYPE_OF_ENTRANCE, "8", "环框式洞口"),

    OTHER_CAVITY(DictTypeEnums.CONSTRUCTION_TYPE_OF_ENTRANCE, "9", "其他"),

    CAVE_ROOF_DRAINAGE(DictTypeEnums.TUNNEL_DRAINAGE_TYPE, "1", "洞顶排水"),

    ROADSIDE_DRAINAGE(DictTypeEnums.TUNNEL_DRAINAGE_TYPE, "2", "洞内路侧排水"),

    HORIZONTAL_DRAINAGE(DictTypeEnums.TUNNEL_DRAINAGE_TYPE, "3", "洞内路面横向排水"),

    UNDERGROUND_DRAINAGE(DictTypeEnums.TUNNEL_DRAINAGE_TYPE, "4", "洞内地下排水"),

    CAVE_ENTRANCE_AND_OPEN_HOLE(DictTypeEnums.TUNNEL_DRAINAGE_TYPE, "5", "洞口及明洞防排水"),

    BLIND_TRENCH_DRAINAGE(DictTypeEnums.TUNNEL_DRAINAGE_TYPE, "6", "洞口边墙盲沟排水"),

    OPEN_HOLE_BLIND_TRENCH_DRAINAGE(DictTypeEnums.TUNNEL_DRAINAGE_TYPE, "7", "明洞盲沟排水"),

    OTHER_DRAINAGE(DictTypeEnums.TUNNEL_DRAINAGE_TYPE, "9", "其他"),

    STRAIGHT_SINGLE_DOME(DictTypeEnums.OUTLINE_TYPE, "1", "直墙式单心圆拱"),

    STRAIGHT_DOUBLE_DOME(DictTypeEnums.OUTLINE_TYPE, "2", "直墙式坦顶双心圆拱"),

    STRAIGHT_THREE_DOME(DictTypeEnums.OUTLINE_TYPE, "3", "直墙式尖顶三心圆拱"),

    CURVED_SINGLE_DOME(DictTypeEnums.OUTLINE_TYPE, "4", "曲墙式单心圆拱"),

    CURVED_DOUBLE_DOME(DictTypeEnums.OUTLINE_TYPE, "5", "曲墙式坦顶双心圆拱"),

    CURVED_THREE_DOME(DictTypeEnums.OUTLINE_TYPE, "6", "曲墙式尖顶三心圆拱"),

    MULTI_CENTERED_DOME(DictTypeEnums.OUTLINE_TYPE, "7", "多心圆拱"),

    OTHER_DOME(DictTypeEnums.OUTLINE_TYPE, "8", "其他"),

    NATURAL_VENTILATION(DictTypeEnums.TUNNEL_VENTILATION_TYPE, "10", "自然通风"),

    MECHANICAL_VENTILATION(DictTypeEnums.TUNNEL_VENTILATION_TYPE, "11", "机械通风"),

    MIXED_VENTILATION(DictTypeEnums.TUNNEL_VENTILATION_TYPE, "12", "混合通风"),

    NO_LIGHTING(DictTypeEnums.TUNNEL_LIGHTING_CONDITION_TYPE, "10", "无照明"),

    ALL_LIGHTING(DictTypeEnums.TUNNEL_LIGHTING_CONDITION_TYPE, "11", "全部照明"),

    PARTIAL_LIGHTING(DictTypeEnums.TUNNEL_LIGHTING_CONDITION_TYPE, "12", "局部照明"),

    TRAFFIC_SIGN(DictTypeEnums.TRAFFIC_TYPE, "1", "标志"),

    TRAFFIC_LINE(DictTypeEnums.TRAFFIC_TYPE, "2", "标线"),

    TRAFFIC_PROTECT(DictTypeEnums.TRAFFIC_TYPE, "3", "防护设施"),

    TRAFFIC_GLARE(DictTypeEnums.TRAFFIC_TYPE, "4", "防眩设施"),

    TRAFFIC_QUARANTINE(DictTypeEnums.TRAFFIC_TYPE, "5", "隔离设施"),

    TRAFFIC_LIMITATION(DictTypeEnums.TRAFFIC_TYPE, "6", "限制设施"),

    TRAFFIC_OTHER(DictTypeEnums.TRAFFIC_TYPE, "7", "其他设施"),

    MQ_NOT_FINISH(DictTypeEnums.MQ_DEAL_WITH_STATUS, "0", "未完成"),

    MQ_FINISH(DictTypeEnums.MQ_DEAL_WITH_STATUS, "1", "完成"),

    MQ_RETRY_ERROR(DictTypeEnums.MQ_DEAL_WITH_STATUS, "2", "重试异常"),

    MQ_ERROR(DictTypeEnums.MQ_DEAL_WITH_STATUS, "3", "处理异常"),

    ROUTE_CORRECT(DictTypeEnums.TENANT_PERMISSION, "1", "道路纠偏权限"),

    PATROL_CAR_K_TYPE(DictTypeEnums.PATROL_CAR_TYPE, "K", "客车"),

    PATROL_CAR_H_TYPE(DictTypeEnums.PATROL_CAR_TYPE, "H", "货车"),

    PATROL_CAR_Q_TYPE(DictTypeEnums.PATROL_CAR_TYPE, "Q", "牵引车"),

    PATROL_CAR_Z_TYPE(DictTypeEnums.PATROL_CAR_TYPE, "Z", "专项作业车"),
    PATROL_CAR_D_TYPE(DictTypeEnums.PATROL_CAR_TYPE, "D", "电车"),
    PATROL_CAR_M_TYPE(DictTypeEnums.PATROL_CAR_TYPE, "M", "摩托车"),
    PATROL_CAR_N_TYPE(DictTypeEnums.PATROL_CAR_TYPE, "N", "三轮汽车"),
    PATROL_CAR_T_TYPE(DictTypeEnums.PATROL_CAR_TYPE, "T", "拖拉机"),
    PATROL_CAR_J_TYPE(DictTypeEnums.PATROL_CAR_TYPE, "J", "轮式机械"),
    PATROL_CAR_G_TYPE(DictTypeEnums.PATROL_CAR_TYPE, "G", "全挂车"),
    PATROL_CAR_B_TYPE(DictTypeEnums.PATROL_CAR_TYPE, "B", "半挂车"),
    PATROL_CAR_X_TYPE(DictTypeEnums.PATROL_CAR_TYPE, "X", "其他"),


    CAR_TERMINAL_NVR(DictTypeEnums.PATROL_DEVICE_TYPE, "1", "车载采集终端-NVR"),

    CAR_TERMINAL_CAMERA(DictTypeEnums.PATROL_DEVICE_TYPE, "2", "车载采集终端-摄像头"),

    CAR_TERMINAL_TABLET(DictTypeEnums.PATROL_DEVICE_TYPE, "3", "平板电脑"),


    CONSTRUCTION_PROJECT_TYPE_TRAIN(DictTypeEnums.CONSTRUCTION_PROJECT_TYPE, "1", "铁路"),
    CONSTRUCTION_PROJECT_TYPE_ROAD(DictTypeEnums.CONSTRUCTION_PROJECT_TYPE, "2", "公路"),
    CONSTRUCTION_PROJECT_TYPE_FERRY(DictTypeEnums.CONSTRUCTION_PROJECT_TYPE, "3", "水运"),
    CONSTRUCTION_PROJECT_TYPE_AIRPLANE(DictTypeEnums.CONSTRUCTION_PROJECT_TYPE, "4", "航空"),
    CONSTRUCTION_PROJECT_TYPE_PIVOT(DictTypeEnums.CONSTRUCTION_PROJECT_TYPE, "5", "枢纽"),
    CONSTRUCTION_PROJECT_TYPE_SMART_TRANSPORTATION(DictTypeEnums.CONSTRUCTION_PROJECT_TYPE, "6", "智慧交通"),
    CONSTRUCTION_PROJECT_TYPE_OTHER(DictTypeEnums.CONSTRUCTION_PROJECT_TYPE, "7", "其他"),

    CHECK_RESULT_01_TYPE(DictTypeEnums.CHECK_RESULT_TYPE, "01", "未发现问题终止检查并想监管对象告知检查结果"),
    CHECK_RESULT_02_TYPE(DictTypeEnums.CHECK_RESULT_TYPE, "02", "发现问题做出责令改正等行政命令"),
    CHECK_RESULT_03_TYPE(DictTypeEnums.CHECK_RESULT_TYPE, "03", "发现问题做出行政指导"),
    CHECK_RESULT_04_TYPE(DictTypeEnums.CHECK_RESULT_TYPE, "04", "发现问题做出行政处罚决定"),
    CHECK_RESULT_05_TYPE(DictTypeEnums.CHECK_RESULT_TYPE, "05", "发现问题做出行政强制决定"),
    CHECK_RESULT_06_TYPE(DictTypeEnums.CHECK_RESULT_TYPE, "06", "发现问题做出其他具体行政行为"),

    COMPLAINT_EMOTING_10_TYPE(DictTypeEnums.COMPLAINT_EMOTING_TYPE, "10", "开心"),
    COMPLAINT_EMOTING_20_TYPE(DictTypeEnums.COMPLAINT_EMOTING_TYPE, "20", "激动"),
    COMPLAINT_EMOTING_30_TYPE(DictTypeEnums.COMPLAINT_EMOTING_TYPE, "30", "生气"),
    COMPLAINT_EMOTING_40_TYPE(DictTypeEnums.COMPLAINT_EMOTING_TYPE, "40", "难过"),
    COMPLAINT_EMOTING_50_TYPE(DictTypeEnums.COMPLAINT_EMOTING_TYPE, "50", "疑惑"),
    COMPLAINT_EMOTING_60_TYPE(DictTypeEnums.COMPLAINT_EMOTING_TYPE, "60", "平淡"),
    COMPLAINT_EMOTING_70_TYPE(DictTypeEnums.COMPLAINT_EMOTING_TYPE, "70", "其他"),

    COMPLAINT_DEAL_10_TYPE(DictTypeEnums.COMPLAINT_DEAL_TYPE, "10", "直接办结"),
    COMPLAINT_DEAL_20_TYPE(DictTypeEnums.COMPLAINT_DEAL_TYPE, "20", "交办"),
    COMPLAINT_DEAL_30_TYPE(DictTypeEnums.COMPLAINT_DEAL_TYPE, "30", "派单员重派"),
    COMPLAINT_DEAL_40_TYPE(DictTypeEnums.COMPLAINT_DEAL_TYPE, "40", "结果审核重派"),
    COMPLAINT_DEAL_50_TYPE(DictTypeEnums.COMPLAINT_DEAL_TYPE, "50", "回访不满意重派"),


    COMPLAINT_402_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0402", "停车管理"),
    COMPLAINT_403_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0403", "道路运输"),
    COMPLAINT_404_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0404", "交通设施"),
    COMPLAINT_405_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0405", "疏导措施"),
    COMPLAINT_406_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0406", "道路拥堵"),
    COMPLAINT_407_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0407", "交通事故"),
    COMPLAINT_408_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0408", "违法运营"),
    COMPLAINT_410_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0410", "通行证办理"),
    COMPLAINT_411_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0411", "公路管理"),
    COMPLAINT_412_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0412", "车驾管业务"),
    COMPLAINT_413_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0413", "水路管理"),
    COMPLAINT_414_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0414", "高速公路"),
    COMPLAINT_416_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0416", "海事"),
    COMPLAINT_417_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0417", "求助打捞"),
    COMPLAINT_418_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0418", "铁路"),
    COMPLAINT_419_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0419", "民航"),
    COMPLAINT_420_TYPE(DictTypeEnums.COMPLAINT_TYPE, "0420", "邮政"),

    BLOCKING_EVENTS_REASON_1_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "1", "公路施工养护"),
    BLOCKING_EVENTS_REASON_2_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "2", "桥梁施工养护"),
    BLOCKING_EVENTS_REASON_3_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "3", "隧道施工养护"),
    BLOCKING_EVENTS_REASON_4_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "4", "重大活动"),
    BLOCKING_EVENTS_REASON_5_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "5", "崩塌"),
    BLOCKING_EVENTS_REASON_6_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "6", "滑坡"),
    BLOCKING_EVENTS_REASON_7_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "7", "洪水"),
    BLOCKING_EVENTS_REASON_8_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "8", "泥石流"),
    BLOCKING_EVENTS_REASON_9_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "9", "地面塌陷、沉降或开裂"),
    BLOCKING_EVENTS_REASON_10_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "10", "地震"),
    BLOCKING_EVENTS_REASON_11_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "11", "海啸"),
    BLOCKING_EVENTS_REASON_12_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "12", "降雨（积水）"),
    BLOCKING_EVENTS_REASON_13_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "13", "雾霾"),
    BLOCKING_EVENTS_REASON_14_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "14", "降雪（积雪）"),
    BLOCKING_EVENTS_REASON_15_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "15", "风吹雪"),
    BLOCKING_EVENTS_REASON_16_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "16", "结冰"),
    BLOCKING_EVENTS_REASON_17_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "17", "台风"),
    BLOCKING_EVENTS_REASON_18_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "18", "大风（横风）"),
    BLOCKING_EVENTS_REASON_19_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "19", "沙尘"),
    BLOCKING_EVENTS_REASON_20_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "20", "冰雹"),
    BLOCKING_EVENTS_REASON_21_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "21", "高温"),
    BLOCKING_EVENTS_REASON_22_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "22", "车辆交通事故"),
    BLOCKING_EVENTS_REASON_23_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "23", "危险品泄漏"),
    BLOCKING_EVENTS_REASON_24_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "24", "车辆故障"),
    BLOCKING_EVENTS_REASON_25_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "25", "涉桥事故"),
    BLOCKING_EVENTS_REASON_26_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "26", "涉隧事故"),
    BLOCKING_EVENTS_REASON_27_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "27", "抢修作业与临时性施工养护"),
    BLOCKING_EVENTS_REASON_28_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "28", "车流量大"),
    BLOCKING_EVENTS_REASON_29_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "29", "收费争议"),
    BLOCKING_EVENTS_REASON_30_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "30", "执法事件"),
    BLOCKING_EVENTS_REASON_31_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "31", "服务区事件"),
    BLOCKING_EVENTS_REASON_32_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "32", "执行警备任务"),
    BLOCKING_EVENTS_REASON_33_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "33", "设备故障"),
    BLOCKING_EVENTS_REASON_34_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "34", "治安事件"),
    BLOCKING_EVENTS_REASON_35_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "35", "安检"),
    BLOCKING_EVENTS_REASON_36_TYPE(DictTypeEnums.BLOCKING_EVENTS_REASON_TYPE, "36", "其他"),

    BLOCKING_EVENTS_DIRECTION_UPWARD(DictTypeEnums.BLOCKING_EVENTS_DIRECTION, "1", "上行"),
    BLOCKING_EVENTS_DIRECTION_DOWNWARD(DictTypeEnums.BLOCKING_EVENTS_DIRECTION, "2", "下行"),
    BLOCKING_EVENTS_DIRECTION_TWO_WAY(DictTypeEnums.BLOCKING_EVENTS_DIRECTION, "3", "双向"),

    T_MAN(DictTypeEnums.T_SEX, "1", "男"),

    T_WOMAN(DictTypeEnums.T_SEX, "2", "女"),

    T_NONE(DictTypeEnums.T_SEX, "0", "未知"),

    VEHICLE_OPERATION_10_STATUS(DictTypeEnums.VEHICLE_OPERATION_STATUS, "10", "营运"),
    VEHICLE_OPERATION_21_STATUS(DictTypeEnums.VEHICLE_OPERATION_STATUS, "21", "停运"),
    VEHICLE_OPERATION_22_STATUS(DictTypeEnums.VEHICLE_OPERATION_STATUS, "22", "挂失"),
    VEHICLE_OPERATION_31_STATUS(DictTypeEnums.VEHICLE_OPERATION_STATUS, "31", "迁出（过户）"),
    VEHICLE_OPERATION_32_STATUS(DictTypeEnums.VEHICLE_OPERATION_STATUS, "32", "迁出（转籍）"),
    VEHICLE_OPERATION_33_STATUS(DictTypeEnums.VEHICLE_OPERATION_STATUS, "33", "报废"),
    VEHICLE_OPERATION_34_STATUS(DictTypeEnums.VEHICLE_OPERATION_STATUS, "34", "歇业"),
    VEHICLE_OPERATION_35_STATUS(DictTypeEnums.VEHICLE_OPERATION_STATUS, "35", "备案到外省"),
    VEHICLE_OPERATION_80_STATUS(DictTypeEnums.VEHICLE_OPERATION_STATUS, "80", "注销"),
    VEHICLE_OPERATION_90_STATUS(DictTypeEnums.VEHICLE_OPERATION_STATUS, "90", "其他"),

    VEHIC_TYPE_K11_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K11", "大型普通客车"),
    VEHIC_TYPE_K33_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K33", "小型轿车"),
    VEHIC_TYPE_K39_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K39", "小型面包车"),
    VEHIC_TYPE_H1D_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H1D", "重型平板自卸货车"),
    VEHIC_TYPE_K12_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K12", "大型双层客车"),
    VEHIC_TYPE_H1E_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H1E", "重型集装箱自卸货车"),
    VEHIC_TYPE_H1J_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H1J", "重型专门用途货车"),
    VEHIC_TYPE_K13_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K13", "大型卧铺客车"),
    VEHIC_TYPE_H1F_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H1F", "重型特殊结构自卸货车"),
    VEHIC_TYPE_K14_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K14", "大型铰接客车"),
    VEHIC_TYPE_H1G_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H1G", "重型仓栅式自卸货车"),
    VEHIC_TYPE_K15_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K15", "大型越野客车"),
    VEHIC_TYPE_H21_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H21", "中型栏板货车"),
    VEHIC_TYPE_B15_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B15", "重型集装箱半挂车"),
    VEHIC_TYPE_K16_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K16", "大型轿车"),
    VEHIC_TYPE_H22_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H22", "中型厢式货车"),
    VEHIC_TYPE_K17_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K17", "大型专用客车"),
    VEHIC_TYPE_H23_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H23", "中型封闭式货车"),
    VEHIC_TYPE_K18_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K18", "大型专用校车"),
    VEHIC_TYPE_H24_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H24", "中型罐式货车"),
    VEHIC_TYPE_K21_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K21", "中型普通客车"),
    VEHIC_TYPE_H25_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H25", "中型平板货车"),
    VEHIC_TYPE_K22_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K22", "中型双层客车"),
    VEHIC_TYPE_H26_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H26", "中型集装箱车"),
    VEHIC_TYPE_K23_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K23", "中型卧铺客车"),
    VEHIC_TYPE_H27_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H27", "中型自卸货车"),
    VEHIC_TYPE_K24_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K24", "中型铰接客车"),
    VEHIC_TYPE_H28_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H28", "中型特殊结构货车"),
    VEHIC_TYPE_K25_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K25", "中型越野客车"),
    VEHIC_TYPE_H29_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H29", "中型仓栅式货车"),
    VEHIC_TYPE_K26_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K26", "中型轿车"),
    VEHIC_TYPE_H2A_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H2A", "中型车辆运输车"),
    VEHIC_TYPE_K27_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K27", "中型专用客车"),
    VEHIC_TYPE_H2B_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H2B", "中型厢式自卸货车"),
    VEHIC_TYPE_K28_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K28", "中型专用校车"),
    VEHIC_TYPE_H2C_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H2C", "中型罐式自卸货车"),
    VEHIC_TYPE_K31_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K31", "小型普通客车"),
    VEHIC_TYPE_H2D_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H2D", "中型平板自卸货车"),
    VEHIC_TYPE_K32_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K32", "小型越野客车"),
    VEHIC_TYPE_H2E_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H2E", "中型集装箱自卸货车"),
    VEHIC_TYPE_K41_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K41", "微型普通客车"),
    VEHIC_TYPE_H2F_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H2F", "中型特殊结构自卸货车"),
    VEHIC_TYPE_K42_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K42", "微型越野客车"),
    VEHIC_TYPE_H2G_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H2G", "中型仓栅式自卸货车"),
    VEHIC_TYPE_K43_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K43", "微型轿车"),
    VEHIC_TYPE_H31_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H31", "轻型栏板货车"),
    VEHIC_TYPE_K49_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "K49", "微型面包车"),
    VEHIC_TYPE_H32_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H32", "轻型厢式货车"),
    VEHIC_TYPE_H11_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H11", "重型栏板货车"),
    VEHIC_TYPE_H33_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H33", "轻型封闭式货车"),
    VEHIC_TYPE_H12_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H12", "重型厢式货车"),
    VEHIC_TYPE_H34_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H34", "轻型罐式货车"),
    VEHIC_TYPE_H13_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H13", "重型封闭式货车"),
    VEHIC_TYPE_H35_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H35", "轻型平板货车"),
    VEHIC_TYPE_H3J_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H3J", "轻型专门用途货车"),
    VEHIC_TYPE_H14_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H14", "重型罐式货车"),
    VEHIC_TYPE_H37_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H37", "轻型自卸货车"),
    VEHIC_TYPE_H15_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H15", "重型平板货车"),
    VEHIC_TYPE_H38_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H38", "轻型特殊结构货车"),
    VEHIC_TYPE_H16_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H16", "重型集装箱车"),
    VEHIC_TYPE_H39_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H39", "轻型仓栅式货车"),
    VEHIC_TYPE_H17_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H17", "重型自卸货车"),
    VEHIC_TYPE_H3A_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H3A", "轻型车辆运输车"),
    VEHIC_TYPE_H18_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H18", "重型特殊结构货车"),
    VEHIC_TYPE_H3B_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H3B", "轻型厢式自卸货车"),
    VEHIC_TYPE_H19_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H19", "重型仓栅式货车"),
    VEHIC_TYPE_H3C_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H3C", "轻型罐式自卸货车"),
    VEHIC_TYPE_H1A_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H1A", "重型车辆运输车"),
    VEHIC_TYPE_H3D_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H3D", "轻型平板自卸货车"),
    VEHIC_TYPE_H1B_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H1B", "重型厢式自卸货车"),
    VEHIC_TYPE_H3F_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H3F", "轻型特殊结构自卸货车"),
    VEHIC_TYPE_H1C_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H1C", "重型罐式自卸货车"),
    VEHIC_TYPE_H3G_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H3G", "轻型仓栅式自卸货车"),
    VEHIC_TYPE_H41_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H41", "微型栏板货车"),
    VEHIC_TYPE_Z52_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Z52", "重型载货专项作业车"),
    VEHIC_TYPE_H42_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H42", "微型厢式货车"),
    VEHIC_TYPE_Z71_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Z71", "轻型非载货专项作业车"),
    VEHIC_TYPE_H43_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H43", "微型封闭式货车"),
    VEHIC_TYPE_Z72_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Z72", "轻型载货专项作业车"),
    VEHIC_TYPE_H44_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H44", "微型罐式货车"),
    VEHIC_TYPE_D11_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "D11", "无轨电车"),
    VEHIC_TYPE_H45_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H45", "微型自卸货车"),
    VEHIC_TYPE_D12_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "D12", "有轨电车"),
    VEHIC_TYPE_H46_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H46", "微型特殊结构货车"),
    VEHIC_TYPE_M11_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "M11", "普通正三轮摩托车"),
    VEHIC_TYPE_H47_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H47", "微型仓栅式货车"),
    VEHIC_TYPE_M12_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "M12", "轻便正三轮摩托车"),
    VEHIC_TYPE_H4A_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H4A", "微型车辆运输车"),
    VEHIC_TYPE_M13_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "M13", "正三轮载客摩托车"),
    VEHIC_TYPE_H4B_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H4B", "微型厢式自卸货车"),
    VEHIC_TYPE_M14_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "M14", "正三轮载货摩托车"),
    VEHIC_TYPE_H4C_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H4C", "微型罐式自卸货车"),
    VEHIC_TYPE_M15_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "M15", "侧三轮摩托车"),
    VEHIC_TYPE_H4F_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H4F", "微型特殊结构自卸货车"),
    VEHIC_TYPE_M21_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "M21", "普通二轮摩托车"),
    VEHIC_TYPE_H4G_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H4G", "微型仓栅式自卸货车"),
    VEHIC_TYPE_M22_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "M22", "轻便二轮摩托车"),
    VEHIC_TYPE_H51_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H51", "栏板低速货车"),
    VEHIC_TYPE_N11_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "N11", "三轮汽车"),
    VEHIC_TYPE_H52_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H52", "厢式低速货车"),
    VEHIC_TYPE_T11_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "T11", "大型轮式拖拉机"),
    VEHIC_TYPE_H53_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H53", "罐式低速货车"),
    VEHIC_TYPE_T21_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "T21", "小型轮式拖拉机"),
    VEHIC_TYPE_H54_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H54", "自卸低速货车"),
    VEHIC_TYPE_T22_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "T22", "手扶拖拉机"),
    VEHIC_TYPE_H55_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H55", "仓栅式低速货车"),
    VEHIC_TYPE_T23_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "T23", "手扶变形运输机"),
    VEHIC_TYPE_H5B_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H5B", "厢式自卸低速货车"),
    VEHIC_TYPE_J11_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "J11", "轮式装载机械"),
    VEHIC_TYPE_H5C_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "H5C", "罐式自卸低速货车"),
    VEHIC_TYPE_J12_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "J12", "轮式挖掘机械"),
    VEHIC_TYPE_Q11_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Q11", "重型半挂牵引车"),
    VEHIC_TYPE_J13_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "J13", "轮式平地机械"),
    VEHIC_TYPE_Q12_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Q12", "重型全挂牵引车"),
    VEHIC_TYPE_G11_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G11", "重型栏板全挂车"),
    VEHIC_TYPE_Q21_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Q21", "中型半挂牵引车"),
    VEHIC_TYPE_G12_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G12", "重型厢式全挂车"),
    VEHIC_TYPE_Q22_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Q22", "中型全挂牵引车"),
    VEHIC_TYPE_G13_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G13", "重型罐式全挂车"),
    VEHIC_TYPE_Q31_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Q31", "轻型半挂牵引车"),
    VEHIC_TYPE_G14_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G14", "重型平板全挂车"),
    VEHIC_TYPE_Q32_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Q32", "轻型全挂牵引车"),
    VEHIC_TYPE_G15_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G15", "重型集装箱全挂车"),
    VEHIC_TYPE_Z11_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Z11", "大型非载货专项作业车"),
    VEHIC_TYPE_G16_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G16", "重型自卸全挂车"),
    VEHIC_TYPE_Z12_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Z12", "大型载货专项作业车"),
    VEHIC_TYPE_G17_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G17", "重型仓栅式全挂车"),
    VEHIC_TYPE_Z21_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Z21", "中型非载货专项作业车"),
    VEHIC_TYPE_G18_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G18", "重型旅居全挂车"),
    VEHIC_TYPE_Z22_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Z22", "中型载货专项作业车"),
    VEHIC_TYPE_G19_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G19", "重型特殊用途全挂车"),
    VEHIC_TYPE_Z31_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Z31", "小型非载货专项作业车"),
    VEHIC_TYPE_G1A_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G1A", "重型厢式自卸全挂车"),
    VEHIC_TYPE_Z32_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Z32", "小型载货专项作业车"),
    VEHIC_TYPE_G1B_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G1B", "重型罐式自卸全挂车"),
    VEHIC_TYPE_Z41_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Z41", "微型非载货专项作业车"),
    VEHIC_TYPE_G1C_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G1C", "重型平板自卸全挂车"),
    VEHIC_TYPE_Z42_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Z42", "微型载货专项作业车"),
    VEHIC_TYPE_G1D_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G1D", "重型集装箱自卸全挂车"),
    VEHIC_TYPE_Z51_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "Z51", "重型非载货专项作业车"),
    VEHIC_TYPE_G1E_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G1E", "重型仓栅式自卸全挂车"),
    VEHIC_TYPE_G1F_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G1F", "重型特殊用途自卸全挂车"),
    VEHIC_TYPE_B16_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B16", "重型自卸半挂车"),
    VEHIC_TYPE_G21_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G21", "中型栏板全挂车"),
    VEHIC_TYPE_B17_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B17", "重型特殊结构半挂车"),
    VEHIC_TYPE_G22_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G22", "中型厢式全挂车"),
    VEHIC_TYPE_B18_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B18", "重型仓栅式半挂车"),
    VEHIC_TYPE_G23_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G23", "中型罐式全挂车"),
    VEHIC_TYPE_B19_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B19", "重型旅居半挂车"),
    VEHIC_TYPE_G24_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G24", "中型平板全挂车"),
    VEHIC_TYPE_B1A_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B1A", "重型专项作业半挂车"),
    VEHIC_TYPE_G25_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G25", "中型集装箱全挂车"),
    VEHIC_TYPE_B1B_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B1B", "重型低平板半挂车"),
    VEHIC_TYPE_G26_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G26", "中型自卸全挂车"),
    VEHIC_TYPE_B1C_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B1C", "重型车辆运输半挂车"),
    VEHIC_TYPE_G27_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G27", "中型仓栅式全挂车"),
    VEHIC_TYPE_B1D_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B1D", "重型罐式自卸半挂车"),
    VEHIC_TYPE_G28_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G28", "中型旅居全挂车"),
    VEHIC_TYPE_B1E_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B1E", "重型平板自卸半挂车"),
    VEHIC_TYPE_G29_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G29", "中型特殊用途全挂车"),
    VEHIC_TYPE_B1F_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B1F", "重型集装箱自卸半挂车"),
    VEHIC_TYPE_G2A_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G2A", "中型厢式自卸全挂车"),
    VEHIC_TYPE_B1G_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B1G", "重型特殊结构自卸半挂车"),
    VEHIC_TYPE_G2B_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G2B", "中型罐式自卸全挂车"),
    VEHIC_TYPE_B1H_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B1H", "重型仓栅式自卸半挂车"),
    VEHIC_TYPE_G2C_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G2C", "中型平板自卸全挂车"),
    VEHIC_TYPE_B1J_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B1J", "重型专项作业自卸半挂车"),
    VEHIC_TYPE_G2D_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G2D", "中型集装箱自卸全挂车"),
    VEHIC_TYPE_B1K_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B1K", "重型低平板自卸半挂车"),
    VEHIC_TYPE_G2E_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G2E", "中型仓栅式自卸全挂车"),
    VEHIC_TYPE_B1U_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B1U", "重型中置轴旅居挂车"),
    VEHIC_TYPE_G2F_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G2F", "中型特殊用途自卸全挂车"),
    VEHIC_TYPE_B1V_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B1V", "重型中置轴车辆运输挂车"),
    VEHIC_TYPE_G31_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G31", "轻型栏板全挂车"),
    VEHIC_TYPE_B1W_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B1W", "重型中置轴普通挂车"),
    VEHIC_TYPE_G32_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G32", "轻型厢式全挂车"),
    VEHIC_TYPE_B21_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B21", "中型栏板半挂车"),
    VEHIC_TYPE_G33_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G33", "轻型罐式全挂车"),
    VEHIC_TYPE_B22_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B22", "中型厢式半挂车"),
    VEHIC_TYPE_G34_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G34", "轻型平板全挂车"),
    VEHIC_TYPE_B23_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B23", "中型罐式半挂车"),
    VEHIC_TYPE_G35_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G35", "轻型自卸全挂车"),
    VEHIC_TYPE_B24_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B24", "中型平板半挂车"),
    VEHIC_TYPE_G36_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G36", "轻型仓栅式全挂车"),
    VEHIC_TYPE_B25_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B25", "中型集装箱半挂车"),
    VEHIC_TYPE_G37_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G37", "轻型旅居全挂车"),
    VEHIC_TYPE_B26_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B26", "中型自卸半挂车"),
    VEHIC_TYPE_G38_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G38", "轻型特殊用途全挂车"),
    VEHIC_TYPE_B27_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B27", "中型特殊结构半挂车"),
    VEHIC_TYPE_G3A_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G3A", "轻型厢式自卸全挂车"),
    VEHIC_TYPE_B28_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B28", "中型仓栅式半挂车"),
    VEHIC_TYPE_G3B_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G3B", "轻型罐式自卸全挂车"),
    VEHIC_TYPE_B29_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B29", "中型旅居半挂车"),
    VEHIC_TYPE_G3C_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G3C", "轻型平板自卸全挂车"),
    VEHIC_TYPE_B2A_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B2A", "中型专项作业半挂车"),
    VEHIC_TYPE_G3D_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G3D", "轻型集装箱自卸全挂车"),
    VEHIC_TYPE_B2B_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B2B", "中型低平板半挂车"),
    VEHIC_TYPE_G3E_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G3E", "轻型仓栅式自卸全挂车"),
    VEHIC_TYPE_B2C_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B2C", "中型车辆运输半挂车"),
    VEHIC_TYPE_G3F_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "G3F", "轻型特殊用途自卸全挂车"),
    VEHIC_TYPE_B2D_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B2D", "中型罐式自卸半挂车"),
    VEHIC_TYPE_B11_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B11", "重型栏板半挂车"),
    VEHIC_TYPE_B2E_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B2E", "中型平板自卸半挂车"),
    VEHIC_TYPE_B12_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B12", "重型厢式半挂车"),
    VEHIC_TYPE_B2F_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B2F", "中型集装箱自卸半挂车"),
    VEHIC_TYPE_B13_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B13", "重型罐式半挂车"),
    VEHIC_TYPE_B2G_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B2G", "中型特殊结构自卸半挂车"),
    VEHIC_TYPE_B14_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B14", "重型平板半挂车"),
    VEHIC_TYPE_B2H_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B2H", "中型仓栅式自卸半挂车"),
    VEHIC_TYPE_B2J_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B2J", "中型专项作业自卸半挂车"),
    VEHIC_TYPE_B39_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B39", "轻型低平板半挂车"),
    VEHIC_TYPE_B2K_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B2K", "中型低平板自卸半挂车"),
    VEHIC_TYPE_B3C_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B3C", "轻型车辆运输半挂车"),
    VEHIC_TYPE_B2U_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B2U", "中型中置轴旅居挂车"),
    VEHIC_TYPE_B3D_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B3D", "轻型罐式自卸半挂车"),
    VEHIC_TYPE_B2V_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B2V", "中型中置轴车辆运输挂车"),
    VEHIC_TYPE_B3E_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B3E", "轻型平板自卸半挂车"),
    VEHIC_TYPE_B2W_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B2W", "中型中置轴普通挂车"),
    VEHIC_TYPE_B3F_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B3F", "轻型集装箱自卸半挂车"),
    VEHIC_TYPE_B31_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B31", "轻型栏板半挂车"),
    VEHIC_TYPE_B3G_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B3G", "轻型特殊结构自卸半挂车"),
    VEHIC_TYPE_B32_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B32", "轻型厢式半挂车"),
    VEHIC_TYPE_B3H_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B3H", "轻型仓栅式自卸半挂车"),
    VEHIC_TYPE_B33_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B33", "轻型罐式半挂车"),
    VEHIC_TYPE_B3J_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B3J", "轻型专项作业自卸半挂车"),
    VEHIC_TYPE_B34_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B34", "轻型平板半挂车"),
    VEHIC_TYPE_B3K_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B3K", "轻型低平板自卸半挂车"),
    VEHIC_TYPE_B35_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B35", "轻型自卸半挂车"),
    VEHIC_TYPE_B3U_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B3U", "轻型中置轴旅居挂车"),
    VEHIC_TYPE_B36_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B36", "轻型仓栅式半挂车"),
    VEHIC_TYPE_B3V_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B3V", "轻型中置轴车辆运输挂车"),
    VEHIC_TYPE_B37_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B37", "轻型旅居半挂车"),
    VEHIC_TYPE_B3W_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B3W", "轻型中置轴普通挂车"),
    VEHIC_TYPE_B38_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "B38", "轻型专项作业半挂车"),
    VEHIC_TYPE_X99_CODE(DictTypeEnums.VEHIC_TYPE_CODE, "X99", "其他"),

    PASSENGER_STATION_1_TYPE(DictTypeEnums.PASSENGER_STATION_TYPE, "1", "自有专用客运站"),
    PASSENGER_STATION_2_TYPE(DictTypeEnums.PASSENGER_STATION_TYPE, "2", "自有共用客运站"),
    PASSENGER_STATION_3_TYPE(DictTypeEnums.PASSENGER_STATION_TYPE, "3", "共同经营共用客运站"),
    PASSENGER_STATION_4_TYPE(DictTypeEnums.PASSENGER_STATION_TYPE, "4", "普通共用客运站"),
    PASSENGER_STATION_9_TYPE(DictTypeEnums.PASSENGER_STATION_TYPE, "9", "其他"),

    GA_T_AREA_GAS_CODE(DictTypeEnums.GA_T_AREA_CODE, "511600", "广安市"),
    GA_T_AREA_LSX_CODE(DictTypeEnums.GA_T_AREA_CODE, "511623", "邻水县"),
    GA_T_AREA_GAQ_CODE(DictTypeEnums.GA_T_AREA_CODE, "511602", "广安区"),
    GA_T_AREA_YCX_CODE(DictTypeEnums.GA_T_AREA_CODE, "511621", "岳池县"),
    GA_T_AREA_WSX_CODE(DictTypeEnums.GA_T_AREA_CODE, "511622", "武胜县"),
    GA_T_AREA_QFQ_CODE(DictTypeEnums.GA_T_AREA_CODE, "511603", "前锋区"),
    GA_T_AREA_HYS_CODE(DictTypeEnums.GA_T_AREA_CODE, "511681", "华蓥市"),

    NATION_01(DictTypeEnums.NATION, "01", "汉族"),
    NATION_02(DictTypeEnums.NATION, "02", "蒙古族"),
    NATION_03(DictTypeEnums.NATION, "03", "回族"),
    NATION_04(DictTypeEnums.NATION, "04", "藏族"),
    NATION_05(DictTypeEnums.NATION, "05", "维吾尔族"),
    NATION_06(DictTypeEnums.NATION, "06", "苗族"),
    NATION_07(DictTypeEnums.NATION, "07", "彝族"),
    NATION_08(DictTypeEnums.NATION, "08", "壮族"),
    NATION_09(DictTypeEnums.NATION, "09", "布依族"),
    NATION_10(DictTypeEnums.NATION, "10", "朝鲜族"),
    NATION_11(DictTypeEnums.NATION, "11", "满族"),
    NATION_12(DictTypeEnums.NATION, "12", "侗族"),
    NATION_13(DictTypeEnums.NATION, "13", "瑶族"),
    NATION_14(DictTypeEnums.NATION, "14", "白族"),
    NATION_15(DictTypeEnums.NATION, "15", "土家族"),
    NATION_16(DictTypeEnums.NATION, "16", "哈尼族"),
    NATION_17(DictTypeEnums.NATION, "17", "哈萨克族"),
    NATION_18(DictTypeEnums.NATION, "18", "傣族"),
    NATION_19(DictTypeEnums.NATION, "19", "黎族"),
    NATION_20(DictTypeEnums.NATION, "20", "傈僳族"),
    NATION_21(DictTypeEnums.NATION, "21", "佤族"),
    NATION_22(DictTypeEnums.NATION, "22", "畲族"),
    NATION_23(DictTypeEnums.NATION, "23", "高山族"),
    NATION_24(DictTypeEnums.NATION, "24", "拉祜族"),
    NATION_25(DictTypeEnums.NATION, "25", "水族"),
    NATION_26(DictTypeEnums.NATION, "26", "东乡族"),
    NATION_27(DictTypeEnums.NATION, "27", "纳西族"),
    NATION_28(DictTypeEnums.NATION, "28", "景颇族"),
    NATION_29(DictTypeEnums.NATION, "29", "柯尔克孜族"),
    NATION_30(DictTypeEnums.NATION, "30", "土族"),
    NATION_31(DictTypeEnums.NATION, "31", "达斡尔族"),
    NATION_32(DictTypeEnums.NATION, "32", "仫佬族"),
    NATION_33(DictTypeEnums.NATION, "33", "羌族"),
    NATION_34(DictTypeEnums.NATION, "34", "布朗族"),
    NATION_35(DictTypeEnums.NATION, "35", "撒拉族"),
    NATION_36(DictTypeEnums.NATION, "36", "毛南族"),
    NATION_37(DictTypeEnums.NATION, "37", "仡佬族"),
    NATION_38(DictTypeEnums.NATION, "38", "锡伯族"),
    NATION_39(DictTypeEnums.NATION, "39", "阿昌族"),
    NATION_40(DictTypeEnums.NATION, "40", "普米族"),
    NATION_41(DictTypeEnums.NATION, "41", "塔吉克族"),
    NATION_42(DictTypeEnums.NATION, "42", "怒族"),
    NATION_43(DictTypeEnums.NATION, "43", "乌孜别克族"),
    NATION_44(DictTypeEnums.NATION, "44", "俄罗斯族"),
    NATION_45(DictTypeEnums.NATION, "45", "鄂温克族"),
    NATION_46(DictTypeEnums.NATION, "46", "德昂族"),
    NATION_47(DictTypeEnums.NATION, "47", "保安族"),
    NATION_48(DictTypeEnums.NATION, "48", "裕固族"),
    NATION_49(DictTypeEnums.NATION, "49", "京族"),
    NATION_50(DictTypeEnums.NATION, "50", "塔塔尔族"),
    NATION_51(DictTypeEnums.NATION, "51", "独龙族"),
    NATION_52(DictTypeEnums.NATION, "52", "鄂伦春族"),
    NATION_53(DictTypeEnums.NATION, "53", "赫哲族"),
    NATION_54(DictTypeEnums.NATION, "54", "门巴族"),
    NATION_55(DictTypeEnums.NATION, "55", "珞巴族"),
    NATION_56(DictTypeEnums.NATION, "56", "基诺族"),
    NATION_97(DictTypeEnums.NATION, "97", "其他"),
    NATION_98(DictTypeEnums.NATION, "98", "外国血统中国籍人士"),

    SHIP_0100_TYPE(DictTypeEnums.SHIP_TYPE, "0100", "客船类"),
    SHIP_0101_TYPE(DictTypeEnums.SHIP_TYPE, "0101", "普通客船"),
    SHIP_0102_TYPE(DictTypeEnums.SHIP_TYPE, "0102", "客货船"),
    SHIP_0103_TYPE(DictTypeEnums.SHIP_TYPE, "0103", "客渡船"),
    SHIP_0104_TYPE(DictTypeEnums.SHIP_TYPE, "0104", "车客渡船"),
    SHIP_0105_TYPE(DictTypeEnums.SHIP_TYPE, "0105", "旅游客船"),
    SHIP_0106_TYPE(DictTypeEnums.SHIP_TYPE, "0106", "高速客船"),
    SHIP_0107_TYPE(DictTypeEnums.SHIP_TYPE, "0107", "客驳船"),
    SHIP_0108_TYPE(DictTypeEnums.SHIP_TYPE, "0108", "滚装客船"),
    SHIP_0109_TYPE(DictTypeEnums.SHIP_TYPE, "0109", "客箱船"),
    SHIP_0110_TYPE(DictTypeEnums.SHIP_TYPE, "0110", "火车渡船"),
    SHIP_0111_TYPE(DictTypeEnums.SHIP_TYPE, "0111", "地效翼船"),
    SHIP_0200_TYPE(DictTypeEnums.SHIP_TYPE, "0200", "普通货船类"),
    SHIP_0201_TYPE(DictTypeEnums.SHIP_TYPE, "0201", "干货船"),
    SHIP_0202_TYPE(DictTypeEnums.SHIP_TYPE, "0202", "杂货船"),
    SHIP_0203_TYPE(DictTypeEnums.SHIP_TYPE, "0203", "散货船"),
    SHIP_0204_TYPE(DictTypeEnums.SHIP_TYPE, "0204", "散装水泥运输船"),
    SHIP_0205_TYPE(DictTypeEnums.SHIP_TYPE, "0205", "集装箱船"),
    SHIP_0206_TYPE(DictTypeEnums.SHIP_TYPE, "0206", "滚装船"),
    SHIP_0207_TYPE(DictTypeEnums.SHIP_TYPE, "0207", "多用途船"),
    SHIP_0208_TYPE(DictTypeEnums.SHIP_TYPE, "0208", "木材船"),
    SHIP_0209_TYPE(DictTypeEnums.SHIP_TYPE, "0209", "水产品运输船"),
    SHIP_0210_TYPE(DictTypeEnums.SHIP_TYPE, "0210", "重大件运输船"),
    SHIP_0211_TYPE(DictTypeEnums.SHIP_TYPE, "0211", "驳船"),
    SHIP_0212_TYPE(DictTypeEnums.SHIP_TYPE, "0212", "汽车渡船"),
    SHIP_0213_TYPE(DictTypeEnums.SHIP_TYPE, "0213", "挂桨机船"),
    SHIP_0214_TYPE(DictTypeEnums.SHIP_TYPE, "0214", "冷藏船"),
    SHIP_0215_TYPE(DictTypeEnums.SHIP_TYPE, "0215", "火车渡船"),
    SHIP_0216_TYPE(DictTypeEnums.SHIP_TYPE, "0216", "矿散油船"),
    SHIP_0217_TYPE(DictTypeEnums.SHIP_TYPE, "0217", "半潜船"),
    SHIP_0218_TYPE(DictTypeEnums.SHIP_TYPE, "0218", "粮食船"),
    SHIP_0300_TYPE(DictTypeEnums.SHIP_TYPE, "0300", "液货船类"),
    SHIP_0301_TYPE(DictTypeEnums.SHIP_TYPE, "0301", "油船"),
    SHIP_0302_TYPE(DictTypeEnums.SHIP_TYPE, "0302", "散装化学品船"),
    SHIP_0303_TYPE(DictTypeEnums.SHIP_TYPE, "0303", "散装化学品船/油船"),
    SHIP_0304_TYPE(DictTypeEnums.SHIP_TYPE, "0304", "液化气船"),
    SHIP_0305_TYPE(DictTypeEnums.SHIP_TYPE, "0305", "散装沥青船"),
    SHIP_0306_TYPE(DictTypeEnums.SHIP_TYPE, "0306", "油驳"),
    SHIP_0400_TYPE(DictTypeEnums.SHIP_TYPE, "0400", "工程船类"),
    SHIP_0401_TYPE(DictTypeEnums.SHIP_TYPE, "0401", "工程船"),
    SHIP_0402_TYPE(DictTypeEnums.SHIP_TYPE, "0402", "测量船"),
    SHIP_0403_TYPE(DictTypeEnums.SHIP_TYPE, "0403", "采沙船"),
    SHIP_0404_TYPE(DictTypeEnums.SHIP_TYPE, "0404", "挖泥船"),
    SHIP_0405_TYPE(DictTypeEnums.SHIP_TYPE, "0405", "疏浚船"),
    SHIP_0406_TYPE(DictTypeEnums.SHIP_TYPE, "0406", "打捞船"),
    SHIP_0407_TYPE(DictTypeEnums.SHIP_TYPE, "0407", "打桩船"),
    SHIP_0408_TYPE(DictTypeEnums.SHIP_TYPE, "0408", "起重船"),
    SHIP_0409_TYPE(DictTypeEnums.SHIP_TYPE, "0409", "搅拌船"),
    SHIP_0410_TYPE(DictTypeEnums.SHIP_TYPE, "0410", "布缆船"),
    SHIP_0411_TYPE(DictTypeEnums.SHIP_TYPE, "0411", "钻井船"),
    SHIP_0412_TYPE(DictTypeEnums.SHIP_TYPE, "0412", "打桩起重船"),
    SHIP_0413_TYPE(DictTypeEnums.SHIP_TYPE, "0413", "吹泥船"),
    SHIP_0414_TYPE(DictTypeEnums.SHIP_TYPE, "0414", "起重驳"),
    SHIP_0500_TYPE(DictTypeEnums.SHIP_TYPE, "0500", "工作船类"),
    SHIP_0501_TYPE(DictTypeEnums.SHIP_TYPE, "0501", "工作船"),
    SHIP_0502_TYPE(DictTypeEnums.SHIP_TYPE, "0502", "破冰船"),
    SHIP_0503_TYPE(DictTypeEnums.SHIP_TYPE, "0503", "航标船"),
    SHIP_0504_TYPE(DictTypeEnums.SHIP_TYPE, "0504", "油污水处理船"),
    SHIP_0505_TYPE(DictTypeEnums.SHIP_TYPE, "0505", "供给船"),
    SHIP_0506_TYPE(DictTypeEnums.SHIP_TYPE, "0506", "垃圾处理船"),
    SHIP_0600_TYPE(DictTypeEnums.SHIP_TYPE, "0600", "拖船类"),
    SHIP_0601_TYPE(DictTypeEnums.SHIP_TYPE, "0601", "拖船"),
    SHIP_0602_TYPE(DictTypeEnums.SHIP_TYPE, "0602", "推轮"),
    SHIP_0900_TYPE(DictTypeEnums.SHIP_TYPE, "0900", "其他类"),
    SHIP_0901_TYPE(DictTypeEnums.SHIP_TYPE, "0901", "交通艇"),
    SHIP_0902_TYPE(DictTypeEnums.SHIP_TYPE, "0902", "引航船"),
    SHIP_0903_TYPE(DictTypeEnums.SHIP_TYPE, "0903", "救助船"),
    SHIP_0904_TYPE(DictTypeEnums.SHIP_TYPE, "0904", "浮船坞"),
    SHIP_0905_TYPE(DictTypeEnums.SHIP_TYPE, "0905", "公务船"),
    SHIP_0906_TYPE(DictTypeEnums.SHIP_TYPE, "0906", "摩托艇"),
    SHIP_0907_TYPE(DictTypeEnums.SHIP_TYPE, "0907", "帆船"),
    SHIP_0908_TYPE(DictTypeEnums.SHIP_TYPE, "0908", "趸船"),
    SHIP_0909_TYPE(DictTypeEnums.SHIP_TYPE, "0909", "游艇"),
    SHIP_0910_TYPE(DictTypeEnums.SHIP_TYPE, "0910", "特种用途船"),
    SHIP_0911_TYPE(DictTypeEnums.SHIP_TYPE, "0911", "水上平台"),
    SHIP_0912_TYPE(DictTypeEnums.SHIP_TYPE, "0912", "水下观光船"),
    SHIP_0913_TYPE(DictTypeEnums.SHIP_TYPE, "0913", "科学调查船"),
    SHIP_0914_TYPE(DictTypeEnums.SHIP_TYPE, "0914", "勘探船"),

    SERVICE_AREA_1_TYPE(DictTypeEnums.SERVICE_AREA_TYPE, "1", "高速公路服务区"),
    SERVICE_AREA_2_TYPE(DictTypeEnums.SERVICE_AREA_TYPE, "2", "一般公路服务区"),
    SERVICE_AREA_3_TYPE(DictTypeEnums.SERVICE_AREA_TYPE, "3", "入口"),
    SERVICE_AREA_4_TYPE(DictTypeEnums.SERVICE_AREA_TYPE, "4", "出口"),


    SLOPE_STRUCTURE_1(DictTypeEnums.SLOPE_STRUCTURE, "1", "岩质"),

    SLOPE_STRUCTURE_2(DictTypeEnums.SLOPE_STRUCTURE, "2", "土质"),

    SLOPE_STRUCTURE_3(DictTypeEnums.SLOPE_STRUCTURE, "3", "土石混合"),

    TRAFFIC_ON_OR_OFF_1(DictTypeEnums.TRAFFIC_ON_OR_OFF, "1", "中断"),

    TRAFFIC_ON_OR_OFF_2(DictTypeEnums.TRAFFIC_ON_OR_OFF, "2", "半幅通信"),

    TRAFFIC_ON_OR_OFF_3(DictTypeEnums.TRAFFIC_ON_OR_OFF, "3", "双向同行"),

    HARM_TARGET_1(DictTypeEnums.HARM_TARGET, "1", "桥梁"),

    HARM_TARGET_2(DictTypeEnums.HARM_TARGET, "2", "隧道洞口"),

    HARM_TARGET_3(DictTypeEnums.HARM_TARGET, "3", "路基"),

    HARM_TARGET_4(DictTypeEnums.HARM_TARGET, "4", "涵洞"),

    HARM_TARGET_5(DictTypeEnums.HARM_TARGET, "5", "服务区"),

    HARM_TARGET_6(DictTypeEnums.HARM_TARGET, "6", "停车区"),

    HARM_TARGET_7(DictTypeEnums.HARM_TARGET, "7", "收费站"),

    HARM_TARGET_8(DictTypeEnums.HARM_TARGET, "8", "其他"),

    HARM_LEVEL_1(DictTypeEnums.HARM_LEVEL, "1", "严重"),

    HARM_LEVEL_2(DictTypeEnums.HARM_LEVEL, "2", "较严重"),

    HARM_LEVEL_3(DictTypeEnums.HARM_LEVEL, "3", "一般"),

    HARM_LEVEL_4(DictTypeEnums.HARM_LEVEL, "4", "轻微"),


    DISASTER_DISPOSAL_1(DictTypeEnums.DISASTER_DISPOSAL, "1", "已处置修复"),

    DISASTER_DISPOSAL_2(DictTypeEnums.DISASTER_DISPOSAL, "2", "未处置修复"),

    DISASTER_DISPOSAL_3(DictTypeEnums.DISASTER_DISPOSAL, "3", "正在处置修复"),

    FLOW_TYPE_1(DictTypeEnums.FLOW_TYPE, "1", "山坡"),

    FLOW_TYPE_2(DictTypeEnums.FLOW_TYPE, "2", "沟谷"),


    COLLAPSE_REASON_1(DictTypeEnums.COLLAPSE_REASON, "1", "采空区"),

    COLLAPSE_REASON_2(DictTypeEnums.COLLAPSE_REASON, "2", "黄土陷穴"),

    COLLAPSE_REASON_3(DictTypeEnums.COLLAPSE_REASON, "3", "岩溶"),

    COLLAPSE_REASON_4(DictTypeEnums.COLLAPSE_REASON, "4", "地下水"),

    COLLAPSE_REASON_5(DictTypeEnums.COLLAPSE_REASON, "5", "其他"),

    WATER_FEATURE_1(DictTypeEnums.WATER_FEATURE, "1", "凹岸"),

    WATER_FEATURE_2(DictTypeEnums.WATER_FEATURE, "2", "凸岸"),


    WATER_PART_1(DictTypeEnums.WATER_PART, "1", "路基 "),

    WATER_PART_2(DictTypeEnums.WATER_PART, "2", "桥梁"),

    WATER_PART_3(DictTypeEnums.WATER_PART, "3", "寒冬 "),

    WATER_PART_4(DictTypeEnums.WATER_PART, "4", "路面"),

    WATER_PART_5(DictTypeEnums.WATER_PART, "5", "隧道"),

    WATER_PART_6(DictTypeEnums.WATER_PART, "6", "其他"),


    WATER_TYPE_1(DictTypeEnums.WATER_TYPE, "1", "采空区"),

    WATER_TYPE_2(DictTypeEnums.WATER_TYPE, "2", "黄土陷穴"),

    WATER_TYPE_3(DictTypeEnums.WATER_TYPE, "3", "岩溶"),

    WATER_TYPE_4(DictTypeEnums.WATER_TYPE, "4", "地下水"),

    WATER_TYPE_5(DictTypeEnums.WATER_TYPE, "5", "其他"),


    BREAK_POSITION_1(DictTypeEnums.BREAK_POSITION, "1", "坡顶"),

    BREAK_POSITION_2(DictTypeEnums.BREAK_POSITION, "2", "坡中"),

    BREAK_POSITION_3(DictTypeEnums.BREAK_POSITION, "3", "坡脚"),

    BREAK_POSITION_4(DictTypeEnums.BREAK_POSITION, "4", "路基"),

    STRUCTURE_AWAY_WATER_1(DictTypeEnums.STRUCTURE_AWAY_WATER, "1", "边沟"),

    STRUCTURE_AWAY_WATER_2(DictTypeEnums.STRUCTURE_AWAY_WATER, "2", "截水沟"),

    STRUCTURE_AWAY_WATER_3(DictTypeEnums.STRUCTURE_AWAY_WATER, "3", "排水沟"),

    STRUCTURE_AWAY_WATER_4(DictTypeEnums.STRUCTURE_AWAY_WATER, "4", "跌水与集水槽"),

    STRUCTURE_AWAY_WATER_5(DictTypeEnums.STRUCTURE_AWAY_WATER, "5", "无"),

    STRUCTURE_SLOP_PROTECT_1(DictTypeEnums.STRUCTURE_SLOP_PROTECT, "1", "植物防护"),

    STRUCTURE_SLOP_PROTECT_2(DictTypeEnums.STRUCTURE_SLOP_PROTECT, "2", "骨架防护"),

    STRUCTURE_SLOP_PROTECT_3(DictTypeEnums.STRUCTURE_SLOP_PROTECT, "3", "挂网喷护"),

    STRUCTURE_SLOP_PROTECT_4(DictTypeEnums.STRUCTURE_SLOP_PROTECT, "4", "片石护坡"),

    STRUCTURE_SLOP_PROTECT_5(DictTypeEnums.STRUCTURE_SLOP_PROTECT, "5", "护面墙"),

    STRUCTURE_SUPPORT_1(DictTypeEnums.STRUCTURE_SUPPORT, "1", "挡墙"),

    STRUCTURE_SUPPORT_2(DictTypeEnums.STRUCTURE_SUPPORT, "2", "抗滑桩"),

    STRUCTURE_SUPPORT_3(DictTypeEnums.STRUCTURE_SUPPORT, "3", "锚（杆）索"),

    STRUCTURE_SUPPORT_4(DictTypeEnums.STRUCTURE_SUPPORT, "4", "框架"),

    STRUCTURE_SUPPORT_5(DictTypeEnums.STRUCTURE_SUPPORT, "5", "无"),

    STRUCTURE_ALONG_RIVER_1(DictTypeEnums.STRUCTURE_ALONG_RIVER, "1", "植物防护"),

    STRUCTURE_ALONG_RIVER_2(DictTypeEnums.STRUCTURE_ALONG_RIVER, "2", "砌石或砼护坡"),

    STRUCTURE_ALONG_RIVER_3(DictTypeEnums.STRUCTURE_ALONG_RIVER, "3", "石笼防护"),

    STRUCTURE_ALONG_RIVER_4(DictTypeEnums.STRUCTURE_ALONG_RIVER, "4", "浸水挡墙"),

    STRUCTURE_ALONG_RIVER_5(DictTypeEnums.STRUCTURE_ALONG_RIVER, "5", "护坦"),

    STRUCTURE_ALONG_RIVER_6(DictTypeEnums.STRUCTURE_ALONG_RIVER, "6", "抛石"),

    STRUCTURE_ALONG_RIVER_7(DictTypeEnums.STRUCTURE_ALONG_RIVER, "7", "排桩"),

    STRUCTURE_ALONG_RIVER_8(DictTypeEnums.STRUCTURE_ALONG_RIVER, "8", "丁坝、顺坝"),


    NATURAL_DANGER_LEVEL_ONE(DictTypeEnums.NATURAL_DANGER_LEVEL, "1", "一级"),

    NATURAL_DANGER_LEVEL_TWO(DictTypeEnums.NATURAL_DANGER_LEVEL, "2", "二级"),

    NATURAL_DANGER_LEVEL_THREE(DictTypeEnums.NATURAL_DANGER_LEVEL, "3", "三级"),

    NATURAL_DANGER_LEVEL_FOUR(DictTypeEnums.NATURAL_DANGER_LEVEL, "4", "四级"),


    STRUCTURE_BRIDGE_1(DictTypeEnums.STRUCTURE_BRIDGE, "1", "支座"),

    STRUCTURE_BRIDGE_2(DictTypeEnums.STRUCTURE_BRIDGE, "2", "上部承重结构"),

    STRUCTURE_BRIDGE_3(DictTypeEnums.STRUCTURE_BRIDGE, "3", "桥墩、桥台"),

    STRUCTURE_BRIDGE_4(DictTypeEnums.STRUCTURE_BRIDGE, "4", "基础"),

    STRUCTURE_BRIDGE_5(DictTypeEnums.STRUCTURE_BRIDGE, "5", "其他"),

    STRUCTURE_HOLE_1(DictTypeEnums.STRUCTURE_HOLE, "1", "边坡"),

    STRUCTURE_HOLE_2(DictTypeEnums.STRUCTURE_HOLE, "2", "仰坡"),

    STRUCTURE_HOLE_3(DictTypeEnums.STRUCTURE_HOLE, "3", "洞身"),

    STRUCTURE_HOLE_4(DictTypeEnums.STRUCTURE_HOLE, "4", "仰拱"),

    STRUCTURE_HOLE_5(DictTypeEnums.STRUCTURE_HOLE, "5", "顶拱"),

    STRUCTURE_HOLE_6(DictTypeEnums.STRUCTURE_HOLE, "6", "边墙"),

    STRUCTURE_HOLE_7(DictTypeEnums.STRUCTURE_HOLE, "7", "其他"),

    DAMAGE_LEVEL_1(DictTypeEnums.DAMAGE_LEVEL, "1", "无"),

    DAMAGE_LEVEL_2(DictTypeEnums.DAMAGE_LEVEL, "2", "轻微"),

    DAMAGE_LEVEL_3(DictTypeEnums.DAMAGE_LEVEL, "3", "中等"),

    DAMAGE_LEVEL_4(DictTypeEnums.DAMAGE_LEVEL, "4", "严重"),

    BRIDGE_PATROL_DAILY(DictTypeEnums.BRIDGE_PATROL, "1", "日常巡查"),

    BRIDGE_PATROL_OFTEN(DictTypeEnums.BRIDGE_PATROL, "2", "经常巡查"),

    BRIDGE_PATROL_OTHER(DictTypeEnums.BRIDGE_PATROL, "3", "日常巡查"),


    // 桥路连接处是否异常
    BRIDGE_PROBLEM_JOINT_STATUS(DictTypeEnums.BRIDGE_PROBLEM, "1", "桥路连接处是否异常"),
    // 桥面铺装、伸缩缝是否有明显破损;伸缩缝位置的桥面系是否存在异常
    BRIDGE_PROBLEM_SURFACE_SEAM(DictTypeEnums.BRIDGE_PROBLEM, "2", "桥面铺装、伸缩缝是否有明显破损;伸缩缝位置的桥面系是否存在异常"),
    // 栏杆或护栏等有无明显缺损
    BRIDGE_PROBLEM_RAILING(DictTypeEnums.BRIDGE_PROBLEM, "3", "栏杆或护栏等有无明显缺损"),
    // 标志标牌是否完好
    BRIDGE_PROBLEM_SIGN(DictTypeEnums.BRIDGE_PROBLEM, "4", "标志标牌是否完好"),
    // 桥梁线形是否存在明显异常
    BRIDGE_PROBLEM_LINE_SHAPE(DictTypeEnums.BRIDGE_PROBLEM, "5", "桥梁线形是否存在明显异常"),
    // 桥梁是否存在异常的振动、摆动和声响
    BRIDGE_PROBLEM_VIBRATION(DictTypeEnums.BRIDGE_PROBLEM, "6", "桥梁是否存在异常的振动、摆动和声响"),
    // 桥梁安全保护区是否存在侵害桥梁安全的情况
    BRIDGE_PROBLEM_PROTECTION_AREA(DictTypeEnums.BRIDGE_PROBLEM, "7", "桥梁安全保护区是否存在侵害桥梁安全的情况"),
    // 其他情况
    BRIDGE_PROBLEM_OTHER(DictTypeEnums.BRIDGE_PROBLEM, "8", "其他情况"),


    //--------------------------------------------  交通相关  ----------------------------------------------------

    PASSENGER_SHIP(DictTypeEnums.EQUIPMENT_TYPE, "1", "客船"),

    GOODS_SHIP(DictTypeEnums.EQUIPMENT_TYPE, "2", "货船"),

    ENGINEERING_SHIP(DictTypeEnums.EQUIPMENT_TYPE, "3", "工程船"),

    SPECIAL_OPERATIONS_SHIP(DictTypeEnums.EQUIPMENT_TYPE, "4", "特种作业船"),

    BUSES(DictTypeEnums.EQUIPMENT_TYPE, "5", "客车"),

    TRUCKS(DictTypeEnums.EQUIPMENT_TYPE, "6", "货车"),

    ENGINEERING_VEHICLE(DictTypeEnums.EQUIPMENT_TYPE, "7", "工程车"),

    DRONES(DictTypeEnums.EQUIPMENT_TYPE, "8", "无人机"),

    LIFE_SAVING_EQUIPMENT(DictTypeEnums.EQUIPMENT_TYPE, "9", "救生装备"),

    INTER_PHONE(DictTypeEnums.EQUIPMENT_TYPE, "10", "对讲机"),

    EQUIPMENT_TYPE_OTHER(DictTypeEnums.EQUIPMENT_TYPE, "11", "其他"),


    HOSPITAL(DictTypeEnums.MEDICAL_RESOURCE, "1", "医院"),

    EMERGENCY_CENTER(DictTypeEnums.MEDICAL_RESOURCE, "2", "急救中心"),

    CDC(DictTypeEnums.MEDICAL_RESOURCE, "3", "疾控中心"),

    BLOOD_COLLECTION(DictTypeEnums.MEDICAL_RESOURCE, "4", "采血站"),

    //--------------------------------------------  场站相关  ----------------------------------------------------

    HK_EVENT(DictTypeEnums.HK_REQUEST_TYPE, "1", "按事件类型订阅事件"),

    HK_CANCEL_EVENT(DictTypeEnums.HK_REQUEST_TYPE, "2", "按事件类型取消订阅事件"),

    HK_EVENT_LIST(DictTypeEnums.HK_REQUEST_TYPE, "3", "查询事件订阅详情"),

    HK_ROOT(DictTypeEnums.HK_REQUEST_TYPE, "4", "获取根区域信息接口_查询根区域"),

    HK_CHILD(DictTypeEnums.HK_REQUEST_TYPE, "5", "查询区域列表v2_查询区域细节"),

    HK_DEVICE(DictTypeEnums.HK_REQUEST_TYPE, "6", "查询编码设备列表"),

    WORK_SHEET_CREATE(DictTypeEnums.WORK_SHEET, "0", "创建"),

    WORK_SHEET_POINT(DictTypeEnums.WORK_SHEET, "1", "已指派"),

    WORK_SHEET_DEAL(DictTypeEnums.WORK_SHEET, "2", "已处理"),

    WORK_SHEET_AUDITED(DictTypeEnums.WORK_SHEET, "3", "已审核"),

    WORK_SHEET_REJECTED(DictTypeEnums.WORK_SHEET, "4", "已驳回"),

    WORK_SHEET_REJECTED_DEAL(DictTypeEnums.WORK_SHEET, "5", "驳回已处理"),

    //--------------------------------------------  低代码相关 ----------------------------------------------------
    VARCHAR(DictTypeEnums.TABLE_DATA_TYPE, "varchar", "字符类型"),
    TEXT(DictTypeEnums.TABLE_DATA_TYPE, "text", "文本类型"),
    BINARY(DictTypeEnums.TABLE_DATA_TYPE, "binary", "二进制类型"),
    NUMBER(DictTypeEnums.TABLE_DATA_TYPE, "number", "数字类型"),
    DATE(DictTypeEnums.TABLE_DATA_TYPE, "date", "日期"),

    COLUMN_STAY(DictTypeEnums.COLUMN_STATUS, "1", "不变更"),
    COLUMN_ADD(DictTypeEnums.COLUMN_STATUS, "2", "新增字段"),
    COLUMN_UPDATE(DictTypeEnums.COLUMN_STATUS, "3", "变更字段"),
    COLUMN_DELETE(DictTypeEnums.COLUMN_STATUS, "4", "删除字段"),

    DATA_BASE_MYSQL(DictTypeEnums.DATA_BASE, "mysql", "mysql"),

    PRI(DictTypeEnums.COLUMN_TYPE, "pri", "主键"),

    CONVERT_NOT(DictTypeEnums.COLUMN_CONVERT, "1", "不转换"),
    CONVERT_DATE(DictTypeEnums.COLUMN_CONVERT, "2", "时间格式"),
    CONVERT_STATIC_CODE(DictTypeEnums.COLUMN_CONVERT, "3", "静态选项Code转名称"),
    CONVERT_STATIC_DICT(DictTypeEnums.COLUMN_CONVERT, "4", "数据字典Code转名称"),
    CONVERT_SQL(DictTypeEnums.COLUMN_CONVERT, "5", "通过Sql配置转换"),
    CONVERT_SERVER(DictTypeEnums.COLUMN_CONVERT, "6", "通过服务配置转换"),
    CONVERT_USER_ID(DictTypeEnums.COLUMN_CONVERT, "7", "用户ID转名称"),
    CONVERT_USER_CODE(DictTypeEnums.COLUMN_CONVERT, "8", "用户CODE转名称"),
    CONVERT_DEPART_ID(DictTypeEnums.COLUMN_CONVERT, "9", "部门ID转名称"),
    CONVERT_DEPART_CODE(DictTypeEnums.COLUMN_CONVERT, "10", "部门CODE转名称"),
    CONVERT_PAGE_MODEL(DictTypeEnums.COLUMN_CONVERT, "11", "通过页面模型进行数据转换"),
    CONVERT_PIC(DictTypeEnums.COLUMN_CONVERT, "12", "以图片形式展示"),
    CONVERT_FILES(DictTypeEnums.COLUMN_CONVERT, "13", "以附件形式展示"),
    CONVERT_LINKS(DictTypeEnums.COLUMN_CONVERT, "14", "超链接"),
    CONVERT_SWITCH(DictTypeEnums.COLUMN_CONVERT, "15", "开关"),
    CONVERT_HTML(DictTypeEnums.COLUMN_CONVERT, "16", "页面"),

    COLUMN_SHOW_CONTROL_HIDE(DictTypeEnums.COLUMN_SHOW_CONTROL, "1", "隐藏"),
    COLUMN_SHOW_CONTROL_SINGLE_LINE(DictTypeEnums.COLUMN_SHOW_CONTROL, "2", "单行文本"),
    COLUMN_SHOW_CONTROL_MULTILINE(DictTypeEnums.COLUMN_SHOW_CONTROL, "3", "多行文本"),
    COLUMN_SHOW_CONTROL_NUMBER(DictTypeEnums.COLUMN_SHOW_CONTROL, "4", "数字"),
    COLUMN_SHOW_CONTROL_PULL(DictTypeEnums.COLUMN_SHOW_CONTROL, "5", "下拉框"),
    COLUMN_SHOW_CONTROL_RADIO(DictTypeEnums.COLUMN_SHOW_CONTROL, "6", "复选框"),
    COLUMN_SHOW_CONTROL_CHECKBOX(DictTypeEnums.COLUMN_SHOW_CONTROL, "7", "单选框"),
    COLUMN_SHOW_CONTROL_SWITCH(DictTypeEnums.COLUMN_SHOW_CONTROL, "8", "开关"),
    COLUMN_SHOW_CONTROL_DATETIME(DictTypeEnums.COLUMN_SHOW_CONTROL, "9", "日期"),
    COLUMN_SHOW_CONTROL_TIME(DictTypeEnums.COLUMN_SHOW_CONTROL, "10", "时间"),
    COLUMN_SHOW_CONTROL_SCORE(DictTypeEnums.COLUMN_SHOW_CONTROL, "11", "评分"),
    COLUMN_SHOW_CONTROL_SLIDE(DictTypeEnums.COLUMN_SHOW_CONTROL, "12", "滑动条"),
    COLUMN_SHOW_CONTROL_FLOW_NO(DictTypeEnums.COLUMN_SHOW_CONTROL, "13", "流水号"),
    COLUMN_SHOW_CONTROL_RICH_TEXT(DictTypeEnums.COLUMN_SHOW_CONTROL, "14", "富文本"),
    COLUMN_SHOW_CONTROL_ICON(DictTypeEnums.COLUMN_SHOW_CONTROL, "15", "图标选择"),
    COLUMN_SHOW_CONTROL_FILE(DictTypeEnums.COLUMN_SHOW_CONTROL, "16", "附件上传"),
    COLUMN_SHOW_CONTROL_PHOTO(DictTypeEnums.COLUMN_SHOW_CONTROL, "17", "图片上传"),
    COLUMN_SHOW_CONTROL_USER(DictTypeEnums.COLUMN_SHOW_CONTROL, "18", "用户选择"),
    COLUMN_SHOW_CONTROL_DEPART(DictTypeEnums.COLUMN_SHOW_CONTROL, "19", "部门选择"),
    COLUMN_SHOW_CONTROL_POP(DictTypeEnums.COLUMN_SHOW_CONTROL, "20", "弹框选择"),
    COLUMN_SHOW_CONTROL_SIGNAL(DictTypeEnums.COLUMN_SHOW_CONTROL, "21", "签名"),
    COLUMN_SHOW_CONTROL_TREE(DictTypeEnums.COLUMN_SHOW_CONTROL, "22", "树选择"),
    COLUMN_SHOW_CONTROL_CASCADE(DictTypeEnums.COLUMN_SHOW_CONTROL, "23", "级联"),

    COLUMN_VALIDATE_EMAIL(DictTypeEnums.COLUMN_VALIDATE, "1", "邮箱地址"),
    COLUMN_VALIDATE_PHONE(DictTypeEnums.COLUMN_VALIDATE, "2", "手机号码"),
    COLUMN_VALIDATE_NUMBER(DictTypeEnums.COLUMN_VALIDATE, "3", "数字"),
    COLUMN_VALIDATE_LETTER_LINE(DictTypeEnums.COLUMN_VALIDATE, "4", "字母或下划线"),
    COLUMN_VALIDATE_LETTER_NUMBER_LINE(DictTypeEnums.COLUMN_VALIDATE, "5", "首字字母,最长18,仅包含字母、数字、下划线"),
    COLUMN_VALIDATE_NETWORK(DictTypeEnums.COLUMN_VALIDATE, "6", "网址"),
    COLUMN_VALIDATE_CHINESE(DictTypeEnums.COLUMN_VALIDATE, "7", "汉字"),
    COLUMN_VALIDATE_QQ(DictTypeEnums.COLUMN_VALIDATE, "8", "QQ号"),
    COLUMN_VALIDATE_START_WITH_LETTER(DictTypeEnums.COLUMN_VALIDATE, "9", "以字母开头"),
    COLUMN_VALIDATE_INTEGER(DictTypeEnums.COLUMN_VALIDATE, "10", "整数"),
    COLUMN_VALIDATE_POSITIVE_INTEGER(DictTypeEnums.COLUMN_VALIDATE, "11", "正整数"),
    COLUMN_VALIDATE_DATE(DictTypeEnums.COLUMN_VALIDATE, "12", "日期"),
    COLUMN_VALIDATE_TIME(DictTypeEnums.COLUMN_VALIDATE, "13", "时间"),
    COLUMN_VALIDATE_EMAIL_CODE(DictTypeEnums.COLUMN_VALIDATE, "14", "邮政编码"),
    COLUMN_VALIDATE_ID_CARD(DictTypeEnums.COLUMN_VALIDATE, "15", "身份证"),
    COLUMN_VALIDATE_FIXED_PHONE(DictTypeEnums.COLUMN_VALIDATE, "16", "固定电话"),
    PLATE_COLOR_BLUE(DictTypeEnums.PLATE_COLOR, "1", "蓝色"),
    PLATE_COLOR_YELLOW(DictTypeEnums.PLATE_COLOR, "2", "黄色"),
    PLATE_COLOR_BLACK(DictTypeEnums.PLATE_COLOR, "3", "黑色"),
    PLATE_COLOR_WHITE(DictTypeEnums.PLATE_COLOR, "4", "白色"),
    PLATE_COLOR_GREEN(DictTypeEnums.PLATE_COLOR, "5", "绿色"),
    PLATE_COLOR_OTHER(DictTypeEnums.PLATE_COLOR, "9", "其他"),
    PLATE_COLOR_AGRICULTURAL_YELLOW(DictTypeEnums.PLATE_COLOR, "91", "农黄色"),
    PLATE_COLOR_AGRO_GREEN(DictTypeEnums.PLATE_COLOR, "92", "农绿色"),
    PLATE_COLOR_YELLOW_GREEN(DictTypeEnums.PLATE_COLOR, "93", "黄绿色"),
    PLATE_COLOR_GRADIENT_GREEN(DictTypeEnums.PLATE_COLOR, "94", "渐变绿"),

    RA(DictTypeEnums.THREE_TYPE_NINI_CERT_TYPE, "RA", "经营许可证"),

    RB(DictTypeEnums.THREE_TYPE_NINI_CERT_TYPE, "RB", "道路运输证"),

    RE(DictTypeEnums.THREE_TYPE_NINI_CERT_TYPE, "RE", "从业资格证"),

    SELF_CODE(DictTypeEnums.DZZZ_CODE_SOURCE, "0", "自定义"),

    MINISTERIA_LEVEL_CODE(DictTypeEnums.DZZZ_CODE_SOURCE, "1", "部道路运输电子证照"),

    PROVINCIAL_LEVEL_CODE(DictTypeEnums.DZZZ_CODE_SOURCE, "2", "省道路运输电子证照"),

    DZZZ_STAGE_ONE(DictTypeEnums.DZZZ_DATA_STAGE, "1", "保存入库"),

    DZZZ_STAGE_TWO(DictTypeEnums.DZZZ_DATA_STAGE, "2", "数据验证"),

    DZZZ_STAGE_THREE(DictTypeEnums.DZZZ_DATA_STAGE, "3", "申请二维码"),

    DZZZ_STAGE_FOUR(DictTypeEnums.DZZZ_DATA_STAGE, "4", "上传证照标识、照面数据、二维码到省电子证照系统"),

    DZZZ_STAGE_FIVE(DictTypeEnums.DZZZ_DATA_STAGE, "5", "向省电子证照系统获取证照"),

    DZZZ_STAGE_SIX(DictTypeEnums.DZZZ_DATA_STAGE, "6", "上传证照到部道路运输电子证照系统"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_001(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "001", "统一社会信用代码"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_099(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "099", "其它法人与其它组织有效证件代码"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_O111(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "111", "公民身份证号码"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_114(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "114", "中国人民解放军军官证编号"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_115(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "115", "中国人民武装警察部队警官证编号"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_118(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "118", "中国人民解放军士兵证编号"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_119(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "119", "中国人民武警警察部队士兵证编号"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_120(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "120", "中国人民解放军文职人员证编号"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_122(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "122", "中国人民武警警察部队文职人员证编号"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_411(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "411", "外交护照护照号"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_412(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "412", "公务护照护照号"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_413(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "413", "公务普通护照护照号"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_414(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "414", "普通护照护照号"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_511(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "511", "台湾居民来往大陆通行证号码"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_516(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "516", "港澳居民来往内地通行证号码"),

    DZZZ_CERTIFICATE_HOLDER_TYPE_999(DictTypeEnums.DZZZ_CERTIFICATE_HOLDER_TYPE, "999", "其它自然人有效证件号码"),

    RA_DZZZ_SLJZ_0(DictTypeEnums.DZZZ_SLJT_TYPE, "11100000000019713D008", "道路运输经营许可证"),

    RA_DZZZ_SLJZ_1(DictTypeEnums.DZZZ_SLJT_TYPE, "11100000000019713D016", "道路危险货物运输许可证"),

    RA_DZZZ_SLJZ_2(DictTypeEnums.DZZZ_SLJT_TYPE, "11100000000019713D074", "网络预约出租汽车经营许可证"),

    RA_DZZZ_SLJZ_3(DictTypeEnums.DZZZ_SLJT_TYPE, "11100000000019713D079", "放射性物品道路运输许可证"),

    RB_DZZZ_SLJZ_0(DictTypeEnums.DZZZ_SLJT_TYPE, "11100000000019713D013", "道路运输证"),

    RB_DZZZ_SLJZ_1(DictTypeEnums.DZZZ_SLJT_TYPE, "11100000000019713D073", "网络预约出租汽车运输证"),

    RE_DZZZ_SLJZ_0(DictTypeEnums.DZZZ_SLJT_TYPE, "11100000000019713D071", "道路运输从业人员从业资格证"),

    RE_DZZZ_SLJZ_1(DictTypeEnums.DZZZ_SLJT_TYPE, "11100000000019713D075", "巡游出租汽车驾驶员证"),

    RE_DZZZ_SLJZ_2(DictTypeEnums.DZZZ_SLJT_TYPE, "11100000000019713D072", "网络预约出租汽车驾驶员证"),

    RA_OPERATING_STATUS_1(DictTypeEnums.RA_OPERATING_STATUS, "1", "营业"),

    RA_OPERATING_STATUS_2(DictTypeEnums.RA_OPERATING_STATUS, "2", "停业"),

    RA_OPERATING_STATUS_3(DictTypeEnums.RA_OPERATING_STATUS, "3", "整改"),

    RA_OPERATING_STATUS_4(DictTypeEnums.RA_OPERATING_STATUS, "4", "停业整顿"),

    RA_OPERATING_STATUS_5(DictTypeEnums.RA_OPERATING_STATUS, "5", "歇业"),

    RA_OPERATING_STATUS_6(DictTypeEnums.RA_OPERATING_STATUS, "6", "注销"),

    RA_OPERATING_STATUS_9(DictTypeEnums.RA_OPERATING_STATUS, "7", "其它"),

    VEHICLES_AUDIT_YEAR_STATUS_0(DictTypeEnums.VEHICLES_AUDIT_YEAR_STATUS, "0", "未年审"),

    VEHICLES_AUDIT_YEAR_STATUS_1(DictTypeEnums.VEHICLES_AUDIT_YEAR_STATUS, "1", "年审合格"),

    VEHICLES_AUDIT_YEAR_STATUS_2(DictTypeEnums.VEHICLES_AUDIT_YEAR_STATUS, "2", "年审不合格"),

    VEHICLES_TECHNOLOGY_LEVEL_0(DictTypeEnums.VEHICLES_TECHNOLOGY_LEVEL, "0", "未评定"),

    VEHICLES_TECHNOLOGY_LEVEL_1(DictTypeEnums.VEHICLES_TECHNOLOGY_LEVEL, "1", "一级"),

    VEHICLES_TECHNOLOGY_LEVEL_2(DictTypeEnums.VEHICLES_TECHNOLOGY_LEVEL, "2", "二级"),

    VEHICLES_TECHNOLOGY_LEVEL_9(DictTypeEnums.VEHICLES_TECHNOLOGY_LEVEL, "9", "不合格"),

    PROVINCE_RA_DZZZ_SLJZ_0(DictTypeEnums.PROVINCE_DZZZ_SLJT_TYPE, "866CDFC091CD4529A87771E558BA7E5A", "中华人民共和国道路运输经营许可证"),

    PROVINCE_RA_DZZZ_SLJZ_1(DictTypeEnums.PROVINCE_DZZZ_SLJT_TYPE, "D2C34C63E3564340980F0C87535CBC85", "中华人民共和国道路危险货物运输许可证"),

    PROVINCE_RA_DZZZ_SLJZ_2(DictTypeEnums.PROVINCE_DZZZ_SLJT_TYPE, "57CC896E44D34A0A9F112FFBBD14F65A", "网络预约出租汽车经营许可证"),

    PROVINCE_RA_DZZZ_SLJZ_3(DictTypeEnums.PROVINCE_DZZZ_SLJT_TYPE, "5BCD24DC10A141BF8DFE8CF52899CD25", "放射性物品道路运输许可证"),

    PROVINCE_RB_DZZZ_SLJZ_0(DictTypeEnums.PROVINCE_DZZZ_SLJT_TYPE, "71124074089E440BBE8C131A26E7088F", "中华人民共和国道路运输证"),

    PROVINCE_RB_DZZZ_SLJZ_1(DictTypeEnums.PROVINCE_DZZZ_SLJT_TYPE, "C0C78F6CC9464D92B80018770FB87842", "网络预约出租汽车运输证"),

    PROVINCE_RE_DZZZ_SLJZ_0(DictTypeEnums.PROVINCE_DZZZ_SLJT_TYPE, "F9E02C91FBA646C7A6C537EDC7669E4E", "道路运输从业人员从业资格证"),

    PROVINCE_RE_DZZZ_SLJZ_1(DictTypeEnums.PROVINCE_DZZZ_SLJT_TYPE, "DF080274C01F4781A1E7EA29D3249509", "巡游出租汽车驾驶员证"),

    PROVINCE_RE_DZZZ_SLJZ_2(DictTypeEnums.PROVINCE_DZZZ_SLJT_TYPE, "0808C9021A38499197D5ECC917886084", "网络预约出租汽车驾驶员证"),

    FSLJZ_DZZZ_STAGE_ONE(DictTypeEnums.FSLJZ_DZZZ_DATA_STAGE, "1", "保存入库"),

    FSLJZ_DZZZ_STAGE_TWO(DictTypeEnums.FSLJZ_DZZZ_DATA_STAGE, "2", "验证"),

    FSLJZ_DZZZ_STAGE_THREE(DictTypeEnums.FSLJZ_DZZZ_DATA_STAGE, "3", "上传照面数据到省电子证照系统"),

    FSLJZ_DZZZ_STAGE_FOUR(DictTypeEnums.FSLJZ_DZZZ_DATA_STAGE, "4", "向省电子证照系统获取证照"),
    OPERATE_STATUS_0(DictTypeEnums.OPERATE_STATUS, "0", "成功"),
    OPERATE_STATUS_1(DictTypeEnums.OPERATE_STATUS, "1", "失败"),
    HDDJ_1(DictTypeEnums.HDDJ, "1", "一级航道"),
    HDDJ_2(DictTypeEnums.HDDJ, "2", "二级航道"),
    HDDJ_3(DictTypeEnums.HDDJ, "3", "三级航道"),
    HDDJ_4(DictTypeEnums.HDDJ, "4", "四级航道"),
    HDDJ_5(DictTypeEnums.HDDJ, "5", "五级航道"),
    HDDJ_6(DictTypeEnums.HDDJ, "6", "六级航道"),
    HDDJ_7(DictTypeEnums.HDDJ, "7", "七级航道"),
    HDDJ_8(DictTypeEnums.HDDJ, "8", "等外航道"),
    HDDJ_9(DictTypeEnums.HDDJ, "9", "不通航"),
    fwqlx_1(DictTypeEnums.fwqlx, "1", "高速公路服务区"),
    fwqlx_2(DictTypeEnums.fwqlx, "2", "一般公路服务区"),
    fwqlx_3(DictTypeEnums.fwqlx, "3", "入口"),
    fwqlx_4(DictTypeEnums.fwqlx, "4", "出口"),
    DATA_SOURCE_0(DictTypeEnums.DATA_SOURCE, "0", "厅信息中心"),
    DATA_SOURCE_1(DictTypeEnums.DATA_SOURCE, "1", "企业内部"),
    DATA_SOURCE_2(DictTypeEnums.DATA_SOURCE, "2", "手动录入"),
    STANDARD_TRUE_FALSE_1(DictTypeEnums.STANDARD_TRUE_FALSE, "1", "是"),
    STANDARD_TRUE_FALSE_2(DictTypeEnums.STANDARD_TRUE_FALSE, "2", "否"),
    AI_PATROL_ANALYSIS_1(DictTypeEnums.AI_PATROL_ANALYSIS, "1", "分析中"),
    AI_PATROL_ANALYSIS_0(DictTypeEnums.AI_PATROL_ANALYSIS, "0", "分析完成"),
    MAINTAIN_TYPE_裂缝处置(DictTypeEnums.MAINTAIN_TYPE, "裂缝处置", "裂缝处置"),
    MAINTAIN_TYPE_路面微表处(DictTypeEnums.MAINTAIN_TYPE, "路面微表处", "路面微表处"),
    MAINTAIN_TYPE_2路面沥青防渗(DictTypeEnums.MAINTAIN_TYPE, "路面沥青防渗", "路面沥青防渗"),
    MAINTAIN_TYPE_路面刨铣与恢复(DictTypeEnums.MAINTAIN_TYPE, "路面刨铣与恢复", "路面刨铣与恢复"),
    MAINTAIN_TYPE_路基病害处置(DictTypeEnums.MAINTAIN_TYPE, "路基病害处置", "路基病害处置"),
    MAINTAIN_TYPE_桥梁附属设施维修(DictTypeEnums.MAINTAIN_TYPE, "桥梁附属设施维修", "桥梁附属设施维修"),
    MAINTAIN_TYPE_交通安全设施维修(DictTypeEnums.MAINTAIN_TYPE, "交通安全设施维修", "交通安全设施维修"),
    AUTH_BUTTON_add(DictTypeEnums.AUTH_BUTTON, "add", "添加"),
    AUTH_BUTTON_eidt(DictTypeEnums.AUTH_BUTTON, "eidt", "编辑"),
    AUTH_BUTTON_del(DictTypeEnums.AUTH_BUTTON, "del", "删除"),
    AUTH_BUTTON_look(DictTypeEnums.AUTH_BUTTON, "look", "查看"),
    ENABLE_1(DictTypeEnums.ENABLE, "1", "启用"),
    ENABLE_0(DictTypeEnums.ENABLE, "0", "未启用"),
    LINK_TYPE_1(DictTypeEnums.LINK_TYPE, "1", "弹窗"),
    ROAD_LEVEL_G(DictTypeEnums.ROAD_LEVEL, "G", "国道"),
    ROAD_LEVEL_S(DictTypeEnums.ROAD_LEVEL, "S", "省道"),
    ROAD_LEVEL_X(DictTypeEnums.ROAD_LEVEL, "X", "县道"),
    ROAD_LEVEL_Y(DictTypeEnums.ROAD_LEVEL, "Y", "乡道"),
    ROAD_LEVEL_C(DictTypeEnums.ROAD_LEVEL, "C", "村道"),
    ROAD_LEVEL_W(DictTypeEnums.ROAD_LEVEL, "W", "组路"),
    PROPERTY_1(DictTypeEnums.PROPERTY, "1", "边界"),
    PROPERTY_2(DictTypeEnums.PROPERTY, "2", "路线"),
    PROPERTY_3(DictTypeEnums.PROPERTY, "3", "桥梁"),
    PROPERTY_4(DictTypeEnums.PROPERTY, "4", "隧道"),
    PROPERTY_5(DictTypeEnums.PROPERTY, "5", "涵洞"),
    PROPERTY_6(DictTypeEnums.PROPERTY, "6", "边坡"),

    BRIDGE_ABUTMENT_10(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "10", "无桥台"),
    BRIDGE_ABUTMENT_19(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "19", "多柱框架式桥台"),
    BRIDGE_ABUTMENT_11(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "11", "U形桥台"),
    BRIDGE_ABUTMENT_20(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "20", "墙式桥台"),
    BRIDGE_ABUTMENT_12(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "12", "八字形桥台"),
    BRIDGE_ABUTMENT_21(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "21", "组合式桥台"),
    BRIDGE_ABUTMENT_13(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "13", "埋置式桥台"),
    BRIDGE_ABUTMENT_22(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "22", "支撑式桥台"),
    BRIDGE_ABUTMENT_14(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "14", "拱形桥台"),
    BRIDGE_ABUTMENT_23(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "23", "一字形桥台"),
    BRIDGE_ABUTMENT_15(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "15", "埋置衡重式桥台"),
    BRIDGE_ABUTMENT_24(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "24", "扶壁(空腹)式桥台"),
    BRIDGE_ABUTMENT_16(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "16", "空箱式桥台"),
    BRIDGE_ABUTMENT_25(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "25", "锚碗板式桥台"),
    BRIDGE_ABUTMENT_17(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "17", "构架式墩"),
    BRIDGE_ABUTMENT_90(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "90", "其他"),
    BRIDGE_ABUTMENT_18(DictTypeEnums.BRIDGE_ABUTMENT_TYPE, "18", "双柱框架式桥台"),


    CLASSIFICATION_OF_TUNNEL_1(DictTypeEnums.CLASSIFICATION_OF_TUNNEL, "1", "特长隧道"),
    CLASSIFICATION_OF_TUNNEL_2(DictTypeEnums.CLASSIFICATION_OF_TUNNEL, "2", "长隧道"),
    CLASSIFICATION_OF_TUNNEL_3(DictTypeEnums.CLASSIFICATION_OF_TUNNEL, "3", "中隧道"),
    CLASSIFICATION_OF_TUNNEL_4(DictTypeEnums.CLASSIFICATION_OF_TUNNEL, "4", "短隧道"),
    PATROL_CAR_TYPE_1(DictTypeEnums.PATROL_CAR_TYPE, "1", "大型客车"),
    PATROL_CAR_TYPE_2(DictTypeEnums.PATROL_CAR_TYPE, "2", "中型客车"),
    PATROL_CAR_TYPE_3(DictTypeEnums.PATROL_CAR_TYPE, "3", "小型客车"),
    PATROL_CAR_TYPE_4(DictTypeEnums.PATROL_CAR_TYPE, "4", "城市公交车"),
    BUSINESS_TYPE_4(DictTypeEnums.BUSINESS_TYPE, "4", "授权"),
    BUSINESS_TYPE_5(DictTypeEnums.BUSINESS_TYPE, "5", "导出"),
    BUSINESS_TYPE_6(DictTypeEnums.BUSINESS_TYPE, "6", "导入"),
    BUSINESS_TYPE_7(DictTypeEnums.BUSINESS_TYPE, "7", "强退"),
    BUSINESS_TYPE_8(DictTypeEnums.BUSINESS_TYPE, "8", "生成代码"),
    BUSINESS_TYPE_9(DictTypeEnums.BUSINESS_TYPE, "9", "清空数据"),
    NEWS_TYPE_1(DictTypeEnums.NEWS_TYPE, "1", "行业新闻"),
    NEWS_TYPE_0(DictTypeEnums.NEWS_TYPE, "0", "企业新闻"),

    HOLDER_TYPE_1(DictTypeEnums.HOLDER_TYPE, "1", "自然人"),

    HOLDER_TYPE_2(DictTypeEnums.HOLDER_TYPE, "2", "法人"),

    HOLDER_TYPE_3(DictTypeEnums.HOLDER_TYPE, "3", "混合"),

    HOLDER_TYPE_4(DictTypeEnums.HOLDER_TYPE, "4", "其它"),

    QUALIFICATION_LEVEL_CODE_01(DictTypeEnums.DZZZ_QUALIFICATION_LEVEL_CODE, "01", "公路工程甲级"),
    QUALIFICATION_LEVEL_CODE_02(DictTypeEnums.DZZZ_QUALIFICATION_LEVEL_CODE, "02", "公路工程乙级"),
    QUALIFICATION_LEVEL_CODE_03(DictTypeEnums.DZZZ_QUALIFICATION_LEVEL_CODE, "03", "公路机电工程专项"),
    QUALIFICATION_LEVEL_CODE_11(DictTypeEnums.DZZZ_QUALIFICATION_LEVEL_CODE, "11", "水运工程甲级"),
    QUALIFICATION_LEVEL_CODE_12(DictTypeEnums.DZZZ_QUALIFICATION_LEVEL_CODE, "12", "水运工程乙级"),
    QUALIFICATION_LEVEL_CODE_13(DictTypeEnums.DZZZ_QUALIFICATION_LEVEL_CODE, "13", "水运机电工程专项"),

    DZZZ_BUSINESS_SCOPE_CODE_01(DictTypeEnums.DZZZ_BUSINESS_SCOPE_CODE, "01", "从事一、二、三类公路工程项目的监理业务"),
    DZZZ_BUSINESS_SCOPE_CODE_02(DictTypeEnums.DZZZ_BUSINESS_SCOPE_CODE, "02", "从事二、三类公路工程项目的监理业务"),
    DZZZ_BUSINESS_SCOPE_CODE_03(DictTypeEnums.DZZZ_BUSINESS_SCOPE_CODE, "03", "从事各等级公路工程机电工程项目的监理业务"),
    DZZZ_BUSINESS_SCOPE_CODE_11(DictTypeEnums.DZZZ_BUSINESS_SCOPE_CODE, "11", "从事大、中、小型水运工程项目的监理业务"),
    DZZZ_BUSINESS_SCOPE_CODE_12(DictTypeEnums.DZZZ_BUSINESS_SCOPE_CODE, "12", "从事中、小型水运工程项目的监理业务"),
    DZZZ_BUSINESS_SCOPE_CODE_13(DictTypeEnums.DZZZ_BUSINESS_SCOPE_CODE, "13", "从事各类型水运工程机电工程项目的监理业务"),

    DZZZ_CERTIFICATE_STATUS_CODE_10(DictTypeEnums.DZZZ_CERTIFICATE_STATUS_CODE, "10", "有效"),
    DZZZ_CERTIFICATE_STATUS_CODE_01(DictTypeEnums.DZZZ_CERTIFICATE_STATUS_CODE, "01", "无效（已换发）"),
    DZZZ_CERTIFICATE_STATUS_CODE_02(DictTypeEnums.DZZZ_CERTIFICATE_STATUS_CODE, "02", "无效（已过期）"),
    DZZZ_CERTIFICATE_STATUS_CODE_09(DictTypeEnums.DZZZ_CERTIFICATE_STATUS_CODE, "09", "无效（其他）"),

    SHIP_CERTIFICATE_STATUS_CODE_10(DictTypeEnums.SHIP_CERTIFICATE_STATUS_CODE, "10", "有效"),

    SHIP_CERTIFICATE_STATUS_CODE_01(DictTypeEnums.SHIP_CERTIFICATE_STATUS_CODE, "01", "无效（已换发）"),
    SHIP_CERTIFICATE_STATUS_CODE_02(DictTypeEnums.SHIP_CERTIFICATE_STATUS_CODE, "02", "无效（已过期）"),
    SHIP_CERTIFICATE_STATUS_CODE_03(DictTypeEnums.SHIP_CERTIFICATE_STATUS_CODE, "03", "无效（已注销）"),
    SHIP_CERTIFICATE_STATUS_CODE_04(DictTypeEnums.SHIP_CERTIFICATE_STATUS_CODE, "04", "无效（已撤销）"),
    SHIP_CERTIFICATE_STATUS_CODE_05(DictTypeEnums.SHIP_CERTIFICATE_STATUS_CODE, "05", "无效（已吊销）"),
    SHIP_CERTIFICATE_STATUS_CODE_09(DictTypeEnums.SHIP_CERTIFICATE_STATUS_CODE, "09", "无效（其他）"),

    SUPERVISION_OF_ENTERPRISES_CERTIFICATE_STATUS_CODE_10(DictTypeEnums.SUPERVISION_OF_ENTERPRISES_CERTIFICATE_STATUS_CODE, "10", "有效"),
    SUPERVISION_OF_ENTERPRISES_CERTIFICATE_STATUS_CODE_01(DictTypeEnums.SUPERVISION_OF_ENTERPRISES_CERTIFICATE_STATUS_CODE, "01", "无效（已换发）"),
    SUPERVISION_OF_ENTERPRISES_CERTIFICATE_STATUS_CODE_02(DictTypeEnums.SUPERVISION_OF_ENTERPRISES_CERTIFICATE_STATUS_CODE, "02", "无效（已过期）"),
    SUPERVISION_OF_ENTERPRISES_CERTIFICATE_STATUS_CODE_09(DictTypeEnums.SUPERVISION_OF_ENTERPRISES_CERTIFICATE_STATUS_CODE, "09", "无效（其他）"),

    DZZZ_SEX_0(DictTypeEnums.DZZZ_SEX, "0", "未知的性别"),

    DZZZ_SEX_1(DictTypeEnums.DZZZ_SEX, "1", "男性"),

    DZZZ_SEX_2(DictTypeEnums.DZZZ_SEX, "2", "女性"),

    DZZZ_SEX_9(DictTypeEnums.DZZZ_SEX, "9", "未说明的性别"),


    BLACK_LIST(DictTypeEnums.TYPES_OF_ILLEGAL_OPERATIONS_MONITORING, "1", "黑名单车辆"),


    SUSPECTED_OF_ILLEGAL_OPERATIONS(DictTypeEnums.TYPES_OF_ILLEGAL_OPERATIONS_MONITORING, "2", "疑似非法营运车辆"),

    BRIDGE_PATROL_PROBLEM_TYPE_1(DictTypeEnums.BRIDGE_PATROL_PROBLEM_TYPE, "1", "桥梁连接处异常"),
    BRIDGE_PATROL_PROBLEM_TYPE_2(DictTypeEnums.BRIDGE_PATROL_PROBLEM_TYPE, "2", "铺装、伸缩逢异常"),
    BRIDGE_PATROL_PROBLEM_TYPE_3(DictTypeEnums.BRIDGE_PATROL_PROBLEM_TYPE, "3", "栏杆、护栏缺损"),
    BRIDGE_PATROL_PROBLEM_TYPE_4(DictTypeEnums.BRIDGE_PATROL_PROBLEM_TYPE, "4", "标志标牌损害"),
    BRIDGE_PATROL_PROBLEM_TYPE_5(DictTypeEnums.BRIDGE_PATROL_PROBLEM_TYPE, "5", "桥梁线型异常"),
    BRIDGE_PATROL_PROBLEM_TYPE_6(DictTypeEnums.BRIDGE_PATROL_PROBLEM_TYPE, "6", "异常振动、摆动、声响"),
    BRIDGE_PATROL_PROBLEM_TYPE_7(DictTypeEnums.BRIDGE_PATROL_PROBLEM_TYPE, "7", "安全保护区存在侵害桥梁安全情况"),


    BRIDGE_PATROL_DISPOSAL_SITUATION_1(DictTypeEnums.BRIDGE_PATROL_DISPOSAL_SITUATION, "1", "未处置"),

    BRIDGE_PATROL_DISPOSAL_SITUATION_2(DictTypeEnums.BRIDGE_PATROL_DISPOSAL_SITUATION, "2", "处置中"),

    BRIDGE_PATROL_DISPOSAL_SITUATION_3(DictTypeEnums.BRIDGE_PATROL_DISPOSAL_SITUATION, "3", "处置完成"),


    BRIDGE_COMPONENT_TYPE_1(DictTypeEnums.BRIDGE_COMPONENT_TYPE, "1", "上部构件"),

    BRIDGE_COMPONENT_TYPE_2(DictTypeEnums.BRIDGE_COMPONENT_TYPE, "2", "下部构件"),

    BRIDGE_COMPONENT_TYPE_3(DictTypeEnums.BRIDGE_COMPONENT_TYPE, "3", "桥面系"),


    BRIDGE_CHECK_TYPE_1(DictTypeEnums.BRIDGE_CHECK_TYPE, "1", "定期检查"),
    BRIDGE_CHECK_TYPE_2(DictTypeEnums.BRIDGE_CHECK_TYPE, "2", "特殊检查"),
    BRIDGE_CHECK_TYPE_3(DictTypeEnums.BRIDGE_CHECK_TYPE, "3", "经常检查"),
    BRIDGE_CHECK_TYPE_4(DictTypeEnums.BRIDGE_CHECK_TYPE, "4", "其他"),

    BRIDGE_DISEASE_TYPE_1(DictTypeEnums.BRIDGE_DISEASE_TYPE, "1", "蜂窝、麻面"),
    BRIDGE_DISEASE_TYPE_2(DictTypeEnums.BRIDGE_DISEASE_TYPE, "2", "剥溶、掉角"),
    BRIDGE_DISEASE_TYPE_3(DictTypeEnums.BRIDGE_DISEASE_TYPE, "3", "空洞、孔洞"),
    BRIDGE_DISEASE_TYPE_4(DictTypeEnums.BRIDGE_DISEASE_TYPE, "4", "混凝土保护层厚度"),
    BRIDGE_DISEASE_TYPE_5(DictTypeEnums.BRIDGE_DISEASE_TYPE, "5", "钢筋锈蚀"),
    BRIDGE_DISEASE_TYPE_6(DictTypeEnums.BRIDGE_DISEASE_TYPE, "6", "混凝土碳化"),
    BRIDGE_DISEASE_TYPE_7(DictTypeEnums.BRIDGE_DISEASE_TYPE, "7", "混凝土强度"),
    BRIDGE_DISEASE_TYPE_8(DictTypeEnums.BRIDGE_DISEASE_TYPE, "8", "跨中挠度"),
    BRIDGE_DISEASE_TYPE_9(DictTypeEnums.BRIDGE_DISEASE_TYPE, "9", "结构变位"),
    BRIDGE_DISEASE_TYPE_10(DictTypeEnums.BRIDGE_DISEASE_TYPE, "10", "预应力构件损伤（锚头、钢绞线、齿板等）"),
    BRIDGE_DISEASE_TYPE_11(DictTypeEnums.BRIDGE_DISEASE_TYPE, "11", "简支梁（板）桥、刚架桥裂缝"),
    BRIDGE_DISEASE_TYPE_12(DictTypeEnums.BRIDGE_DISEASE_TYPE, "12", "连续梁桥、连续刚构桥、悬臂梁桥、T形刚构桥裂缝"),

    ROUTE_SECTION_GLSX_1(DictTypeEnums.ROUTE_SECTION_GLSX, "1", "干线公路接养"),

    ROUTE_SECTION_GLSX_2(DictTypeEnums.ROUTE_SECTION_GLSX, "2", "非干线公路接养"),

    // 枚举常量
    JTT_415_2006_5_3_1_1(DictTypeEnums.JTT_415_2006_5_3_1, "1", "营业"),
    JTT_415_2006_5_3_1_2(DictTypeEnums.JTT_415_2006_5_3_1, "2", "停业"),
    JTT_415_2006_5_3_1_3(DictTypeEnums.JTT_415_2006_5_3_1, "3", "整改"),
    JTT_415_2006_5_3_1_4(DictTypeEnums.JTT_415_2006_5_3_1, "4", "停业整顿"),
    JTT_415_2006_5_3_1_5(DictTypeEnums.JTT_415_2006_5_3_1, "5", "歇业"),
    JTT_415_2006_5_3_1_6(DictTypeEnums.JTT_415_2006_5_3_1, "6", "注销"),
    JTT_415_2006_5_3_1_9(DictTypeEnums.JTT_415_2006_5_3_1, "9", "其他"),


    JTT_415_2006_5_1_8_100(DictTypeEnums.JTT_415_2006_5_1_8, "100", "内资"),
    JTT_415_2006_5_1_8_110(DictTypeEnums.JTT_415_2006_5_1_8, "110", "国有全资"),
    JTT_415_2006_5_1_8_120(DictTypeEnums.JTT_415_2006_5_1_8, "120", "集体全资"),
    JTT_415_2006_5_1_8_130(DictTypeEnums.JTT_415_2006_5_1_8, "130", "股份合作"),
    JTT_415_2006_5_1_8_140(DictTypeEnums.JTT_415_2006_5_1_8, "140", "联营"),
    JTT_415_2006_5_1_8_141(DictTypeEnums.JTT_415_2006_5_1_8, "141", "国有联营"),
    JTT_415_2006_5_1_8_142(DictTypeEnums.JTT_415_2006_5_1_8, "142", "集体联营"),
    JTT_415_2006_5_1_8_143(DictTypeEnums.JTT_415_2006_5_1_8, "143", "国有与集体联营"),
    JTT_415_2006_5_1_8_149(DictTypeEnums.JTT_415_2006_5_1_8, "149", "其他联营"),
    JTT_415_2006_5_1_8_150(DictTypeEnums.JTT_415_2006_5_1_8, "150", "有限责任(公司)"),
    JTT_415_2006_5_1_8_151(DictTypeEnums.JTT_415_2006_5_1_8, "151", "国有独资(公司)"),
    JTT_415_2006_5_1_8_159(DictTypeEnums.JTT_415_2006_5_1_8, "159", "其他有限责任(公司)"),
    JTT_415_2006_5_1_8_160(DictTypeEnums.JTT_415_2006_5_1_8, "160", "股份有限公司"),
    JTT_415_2006_5_1_8_170(DictTypeEnums.JTT_415_2006_5_1_8, "170", "私有"),
    JTT_415_2006_5_1_8_171(DictTypeEnums.JTT_415_2006_5_1_8, "171", "私有独资"),
    JTT_415_2006_5_1_8_172(DictTypeEnums.JTT_415_2006_5_1_8, "172", "私有合作"),
    JTT_415_2006_5_1_8_173(DictTypeEnums.JTT_415_2006_5_1_8, "173", "私营有限责任(公司)"),
    JTT_415_2006_5_1_8_174(DictTypeEnums.JTT_415_2006_5_1_8, "174", "私营股份有限公司"),
    JTT_415_2006_5_1_8_175(DictTypeEnums.JTT_415_2006_5_1_8, "175", "个体经营"),
    JTT_415_2006_5_1_8_179(DictTypeEnums.JTT_415_2006_5_1_8, "179", "其他私有"),
    JTT_415_2006_5_1_8_190(DictTypeEnums.JTT_415_2006_5_1_8, "190", "其他内资"),
    JTT_415_2006_5_1_8_200(DictTypeEnums.JTT_415_2006_5_1_8, "200", "港、澳、台投资"),
    JTT_415_2006_5_1_8_210(DictTypeEnums.JTT_415_2006_5_1_8, "210", "内地和港、澳、台合资"),
    JTT_415_2006_5_1_8_220(DictTypeEnums.JTT_415_2006_5_1_8, "220", "内地和港、澳、台合作"),
    JTT_415_2006_5_1_8_230(DictTypeEnums.JTT_415_2006_5_1_8, "230", "港、澳、台独资"),
    JTT_415_2006_5_1_8_240(DictTypeEnums.JTT_415_2006_5_1_8, "240", "港、澳、台投资股份有限公司"),
    JTT_415_2006_5_1_8_290(DictTypeEnums.JTT_415_2006_5_1_8, "290", "其他港、澳、台投资"),
    JTT_415_2006_5_1_8_300(DictTypeEnums.JTT_415_2006_5_1_8, "300", "国外投资"),
    JTT_415_2006_5_1_8_310(DictTypeEnums.JTT_415_2006_5_1_8, "310", "中外合资"),
    JTT_415_2006_5_1_8_320(DictTypeEnums.JTT_415_2006_5_1_8, "320", "中外合作"),
    JTT_415_2006_5_1_8_330(DictTypeEnums.JTT_415_2006_5_1_8, "330", "外资"),
    JTT_415_2006_5_1_8_340(DictTypeEnums.JTT_415_2006_5_1_8, "340", "国外投资股份有限公司"),
    JTT_415_2006_5_1_8_390(DictTypeEnums.JTT_415_2006_5_1_8, "390", "其他国外投资"),
    JTT_415_2006_5_1_8_900(DictTypeEnums.JTT_415_2006_5_1_8, "900", "其他"),

    JTT_415_2006_5_5_2_1(DictTypeEnums.JTT_415_2006_5_5_2, "1", "有效"),
    JTT_415_2006_5_5_2_2(DictTypeEnums.JTT_415_2006_5_5_2, "2", "无效"),

    JTT_415_2006_5_5_1_100(DictTypeEnums.JTT_415_2006_5_5_1, "100", "道路运输经营许可证"),
    JTT_415_2006_5_5_1_110(DictTypeEnums.JTT_415_2006_5_5_1, "110", "道路运输经营许可证正本"),
    JTT_415_2006_5_5_1_120(DictTypeEnums.JTT_415_2006_5_5_1, "120", "道路运输经营许可证副本"),
    JTT_415_2006_5_5_1_200(DictTypeEnums.JTT_415_2006_5_5_1, "200", "车辆营运证(道路运输证核新版证件)"),
    JTT_415_2006_5_5_1_210(DictTypeEnums.JTT_415_2006_5_5_1, "210", "道路运输证"),
    JTT_415_2006_5_5_1_220(DictTypeEnums.JTT_415_2006_5_5_1, "220", "车辆暂扣证明"),
    JTT_415_2006_5_5_1_230(DictTypeEnums.JTT_415_2006_5_5_1, "230", "车辆年审标贴"),
    JTT_415_2006_5_5_1_240(DictTypeEnums.JTT_415_2006_5_5_1, "240", "国际汽车运输行车许可证"),
    JTT_415_2006_5_5_1_241(DictTypeEnums.JTT_415_2006_5_5_1, "241", "A 种行车许可证"),
    JTT_415_2006_5_5_1_242(DictTypeEnums.JTT_415_2006_5_5_1, "242", "B 种行车许可证"),
    JTT_415_2006_5_5_1_243(DictTypeEnums.JTT_415_2006_5_5_1, "243", "C 种行车许可证"),
    JTT_415_2006_5_5_1_300(DictTypeEnums.JTT_415_2006_5_5_1, "300", "营运标志牌"),
    JTT_415_2006_5_5_1_310(DictTypeEnums.JTT_415_2006_5_5_1, "310", "班车客运标志牌"),
    JTT_415_2006_5_5_1_311(DictTypeEnums.JTT_415_2006_5_5_1, "311", "县内班车客运标志牌"),
    JTT_415_2006_5_5_1_312(DictTypeEnums.JTT_415_2006_5_5_1, "312", "县际班车客运标志牌"),
    JTT_415_2006_5_5_1_313(DictTypeEnums.JTT_415_2006_5_5_1, "313", "市际班车客运标志牌"),
    JTT_415_2006_5_5_1_314(DictTypeEnums.JTT_415_2006_5_5_1, "314", "省际班车客运标志牌"),
    JTT_415_2006_5_5_1_320(DictTypeEnums.JTT_415_2006_5_5_1, "320", "包车客运标志牌"),
    JTT_415_2006_5_5_1_321(DictTypeEnums.JTT_415_2006_5_5_1, "321", "县内包车客运标志牌"),
    JTT_415_2006_5_5_1_322(DictTypeEnums.JTT_415_2006_5_5_1, "322", "县际包车客运标志牌"),
    JTT_415_2006_5_5_1_323(DictTypeEnums.JTT_415_2006_5_5_1, "323", "市际包车客运标志牌"),
    JTT_415_2006_5_5_1_324(DictTypeEnums.JTT_415_2006_5_5_1, "324", "省际包车客运标志牌"),
    JTT_415_2006_5_5_1_330(DictTypeEnums.JTT_415_2006_5_5_1, "330", "临时客运标志牌"),
    JTT_415_2006_5_5_1_331(DictTypeEnums.JTT_415_2006_5_5_1, "331", "县内临时客运标志牌"),
    JTT_415_2006_5_5_1_332(DictTypeEnums.JTT_415_2006_5_5_1, "332", "县际临时客运标志牌"),
    JTT_415_2006_5_5_1_333(DictTypeEnums.JTT_415_2006_5_5_1, "333", "市际临时客运标志牌"),
    JTT_415_2006_5_5_1_334(DictTypeEnums.JTT_415_2006_5_5_1, "334", "省际临时客运标志牌"),
    JTT_415_2006_5_5_1_340(DictTypeEnums.JTT_415_2006_5_5_1, "340", "道路客运班线经营许可证明"),
    JTT_415_2006_5_5_1_400(DictTypeEnums.JTT_415_2006_5_5_1, "400", "从业资格证"),
    JTT_415_2006_5_5_1_410(DictTypeEnums.JTT_415_2006_5_5_1, "410", "道路运输从业人员资格证"),
    JTT_415_2006_5_5_1_420(DictTypeEnums.JTT_415_2006_5_5_1, "420", "教练员证"),
    JTT_415_2006_5_5_1_430(DictTypeEnums.JTT_415_2006_5_5_1, "430", "维修技工证"),
    JTT_415_2006_5_5_1_440(DictTypeEnums.JTT_415_2006_5_5_1, "440", "质检员证"),
    JTT_415_2006_5_5_1_450(DictTypeEnums.JTT_415_2006_5_5_1, "450", "总质检员证"),
    JTT_415_2006_5_5_1_460(DictTypeEnums.JTT_415_2006_5_5_1, "460", "价格结算员证"),
    JTT_415_2006_5_5_1_470(DictTypeEnums.JTT_415_2006_5_5_1, "470", "考核员证"),
    JTT_415_2006_5_5_1_900(DictTypeEnums.JTT_415_2006_5_5_1, "900", "其他"),

    JTT_415_2006_5_5_3_1(DictTypeEnums.JTT_415_2006_5_5_3, "1", "初领"),
    JTT_415_2006_5_5_3_2(DictTypeEnums.JTT_415_2006_5_5_3, "2", "证照丢失"),
    JTT_415_2006_5_5_3_3(DictTypeEnums.JTT_415_2006_5_5_3, "3", "证照污损"),
    JTT_415_2006_5_5_3_4(DictTypeEnums.JTT_415_2006_5_5_3, "4", "经营状态变更"),
    JTT_415_2006_5_5_3_5(DictTypeEnums.JTT_415_2006_5_5_3, "5", "证照信息变更"),
    JTT_415_2006_5_5_3_6(DictTypeEnums.JTT_415_2006_5_5_3, "6", "证照到期"),
    JTT_415_2006_5_5_3_7(DictTypeEnums.JTT_415_2006_5_5_3, "7", "证照改版"),
    JTT_415_2006_5_5_3_9(DictTypeEnums.JTT_415_2006_5_5_3, "9", "其他"),


    JTT_415_2006_5_3_6_1(DictTypeEnums.JTT_415_2006_5_3_6, "1", "一级站"),
    JTT_415_2006_5_3_6_2(DictTypeEnums.JTT_415_2006_5_3_6, "2", "二级站"),
    JTT_415_2006_5_3_6_3(DictTypeEnums.JTT_415_2006_5_3_6, "3", "三级站"),
    JTT_415_2006_5_3_6_4(DictTypeEnums.JTT_415_2006_5_3_6, "4", "四级站"),
    JTT_415_2006_5_3_6_5(DictTypeEnums.JTT_415_2006_5_3_6, "5", "五级站"),
    JTT_415_2006_5_3_6_9(DictTypeEnums.JTT_415_2006_5_3_6, "9", "未评定"),

    JTT_415_2006_5_2_11_1(DictTypeEnums.JTT_415_2006_5_2_11, "1", "专项"),
    JTT_415_2006_5_2_11_2(DictTypeEnums.JTT_415_2006_5_2_11, "2", "综合"),

    JTT_415_2006_5_4_5_1(DictTypeEnums.JTT_415_2006_5_4_5, "1", "汽油"),
    JTT_415_2006_5_4_5_2(DictTypeEnums.JTT_415_2006_5_4_5, "2", "柴油"),
    JTT_415_2006_5_4_5_3(DictTypeEnums.JTT_415_2006_5_4_5, "3", "天然气"),
    JTT_415_2006_5_4_5_4(DictTypeEnums.JTT_415_2006_5_4_5, "4", "液化气"),
    JTT_415_2006_5_4_5_5(DictTypeEnums.JTT_415_2006_5_4_5, "5", "电动"),
    JTT_415_2006_5_4_5_9(DictTypeEnums.JTT_415_2006_5_4_5, "9", "其他"),

    JTT_415_2006_5_4_6_10(DictTypeEnums.JTT_415_2006_5_4_6, "10", "营运"),
    JTT_415_2006_5_4_6_21(DictTypeEnums.JTT_415_2006_5_4_6, "21", "停运"),
    JTT_415_2006_5_4_6_22(DictTypeEnums.JTT_415_2006_5_4_6, "22", "挂失"),
    JTT_415_2006_5_4_6_31(DictTypeEnums.JTT_415_2006_5_4_6, "31", "迁出(过户)"),
    JTT_415_2006_5_4_6_32(DictTypeEnums.JTT_415_2006_5_4_6, "32", "迁出(转籍)"),
    JTT_415_2006_5_4_6_33(DictTypeEnums.JTT_415_2006_5_4_6, "33", "报废"),
    JTT_415_2006_5_4_6_34(DictTypeEnums.JTT_415_2006_5_4_6, "34", "歇业"),
    JTT_415_2006_5_4_6_80(DictTypeEnums.JTT_415_2006_5_4_6, "80", "注销"),
    JTT_415_2006_5_4_6_90(DictTypeEnums.JTT_415_2006_5_4_6, "90", "其他"),

    JTT_415_2006_5_4_10_A1(DictTypeEnums.JTT_415_2006_5_4_10, "A1", "大型客车"),
    JTT_415_2006_5_4_10_A2(DictTypeEnums.JTT_415_2006_5_4_10, "A2", "牵引车"),
    JTT_415_2006_5_4_10_A3(DictTypeEnums.JTT_415_2006_5_4_10, "A3", "城市公交车"),
    JTT_415_2006_5_4_10_B1(DictTypeEnums.JTT_415_2006_5_4_10, "B1", "中型客车"),
    JTT_415_2006_5_4_10_B2(DictTypeEnums.JTT_415_2006_5_4_10, "B2", "大型货车"),
    JTT_415_2006_5_4_10_C1(DictTypeEnums.JTT_415_2006_5_4_10, "C1", "小型汽车"),
    JTT_415_2006_5_4_10_C2(DictTypeEnums.JTT_415_2006_5_4_10, "C2", "小型自动挡汽车"),
    JTT_415_2006_5_4_10_C3(DictTypeEnums.JTT_415_2006_5_4_10, "C3", "低速载货汽车"),
    JTT_415_2006_5_4_10_C4(DictTypeEnums.JTT_415_2006_5_4_10, "C4", "三轮汽车"),
    JTT_415_2006_5_4_10_D(DictTypeEnums.JTT_415_2006_5_4_10, "D", "普通三轮摩托车"),
    JTT_415_2006_5_4_10_E(DictTypeEnums.JTT_415_2006_5_4_10, "E", "普通二轮摩托车"),
    JTT_415_2006_5_4_10_F(DictTypeEnums.JTT_415_2006_5_4_10, "F", "轻便摩托车"),
    JTT_415_2006_5_4_10_M(DictTypeEnums.JTT_415_2006_5_4_10, "M", "轮式自行机械车"),
    JTT_415_2006_5_4_10_N(DictTypeEnums.JTT_415_2006_5_4_10, "N", "无轨电车"),
    JTT_415_2006_5_4_10_P(DictTypeEnums.JTT_415_2006_5_4_10, "P", "有轨电车"),

    JTT_415_2006_5_4_12_1(DictTypeEnums.JTT_415_2006_5_4_12, "1", "蓝色"),
    JTT_415_2006_5_4_12_2(DictTypeEnums.JTT_415_2006_5_4_12, "2", "黄色"),
    JTT_415_2006_5_4_12_3(DictTypeEnums.JTT_415_2006_5_4_12, "3", "黑色"),
    JTT_415_2006_5_4_12_4(DictTypeEnums.JTT_415_2006_5_4_12, "4", "白色"),
    JTT_415_2006_5_4_12_5(DictTypeEnums.JTT_415_2006_5_4_12, "5", "绿色"),
    JTT_415_2006_5_4_12_9(DictTypeEnums.JTT_415_2006_5_4_12, "9", "其他"),

    JTT_697_7_2014_5_21_11(DictTypeEnums.JTT_697_7_2014_5_21, "11", "特大型高三级"),
    JTT_697_7_2014_5_21_12(DictTypeEnums.JTT_697_7_2014_5_21, "12", "特大型高二级"),
    JTT_697_7_2014_5_21_13(DictTypeEnums.JTT_697_7_2014_5_21, "13", "特大型高一级"),
    JTT_697_7_2014_5_21_14(DictTypeEnums.JTT_697_7_2014_5_21, "14", "特大型中级"),
    JTT_697_7_2014_5_21_15(DictTypeEnums.JTT_697_7_2014_5_21, "15", "特大型普通级"),
    JTT_697_7_2014_5_21_21(DictTypeEnums.JTT_697_7_2014_5_21, "21", "大型高三级"),
    JTT_697_7_2014_5_21_22(DictTypeEnums.JTT_697_7_2014_5_21, "22", "大型高二级"),
    JTT_697_7_2014_5_21_23(DictTypeEnums.JTT_697_7_2014_5_21, "23", "大型高一级"),
    JTT_697_7_2014_5_21_24(DictTypeEnums.JTT_697_7_2014_5_21, "24", "大型中级"),
    JTT_697_7_2014_5_21_25(DictTypeEnums.JTT_697_7_2014_5_21, "25", "大型普通级"),
    JTT_697_7_2014_5_21_31(DictTypeEnums.JTT_697_7_2014_5_21, "31", "中型高二级"),
    JTT_697_7_2014_5_21_32(DictTypeEnums.JTT_697_7_2014_5_21, "32", "中型高一级"),
    JTT_697_7_2014_5_21_33(DictTypeEnums.JTT_697_7_2014_5_21, "33", "中型中级"),
    JTT_697_7_2014_5_21_34(DictTypeEnums.JTT_697_7_2014_5_21, "34", "中型普通级"),
    JTT_697_7_2014_5_21_41(DictTypeEnums.JTT_697_7_2014_5_21, "41", "小型高二级"),
    JTT_697_7_2014_5_21_42(DictTypeEnums.JTT_697_7_2014_5_21, "42", "小型高一级"),
    JTT_697_7_2014_5_21_43(DictTypeEnums.JTT_697_7_2014_5_21, "43", "小型中级"),
    JTT_697_7_2014_5_21_44(DictTypeEnums.JTT_697_7_2014_5_21, "44", "小型普通级"),
    JTT_697_7_2014_5_21_51(DictTypeEnums.JTT_697_7_2014_5_21, "51", "乘用车高级"),
    JTT_697_7_2014_5_21_53(DictTypeEnums.JTT_697_7_2014_5_21, "53", "乘用车中级"),
    JTT_697_7_2014_5_21_54(DictTypeEnums.JTT_697_7_2014_5_21, "54", "乘用车普通级"),
    JTT_697_7_2014_5_21_9(DictTypeEnums.JTT_697_7_2014_5_21, "9", "其他"),

    JTT_697_7_2014_5_2_1(DictTypeEnums.JTT_697_7_2014_5_2, "1", "居民身份证"),
    JTT_697_7_2014_5_2_2(DictTypeEnums.JTT_697_7_2014_5_2, "2", "军官证"),
    JTT_697_7_2014_5_2_3(DictTypeEnums.JTT_697_7_2014_5_2, "3", "警官证"),
    JTT_697_7_2014_5_2_4(DictTypeEnums.JTT_697_7_2014_5_2, "4", "护照"),
    JTT_697_7_2014_5_2_5(DictTypeEnums.JTT_697_7_2014_5_2, "5", "机动车驾驶证"),
    JTT_697_7_2014_5_2_6(DictTypeEnums.JTT_697_7_2014_5_2, "6", "港澳通行证"),
    JTT_697_7_2014_5_2_7(DictTypeEnums.JTT_697_7_2014_5_2, "7", "台胞证"),
    JTT_697_7_2014_5_2_9(DictTypeEnums.JTT_697_7_2014_5_2, "9", "其他国家认可的有效证件"),

    JTT_697_7_2014_5_22_01001(DictTypeEnums.JTT_697_7_2014_5_22, "01001", "经营性道路旅客运输驾驶员（客运驾驶员）"),
    JTT_697_7_2014_5_22_02001(DictTypeEnums.JTT_697_7_2014_5_22, "02001", "经管性道路货物运输驾驶员（货运驾驶员）"),
    JTT_697_7_2014_5_22_03001(DictTypeEnums.JTT_697_7_2014_5_22, "03001", "道路危险货物运输驾驶员（危货驾驶员）"),
    JTT_697_7_2014_5_22_03002(DictTypeEnums.JTT_697_7_2014_5_22, "03002", "道路危险货物运输装卸管理人员（危货装卸员）"),
    JTT_697_7_2014_5_22_03003(DictTypeEnums.JTT_697_7_2014_5_22, "03003", "道路危险货物运输押运人员（危货押运员）"),
    JTT_697_7_2014_5_22_03004(DictTypeEnums.JTT_697_7_2014_5_22, "03004", "道路危险货物运输专职安全管理人员（危货安全员）"),
    JTT_697_7_2014_5_22_03005(DictTypeEnums.JTT_697_7_2014_5_22, "03005", "剧化学品道路运输驾驶员（剧毒品驾驶员）"),
    JTT_697_7_2014_5_22_03006(DictTypeEnums.JTT_697_7_2014_5_22, "03006", "剧毒化学品道路运输装卸管理人员（剧毒品装卸员）"),
    JTT_697_7_2014_5_22_03007(DictTypeEnums.JTT_697_7_2014_5_22, "03007", "剧化学品道路运输押运人员（剧毒品押运员）"),
    JTT_697_7_2014_5_22_03008(DictTypeEnums.JTT_697_7_2014_5_22, "03008", "剧毒化学品道路运输专职安全管理人员（剧品安全员）"),
    JTT_697_7_2014_5_22_03009(DictTypeEnums.JTT_697_7_2014_5_22, "03009", "爆炸品道路运输驾驶员（爆炸品驾驶员）"),
    JTT_697_7_2014_5_22_03010(DictTypeEnums.JTT_697_7_2014_5_22, "03010", "爆炸品道路运输装卸管理人员（举炸品装卸员）"),
    JTT_697_7_2014_5_22_03011(DictTypeEnums.JTT_697_7_2014_5_22, "03011", "爆炸品道路运输押运人员（爆炸品押运员）"),
    JTT_697_7_2014_5_22_03012(DictTypeEnums.JTT_697_7_2014_5_22, "03012", "爆炸品道路运输专职安全管理人员（爆炸品安全员）"),
    JTT_697_7_2014_5_22_03013(DictTypeEnums.JTT_697_7_2014_5_22, "03013", "放射性物品道路运输驾驶员（放射品驾驶员）"),
    JTT_697_7_2014_5_22_03014(DictTypeEnums.JTT_697_7_2014_5_22, "03014", "放射性物品道路运输装卸管理人员（放射品装卸员）"),
    JTT_697_7_2014_5_22_03015(DictTypeEnums.JTT_697_7_2014_5_22, "03015", "放射性物品道路运输押运人员（放射品押运员）"),
    JTT_697_7_2014_5_22_03016(DictTypeEnums.JTT_697_7_2014_5_22, "03016", "放射性物品道路运输专职安全管理人员（放射品安全员）"),
    JTT_697_7_2014_5_22_04001(DictTypeEnums.JTT_697_7_2014_5_22, "04001", "机动车维修技术负责人员（维修技术负责）"),
    JTT_697_7_2014_5_22_04002(DictTypeEnums.JTT_697_7_2014_5_22, "04002", "机动乍维修质量检验人员（维修·质检）"),
    JTT_697_7_2014_5_22_04003(DictTypeEnums.JTT_697_7_2014_5_22, "04003", "机修技术人员（维修机修）"),
    JTT_697_7_2014_5_22_04004(DictTypeEnums.JTT_697_7_2014_5_22, "04004", "电器维修技术人员（维修·电器）"),
    JTT_697_7_2014_5_22_04005(DictTypeEnums.JTT_697_7_2014_5_22, "04005", "钣金(车身修复)技术人员（维修·钣金）"),
    JTT_697_7_2014_5_22_04006(DictTypeEnums.JTT_697_7_2014_5_22, "04006", "涂漆(车身涂装)技术人员（维修涂漆）"),
    JTT_697_7_2014_5_22_04007(DictTypeEnums.JTT_697_7_2014_5_22, "04007", "车辆技术评估(含检测)技术人员（维修技术评估）"),
    JTT_697_7_2014_5_22_05001(DictTypeEnums.JTT_697_7_2014_5_22, "05001", "理论教练员（理论教练员）"),
    JTT_697_7_2014_5_22_05002(DictTypeEnums.JTT_697_7_2014_5_22, "05002", "驾驶操作教练员（操作教练员）"),
    JTT_697_7_2014_5_22_05003(DictTypeEnums.JTT_697_7_2014_5_22, "05003", "道路客货运输从业资格培训教练员（客货教练员）"),
    JTT_697_7_2014_5_22_05005(DictTypeEnums.JTT_697_7_2014_5_22, "05005", "道路危险货物运输从业资格培训教练员（危货教练员）"),
    JTT_697_7_2014_5_22_05006(DictTypeEnums.JTT_697_7_2014_5_22, "05006", "放射性物品道路运输从业资格培训教练员（放射品教练员）"),
    JTT_697_7_2014_5_22_05007(DictTypeEnums.JTT_697_7_2014_5_22, "05007", "机动车残疾人驾驶培训教练员（残疾人教练员）"),
    JTT_697_7_2014_5_22_09001(DictTypeEnums.JTT_697_7_2014_5_22, "09001", "出租汽车驾驶员（出租车驾驶员）"),
    JTT_697_7_2014_5_22_09002(DictTypeEnums.JTT_697_7_2014_5_22, "09002", "巡游出租汽车驾驶员证"),
    JTT_697_7_2014_5_22_09003(DictTypeEnums.JTT_697_7_2014_5_22, "09003", "网络预约出租汽车驾驶员证"),
    JTT_697_7_2014_5_22_11001(DictTypeEnums.JTT_697_7_2014_5_22, "11001", "道路旅客运输及客运站经理人（客运经理人）"),
    JTT_697_7_2014_5_22_11002(DictTypeEnums.JTT_697_7_2014_5_22, "11002", "道路货物运输及站场经理人（货运经理人）"),
    JTT_697_7_2014_5_22_11003(DictTypeEnums.JTT_697_7_2014_5_22, "11003", "机动车检测维修经理人（维修经理人）"),
    JTT_697_7_2014_5_22_11004(DictTypeEnums.JTT_697_7_2014_5_22, "11004", "机动车驾驶培训经理人（驾培经理人）"),
    JTT_697_7_2014_5_22_12001(DictTypeEnums.JTT_697_7_2014_5_22, "12001", "汽车租赁业务员（租赁业务员）"),
    JTT_697_7_2014_5_22_12002(DictTypeEnums.JTT_697_7_2014_5_22, "12002", "道路旅客运输乘务员（客运乘务员）"),
    JTT_697_7_2014_5_22_12008(DictTypeEnums.JTT_697_7_2014_5_22, "12008", "机动车驾驶员培训机构教学负责人（驾培负责人）"),
    JTT_697_7_2014_5_22_12009(DictTypeEnums.JTT_697_7_2014_5_22, "12009", "机动车驾驶员培训机构结业考核人员（驾培考核员）"),
    JTT_697_7_2014_5_22_12010(DictTypeEnums.JTT_697_7_2014_5_22, "12010", "机动车检测维修企业价格结算员（维修结算员）"),
    JTT_697_7_2014_5_22_12011(DictTypeEnums.JTT_697_7_2014_5_22, "12011", "机动车检测维修企业业务接待员（维修接待员）"),
    JTT_697_7_2014_5_22_13001(DictTypeEnums.JTT_697_7_2014_5_22, "13001", "城市公共汽电车运输驾驶员（公交驾驶员）"),
    JTT_697_7_2014_5_22_14001(DictTypeEnums.JTT_697_7_2014_5_22, "14001", "城市轨道交通运输车辆驾驶员（轨道驾驶员）"),
    JTT_697_7_2014_5_22_14002(DictTypeEnums.JTT_697_7_2014_5_22, "14002", "城市轨道交通运输行车调度员（轨道调度员）"),
    JTT_697_7_2014_5_22_14003(DictTypeEnums.JTT_697_7_2014_5_22, "14003", "城市轨道交通运输行车值班员（轨道值班员）"),

    SLJZ_STATISTICS_TYPE_0(DictTypeEnums.SLJZ_STATISTICS_TYPE, "0", "天统计"),
    SLJZ_STATISTICS_TYPE_1(DictTypeEnums.SLJZ_STATISTICS_TYPE, "1", "周统计"),
    SLJZ_STATISTICS_TYPE_2(DictTypeEnums.SLJZ_STATISTICS_TYPE, "2", "月统计"),
    SLJZ_STATISTICS_TYPE_3(DictTypeEnums.SLJZ_STATISTICS_TYPE, "3", "年统计"),
    SLJZ_STATISTICS_TYPE_4(DictTypeEnums.SLJZ_STATISTICS_TYPE, "4", "季统计"),


    JTT_1291_2019_6_3_2_5_3_1(DictTypeEnums.JTT_1291_2019_6_3_2_5_3, "1", "企业法人"),
    JTT_1291_2019_6_3_2_5_3_2(DictTypeEnums.JTT_1291_2019_6_3_2_5_3, "2", "分支机构"),

    JTT_1291_2019_6_4_1_000118003000(DictTypeEnums.JTT_1291_2019_6_4_1, "000118003000", "道路旅客运输经营许可"),
    JTT_1291_2019_6_4_1_000118017000(DictTypeEnums.JTT_1291_2019_6_4_1, "000118017000", "道路货运经营许可"),
    JTT_1291_2019_6_4_1_000118036000(DictTypeEnums.JTT_1291_2019_6_4_1, "000118036000", "放射性物品道路运输经营许可"),
    JTT_1291_2019_6_4_1_000118037000(DictTypeEnums.JTT_1291_2019_6_4_1, "000118037000", "危险货物运输经营许可"),
    JTT_1291_2019_6_4_1_000118081000(DictTypeEnums.JTT_1291_2019_6_4_1, "000118081000", "县内客运业户开业、增项经营许可"),
    JTT_1291_2019_6_4_1_000118022000(DictTypeEnums.JTT_1291_2019_6_4_1, "000118022000", "出租汽车经营许可"),

    GAT_16_4_K10(DictTypeEnums.GAT_16_4, "K10", "大型客车"),
    GAT_16_4_K11(DictTypeEnums.GAT_16_4, "K11", "大型普通客车"),
    GAT_16_4_K12(DictTypeEnums.GAT_16_4, "K12", "大型双层客车"),
    GAT_16_4_K13(DictTypeEnums.GAT_16_4, "K13", "大型卧铺客车"),
    GAT_16_4_K14(DictTypeEnums.GAT_16_4, "K14", "大型铰接客车"),
    GAT_16_4_K15(DictTypeEnums.GAT_16_4, "K15", "大型越野客车"),
    GAT_16_4_K16(DictTypeEnums.GAT_16_4, "K16", "大型轿车"),
    GAT_16_4_K17(DictTypeEnums.GAT_16_4, "K17", "大型专用客车"),
    GAT_16_4_K20(DictTypeEnums.GAT_16_4, "K20", "中型客车"),
    GAT_16_4_K21(DictTypeEnums.GAT_16_4, "K21", "中型普通客车"),
    GAT_16_4_K22(DictTypeEnums.GAT_16_4, "K22", "中型双层客车"),
    GAT_16_4_K23(DictTypeEnums.GAT_16_4, "K23", "中型卧铺客车"),
    GAT_16_4_K24(DictTypeEnums.GAT_16_4, "K24", "中型铰接客车"),
    GAT_16_4_K25(DictTypeEnums.GAT_16_4, "K25", "中型越野客车"),
    GAT_16_4_K27(DictTypeEnums.GAT_16_4, "K27", "中型专用客车"),
    GAT_16_4_K30(DictTypeEnums.GAT_16_4, "K30", "小型客车"),
    GAT_16_4_K31(DictTypeEnums.GAT_16_4, "K31", "小型普通客车"),
    GAT_16_4_K32(DictTypeEnums.GAT_16_4, "K32", "小型越野客车"),
    GAT_16_4_K33(DictTypeEnums.GAT_16_4, "K33", "小型轿车"),
    GAT_16_4_K34(DictTypeEnums.GAT_16_4, "K34", "小型专用客车"),
    GAT_16_4_K40(DictTypeEnums.GAT_16_4, "K40", "微型客车"),
    GAT_16_4_K41(DictTypeEnums.GAT_16_4, "K41", "微型普通客车"),
    GAT_16_4_K42(DictTypeEnums.GAT_16_4, "K42", "微型越野客车"),
    GAT_16_4_K43(DictTypeEnums.GAT_16_4, "K43", "微型轿车"),
    GAT_16_4_H10(DictTypeEnums.GAT_16_4, "H10", "重型货车"),
    GAT_16_4_H11(DictTypeEnums.GAT_16_4, "H11", "重型普通货车"),
    GAT_16_4_H12(DictTypeEnums.GAT_16_4, "H12", "重型厢式货车"),
    GAT_16_4_H13(DictTypeEnums.GAT_16_4, "H13", "重型封闭货车"),
    GAT_16_4_H14(DictTypeEnums.GAT_16_4, "H14", "重型罐式货车"),
    GAT_16_4_H15(DictTypeEnums.GAT_16_4, "H15", "重型平板货车"),
    GAT_16_4_H16(DictTypeEnums.GAT_16_4, "H16", "重型集装箱车"),
    GAT_16_4_H17(DictTypeEnums.GAT_16_4, "H17", "重型自卸货车"),
    GAT_16_4_H18(DictTypeEnums.GAT_16_4, "H18", "重型特殊结构货车"),
    GAT_16_4_H19(DictTypeEnums.GAT_16_4, "H19", "重型仓栅式货车"),
    GAT_16_4_H20(DictTypeEnums.GAT_16_4, "H20", "中型货车"),
    GAT_16_4_H21(DictTypeEnums.GAT_16_4, "H21", "中型普通货车"),
    GAT_16_4_H22(DictTypeEnums.GAT_16_4, "H22", "中型厢式货车"),
    GAT_16_4_H23(DictTypeEnums.GAT_16_4, "H23", "中型封闭货车"),
    GAT_16_4_H24(DictTypeEnums.GAT_16_4, "H24", "中型罐式货车"),
    GAT_16_4_H25(DictTypeEnums.GAT_16_4, "H25", "中型平板货车"),
    GAT_16_4_H26(DictTypeEnums.GAT_16_4, "H26", "中型集装箱车"),
    GAT_16_4_H27(DictTypeEnums.GAT_16_4, "H27", "中型自卸货车"),
    GAT_16_4_H28(DictTypeEnums.GAT_16_4, "H28", "中型特殊结构货车"),
    GAT_16_4_H29(DictTypeEnums.GAT_16_4, "H29", "中型仓栅式货车"),
    GAT_16_4_H30(DictTypeEnums.GAT_16_4, "H30", "轻型货车"),
    GAT_16_4_H31(DictTypeEnums.GAT_16_4, "H31", "轻型普通货车"),
    GAT_16_4_H32(DictTypeEnums.GAT_16_4, "H32", "轻型厢式货车"),
    GAT_16_4_H33(DictTypeEnums.GAT_16_4, "H33", "轻型封闭货车"),
    GAT_16_4_H34(DictTypeEnums.GAT_16_4, "H34", "轻型罐式货车"),
    GAT_16_4_H35(DictTypeEnums.GAT_16_4, "H35", "轻型平板货车"),
    GAT_16_4_H37(DictTypeEnums.GAT_16_4, "H37", "轻型自卸货车"),
    GAT_16_4_H38(DictTypeEnums.GAT_16_4, "H38", "轻型特殊结构货车"),
    GAT_16_4_H39(DictTypeEnums.GAT_16_4, "H39", "轻型仓栅式货车"),
    GAT_16_4_H40(DictTypeEnums.GAT_16_4, "H40", "微型货车"),
    GAT_16_4_H41(DictTypeEnums.GAT_16_4, "H41", "微型普通货车"),
    GAT_16_4_H42(DictTypeEnums.GAT_16_4, "H42", "微型厢式货车"),
    GAT_16_4_H43(DictTypeEnums.GAT_16_4, "H43", "微型封闭货车"),
    GAT_16_4_H44(DictTypeEnums.GAT_16_4, "H44", "微型罐式货车"),
    GAT_16_4_H45(DictTypeEnums.GAT_16_4, "H45", "微型自卸货车"),
    GAT_16_4_H46(DictTypeEnums.GAT_16_4, "H46", "微型特殊结构货车"),
    GAT_16_4_H47(DictTypeEnums.GAT_16_4, "H47", "微型仓栅式货车"),
    GAT_16_4_H50(DictTypeEnums.GAT_16_4, "H50", "低速货车"),
    GAT_16_4_H51(DictTypeEnums.GAT_16_4, "H51", "普通低速货车"),
    GAT_16_4_H52(DictTypeEnums.GAT_16_4, "H52", "厢式低速货车"),
    GAT_16_4_H53(DictTypeEnums.GAT_16_4, "H53", "罐式低速货车"),
    GAT_16_4_H54(DictTypeEnums.GAT_16_4, "H54", "自卸低速货车"),
    GAT_16_4_H55(DictTypeEnums.GAT_16_4, "H55", "仓栅式低速货车"),
    GAT_16_4_Q10(DictTypeEnums.GAT_16_4, "Q10", "重型牵引车"),
    GAT_16_4_Q11(DictTypeEnums.GAT_16_4, "Q11", "重型半挂牵引车"),
    GAT_16_4_Q12(DictTypeEnums.GAT_16_4, "Q12", "重型全挂牵引车"),
    GAT_16_4_Q20(DictTypeEnums.GAT_16_4, "Q20", "中型牵引车"),
    GAT_16_4_Q21(DictTypeEnums.GAT_16_4, "Q21", "中型半挂牵引车"),
    GAT_16_4_Q22(DictTypeEnums.GAT_16_4, "Q22", "中型全挂牵引车"),
    GAT_16_4_Q30(DictTypeEnums.GAT_16_4, "Q30", "轻型牵引车"),
    GAT_16_4_Q31(DictTypeEnums.GAT_16_4, "Q31", "轻型半挂牵引车"),
    GAT_16_4_Q32(DictTypeEnums.GAT_16_4, "Q32", "轻型全挂牵引车"),
    GAT_16_4_Z11(DictTypeEnums.GAT_16_4, "Z11", "大型专项作业车"),
    GAT_16_4_Z21(DictTypeEnums.GAT_16_4, "Z21", "中型专项作业车"),
    GAT_16_4_Z31(DictTypeEnums.GAT_16_4, "Z31", "小型专项作业车"),
    GAT_16_4_Z41(DictTypeEnums.GAT_16_4, "Z41", "微型专项作业车"),
    GAT_16_4_Z51(DictTypeEnums.GAT_16_4, "Z51", "重型专项作业车"),
    GAT_16_4_Z71(DictTypeEnums.GAT_16_4, "Z71", "轻型专项作业车"),
    GAT_16_4_D11(DictTypeEnums.GAT_16_4, "D11", "无轨电车"),
    GAT_16_4_D12(DictTypeEnums.GAT_16_4, "D12", "有轨电车"),
    GAT_16_4_M10(DictTypeEnums.GAT_16_4, "M10", "三轮摩托车"),
    GAT_16_4_M11(DictTypeEnums.GAT_16_4, "M11", "普通正三轮摩托车"),
    GAT_16_4_M12(DictTypeEnums.GAT_16_4, "M12", "轻便正三轮摩托车"),
    GAT_16_4_M13(DictTypeEnums.GAT_16_4, "M13", "正三轮载客摩托车"),
    GAT_16_4_M14(DictTypeEnums.GAT_16_4, "M14", "正三轮载货摩托车"),
    GAT_16_4_M15(DictTypeEnums.GAT_16_4, "M15", "侧三轮摩托车"),
    GAT_16_4_M20(DictTypeEnums.GAT_16_4, "M20", "二轮摩托车"),
    GAT_16_4_M21(DictTypeEnums.GAT_16_4, "M21", "普通二轮摩托车"),
    GAT_16_4_M22(DictTypeEnums.GAT_16_4, "M22", "轻便二轮摩托车"),
    GAT_16_4_N11(DictTypeEnums.GAT_16_4, "N11", "三轮汽车"),
    GAT_16_4_T11(DictTypeEnums.GAT_16_4, "T11", "大型轮式拖拉机"),
    GAT_16_4_T20(DictTypeEnums.GAT_16_4, "T20", "小型拖拉机"),
    GAT_16_4_T21(DictTypeEnums.GAT_16_4, "T21", "小型轮式拖拉机"),
    GAT_16_4_T22(DictTypeEnums.GAT_16_4, "T22", "手扶拖拉机"),
    GAT_16_4_T23(DictTypeEnums.GAT_16_4, "T23", "手扶变形运输机"),
    GAT_16_4_J11(DictTypeEnums.GAT_16_4, "J11", "轮式装载机械"),
    GAT_16_4_J12(DictTypeEnums.GAT_16_4, "J12", "轮式挖掘机械"),
    GAT_16_4_J13(DictTypeEnums.GAT_16_4, "J13", "轮式平地机械"),
    GAT_16_4_G10(DictTypeEnums.GAT_16_4, "G10", "重型全挂车"),
    GAT_16_4_G11(DictTypeEnums.GAT_16_4, "G11", "重型普通全挂车"),
    GAT_16_4_G12(DictTypeEnums.GAT_16_4, "G12", "重型厢式全挂车"),
    GAT_16_4_G13(DictTypeEnums.GAT_16_4, "G13", "重型罐式全挂车"),
    GAT_16_4_G14(DictTypeEnums.GAT_16_4, "G14", "重型平板全挂车"),
    GAT_16_4_G15(DictTypeEnums.GAT_16_4, "G15", "重型集装箱全挂车"),
    GAT_16_4_G16(DictTypeEnums.GAT_16_4, "G16", "重型自卸全挂车"),
    GAT_16_4_G17(DictTypeEnums.GAT_16_4, "G17", "重型仓栅式全挂车"),
    GAT_16_4_G18(DictTypeEnums.GAT_16_4, "G18", "重型旅居全挂车"),
    GAT_16_4_G19(DictTypeEnums.GAT_16_4, "G19", "重型专项作业全挂车"),
    GAT_16_4_G20(DictTypeEnums.GAT_16_4, "G20", "中型全挂车"),
    GAT_16_4_G21(DictTypeEnums.GAT_16_4, "G21", "中型普通全挂车"),
    GAT_16_4_G22(DictTypeEnums.GAT_16_4, "G22", "中型厢式全挂车"),
    GAT_16_4_G23(DictTypeEnums.GAT_16_4, "G23", "中型罐式全挂车"),
    GAT_16_4_G24(DictTypeEnums.GAT_16_4, "G24", "中型平板全挂车"),
    GAT_16_4_G25(DictTypeEnums.GAT_16_4, "G25", "中型集装箱全挂车"),
    GAT_16_4_G26(DictTypeEnums.GAT_16_4, "G26", "中型自卸全挂车"),
    GAT_16_4_G27(DictTypeEnums.GAT_16_4, "G27", "中型仓栅式全挂车"),
    GAT_16_4_G28(DictTypeEnums.GAT_16_4, "G28", "中型旅居全挂车"),
    GAT_16_4_G29(DictTypeEnums.GAT_16_4, "G29", "中型专项作业全挂车"),
    GAT_16_4_G30(DictTypeEnums.GAT_16_4, "G30", "轻型全挂车"),
    GAT_16_4_G31(DictTypeEnums.GAT_16_4, "G31", "轻型普通全挂车"),
    GAT_16_4_G32(DictTypeEnums.GAT_16_4, "G32", "轻型厢式全挂车"),
    GAT_16_4_G33(DictTypeEnums.GAT_16_4, "G33", "轻型罐式全挂车"),
    GAT_16_4_G34(DictTypeEnums.GAT_16_4, "G34", "轻型平板全挂车"),
    GAT_16_4_G35(DictTypeEnums.GAT_16_4, "G35", "轻型自卸全挂车"),
    GAT_16_4_G36(DictTypeEnums.GAT_16_4, "G36", "轻型仓栅式全挂车"),
    GAT_16_4_G37(DictTypeEnums.GAT_16_4, "G37", "轻型旅居全挂车"),
    GAT_16_4_G38(DictTypeEnums.GAT_16_4, "G38", "轻型专项作业全挂车"),
    GAT_16_4_B10(DictTypeEnums.GAT_16_4, "B10", "重型半挂车"),
    GAT_16_4_B11(DictTypeEnums.GAT_16_4, "B11", "重型普通半挂车"),
    GAT_16_4_B12(DictTypeEnums.GAT_16_4, "B12", "重型厢式半挂车"),
    GAT_16_4_B13(DictTypeEnums.GAT_16_4, "B13", "重型罐式半挂车"),
    GAT_16_4_B14(DictTypeEnums.GAT_16_4, "B14", "重型平板半挂车"),
    GAT_16_4_B15(DictTypeEnums.GAT_16_4, "B15", "重型集装箱半挂车"),
    GAT_16_4_B16(DictTypeEnums.GAT_16_4, "B16", "重型自卸半挂车"),
    GAT_16_4_B17(DictTypeEnums.GAT_16_4, "B17", "重型特殊结构半挂车"),
    GAT_16_4_B18(DictTypeEnums.GAT_16_4, "B18", "重型仓栅式半挂车"),
    GAT_16_4_B19(DictTypeEnums.GAT_16_4, "B19", "重型旅居半挂车"),
    GAT_16_4_B1A(DictTypeEnums.GAT_16_4, "B1A", "重型专项作业半挂车"),
    GAT_16_4_B1B(DictTypeEnums.GAT_16_4, "B1B", "重型低平板半挂车"),
    GAT_16_4_B20(DictTypeEnums.GAT_16_4, "B20", "中型半挂车"),
    GAT_16_4_B21(DictTypeEnums.GAT_16_4, "B21", "中型普通半挂车"),
    GAT_16_4_B22(DictTypeEnums.GAT_16_4, "B22", "中型厢式半挂车"),
    GAT_16_4_B23(DictTypeEnums.GAT_16_4, "B23", "中型罐式半挂车"),
    GAT_16_4_B24(DictTypeEnums.GAT_16_4, "B24", "中型平板半挂车"),
    GAT_16_4_B25(DictTypeEnums.GAT_16_4, "B25", "中型集装箱半挂车"),
    GAT_16_4_B26(DictTypeEnums.GAT_16_4, "B26", "中型自卸半挂车"),
    GAT_16_4_B27(DictTypeEnums.GAT_16_4, "B27", "中型特殊结构半挂车"),
    GAT_16_4_B28(DictTypeEnums.GAT_16_4, "B28", "中型仓栅式半挂车"),
    GAT_16_4_B29(DictTypeEnums.GAT_16_4, "B29", "中型旅居半挂车"),
    GAT_16_4_B2A(DictTypeEnums.GAT_16_4, "B2A", "中型专项作业半挂车"),
    GAT_16_4_B2B(DictTypeEnums.GAT_16_4, "B2B", "中型低平板半挂车"),
    GAT_16_4_B30(DictTypeEnums.GAT_16_4, "B30", "轻型半挂车"),
    GAT_16_4_B31(DictTypeEnums.GAT_16_4, "B31", "轻型普通半挂车"),
    GAT_16_4_B32(DictTypeEnums.GAT_16_4, "B32", "轻型厢式半挂车"),
    GAT_16_4_B33(DictTypeEnums.GAT_16_4, "B33", "轻型罐式半挂车"),
    GAT_16_4_B34(DictTypeEnums.GAT_16_4, "B34", "轻型平板半挂车"),
    GAT_16_4_B35(DictTypeEnums.GAT_16_4, "B35", "轻型自卸半挂车"),
    GAT_16_4_B36(DictTypeEnums.GAT_16_4, "B36", "轻型仓栅式半挂车"),
    GAT_16_4_B37(DictTypeEnums.GAT_16_4, "B37", "轻型旅居半挂车"),
    GAT_16_4_B38(DictTypeEnums.GAT_16_4, "B38", "轻型专项作业半挂车"),
    GAT_16_4_B39(DictTypeEnums.GAT_16_4, "B39", "轻型低平板半挂车"),
    GAT_16_4_X99(DictTypeEnums.GAT_16_4, "X99", "其他"),


    OVERRUN_TRUCK_TYPE_1(DictTypeEnums.OVERRUN_TRUCK_TYPE, "1", "载货汽车(18)"),
    OVERRUN_TRUCK_TYPE_2(DictTypeEnums.OVERRUN_TRUCK_TYPE, "2", "中置轴挂车列车_主车2轴挂车1轴(27)"),
    OVERRUN_TRUCK_TYPE_3(DictTypeEnums.OVERRUN_TRUCK_TYPE, "3", "铰接列车_主车2轴挂车1轴(27)"),
    OVERRUN_TRUCK_TYPE_4(DictTypeEnums.OVERRUN_TRUCK_TYPE, "4", "载货汽车(25)"),
    OVERRUN_TRUCK_TYPE_5(DictTypeEnums.OVERRUN_TRUCK_TYPE, "5", "中置轴挂车列车_主车2轴挂车2轴(36)"),
    OVERRUN_TRUCK_TYPE_6(DictTypeEnums.OVERRUN_TRUCK_TYPE, "6", "中置轴挂车列车_主车3轴挂车1轴(35)"),
    OVERRUN_TRUCK_TYPE_7(DictTypeEnums.OVERRUN_TRUCK_TYPE, "7", "铰接列车_主车2轴挂车2轴(36)"),
    OVERRUN_TRUCK_TYPE_8(DictTypeEnums.OVERRUN_TRUCK_TYPE, "8", "全挂汽车列车_主车2轴挂车2轴(36)"),
    OVERRUN_TRUCK_TYPE_9(DictTypeEnums.OVERRUN_TRUCK_TYPE, "9", "载货汽车(31)"),
    OVERRUN_TRUCK_TYPE_10(DictTypeEnums.OVERRUN_TRUCK_TYPE, "10", "中置轴挂车列车_主车3轴挂车2轴(43)"),
    OVERRUN_TRUCK_TYPE_11(DictTypeEnums.OVERRUN_TRUCK_TYPE, "11", "铰接列车_主车3轴挂车2轴(43)"),
    OVERRUN_TRUCK_TYPE_12(DictTypeEnums.OVERRUN_TRUCK_TYPE, "12", "铰接列车_主车2轴挂车3轴(42)"),
    OVERRUN_TRUCK_TYPE_13(DictTypeEnums.OVERRUN_TRUCK_TYPE, "13", "全挂汽车列车_主车3轴挂车2轴(43)"),
    OVERRUN_TRUCK_TYPE_14(DictTypeEnums.OVERRUN_TRUCK_TYPE, "14", "中置轴挂车列车_主车3轴挂车3轴(49)"),
    OVERRUN_TRUCK_TYPE_15(DictTypeEnums.OVERRUN_TRUCK_TYPE, "15", "中置轴挂车列车_主车3轴挂车3轴(46)"),
    OVERRUN_TRUCK_TYPE_16(DictTypeEnums.OVERRUN_TRUCK_TYPE, "16", "中置轴挂车列车_主车4轴挂车2轴(49)"),
    OVERRUN_TRUCK_TYPE_17(DictTypeEnums.OVERRUN_TRUCK_TYPE, "17", "中置轴挂车列车_主车4轴挂车2轴(46)"),
    OVERRUN_TRUCK_TYPE_18(DictTypeEnums.OVERRUN_TRUCK_TYPE, "18", "铰接列车_主车3轴挂车3轴(49)"),
    OVERRUN_TRUCK_TYPE_19(DictTypeEnums.OVERRUN_TRUCK_TYPE, "19", "铰接列车_主车3轴挂车3轴(46)"),
    OVERRUN_TRUCK_TYPE_20(DictTypeEnums.OVERRUN_TRUCK_TYPE, "20", "全挂列车_主车4轴挂车2轴(49)"),
    OVERRUN_TRUCK_TYPE_21(DictTypeEnums.OVERRUN_TRUCK_TYPE, "21", "全挂列车_主车4轴挂车2轴(46)"),

    OVERRUN_WORK_TYPE_01001(DictTypeEnums.OVERRUN_WORK_TYPE, "01001", "经营性道路旅客运输驾驶员"),
    OVERRUN_WORK_TYPE_02001(DictTypeEnums.OVERRUN_WORK_TYPE, "02001", "经营性道路货物运输驾驶员"),
    OVERRUN_WORK_TYPE_03001(DictTypeEnums.OVERRUN_WORK_TYPE, "03001", "道路危险货物运输驾驶员"),
    OVERRUN_WORK_TYPE_03002(DictTypeEnums.OVERRUN_WORK_TYPE, "03002", "道路危险货物运输装卸管理人员"),
    OVERRUN_WORK_TYPE_03003(DictTypeEnums.OVERRUN_WORK_TYPE, "03003", "道路危险货物运输押运人员"),
    OVERRUN_WORK_TYPE_03004(DictTypeEnums.OVERRUN_WORK_TYPE, "03004", "道路危险货物运输专职安全管理人员"),
    OVERRUN_WORK_TYPE_03005(DictTypeEnums.OVERRUN_WORK_TYPE, "03005", "剧毒化学品道路运输驾驶员"),
    OVERRUN_WORK_TYPE_03006(DictTypeEnums.OVERRUN_WORK_TYPE, "03006", "剧毒化学品道路运输装卸管理人员"),
    OVERRUN_WORK_TYPE_03007(DictTypeEnums.OVERRUN_WORK_TYPE, "03007", "剧毒化学品道路运输押运人员"),
    OVERRUN_WORK_TYPE_03008(DictTypeEnums.OVERRUN_WORK_TYPE, "03008", "剧毒化学品道路运输专职安全管理人员"),
    OVERRUN_WORK_TYPE_03009(DictTypeEnums.OVERRUN_WORK_TYPE, "03009", "爆炸品道路运输驾驶员"),
    OVERRUN_WORK_TYPE_03010(DictTypeEnums.OVERRUN_WORK_TYPE, "03010", "爆炸品道路运输装卸管理人员"),
    OVERRUN_WORK_TYPE_03011(DictTypeEnums.OVERRUN_WORK_TYPE, "03011", "爆炸品道路运输押运人员"),
    OVERRUN_WORK_TYPE_03012(DictTypeEnums.OVERRUN_WORK_TYPE, "03012", "爆炸品道路运输专职安全管理人员"),
    OVERRUN_WORK_TYPE_03013(DictTypeEnums.OVERRUN_WORK_TYPE, "03013", "放射性物品道路运输驾驶员"),
    OVERRUN_WORK_TYPE_03014(DictTypeEnums.OVERRUN_WORK_TYPE, "03014", "放射性物品道路运输装卸管理人员"),
    OVERRUN_WORK_TYPE_03015(DictTypeEnums.OVERRUN_WORK_TYPE, "03015", "放射性物品道路运输押运人员"),
    OVERRUN_WORK_TYPE_03016(DictTypeEnums.OVERRUN_WORK_TYPE, "03016", "放射性物品道路运输专职安全管理人员"),

    OVERRUN_SITE_TYPE_11(DictTypeEnums.OVERRUN_SITE_TYPE, "11", "I类公路超限检测站检测系统"),
    OVERRUN_SITE_TYPE_12(DictTypeEnums.OVERRUN_SITE_TYPE, "12", "II类公路超限检测站检测系统"),
    OVERRUN_SITE_TYPE_21(DictTypeEnums.OVERRUN_SITE_TYPE, "21", "超限检测点"),
    OVERRUN_SITE_TYPE_31(DictTypeEnums.OVERRUN_SITE_TYPE, "31", "公路车辆动态检测点"),
    OVERRUN_SITE_TYPE_41(DictTypeEnums.OVERRUN_SITE_TYPE, "41", "高速公路入口检测"),
    OVERRUN_SITE_TYPE_42(DictTypeEnums.OVERRUN_SITE_TYPE, "42", "高速公路出口检测"),
    OVERRUN_SITE_TYPE_51(DictTypeEnums.OVERRUN_SITE_TYPE, "51", "源头监管站"),

    OVERRUN_CAR_TYPE_A1(DictTypeEnums.OVERRUN_CAR_TYPE, "A1", "A1"),
    OVERRUN_CAR_TYPE_A2(DictTypeEnums.OVERRUN_CAR_TYPE, "A2", "A2"),
    OVERRUN_CAR_TYPE_A3(DictTypeEnums.OVERRUN_CAR_TYPE, "A3", "A3"),
    OVERRUN_CAR_TYPE_B1(DictTypeEnums.OVERRUN_CAR_TYPE, "B1", "B1"),
    OVERRUN_CAR_TYPE_B2(DictTypeEnums.OVERRUN_CAR_TYPE, "B2", "B2"),
    OVERRUN_CAR_TYPE_C1(DictTypeEnums.OVERRUN_CAR_TYPE, "C1", "C1"),
    OVERRUN_CAR_TYPE_C2(DictTypeEnums.OVERRUN_CAR_TYPE, "C2", "C2"),
    OVERRUN_CAR_TYPE_C3(DictTypeEnums.OVERRUN_CAR_TYPE, "C3", "C3"),
    OVERRUN_CAR_TYPE_C4(DictTypeEnums.OVERRUN_CAR_TYPE, "C4", "C4"),
    OVERRUN_CAR_TYPE_D(DictTypeEnums.OVERRUN_CAR_TYPE, "D", "D"),
    OVERRUN_CAR_TYPE_E(DictTypeEnums.OVERRUN_CAR_TYPE, "E", "E"),
    OVERRUN_CAR_TYPE_F(DictTypeEnums.OVERRUN_CAR_TYPE, "F", "F"),
    OVERRUN_CAR_TYPE_M(DictTypeEnums.OVERRUN_CAR_TYPE, "M", "M"),
    OVERRUN_CAR_TYPE_N(DictTypeEnums.OVERRUN_CAR_TYPE, "N", "N"),
    OVERRUN_CAR_TYPE_P(DictTypeEnums.OVERRUN_CAR_TYPE, "P", "P"),
//    OVERRUN_PROCESS_TYPE_1(DictTypeEnums.OVERRUN_PROCESS_TYPE,"1","交通-复核"),
//    OVERRUN_PROCESS_TYPE_2(DictTypeEnums.OVERRUN_PROCESS_TYPE,"2","交通-确认"),
//    OVERRUN_PROCESS_TYPE_3(DictTypeEnums.OVERRUN_PROCESS_TYPE,"3","交通-移交"),
//    OVERRUN_PROCESS_TYPE_4(DictTypeEnums.OVERRUN_PROCESS_TYPE,"4","交通-处罚"),
//    OVERRUN_PROCESS_TYPE_5(DictTypeEnums.OVERRUN_PROCESS_TYPE,"5","交通-抄告"),
//    OVERRUN_PROCESS_TYPE_6(DictTypeEnums.OVERRUN_PROCESS_TYPE,"6","交通-反馈"),
//    OVERRUN_PROCESS_TYPE_7(DictTypeEnums.OVERRUN_PROCESS_TYPE,"7","交通-结案"),
//    OVERRUN_PROCESS_TYPE_11(DictTypeEnums.OVERRUN_PROCESS_TYPE,"11","公安-流转"),
//    OVERRUN_PROCESS_TYPE_12(DictTypeEnums.OVERRUN_PROCESS_TYPE,"12","公安-确认"),
//    OVERRUN_PROCESS_TYPE_13(DictTypeEnums.OVERRUN_PROCESS_TYPE,"13","公安-抄告"),
//    OVERRUN_PROCESS_TYPE_14(DictTypeEnums.OVERRUN_PROCESS_TYPE,"14","公安-反馈"),
//    OVERRUN_PROCESS_TYPE_15(DictTypeEnums.OVERRUN_PROCESS_TYPE,"15","公安-结案"),

    OVERRUN_PROCESS_TYPE_10(DictTypeEnums.OVERRUN_PROCESS_TYPE, "10", "交通-确认"),
    OVERRUN_PROCESS_TYPE_20(DictTypeEnums.OVERRUN_PROCESS_TYPE, "20", "交通-移交"),
    OVERRUN_PROCESS_TYPE_30(DictTypeEnums.OVERRUN_PROCESS_TYPE, "30", "交通-抄告"),
    OVERRUN_PROCESS_TYPE_40(DictTypeEnums.OVERRUN_PROCESS_TYPE, "40", "交通-反馈"),
    OVERRUN_PROCESS_TYPE_50(DictTypeEnums.OVERRUN_PROCESS_TYPE, "50", "交通-结案"),
    //    OVERRUN_PROCESS_TYPE_110(DictTypeEnums.OVERRUN_PROCESS_TYPE,"110","公安-处罚"),
    OVERRUN_PROCESS_TYPE_210(DictTypeEnums.OVERRUN_PROCESS_TYPE, "210", "公安-确认"),
    OVERRUN_PROCESS_TYPE_220(DictTypeEnums.OVERRUN_PROCESS_TYPE, "220", "公安-抄告"),
    OVERRUN_PROCESS_TYPE_230(DictTypeEnums.OVERRUN_PROCESS_TYPE, "230", "公安-反馈"),
    OVERRUN_PROCESS_TYPE_240(DictTypeEnums.OVERRUN_PROCESS_TYPE, "240", "公安-结案"),
    OVERRUN_PROCESS_TYPE_310(DictTypeEnums.OVERRUN_PROCESS_TYPE, "310", "道安办-抄告"),
    OVERRUN_PROCESS_TYPE_320(DictTypeEnums.OVERRUN_PROCESS_TYPE, "320", "道安办-反馈"),
    OVERRUN_PROCESS_TYPE_330(DictTypeEnums.OVERRUN_PROCESS_TYPE, "330", "道安办-结案"),

    OVERRUN_MSG_TYPE_OVERDUE(DictTypeEnums.OVERRUN_MSG_TYPE, "OVERRUN_OVERDUE", "超期短信"),
    OVERRUN_MSG_TYPE_WARNING(DictTypeEnums.OVERRUN_MSG_TYPE, "OVERRUN_WARNING", "预警短信"),
    OVERRUN_MSG_TYPE_DELIVER(DictTypeEnums.OVERRUN_MSG_TYPE, "OVERRUN_DELIVER", "抄告短信"),


    ALL_CERT_STATUS_ZERO(DictTypeEnums.ALL_CERT_STATUS, "0", "生成失败"),
    ALL_CERT_STATUS_ONE(DictTypeEnums.ALL_CERT_STATUS, "1", "签章失败"),
    ALL_CERT_STATUS_TWO(DictTypeEnums.ALL_CERT_STATUS, "2", "排队中"),
    ALL_CERT_STATUS_THREE(DictTypeEnums.ALL_CERT_STATUS, "3", "已生成并签章（三类九证）"),
    ALL_CERT_STATUS_FOUR(DictTypeEnums.ALL_CERT_STATUS, "4", "注销"),
    ALL_CERT_STATUS_FIVE(DictTypeEnums.ALL_CERT_STATUS, "5", "成功（非三类九证）"),

    ALL_CERT_TYPE_6(DictTypeEnums.ALL_CERT_TYPE, "F9E02C91FBA646C7A6C537EDC7669E4E", "道路运输从业人员从业资格证"),
    ALL_CERT_TYPE_8(DictTypeEnums.ALL_CERT_TYPE, "0808C9021A38499197D5ECC917886084", "网络预约出租汽车驾驶员证"),
    ALL_CERT_TYPE_7(DictTypeEnums.ALL_CERT_TYPE, "DF080274C01F4781A1E7EA29D3249509", "巡游出租汽车驾驶员证"),
    ALL_CERT_TYPE_4(DictTypeEnums.ALL_CERT_TYPE, "71124074089E440BBE8C131A26E7088F", "中华人民共和国道路运输证"),
    ALL_CERT_TYPE_5(DictTypeEnums.ALL_CERT_TYPE, "C0C78F6CC9464D92B80018770FB87842", "网络预约出租汽车运输证"),
    ALL_CERT_TYPE_0(DictTypeEnums.ALL_CERT_TYPE, "866CDFC091CD4529A87771E558BA7E5A", "中华人民共和国道路运输经营许可证"),
    ALL_CERT_TYPE_2(DictTypeEnums.ALL_CERT_TYPE, "57CC896E44D34A0A9F112FFBBD14F65A", "网络预约出租汽车经营许可证"),
    ALL_CERT_TYPE_1(DictTypeEnums.ALL_CERT_TYPE, "D2C34C63E3564340980F0C87535CBC85", "中华人民共和国道路危险货物运输许可证"),
    ALL_CERT_TYPE_3(DictTypeEnums.ALL_CERT_TYPE, "5BCD24DC10A141BF8DFE8CF52899CD25", "放射性物品道路运输许可证"),
    ALL_CERT_TYPE_12(DictTypeEnums.ALL_CERT_TYPE, "DF686B22ABEC4CE68EBDE69C34C467CF", "超限运输车辆通行证"),
    ALL_CERT_TYPE_11(DictTypeEnums.ALL_CERT_TYPE, "B22207F65DBC4E228ED768EB78822E98", "公路养护作业单位资质证书"),
    ALL_CERT_TYPE_10(DictTypeEnums.ALL_CERT_TYPE, "2308A506D08C43AFABA53B5E911FDE50", "公路水运工程监理企业资质证书"),
    ALL_CERT_TYPE_9(DictTypeEnums.ALL_CERT_TYPE, "2E466E6C5DE949AB9F31CFE61D0F7657", "公路水运工程质量检测机构资质证书"),
    ALL_CERT_TYPE_13(DictTypeEnums.ALL_CERT_TYPE, "D1DA743959D44EB9B5A4C5D05B646176", "中华人民共和国港口经营许可证"),
    ALL_CERT_TYPE_14(DictTypeEnums.ALL_CERT_TYPE, "CCD8726A1CD64888B1D7C612B5709BEC", "船舶营业运输证注销登记证明书"),
    ALL_CERT_TYPE_15(DictTypeEnums.ALL_CERT_TYPE, "1377BB2EAAD64EA3A0A9B414997D78C0", "国内水路运输经营许可证"),
    ALL_CERT_TYPE_16(DictTypeEnums.ALL_CERT_TYPE, "35E16806D2F4450884C247457BA2B49F", "国内船舶管理业务经营许可证"),
    ALL_CERT_TYPE_17(DictTypeEnums.ALL_CERT_TYPE, "2A2930737804432BA267A7E18D077529", "水上水下作业和活动许可证"),
    ALL_CERT_TYPE_18(DictTypeEnums.ALL_CERT_TYPE, "54940FE8F7454F9184569C86D9D8A2B1", "港口危险货物作业附证"),
    ALL_CERT_TYPE_19(DictTypeEnums.ALL_CERT_TYPE, "27108A084B894E1C8EA4FB059879D8A9", "危险化学品水路运输从业资格证书（申报员）"),
    ALL_CERT_TYPE_20(DictTypeEnums.ALL_CERT_TYPE, "8BE4DD8923D04E7AB426EE9CEB62A87C", "危险化学品水路运输从业资格证书（装卸管理员）"),
    ALL_CERT_TYPE_21(DictTypeEnums.ALL_CERT_TYPE, "D07348516006450882043351C85D29EA", "危险化学品水路运输从业资格证书（检查员）"),
    ALL_CERT_TYPE_22(DictTypeEnums.ALL_CERT_TYPE, "8983007FC8BD48D98920AC7BB5153F26", "船舶营业运输证"),
    ALL_CERT_TYPE_23(DictTypeEnums.ALL_CERT_TYPE, "786C5B0B41E6430099C8ABDD1E258648", "超限运输车辆通行证(跨省)"),
    ALL_CERT_TYPE_24(DictTypeEnums.ALL_CERT_TYPE, "E3E4EF56DD644F88A7D799640DD42AFB", "中华人民共和国二级造价工程师注册证书"),


    AREA_LEVEL_ZERO(DictTypeEnums.AREA_LEVEL, "country", "国家"),
    AREA_LEVEL_ONE(DictTypeEnums.AREA_LEVEL, "province", "省份"),
    AREA_LEVEL_TWO(DictTypeEnums.AREA_LEVEL, "city", "市级"),
    AREA_LEVEL_THREE(DictTypeEnums.AREA_LEVEL, "district", "区/县"),
    AREA_LEVEL_FOUR(DictTypeEnums.AREA_LEVEL, "street", "街道"),

    CERT_BIG_TYPE_ZERO(DictTypeEnums.CERT_BIG_TYPE, "ROAD_TRANSPORT", "道路运输类"),
    CERT_BIG_TYPE_ONE(DictTypeEnums.CERT_BIG_TYPE, "WATER_TRANSPORT", "水路运输类"),
    CERT_BIG_TYPE_TWO(DictTypeEnums.CERT_BIG_TYPE, "HIGHWAY_CATEGORY", "公路类"),
    CERT_BIG_TYPE_THREE(DictTypeEnums.CERT_BIG_TYPE, "CONSTRUCTION_CATEGORY", "建管类"),

    ROAD_TRANSPORT_0(DictTypeEnums.ROAD_TRANSPORT, "866CDFC091CD4529A87771E558BA7E5A", "中华人民共和国道路运输经营许可证"),
    ROAD_TRANSPORT_1(DictTypeEnums.ROAD_TRANSPORT, "D2C34C63E3564340980F0C87535CBC85", "中华人民共和国道路危险货物运输许可证"),
    ROAD_TRANSPORT_2(DictTypeEnums.ROAD_TRANSPORT, "57CC896E44D34A0A9F112FFBBD14F65A", "网络预约出租汽车经营许可证"),
    ROAD_TRANSPORT_3(DictTypeEnums.ROAD_TRANSPORT, "5BCD24DC10A141BF8DFE8CF52899CD25", "放射性物品道路运输许可证"),
    ROAD_TRANSPORT_4(DictTypeEnums.ROAD_TRANSPORT, "71124074089E440BBE8C131A26E7088F", "中华人民共和国道路运输证"),
    ROAD_TRANSPORT_5(DictTypeEnums.ROAD_TRANSPORT, "C0C78F6CC9464D92B80018770FB87842", "网络预约出租汽车运输证"),
    ROAD_TRANSPORT_6(DictTypeEnums.ROAD_TRANSPORT, "F9E02C91FBA646C7A6C537EDC7669E4E", "道路运输从业人员从业资格证"),
    ROAD_TRANSPORT_7(DictTypeEnums.ROAD_TRANSPORT, "DF080274C01F4781A1E7EA29D3249509", "巡游出租汽车驾驶员证"),
    ROAD_TRANSPORT_8(DictTypeEnums.ROAD_TRANSPORT, "0808C9021A38499197D5ECC917886084", "网络预约出租汽车驾驶员证"),

    WATER_TRANSPORT_0(DictTypeEnums.WATER_TRANSPORT, "D1DA743959D44EB9B5A4C5D05B646176", "中华人民共和国港口经营许可证"),
    WATER_TRANSPORT_1(DictTypeEnums.WATER_TRANSPORT, "CCD8726A1CD64888B1D7C612B5709BEC", "船舶营业运输证注销登记证明书"),
    WATER_TRANSPORT_2(DictTypeEnums.WATER_TRANSPORT, "1377BB2EAAD64EA3A0A9B414997D78C0", "国内水路运输经营许可证"),
    WATER_TRANSPORT_3(DictTypeEnums.WATER_TRANSPORT, "35E16806D2F4450884C247457BA2B49F", "国内船舶管理业务经营许可证"),
    WATER_TRANSPORT_4(DictTypeEnums.WATER_TRANSPORT, "2A2930737804432BA267A7E18D077529", "水上水下作业和活动许可证"),
    WATER_TRANSPORT_5(DictTypeEnums.WATER_TRANSPORT, "54940FE8F7454F9184569C86D9D8A2B1", "港口危险货物作业附证"),
    WATER_TRANSPORT_6(DictTypeEnums.WATER_TRANSPORT, "27108A084B894E1C8EA4FB059879D8A9", "危险化学品水路运输从业资格证书（申报员）"),
    WATER_TRANSPORT_7(DictTypeEnums.WATER_TRANSPORT, "8BE4DD8923D04E7AB426EE9CEB62A87C", "危险化学品水路运输从业资格证书（装卸管理员）"),
    WATER_TRANSPORT_8(DictTypeEnums.WATER_TRANSPORT, "D07348516006450882043351C85D29EA", "危险化学品水路运输从业资格证书（检查员）"),
    WATER_TRANSPORT_9(DictTypeEnums.WATER_TRANSPORT, "8983007FC8BD48D98920AC7BB5153F26", "船舶营业运输证"),

    HIGHWAY_CATEGORY_0(DictTypeEnums.HIGHWAY_CATEGORY, "B22207F65DBC4E228ED768EB78822E98", "公路养护作业单位资质证书"),
    HIGHWAY_CATEGORY_1(DictTypeEnums.HIGHWAY_CATEGORY, "DF686B22ABEC4CE68EBDE69C34C467CF", "超限运输车辆通行证"),
    HIGHWAY_CATEGORY_2(DictTypeEnums.HIGHWAY_CATEGORY, "786C5B0B41E6430099C8ABDD1E258648", "超限运输车辆通行证(跨省)"),
    HIGHWAY_CATEGORY_3(DictTypeEnums.HIGHWAY_CATEGORY, "E3E4EF56DD644F88A7D799640DD42AFB", "中华人民共和国二级造价工程师注册证书"),


    CONSTRUCTION_CATEGORY_0(DictTypeEnums.CONSTRUCTION_CATEGORY, "2E466E6C5DE949AB9F31CFE61D0F7657", "公路水运工程质量检测机构资质证书"),
    CONSTRUCTION_CATEGORY_1(DictTypeEnums.CONSTRUCTION_CATEGORY, "2308A506D08C43AFABA53B5E911FDE50", "公路水运工程监理企业资质证书"),

    CERT_REPORT_TYPE_0(DictTypeEnums.CERT_REPORT_TYPE, "0", "日报"),
    CERT_REPORT_TYPE_1(DictTypeEnums.CERT_REPORT_TYPE, "1", "周报"),
    CERT_REPORT_TYPE_2(DictTypeEnums.CERT_REPORT_TYPE, "2", "月报"),
    CERT_REPORT_TYPE_3(DictTypeEnums.CERT_REPORT_TYPE, "-1", "自定义"),


    SUBGRADE_STATUS(DictTypeEnums.PERIOD_DISEASE, "1", "路基是否完好整洁、使用正常"),
    ROAD_SURFACE_STATUS(DictTypeEnums.PERIOD_DISEASE, "2", "路面是否完好整洁、使用正常"),
    BRIDGE_DECK_STATUS(DictTypeEnums.PERIOD_DISEASE, "3", "桥面系是否完好整洁、使用正常"),
    TUNNEL_STRUCTURE_STATUS(DictTypeEnums.PERIOD_DISEASE, "4", "隧道土建结构及其他工程设施是否完好整洁、使用正常"),
    TRAFFIC_SAFETY_FACILITY_STATUS(DictTypeEnums.PERIOD_DISEASE, "5", "交通安全设施是否完好整洁、使用正常"),
    MECHANICAL_ELECTRICAL_FACILITY_STATUS(DictTypeEnums.PERIOD_DISEASE, "6", "机电设施是否完好整洁、使用正常"),
    GREENING_ENVIRONMENT_FACILITY_STATUS(DictTypeEnums.PERIOD_DISEASE, "7", "绿化与环境保护设施是否完好整洁、使用正常"),
    SAFETY_ISSUE(DictTypeEnums.PERIOD_DISEASE, "8", "是否存在影响安全的病害、缺损及其他异常情况"),
    ROADSIDE_BLOCKING(DictTypeEnums.PERIOD_DISEASE, "9", "路侧是否存在遮挡标志和安全视距的植物和设施"),
    OTHER_DAY_ISSUE(DictTypeEnums.PERIOD_DISEASE, "10", "是否存在其他异常问题"),
    SIGN_NIGHT_VISIBILITY(DictTypeEnums.PERIOD_DISEASE, "11", "标志夜间视认性是否满足使用要求"),
    MARKING_NIGHT_VISIBILITY(DictTypeEnums.PERIOD_DISEASE, "12", "标线夜间视认性是否满足使用要求"),
    OUTLINE_MARKER_NIGHT_VISIBILITY(DictTypeEnums.PERIOD_DISEASE, "13", "轮廓标夜间视认性是否满足使用要求"),
    LIGHTING_FACILITY_STATUS(DictTypeEnums.PERIOD_DISEASE, "14", "照明设施是否齐全完好、工作正常"),
    OTHER_NIGHT_ISSUE(DictTypeEnums.PERIOD_DISEASE, "15", "是否存在其他异常问题(夜间)"),

    GEO_JSON_FORMAT_TYPE_0(DictTypeEnums.GEO_JSON_FORMAT_TYPE, "Point", "点"),
    GEO_JSON_FORMAT_TYPE_1(DictTypeEnums.GEO_JSON_FORMAT_TYPE, "LineString", "线"),
    GEO_JSON_FORMAT_TYPE_2(DictTypeEnums.GEO_JSON_FORMAT_TYPE, "Polygon", "多边形"),
    GEO_JSON_FORMAT_TYPE_3(DictTypeEnums.GEO_JSON_FORMAT_TYPE, "MultiPoint", "多点"),
    GEO_JSON_FORMAT_TYPE_4(DictTypeEnums.GEO_JSON_FORMAT_TYPE, "MultiLineString", "多线"),
    GEO_JSON_FORMAT_TYPE_5(DictTypeEnums.GEO_JSON_FORMAT_TYPE, "MultiPolygon", "多多边形"),

    /* --------------------------------- 非标道路通行服务一件事 ---------------------------*/
    FB_JCHD_UCEM_TITLE_0(DictTypeEnums.FB_JCHD_UCEM_TITLE, "0", "工作人员非标路巡查上报"),
    FB_JCHD_UCEM_TITLE_1(DictTypeEnums.FB_JCHD_UCEM_TITLE, "1", "群众非标路通行问题上报"),

    FB_JCHD_UCEM_TYPE_0(DictTypeEnums.FB_JCHD_UCEM_TYPE, "0", "问题"),
    FB_JCHD_UCEM_TYPE_1(DictTypeEnums.FB_JCHD_UCEM_TYPE, "1", "突发事件"),
    FB_JCHD_UCEM_TYPE_2(DictTypeEnums.FB_JCHD_UCEM_TYPE, "2", "风险"),

    FB_LOW_RISK(DictTypeEnums.FB_RISK_LEVEL_TYPE, "0", "低风险"),
    FB_MEDIUM_RISK(DictTypeEnums.FB_RISK_LEVEL_TYPE, "1", "中风险"),
    FB_HIGH_RISK(DictTypeEnums.FB_RISK_LEVEL_TYPE, "2", "高风险"),
    FB_EXTREMELY_HIGH_RISK(DictTypeEnums.FB_RISK_LEVEL_TYPE, "3", "极高风险"),

    FB_HIGH_SLOPE(DictTypeEnums.FB_RISK_TYPE, "1", "公路高边坡"),
    FB_COLLAPSE(DictTypeEnums.FB_RISK_TYPE, "2", "崩塌"),
    FB_LANDSLIDE(DictTypeEnums.FB_RISK_TYPE, "3", "滑坡"),
    FB_MUDSLIDE(DictTypeEnums.FB_RISK_TYPE, "4", "泥石流"),
    FB_SUBSIDENCE_AND_COLLAPSE(DictTypeEnums.FB_RISK_TYPE, "5", "沉陷与塌陷"),
    FB_WATER_DAMAGE(DictTypeEnums.FB_RISK_TYPE, "6", "水毁"),
    FB_OTHER_DISASTERS(DictTypeEnums.FB_RISK_TYPE, "9", "其他"),

    FB_PROBLEM_DISPOSE_STATUS_0(DictTypeEnums.FB_PROBLEM_DISPOSE_STATUS, "0", "未处置"),
    FB_PROBLEM_DISPOSE_STATUS_1(DictTypeEnums.FB_PROBLEM_DISPOSE_STATUS, "1", "处置中"),
    FB_PROBLEM_DISPOSE_STATUS_2(DictTypeEnums.FB_PROBLEM_DISPOSE_STATUS, "2", "已办结"),

    FB_UNEXPECTED_EVENT_TYPE_0(DictTypeEnums.FB_UNEXPECTED_EVENT_TYPE, "0", "交通事故"),
    FB_UNEXPECTED_EVENT_TYPE_1(DictTypeEnums.FB_UNEXPECTED_EVENT_TYPE, "1", "道路损坏"),
    FB_UNEXPECTED_EVENT_TYPE_2(DictTypeEnums.FB_UNEXPECTED_EVENT_TYPE, "2", "自然灾害"),

    FB_RATING_REPORT_TYPE_0(DictTypeEnums.FB_RATING_REPORT_TYPE, "0", "月报"),
    FB_RATING_REPORT_TYPE_1(DictTypeEnums.FB_RATING_REPORT_TYPE, "1", "季报"),
    FB_RATING_REPORT_TYPE_2(DictTypeEnums.FB_RATING_REPORT_TYPE, "2", "年报"),

    RISK_ALARM_TYPE_0(DictTypeEnums.RISK_ALARM_TYPE, "0", "日降水量"),
    RISK_ALARM_TYPE_1(DictTypeEnums.RISK_ALARM_TYPE, "1", "日最高温值"),
    RISK_ALARM_TYPE_2(DictTypeEnums.RISK_ALARM_TYPE, "2", "日最低温值"),

    WEATHER_ALARM_LEVEL_1(DictTypeEnums.WEATHER_ALARM_LEVEL, "1", "红色预警"),
    WEATHER_ALARM_LEVEL_2(DictTypeEnums.WEATHER_ALARM_LEVEL, "2", "橙色预警"),
    WEATHER_ALARM_LEVEL_3(DictTypeEnums.WEATHER_ALARM_LEVEL, "3", "黄色预警"),

    NON_STANDARD_ROAD_0(DictTypeEnums.NON_STANDARD_ROAD,"0","农业生产便道、机耕道"),
    NON_STANDARD_ROAD_1(DictTypeEnums.NON_STANDARD_ROAD,"1","下河道"),
    NON_STANDARD_ROAD_2(DictTypeEnums.NON_STANDARD_ROAD,"2","山坪塘、田改塘路"),
    NON_STANDARD_ROAD_3(DictTypeEnums.NON_STANDARD_ROAD,"3","水利后扶路、移民路、水库和电站堤坝路"),
    NON_STANDARD_ROAD_4(DictTypeEnums.NON_STANDARD_ROAD,"4","农家乐、乡村游、果(菜)园自建路"),
    NON_STANDARD_ROAD_5(DictTypeEnums.NON_STANDARD_ROAD,"5","森林防火通道"),
    NON_STANDARD_ROAD_6(DictTypeEnums.NON_STANDARD_ROAD,"6","“一事一议”路"),
    NON_STANDARD_ROAD_7(DictTypeEnums.NON_STANDARD_ROAD,"7","其他"),

    FB_REPORT_TYPE_0(DictTypeEnums.FB_REPORT_TYPE, "0", "服务评价报告"),
    FB_REPORT_TYPE_1(DictTypeEnums.FB_REPORT_TYPE, "1", "路网运行流量报告"),
    FB_REPORT_TYPE_2(DictTypeEnums.FB_REPORT_TYPE, "2", "路网运行成效分析报告"),

    FB_PROBLEM_TYPE_0(DictTypeEnums.FB_PROBLEM_TYPE, "0", "占道堆码"),
    FB_PROBLEM_TYPE_1(DictTypeEnums.FB_PROBLEM_TYPE, "1", "路面垃圾"),
    FB_PROBLEM_TYPE_2(DictTypeEnums.FB_PROBLEM_TYPE, "2", "路面/边沟杂草"),
    FB_PROBLEM_TYPE_3(DictTypeEnums.FB_PROBLEM_TYPE, "3", "其他"),

    FB_AUDIT_STATUS_0(DictTypeEnums.FB_AUDIT_STATUS, "0", "待审核"),
    FB_AUDIT_STATUS_1(DictTypeEnums.FB_AUDIT_STATUS, "1", "已审核"),

    FB_DATA_SOURCE_0(DictTypeEnums.FB_DATA_SOURCE, "0", "工作人员上报"),
    FB_DATA_SOURCE_1(DictTypeEnums.FB_DATA_SOURCE, "1", "群众上报"),


    ;

    private DictTypeEnums dictTypeEnums;

    private String code;

    private String name;

    DictValueEnums(DictTypeEnums dictTypeEnums, String code, String name) {
        this.dictTypeEnums = dictTypeEnums;
        this.name = name;
        this.code = code;
    }

    public DictTypeEnums getDictTypeEnums() {
        return dictTypeEnums;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


    public static DictValueEnums findByTypeAndCode(String parentCode, String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values()).filter(dictValueEnums -> StringUtils.equals(dictValueEnums.getDictTypeEnums().getCode(), parentCode) && code.toString().equals(dictValueEnums.getCode().toString())).findFirst().orElse(null);
    }

    public static String findNameByTypeAndCode(String parentCode, String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values()).filter(dictValueEnums -> StringUtils.equals(dictValueEnums.getDictTypeEnums().getCode(), parentCode) && code.equals(dictValueEnums.getCode()))
                .map(DictValueEnums::getName)
                .findFirst().orElse(null);
    }

    public static List<DictValueEnums> findByType(String parentCode) {
        if (parentCode == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(values()).filter(dictValueEnums -> StringUtils.equals(dictValueEnums.getDictTypeEnums().getCode(), parentCode)).collect(Collectors.toList());
    }

    public static List<DictValueEnums> findByTypes(List<String> parentCodes) {
        return Arrays.stream(values()).filter(dictValueEnums -> parentCodes.contains(dictValueEnums.getDictTypeEnums().getCode())).collect(Collectors.toList());
    }

    public static List<String> findByTypes(DictTypeEnums dictTypeEnums) {
        return Arrays.stream(values()).filter(
                v -> StringUtils.equals(v.getDictTypeEnums().getCode(), dictTypeEnums.getCode())
        ).map(DictValueEnums::getCode).distinct().collect(Collectors.toList());
    }

    public static Map<Object, String> findByTypeMap(String parentCode) {
        List<DictValueEnums> dictValueEnumsList = Arrays.stream(values()).filter(dictValueEnums -> StringUtils.equals(dictValueEnums.getDictTypeEnums().getCode(), parentCode)).collect(Collectors.toList());
        return StringUtils.isNotEmpty(dictValueEnumsList) ? dictValueEnumsList.stream().collect(Collectors.toMap(DictValueEnums::getCode, DictValueEnums::getName)) : new HashMap<>();
    }

    public static Integer codeToInteger(DictValueEnums dictValueEnums) {
        Integer code = null;
        try {
            code = Integer.valueOf(String.valueOf(dictValueEnums.getCode()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public static DictValueEnums findByTypeAndName(String parentCode, String name) {
        if (name == null) {
            return null;
        }
        return Arrays.stream(values()).filter(dictValueEnums -> StringUtils.equals(dictValueEnums.getDictTypeEnums().getCode(), parentCode) && name.equals(dictValueEnums.getName())).findFirst().orElse(null);
    }
}
