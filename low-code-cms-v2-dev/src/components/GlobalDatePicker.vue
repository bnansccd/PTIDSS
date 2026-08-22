<script setup lang="ts">
const dateArr = ref<Array<string>>([]);
const props = defineProps({
    startTime: { type: String },
    endTime: { type: String },
});

watch(
    () => props.startTime,
    (newVal) => {
        dateArr.value = [props.startTime!, props.endTime!];
    },
    { deep: true, immediate: true }
);
const emit = defineEmits(["update:startTime", "update:endTime"]);
const onChange = (val: any) => {
    console.log(val);

    if (val) {
        // console.log("val...");
        // const sDate = new Date(val[0]);
        // const sTime = `${sDate.getFullYear()}-${
        //     sDate.getMonth() + 1
        // }-${sDate.getDate()} 00:00:00`;
        // const eDate = new Date(val[1]);
        // const eTime = `${eDate.getFullYear()}-${
        //     eDate.getMonth() + 1
        // }-${eDate.getDate()} 00:00:00`;
        // emit("update:startTime", sTime);
        // emit("update:endTime", eTime);
        emit("update:startTime", val[0]);
        emit("update:endTime", val[1]);
    } else {
        emit("update:startTime", "");
        emit("update:endTime", "");
    }
};
</script>

<template>
    <el-date-picker
        :="$attrs"
        v-model="dateArr"
        type="daterange"
        range-separator="至"
        start-placeholder="开始时间"
        end-placeholder="结束时间"
        size="default"
        @change="onChange"
    />
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { defineComponent, ref, watch } from "vue";
export default defineComponent({
    title: "全局日期选择",
    name: "GlobalDatePicker",
});
</script>
