import { reactive, onBeforeMount, ref } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import qs from "qs";
import $api from "@/api/Axios";
// import { filterRecords } from "@/tools/function";
import type { Response, BaseParams } from "@/types/index";

export const ApibaseURL = "/system";

interface AjaxParams extends BaseParams {
    userId: string | null; //病害分类名称
    username: string | null;
}

const initParams: AjaxParams /*初始化入参*/ = {
    current: 1,
    size: Number(window.sessionStorage.getItem("Globalpagesize")) || 10,
    userId: null,
    username: null,
    // name: "",
    // orderByDTOS: [{ asc: true, column: "sort" }],
};

export function useOpListData(props: any) {
    const refVxeTableDom = ref();
    const xToolbarDom = ref();
    const phone = ref("");
    const loading = ref<boolean>(false);

    const ajaxParams = reactive({ ...initParams });
    const response: any = reactive({
        records: [],
        total: 0,
    });
    async function getGZT() {
        await $api
            .get(`/system/api/web/v1/sysUser/detail/${props.sysUserVO.id}`)
            .then(async (res1) => {
                phone.value = res1.data.sysUserVO.phone;
            });
    }
    async function fetchList() {
        ajaxParams.userId = props.id;
        const res: Response = await $api.get(
            `/roadSystem/api/web/v1/sso/user`,
            {
                params: ajaxParams,
                paramsSerializer: (ajaxParams) =>
                    encodeURI(
                        qs.stringify(ajaxParams, {
                            allowDots: true,
                            encode: false,
                        })
                    ),
            }
        );
        const { code, data } = res;
        if (code === 200) {
            response.total = data.total * 1;
            response.records = (data.records || []).map((item: any) => {
                // item.gztOpenId = gztOpenId.value
                return {
                    ...item,
                };
            });
            // response.records = data.records;
        }
    }

    const opInit = {
        show: false,
        code: "init", // "look" "edit" "add"
        id: "0", //id为0时表示添加
    };
    const opObject = reactive({
        ...opInit,
    });
    const close = () => /* 关闭模态框 */ {
        Object.assign(opObject, opInit);
    };

    const reset = () => /* 重置按钮 */ {
        Object.assign(ajaxParams, initParams);
        fetchList();
    };

    const fetchDel = (val: string | Array<string>) => {
        let ids: string | Array<string>;
        if (Array.isArray(val)) {
            ids = refVxeTableDom.value
                ?.getCheckboxRecords(true)
                .map((item: { id: string }) => item.id);
            if (ids.length === 0) {
                ElMessage({
                    message: "请选择数据后进行操作",
                    type: "warning",
                    center: true,
                });
                return;
            }
        } else {
            ids = val;
        }
        ElMessageBox.confirm("确定要删除吗？请谨慎操作！", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
            center: true,
        })
            .then(async () => {
                const response: Response = await $api.delete(
                    `/roadSystem/api/web/v1/sso/user/${"" + ids}`
                );
                const { code } = response;
                if (code === 200) {
                    ElMessage({
                        message: "删除成功",
                        type: "success",
                        center: true,
                    });
                    fetchList();
                }
            })
            .catch(() => {
                /* */
            });
    };

    onBeforeMount(async () => {
        await getGZT();
        await fetchList();
    });

    return {
        refVxeTableDom,
        xToolbarDom,
        opObject,
        fetchList,
        fetchDel,
        close,
        reset,
        ajaxParams,
        response,
        loading,
        phone,
    };
}
