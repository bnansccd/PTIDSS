## UI库

[https://www.naiveui.com/zh-CN/os-theme](https://www.naiveui.com/zh-CN/os-theme)


## 像素转 rem 的库

```shell
    npm i postcss-px-to-viewport -D
```

> 文档：https://github.com/evrone/postcss-px-to-viewport/blob/HEAD/README_CN.md

> 单位换算   设计稿 width: 1920px

>> 1920px == 100vw   1vw = 19.2px

>> 1px = 100/1920 = 0.05208333333333333333333333

>> 100px = 5.2083333333333333333 vw

>> 1rem = 100px

>> style="font-size:5.2083333333333333333 vw"

>> 1rm = 14px    14px/1920px = x/100vw  1rm === 0.7291666666vw
