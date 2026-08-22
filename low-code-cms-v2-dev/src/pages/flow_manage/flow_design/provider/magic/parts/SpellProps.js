import { html } from "htm/preact";

import {
    TextFieldEntry,
    isTextFieldEntryEdited,
    SelectEntry,
    isSelectEntryEdited,
    HeaderButton,
    LayoutContext,
} from "@bpmn-io/properties-panel";
import { useService } from "bpmn-js-properties-panel";

export default function (element) {
    return [
        {
            id: "spell",
            element,
            component: Spell,
            isEdited: isTextFieldEntryEdited,
        },
        {
            id: "userName",
            element,
            component: UserName,
            isEdited: isSelectEntryEdited,
        },
        {
            id: "role",
            element,
            component: Role,
            isEdited: isTextFieldEntryEdited,
        },
    ];
}

function Spell(props) {
    const { element, id } = props;

    const modeling = useService("modeling");
    const translate = useService("translate");
    const debounce = useService("debounceInput");

    const getValue = () => {
        return element.businessObject.spell || "";
    };

    const setValue = (value) => {
        return modeling.updateProperties(element, {
            spell: value,
        });
    };

    return html`<${TextFieldEntry}
        id=${id}
        element=${element}
        description=${translate("Apply a black magic spell")}
        label=${translate("Spell")}
        getValue=${getValue}
        setValue=${setValue}
        debounce=${debounce}
    />`;
}

function UserName(props) {
    const { element, id } = props;

    const modeling = useService("modeling");
    const translate = useService("translate");
    const debounce = useService("debounceInput");

    const getValue = () => {
        return element.businessObject.userName || "";
    };

    const setValue = (value) => {
        return modeling.updateProperties(element, {
            userName: value,
        });
    };

    const getOptions = () => {
        const options = [
            {
                label: translate("张三"),
                value: "张三",
            },
            {
                label: translate("李四"),
                value: "李四",
            },
        ];
        return options;
    };

    const onFocus = () => {
        console.log("onFocus...");
    };

    return html`<${SelectEntry}
        id=${id}
        element=${element}
        description=${translate("请输入用户名")}
        label=${translate("UserName")}
        getValue=${getValue}
        setValue=${setValue}
        getOptions=${getOptions}
        onFocus=${onFocus}
    /> `;
}

function Role(props) {
    const { element, id } = props;

    const modeling = useService("modeling");
    const translate = useService("translate");
    const debounce = useService("debounceInput");
    const eventBus = useService("eventBus"); // 事件总线
    const getValue = () => {
        return element.businessObject.role || "";
    };

    const setValue = (value) => {
        return modeling.updateProperties(element, {
            role: value,
        });
    };

    const onFocus = () => {
        eventBus.fire("AAmyInput", element);
    };

    return html`<${TextFieldEntry}
        id=${id}
        element=${element}
        description=${translate("点击可以打开弹窗")}
        label=${translate("Role")}
        getValue=${getValue}
        setValue=${setValue}
        debounce=${debounce}
        onFocus=${onFocus}
    />`;
}
