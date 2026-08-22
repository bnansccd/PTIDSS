import { defineStore } from "pinia";
import $api from "@/api/Axios";
import { ApibaseURL } from "@/pages/system_manage/data_dictionary/api";
import type { Response } from "@/types/index";
import { toRaw } from "vue";
export const useDataDict = defineStore("dataDict", {
    state: () => {
        return {
            dict: {},
        };
    },
    actions: {
        async getDictList(parentType: string) {
            if (
                Array.isArray(this.dict[parentType]) &&
                this.dict[parentType].length > 0
            ) {
                return this.dict[parentType];
            } else {
                return await this.getDictListApi(parentType);
            }
        },

        getDictListValbyId(parentType: string, id: string) {
            let arr: any = [];
            if (id == "--") {
                return "--";
            }

            arr = toRaw(this.dict[parentType]);
            const res = arr.filter((item: any) => item.dictType == id)[0];
            return res.dictName;
        },

        getDictListApi(parentType: string) {
            return new Promise((resolve, reject) => {
                $api.get(`${ApibaseURL}/parentType/${parentType}`)
                    .then((response: Response) => {
                        const { code, data } = response;
                        if (code === 200) {
                            this.dict[parentType] = data;
                            console.log(
                                this.dict[parentType],
                                "this.dict[parentType]"
                            );
                            resolve(data);
                        }
                    })
                    .catch((err) => {
                        reject(err);
                    });
            });
        },
    },
});
