package com.xqy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqy.entity.QueryNode;
import com.xqy.mapper.QueryNodeMapper;
import com.xqy.service.QueryNodeService;
import org.springframework.stereotype.Service;

/**
 * 查询节点Service实现类
 */
@Service
public class QueryNodeServiceImpl extends ServiceImpl<QueryNodeMapper, QueryNode> implements QueryNodeService {
}
