package com.troy.gen.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.constant.GenConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.ResultConstants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.core.text.CharsetKit;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.common.core.web.DTO.PageDTO;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.common.security.utils.SecurityUtils;
import com.troy.gen.dao.GenTableColumnDao;
import com.troy.gen.dao.GenTableDao;
import com.troy.gen.domain.DTO.DbTableDTO;
import com.troy.gen.domain.DTO.GenTableColumnsDTO;
import com.troy.gen.domain.DTO.GenTableDTO;
import com.troy.gen.domain.VO.DbTableVO;
import com.troy.gen.domain.VO.GenTableColumnVO;
import com.troy.gen.domain.VO.GenTableVO;
import com.troy.gen.service.GenTableService;
import com.troy.gen.util.GenUtils;
import com.troy.gen.util.VelocityInitializer;
import com.troy.gen.util.VelocityUtils;
import com.troy.gen.entity.GenTableColumnEntity;
import com.troy.gen.entity.GenTableEntity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 * 代码生成业务表 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/15 16:18:30
 */
@Service
public class GenTableServiceImpl implements GenTableService {

    public static final Logger LOGGER = LoggerFactory.getLogger(GenTableServiceImpl.class);

    @Autowired
    private GenTableDao genTableDao;

    @Autowired
    private GenTableColumnDao genTableColumnDao;

    @Override
    public PageVO<GenTableVO> genTableList(PageDTO dto) {
        Page page = this.genTableDao.page(new Page<>(dto.getCurrent(),dto.getSize()));
        return PageUtils.convertPageVo(page, GenTableVO.class);
    }

    @Override
    public GenTableVO findById(Long id) {
        GenTableVO vo = null;
        GenTableEntity genTableEntity = this.genTableDao.getById(id);
        if (StringUtils.isNotNull(genTableEntity)) {
            List<GenTableColumnEntity> genTableColumnEntities = this.genTableColumnDao.findByTableId(id);
            vo = new GenTableVO();
            BeanUtils.copyProperties(genTableEntity, vo);
            this.setTableFromOptions(vo);
            if (StringUtils.isNotEmpty(genTableColumnEntities)) {
                List<GenTableColumnVO> genTableColumnVOS = new ArrayList<>();
                GenTableColumnVO genTableColumnVO = null;
                for (GenTableColumnEntity genTableColumnEntity : genTableColumnEntities) {
                    BeanUtils.copyProperties(genTableColumnEntity, genTableColumnVO);
                    genTableColumnVOS.add(genTableColumnVO);
                }
                vo.setColumns(genTableColumnVOS);
            }
        }
        return vo;
    }

