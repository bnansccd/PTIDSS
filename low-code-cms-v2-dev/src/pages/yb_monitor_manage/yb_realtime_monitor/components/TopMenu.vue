<template>
    <div class="top-menu">
        <div
            class="menu-item"
            :style="{ background: activeIndex == 1 ? '#41C980' : '#e8f8ef' }"
            @click="onClick(1)"
        >
            <img
                v-if="activeIndex == 1"
                src="../../../yb_map/imgs/car1b.png"
                class="img-logo"
            />
            <img v-else src="../../../yb_map/imgs/car1.png" class="img-logo" />
        </div>
        <div
            class="menu-item"
            :style="{ background: activeIndex == 2 ? '#41C980' : '#e8f8ef' }"
            @click="onClick(2)"
        >
            <img
                v-if="activeIndex == 2"
                src="../../../yb_map/imgs/car2b.png"
                class="img-logo"
            />
            <img v-else src="../../../yb_map/imgs/car2.png" class="img-logo" />
        </div>
        <div
            class="menu-item"
            :style="{ background: activeIndex == 3 ? '#41C980' : '#e8f8ef' }"
            @click="onClick(3)"
        >
            <img
                v-if="activeIndex == 3"
                src="../../../yb_map/imgs/car3b.png"
                class="img-logo"
            />
            <img v-else src="../../../yb_map/imgs/car3.png" class="img-logo" />
        </div>
    </div>
</template>

<script lang="ts" setup>
const props = defineProps<{
    activeIndex: number;
}>();
const emit = defineEmits(["chose"]);
const onClick = (index: number) => {
    emit("chose", index);
};
</script>

<style lang="scss" scoped>
.top-menu {
    width: 100%;
    display: flex;
    justify-content: space-around;
    margin-bottom: 10px;

    .menu-item {
        width: 50px;
        height: 50px;
        background: #e8f8ef;
        border-radius: 10px 10px 10px 10px;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;

        .img-logo {
            width: 34px;
            height: 34px;
        }
    }
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "顶部菜单",
    name: "TopMenu",
});
</script>
