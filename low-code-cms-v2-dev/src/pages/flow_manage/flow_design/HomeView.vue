<template>
    <div class="container">
        <!-- 创建一个canvas画布 npmn-js是通过canvas实现绘图的，并设置ref让vue获取到element -->
        <div class="bpmn-canvas" ref="canvas"></div>
        <div id="properties" class="bpmn-properties"></div>
        <div class="btn-box">
            <el-button @click="onExport" type="warning">导入xml</el-button>
            <el-button @click="saveDiagram" type="primary">导出xml</el-button>
            <el-button ref="saveSvgRef" @click="saveSVG" plain
                >导出svg</el-button
            >
        </div>
    </div>

    <OperatData
        title="导入xml"
        :id="opObject.id"
        v-model="opObject.show"
        @close="opObject.show = false"
        @query="onQuery"
        :parent-id="opObject.parentId"
    />
    <OperatData
        title="编辑用户"
        :id="opObject.id"
        v-model="showUser"
        @close="showUser = false"
        @query="onEditUserInput"
        :parent-id="opObject.parentId"
    />
</template>

<script lang="ts" setup>
// 在这里引入一下Bpmn建模器对象
import BpmnModeler from "bpmn-js/lib/Modeler";
import { reactive, ref, onMounted } from "vue";
import {
    BpmnPropertiesPanelModule,
    BpmnPropertiesProviderModule,
    CamundaPlatformPropertiesProviderModule,
} from "bpmn-js-properties-panel";
import CamundaBpmnModdle from "camunda-bpmn-moddle/resources/camunda.json";
import OperatData from "./components/OperatData.vue";
import OperatUser from "./components/OperatUser.vue";
import customTranslate from "./customTranalate/customTranslate";

import magicPropertiesProviderModule from "./provider/magic/index";
import magicModdleDescriptor from "./descriptors/magic";

const response = reactive({
    canvas: null,
    bpmnModeler: null,
    initTemplate: `<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="sid-38422fae-e03e-43a3-bef4-bd33b32041b2" targetNamespace="http://bpmn.io/bpmn" exporter="bpmn-js (https://demo.bpmn.io)" exporterVersion="5.1.2">
<process id="Process_1" isExecutable="false">
    <startEvent id="StartEvent_1y45yut" name="开始">
    <outgoing>SequenceFlow_0h21x7r</outgoing>
    </startEvent>
    <task id="Task_1hcentk">
    <incoming>SequenceFlow_0h21x7r</incoming>
    </task>
    <sequenceFlow id="SequenceFlow_0h21x7r" sourceRef="StartEvent_1y45yut" targetRef="Task_1hcentk" />
</process>
<bpmndi:BPMNDiagram id="BpmnDiagram_1">
    <bpmndi:BPMNPlane id="BpmnPlane_1" bpmnElement="Process_1">
    <bpmndi:BPMNShape id="StartEvent_1y45yut_di" bpmnElement="StartEvent_1y45yut">
        <omgdc:Bounds x="152" y="102" width="36" height="36" />
        <bpmndi:BPMNLabel>
        <omgdc:Bounds x="160" y="145" width="22" height="14" />
        </bpmndi:BPMNLabel>
    </bpmndi:BPMNShape>
    <bpmndi:BPMNShape id="Task_1hcentk_di" bpmnElement="Task_1hcentk">
        <omgdc:Bounds x="240" y="80" width="100" height="80" />
    </bpmndi:BPMNShape>
    <bpmndi:BPMNEdge id="SequenceFlow_0h21x7r_di" bpmnElement="SequenceFlow_0h21x7r">
        <omgdi:waypoint x="188" y="120" />
        <omgdi:waypoint x="240" y="120" />
    </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
</bpmndi:BPMNDiagram>
</definitions>`,
    currentElement: null,
});
const canvas = ref(null);
const saveSvgRef = ref(null);

const opInit = {
    show: false,
    code: "init", // "look" "edit" "add"
    id: "0", //id为0时表示添加
    parentId: "",
};
const opObject = reactive({
    ...opInit,
});
const showUser = ref(false);

