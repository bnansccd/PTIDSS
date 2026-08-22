package com.troy.form.controller.web;

import com.mybatisflex.core.paginate.Page;
import com.troy.form.service.AppTypeService;
import com.troy.form.entity.AppTypeEntity;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 *  控制层。
 *
 * @author chenxl
 * @since 2023-11-02 13:28:36
 */
@RestController
@RequestMapping("/appTypeEntity")
@Api(tags = "app类型管理")
public class AppTypeController {

    @Autowired
    private AppTypeService appTypeService;

    /**
     * 添加。
     *
     * @param appTypeEntity 
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody AppTypeEntity appTypeEntity) {
        return appTypeService.save(appTypeEntity);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return appTypeService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param appTypeEntity 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody AppTypeEntity appTypeEntity) {
        return appTypeService.updateById(appTypeEntity);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<AppTypeEntity> list() {
        return appTypeService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public AppTypeEntity getInfo(@PathVariable Serializable id) {
        return appTypeService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<AppTypeEntity> page(Page<AppTypeEntity> page) {
        return appTypeService.page(page);
    }

}
