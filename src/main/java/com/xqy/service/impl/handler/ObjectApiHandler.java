package com.xqy.service.impl.handler;

import com.xqy.dto.QueryNodeTreeDto;
import com.xqy.entity.ApiInfo;
import com.xqy.enums.QueryNodeType;
import com.xqy.service.DynamicDataSourceExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象API处理器
 */
@Slf4j
@Component("MAP")
public record ObjectApiHandler() implements ApiHandler {

    @Override
    public Object handle(ApiInfo apiInfo, QueryNodeTreeDto queryNode, DynamicDataSourceExecutor dataSourceExecutor, Map<String, Object> params) {
        log.info("执行对象API: {}", apiInfo.getApiName());

        try {
            String sql = queryNode.getSqlContent();
            Integer dataSourceId = queryNode.getDataSourceId();
            QueryNodeType nodeType = queryNode.getNodeType();

            // 根据节点类型执行不同的查询
            return switch (nodeType) {
                case ROWS -> dataSourceExecutor.executeQueryForList(dataSourceId, sql, params);
                case VALUE -> {
                    Object value = dataSourceExecutor.executeQueryForObject(dataSourceId, sql, params);
                    Map<String, Object> result = new HashMap<>();
                    result.put("value", value);
                    yield result;
                }
                case COLUMN -> {
                    List<Map<String, Object>> list = dataSourceExecutor.executeQueryForList(dataSourceId, sql, params);
                    // 提取第一列的值
                    List<Object> column = list.stream()
                            .map(row -> row.values().iterator().next())
                            .toList();
                    Map<String, Object> result = new HashMap<>();
                    result.put("values", column);
                    yield result;
                }
                default -> dataSourceExecutor.executeQueryForMap(dataSourceId, sql, params);
            };

        } catch (Exception e) {
            log.error("对象查询执行失败", e);
            throw new RuntimeException("对象查询失败: " + e.getMessage(), e);
        }
    }
}