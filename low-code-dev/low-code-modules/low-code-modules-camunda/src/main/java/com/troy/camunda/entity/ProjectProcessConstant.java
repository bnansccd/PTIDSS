package com.troy.camunda.entity;

public class ProjectProcessConstant {

    /**
     * 整个项目流程id
     */
    public static final String PROCESS_ID = "会签";

    public static final String PROCESS_ID_TWO = "Process_Demo1";

    /**
     * 学生id变量
     */
    public static final String VAR_NAME_STUDENT = "student";

    /**
     * 学院名称变量
     */
    public static final String VAR_NAME_SCHOOL = "school";

    /**
     * 一级审核user task的名称
     */
    public static final String TASK_NAME_FIRST_LEVEL_REVIEW = "一级审核";

    /**
     * 二级审核 user task的名称
     */
    public static final String TASK_NAME_SECOND_LEVEL_REVIEW = "二级审核";

    /**
     * 上传额外资料user task的名称
     */
    public static final String TASK_NAME_UPLOAD_EXTRA_INFO = "上传额外材料";


    /**
     * 表单字段名
     * 项目申请记录id
     */
    public static final String FORM_RECORD_ID = "recordId";

    /**
     * 表单字段名
     * 是否需要额外材料
     */
    public static final String FORM_EXTRA_INFO_1 = "extra_info_1";

    /**
     * 表单字段名
     * 是否已通过审批
     */
    public static final String FORM_APPROVED_1 = "approved_1";

    /**
     * 初始化人员
     */
    public static final String PROCESS_INITIATOR = "INITIATOR";
}
