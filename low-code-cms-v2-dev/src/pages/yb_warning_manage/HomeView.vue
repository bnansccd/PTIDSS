<template>
    <WrapScroll class="WrapScroll">
        <div class="yb-warning-manage">
            <div class="top-box">
                <WrapLayout class="left-box">
                    <TopOne></TopOne>
                </WrapLayout>
                <WrapLayout class="right-box">
                    <div style="min-width: 800px; height: 100%">
                        <TableData />
                    </div>
                </WrapLayout>
            </div>
            <WrapLayout class="bottom-box">
                <Bottom></Bottom>
            </WrapLayout>
        </div>
    </WrapScroll>
</template>

<script lang="ts" setup>
import TableData from "./TableData.vue";
import WrapLayout from "./components/WrapLayout.vue";
import WrapScroll from "./components/WrapScroll.vue";
import TopOne from "./components/TopOne.vue";
import Bottom from "./components/Bottom.vue";
</script>

<style lang="scss" scoped>
.WrapScroll {
    width: 100%;
    height: calc(100vh - 110px);
}
.yb-warning-manage {
    width: 100%;
    // height: calc(100% - 24px);

    padding-bottom: 24px;
    display: flex;
    flex-direction: column;
    //  background: #584b4b;

    .top-box {
        width: 100%;
        // flex: 1;
        height: 439px;

        display: flex;

        .left-box {
            width: 580px;
        }
        .right-box {
            flex: 1;
            // width: 1026px;
            margin-left: 0;
            padding: 0;
            height: calc(100% - 20px);
            overflow: auto !important;
        }
    }

    .bottom-box {
        // width: 100%;
        //  overflow-x: hidden;
        height: 439px;

        .b-content-box {
            min-width: 1500px;
            height: 100%;
            //  background: #916e6e;
            overflow-x: auto;
        }
    }
}

.yb-warning-manage::-webkit-scrollbar {
    width: 6px;
    height: 8px;
    background-color: transparent;
    /* 整个滚动条 */
}

.yb-warning-manage::-webkit-scrollbar-button {
    height: 0;
    /* 滚动条上的按钮（上下箭头）*/
}

.yb-warning-manage::-webkit-scrollbar-thumb {
    border-radius: 10px;
    width: 6px;
    height: 112px;
    background: #afe8ca;
    border-radius: 5px;
    /* 滚动条上的滚动滑块 */
}
</style>
