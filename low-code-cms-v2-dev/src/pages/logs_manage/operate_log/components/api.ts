import type { Response } from "@/types/index";
import $api from "@/api/Axios";

import { ApibaseURL } from "../api";

export async function apiGetDetails(id: string) {
    let response: Response = await $api.get(`${ApibaseURL}/${id}`);
    let { code, data } = response;
    if (code === 200) {
        return data;
    }
}
