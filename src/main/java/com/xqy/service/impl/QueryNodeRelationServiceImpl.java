package com.xqy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqy.entity.QueryNodeRelation;
import com.xqy.mapper.QueryNodeRelationMapper;
import com.xqy.service.QueryNodeRelationService;
import org.springframework.stereotype.Service;

/**
 * 查询节点关系Service实现类
 */
@Service
public class QueryNodeRelationServiceImpl extends ServiceImpl<QueryNodeRelationMapper, QueryNodeRelation> implements QueryNodeRelationService {
}
