package com.troy.gen.service.impl;

import com.troy.common.core.utils.StringUtils;
import com.troy.gen.dao.GenTableColumnDao;
import com.troy.gen.service.GenTableColumnService;
import com.troy.gen.domain.VO.GenTableColumnVO;
import com.troy.gen.entity.GenTableColumnEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 代码生成业务表字段 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/15 16:18:30
 */
@Service
public class GenTableColumnServiceImpl implements GenTableColumnService {

    @Autowired
    private GenTableColumnDao genTableColumnDao;

    @Override
    public List<GenTableColumnVO> findByTableId(Long tableId) {
        List<GenTableColumnEntity> genTableColumnEntities = this.genTableColumnDao.findByTableId(tableId);
        List<GenTableColumnVO> vos = new ArrayList<>();
        if (StringUtils.isNotEmpty(genTableColumnEntities)) {
            GenTableColumnVO vo = null;
            for (GenTableColumnEntity genTableColumnEntity : genTableColumnEntities) {
                vo = new GenTableColumnVO();
                BeanUtils.copyProperties(genTableColumnEntity, vo);
                vos.add(vo);
            }
        }
        return vos;
    }
}
