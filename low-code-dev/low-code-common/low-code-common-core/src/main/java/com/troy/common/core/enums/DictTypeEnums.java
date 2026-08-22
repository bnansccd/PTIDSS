package com.troy.common.core.enums;

import com.troy.common.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname: DictTypeEnums
 * @Description:
 * @Date 2022/9/2
 * @Author: yzy
 * @Version
 **/
public enum DictTypeEnums {

    /* --------------------------------- 基础类型 ---------------------------*/

    TRUE_FALSE("TRUE_FALSE", "是否"),

    SEX("SEX", "性别"),

    STATUS_TYPE("STATUS_TYPE", "启用停用"),

    MENU_TYPE("MENU_TYPE", "菜单类型"),

    BUSINESS_TYPE("BUSINESS_TYPE", "业务类型"),

    OPERATOR_TYPE("OPERATOR_TYPE", "操作类别"),

    DATA_RANGE("DATA_RANGE", "数据范围"),

    REQUEST_METHOD("REQUEST_METHOD", "请求方式"),

    APP_TYPE("APP_TYPE", "应用类型"),

    FRAME_TYPE("FRAME_TYPE", "外链方式"),

    NEWS_TYPE("NEWS_TYPE", "新闻类型"),

    TENANT_PERMISSION("TENANT_PERMISSION", "租户权限配置"),

    COMPARISON_OPERATOR("COMPARISON_OPERATOR","比较运算符"),

    /* --------------------------------- 市级平台 ---------------------------*/

    GEO_JSON_STATUS("GEO_JSON_STATUS", "geo状态"),

    GEO_JSON_TYPE("GEO_JSON_TYPE", "GEOJSON文件类型"),

    SFWCFLD("SFWCFLD", "是否为重复路段"),

    ADMINISTRATION_GRADE("ADMINISTRATION_GRADE", "行政等级"),

    PAVEMENT_TYPE("PAVEMENT_TYPE", "面层类型"),

    TECHNICAL_GRADE("TECHNICAL_GRADE", "技术等级"),

    DESIGN_LOAD_LEVEL("DESIGN_LOAD_LEVEL", "设计荷载等级"),

    LEVEL_TECHNICAL_BRIDGE("LEVEL_TECHNICAL_BRIDGE", "桥梁技术状况评定等级"),

    BRIDGE_LEVEL("BRIDGE_LEVEL", "桥梁类型"),

    BRIDGE_ZQSBGZJGSS("BRIDGE_ZQSBGZJGSS", "桥梁上部结构形式"),

    BRIDGE_ABUTMENT_TYPE("BRIDGE_ABUTMENT_TYPE", "桥台类型"),

    NAVIGABLE_LEVEL("NAVIGABLE_LEVEL", "通航等级"),

    CULVERT_TYPE("CULVERT_TYPE", "涵洞类型"),

    CONSTRUCTION_TYPE_OF_ENTRANCE("CONSTRUCTION_TYPE_OF_ENTRANCE", "洞口形式"),

    TUNNEL_DRAINAGE_TYPE("TUNNEL_DRAINAGE_TYPE", "隧道排水类型"),

    OUTLINE_TYPE("OUTLINE_TYPE", "隧道断面形式"),

    TUNNEL_VENTILATION_TYPE("TUNNEL_VENTILATION_TYPE", "隧道通风类型"),

    TUNNEL_LIGHTING_CONDITION_TYPE("TUNNEL_LIGHTING_CONDITION_TYPE", "隧道照明状况类型"),

    TRAFFIC_TYPE("TRAFFIC_TYPE", "交通设施类型"),

    SIGN_TYPE("SIGN_TYPE", "警示标志标牌类型"),

    SIGN_POSITION("SIGN_POSITION", "警示标志标牌位置类型"),

    GUARDRAIL("GUARDRAIL", "护栏类型"),

    NATURAL_DISASTERS("NATURAL_DISASTERS", "自然灾害类型"),

    MQ_DEAL_WITH_STATUS("MQ_DEAL_WITH_STATUS", "MQ处理状态"),

