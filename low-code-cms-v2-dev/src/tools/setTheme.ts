export const setCssVar = (
    prop: string,
    val: any,
    dom = document.documentElement
) => {
    dom.style.setProperty(prop, val);
};

export const setThemeOne = () => {
    setCssVar("--sys-theme-background", "#F4F7FF");
    setCssVar("--sys-theme-warp-background", "#ffffff");
    setCssVar(
        "--sys-theme-left-menu-background",
        "linear-gradient(180deg, #13cfff 0%, #108cfb 100%)"
    );
    setCssVar("--sys-theme-left-menu-color", "#ffffff");
    setCssVar("--sys-theme-left-menu-active-color", "#108cfb");
    setCssVar("--sys-theme-left-menu-active-background", "#f4f7ff");
    setCssVar("--sys-theme-btn-primary-background", "#108cfb");
    setCssVar("--sys-theme-btn-primary-border-color", "#108cfb");
    setCssVar("--sys-theme-btn-primary-hover-border-color", "#108cfb");
    setCssVar("--sys-theme-tab-active-color", "#108cfb");
    setCssVar("--sys-theme-pagination-active-color", "#108cfb");

    setCssVar("--sys-theme-table-header-wapper", "#F5FAFF");
    setCssVar("--sys-theme-table-row-hover", "#DFEFFF");
    setCssVar("--sys-theme-check-box-color", "#108cfb");

    setCssVar("--sys-theme-input-border-active-color", "#108cfb");
    setCssVar("--sys-theme-input-border-color", "#108cfb");
    setCssVar("--el-color-primary", "#108cfb");
    setCssVar("--el-color-primary-light-3", "#108cfb");
    setCssVar("--el-color-primary-dark-2", "#108cfb");
    setCssVar("--el-border-color-light", "#dcdfe6");
    setCssVar("--el-color-primary-light-9", "#DFEFFF");
    setCssVar("--el-color-primary-light-7", "#108cfb");

    setCssVar("--sys-theme-left-menu-option-active-background", "#DFEFFF");
    setCssVar("--sys-theme-left-menu-option-active-color", "#108cfb");
    setCssVar("--sys-theme-table-row-color", "#051838");
    setCssVar("--sys-theme-tab-active-background", "#ffffff");
    setCssVar("--sys-theme-table-loading-color", "#108cfb");
    setCssVar("--sys-theme-left-menu-option-color", "#626A71");
    setCssVar("--sys-theme-input-background", "#ffffff");
    setCssVar("--sys-theme-input-border-color", "#dcdfe6");
    setCssVar("--sys-theme-dialog-background", "#ffffff");
    setCssVar("--sys-theme-form-label-color", "#626a71");
    setCssVar("--sys-theme-btn-plain-color", "#606266");
    setCssVar("--sys-theme-app-card-background", "#f5f5f5");
};

export const setThemeThree = () => {
    setCssVar("--sys-theme-background", "#FBFAF9");
    setCssVar("--sys-theme-warp-background", "#ffffff");
    setCssVar(
        "--sys-theme-left-menu-background",
        "linear-gradient(180deg, #FED564 0%, #FBB016 100%)"
    );
    setCssVar("--sys-theme-left-menu-color", "#ffffff");
    setCssVar("--sys-theme-left-menu-active-color", "#FBB016");
    setCssVar("--sys-theme-left-menu-active-background", "#f4f7ff");
    setCssVar("--sys-theme-btn-primary-background", "#FBB016");
    setCssVar("--sys-theme-btn-primary-border-color", "#FBB016");
    setCssVar("--sys-theme-btn-primary-hover-border-color", "#FBB016");
    setCssVar("--sys-theme-tab-active-color", "#FBB016");
    setCssVar("--sys-theme-pagination-active-color", "#FBB016");

    setCssVar("--sys-theme-table-header-wapper", "#FFFBF3");
    setCssVar("--sys-theme-table-row-hover", "#FEF6E6");
    setCssVar("--sys-theme-check-box-color", "#FBB016");
    setCssVar("--sys-theme-input-border-active-color", "#FBB016");

    setCssVar("--el-color-primary", "#FBB016");
    setCssVar("--el-color-primary-light-3", "#FBB016");
    setCssVar("--el-color-primary-dark-2", "#FBB016");
    setCssVar("--el-border-color-light", "#dcdfe6");
    setCssVar("--el-color-primary-light-9", "#FEF6E6");
    setCssVar("--el-color-primary-light-7", "#FBB016");

    setCssVar("--sys-theme-left-menu-option-active-background", "#FEF6E6");
    setCssVar("--sys-theme-left-menu-option-active-color", "#FBB016");
    setCssVar("--sys-theme-table-row-color", "#051838");
    setCssVar("--sys-theme-tab-active-background", "#ffffff");
    setCssVar("--sys-theme-table-loading-color", "#FBB016");
    setCssVar("--sys-theme-left-menu-option-color", "#626A71");
    setCssVar("--sys-theme-input-background", "#ffffff");
    setCssVar("--sys-theme-input-border-color", "#dcdfe6");
    setCssVar("--sys-theme-dialog-background", "#ffffff");
    setCssVar("--sys-theme-form-label-color", "#626a71");
    setCssVar("--sys-theme-btn-plain-color", "#606266");
    setCssVar("--sys-theme-app-card-background", "#f5f5f5");
};

