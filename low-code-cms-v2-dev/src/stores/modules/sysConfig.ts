import { defineStore } from "pinia";

export const useSysConfigStore = defineStore("sysConfig", {
    state: () => {
        return {
            menuActiveKey: localStorage.getItem("menuActiveKey") || "",
            tabs: JSON.parse(localStorage.getItem("tabs") || "[]"),
            tabsValue: localStorage.getItem("tabsValue") || "",
            systemConfig: JSON.parse(
                localStorage.getItem("system_config") || "[]"
            ),
            collapsed: false, // 是否收缩菜单 默认展开
        };
    },
});
