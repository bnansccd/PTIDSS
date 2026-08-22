<template>
    <div class="item-card">
        <img class="icon-img" :src="imgUrl" />
        <div class="right-box">
            <div class="right-top-box">
                <div class="card-label">{{ title }}</div>
                <div class="card-val">{{ carObj.sumCount }}</div>
            </div>
            <div class="right-bottom-box">
                <img src="../imgs/监控green.png" class="r-b-icon" />
                <div class="r-b-right-box">
                    <div class="r-b-right-label">
                        在线<span class="r-b-right-val">{{ carObj.num1 }}</span>
                    </div>
                    <div class="r-b-right-label">
                        总数<span class="r-b-right-val">{{ carObj.num2 }}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
export interface CarParams {
    sumCount: number;
    num1: number;
    num2: number;
}
const props = defineProps<{
    imgUrl: string;
    title: string;
    carObj: CarParams;
}>();
</script>

<style lang="scss" scoped>
.item-card {
    width: calc(100% - 43px);
    height: 42px;
    border-radius: 10px 10px 10px 10px;
    padding: 14px 27px 14px 16px;
    display: flex;
    align-items: center;
    cursor: pointer;
    //  background: #e8f8ef;

    .icon-img {
        width: 40px;
        height: 40px;
    }

    .right-box {
        width: calc(100% - 52px);
        // background: #fff;
        height: 42px;
        margin-left: 12px;

        .right-top-box {
            width: 100%;
            height: 10px;
            background: linear-gradient(
                90deg,
                rgba(65, 201, 128, 0) 0%,
                #a9e5c5 26%,
                rgba(104, 211, 154, 0) 74%,
                rgba(65, 201, 128, 0) 100%
            );
            display: flex;
            justify-content: space-between;
        }
        .right-bottom-box {
            width: 100%;
            height: 20px;
            margin-top: 12px;
            display: flex;

            .r-b-icon {
                width: 20px;
                height: 20px;
                margin-left: 20px;
            }

            .r-b-right-box {
                width: calc(100% - 40px);
                //  background: #a6a8aa;
                display: flex;
                justify-content: space-between;

                .r-b-right-label {
                    font-size: 14px;
                    font-family: Source Han Sans CN-Regular, Source Han Sans CN;
                    font-weight: 400;
                    color: #838c99;
                    margin-left: 12px;
                }
                .r-b-right-val {
                    font-size: 14px;
                    font-family: Source Han Sans CN-Bold, Source Han Sans CN;
                    font-weight: bold;
                    color: #41c980;
                    margin-left: 8px;
                }
            }
        }
    }
}

.card-label {
    font-size: 14px;
    font-family: Source Han Sans CN-Regular, Source Han Sans CN;
    font-weight: 600;
    color: #051838;
    line-height: 10px;
    margin-left: 20px;
}
.card-val {
    font-size: 20px;
    font-family: Source Han Sans CN-Bold, Source Han Sans CN;
    font-weight: bold;
    color: #41c980;
    line-height: 10px;
}
</style>
