import { fileURLToPath, URL } from "node:url";
import { loadEnv } from "vite";
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueJsx from "@vitejs/plugin-vue-jsx";

// import postcsspxtoviewport from "postcss-px-to-viewport";
import mkcert from "vite-plugin-mkcert";
import legacy from "@vitejs/plugin-legacy";

// https://vitejs.dev/config/
export default ({ mode }: any) =>
    defineConfig({
        // base: "/ybyx-cms-web",
        //base: "/",
        base: loadEnv(mode, process.cwd()).VITE_APP_NAME,
        plugins: [
            vue(),
            legacy({
                targets: ["defaults", "not IE 11"],
            }),
            vueJsx(),
            mkcert(),
        ],
        resolve: {
            alias: {
                "@": fileURLToPath(new URL("./src", import.meta.url)),
            },
        },
        build: {
            outDir: "dist/",
            target: "es2015",
        },
        server: {
            https: false,
            host: "0.0.0.0", // 需要开启https服务
            port: 30001,
            // 开发机 inotify watch 配额被 IDE 占用，禁用文件监听（编译服务不受影响，改动需重启 dev server）
            watch: null,
            proxy: {
                "/proxy_api": {
                    secure: false,
                   // target: "http://10.0.21.153:8081",
                     target: "https://jt-app.sznanjiang.com:11443/lc",
                    // target: "https://gajt.gazhcs.com:11433/lc", //http://192.168.88.166:8081/swagger-ui/index.html#/
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/proxy_api/, ""),
                    headers: {
                        // 自定义请求头
                        // "X-Custom-Header": "foobar",
                        // 修改Origin（虽然changeOrigin: true已经会自动修改）
                        Origin: "https://gajt.gazhcs.com",
                        // Origin: "https://jt-app.sznanjiang.com",
                    },
                },
            },
        },
        css: {
            postcss: {
                plugins: [
                    // postcsspxtoviewport({
                    //     unitToConvert: "px", // 要转化的单位
                    //     viewportWidth: 1920, // UI设计稿的宽度
                    //     unitPrecision: 10, // 转换后的精度，即小数点位数
                    //     propList: ["*"], // 指定转换的css属性的单位，*代表全部css属性的单位都进行转换
                    //     // viewportUnit: "vw", // 指定需要转换成的视窗单位，默认vw
                    //     // fontViewportUnit: "vw", // 指定字体需要转换成的视窗单位，默认vw
                    //     // selectorBlackList: ["el-message__icon"], // 指定不转换为视窗单位的类名，
                    //     minPixelValue: 1, // 默认值1，小于或等于1px则不进行转换
                    //     mediaQuery: true, // 是否在媒体查询的css代码中也进行转换，默认false
                    //     replace: true, // 是否转换后直接更换属性值
                    //     // exclude: [/node_modules/], // 设置忽略文件，用正则做目录名匹配
                    //     // exclude: [],
                    //     // landscape: false, // 是否处理横屏情况
                    // }),
                ],
            },
        },
    });
