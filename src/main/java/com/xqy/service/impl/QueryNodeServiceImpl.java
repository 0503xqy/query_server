package com.xqy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqy.dto.QueryNodeTreeDto;
import com.xqy.entity.DataSource;
import com.xqy.entity.QueryNode;
import com.xqy.entity.QueryNodeRelation;
import com.xqy.enums.RelationType;
import com.xqy.mapper.QueryNodeMapper;
import com.xqy.service.DataSourceService;
import com.xqy.service.QueryNodeRelationService;
import com.xqy.service.QueryNodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * 查询节点Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryNodeServiceImpl extends ServiceImpl<QueryNodeMapper, QueryNode> implements QueryNodeService {

    private final QueryNodeRelationService relationService;
    private final DataSourceService dataSourceService;

    /**
     * 根据根节点ID构建执行树
     */
    @Override
    public QueryNodeTreeDto buildExecutionTree(Integer rootNodeId) {
        log.info("开始构建执行树，根节点ID: {}", rootNodeId);

        // 获取根节点
        QueryNode rootNode = this.getById(rootNodeId);
        if (rootNode == null) {
            log.warn("根节点不存在: {}", rootNodeId);
            throw new RuntimeException("根节点不存在: " + rootNodeId);
        }

        // 转换为DTO
        QueryNodeTreeDto rootDto = convertToDto(rootNode, null, 0);

        // 递归构建子节点
        buildChildrenRecursive(rootDto, 0);

        log.info("执行树构建完成，根节点: {}", rootNode.getNodeName());
        return rootDto;
    }

    /**
     * 获取节点的所有子节点（递归）
     */
    @Override
    public List<QueryNodeTreeDto> getNodeChildren(Integer nodeId) {
        // 查询该节点的子节点关系
        LambdaQueryWrapper<QueryNodeRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QueryNodeRelation::getParentId, nodeId)
                .eq(QueryNodeRelation::getRelationType, RelationType.CHILD);
        List<QueryNodeRelation> relations = relationService.list(wrapper);

        if (relations.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取子节点信息
        List<Integer> childIds = relations.stream()
                .map(QueryNodeRelation::getNodeId)
                .collect(Collectors.toList());

        List<QueryNode> childNodes = this.listByIds(childIds);

        // 转换为DTO并递归构建
        return childNodes.stream().map(node -> {
            QueryNodeRelation relation = relations.stream()
                    .filter(r -> r.getNodeId().equals(node.getId()))
                    .findFirst()
                    .orElse(null);
            return convertToDto(node, relation, null);
        }).collect(Collectors.toList());
    }

    /**
     * 获取执行树的所有节点（按执行顺序）
     * 使用广度优先遍历(BFS)获取执行顺序
     */
    @Override
    public List<QueryNode> getExecutionOrder(Integer rootNodeId) {
        log.info("获取执行顺序，根节点ID: {}", rootNodeId);

        List<QueryNode> executionOrder = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(rootNodeId);

        while (!queue.isEmpty()) {
            Integer currentNodeId = queue.poll();
            QueryNode currentNode = this.getById(currentNodeId);

            if (currentNode != null) {
                executionOrder.add(currentNode);

                // 查找子节点
                LambdaQueryWrapper<QueryNodeRelation> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(QueryNodeRelation::getParentId, currentNodeId)
                        .eq(QueryNodeRelation::getRelationType, RelationType.CHILD);
                List<QueryNodeRelation> relations = relationService.list(wrapper);

                // 将子节点加入队列
                relations.stream()
                        .map(QueryNodeRelation::getNodeId)
                        .forEach(queue::offer);
            }
        }

        log.info("执行顺序获取完成，共 {} 个节点", executionOrder.size());
        return executionOrder;
    }

    /**
     * 递归构建子节点
     */
    private void buildChildrenRecursive(QueryNodeTreeDto parentDto, int level) {
        // 查询子节点关系
        LambdaQueryWrapper<QueryNodeRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QueryNodeRelation::getParentId, parentDto.getId())
                .eq(QueryNodeRelation::getRelationType, RelationType.CHILD);
        List<QueryNodeRelation> relations = relationService.list(wrapper);

        if (relations.isEmpty()) {
            parentDto.setHasChildren(false);
            parentDto.setChildrenList(new ArrayList<>());
            return;
        }

        parentDto.setHasChildren(true);

        // 获取子节点
        List<Integer> childIds = relations.stream()
                .map(QueryNodeRelation::getNodeId)
                .collect(Collectors.toList());

        List<QueryNode> childNodes = this.listByIds(childIds);

        // 转换并递归构建
        List<QueryNodeTreeDto> children = new ArrayList<>();
        for (QueryNode childNode : childNodes) {
            QueryNodeRelation relation = relations.stream()
                    .filter(r -> r.getNodeId().equals(childNode.getId()))
                    .findFirst()
                    .orElse(null);

            QueryNodeTreeDto childDto = convertToDto(childNode, relation, level + 1);
            buildChildrenRecursive(childDto, level + 1);
            children.add(childDto);
        }

        parentDto.setChildrenList(children);
    }

    /**
     * 将QueryNode转换为QueryNodeTreeDto
     */
    private QueryNodeTreeDto convertToDto(QueryNode node, QueryNodeRelation relation, Integer level) {
        QueryNodeTreeDto dto = new QueryNodeTreeDto();

        // 复制节点基本信息
        BeanUtils.copyProperties(node, dto);

        // 设置关系信息
        if (relation != null) {
            dto.setParentId(relation.getParentId());
            dto.setRelationType(relation.getRelationType());
        }

        // 设置层级
        if (level != null) {
            dto.setLevel(level);
        }

        // 获取数据源名称
        if (node.getDataSourceId() != null) {
            DataSource dataSource = dataSourceService.getById(node.getDataSourceId());
            if (dataSource != null) {
                dto.setDataSourceName(dataSource.getName());
            }
        }

        return dto;
    }
}