    PATROL_CAR_TYPE("PATROL_CAR_TYPE", "巡查车辆类型"),

    PATROL_DEVICE_TYPE("PATROL_DEVICE_TYPE", "巡查设备类型"),

    CONSTRUCTION_PROJECT_TYPE("CONSTRUCTION_PROJECT_TYPE", "建设项目类别"),

    CHECK_RESULT_TYPE("CHECK_RESULT_TYPE","检查结果"),


    COMPLAINT_TYPE("COMPLAINT_TYPE","投诉分类"),

    COMPLAINT_EMOTING_TYPE("COMPLAINT_EMOTING_TYPE","投诉情绪分析"),

    COMPLAINT_DEAL_TYPE("COMPLAINT_DEAL_TYPE","投诉处理方式"),

    BLOCKING_EVENTS_REASON_TYPE("BLOCKING_EVENTS_REASON_TYPE","公路阻断原因"),

    BLOCKING_EVENTS_DIRECTION("BLOCKING_EVENTS_DIRECTION","公路阻断方向"),

    T_SEX("T_SEX", "厅信息中心人员表性别"),

    VEHICLE_OPERATION_STATUS("VEHICLE_OPERATION_STATUS","车辆营运状态"),

    VEHIC_TYPE_CODE("VEHIC_TYPE_CODE","车辆类型"),

    PASSENGER_STATION_TYPE("PASSENGER_STATION_TYPE","客运站分类"),

    GA_T_AREA_CODE("GA_T_AREA_CODE","广安厅信息中心区域"),

    NATION("NATION","民族"),

    SHIP_TYPE("SHIP_TYPE","船舶类型"),

    SERVICE_AREA_TYPE("SERVICE_AREA_TYPE","服务区类型"),



    SLOPE_STRUCTURE("SLOPE_STRUCTURE", "坡体类型"),

    TRAFFIC_ON_OR_OFF("TRAFFIC_ON_OR_OFF", "通断状况"),

    HARM_TARGET("HARM_TARGET", "危害对象"),

    HARM_LEVEL("HARM_LEVEL", "危害程度"),

    DISASTER_DISPOSAL("DISASTER_DISPOSAL", "灾害处置情况"),

    BREAK_POSITION("BREAK_POSITION", "裂缝位置"),

    STRUCTURE_AWAY_WATER("STRUCTURE_AWAY_WATER", "排水"),

    STRUCTURE_SLOP_PROTECT("STRUCTURE_SLOP_PROTECT", "坡面防护"),

    STRUCTURE_ALONG_RIVER("STRUCTURE_ALONG_RIVER", "沿河防护"),

    STRUCTURE_SUPPORT("STRUCTURE_SUPPORT", "支挡设施"),

    STRUCTURE_BRIDGE("STRUCTURE_BRIDGE", "桥面系"),

    STRUCTURE_HOLE("STRUCTURE_HOLE", "洞门"),

    DAMAGE_LEVEL("DAMAGE_LEVEL", "破损严重程度"),


    FLOW_TYPE("FLOW_TYPE", "泥石流类型"),

    COLLAPSE_REASON("COLLAPSE_REASON", "塌陷触发原因"),

    WATER_FEATURE("WATER_FEATURE", "地形特征"),

    WATER_PART("WATER_PART", "水毁部位"),

    WATER_TYPE("WATER_TYPE", "水毁类型"),


    HIDDEN_DANGER_POINTS("HIDDEN_DANGER_POINTS", "隐患点类型"),

    NATURAL_DANGER_LEVEL("NATURAL_DANGER_LEVEL", "自然危害风险等级"),

    BRIDGE_PATROL("BRIDGE_PATROL", "桥梁巡查类型"),


    BRIDGE_PROBLEM("BRIDGE_PROBLEM", "桥梁问题类型"),


    PERIOD_DISEASE("PERIOD_DISEASE", "巡查时段病害"),


    /* --------------------------------- ybyx平台 ---------------------------*/

