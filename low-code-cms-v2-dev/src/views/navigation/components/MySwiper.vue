<template>
    <div class="swiper">
        <div
            class="swiper__container"
            :style="{
                transform:
                    'translateX(' + -state.currentIndex * slideWidth + 'px)',
            }"
        >
            <div
                class="swiper__wrapper"
                :style="{ width: slideWidth + 'px' }"
                v-for="slide in slides"
                :key="slide.id"
            >
                <img :src="slide.src" alt="" />
            </div>
        </div>
        <div class="swiper__nav">
            <div
                class="swiper__dot"
                v-for="(slide, index) in slides"
                :key="slide.id"
                :class="{ active: index === state.currentIndex }"
                @click="slideTo(index)"
            ></div>
        </div>
    </div>
</template>

<script lang="ts">
import { defineComponent, reactive } from "vue";

interface Slide {
    id: number;
    src: string;
}

export default defineComponent({
    name: "MySwiper",
    props: {
        slides: {
            type: Array as () => Slide[],
            required: true,
        },
        slideWidth: {
            type: Number,
            default: 320,
        },
        autoplay: {
            type: Boolean,
            default: false,
        },
        autoplayInterval: {
            type: Number,
            default: 3000,
        },
    },
    setup(props) {
        const state = reactive({
            currentIndex: 0,
            timerId: 0,
        });

        function slideTo(index: number) {
            if (index < 0 || index > props.slides.length - 1) {
                return;
            }
            state.currentIndex = index;
        }

        function next() {
            if (state.currentIndex === props.slides.length - 1) {
                slideTo(0);
            } else {
                slideTo(state.currentIndex + 1);
            }
        }

        function startAutoplay() {
            clearInterval(state.timerId);
            state.timerId = setInterval(next, props.autoplayInterval);
        }

        function stopAutoplay() {
            clearInterval(state.timerId);
        }

        function beginDrag(event: MouseEvent | TouchEvent) {
            event.preventDefault();
            stopAutoplay();
        }

        function endDrag(event: MouseEvent | TouchEvent) {
            event.preventDefault();
            startAutoplay();
        }

        return {
            state,
            slideWidth: props.slideWidth,
            slides: props.slides,
            slideTo,
            next,
            startAutoplay,
            stopAutoplay,
            beginDrag,
            endDrag,
        };
    },
    mounted() {
        if (this.autoplay) {
            this.startAutoplay();
        }
    },
    beforeUnmount() {
        this.stopAutoplay();
    },
});
</script>

<style>
.swiper {
    position: relative;
    width: 100%;
    overflow: hidden;
    cursor: pointer;
}
.swiper__container {
    display: flex;
    transition: transform 0.3s ease-out;
}
.swiper__wrapper {
    flex-shrink: 0;
    width: 100%;
}
.swiper__wrapper img {
    width: 100%;
    height: auto;
}
.swiper__nav {
    position: absolute;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
}
.swiper__dot {
    display: inline-block;
    width: 10px;
    height: 10px;
    margin: 0 5px;
    border-radius: 50%;
    background-color: #ccc;
    cursor: pointer;
    transition: background-color 0.3s ease-out;
}
.swiper__dot.active {
    background-color: #4caf50;
}
</style>