function init() {
    let customTranslateModule = {
        translate: ["value", customTranslate],
    };
    // 创建Bpmn对象
    response.bpmnModeler = new BpmnModeler({
        // 设置bpmn的绘图容器为上门获取的画布 element
        container: canvas.value,
        //添加控制板
        propertiesPanel: {
            parent: "#properties",
        },
        additionalModules: [
            // 右边的属性栏
            BpmnPropertiesPanelModule,
            BpmnPropertiesProviderModule,
            CamundaPlatformPropertiesProviderModule,

            // 汉化
            customTranslateModule,
            magicPropertiesProviderModule,
        ],
        moddleExtensions: {
            camunda: CamundaBpmnModdle,
            magic: magicModdleDescriptor,
        },
    });

    addEventBusListener();
    // 初始化建模器内容
    initDiagram(response.initTemplate);
}

// 添加监听事件
function addEventBusListener() {
    const eventBus = response.bpmnModeler.get("eventBus"); // 需要使用eventBus
    const eventTypes = ["element.click", "AAmyInput"]; // 需要监听的事件集合

    eventBus.on("AAmyInput", function (e) {
        console.log("AAmyInput 触发");
        // console.log(e);
        response.currentElement = e;
        showUser.value = true;
        // onExport();
    });
    // eventTypes.forEach(function (eventType) {
    //     eventBus.on(eventType, function (e) {
    //         console.log(e);
    //     });
    // });
}

function initDiagram(bpmn: string) {
    // 将xml导入Bpmn-js建模器
    response.bpmnModeler!.importXML(bpmn, (err: any) => {
        if (err) {
            console.log("打开模型出错,请确认该模型符合Bpmn2.0规范");
        }
    });
}
onMounted(() => {
    console.log(
        "CamundaPlatformPropertiesProviderModule",
        CamundaPlatformPropertiesProviderModule
    );
    init();
});

// 导入xml 重新渲染
const onQuery = (xml: string) => {
    response.bpmnModeler!.importXML(xml, (err: any) => {
        if (err) {
            console.log("打开模型出错,请确认该模型符合Bpmn2.0规范");
        }
    });
    opObject.show = false;
};

// 导出xml
const saveDiagram = () => {
    response.bpmnModeler!.saveXML(
        { format: true },
        function (err: any, xml: string) {
            console.log(xml);
            alert(xml);
        }
    );
};

// 导出svg
const saveSVG = () => {
    response.bpmnModeler!.saveSVG({ format: true }, function (error, svg) {
        if (error) {
            return;
        }

        const svgBlob = new Blob([svg], {
            type: "image/svg+xml",
        });

        const fileName = Math.random(36).toString().substring(7) + ".svg";

        const downloadLink = document.createElement("a");
        downloadLink.download = fileName;
        downloadLink.innerHTML = "Get BPMN SVG";
        downloadLink.href = window.URL.createObjectURL(svgBlob);
        downloadLink.onclick = function (event) {
            document.body.removeChild(event.target);
        };
        downloadLink.style.visibility = "hidden";
        document.body.appendChild(downloadLink);
        downloadLink.click();
    });
};

// 导入xml
const onExport = () => {
    opObject.show = true;
};

// 编辑用户确认
const onEditUserInput = (str: string) => {
    showUser.value = false;
    //  console.log(response.bpmnModeler);

    const modeling = response.bpmnModeler.get("modeling");

    const elementRegistry = response.bpmnModeler.get("elementRegistry");

    const shape = response.currentElement
        ? elementRegistry.get(response.currentElement.id)
        : response.currentElement.shape;

    console.log(shape, "shape");
    modeling.updateProperties(shape, {
        name: str,
        role: str,
    });
};
</script>

<style lang="scss" scoped>
.container {
    width: calc(100% - 16px);
    height: calc(100vh - 100px);
    display: flex;
    position: relative;
}
.bpmn-canvas {
    width: 80%;
    height: calc(100vh - 100px);
    background-color: #ffffff;
}

.bpmn-properties {
    flex: 1;
    background-color: #ffffff;
    border: 1px solid rgb(207, 200, 200);
}

.btn-box {
    position: absolute;
    top: 20px;
    right: 380px;
    // width: 200px;
    // height: 50px;
    // background: #ad9b9b;
    z-index: 999;
}
</style>
