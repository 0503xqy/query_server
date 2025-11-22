package com.xqy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqy.common.Result;
import com.xqy.entity.DataSource;
import com.xqy.service.DataSourceService;
import com.xqy.service.DynamicDataSourceExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据源控制器
 */
@RestController
@RequestMapping("/datasource")
@RequiredArgsConstructor
public class DataSourceController {

    private final DataSourceService dataSourceService;
    private final DynamicDataSourceExecutor dataSourceExecutor;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<Page<DataSource>> page(@RequestParam(defaultValue = "1") Integer current,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) String name,
                                          @RequestParam(required = false) String type) {
        Page<DataSource> page = new Page<>(current, size);
        LambdaQueryWrapper<DataSource> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(DataSource::getName, name);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(DataSource::getType, type);
        }
        
        dataSourceService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 查询所有数据源
     */
    @GetMapping("/list")
    public Result<List<DataSource>> list() {
        List<DataSource> list = dataSourceService.list();
        return Result.success(list);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<DataSource> getById(@PathVariable Integer id) {
        DataSource dataSource = dataSourceService.getById(id);
        return Result.success(dataSource);
    }

    /**
     * 获取默认数据源
     */
    @GetMapping("/default")
    public Result<DataSource> getDefault() {
        DataSource dataSource = dataSourceService.getDefaultDataSource();
        return Result.success(dataSource);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> save(@RequestBody DataSource dataSource) {
        dataSourceService.save(dataSource);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody DataSource dataSource) {
        dataSourceService.updateById(dataSource);
        return Result.success();
    }

    /**
     * 设置默认数据源
     */
    @PutMapping("/default/{id}")
    public Result<Void> setDefault(@PathVariable Integer id) {
        dataSourceService.setDefaultDataSource(id);
        return Result.success();
    }

    /**
     * 测试数据源连接
     */
    @PostMapping("/test")
    public Result<Boolean> testConnection(@RequestBody DataSource dataSource) {
        boolean success = dataSourceExecutor.testConnection(dataSource);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error("数据源连接失败");
        }
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        dataSourceService.removeById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        dataSourceService.removeByIds(ids);
        return Result.success();
    }
}
