package com.troy.common.core.utils;

import com.troy.common.core.domain.ChartVO;
import com.troy.common.core.domain.DynamicColumnConfig;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description: 通过word模板生成新的word工具类
 * @Author: zhuQing
 * @Date: 2024/12/11 14:36
 * @Version: 1.0
 **/
public class WorderToNewWordUtils {

    /**
     * 根据模板生成新word文档
     * 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
     *
     * @param inputUrl
     * @param textMap
     * @param tableList
     * @return
     */
    public static byte[] changWord(String inputUrl, Map<String, String> textMap, List<String[]> tableList, List<ChartVO> chartVOS) {

        //模板转换默认成功
        ByteArrayOutputStream stream = null;
        FileInputStream fileInputStream = null;
        byte[] byteArray = new byte[0];
        try {
            fileInputStream = new FileInputStream(inputUrl);
            //获取docx解析对象
            XWPFDocument document = new XWPFDocument(fileInputStream);
            //解析替换文本段落对象
            changeText(document, textMap);
            //解析替换表格对象
            changeTable(document, textMap, tableList);
            //解析图表
            changeCharts(document, chartVOS);


            //生成新的word
            stream = new ByteArrayOutputStream();
            document.write(stream);
            byteArray = stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (StringUtils.isNotNull(stream)) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (StringUtils.isNotNull(fileInputStream)) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return byteArray;

    }

    /**
     * 解析报表
     *
     * @param document
     * @param chartVOS
     */
    private static void changeCharts(XWPFDocument document, List<ChartVO> chartVOS) {
        if (StringUtils.isNotEmpty(chartVOS)) {
            List<XWPFChart> charts = document.getCharts();
            if (charts.size() >= chartVOS.size()) {
                for (int j = 0; j < chartVOS.size(); j++) {
                    ChartVO chartVO = chartVOS.get(j);
                    if (chartVO.getBarValues().size() != chartVO.getSeriesName().length) {
                        continue;
                    }

                    if (chartVO.getBarValues().get(0).length != chartVO.getXValues().length) {
                        continue;
                    }
                    XWPFChart xwpfChart = charts.get(j);
                    Integer firstRow=1;
                    if (StringUtils.isEmpty(chartVO.getXValues())){
                        firstRow=0;
                    }
                    String  catDataRange = xwpfChart.formatRange(new CellRangeAddress(firstRow, chartVO.getXValues().length, 0, 0));
                    //根据分类信息的范围创建分类信息的数据源
                    XDDFDataSource<?> catDataSource = XDDFDataSourcesFactory.fromArray(chartVO.getXValues(), catDataRange, 0);
                    //更新数据
                    //主次坐标
                    XDDFChartData chartData = null;
                    for (int i = 0; i < chartVO.getSeriesName().length; i++) {
                        chartData = xwpfChart.getChartSeries().get(0);
                        String valDataRange = xwpfChart.formatRange(new CellRangeAddress(firstRow, chartVO.getXValues().length, i + 1, i + 1));
                        //根据数据的范围创建值的数据源
                        Number[] val = chartVO.getBarValues().get(i);
                        XDDFNumericalDataSource<Number> valDataSource = XDDFDataSourcesFactory.fromArray(val, valDataRange, i + 1);
                        //获取图表系列的数据对象
                        XDDFChartData.Series series = chartData.getSeries(i);
                        //替换系列数据对象中的分类和值
                        series.replaceData(catDataSource, valDataSource);
                        //修改系列数据对象中的标题
                        CellReference cellReference = xwpfChart.setSheetTitle(chartVO.getSeriesName()[i], 1);
                        series.setTitle(chartVO.getSeriesName()[i], cellReference);
                        //更新图表数据对象
                        xwpfChart.plot(chartData);
                    }
                }
            }
        }
    }

    /**
     * 替换段落文本
     *
     * @param document docx解析对象
     * @param textMap  需要替换的信息集合
     */
    public static void changeText(XWPFDocument document, Map<String, String> textMap) {
        //获取段落集合
        List<XWPFParagraph> paragraphs = document.getParagraphs();

        for (XWPFParagraph paragraph : paragraphs) {
            //判断此段落时候需要进行替换
            String text = paragraph.getText();

            if (checkText(text)) {
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    //替换模板原来位置
                    run.setText(changeValue(run.toString(), textMap), 0);
                }
            }
        }

    }

    /**
     * 替换表格对象方法
     *
     * @param document  docx解析对象
     * @param textMap   需要替换的信息集合
     * @param tableList 需要插入的表格信息集合
     */
    public static void changeTable(XWPFDocument document, Map<String, String> textMap,
                                   List<String[]> tableList) {
        //获取表格对象集合
        List<XWPFTable> tables = document.getTables();
        for (int i = 0; i < tables.size(); i++) {
            //只处理行数大于等于2的表格，且不循环表头
            XWPFTable table = tables.get(i);
            if (table.getRows().size() >= 1) {
                //判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入

                if (checkText(table.getText())) {
                    List<XWPFTableRow> rows = table.getRows();
                    //遍历表格,并替换模板
                    eachTable(rows, textMap);
                } else {
                    if (StringUtils.isNotEmpty(tableList)) {
                        insertTable(table, tableList);
                    }
                }
            }
        }
    }


