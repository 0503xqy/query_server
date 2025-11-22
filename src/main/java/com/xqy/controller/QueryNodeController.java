package com.xqy.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqy.common.Result;
import com.xqy.entity.QueryNode;
import com.xqy.service.QueryNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 查询节点控制器
 */
@RestController
@RequestMapping("/query/node")
@RequiredArgsConstructor
public class QueryNodeController {

    private final QueryNodeService queryNodeService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<Page<QueryNode>> page(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size) {
        Page<QueryNode> page = queryNodeService.page(new Page<>(current, size));
        return Result.success(page);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<QueryNode> getById(@PathVariable Integer id) {
        QueryNode queryNode = queryNodeService.getById(id);
        return Result.success(queryNode);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> save(@RequestBody QueryNode queryNode) {
        queryNodeService.save(queryNode);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody QueryNode queryNode) {
        queryNodeService.updateById(queryNode);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        queryNodeService.removeById(id);
        return Result.success();
    }
}
