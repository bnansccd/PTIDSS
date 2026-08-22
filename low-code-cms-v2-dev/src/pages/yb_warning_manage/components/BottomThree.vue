<template>
    <div class="bottom-two">
        <CardTitle title="告警趋势"></CardTitle>
        <div>
            <div v-for="(item, index) in rankData" :key="index">
                <BarItem :rank="item" />
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import CardTitle from "./CardTitle.vue";
import BarItem from "./BarItem.vue";
interface Ranking {
    area: string;
    process: string;
    count: string;
    no: string | number;
}
const rankData: Array<Ranking> = [
    {
        area: "武侯区",
        process: "90%",
        count: "8项",
        no: 1,
    },
    {
        area: "锦江区",
        process: "80%",
        count: "6项",
        no: 2,
    },
    {
        area: "金牛区",
        process: "70%",
        count: "5项",
        no: 3,
    },
    {
        area: "青羊区",
        process: "60%",
        count: "4项",
        no: 4,
    },
    {
        area: "郫都区",
        process: "40%",
        count: "1项",
        no: 5,
    },
    {
        area: "温江区",
        process: "40%",
        count: "1项",
        no: 6,
    },
    {
        area: "新津区",
        process: "40%",
        count: "1项",
        no: 7,
    },
    {
        area: "龙泉驿区",
        process: "40%",
        count: "1项",
        no: 8,
    },
    {
        area: "天府新区",
        process: "40%",
        count: "1项",
        no: 9,
    },
    {
        area: "青白江区",
        process: "40%",
        count: "1项",
        no: 10,
    },
];
</script>

<style lang="scss" scoped>
.bottom-two {
    width: 550px;
    height: 380px;
    //  background: #4e4a4a2d;
    padding: 20px 0;
}
</style>
