<script setup lang="ts"></script>

<template>
    <el-input
        class="search-input"
        :="$attrs"
        @input="input"
        @keydown.enter="keydown"
    />
</template>

<style scoped lang="scss">
.search-input {
    width: 200px;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "全局模糊搜索",
    name: "GlobalSearchInput",
    data(): any {
        return {
            timer: null,
        };
    },
    methods: {
        input(val: any) {
            // let that = this;
            // if (!this.timer) {
            //     this.timer = setTimeout(() => {
            //         this.$emit("query");
            //         clearTimeout(that.timer);
            //         this.timer = null;
            //     }, 600);
            // }
        },

        keydown() {
            this.$emit("query");
            if (this.timer) {
                clearTimeout(this.timer);
                this.timer = null;
            }
        },
    },
});
</script>
