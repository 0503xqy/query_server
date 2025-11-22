package com.xqy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xqy.entity.ApiInfo;
import com.xqy.entity.QueryNode;
import com.xqy.service.ApiInfoService;
import com.xqy.service.DynamicApiService;
import com.xqy.service.DynamicDataSourceExecutor;
import com.xqy.service.QueryNodeService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态API执行服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicApiServiceImpl implements DynamicApiService {

    private final ApiInfoService apiInfoService;
    private final QueryNodeService queryNodeService;
    private final DynamicDataSourceExecutor dataSourceExecutor;

    /**
     * API配置缓存
     * key: apiPath + ":" + method
     * value: ApiInfo
     */
    private final Map<String, ApiInfo> apiCache = new ConcurrentHashMap<>();

    /**
     * API处理器缓存
     * key: apiPath + ":" + method
     * value: ApiHandler
     */
    private final Map<String, ApiHandler> handlerCache = new ConcurrentHashMap<>();

    /**
     * 项目启动时初始化API配置
     */
    @PostConstruct
    @Override
    public void initApiConfigs() {
        log.info("开始初始化API配置...");
        
        try {
            // 清空缓存
            apiCache.clear();
            handlerCache.clear();

            // 从数据库加载所有API配置
            List<ApiInfo> apiInfoList = apiInfoService.list();
            
            if (apiInfoList == null || apiInfoList.isEmpty()) {
                log.warn("未找到任何API配置信息");
                return;
            }

            // 遍历所有API配置，构建缓存和处理器
            for (ApiInfo apiInfo : apiInfoList) {
                String cacheKey = buildCacheKey(apiInfo.getApiPath(), apiInfo.getApiMethod());
                
                // 缓存API信息
                apiCache.put(cacheKey, apiInfo);
                
                // 创建并缓存API处理器
                ApiHandler handler = createApiHandler(apiInfo);
                handlerCache.put(cacheKey, handler);
                
                log.info("成功注册API: {} {} - {}", apiInfo.getApiMethod(), apiInfo.getApiPath(), apiInfo.getApiName());
            }

            log.info("API配置初始化完成，共加载 {} 个API", apiInfoList.size());
            
        } catch (Exception e) {
            log.error("初始化API配置失败", e);
            throw new RuntimeException("初始化API配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行动态API请求
     */
    @Override
    public Object executeApi(String apiPath, String method, Map<String, Object> params, HttpServletRequest request) {
        String cacheKey = buildCacheKey(apiPath, method);
        
        // 从缓存获取处理器
        ApiHandler handler = handlerCache.get(cacheKey);
        if (handler == null) {
            log.warn("未找到API处理器: {} {}", method, apiPath);
            throw new RuntimeException("API不存在: " + method + " " + apiPath);
        }

        try {
            log.info("开始执行API: {} {}, 参数: {}", method, apiPath, params);
            
            // 执行处理器
            Object result = handler.handle(params, request);
            
            log.info("API执行成功: {} {}", method, apiPath);
            return result;
            
        } catch (Exception e) {
            log.error("API执行失败: {} {}", method, apiPath, e);
            throw new RuntimeException("API执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 重新加载API配置
     */
    @Override
    public void reloadApiConfigs() {
        log.info("开始重新加载API配置...");
        initApiConfigs();
        log.info("API配置重新加载完成");
    }

    /**
     * 根据路径和方法获取API信息
     */
    @Override
    public ApiInfo getApiInfo(String apiPath, String method) {
        String cacheKey = buildCacheKey(apiPath, method);
        return apiCache.get(cacheKey);
    }

    /**
     * 构建缓存键
     */
    private String buildCacheKey(String apiPath, String method) {
        return apiPath + ":" + method.toUpperCase();
    }

    /**
     * 创建API处理器
     */
    private ApiHandler createApiHandler(ApiInfo apiInfo) {
        String apiType = apiInfo.getApiType();
        
        // 根据API类型创建不同的处理器
        return switch (apiType) {
            case "分页" -> new PageApiHandler(apiInfo, queryNodeService, dataSourceExecutor);
            case "列表" -> new ListApiHandler(apiInfo, queryNodeService, dataSourceExecutor);
            case "对象" -> new ObjectApiHandler(apiInfo, queryNodeService, dataSourceExecutor);
            default -> throw new RuntimeException("不支持的API类型: " + apiType);
        };
    }

    /**
     * API处理器接口
     */
    interface ApiHandler {
        /**
         * 处理API请求
         *
         * @param params  请求参数
         * @param request HTTP请求对象
         * @return 处理结果
         */
        Object handle(Map<String, Object> params, HttpServletRequest request);
    }

    /**
     * 分页API处理器
     */
    record PageApiHandler(ApiInfo apiInfo, QueryNodeService queryNodeService,
                          DynamicDataSourceExecutor dataSourceExecutor) implements ApiHandler {

        @Override
        public Object handle(Map<String, Object> params, HttpServletRequest request) {
            log.info("执行分页API: {}", apiInfo.getApiName());

            // 获取分页参数
            int current = params.containsKey("current") ? Integer.parseInt(params.get("current").toString()) : 1;
            int size = params.containsKey("size") ? Integer.parseInt(params.get("size").toString()) : 10;
            int offset = (current - 1) * size;

            // TODO: 这里需要根据apiInfo.getId()查询关联的QueryNode
            // 暂时使用模拟数据
            LambdaQueryWrapper<QueryNode> wrapper = new LambdaQueryWrapper<>();
            // wrapper.eq(QueryNode::getApiId, apiInfo.getId()); // 需要在QueryNode中添加apiId字段
            List<QueryNode> queryNodes = queryNodeService.list(wrapper);

            if (queryNodes.isEmpty()) {
                log.warn("API {} 未配置查询节点", apiInfo.getApiName());
                return buildPageResult(List.of(), 0, current, size);
            }

            try {
                QueryNode queryNode = queryNodes.get(0);
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

    /**
     * 列表API处理器
     */
    record ListApiHandler(ApiInfo apiInfo, QueryNodeService queryNodeService,
                          DynamicDataSourceExecutor dataSourceExecutor) implements ApiHandler {

        @Override
        public Object handle(Map<String, Object> params, HttpServletRequest request) {
            log.info("执行列表API: {}", apiInfo.getApiName());

            // TODO: 这里需要根据apiInfo.getId()查询关联的QueryNode
            LambdaQueryWrapper<QueryNode> wrapper = new LambdaQueryWrapper<>();
            // wrapper.eq(QueryNode::getApiId, apiInfo.getId()); // 需要在QueryNode中添加apiId字段
            List<QueryNode> queryNodes = queryNodeService.list(wrapper);

            if (queryNodes.isEmpty()) {
                log.warn("API {} 未配置查询节点", apiInfo.getApiName());
                return List.of();
            }

            try {
                QueryNode queryNode = queryNodes.get(0);
                String sql = queryNode.getSqlContent();
                Integer dataSourceId = queryNode.getDataSourceId();

                // 执行查询 - 返回多行结果
                return dataSourceExecutor.executeQueryForList(dataSourceId, sql, params);

            } catch (Exception e) {
                log.error("列表查询执行失败", e);
                throw new RuntimeException("列表查询失败: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 对象API处理器
     */
    record ObjectApiHandler(ApiInfo apiInfo, QueryNodeService queryNodeService,
                            DynamicDataSourceExecutor dataSourceExecutor) implements ApiHandler {

        @Override
        public Object handle(Map<String, Object> params, HttpServletRequest request) {
            log.info("执行对象API: {}", apiInfo.getApiName());

            // TODO: 这里需要根据apiInfo.getId()查询关联的QueryNode
            LambdaQueryWrapper<QueryNode> wrapper = new LambdaQueryWrapper<>();
            // wrapper.eq(QueryNode::getApiId, apiInfo.getId()); // 需要在QueryNode中添加apiId字段
            List<QueryNode> queryNodes = queryNodeService.list(wrapper);

            if (queryNodes.isEmpty()) {
                log.warn("API {} 未配置查询节点", apiInfo.getApiName());
                return Map.of();
            }

            try {
                QueryNode queryNode = queryNodes.get(0);
                String sql = queryNode.getSqlContent();
                Integer dataSourceId = queryNode.getDataSourceId();
                String nodeType = queryNode.getNodeType();

                // 根据节点类型执行不同的查询
                return switch (nodeType) {
                    case "单行" -> dataSourceExecutor.executeQueryForMap(dataSourceId, sql, params);
                    case "单值" -> {
                        Object value = dataSourceExecutor.executeQueryForObject(dataSourceId, sql, params);
                        Map<String, Object> result = new HashMap<>();
                        result.put("value", value);
                        yield result;
                    }
                    case "单列" -> {
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
}
