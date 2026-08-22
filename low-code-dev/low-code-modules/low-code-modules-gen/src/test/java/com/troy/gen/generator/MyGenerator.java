package com.troy.gen.generator;


import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.StrategyConfig;
import com.mybatisflex.codegen.entity.Column;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.codegen.generator.GeneratorFactory;
import com.mybatisflex.codegen.generator.IGenerator;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.gen.DTO.MyColumnConfig;
import com.troy.gen.DTO.MyTableConfig;
import com.troy.gen.domain.VO.GenTableColumnVO;
import com.troy.gen.domain.VO.GenTableVO;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/11 10:10:49
 * @Description: MyGenerator
 * @Version: 1.0.0
 */
public class MyGenerator {

    private GlobalConfig globalConfig;

    public MyGenerator(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public void generate(GenTableVO vo) {
        Table table = this.buildTable(vo);
        MyTableConfig myTableConfig = new MyTableConfig();
        BeanUtils.copyProperties(vo, myTableConfig);
        if (StringUtils.isNotNull(vo.getSubTable())) {
            myTableConfig.setSubTable(this.buildTable(vo.getSubTable()));
        }
        table.setTableConfig(myTableConfig);
        GeneratorFactory.registerGenerator("dao" , new DaoGenerator());
        for (IGenerator generator : GeneratorFactory.getGenerators()) {
            generator.generate(table, globalConfig);
        }

    }

    /**
     * 设置信息
     *
     * @param vo
     * @return
     */
    private Table buildTable(GenTableVO vo) {
        Table table = new Table();
        StrategyConfig strategyConfig = globalConfig.getStrategyConfig();
        String schemaName = strategyConfig.getGenerateSchema();
        String tableName = vo.getTableName();
        table.setGlobalConfig(globalConfig);
        table.setTableConfig(strategyConfig.getTableConfig(tableName));
        table.setSchema(schemaName);
        table.setName(tableName);
        table.setComment(vo.getTableComment());
        //设置主键
        GenTableColumnVO genTableColumnVO = vo.getPkColumn();
        table.addPrimaryKey(genTableColumnVO.getColumnName());
        //设置字段
        buildTableColumns(vo, table);
        return table;
    }

    /**
     * 设置字段信息
     *
     * @param vo
     * @param table
     */
    private void buildTableColumns(GenTableVO vo, Table table) {
        List<GenTableColumnVO> columns = vo.getColumns();
        if (StringUtils.isNotEmpty(columns)) {
            for (GenTableColumnVO columnVO : columns) {
                Column column = new Column();
                MyColumnConfig myColumnConfig = new MyColumnConfig();
                BeanUtils.copyProperties(columnVO, myColumnConfig);
                column.setColumnConfig(myColumnConfig);
                column.setName(columnVO.getColumnName());
                column.setRawType(columnVO.getColumnTypeName());
                column.setRawLength(columnVO.getColumnDisplaySize());
                column.setPropertyType(columnVO.getJavaType());
                column.setAutoIncrement(columnVO.isIncrement());
                column.setComment(columnVO.getColumnComment());
                table.addColumn(column);
            }
        }
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public MyGenerator setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }
}