    HK_REQUEST_TYPE("HK_REQUEST_TYPE", "海康请求类型"),

    WORK_SHEET("WORK_SHEET", "工单状态"),

    /* --------------------------------- 交通 ---------------------------*/

    EQUIPMENT_TYPE("EQUIPMENT_TYPE", "物资装备类型"),

    MEDICAL_RESOURCE("MEDICAL_RESOURCE", "医疗资源"),

    BRIDGE_PATROL_PROBLEM_TYPE("BRIDGE_PATROL_PROBLEM_TYPE","桥梁巡查问题类型"),


    BRIDGE_PATROL_DISPOSAL_SITUATION("BRIDGE_PATROL_DISPOSAL_SITUATION","桥梁巡查处置情况类型"),

    BRIDGE_COMPONENT_TYPE("BRIDGE_COMPONENT_TYPE","桥梁构件部位"),


    BRIDGE_CHECK_TYPE("BRIDGE_CHECK_TYPE","桥梁检查分类"),

    BRIDGE_DISEASE_TYPE("BRIDGE_DISEASE_TYPE","桥梁病害分类"),

    ROUTE_SECTION_GLSX("ROUTE_SECTION_GLSX","是否干线公路接养"),

    MONITOR_ALARM_EVENT_STATUS("MONITOR_ALARM_EVENT_STATUS","AI 监控处置状态"),

    MONITOR_ALARM_EVENT_CODE("MONITOR_ALARM_EVENT_CODE","事件编码"),

    OVERRUN_TRUCK_TYPE("OVERRUN_TRUCK_TYPE","超限货车类型"),


    /* --------------------------------- 低代码 ---------------------------*/
    DATA_BASE("DATA_BASE", "数据库类型"),

    TABLE_DATA_TYPE("TABLE_DATA_TYPE", "数据字段类型"),

    COLUMN_STATUS("COLUMN_STATUS", "字段状态"),

    COLUMN_TYPE("COLUMN_TYPE", "字段类型"),

    COLUMN_SHOW_CONTROL("COLUMN_SHOW_CONTROL", "控件显示类型"),

    COLUMN_CONVERT("COLUMN_CONVERT", "字段控件类型"),

    COLUMN_VALIDATE("COLUMN_VALIDATE", "字段校验"),

    PLATE_COLOR("PLATE_COLOR", "车牌颜色"),

    THREE_TYPE_NINI_CERT_TYPE("THREE_TYPE_NINI_CERT_TYPE", "三类九证类型"),

    DZZZ_CODE_SOURCE("DZZZ_CODE_SOURCE", "电子证照反馈结果来源"),

    DZZZ_DATA_STAGE("DZZZ_DATA_STAGE", "电子证照数据处理阶段"),

    DZZZ_CERTIFICATE_HOLDER_TYPE("DZZZ_CERTIFICATE_HOLDER_TYPE", "持证主体代码类型"),

    DZZZ_SLJT_TYPE("DZZZ_SLJT_TYPE","电子证照类型代码"),

    PROVINCE_DZZZ_SLJT_TYPE("PROVINCE_DZZZ_SLJT_TYPE","省平台电子证照明细编码"),

    RA_OPERATING_STATUS("RA_OPERATING_STATUS","业户经营状态"),

    VEHICLES_AUDIT_YEAR_STATUS("VEHICLES_AUDIT_YEAR_STATUS","车辆年审状态代码"),

    VEHICLES_TECHNOLOGY_LEVEL("VEHICLES_TECHNOLOGY_LEVEL","车辆技术等级代码"),

    FSLJZ_DZZZ_DATA_STAGE("FSLJZ_DZZZ_DATA_STAGE", "非三类九证电子证照数据处理阶段"),

