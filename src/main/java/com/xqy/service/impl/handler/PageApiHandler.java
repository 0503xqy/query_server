package com.xqy.service.impl.handler;

import com.xqy.dto.QueryNodeTreeDto;
import com.xqy.entity.ApiInfo;
import com.xqy.service.DynamicDataSourceExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页API处理器
 */
@Slf4j
@Component("PAGE")
public record PageApiHandler() implements ApiHandler {

    @Override
    public Object handle(ApiInfo apiInfo, QueryNodeTreeDto queryNode, DynamicDataSourceExecutor dataSourceExecutor, Map<String, Object> params) {
        log.info("执行分页API: {}", apiInfo.getApiName());

        // 获取分页参数
        int current = params.containsKey("current") ? Integer.parseInt(params.get("current").toString()) : 1;
        int size = params.containsKey("size") ? Integer.parseInt(params.get("size").toString()) : 10;
        int offset = (current - 1) * size;

        try {
            String sql = queryNode.getSqlContent();
            Integer dataSourceId = queryNode.getDataSourceId();

            // 构建分页SQL
            String countSql = "SELECT COUNT(*) FROM (" + sql + ") AS temp_count";
            String dataSql = sql + " LIMIT " + size + " OFFSET " + offset;

            // 执行查询
            Object totalObj = dataSourceExecutor.executeQueryForObject(dataSourceId, countSql, params);
            long total = totalObj != null ? Long.parseLong(totalObj.toString()) : 0;
            List<Map<String, Object>> records = dataSourceExecutor.executeQueryForList(dataSourceId, dataSql, params);

            return buildPageResult(records, total, current, size);

        } catch (Exception e) {
            log.error("分页查询执行失败", e);
            throw new RuntimeException("分页查询失败: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> buildPageResult(List<Map<String, Object>> records, long total, int current, int size) {
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("current", current);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        return result;
    }
}