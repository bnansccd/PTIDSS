package com.troy.gen;

import com.troy.gen.dao.GenTableColumnDao;
import com.troy.gen.entity.GenTableColumnEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Auther: zhuqing
 * @Date: 2022/8/16 16:16:55
 * @Description: LowCodeGenApplicationTests
 * @Version: 1.0.0
 */
@SpringBootTest
public class LowCodeGenApplicationTests {

    @Autowired
    private GenTableColumnDao genTableColumnDao;

    @Test
    void context() {
        List<GenTableColumnEntity> dbTableColumnVOS = this.genTableColumnDao.selectDbTableColumnsByName("t_sys_user");
        System.err.println(dbTableColumnVOS);
    }
}