    @Transactional
    @Override
    public ResultVO importTableSave(List<String> tableNames) {
        List<GenTableEntity> genTableEntities = this.genTableDao.selectDbTableListByNames(tableNames);
        Long userId = SecurityUtils.getUserId();
        try {
            for (GenTableEntity genTableEntity : genTableEntities) {
                GenUtils.initTable(genTableEntity, userId);
                if (this.genTableDao.save(genTableEntity)) {
                    List<GenTableColumnEntity> genTableColumnEntities = this.genTableColumnDao.selectDbTableColumnsByName(genTableEntity.getTableName());
                    for (GenTableColumnEntity genTableColumnEntity : genTableColumnEntities) {
                        GenUtils.initColumnField(genTableColumnEntity, genTableEntity);
                        this.genTableColumnDao.save(genTableColumnEntity);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException(ResultEnum.FAIL);
        }
        return ResultVO.success();
    }

    @Override
    public PageVO<DbTableVO> dataList(DbTableDTO dto) {
        Page<GenTableEntity> page = this.genTableDao.dataList(dto);
        PageVO pageVO = PageUtils.convertPageVo(page, DbTableVO.class);
        return pageVO;
    }

    @Transactional
    @Override
    public ResultVO editGenTable(Long id, GenTableDTO dto) {
        this.validateEdit(dto);
        GenTableEntity genTableEntity = this.genTableDao.getById(id);
        if (StringUtils.isNull(genTableEntity)) {
            throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND, ResultConstants.BUSINESS_TABLE));
        }
        BeanUtils.copyProperties(dto, genTableEntity);
        String options = JSON.toJSONString(dto.getParams());
        genTableEntity.setOptions(options);
        if (this.genTableDao.updateById(genTableEntity)) {
            List<GenTableColumnsDTO> genTableColumnsDTOS = dto.getGenTableColumnsDTOS();
            List<Long> columnsIds = genTableColumnsDTOS.stream().map(GenTableColumnsDTO::getId).distinct().collect(Collectors.toList());
            List<GenTableColumnEntity> genTableColumnEntities = this.genTableColumnDao.listByIds(columnsIds);
            if (StringUtils.isNotEmpty(genTableColumnEntities)) {
                for (GenTableColumnEntity genTableColumnEntity : genTableColumnEntities) {
                    Optional<GenTableColumnsDTO> optional = genTableColumnsDTOS.stream().filter(genTableColumnsDTO -> genTableColumnEntity.getId().equals(genTableColumnsDTO.getId()))
                            .findFirst();
                    if (optional.isPresent()) {
                        BeanUtils.copyProperties(optional.get(), genTableColumnEntity);
                    }
                }
                this.genTableColumnDao.updateBatch(genTableColumnEntities);
            }
        }
        return ResultVO.success();
    }

    @Transactional
    @Override
    public ResultVO deleteByIds(List<Long> ids) {
        this.genTableDao.removeByIds(ids);
        this.genTableColumnDao.deleteByTableIds(ids);
        return ResultVO.success();
    }

    @Override
    public Map<String, String> previewCode(Long id) {
        Map<String, String> dataMap = new LinkedHashMap<>();
        GenTableEntity genTableEntity = this.genTableDao.getById(id);
        if (StringUtils.isNotNull(genTableEntity)) {
            GenTableVO genTableVO = this.genTableVO(genTableEntity);
            VelocityInitializer.initVelocity();
            VelocityContext context = VelocityUtils.prepareContext(genTableVO);

            // 获取模板列表
            List<String> templates = VelocityUtils.getTemplateList(genTableVO.getTplCategory());
            for (String template : templates) {
                // 渲染模板
                StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, Constants.UTF8);
                tpl.merge(context, sw);
                dataMap.put(template, sw.toString());
            }

        }
        return dataMap;

    }

    @Override
    public byte[] downloadCode(Long id) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        generatorCode(id, zip);
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    @Override
    public ResultVO generatorCode(Long id) {
        GenTableEntity genTableEntity = this.genTableDao.getById(id);
        if (StringUtils.isNotNull(genTableEntity)) {
            GenTableVO genTableVO = this.genTableVO(genTableEntity);
            VelocityInitializer.initVelocity();

            VelocityContext context = VelocityUtils.prepareContext(genTableVO);

            // 获取模板列表
            List<String> templates = VelocityUtils.getTemplateList(genTableVO.getTplCategory());
            for (String template : templates) {
                if (!StringUtils.containsAny(template, GenConstants.JS_TEMPLATE)) {
                    // 渲染模板
                    StringWriter sw = new StringWriter();
                    Template tpl = Velocity.getTemplate(template, Constants.UTF8);
                    tpl.merge(context, sw);
                    try {
                        String path = getGenPath(genTableVO, template);
                        FileUtils.writeStringToFile(new File(path), sw.toString(), CharsetKit.UTF_8);
                    } catch (IOException e) {
                        throw new ServiceException(ResultEnum.getMsg(ResultEnum.RENDER_TEMPLATE_FAIL, genTableVO.getTableName()));
                    }
                }
            }
        }
        return ResultVO.success();
    }

