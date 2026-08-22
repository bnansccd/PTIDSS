<template>
    <GlobalElDialog title="解除用户账号锁定" :="$attrs" @close="$emit('close')">
        <el-button style="margin-bottom: 10px;" type="primary" @click="secureUserAllLock()">一键解除所有账号</el-button>
       <div  v-loading="response.loading">
        <vxe-table
            ref="refVxeTableDom"
            align="center"
            :data="response.records"
            :row-config="{ isCurrent: true, isHover: true }"
            size="medium"
            border="inner"
            show-overflow
            height="300"
            :auto-resize="true"
            :sync-resize="true"
        >
            <vxe-column title="账号" field="name"></vxe-column>
            <vxe-column field="" title="操作" width="250" fixed="right">
                <template #default="{ row }">
                <el-button type="primary" link @click="secureUserLock(row)">解除锁定</el-button>
                </template>
            </vxe-column>
        </vxe-table>
        </div>
    </GlobalElDialog>
</template>
<script setup lang="ts">
import { reactive, onBeforeMount } from 'vue'
import $api from "@/api/Axios";
import { ElMessage } from "element-plus";
const response = reactive({
    records: [],
    loading: false
})
const api:string = `/system/api/web/v1/sysUser/removeLock`
const fetchList = () => {
    response.loading = true
    $api.get(api).then((res:any) => {
        response.loading = false
        if (res.code == 200) {
            response.records = (res.data || []).map((item: string) => {
                return{
                    name: item
                }
            })
        }
    })
}
const secureUserLock = (row: any) => {
    $api.post(`${api}/${row.name}`).then((res:any) => {
        if (res.code == 200) {
            ElMessage.success('解锁成功')
            fetchList()
        }
    })
}

const secureUserAllLock = () => {
    $api.post(`${api}`).then((res:any) => {
        if (res.code == 200) {
            ElMessage.success('全部解锁成功')
            fetchList()

        }
    })
}

onBeforeMount(() => {
    fetchList()
})
</script>

<style scoped lang="scss">
.GlobalElPagination-11 {
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>