<template>
    <div class="right-two">
        <div class="right-two-title">
            <div class="title-icon"></div>
            <div class="title-text">监控视频</div>
            <el-icon
                @click="onClose"
                class="close-btn"
                color="#41c980"
                :size="16"
                ><Close
            /></el-icon>
        </div>
        <div class="empty-box">
            <el-empty description="暂无数据" :image-size="80" />
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive } from "vue";
import MapWrapScroll from "./MapWrapScroll.vue";

const emits = defineEmits(["close"]);

const onClose = () => {
    emits("close");
};
</script>

<style lang="scss" scoped>
.right-two {
    width: 324px;
    height: calc(100vh - 200px);
    background: #ffffff;
    border-radius: 12px 12px 12px 12px;
    padding: 22px 0 22px 18px;
}

.right-two-title {
    width: 100%;
    //  background: #726767;
    height: 20px;
    display: flex;
    align-items: center;
    position: relative;

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

    .close-btn {
        position: absolute;
        right: 20px;
        top: 5px;
        cursor: pointer;
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