    /**
     * 遍历表格
     *
     * @param rows    表格行对象
     * @param textMap 需要替换的信息集合
     */
    public static void eachTable(List<XWPFTableRow> rows, Map<String, String> textMap) {
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                //判断单元格是否需要替换
                if (checkText(cell.getText())) {
                    List<XWPFParagraph> paragraphs = cell.getParagraphs();
                    for (XWPFParagraph paragraph : paragraphs) {
                        List<XWPFRun> runs = paragraph.getRuns();
                        String text = "";
                        for (XWPFRun run : runs) {
                            if (StringUtils.startsWith(run.toString(), "${") && StringUtils.endsWith(run.toString(), "}")) {
                                run.setText(changeValue(run.toString(), textMap), 0);
                            } else {
                                text += run.toString();
                                if (StringUtils.endsWith(run.toString(), "}")) {
                                    run.setText(changeValue(text, textMap), 0);
                                    text = "";
                                }else {
                                    run.setText("", 0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 为表格插入数据，行数不够添加新行
     *
     * @param table     需要插入数据的表格
     * @param tableList 插入数据集合
     */
    public static void insertTable(XWPFTable table, List<String[]> tableList) {
        //创建行,根据需要插入的数据添加新行，不处理表头
        for (int i = 1; i < tableList.size(); i++) {
            XWPFTableRow row = table.createRow();
        }
        //遍历表格插入数据
        List<XWPFTableRow> rows = table.getRows();
        for (int i = 1; i < rows.size(); i++) {
            XWPFTableRow newRow = table.getRow(i);
            List<XWPFTableCell> cells = newRow.getTableCells();
            for (int j = 0; j < cells.size(); j++) {
                XWPFTableCell cell = cells.get(j);
                cell.setText(tableList.get(i - 1)[j]);
            }
        }

    }

    /**
     * 判断文本中时候包含$
     *
     * @param text 文本
     * @return 包含返回true, 不包含返回false
     */
    public static boolean checkText(String text) {
        boolean check = false;
        if (text.indexOf("$") != -1) {
            check = true;
        }
        return check;

    }

    /**
     * 匹配传入信息集合与模板
     *
     * @param value   模板需要替换的区域
     * @param textMap 传入信息集合
     * @return 模板需要替换区域信息集合对应值
     */
    public static String changeValue(String value, Map<String, String> textMap) {
        Set<Map.Entry<String, String>> textSets = textMap.entrySet();
        for (Map.Entry<String, String> textSet : textSets) {
            //匹配模板与替换值 格式${key}
            String key = "${" + textSet.getKey() + "}";
            if (value.indexOf(key) != -1) {
                value = textSet.getValue();
            }
        }
        //模板未匹配到区域替换为空
        if (checkText(value)) {
            value = "";
        }
        return value;
    }



    /**
     * 根据模板生成新word文档
     * @param inputUrl url
     * @param textMap 替换的文字
     * @param lineBreak 换行符标识
     * @param columnConfigs 表格头配置
     * @param tableData 数据项
     * @return
     */
    public static byte[] tableWord(String inputUrl, Map<String, String> textMap, String lineBreak, List<DynamicColumnConfig> columnConfigs,
                                   List<Map<String, Object>> tableData) {
        //模板转换默认成功
        ByteArrayOutputStream stream = null;
        FileInputStream fileInputStream = null;
        byte[] byteArray = new byte[0];
        try {
            fileInputStream = new FileInputStream(inputUrl);
            //获取docx解析对象
            XWPFDocument document = new XWPFDocument(fileInputStream);
            //解析替换文本段落对象
            changeTextAndBreak(document, textMap, lineBreak);
            List<XWPFTable> tables = document.getTables();

            for (XWPFTable table : tables) {
                insertDynamicTable(table, columnConfigs, tableData);
            }

            //生成新的word
            stream = new ByteArrayOutputStream();
            document.write(stream);
            byteArray = stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return byteArray;
    }

    /**
     * 替换段落文本和换行符
     *
     * @param document  docx解析对象
     * @param textMap   需要替换的信息集合
     * @param lineBreak 自定义换行符
     */
    public static void changeTextAndBreak(XWPFDocument document, Map<String, String> textMap, String lineBreak) {
        //获取段落集合
        List<XWPFParagraph> paragraphs = document.getParagraphs();

        for (XWPFParagraph paragraph : paragraphs) {
            //判断此段落时候需要进行替换
            String text = paragraph.getText();

            if (checkText(text)) {
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    //替换模板原来位置
                    String result = changeValue(run.toString(), textMap);
                    String[] split = result.split(lineBreak);
                    if (split.length == 1) {
                        run.setText(result, 0);
                    } else {
                        for (int i = 0; i < split.length; i++) {
                            if (i == 0) {
                                run.setText(split[i], 0);
                            } else {
                                run.setText(split[i]);
                            }
                            if (i != split.length - 1) {
                                run.addBreak();
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 动态添加表格列并填充数据
     *
     * @param table         表格对象
     * @param columnConfigs 列配置信息
     * @param tableData     表格数据
     */
    public static void insertDynamicTable(XWPFTable table, List<DynamicColumnConfig> columnConfigs,
                                          List<Map<String, Object>> tableData) {
        if (columnConfigs == null || columnConfigs.isEmpty() || tableData == null || tableData.isEmpty()) {
            return;
        }

        setTableStyle(table);

        // 创建表头行
        XWPFTableRow headerRow = table.getRow(0);
        if (headerRow == null) {
            headerRow = table.createRow();
        }

        // 清空表头行原有的单元格
        while (headerRow.getTableCells().size() > 0) {
            headerRow.removeCell(0);
        }

        // 添加表头单元格
        for (int i = 0; i < columnConfigs.size(); i++) {
            DynamicColumnConfig config = columnConfigs.get(i);
            XWPFTableCell cell = headerRow.addNewTableCell();
            cell.setText(config.getHeader());

            // 设置表头样式
            setHeaderCellStyle(cell);
        }

        // 添加数据行
        for (Map<String, Object> rowData : tableData) {
            XWPFTableRow dataRow = table.createRow();

            for (int i = 0; i < columnConfigs.size(); i++) {
                DynamicColumnConfig config = columnConfigs.get(i);
                XWPFTableCell cell = dataRow.getCell(i);
                if (cell == null) {
                    cell = dataRow.addNewTableCell();
                }

                // 获取数据并设置到单元格
                Object value = rowData.get(config.getField());
                if (value != null) {
                    cell.setText(value.toString());
                }

                // 设置数据单元格样式
                setDataCellStyle(cell, config.getAlignment());
            }
        }
    }


    /**
     * 设置表格样式
     *
     * @param table 表格对象
     */
    private static void setTableStyle(XWPFTable table) {
        // 设置表格宽度为页面宽度
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setW(BigInteger.valueOf(5000));
        tblWidth.setType(STTblWidth.PCT);

        // 设置表格边框 - 实框样式
        CTTblBorders borders = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();
        setBorder(borders.addNewTop(), STBorder.SINGLE, 4, 0, "000000");
        setBorder(borders.addNewLeft(), STBorder.SINGLE, 4, 0, "000000");
        setBorder(borders.addNewBottom(), STBorder.SINGLE, 4, 0, "000000");
        setBorder(borders.addNewRight(), STBorder.SINGLE, 4, 0, "000000");
        setBorder(borders.addNewInsideH(), STBorder.SINGLE, 4, 0, "000000");
        setBorder(borders.addNewInsideV(), STBorder.SINGLE, 4, 0, "000000");
    }

    /**
     * 设置边框样式
     *
     * @param border     边框对象
     * @param borderType 边框类型
     * @param size       边框大小
     * @param space      边框间距
     * @param color      边框颜色
     */
    private static void setBorder(CTBorder border, STBorder.Enum borderType, int size, int space, String color) {
        border.setVal(borderType);
        border.setSz(BigInteger.valueOf(size));
        border.setSpace(BigInteger.valueOf(space));
        border.setColor(color);
    }

    /**
     * 设置表头单元格样式
     *
     * @param cell 单元格对象
     */
    private static void setHeaderCellStyle(XWPFTableCell cell) {
        // 设置单元格样式
        CTTcPr cellPr = cell.getCTTc().addNewTcPr();

        // 设置单元格宽度
        CTTblWidth width = cellPr.addNewTcW();
        width.setType(STTblWidth.AUTO);
        width.setW(BigInteger.valueOf(1000));

        // 设置单元格对齐方式
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        // 设置文本样式
        for (XWPFParagraph para : cell.getParagraphs()) {
            para.setAlignment(ParagraphAlignment.CENTER);
            for (XWPFRun run : para.getRuns()) {
                run.setBold(true); // 表头字体加粗
                run.setFontSize(10);
            }
        }

        // 设置表头单元格背景色
        cell.setColor("F2F2F2");
    }

    /**
     * 设置数据单元格样式
     *
     * @param cell      单元格对象
     * @param alignment 对齐方式
     */
    private static void setDataCellStyle(XWPFTableCell cell, ParagraphAlignment alignment) {
        // 设置单元格样式
        CTTcPr cellPr = cell.getCTTc().addNewTcPr();

        // 设置单元格宽度
        CTTblWidth width = cellPr.addNewTcW();
        width.setType(STTblWidth.NIL);
        width.setW(BigInteger.valueOf(1000));

        // 设置单元格对齐方式
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        // 设置文本样式
        for (XWPFParagraph para : cell.getParagraphs()) {
            para.setAlignment(alignment);
            for (XWPFRun run : para.getRuns()) {
                run.setBold(true); // 数据字体加粗
                run.setFontSize(9);
            }
        }
    }


}
