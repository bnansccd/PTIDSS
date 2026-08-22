package com.troy.form.controller.web;

import com.mybatisflex.core.paginate.Page;
import com.troy.form.service.DbColumnService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.troy.form.entity.DbColumnEntity;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 *  控制层。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@RestController
@RequestMapping("/dbColumnEntity")
public class DbColumnController {

    @Autowired
    private DbColumnService dbColumnService;

    /**
     * 添加。
     *
     * @param dbColumnEntity 
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody DbColumnEntity dbColumnEntity) {
        return dbColumnService.save(dbColumnEntity);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return dbColumnService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param dbColumnEntity 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody DbColumnEntity dbColumnEntity) {
        return dbColumnService.updateById(dbColumnEntity);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<DbColumnEntity> list() {
        return dbColumnService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public DbColumnEntity getInfo(@PathVariable Serializable id) {
        return dbColumnService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<DbColumnEntity> page(Page<DbColumnEntity> page) {
        return dbColumnService.page(page);
    }

}
