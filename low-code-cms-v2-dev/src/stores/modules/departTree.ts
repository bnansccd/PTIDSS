import { defineStore } from "pinia";
import { apiGetListTree } from "@/pages/organization_structure/depart_manage/api";

import $api from "@/api/Axios";
export const usedepartTreeStore = defineStore("departTree", {
    state: () => {
        return {
            departTree: [],
            caremaTree: [],
        };
    },

    actions: {
        async getDepartTree() {
            if (Array.isArray(this.departTree) && this.departTree.length > 0) {
                return this.departTree;
            } else {
                const records: any = await apiGetListTree();
                this.departTree = records;
                return this.departTree;
            }
        },
        async getCaremaDepartTree() {
            if (Array.isArray(this.caremaTree) && this.caremaTree.length > 0) {
                return this.caremaTree;
            } else {
                const records: any = await $api.get(
                    "/dataAccess/api/web/v1/hkDevice/getTree"
                );
                console.log(records);
                this.caremaTree = records.data;
                return this.caremaTree;
            }
        },
    },
});