    @Transactional
    @Override
    public ResultVO synchDb(Long id) {
        GenTableEntity genTableEntity = this.genTableDao.getById(id);
        if (StringUtils.isNotNull(genTableEntity)) {
            GenTableVO genTableVO = this.genTableVO(genTableEntity);
            List<GenTableColumnVO> genTableColumnVOS = genTableVO.getColumns();
            Map<String, GenTableColumnVO> tableColumnMap = genTableColumnVOS.stream().collect(Collectors.toMap(GenTableColumnVO::getColumnName, Function.identity()));

            List<GenTableColumnEntity> dbTableColumns = this.genTableColumnDao.selectDbTableColumnsByName(genTableVO.getTableName());
            if (StringUtils.isEmpty(dbTableColumns)) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.OPERATE_FAIL,ResultConstants.SYNC_DATA));
            }
            List<String> dbTableColumnNames = dbTableColumns.stream().map(GenTableColumnEntity::getColumnName).collect(Collectors.toList());

            dbTableColumns.forEach(column -> {
                GenUtils.initColumnField(column, genTableEntity);
                if (tableColumnMap.containsKey(column.getColumnName())) {
                    GenTableColumnVO prevColumn = tableColumnMap.get(column.getColumnName());
                    column.setId(prevColumn.getId());
                    if (column.isList()) {
                        // 如果是列表，继续保留查询方式/字典类型选项
                        column.setDictType(prevColumn.getDictType());
                        column.setQueryType(prevColumn.getQueryType());
                    }
                    if (StringUtils.isNotEmpty(prevColumn.getIsRequired()) && !column.isPk()
                            && (column.isInsert() || column.isEdit())
                            && ((column.isUsableColumn()) || (!column.isSuperColumn()))) {
                        // 如果是(新增/修改&非主键/非忽略及父属性)，继续保留必填/显示类型选项
                        column.setIsRequired(prevColumn.getIsRequired());
                        column.setHtmlType(prevColumn.getHtmlType());
                    }
                    this.genTableColumnDao.updateById(column);
                } else {
                    this.genTableColumnDao.save(column);
                }
            });

            List<GenTableColumnVO> delColumns = genTableColumnVOS.stream().filter(column -> !dbTableColumnNames.contains(column.getColumnName())).collect(Collectors.toList());
            if (StringUtils.isNotEmpty(delColumns)) {
                List<Long> columnIds = delColumns.stream().map(GenTableColumnVO::getId).distinct().collect(Collectors.toList());
                this.genTableColumnDao.removeByIds(columnIds);
            }
        }
        return ResultVO.success();
    }

    @Override
    public byte[] batchGenCode(List<Long> ids) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (Long id : ids) {
            this.generatorCode(id, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 设置代码生成其他选项值
     *
     * @param genTable 设置后的生成对象
     */
    private void setTableFromOptions(GenTableVO genTable) {
        JSONObject paramsObj = JSON.parseObject(genTable.getOptions());
        if (StringUtils.isNotNull(paramsObj)) {
            String treeCode = paramsObj.getString(GenConstants.TREE_CODE);
            String treeParentCode = paramsObj.getString(GenConstants.TREE_PARENT_CODE);
            String treeName = paramsObj.getString(GenConstants.TREE_NAME);
            String parentMenuId = paramsObj.getString(GenConstants.PARENT_MENU_ID);
            String parentMenuName = paramsObj.getString(GenConstants.PARENT_MENU_NAME);

            genTable.setTreeCode(treeCode);
            genTable.setTreeParentCode(treeParentCode);
            genTable.setTreeName(treeName);
            genTable.setParentMenuId(parentMenuId);
            genTable.setParentMenuName(parentMenuName);
        }
    }

    /**
     * 修改保存参数校验
     *
     * @param genTable
     */
    private void validateEdit(GenTableDTO genTable) {
        if (GenConstants.TPL_TREE.equals(genTable.getTplCategory())) {
            String options = JSON.toJSONString(genTable.getParams());
            JSONObject paramsObj = JSON.parseObject(options);
            if (StringUtils.isBlank(paramsObj.getString(GenConstants.TREE_CODE))) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND,ResultConstants.TREE_CODE));
            } else if (StringUtils.isBlank(paramsObj.getString(GenConstants.TREE_PARENT_CODE))) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND,ResultConstants.PARENT_TREE_CODE));
            } else if (StringUtils.isBlank(paramsObj.getString(GenConstants.TREE_NAME))) {
                throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND,ResultConstants.TREE_NAME));
            } else if (GenConstants.TPL_SUB.equals(genTable.getTplCategory())) {
                if (StringUtils.isBlank(genTable.getSubTableName())) {
                    throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND,ResultConstants.ASSOCIATED_TABLE_NAME));
                } else if (StringUtils.isBlank(genTable.getSubTableFkName())) {
                    throw new ServiceException(ResultEnum.getMsg(ResultEnum.NOT_FOUND,ResultConstants.ASSOCIATED_TABLE_FOREIGN_KEY));
                }
            }
        }
    }

    /**
     * 设置主子表信息
     *
     * @param genTableVO
     */
    private void setSubTable(GenTableVO genTableVO) {
        if (StringUtils.isNoneBlank(genTableVO.getSubTableName())) {
            GenTableEntity subTable = this.genTableDao.findByTableName(genTableVO.getSubTableName());
            if (StringUtils.isNotNull(subTable)) {
                GenTableVO subTableVO = new GenTableVO();
                BeanUtils.copyProperties(subTable, subTableVO);
                this.setTableColumn(subTableVO);
                genTableVO.setSubTable(subTableVO);
            }
        }
    }

    /**
     * 设置主表信息
     *
     * @param genTableVO
     */
    private void setMainTable(GenTableVO genTableVO) {
        List<GenTableEntity> genTableEntities = this.genTableDao.findBySubTableName(genTableVO.getTableName());
        if (StringUtils.isNotEmpty(genTableEntities)) {
            List<GenTableVO> mainTableVOs = new ArrayList<>();
            GenTableVO mainTableVO = null;
            List<Long> ids = genTableEntities.stream().map(GenTableEntity::getId).distinct().collect(Collectors.toList());
            List<GenTableColumnEntity> genTableColumnEntities = this.genTableColumnDao.findByTableIds(ids);
            GenTableColumnVO genTableColumnVO = null;
            List<GenTableColumnVO> genTableColumnVOS = null;
            for (GenTableEntity genTableEntity : genTableEntities) {
                mainTableVO = new GenTableVO();
                genTableColumnVOS = new ArrayList<>();
                BeanUtils.copyProperties(genTableEntity, mainTableVO);
                List<GenTableColumnEntity> genTableColumnEntityList = genTableColumnEntities.stream().filter(genTableColumnEntity -> genTableEntity.getId().equals(genTableColumnEntity.getTableId()))
                        .collect(Collectors.toList());
                if (StringUtils.isNotEmpty(genTableColumnEntityList)) {
                    for (GenTableColumnEntity genTableColumnEntity : genTableColumnEntityList) {
                        genTableColumnVO = new GenTableColumnVO();
                        BeanUtils.copyProperties(genTableColumnEntity, genTableColumnVO);
                        genTableColumnVOS.add(genTableColumnVO);
                    }
                    mainTableVO.setColumns(genTableColumnVOS);
                }
                mainTableVOs.add(mainTableVO);
            }
         //   genTableVO.setMainTables(mainTableVOs);
        }
    }

    /**
     * 设置主键列信息
     *
     * @param genTableVO 业务表信息
     */
    public void setPkColumn(GenTableVO genTableVO) {

        for (GenTableColumnVO column : genTableVO.getColumns()) {
            if (column.isPk()) {
                genTableVO.setPkColumn(column);
                break;
            }
        }
        if (StringUtils.isNull(genTableVO.getPkColumn())) {
            genTableVO.setPkColumn(genTableVO.getColumns().get(0));
        }
        if (GenConstants.TPL_SUB.equals(genTableVO.getTplCategory())) {
            for (GenTableColumnVO column : genTableVO.getSubTable().getColumns()) {
                if (column.isPk()) {
                    genTableVO.getSubTable().setPkColumn(column);
                    break;
                }
            }
            if (StringUtils.isNull(genTableVO.getSubTable().getPkColumn())) {
                genTableVO.getSubTable().setPkColumn(genTableVO.getSubTable().getColumns().get(0));
            }
        }

//        if (genTableVO.isMain()) {
//        //    List<GenTableVO> mainTables = genTableVO.getMainTables();
//            List<GenTableVO> mainTables=new ArrayList<>();
//            for (GenTableVO mainTable : mainTables) {
//                if (StringUtils.isNotEmpty(mainTable.getColumns())) {
//                    for (GenTableColumnVO column : mainTable.getColumns()) {
//                        if (column.isPk()) {
//                            mainTable.setPkColumn(column);
//                            break;
//                        }
//                    }
//                    if (StringUtils.isNull(mainTable.getPkColumn())) {
//                        mainTable.setPkColumn(genTableVO.getColumns().get(0));
//                    }
//                }
//            }
//        }
    }

    /**
     * 设置表字段信息
     *
     * @param genTableVO
     */
    private void setTableColumn(GenTableVO genTableVO) {
        List<GenTableColumnEntity> genTableColumnEntities = this.genTableColumnDao.findByTableId(genTableVO.getId());
        if (StringUtils.isNotEmpty(genTableColumnEntities)) {
            List<GenTableColumnVO> genTableColumnVOS = new ArrayList<>();
            GenTableColumnVO genTableColumnVO = null;
            for (GenTableColumnEntity genTableColumnEntity : genTableColumnEntities) {
                genTableColumnVO = new GenTableColumnVO();
                BeanUtils.copyProperties(genTableColumnEntity, genTableColumnVO);
                genTableColumnVOS.add(genTableColumnVO);
            }
            genTableVO.setColumns(genTableColumnVOS);
        }
    }

    /**
     * 查询表信息并生成代码
     */
    private void generatorCode(Long id, ZipOutputStream zip) {
        // 查询表信息
        GenTableEntity genTableEntity = this.genTableDao.getById(id);
        if (StringUtils.isNotNull(genTableEntity)) {
            GenTableVO genTableVO = this.genTableVO(genTableEntity);
            VelocityInitializer.initVelocity();

            VelocityContext context = VelocityUtils.prepareContext(genTableVO);

            // 获取模板列表
            List<String> templates = VelocityUtils.getTemplateList(genTableVO.getTplCategory());
            for (String template : templates) {
                // 渲染模板
                StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, Constants.UTF8);
                tpl.merge(context, sw);
                try {
                    // 添加到zip
                    zip.putNextEntry(new ZipEntry(VelocityUtils.getFileName(template, genTableVO)));
                    IOUtils.write(sw.toString(), zip, Constants.UTF8);
                    IOUtils.closeQuietly(sw);
                    zip.flush();
                    zip.closeEntry();
                } catch (IOException e) {
                    LOGGER.error("渲染模板失败，表名：" + genTableVO.getTableName(), e);
                }
            }
        }
    }

    /**
     * 获取genTableVO
     *
     * @param genTable
     * @return
     */
    private GenTableVO genTableVO(GenTableEntity genTable) {
        GenTableVO genTableVO = new GenTableVO();
        BeanUtils.copyProperties(genTable, genTableVO);
        //设置字段
        setTableColumn(genTableVO);
        // 设置主子表信息
        setSubTable(genTableVO);
        // 设置主表信息
        setMainTable(genTableVO);
        // 设置主键列信息
        setPkColumn(genTableVO);

        return genTableVO;
    }

    /**
     * 获取代码生成地址
     *
     * @param table    业务表信息
     * @param template 模板文件路径
     * @return 生成地址
     */
    public static String getGenPath(GenTableVO table, String template) {
        String genPath = table.getGenPath();
        if (StringUtils.equals(genPath, "/")) {
            return System.getProperty("user.dir") + File.separator + "src" + File.separator + VelocityUtils.getFileName(template, table);
        }
        return genPath + File.separator + VelocityUtils.getFileName(template, table);
    }
}
