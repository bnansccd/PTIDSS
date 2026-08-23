import { createApp } from "vue";
import { createPinia } from "pinia";

import "./assets/main.scss";

import "bpmn-js/dist/assets/diagram-js.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn-codes.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css";

// bpmn-js-properties-panel 5.x：样式移至 @bpmn-io/properties-panel，element-templates.css 已并入
import "@bpmn-io/properties-panel/dist/assets/properties-panel.css";
import App from "./App.vue";
import router from "./router";

const app = createApp(App);

import GlobalSearchInput from "./components/GlobalSearchInput.vue";
import WrapTableLayout from "./components/WrapTableLayout.vue";
import WrapLayout from "./components/WrapLayout.vue";
import WrapScroll from "./components/WrapScroll.vue";
import GlobalElPagination from "./components/GlobalElPagination.vue";
import GlobalElDialog from "./components/GlobalElDialog.vue";
import GlobalAuthElButton from "./components/GlobalAuthElButton.vue";
import GlobalAuthElButtonLink from "./components/GlobalAuthElButtonLink.vue";
import GlobalElForm from "./components/GlobalElFormNew.vue";
import GlobalElFormItem from "./components/GlobalElFormItem.vue";
import GlobalElSwitch from "./components/GlobalElSwitch.vue";
import GlobalUnfoldFewer from "./components/GlobalUnfoldFewer.vue";
import WrapToolbarSearchLayout from "./components/WrapToolbarSearchLayout.vue";
import GlobalDictSelect from "./components/GlobalDictSelect.vue";
import GlobalEditor from "./components/GlobalEditor.vue";
import GlobalUpload from "./components/GlobalUpload.vue";
import GlobalUploadPreview from "./components/GlobalUploadPreview.vue";
import GlobalDepartSelect from "./components/GlobalDepartSelect.vue";
import GlobalUserSelect from "./components/GlobalUserSelect.vue";
import GlobalDatePicker from "./components/GlobalDatePicker.vue";
import GlobalTenantSelect from "./components/GlobalTenantSelect.vue";

app.component("GlobalSearchInput", GlobalSearchInput);
app.component("WrapTableLayout", WrapTableLayout);
app.component("WrapLayout", WrapLayout);
app.component("WrapScroll", WrapScroll);
app.component("GlobalElPagination", GlobalElPagination);
app.component("GlobalElDialog", GlobalElDialog);
app.component("GlobalAuthElButton", GlobalAuthElButton);
app.component("GlobalAuthElButtonLink", GlobalAuthElButtonLink);
app.component("GlobalElForm", GlobalElForm);
app.component("GlobalElFormItem", GlobalElFormItem);
app.component("GlobalElSwitch", GlobalElSwitch);
app.component("GlobalUnfoldFewer", GlobalUnfoldFewer);
app.component("WrapToolbarSearchLayout", WrapToolbarSearchLayout);
app.component("GlobalDictSelect", GlobalDictSelect);
app.component("GlobalEditor", GlobalEditor);
app.component("GlobalUpload", GlobalUpload);
app.component("GlobalUploadPreview", GlobalUploadPreview);
app.component("GlobalDepartSelect", GlobalDepartSelect);
app.component("GlobalUserSelect", GlobalUserSelect);
app.component("GlobalDatePicker", GlobalDatePicker);
app.component("GlobalTenantSelect", GlobalTenantSelect);

app.use(createPinia());
app.use(router);

import ElementPlus from "element-plus";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
// lib ES2016 无 Object.entries（G3 类型门禁），改用 keys 遍历
for (const key of Object.keys(ElementPlusIconsVue)) {
    app.component(key, (ElementPlusIconsVue as any)[key]);
}
import zhCn from "element-plus/es/locale/lang/zh-cn";
import "element-plus/dist/index.css";
app.use(ElementPlus, { zIndex: 2000, locale: zhCn });

// , size: "small"

import VXETable from "vxe-table";
import "vxe-table/lib/style.css";
import $api from "@/api/Axios";
import type { Response } from "@/types/index";
import { useUserStore } from "./stores/modules/user";
function useTable(app: any) {
    app.use(VXETable);

    // 给 vue 实例挂载内部对象，例如：
    // app.config.globalProperties.$XModal = VXETable.modal
    // app.config.globalProperties.$XPrint = VXETable.print
    // app.config.globalProperties.$XSaveFile = VXETable.saveFile
    // app.config.globalProperties.$XReadFile = VXETable.readFile
}
app.use(useTable);

app.mount("#app");

// 判断是否是已经登录刷新
// if (
//     window.sessionStorage.getItem("access_token") != "" &&
//     !location.href.includes("loginType=sso") &&
//     !location.href.includes("code=")
// ) {
//     console.log("登录刷新");
//     $api.get("/auth/api/web/v1/refresh/userInfo ").then((res: Response) => {
//         if (res.code === 200) {
//             // 重新获取用户信息
//             const userStore = useUserStore();
//             userStore.getUserInfo();
//         }
//     });
// }

// 获取项目的域名地址
// console.log(window.location.protocol, "协议");
// console.log(window.location.host, "domain");
window.sessionStorage.setItem(
    "baseUrl",
    `${window.location.protocol}//${window.location.host}`
);
