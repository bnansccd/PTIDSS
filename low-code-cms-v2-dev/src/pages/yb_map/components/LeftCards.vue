<template>
    <div class="left-cards">
        <ItemCard
            :img-url="url1"
            title="充电场站"
            :car-obj="carObj"
            @click="onChange(1)"
            :style="{ background: activeIndex == 1 ? '#e8f8ef' : '#ffffff' }"
        ></ItemCard>
        <ItemCard
            :img-url="url2"
            title="出租车场站"
            :car-obj="carObj"
            @click="onChange(2)"
            :style="{ background: activeIndex == 2 ? '#e8f8ef' : '#ffffff' }"
        ></ItemCard>
        <ItemCard
            :img-url="url3"
            title="出租车"
            :car-obj="carObj"
            @click="onChange(3)"
            :style="{ background: activeIndex == 3 ? '#e8f8ef' : '#ffffff' }"
        ></ItemCard>

        <LeftTree v-if="activeIndex != 0" @chose="onChoseNode"></LeftTree>
    </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import ItemCard, { type CarParams } from "./ItemCard.vue";
import LeftTree from "./LeftTree.vue";
import url1 from "@/pages/yb_map/imgs/car1.png";
import url2 from "@/pages/yb_map/imgs/car2.png";
import url3 from "@/pages/yb_map/imgs/car3.png";

const carObj: CarParams = {
    num1: 10,
    num2: 90,
    sumCount: 20,
};
const emit = defineEmits(["onChose", "onChoseNode"]);
const activeIndex = ref(0);

const onChange = (index: number) => {
    activeIndex.value = index;
    emit("onChose", index);
};
// 树节点被点击
const onChoseNode = (node: any) => {
    emit("onChoseNode", node);
};
</script>

<style lang="scss" scoped>
.left-cards {
    width: 276px;
    // min-height: 234px;
    background: #ffffff;
    border-radius: 12px 12px 12px 12px;
    padding: 18px 12px;
    //   transition: height 0.5s;
}
</style>
