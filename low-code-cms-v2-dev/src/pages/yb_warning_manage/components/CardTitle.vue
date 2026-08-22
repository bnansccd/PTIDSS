<template>
    <div class="right-one-title">
        <div class="title-icon"></div>
        <div class="title-text">{{ title }}</div>
    </div>
</template>

<script lang="ts" setup>
const props = defineProps<{
    title: string;
}>();
</script>

<style lang="scss" scoped>
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
</style>
