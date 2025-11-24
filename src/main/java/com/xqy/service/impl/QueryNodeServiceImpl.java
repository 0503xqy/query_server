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
import org.springframework.util.CollectionUtils;

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

    @Override
    public QueryNodeTreeDto buildExecutionTree(Long rootNodeId) {
        List<QueryNodeTreeDto> queryNodeTreeDtoList = buildExecutionTree(rootNodeId, true, 0);
        if (!CollectionUtils.isEmpty(queryNodeTreeDtoList)) {
            return queryNodeTreeDtoList.get(0);
        }
        return null;
    }

    /**
     * 根据根节点ID构建执行树
     */
    @Override
    public List<QueryNodeTreeDto> buildExecutionTree(Long rootNodeId, boolean isRootNode, int level) {
        log.info("开始构建执行树，根节点ID: {}", rootNodeId);



        List<QueryNode> nodeList = isRootNode ? lambdaQuery().eq(QueryNode::getId, rootNodeId).list() : lambdaQuery().eq(QueryNode::getParentId, rootNodeId).list();

        if (CollectionUtils.isEmpty(nodeList)) {
            return List.of();
        }
        // 转换为DTO
        List<QueryNodeTreeDto> rootDtoList = nodeList.stream().map(node -> convertToDto(node, level)).toList();

        rootDtoList.forEach(rootDto -> {
            List<QueryNodeTreeDto> children = buildExecutionTree(rootDto.getId(), false, rootDto.getLevel() + 1);
        });

        // 递归构建子节点

        log.info("执行树构建完成，根节点: {}", rootDtoList.get(0).getNodeName());
        return rootDtoList;
    }

    @Override
    public List<QueryNodeTreeDto> getNodeChildren(Long nodeId) {
        return List.of();
    }

    @Override
    public List<QueryNode> getExecutionOrder(Long rootNodeId) {
        return List.of();
    }


    /**
     * 将QueryNode转换为QueryNodeTreeDto
     */
    private QueryNodeTreeDto convertToDto(QueryNode node, Integer level) {
        QueryNodeTreeDto dto = new QueryNodeTreeDto();

        // 复制节点基本信息
        BeanUtils.copyProperties(node, dto);

        // 设置层级
        if (level != null) {
            dto.setLevel(level);
        }

        return dto;
    }
}
