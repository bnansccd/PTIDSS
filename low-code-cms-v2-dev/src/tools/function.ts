export function filterRecords(arr: never[]) {
    arr.forEach((item: any) => {
        Object.keys(item).forEach((key) => {
            //     item[key] =
            //         (Array.isArray(item[key]) && item[key].length === 0) ||
            //         !item[key]
            //             ? "--"
            //             : item[key];

            if (item[key] === null) {
                item[key] = "--";
            } else if (item[key] === "") {
                item[key] = "--";
            } else if (item[key] === undefined) {
                item[key] = "--";
            }

            // item[key] =
            //     item[key] != null && item[key] != "" && item[key] == 0
            //         ? item[key]
            //         : "--";
        });
    });
    return arr;
}
