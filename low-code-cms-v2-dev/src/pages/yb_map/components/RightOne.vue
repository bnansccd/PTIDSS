<template>
    <div class="right-one">
        <div class="right-one-title">
            <div class="title-icon"></div>
            <div class="title-text">今日告警</div>
        </div>
        <div class="empty-box" v-if="response.list.length == 0">
            <el-empty description="暂无报警" :image-size="80" />
        </div>
        <MapWrapScroll class="WrapScroll" v-else>
            <WarnCard :warn-obj="warnObj"></WarnCard>
            <WarnCard :warn-obj="warnObj2"></WarnCard>
            <WarnCard :warn-obj="warnObj3"></WarnCard>
            <WarnCard :warn-obj="warnObj"></WarnCard>
            <WarnCard :warn-obj="warnObj"></WarnCard>
            <WarnCard :warn-obj="warnObj"></WarnCard>
        </MapWrapScroll>
    </div>
</template>

<script lang="ts" setup>
import { reactive } from "vue";
import MapWrapScroll from "./MapWrapScroll.vue";
import WarnCard, { type WarnParams } from "./WarnCard.vue";
const response = reactive({
    list: [{}],
});

const warnObj: WarnParams = {
    name: "烟雾告警",
    level: "1",
    levelName: "一级",
    address: "东郊充电场站",
    time: "2023.03.23 12:00:04",
    id: "0010110",
};
const warnObj2: WarnParams = {
    name: "烟雾告警",
    level: "2",
    levelName: "二级",
    address: "东郊充电场站",
    time: "2023.03.23 12:00:04",
    id: "0010110",
};
const warnObj3: WarnParams = {
    name: "烟雾告警",
    level: "3",
    levelName: "三级",
    address: "东郊充电场站",
    time: "2023.03.23 12:00:04",
    id: "0010110",
};
</script>

<style lang="scss" scoped>
.right-one {
    width: 324px;
    // min-height: 455px;
    background: #ffffff;
    border-radius: 12px 12px 12px 12px;
    padding: 22px 0 22px 18px;
}

.right-one-title {
    width: 100%;
    //  background: #726767;
    height: 20px;
    display: flex;
    align-items: center;

    .title-icon {
        width: 12px;
        height: 12px;
        background: #41c980;
        border-radius: 3px 3px 9px 3px;
        // opacity: 0.5;
    }
    .title-text {
        font-size: 14px;
        font-family: Source Han Sans CN-Bold, Source Han Sans CN;
        font-weight: bold;
        color: #051838;
        line-height: 21px;
        margin-left: 6px;
    }
}

.empty-box {
    width: 100%;
    height: 180px;
}

.WrapScroll {
    width: 100%;
    height: 300px;
}
</style>