export const setThemeTwo = () => {
    setCssVar("--sys-theme-background", "#F6FCFC");
    setCssVar("--sys-theme-warp-background", "#ffffff");
    setCssVar(
        "--sys-theme-left-menu-background",
        "linear-gradient(180deg, #53EB9F 0%, #0CD29C 100%)"
    );
    setCssVar("--sys-theme-left-menu-color", "#ffffff");
    setCssVar("--sys-theme-left-menu-active-color", "#0CD29C");
    setCssVar("--sys-theme-left-menu-active-background", "#f4f7ff");
    setCssVar("--sys-theme-btn-primary-background", "#0CD29C");
    setCssVar("--sys-theme-btn-primary-border-color", "#0CD29C");
    setCssVar("--sys-theme-btn-primary-hover-border-color", "#0CD29C");
    setCssVar("--sys-theme-tab-active-color", "#0CD29C");
    setCssVar("--sys-theme-pagination-active-color", "#0CD29C");

    setCssVar("--sys-theme-table-header-wapper", "#F2FDFA");
    setCssVar("--sys-theme-table-row-hover", "#E6FBF5");
    setCssVar("--sys-theme-check-box-color", "#0CD29C");
    setCssVar("--sys-theme-input-border-active-color", "#0CD29C");

    setCssVar("--el-color-primary", "#0CD29C");
    setCssVar("--el-color-primary-light-3", "#0CD29C");
    setCssVar("--el-color-primary-dark-2", "#0CD29C");
    setCssVar("--el-border-color-light", "#dcdfe6");
    setCssVar("--el-color-primary-light-9", "#E6FBF5");
    setCssVar("--el-color-primary-light-7", "#0CD29C");

    setCssVar("--sys-theme-left-menu-option-active-background", "#E6FBF5");
    setCssVar("--sys-theme-left-menu-option-active-color", "#0CD29C");

    setCssVar("--sys-theme-table-row-color", "#051838");
    setCssVar("--sys-theme-tab-active-background", "#ffffff");
    setCssVar("--sys-theme-table-loading-color", "#0CD29C");
    setCssVar("--sys-theme-left-menu-option-color", "#626A71");
    setCssVar("--sys-theme-input-background", "#ffffff");
    setCssVar("--sys-theme-input-border-color", "#dcdfe6");
    setCssVar("--sys-theme-dialog-background", "#ffffff");
    setCssVar("--sys-theme-form-label-color", "#626a71");
    setCssVar("--sys-theme-btn-plain-color", "#606266");
    setCssVar("--sys-theme-app-card-background", "#f5f5f5");
};

export const setThemeFour = () => {
    setCssVar("--sys-theme-background", "#FAF9FB");
    setCssVar("--sys-theme-warp-background", "#ffffff");
    setCssVar(
        "--sys-theme-left-menu-background",
        "linear-gradient(180deg, #B8A0FC 0%, #D878EC 100%)"
    );
    setCssVar("--sys-theme-left-menu-color", "#ffffff");
    setCssVar("--sys-theme-left-menu-active-color", "#D878EC");
    setCssVar("--sys-theme-left-menu-active-background", "#f4f7ff");
    setCssVar("--sys-theme-btn-primary-background", "#D878EC");
    setCssVar("--sys-theme-btn-primary-border-color", "#D878EC");
    setCssVar("--sys-theme-btn-primary-hover-border-color", "#D878EC");
    setCssVar("--sys-theme-tab-active-color", "#D878EC");
    setCssVar("--sys-theme-pagination-active-color", "#D878EC");

    setCssVar("--sys-theme-table-header-wapper", "#FDF8FE");
    setCssVar("--sys-theme-table-row-hover", "#FAF0FC");
    setCssVar("--sys-theme-check-box-color", "#D878EC");
    setCssVar("--sys-theme-input-border-active-color", "#D878EC");

    setCssVar("--el-color-primary", "#D878EC");
    setCssVar("--el-color-primary-light-3", "#D878EC");
    setCssVar("--el-color-primary-dark-2", "#D878EC");
    setCssVar("--el-border-color-light", "#dcdfe6");
    setCssVar("--el-color-primary-light-9", "#FAF0FC");
    setCssVar("--el-color-primary-light-7", "#D878EC");

    setCssVar("--sys-theme-left-menu-option-active-background", "#FAF0FC");
    setCssVar("--sys-theme-left-menu-option-active-color", "#D878EC");

    setCssVar("--sys-theme-table-row-color", "#051838");
    setCssVar("--sys-theme-tab-active-background", "#ffffff");
    setCssVar("--sys-theme-table-loading-color", "#D878EC");
    setCssVar("--sys-theme-left-menu-option-color", "#626A71");
    setCssVar("--sys-theme-input-background", "#ffffff");
    setCssVar("--sys-theme-input-border-color", "#dcdfe6");
    setCssVar("--sys-theme-dialog-background", "#ffffff");
    setCssVar("--sys-theme-form-label-color", "#626a71");
    setCssVar("--sys-theme-btn-plain-color", "#606266");
    setCssVar("--sys-theme-app-card-background", "#f5f5f5");
};

