package com.xqy.service.impl;

import com.xqy.dto.QueryNodeTreeDto;
import com.xqy.entity.ApiInfo;
import com.xqy.enums.ApiType;
import com.xqy.service.ApiInfoService;
import com.xqy.service.DynamicApiService;
import com.xqy.service.DynamicDataSourceExecutor;
import com.xqy.service.QueryNodeService;
import com.xqy.service.impl.handler.ApiHandler;
import com.xqy.service.impl.handler.ListApiHandler;
import com.xqy.service.impl.handler.ObjectApiHandler;
import com.xqy.service.impl.handler.PageApiHandler;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final Map<String, ApiHandler> apiHandlerMap;

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
     * 查询节点缓存
     * key: queryNodeId
     * value: QueryNodeTreeDto
     */
    private final Map<Long, QueryNodeTreeDto> queryNodeCache = new ConcurrentHashMap<>();

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

                QueryNodeTreeDto queryNodeTreeDto = queryNodeService.buildExecutionTree(apiInfo.getRootQueryNodeId());
                // 缓存查询节点
                queryNodeCache.put(apiInfo.getRootQueryNodeId(), queryNodeTreeDto);

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

        ApiInfo apiInfo = apiCache.get(cacheKey);
        QueryNodeTreeDto queryNodeTreeDto = queryNodeCache.get(apiInfo.getRootQueryNodeId());

        try {
            log.info("开始执行API: {} {}, 参数: {}", method, apiPath, params);
            ApiHandler apiHandler = getApiHandler(apiInfo);
            // 执行处理器
            Object result = apiHandler.handle(apiInfo, queryNodeTreeDto, dataSourceExecutor,params);
            
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
    private ApiHandler getApiHandler(ApiInfo apiInfo) {
        ApiType apiType = apiInfo.getApiType();

        if (apiType == null) {
            log.warn("API类型为空: {}", apiInfo.getApiName());
            throw new RuntimeException("API类型为空: " + apiInfo.getApiName());
        }

        ApiHandler apiHandler = apiHandlerMap.get(apiType.getValue());
        if (apiHandler == null) {
            log.warn("API处理器未找到: {}", apiType.getValue());
            throw new RuntimeException("API处理器未找到: " + apiType.getValue());
        }
        return apiHandler;
    }
}