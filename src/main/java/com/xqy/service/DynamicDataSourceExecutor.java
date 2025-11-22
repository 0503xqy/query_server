package com.xqy.service;

import com.xqy.entity.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * 动态数据源执行器服务
 * 负责根据数据源配置动态执行SQL
 */
public interface DynamicDataSourceExecutor {

    /**
     * 执行查询SQL - 返回多行结果
     *
     * @param dataSourceId 数据源ID
     * @param sql          SQL语句
     * @param params       参数
     * @return 查询结果列表
     */
    List<Map<String, Object>> executeQueryForList(Integer dataSourceId, String sql, Map<String, Object> params);

    /**
     * 执行查询SQL - 返回单行结果
     *
     * @param dataSourceId 数据源ID
     * @param sql          SQL语句
     * @param params       参数
     * @return 单行结果
     */
    Map<String, Object> executeQueryForMap(Integer dataSourceId, String sql, Map<String, Object> params);

    /**
     * 执行查询SQL - 返回单个值
     *
     * @param dataSourceId 数据源ID
     * @param sql          SQL语句
     * @param params       参数
     * @return 单个值
     */
    Object executeQueryForObject(Integer dataSourceId, String sql, Map<String, Object> params);

    /**
     * 获取指定数据源的JdbcTemplate
     *
     * @param dataSourceId 数据源ID
     * @return JdbcTemplate实例
     */
    JdbcTemplate getJdbcTemplate(Integer dataSourceId);

    /**
     * 测试数据源连接
     *
     * @param dataSource 数据源配置
     * @return 是否连接成功
     */
    boolean testConnection(DataSource dataSource);
}