export const setThemeFive = () => {
    setCssVar("--sys-theme-background", "#FAF9FB");
    setCssVar("--sys-theme-warp-background", "#ffffff");
    setCssVar(
        "--sys-theme-left-menu-background",
        "linear-gradient(180deg, #F9AD96 0%, #F6769D 100%)"
    );
    setCssVar("--sys-theme-left-menu-color", "#ffffff");
    setCssVar("--sys-theme-left-menu-active-color", "#F6769D");
    setCssVar("--sys-theme-left-menu-active-background", "#f4f7ff");
    setCssVar("--sys-theme-btn-primary-background", "#F6769D");
    setCssVar("--sys-theme-btn-primary-border-color", "#F6769D");
    setCssVar("--sys-theme-btn-primary-hover-border-color", "#F6769D");
    setCssVar("--sys-theme-tab-active-color", "#F6769D");
    setCssVar("--sys-theme-pagination-active-color", "#F6769D");

    setCssVar("--sys-theme-table-header-wapper", "#FEF8FA");
    setCssVar("--sys-theme-table-row-hover", "#FEF1F5");
    setCssVar("--sys-theme-check-box-color", "#F6769D");
    setCssVar("--sys-theme-input-border-active-color", "#F6769D");

    setCssVar("--el-color-primary", "#F6769D");
    setCssVar("--el-color-primary-light-3", "#F6769D");
    setCssVar("--el-color-primary-dark-2", "#F6769D");
    setCssVar("--el-border-color-light", "#dcdfe6");
    setCssVar("--el-color-primary-light-9", "#FEF1F5");
    setCssVar("--el-color-primary-light-7", "#F6769D");

    setCssVar("--sys-theme-left-menu-option-active-background", "#FAF0FC");
    setCssVar("--sys-theme-left-menu-option-active-color", "#F6769D");

    setCssVar("--sys-theme-table-row-color", "#051838");
    setCssVar("--sys-theme-tab-active-background", "#ffffff");
    setCssVar("--sys-theme-table-loading-color", "#F6769D");
    setCssVar("--sys-theme-left-menu-option-active-background", "#FAF0FC");
    setCssVar("--sys-theme-left-menu-option-color", "#626A71");
    setCssVar("--sys-theme-input-background", "#ffffff");
    setCssVar("--sys-theme-input-border-color", "#dcdfe6");
    setCssVar("--sys-theme-dialog-background", "#ffffff");
    setCssVar("--sys-theme-form-label-color", "#626a71");
    setCssVar("--sys-theme-btn-plain-color", "#606266");
    setCssVar("--sys-theme-app-card-background", "#f5f5f5");
};

