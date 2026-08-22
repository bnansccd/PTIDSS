<script lang="ts" setup>
import img1 from "../imgs/one.png";
import img2 from "../imgs/two.png";
import img3 from "../imgs/three.png";
interface Ranking {
    area: string;
    process: string;
    count: string;
    no: string | number;
}

const props = defineProps<{
    rank: Ranking;
}>();
</script>
<template>
    <div class="bar-item">
        <img :src="img1" v-if="rank.no == 1" class="bar-no-img" />
        <img :src="img2" v-if="rank.no == 2" class="bar-no-img" />
        <img :src="img3" v-if="rank.no == 3" class="bar-no-img" />
        <div v-if="rank.no > 3" class="bar-no-img">{{ rank.no }}</div>

        <div class="bar-label">{{ rank.area }}</div>
        <div class="process-box">
            <div
                class="process"
                v-if="rank.no == 1"
                :style="{ width: rank.process }"
            ></div>
            <div
                class="process-2"
                v-if="rank.no == 2"
                :style="{ width: rank.process }"
            ></div>
            <div
                class="process-3"
                v-if="rank.no == 3"
                :style="{ width: rank.process }"
            ></div>
            <div
                class="process-4"
                v-if="rank.no > 3"
                :style="{ width: rank.process }"
            ></div>
        </div>
        <div class="bar-count">{{ rank.count }}</div>
    </div>
</template>
<style lang="scss" scoped>
.bar-item {
    width: 100%;
    height: 30px;
    // height: 30px;
    //  background: rgb(231, 231, 57);
    display: flex;
    align-items: center;
    margin: 7px 0;
}
.bar-no-img {
    width: 24px;
    height: 24px;
    text-align: center;
    font-size: 14px;
    font-family: Source Han Sans CN-Regular, Source Han Sans CN;
    font-weight: 400;
    color: #767676;
    line-height: 30px;
}
.bar-label {
    width: 150px;
    font-size: 14px;
    font-family: Source Han Sans CN-Regular, Source Han Sans CN;
    font-weight: 400;
    color: #767676;
    line-height: 21px;
    text-align: center;
}

.process-box {
    width: 300px;
    height: 14px;
    //  background: red;
}

.process {
    height: 100%;
    background: linear-gradient(90deg, #f6e017 0%, #fdb12f 100%);
    border-radius: 0px 7px 7px 0px;
}
.process-2 {
    height: 100%;
    background: linear-gradient(90deg, #93b6e8 0%, #5a6ec2 100%);
    border-radius: 0px 7px 7px 0px;
}
.process-3 {
    height: 100%;
    background: linear-gradient(90deg, #f6a917 0%, #fd6e2f 100%);
    border-radius: 0px 7px 7px 0px;
}
.process-4 {
    height: 100%;
    background: linear-gradient(90deg, #41c980 0%, #17b1b7 100%);
    border-radius: 0px 7px 7px 0px;
}

.bar-count {
    font-size: 14px;
    font-family: Source Han Sans CN-Regular, Source Han Sans CN;
    font-weight: 400;
    color: #41c980;
    line-height: 21px;
    margin-left: 32px;
    width: 50px;
}
</style>
<script lang="ts">
import { defineComponent, defineProps } from "vue";
export default defineComponent({
    title: "",
    name: "BarItem",
});
</script>
