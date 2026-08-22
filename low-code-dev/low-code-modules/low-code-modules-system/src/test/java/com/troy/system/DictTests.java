package com.troy.system;

import com.troy.common.core.constant.Constants;
import com.troy.common.core.enums.DictTypeEnums;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.system.dao.SysDictDao;
import com.troy.system.entity.SysDictEntity;
import com.troy.system.service.SysDictService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

/**
 * @author sym
 * @since 2024/7/5 15:40
 */
@SpringBootTest
@Slf4j
public class DictTests {

    @Autowired
    private SysDictDao sysDictDao;

    @Autowired
    private SysDictService sysDictService;


    @Test
    void saveDict() {
        List<DictTypeEnums> list = Arrays.asList(DictTypeEnums.FB_DATA_SOURCE);
        for (DictTypeEnums dictTypeEnums : list) {
            SysDictEntity sysDictEntity = new SysDictEntity();
            sysDictEntity.setDictName(dictTypeEnums.getName());
            sysDictEntity.setDictType(dictTypeEnums.getCode());
            sysDictEntity.setSort(this.sysDictService.getCurrentSort(null));
            this.sysDictDao.save(sysDictEntity);
            List<DictValueEnums> dictValueEnums = DictValueEnums.findByType(dictTypeEnums.getCode());
            for (int i = 0; i < dictValueEnums.size(); i++) {
                DictValueEnums dictValueEnums1 = dictValueEnums.get(i);
                SysDictEntity sysDictEntity1 = new SysDictEntity();
                sysDictEntity1.setDictName(dictValueEnums1.getName());
                sysDictEntity1.setDictType(dictValueEnums1.getCode());
                sysDictEntity1.setSort(i * Constants.TEN);
                sysDictEntity1.setParentType(sysDictEntity.getDictType());
                sysDictEntity1.setParentId(sysDictEntity.getId());
                this.sysDictDao.save(sysDictEntity1);
            }
        }
    }

}
