<template>
    <div class="bottom-one">
        <CardTitle title="告警趋势"></CardTitle>
        <div id="main"></div>
    </div>
</template>

<script lang="ts" setup>
import CardTitle from "./CardTitle.vue";

import * as echarts from "echarts";
import { nextTick, onMounted } from "vue";

type EChartsOption = echarts.EChartsOption;

function initChart() {
    const chartDom = document.getElementById("main")!;
    const myChart = echarts.init(chartDom);
    const option: EChartsOption = {
        xAxis: {
            type: "category",
            data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
        },
        yAxis: {
            type: "value",
            name: "件",
        },
        tooltip: {
            trigger: "axis",
            axisPointer: {
                type: "cross",
                label: {
                    backgroundColor: "#6a7985",
                },
            },
        },
        color: ["#FD2F60", "#41C980"],
        legend: {
            data: ["one", "two"],
        },
        series: [
            {
                name: "one",
                data: [820, 932, 901, 934, 1290, 1330, 1320],
                type: "line",
                smooth: true,
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        {
                            offset: 0,
                            color: "#db969f",
                        },
                        {
                            offset: 1,
                            color: "#e2bcbfc9",
                        },
                    ]),
                },
                emphasis: {
                    focus: "series",
                },
                lineStyle: {
                    color: "#FD2F60",
                    width: 1,
                },
                symbol: "none",
            },
            {
                name: "two",
                data: [920, 1632, 801, 734, 1090, 1030, 1320],
                type: "line",
                smooth: true,
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        {
                            offset: 0,
                            color: "#41C980",
                        },
                        {
                            offset: 1,
                            color: "#d7f4e5ba",
                        },
                    ]),
                },
                emphasis: {
                    focus: "series",
                },
                lineStyle: {
                    color: "#41C980",
                    width: 1,
                },
                symbol: "none",
            },
        ],
    };
    option && myChart.setOption(option);
}

onMounted(() => {
    nextTick(() => {
        initChart();
    });
});
</script>

<style lang="scss" scoped>
.bottom-one {
    width: 500px;
    height: 360px;
    //  background: #4e4a4a2d;
    padding: 20px 0;
}

#main {
    width: 500px;
    height: 360px;
}
</style>
