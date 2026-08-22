<template>
    <div class="Gw-table-WrapTableLayout">
        <div class="Gw-table-WrapTableLayout-wrap" ref="wrap">
            <div class="Gw-table-WrapTableLayout-header" ref="header">
                <!-- 只展示一部分 -->
                <el-collapse-transition @after-enter="onAfterEnter">
                    <div v-if="flag" class="Gw-table-WrapTableLayout-little">
                        <el-space wrap style="margin-right: 20px">
                            <slot name="header"></slot>
                        </el-space>

                        <div>
                            <el-space wrap>
                                <el-button-group>
                                    <el-button
                                        type="primary"
                                        @click="queryList"
                                        style="width: 92px"
                                        >搜索</el-button
                                    >
                                    <!-- <el-tooltip
                                        effect="dark"
                                        :content="
                                            flag
                                                ? `展开更多搜索条件`
                                                : `收起更多搜索条件`
                                        "
                                        placement="top"
                                    >
                                        <el-button
                                            v-if="more"
                                            @click="handleClick"
                                            type="primary"
                                        >
                                            <el-icon v-show="!flag"
                                                ><ArrowUpBold
                                            /></el-icon>
                                            <el-icon v-show="flag"
                                                ><ArrowDownBold
                                            /></el-icon>
                                        </el-button>
                                    </el-tooltip> -->
                                </el-button-group>
                                <el-button
                                    type="primary"
                                    plain
                                    @click="refreshParams"
                                    style="width: 92px"
                                    >重置</el-button
                                >
                            </el-space>
                        </div>
                    </div>
                </el-collapse-transition>

                <!-- 展示全部 -->
                <el-collapse-transition @after-enter="onAfterEnter">
                    <el-space wrap v-if="!flag" alignment="flex-end">
                        <slot name="header"></slot>
                        <slot name="drop"></slot>
                        <div
                            class="Gw-table-WrapTableLayout-button-group-empty"
                        ></div>
                        <div class="Gw-table-WrapTableLayout-button-group">
                            <el-space wrap>
                                <el-button-group>
                                    <el-button
                                        type="primary"
                                        :icon="Search"
                                        @click="queryList"
                                        >搜索</el-button
                                    >
                                    <el-tooltip
                                        effect="dark"
                                        :content="
                                            flag
                                                ? `展开更多搜索条件`
                                                : `收起更多搜索条件`
                                        "
                                        placement="top"
                                    >
                                        <el-button
                                            v-if="more"
                                            @click="handleClick"
                                            type="primary"
                                        >
                                            <el-icon v-show="!flag"
                                                ><ArrowUpBold
                                            /></el-icon>
                                            <el-icon v-show="flag"
                                                ><ArrowDownBold
                                            /></el-icon>
                                        </el-button>
                                    </el-tooltip>
                                </el-button-group>
                                <el-button
                                    :icon="Refresh"
                                    @click="refreshParams"
                                    >重置</el-button
                                >
                            </el-space>
                        </div>
                    </el-space>
                </el-collapse-transition>
            </div>

            <div class="Gw-table-WrapTableLayout-toolbar" ref="toolbar">
                <slot name="toolbar"></slot>
            </div>

            <div
                class="Gw-table-WrapTableLayout-table"
                :style="`height: ${height}px;`"
            >
                <slot />
            </div>
        </div>

        <div v-if="show" class="Gw-table-WrapTableLayout-pagination">
            <slot name="pagination"></slot>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.Gw-table-WrapTableLayout {
    height: calc(100% - 60px) !important;
    overflow: hidden;
    padding: 20px;
    display: flex;
    flex-direction: column;
    background: var(--sys-theme-warp-background);
    border-radius: 12px 12px 12px 12px;
    margin: 0px 20px 0 20px;

    .Gw-table-WrapTableLayout-wrap {
        flex-grow: 1;
        display: flex;
        flex-direction: column;

        .Gw-table-WrapTableLayout-header {
            flex-wrap: wrap;
            display: flex;
            width: 100%;
            justify-content: space-between;
            position: relative;

            .Gw-table-WrapTableLayout-button-group-empty {
                width: 180px;
                height: 20px;
                background-color: transparent;
            }

            .Gw-table-WrapTableLayout-button-group {
                position: absolute;
                bottom: 0;
                right: 0;
            }

            .Gw-table-WrapTableLayout-little {
                display: flex;
                width: 100%;
                justify-content: flex-start;
                :deep(.el-button--primary) {
                    background-color: var(
                        --sys-theme-btn-primary-background
                    ) !important;
                    --el-button-border-color: var(
                        --sys-theme-btn-primary-border-color
                    ) !important;
                    border: 1px solid
                        var(--sys-theme-btn-primary-hover-border-color) !important;
                }
                :deep(.el-button:hover) {
                    border: 1px solid
                        var(--sys-theme-btn-primary-hover-border-color) !important;
                }
                :deep(.el-button:focus, .el-button:hover) {
                    border: 1px solid
                        var(--sys-theme-btn-primary-hover-border-color) !important;
                }

                :deep(.el-button.is-plain) {
                    --el-button-hover-text-color: var(
                        --sys-theme-btn-primary-background
                    ) !important;
                    --el-button-hover-bg-color: #ffffff00 !important;
                    --el-button-hover-border-color: var(
                        --el-color-primary
                    ) !important;
                    background: #ffffff00 !important;
                    border: 1px solid
                        var(--sys-theme-btn-primary-hover-border-color) !important;
                    // color: var(--sys-theme-btn-plain-color) !important;
                }
            }
        }

        // .Gw-table-WrapTableLayout-toolbar {
        // }

        .Gw-table-WrapTableLayout-table {
            flex-grow: 1;
        }
    }

    .Gw-table-WrapTableLayout-pagination {
        margin-top: 10px;
        display: flex;
        justify-content: center;
    }
}

.Gw-table-WrapTableLayout ::-webkit-scrollbar {
    width: 0;
    /* 整个滚动条 */
}

::-webkit-scrollbar {
    width: 0;
    /* 整个滚动条 */
}
</style>

<script lang="ts">
import {
    Search, // 搜索
    Refresh,
} from "@element-plus/icons-vue";
import { defineComponent } from "vue";
export default defineComponent({
    title: "表格布局",
    name: "Gw-table-WrapTableLayout",
    props: {
        more: {
            //设置则显示更多搜索条件
            type: [Boolean, String],
            default: false,
        },
    },
    data(): any {
        return {
            flag: true,
            height: 0,

            wrap: null,
            header: null,
            toolbar: null,

            Maxheight: null,
        };
    },
    computed: {
        show() {
            return this.$slots.pagination ? true : false;
        },
        Refresh() {
            return Refresh;
        },
        Search() {
            return Search;
        },
    },
    mounted() {
        this.$nextTick(() => {
            this.wrap = this.$refs.wrap;
            this.header = this.$refs.header;
            this.toolbar = this.$refs.toolbar;
            this.height =
                this.wrap.getBoundingClientRect().height -
                (this.header.getBoundingClientRect().height +
                    this.toolbar.getBoundingClientRect().height);
        });
    },
    methods: {
        handleClick() {
            this.Maxheight = this.wrap.getBoundingClientRect().height;
            this.flag = !this.flag;
        },

        onAfterEnter() {
            this.height =
                this.Maxheight -
                (this.header.getBoundingClientRect().height +
                    this.toolbar.getBoundingClientRect().height);
        },

        queryList() {
            this.$emit("query");
        },

        refreshParams() {
            this.$emit("reset");
        },
    },
});
</script>
