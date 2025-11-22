package com.xqy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqy.common.Result;
import com.xqy.entity.QueryNodeRelation;
import com.xqy.service.QueryNodeRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 查询节点关系控制器
 */
@RestController
@RequestMapping("/query/relation")
@RequiredArgsConstructor
public class QueryNodeRelationController {

    private final QueryNodeRelationService relationService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<Page<QueryNodeRelation>> page(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        Page<QueryNodeRelation> page = relationService.page(new Page<>(current, size));
        return Result.success(page);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<QueryNodeRelation> getById(@PathVariable Integer id) {
        QueryNodeRelation relation = relationService.getById(id);
        return Result.success(relation);
    }

    /**
     * 根据节点ID查询关系
     */
    @GetMapping("/node/{nodeId}")
    public Result<List<QueryNodeRelation>> getByNodeId(@PathVariable Integer nodeId) {
        LambdaQueryWrapper<QueryNodeRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QueryNodeRelation::getNodeId, nodeId);
        List<QueryNodeRelation> list = relationService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> save(@RequestBody QueryNodeRelation relation) {
        relationService.save(relation);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody QueryNodeRelation relation) {
        relationService.updateById(relation);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        relationService.removeById(id);
        return Result.success();
    }
}
