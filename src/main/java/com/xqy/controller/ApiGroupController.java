package com.xqy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqy.common.Result;
import com.xqy.entity.ApiGroup;
import com.xqy.service.ApiGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API分组控制器
 */
@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class ApiGroupController {

    private final ApiGroupService apiGroupService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<Page<ApiGroup>> page(@RequestParam(defaultValue = "1") Integer current,
                                        @RequestParam(defaultValue = "10") Integer size) {
        Page<ApiGroup> page = apiGroupService.page(new Page<>(current, size));
        return Result.success(page);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<ApiGroup> getById(@PathVariable Integer id) {
        ApiGroup apiGroup = apiGroupService.getById(id);
        return Result.success(apiGroup);
    }

    /**
     * 根据父级ID查询子分组
     */
    @GetMapping("/children/{parentId}")
    public Result<List<ApiGroup>> getChildren(@PathVariable Integer parentId) {
        LambdaQueryWrapper<ApiGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiGroup::getParentId, parentId);
        List<ApiGroup> list = apiGroupService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> save(@RequestBody ApiGroup apiGroup) {
        apiGroupService.save(apiGroup);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody ApiGroup apiGroup) {
        apiGroupService.updateById(apiGroup);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        apiGroupService.removeById(id);
        return Result.success();
    }
}
