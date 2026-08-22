<script setup lang="ts">
// import TheWelcome from "../components/TheWelcome.vue";
import DefaultTheme from "./defaultTheme/HomeView.vue";
</script>

<template>
    <DefaultTheme />
</template>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "主题分发",
    name: "HomeView",
});
</script>