export const setThemeSix = () => {
    setCssVar("--sys-theme-background", "#23272C");
    setCssVar("--sys-theme-warp-background", "#2A2D36");
    setCssVar("--sys-theme-left-menu-background", "#121416");
    setCssVar("--sys-theme-left-menu-color", "#B3BCCC");
    setCssVar("--sys-theme-left-menu-active-color", "#108CFB");
    setCssVar("--sys-theme-left-menu-active-background", "#2A2D36");
    setCssVar("--sys-theme-btn-primary-background", "#108CFB");
    setCssVar("--sys-theme-btn-primary-border-color", "#108CFB");
    setCssVar("--sys-theme-btn-primary-hover-border-color", "#108CFB");
    setCssVar("--sys-theme-tab-active-color", "#108CFB");
    setCssVar("--sys-theme-pagination-active-color", "#108CFB");

    setCssVar("--sys-theme-table-header-wapper", "#282E3A");
    setCssVar("--sys-theme-table-row-hover", "#263648");
    setCssVar("--sys-theme-check-box-color", "#108CFB");
    setCssVar("--sys-theme-input-border-active-color", "#108CFB");

    setCssVar("--el-color-primary", "#108CFB");
    setCssVar("--el-color-primary-light-3", "#108CFB");
    setCssVar("--el-color-primary-dark-2", "#108CFB");
    setCssVar("--el-border-color-light", "#707070");
    setCssVar("--el-color-primary-light-9", "#DFEFFF");
    setCssVar("--el-color-primary-light-7", "#108cfb");

    setCssVar("--sys-theme-left-menu-option-active-background", "#494d56");
    setCssVar("--sys-theme-left-menu-option-active-color", "#108CFB");

    setCssVar("--sys-theme-table-row-color", "#B3BCCC");

    setCssVar("--el-text-color-primary", "#626A71");
    setCssVar("--sys-theme-tab-active-background", "#494d56");
    setCssVar("--sys-theme-table-loading-color", "#108CFB");
    setCssVar("--sys-theme-left-menu-option-color", "#B3BCCC");
    setCssVar("--sys-theme-input-background", "#999ea900 ");
    setCssVar("--sys-theme-input-border-color", "#707070");
    setCssVar("--sys-theme-dialog-background", "#23272C");
    setCssVar("--sys-theme-form-label-color", "#9ea2a8");
    setCssVar("--sys-theme-btn-plain-color", "#ffffff");

    setCssVar("--sys-theme-app-card-background", "#121416");
};

export const setThemeSeveen = () => {
    setCssVar("--sys-theme-background", "#F4F7FF");
    setCssVar("--sys-theme-warp-background", "#ffffff");
    setCssVar(
        "--sys-theme-left-menu-background",
        "linear-gradient(180deg, #2A2D36 0%, #121416 100%)"
    );
    setCssVar("--sys-theme-left-menu-color", "#ffffff");
    setCssVar("--sys-theme-left-menu-active-color", "#108cfb");
    setCssVar("--sys-theme-left-menu-active-background", "#f4f7ff");
    setCssVar("--sys-theme-btn-primary-background", "#108cfb");
    setCssVar("--sys-theme-btn-primary-border-color", "#108cfb");
    setCssVar("--sys-theme-btn-primary-hover-border-color", "#108cfb");
    setCssVar("--sys-theme-tab-active-color", "#108cfb");
    setCssVar("--sys-theme-pagination-active-color", "#108cfb");

    setCssVar("--sys-theme-table-header-wapper", "#F5FAFF");
    setCssVar("--sys-theme-table-row-hover", "#DFEFFF");
    setCssVar("--sys-theme-check-box-color", "#108cfb");

    setCssVar("--sys-theme-input-border-active-color", "#108cfb");
    setCssVar("--el-color-primary", "#108cfb");
    setCssVar("--el-color-primary-light-3", "#108cfb");
    setCssVar("--el-color-primary-dark-2", "#108cfb");
    setCssVar("--el-border-color-light", "#dcdfe6");
    setCssVar("--el-color-primary-light-9", "#DFEFFF");
    setCssVar("--el-color-primary-light-7", "#108cfb");

    setCssVar("--sys-theme-left-menu-option-active-background", "#DFEFFF");
    setCssVar("--sys-theme-left-menu-option-active-color", "#108cfb");

    setCssVar("--sys-theme-table-row-color", "#051838");
    setCssVar("--sys-theme-tab-active-background", "#ffffff");
    setCssVar("--sys-theme-table-loading-color", "#108CFB");
    setCssVar("--sys-theme-left-menu-option-color", "#626A71");
    setCssVar("--sys-theme-input-background", "#ffffff");
    setCssVar("--sys-theme-input-border-color", "#dcdfe6");
    setCssVar("--sys-theme-dialog-background", "#ffffff");
    setCssVar("--sys-theme-form-label-color", "#626a71");
    setCssVar("--sys-theme-btn-plain-color", "#606266");

    setCssVar("--sys-theme-app-card-background", "#f5f5f5");
};

