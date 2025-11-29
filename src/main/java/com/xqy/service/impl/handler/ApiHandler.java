package com.xqy.service.impl.handler;

import com.xqy.dto.QueryNodeTreeDto;
import com.xqy.entity.ApiInfo;
import com.xqy.service.DynamicDataSourceExecutor;

import java.util.Map;

/**
 * API处理器接口
 */
public interface ApiHandler {
    /**
     * 处理API请求
     *
     * @param apiInfo API信息
     * @param queryNode 查询节点
     * @param dataSourceExecutor 数据源执行器
     * @param params             请求参数
     * @return 处理结果
     */
    Object handle(ApiInfo apiInfo, QueryNodeTreeDto queryNode,
                  DynamicDataSourceExecutor dataSourceExecutor, Map<String, Object> params);
}