    OPERATE_STATUS("OPERATE_STATUS","操作状态"),
    HDDJ("HDDJ","航道等级"),
    fwqlx("fwqlx","服务区类型"),
    DATA_SOURCE("DATA_SOURCE","数据来源"),
    STANDARD_TRUE_FALSE("STANDARD_TRUE_FALSE","标准是否"),
    AI_PATROL_ANALYSIS("AI_PATROL_ANALYSIS","智能巡查分析状态"),
    MAINTAIN_TYPE("MAINTAIN_TYPE","养护类别"),
    AUTH_BUTTON("AUTH_BUTTON","权限按钮编码"),
    ENABLE("ENABLE","启动状态"),
    LINK_TYPE("LINK_TYPE","外链类型"),
    ROAD_LEVEL("ROAD_LEVEL","路线等级"),
    PROPERTY("PROPERTY","路产类型"),
    CLASSIFICATION_OF_TUNNEL("CLASSIFICATION_OF_TUNNEL","隧道分类"),

    HOLDER_TYPE("HOLDER_TYPE", "持有者类型"),

    DZZZ_QUALIFICATION_LEVEL_CODE("DZZZ_QUALIFICATION_LEVEL_CODE","电子证照资质等级代码"),

    DZZZ_BUSINESS_SCOPE_CODE("DZZZ_BUSINESS_SCOPE_CODE","电子证照业务范围代码"),

    DZZZ_CERTIFICATE_STATUS_CODE("DZZZ_CERTIFICATE_STATUS_CODE","电子证照证书状态代码"),

    SHIP_CERTIFICATE_STATUS_CODE("SHIP_CERTIFICATE_STATUS_CODE","船舶证书状态代码"),


    SUPERVISION_OF_ENTERPRISES_CERTIFICATE_STATUS_CODE("SUPERVISION_OF_ENTERPRISES_CERTIFICATE_STATUS_CODE","公路水运工程监理企业资质证书电子证照证书状态代码"),

    DZZZ_SEX("DZZZ_SEX","性别"),


    TYPES_OF_ILLEGAL_OPERATIONS_MONITORING("TYPES_OF_ILLEGAL_OPERATIONS_MONITORING","非法营运监测类型"),


    /**
     * 共享交换
     */
    GAT_16_4("GAT_16_4", "车辆类型代码"),

    JTT_415_2006_5_1_8("JTT_415_2006_5_1_8", "经济类型"),

    JTT_415_2006_5_2_11("JTT_415_2006_5_2_11", "驾校类别"),

    JTT_415_2006_5_2_32("JTT_415_2006_5_2_32", "地市简称"),

    JTT_415_2006_5_3_1("JTT_415_2006_5_3_1", "业户经营状态"),

    JTT_415_2006_5_3_6("JTT_415_2006_5_3_6", "客运站类别"),

    JTT_415_2006_5_4_5("JTT_415_2006_5_4_5", "燃料类型"),

    JTT_415_2006_5_4_6("JTT_415_2006_5_4_6", "车辆运行状态"),

    JTT_415_2006_5_4_10("JTT_415_2006_5_4_10", "车型代码"),

    JTT_415_2006_5_4_12("JTT_415_2006_5_4_12", "车牌代码"),

    JTT_415_2006_5_5_3("JTT_415_2006_5_5_3", "证照发放类型"),

    JTT_415_2006_5_5_1("JTT_415_2006_5_5_1", "证照类型与代码/证照类别"),

    JTT_415_2006_5_5_2("JTT_415_2006_5_5_2", "证照状态"),


    JTT_697_7_2014_5_2("JTT_697_7_2014_5_2", "身份证件类别代码"),

    JTT_697_7_2014_5_21("JTT_697_7_2014_5_21", "客车类型与等级代码"),

    JTT_697_7_2014_5_22("JTT_697_7_2014_5_22", "从业资格证类别代码"),

    JTT_1291_2019_6_3_2_5_3("JTT_1291_2019_6_3_2_5_3", "分支机构标识"),

    JTT_1291_2019_6_4_1("JTT_1291_2019_6_4_1", "关联事项代码"),

    SLJZ_STATISTICS_TYPE("SLJZ_STATISTICS_TYPE", "统计类型"),

    ALL_CERT_STATUS("ALL_CERT_STATUS", "所有生成证照状态"),

