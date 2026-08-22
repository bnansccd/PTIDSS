<script setup lang="ts">
import { ref } from "vue";
import { apiGetListTree } from "../../api";

let options: any = ref([]);

let count = 0;
function recursion(arr: Array<any>) {
    count = count + 1;
    arr.forEach((item) => {
        let str = "";
        for (let i = 0; i < count; i++) {
            str += "&nbsp;&nbsp;&nbsp;&nbsp;";
        }
        item.label = item.menuName;
        item.menuName = str + item.menuName;
        options.value.push(item);

        if (item.children) {
            recursion(item.children);
        }
    });
    count = count - 1;
}

async function getTreeList() {
    options.value = [];
    let records: any = await apiGetListTree();
    recursion(records);
}

getTreeList();
</script>

<template>
    <el-select :="$attrs" filterable clearable>
        <el-option
            v-for="item in options"
            :key="item.id"
            :label="item.label"
            :value="item.id"
        >
            <span style="float: left" v-html="item.menuName"></span>
        </el-option>
    </el-select>
</template>

<style scoped lang="scss"></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "父级菜单选则",
    name: "ParentTreeSelect",
});
</script>
