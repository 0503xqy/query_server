package com.xqy.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.xqy.entity.DataSource;
import com.xqy.service.DataSourceService;
import com.xqy.service.DynamicDataSourceExecutor;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态数据源执行器实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicDataSourceExecutorImpl implements DynamicDataSourceExecutor {

    private final DataSourceService dataSourceService;

    /**
     * 数据源缓存
     * key: dataSourceId
     * value: DruidDataSource
     */
    private final Map<Integer, DruidDataSource> dataSourceCache = new ConcurrentHashMap<>();

    /**
     * JdbcTemplate缓存
     * key: dataSourceId
     * value: JdbcTemplate
     */
    private final Map<Integer, JdbcTemplate> jdbcTemplateCache = new ConcurrentHashMap<>();

    /**
     * 执行查询SQL - 返回多行结果
     */
    @Override
    public List<Map<String, Object>> executeQueryForList(Integer dataSourceId, String sql, Map<String, Object> params) {
        try {
            NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate(dataSourceId));
            
            if (params == null || params.isEmpty()) {
                return getJdbcTemplate(dataSourceId).queryForList(sql);
            } else {
                return namedTemplate.queryForList(sql, params);
            }
        } catch (Exception e) {
            log.error("执行查询SQL失败 - 多行结果, dataSourceId: {}, sql: {}", dataSourceId, sql, e);
            throw new RuntimeException("查询失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行查询SQL - 返回单行结果
     */
    @Override
    public Map<String, Object> executeQueryForMap(Integer dataSourceId, String sql, Map<String, Object> params) {
        try {
            NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate(dataSourceId));
            
            if (params == null || params.isEmpty()) {
                return getJdbcTemplate(dataSourceId).queryForMap(sql);
            } else {
                return namedTemplate.queryForMap(sql, params);
            }
        } catch (Exception e) {
            log.error("执行查询SQL失败 - 单行结果, dataSourceId: {}, sql: {}", dataSourceId, sql, e);
            throw new RuntimeException("查询失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行查询SQL - 返回单个值
     */
    @Override
    public Object executeQueryForObject(Integer dataSourceId, String sql, Map<String, Object> params) {
        try {
            NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate(dataSourceId));
            
            if (params == null || params.isEmpty()) {
                return getJdbcTemplate(dataSourceId).queryForObject(sql, Object.class);
            } else {
                return namedTemplate.queryForObject(sql, params, Object.class);
            }
        } catch (Exception e) {
            log.error("执行查询SQL失败 - 单值结果, dataSourceId: {}, sql: {}", dataSourceId, sql, e);
            throw new RuntimeException("查询失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取指定数据源的JdbcTemplate
     */
    @Override
    public JdbcTemplate getJdbcTemplate(Integer dataSourceId) {
        return jdbcTemplateCache.computeIfAbsent(dataSourceId, id -> {
            DruidDataSource druidDataSource = getDruidDataSource(id);
            return new JdbcTemplate(druidDataSource);
        });
    }

    /**
     * 测试数据源连接
     */
    @Override
    public boolean testConnection(DataSource dataSource) {
        DruidDataSource druidDataSource = null;
        try {
            druidDataSource = createDruidDataSource(dataSource);
            druidDataSource.getConnection().close();
            log.info("数据源连接测试成功: {}", dataSource.getName());
            return true;
        } catch (Exception e) {
            log.error("数据源连接测试失败: {}", dataSource.getName(), e);
            return false;
        } finally {
            if (druidDataSource != null) {
                druidDataSource.close();
            }
        }
    }

    /**
     * 获取Druid数据源
     */
    private DruidDataSource getDruidDataSource(Integer dataSourceId) {
        return dataSourceCache.computeIfAbsent(dataSourceId, id -> {
            DataSource dsConfig = dataSourceService.getById(id);
            if (dsConfig == null) {
                throw new RuntimeException("数据源不存在: " + id);
            }
            return createDruidDataSource(dsConfig);
        });
    }

    /**
     * 创建Druid数据源
     */
    private DruidDataSource createDruidDataSource(DataSource dsConfig) {
        DruidDataSource druidDataSource = new DruidDataSource();
        
        druidDataSource.setUrl(dsConfig.getUrl());
        druidDataSource.setUsername(dsConfig.getUsername());
        druidDataSource.setPassword(dsConfig.getPassword());
        druidDataSource.setDriverClassName(getDriverClassName(dsConfig.getType()));
        
        // 连接池配置
        druidDataSource.setInitialSize(5);
        druidDataSource.setMinIdle(5);
        druidDataSource.setMaxActive(20);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setValidationQuery(getValidationQuery(dsConfig.getType()));
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        
        try {
            druidDataSource.setFilters("stat,wall");
            druidDataSource.init();
            log.info("成功创建数据源: {}, 类型: {}", dsConfig.getName(), dsConfig.getType());
        } catch (SQLException e) {
            log.error("初始化数据源失败: {}", dsConfig.getName(), e);
            throw new RuntimeException("初始化数据源失败: " + e.getMessage(), e);
        }
        
        return druidDataSource;
    }

    /**
     * 根据数据库类型获取驱动类名
     */
    private String getDriverClassName(String type) {
        return switch (type.toLowerCase()) {
            case "mysql" -> "com.mysql.cj.jdbc.Driver";
            case "oracle" -> "oracle.jdbc.OracleDriver";
            case "sqlserver" -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case "postgresql" -> "org.postgresql.Driver";
            default -> throw new RuntimeException("不支持的数据库类型: " + type);
        };
    }

    /**
     * 根据数据库类型获取验证查询语句
     */
    private String getValidationQuery(String type) {
        return switch (type.toLowerCase()) {
            case "mysql" -> "SELECT 1";
            case "oracle" -> "SELECT 1 FROM DUAL";
            case "sqlserver" -> "SELECT 1";
            case "postgresql" -> "SELECT 1";
            default -> "SELECT 1";
        };
    }

    /**
     * 销毁时关闭所有数据源连接
     */
    @PreDestroy
    public void destroy() {
        log.info("开始关闭所有动态数据源连接...");
        dataSourceCache.values().forEach(DruidDataSource::close);
        dataSourceCache.clear();
        jdbcTemplateCache.clear();
        log.info("所有动态数据源连接已关闭");
    }
}