    ALL_CERT_TYPE("ALL_CERT_TYPE", "所有证照类型"),

    AREA_LEVEL("AREA_LEVEL", "区域级别"),

    CERT_BIG_TYPE("CERT_BIG_TYPE", "证照大类"),

    ROAD_TRANSPORT("ROAD_TRANSPORT", "道路运输类"),

    WATER_TRANSPORT("WATER_TRANSPORT", "水路运输类"),

    HIGHWAY_CATEGORY("HIGHWAY_CATEGORY", "公路类"),

    CONSTRUCTION_CATEGORY("CONSTRUCTION_CATEGORY", "建管类"),

    CERT_REPORT_TYPE("CERT_REPORT_TYPE", "证照报告类型"),

    OVERRUN_WORK_TYPE("OVERRUN_WORK_TYPE","超限证从业资格类别"),

    OVERRUN_SITE_TYPE("OVERRUN_SITE_TYPE","超限证站点类型"),

    OVERRUN_CAR_TYPE("OVERRUN_CAR_TYPE", "准驾车型"),

    OVERRUN_PROCESS_TYPE("OVERRUN_PROCESS_TYPE","一超四罚流程"),

    OVERRUN_MSG_TYPE("OVERRUN_MSG_TYPE","一超四罚短信类型"),

    GEO_JSON_FORMAT_TYPE("GEO_JSON_FORMAT_TYPE","GEOJSON格式类型"),

     /* --------------------------------- 非标道路通行服务一件事 ---------------------------*/
    CONSTRUCTION_STANDARD("CONSTRUCTION_STANDARD","建设执行的标准和程序标准"),

    NON_STANDARD_ROAD("NON_STANDARD_ROAD","非标道路路段类型"),

    FB_NOTICE_STATUS("FB_NOTICE_STATUS","公告状态"),

    FB_PATROL_TASK_STATUS("FB_PATROL_TASK_STATUS","非标巡检任务状态"),

    FB_PROBLEM_DISPOSE_STATUS("FB_PROBLEM_DISPOSE_STATUS","非标问题处置状态"),

    FB_ITEM_NAME("FB_ITEM_NAME","非标问题事项名称"),

    FB_PROBLEM_TYPE("FB_PROBLEM_TYPE","非标问题类型"),

    FB_AUDIT_STATUS("FB_AUDIT_STATUS","非标审核状态"),

    FB_JCHD_UCEM_TITLE("FB_JCHD_UCEM_TITLE","联合协同处置保通事件单标题"),

    FB_JCHD_UCEM_TYPE("FB_JCHD_UCEM_TYPE","联合协同处置保通事件类型"),

    FB_RISK_LEVEL_TYPE("FB_RISK_LEVEL_TYPE","非标风险等级"),

    FB_RISK_TYPE("FB_RISK_TYPE","非标风险类型"),

    FB_UNEXPECTED_EVENT_TYPE("FB_UNEXPECTED_EVENT_TYPE","非标突发事件类型"),

    FB_RATING_REPORT_TYPE("FB_RATING_REPORT_TYPE","非标评价报告类型"),

    RISK_ALARM_TYPE("RISK_ALARM_TYPE","风险预警类型"),

    WEATHER_ALARM_LEVEL("WEATHER_ALARM_LEVEL","气象预警等级"),

    FB_REPORT_TYPE("FB_REPORT_TYPE","非标报告类型"),

    FB_DATA_SOURCE("FB_DATA_SOURCE","非标数据来源"),
    ;
    private String code;

    private String name;

    DictTypeEnums(String code, String name) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    /**
     * 通过多个字典类型得到字典父code集合
     *
     * @param dictTypeEnums
     * @return
     */
    public static List<String> getCodeList(DictTypeEnums... dictTypeEnums) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(dictTypeEnums)) {
            for (DictTypeEnums dictTypeEnum : dictTypeEnums) {
                list.add(dictTypeEnum.getCode());
            }
        }
        return list;
    }
}

