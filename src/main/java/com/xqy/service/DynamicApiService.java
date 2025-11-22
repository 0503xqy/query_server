package com.xqy.service;

import com.xqy.entity.ApiInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 动态API执行服务接口
 */
public interface DynamicApiService {

    /**
     * 初始化加载所有API配置
     */
    void initApiConfigs();

    /**
     * 执行动态API请求
     *
     * @param apiPath API路径
     * @param method  请求方法
     * @param params  请求参数
     * @param request HTTP请求对象
     * @return 执行结果
     */
    Object executeApi(String apiPath, String method, Map<String, Object> params, HttpServletRequest request);

    /**
     * 重新加载API配置
     */
    void reloadApiConfigs();

    /**
     * 根据路径和方法获取API信息
     *
     * @param apiPath API路径
     * @param method  请求方法
     * @return API信息
     */
    ApiInfo getApiInfo(String apiPath, String method);
}
