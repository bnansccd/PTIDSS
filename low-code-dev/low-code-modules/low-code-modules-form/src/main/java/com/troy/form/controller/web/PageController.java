package com.troy.form.controller.web;

import com.mybatisflex.core.paginate.Page;
import com.troy.form.service.PageService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.troy.form.entity.PageEntity;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 *  控制层。
 *
 * @author chenxl
 * @since 2023-11-10 16:48:54
 */
@RestController
@RequestMapping("/pageEntity")
public class PageController {

    @Autowired
    private PageService pageService;

    /**
     * 添加。
     *
     * @param pageEntity 
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody PageEntity pageEntity) {
        return pageService.save(pageEntity);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return pageService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param pageEntity 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody PageEntity pageEntity) {
        return pageService.updateById(pageEntity);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<PageEntity> list() {
        return pageService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public PageEntity getInfo(@PathVariable Serializable id) {
        return pageService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<PageEntity> page(Page<PageEntity> page) {
        return pageService.page(page);
    }

}
