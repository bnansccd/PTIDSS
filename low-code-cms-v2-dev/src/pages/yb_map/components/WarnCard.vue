<template>
    <div class="warn-card">
        <div class="warn-card-top">
            <div class="warn-title">{{ warnObj.name }}</div>
            <div class="top-btn-box">
                <div class="top-btn">查看现场</div>
                <div class="top-btn">生成工单</div>
                <div class="top-btn">关闭</div>
            </div>
        </div>
        <div class="bottom-box">
            <div class="b-left-box">
                <div class="b-left-tag" :style="tagStyle">
                    {{ warnObj.levelName }}
                </div>
                <div class="b-left-label">{{ warnObj.address }}</div>
            </div>
            <div class="b-date">{{ warnObj.time }}</div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { computed } from "vue";
export interface WarnParams {
    name: string;
    level: string;
    levelName: string;
    address: string;
    time: string;
    id: string;
}

const props = defineProps<{
    warnObj: WarnParams;
}>();

const tagStyle = computed(() => {
    if (props.warnObj.level == "1") {
        return " background: #F6A917;";
    } else if (props.warnObj.level == "2") {
        return " background: #fd6e2f;";
    } else {
        return " background: #FD2F60;";
    }
});
</script>

<style lang="scss" scoped>
.warn-card {
    width: calc(100% - 18px);
    height: 41px;
    padding: 19px 0 14px 0;
    border-bottom: 1px solid #eeeeee;

    .warn-card-top {
        width: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .warn-title {
            width: 50%;
            font-size: 14px;
            font-family: Source Han Sans CN-Medium, Source Han Sans CN;
            font-weight: 500;
            color: #212121;
            line-height: 26px;
        }

        .top-btn-box {
            width: 50%;
            display: flex;
            justify-content: space-between;

            .top-btn {
                font-size: 12px;
                font-family: Source Han Sans CN-Regular, Source Han Sans CN;
                font-weight: 400;
                color: #41c980;
                line-height: 21px;
                cursor: pointer;
            }
        }
    }

    .bottom-box {
        width: 100%;
        display: flex;
        justify-content: space-between;

        .b-left-box {
            width: 50%;
            display: flex;
            align-items: center;
            .b-left-tag {
                width: 32px;
                height: 18px;
                background: #fd6e2f;
                border-radius: 4px 4px 4px 4px;
                font-size: 12px;
                font-family: Source Han Sans CN-Regular, Source Han Sans CN;
                font-weight: 400;
                color: #ffffff;
                line-height: 18px;
                text-align: center;
            }
            .b-left-label {
                font-size: 12px;
                font-family: Source Han Sans CN-Regular, Source Han Sans CN;
                font-weight: 400;
                color: #9b9b9b;
                line-height: 21px;
                margin-left: 10px;
            }
        }
        .b-date {
            width: 50%;
            font-size: 12px;
            font-family: Source Han Sans CN-Regular, Source Han Sans CN;
            font-weight: 400;
            color: #c4c4c4;
            line-height: 21px;
            text-align: right;
        }
    }
}
</style>
