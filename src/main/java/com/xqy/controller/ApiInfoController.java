package com.xqy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqy.common.Result;
import com.xqy.entity.ApiInfo;
import com.xqy.service.ApiInfoService;
import com.xqy.service.DynamicApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API信息控制器
 */
@RestController
@RequestMapping("/api/info")
@RequiredArgsConstructor
public class ApiInfoController {

    private final ApiInfoService apiInfoService;
    private final DynamicApiService dynamicApiService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<Page<ApiInfo>> page(@RequestParam(defaultValue = "1") Integer current,
                                       @RequestParam(defaultValue = "10") Integer size,
                                       @RequestParam(required = false) Integer groupId) {
        Page<ApiInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<ApiInfo> wrapper = new LambdaQueryWrapper<>();
        if (groupId != null) {
            wrapper.eq(ApiInfo::getGroupId, groupId);
        }
        apiInfoService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<ApiInfo> getById(@PathVariable Integer id) {
        ApiInfo apiInfo = apiInfoService.getById(id);
        return Result.success(apiInfo);
    }

    /**
     * 根据分组ID查询API列表
     */
    @GetMapping("/group/{groupId}")
    public Result<List<ApiInfo>> getByGroupId(@PathVariable Integer groupId) {
        LambdaQueryWrapper<ApiInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiInfo::getGroupId, groupId);
        List<ApiInfo> list = apiInfoService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> save(@RequestBody ApiInfo apiInfo) {
        apiInfoService.save(apiInfo);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody ApiInfo apiInfo) {
        apiInfoService.updateById(apiInfo);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        apiInfoService.removeById(id);
        return Result.success();
    }

    /**
     * 发布API
     * 更新指定ID的API配置并重新加载动态API
     */
    @PostMapping("/publish/{id}")
    public Result<Void> publish(@PathVariable Long id, @RequestBody ApiInfo apiInfo) {
        // 设置ID
        apiInfo.setId(id);
        
        // 更新API配置
        apiInfoService.updateById(apiInfo);
        
        // 重新加载动态API配置
        dynamicApiService.reloadApiConfigs();
        
        return Result.success();
    }
}
