<template>
    <div class="top-one">
        <CardTitle title="告警分布"></CardTitle>
        <div class="content-box">
            <div>
                <div class="t-label">告警总数</div>
                <div class="t-val">2000</div>
            </div>
            <div style="width: 1px; height: 45px; background: #eeeeee"></div>
            <div>
                <div class="t-label">待处置</div>
                <div class="t-val" style="color: #f6a917">20</div>
            </div>
            <div style="width: 1px; height: 45px; background: #eeeeee"></div>
            <div>
                <div class="t-label">已处置</div>
                <div class="t-val" style="color: #41c980">2000</div>
            </div>
            <div style="width: 1px; height: 45px; background: #eeeeee"></div>
            <div>
                <div class="t-label">处理率</div>
                <div class="t-val" style="color: #9ba0eb">65%</div>
            </div>
        </div>

        <div class="card-box">
            <div class="card-item">
                <div class="card-title">告警平均响应时长</div>
                <div>
                    <span class="card-val">4.6</span
                    ><span class="card-unit">分钟</span>
                </div>
            </div>
            <div class="card-item-2">
                <div class="card-title">告警平均响应时长</div>
                <div>
                    <span class="card-val">4.6</span
                    ><span class="card-unit">分钟</span>
                </div>
            </div>
            <div class="card-item-3">
                <div class="card-title">告警平均响应时长</div>
                <div>
                    <span class="card-val">4.6</span
                    ><span class="card-unit">分钟</span>
                </div>
            </div>
            <div class="card-item-4">
                <div class="card-title">告警平均响应时长</div>
                <div>
                    <span class="card-val">4.6</span
                    ><span class="card-unit">分钟</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import CardTitle from "./CardTitle.vue";
</script>

<style lang="scss" scoped>
.top-one {
    width: 100%;
    height: 100%;

    .content-box {
        width: 100%;
        display: flex;
        justify-content: space-around;
        margin-top: 20px;
        align-items: center;
    }

    .card-box {
        width: 100%;
        display: flex;
        margin-top: 20px;
        flex-wrap: wrap;
        justify-content: space-between;

        .card-item {
            width: calc(50% - 10px);
            height: 110px;
            background-image: url("../imgs/card1.png");
            background-size: 100% 100%;
            background-repeat: no-repeat;
            margin-bottom: 20px;
        }
        .card-item-2 {
            width: calc(50% - 12px);
            height: 110px;
            background-image: url("../imgs/card2.png");
            background-size: 100% 100%;
            background-repeat: no-repeat;
            margin-bottom: 20px;
        }
        .card-item-3 {
            width: calc(50% - 12px);
            height: 110px;
            background-image: url("../imgs/card3.png");
            background-size: 100% 100%;
            background-repeat: no-repeat;
            margin-bottom: 20px;
        }
        .card-item-4 {
            width: calc(50% - 12px);
            height: 110px;
            background-image: url("../imgs/card4.png");
            background-size: 100% 100%;
            background-repeat: no-repeat;
            margin-bottom: 20px;
        }
        .card-title {
            font-size: 14px;
            font-family: Source Han Sans CN-Regular, Source Han Sans CN;
            font-weight: 400;
            color: #ffffff;
            margin: 20px 0 10px 34px;
        }
        .card-val {
            font-size: 26px;
            font-family: Source Han Sans CN-Bold, Source Han Sans CN;
            font-weight: bold;
            color: #ffffff;
            margin: 0px 0 16px 34px;
        }
        .card-unit {
            font-size: 14px;
            font-family: Source Han Sans CN-Regular, Source Han Sans CN;
            font-weight: 400;
            color: #ffffff;
            margin-left: 16px;
        }
    }
}

.t-label {
    font-size: 14px;
    font-family: Source Han Sans CN-Regular, Source Han Sans CN;
    font-weight: 400;
    color: #051838;
}
.t-val {
    font-size: 26px;
    font-family: Source Han Sans CN-Bold, Source Han Sans CN;
    font-weight: bold;
    color: #fd2f60;
}
</style>
