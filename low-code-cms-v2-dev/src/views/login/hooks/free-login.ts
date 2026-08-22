import { onBeforeMount } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
import { useSysConfigStore } from "@/stores/modules/sysConfig";
import $api from "@/api/Axios";
import type { MenuResponseParams, Response, ConfigParams } from "@/types/index";

function getQueryString(name: string) {
    const reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    const r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return decodeURIComponent(r[2]);
    }
    return null;
}

export function useFreelogin() {
    onBeforeMount(async () => {
        const router = useRouter();
        const userStore = useUserStore();
        const tokon = getQueryString("focus-open-code");

        if (tokon) {
            const { code, data }: any = await $api.get(
                `/roadSystem/api/web/v1/gzt/code?code=${tokon}`
            );
            if (code == 200) {
                console.log("data=", data);
                userStore.freeloginXXX(data.access_token);

                {
                    const respon: Response =
                        (await userStore.getUserInfo()) as Response;

                    const { code, data } = respon;
                    if (code == 200) {
                        const sysMenuVOS =
                            data.sysMenuVOS as Array<MenuResponseParams>;
                        if (
                            Array.isArray(sysMenuVOS) &&
                            sysMenuVOS.length > 0
                        ) {
                            let menu: MenuResponseParams = {
                                href: "",
                                icon: function (): void {
                                    throw new Error(
                                        "Function not implemented."
                                    );
                                },
                                id: "",
                                label: function () {
                                    throw new Error(
                                        "Function not implemented."
                                    );
                                },
                                menuType: "",
                            };
                            if (
                                Array.isArray(sysMenuVOS[0].children) &&
                                sysMenuVOS[0].children.length > 0
                            ) {
                                menu = sysMenuVOS[0].children[0];
                            } else {
                                menu = sysMenuVOS[0];
                            }
                            const sysConfig = useSysConfigStore();
                            router.push({ path: menu.href });
                            sysConfig.menuActiveKey = menu.href;
                            sysConfig.tabs = [
                                { name: menu.href, title: menu.menuName },
                            ];
                            sysConfig.tabsValue = menu.href;
                        } else {
                            router.push({ path: "/code403" });
                        }
                    }
                }

                {
                    const res: Response =
                        (await userStore.getSysConfig()) as Response;
                    const records = (res.data as Array<ConfigParams>) || [];
                    // let tempArr: Array<ConfigParams> = [];
                    // records.forEach((item) => {
                    //     tempArr = [
                    //         ...tempArr,
                    //         ...(item.list as Array<ConfigParams>),
                    //     ];
                    // });
                    // records = tempArr;
                    localStorage.setItem(
                        "system_config",
                        JSON.stringify(records)
                    );
                    const sysConfig = useSysConfigStore();
                    sysConfig.systemConfig = records;
                }
            }
        }
    });

    return {};
}
