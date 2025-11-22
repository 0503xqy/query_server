package com.xqy.controller;

import com.xqy.common.Result;
import com.xqy.service.DynamicApiService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 动态API控制器
 * 处理所有动态配置的API请求
 */
@Slf4j
@RestController
@RequestMapping("/dynamic")
@RequiredArgsConstructor
public class DynamicApiController {

    private final DynamicApiService dynamicApiService;

    /**
     * 动态API请求处理 - GET
     */
    @GetMapping("/**")
    public Result<Object> handleGetRequest(HttpServletRequest request,
                                            @RequestParam(required = false) Map<String, Object> params) {
        String apiPath = extractApiPath(request);
        Object result = dynamicApiService.executeApi(apiPath, "GET", params, request);
        return Result.success(result);
    }

    /**
     * 动态API请求处理 - POST
     */
    @PostMapping("/**")
    public Result<Object> handlePostRequest(HttpServletRequest request,
                                             @RequestBody(required = false) Map<String, Object> params) {
        String apiPath = extractApiPath(request);
        Object result = dynamicApiService.executeApi(apiPath, "POST", params, request);
        return Result.success(result);
    }

    /**
     * 动态API请求处理 - PUT
     */
    @PutMapping("/**")
    public Result<Object> handlePutRequest(HttpServletRequest request,
                                            @RequestBody(required = false) Map<String, Object> params) {
        String apiPath = extractApiPath(request);
        Object result = dynamicApiService.executeApi(apiPath, "PUT", params, request);
        return Result.success(result);
    }

    /**
     * 动态API请求处理 - DELETE
     */
    @DeleteMapping("/**")
    public Result<Object> handleDeleteRequest(HttpServletRequest request,
                                               @RequestParam(required = false) Map<String, Object> params) {
        String apiPath = extractApiPath(request);
        Object result = dynamicApiService.executeApi(apiPath, "DELETE", params, request);
        return Result.success(result);
    }

    /**
     * 重新加载API配置
     */
    @PostMapping("/reload")
    public Result<Void> reloadApiConfigs() {
        dynamicApiService.reloadApiConfigs();
        return Result.success();
    }

    /**
     * 从请求中提取API路径
     */
    private String extractApiPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestUri.substring(contextPath.length());
        
        // 移除 /dynamic 前缀
        if (path.startsWith("/dynamic")) {
            path = path.substring("/dynamic".length());
        }
        
        return path;
    }
}
