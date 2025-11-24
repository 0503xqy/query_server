package com.xqy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xqy.dto.QueryNodeTreeDto;
import com.xqy.entity.QueryNode;

import java.util.List;

/**
 * 查询节点Service接口
 */
public interface QueryNodeService extends IService<QueryNode> {


    /**
     * 根据根节点ID构建执行树
     *
     * @param rootNodeId 根节点ID
     * @return 执行树
     */
    QueryNodeTreeDto buildExecutionTree(Long rootNodeId);

    /**
     * 根据根节点ID构建执行树
     *
     * @param rootNodeId 根节点ID
     * @return 执行树
     */
    List<QueryNodeTreeDto> buildExecutionTree(Long rootNodeId, boolean isRootNode, int level);

    /**
     * 获取节点的所有子节点（递归）
     *
     * @param nodeId 节点ID
     * @return 子节点列表
     */
    List<QueryNodeTreeDto> getNodeChildren(Long nodeId);

    /**
     * 获取执行树的所有节点（按执行顺序）
     *
     * @param rootNodeId 根节点ID
     * @return 按执行顺序排列的节点列表
     */
    List<QueryNode> getExecutionOrder(Long rootNodeId);
}
