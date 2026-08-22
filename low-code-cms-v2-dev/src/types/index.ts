export interface Response {
    code?: number;
    data?: any;
    msg?: [string, null];
}

export interface Order {
    asc: boolean;
    column: string;
}

export interface BaseParams {
    countId?: string;
    current: number; //页码
    maxLimit?: number | string;
    optimizeCountSql?: boolean;
    orders?: Array<Order>;
    pages?: number;
    searchCount?: boolean;
    size: number; // 分页
    total?: number;
    orderByDTOS?: Array<orderByDTOSParams>;
}

// 系统配置数据类型
export interface ConfigParams {
    id: string;
    configName?: string;
    configKey?: string;
    configValue: string;
    configType?: string;
    remarks?: string | null;
    code: string;
    codeName?: string | null;
    createTime?: string;
    modifyTime?: string;
    list?: Array<ConfigParams> | null;
}

// 菜单项返回参数
export interface MenuResponseParams {
    createId?: string;
    createTime?: string;
    delFlag?: string | number;
    frameType?: string;
    href: string;
    icon(): void;
    id: string;
    key?: string;
    label(): any | undefined;
    isShow?: string;
    menuCode?: string;
    menuName?: string;
    menuType: string;
    menuTypeName?: string;
    modifyId?: string;
    modifyTime?: string;
    parentId?: string;
    sort?: string | number;
    status?: string;
    ancestors?: string;
    children?: Array<MenuResponseParams> | undefined;
}

// 部门参数

export interface DepartParams {
    ancestors?: string;
    children?: Array<DepartParams>;
    createId: string;
    createTime?: string;
    delFlag?: string | number;
    departName: string;
    id: string;
    modifyId?: string;
    modifyTime?: string;
    parentId?: string;
    phone?: string;
    sort?: string | number;
    sysRoleVOS?: Array<any>;
    userId?: string;
    userName?: string;
    color?: string;
}

// 排序参数

export interface orderByDTOSParams {
    asc: boolean;
    column: string;
}