export const setThemeEight = () => {
    setCssVar("--sys-theme-background", "#F1F1F1");
    setCssVar("--sys-theme-warp-background", "#ffffff");
    setCssVar("--sys-theme-left-menu-background", "#11141C");
    setCssVar("--sys-theme-left-menu-color", "#ffffff");
    setCssVar("--sys-theme-left-menu-active-color", "#41C980");
    setCssVar("--sys-theme-left-menu-active-background", "#f4f7ff");
    setCssVar("--sys-theme-btn-primary-background", "#41C980");
    setCssVar("--sys-theme-btn-primary-border-color", "#41C980");
    setCssVar("--sys-theme-btn-primary-hover-border-color", "#41C980");
    setCssVar("--sys-theme-tab-active-color", "#41C980");
    setCssVar("--sys-theme-pagination-active-color", "#41C980");

    setCssVar("--sys-theme-table-header-wapper", "#F2FDFA");
    setCssVar("--sys-theme-table-row-hover", "#E6FBF5");
    setCssVar("--sys-theme-check-box-color", "#41C980");

    setCssVar("--sys-theme-input-border-active-color", "#41C980");
    setCssVar("--el-color-primary", "#41C980");
    setCssVar("--el-color-primary-light-3", "#41C980");
    setCssVar("--el-color-primary-dark-2", "#41C980");
    setCssVar("--el-border-color-light", "#dcdfe6");
    setCssVar("--el-color-primary-light-9", "#E6FBF5");
    setCssVar("--el-color-primary-light-7", "#41C980");

    setCssVar("--sys-theme-left-menu-option-active-background", "#DFEFFF");
    setCssVar("--sys-theme-left-menu-option-active-color", "#41C980");

    setCssVar("--sys-theme-table-row-color", "#051838");
    setCssVar("--sys-theme-tab-active-background", "#ffffff");
    setCssVar("--sys-theme-table-loading-color", "#41C980");
    setCssVar("--sys-theme-left-menu-option-color", "#626A71");
    setCssVar("--sys-theme-input-background", "#ffffff");
    setCssVar("--sys-theme-input-border-color", "#dcdfe6");
    setCssVar("--sys-theme-dialog-background", "#ffffff");
    setCssVar("--sys-theme-form-label-color", "#626a71");
    setCssVar("--sys-theme-btn-plain-color", "#606266");

    setCssVar("--sys-theme-app-card-background", "#f5f5f5");
};

export const setThemeNight = () => {
    setCssVar("--sys-theme-background", "#F1F1F1");
    setCssVar("--sys-theme-warp-background", "#ffffff");
    setCssVar(
        "--sys-theme-left-menu-background",
        "linear-gradient( 180deg, #2B63C1 0%, #1D52AA 100%)"
    );
    setCssVar("--sys-theme-left-menu-color", "#ffffff");
    setCssVar("--sys-theme-left-menu-active-color", "#108cfb");
    setCssVar("--sys-theme-left-menu-active-background", "#f4f7ff");
    setCssVar("--sys-theme-btn-primary-background", "#1D52AA");
    setCssVar("--sys-theme-btn-primary-border-color", "#1D52AA");
    setCssVar("--sys-theme-btn-primary-hover-border-color", "#1D52AA");
    setCssVar("--sys-theme-tab-active-color", "#1D52AA");
    setCssVar("--sys-theme-pagination-active-color", "#1D52AA");

    setCssVar("--sys-theme-table-header-wapper", "#F5FAFF");
    setCssVar("--sys-theme-table-row-hover", "#DFEFFF");
    setCssVar("--sys-theme-check-box-color", "#1D52AA");

    setCssVar("--sys-theme-input-border-active-color", "#1D52AA");
    setCssVar("--el-color-primary", "#1D52AA");
    setCssVar("--el-color-primary-light-3", "#1D52AA");
    setCssVar("--el-color-primary-dark-2", "#1D52AA");
    setCssVar("--el-border-color-light", "#dcdfe6");
    setCssVar("--el-color-primary-light-9", "#DFEFFF");
    setCssVar("--el-color-primary-light-7", "#1D52AA");

    setCssVar("--sys-theme-left-menu-option-active-background", "#DFEFFF");
    setCssVar("--sys-theme-left-menu-option-active-color", "#1D52AA");

    setCssVar("--sys-theme-table-row-color", "#051838");
    setCssVar("--sys-theme-tab-active-background", "#ffffff");
    setCssVar("--sys-theme-table-loading-color", "#108CFB");
    setCssVar("--sys-theme-left-menu-option-color", "#626A71");
    setCssVar("--sys-theme-input-background", "#ffffff");
    setCssVar("--sys-theme-input-border-color", "#dcdfe6");
    setCssVar("--sys-theme-dialog-background", "#ffffff");
    setCssVar("--sys-theme-form-label-color", "#626a71");
    setCssVar("--sys-theme-btn-plain-color", "#606266");

    setCssVar("--sys-theme-app-card-background", "#f5f5f5");
};
