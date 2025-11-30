package com.xqy.service.impl.handler;

import com.xqy.dto.QueryNodeTreeDto;
import com.xqy.entity.ApiInfo;
import com.xqy.enums.ApiType;
import com.xqy.service.DynamicDataSourceExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 列表API处理器
 */
@Slf4j
@Component("LIST")
public record ListApiHandler() implements ApiHandler {

    @Override
    public Object handle(ApiInfo apiInfo, QueryNodeTreeDto queryNode, DynamicDataSourceExecutor dataSourceExecutor, Map<String, Object> params) {
        log.info("执行列表API: {}", apiInfo.getApiName());

        try {
            String sql = queryNode.getSqlContent();
            Integer dataSourceId = queryNode.getDataSourceId();
            // 执行查询 - 返回多行结果
            List<Map<String, Object>> list = dataSourceExecutor.executeQueryForList(dataSourceId, sql, params);
            NodeExecutor.execute(list, queryNode, dataSourceExecutor, params);
            return list;

        } catch (Exception e) {
            log.error("列表查询执行失败", e);
            throw new RuntimeException("列表查询失败: " + e.getMessage(), e);
        }
    }
}