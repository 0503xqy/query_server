package com.xqy.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.xqy.entity.DataSource;
import com.xqy.service.DataSourceService;
import com.xqy.service.DynamicDataSourceExecutor;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 动态数据源执行器 - 完全防SQL注入安全版
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicDataSourceExecutorImpl implements DynamicDataSourceExecutor {

    private final DataSourceService dataSourceService;

    // 数据源缓存
    private final Map<Integer, DruidDataSource> dataSourceCache = new ConcurrentHashMap<>();
    private final Map<Integer, JdbcTemplate> jdbcTemplateCache = new ConcurrentHashMap<>();

    // 必须使用命名参数 :paramName 形式
    private static final Pattern NAMED_PARAM_PATTERN = Pattern.compile(":[a-zA-Z_][a-zA-Z0-9_]*");

    /**
     * 执行查询 - 返回多行结果（安全版）
     */
    @Override
    public List<Map<String, Object>> executeQueryForList(Integer dataSourceId, String sql, Map<String, Object> params) {

        validateSqlMustUseNamedParameters(sql, dataSourceId);

        try {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(getJdbcTemplate(dataSourceId));
            Map<String, Object> paramMap = params == null ? Collections.emptyMap() : params;

            return template.queryForList(sql, paramMap);

        } catch (Exception e) {
            log.error("执行查询失败 [多行] dataSourceId: {}, sql: {}", dataSourceId, sql, e);
            throw new RuntimeException("查询失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行查询 - 返回单行结果（安全版）
     */
    @Override
    public Map<String, Object> executeQueryForMap(Integer dataSourceId, String sql, Map<String, Object> params) {
        validateSqlMustUseNamedParameters(sql, dataSourceId);

        try {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(getJdbcTemplate(dataSourceId));
            Map<String, Object> paramMap = params == null ? Collections.emptyMap() : params;

            return template.queryForMap(sql, paramMap);

            // 更简洁写法（推荐）
//             return template.queryForMap(sql, paramMap, new MapResultSetExtractor<>(true));

        } catch (EmptyResultDataAccessException e) {
            return null; // 或根据业务抛自定义异常
        } catch (Exception e) {
            log.error("执行查询失败 [单行] dataSourceId: {}, sql: {}", dataSourceId, sql, e);
            throw new RuntimeException("查询失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行查询 - 返回单个值（安全版）
     */
    @Override
    public Object executeQueryForObject(Integer dataSourceId, String sql, Map<String, Object> params) {
        validateSqlMustUseNamedParameters(sql, dataSourceId);

        try {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(getJdbcTemplate(dataSourceId));
            Map<String, Object> paramMap = params == null ? Collections.emptyMap() : params;

            return template.queryForObject(sql, paramMap, Object.class);

        } catch (EmptyResultDataAccessException e) {
            return null; // 根据业务决定返回 null 或抛异常
        } catch (Exception e) {
            log.error("执行查询失败 [单值] dataSourceId: {}, sql: {}", dataSourceId, sql, e);
            throw new RuntimeException("查询失败: " + e.getMessage(), e);
        }
    }

    /**
     * 强制校验：SQL 必须包含至少一个命名参数 :xxx
     * 从设计上彻底杜绝字符串拼接SQL
     */
    private void validateSqlMustUseNamedParameters(String sql, Integer dataSourceId) {
        if (sql == null || !NAMED_PARAM_PATTERN.matcher(sql).find()) {
            log.error("SQL注入风险拦截 - 禁止使用无命名参数的SQL, dataSourceId: {}, sql: {}", dataSourceId, sql);
            throw new IllegalArgumentException(
                    "SQL必须使用命名参数形式（如 :userId, :status），禁止传入原始拼接SQL字符串");
        }
    }

    /**
     * 获取 JdbcTemplate（带缓存）
     */
    @Override
    public JdbcTemplate getJdbcTemplate(Integer dataSourceId) {
        return jdbcTemplateCache.computeIfAbsent(dataSourceId, id -> {
            DruidDataSource ds = getDruidDataSource(id);
            return new JdbcTemplate(ds);
        });
    }

    /**
     * 测试连接
     */
    @Override
    public boolean testConnection(DataSource dataSource) {
        try (DruidDataSource ds = createDruidDataSource(dataSource)) {
            ds.getConnection().close();
            log.info("数据源连接测试成功: {}", dataSource.getName());
            return true;
        } catch (Exception e) {
            log.error("数据源连接测试失败: {}", dataSource.getName(), e);
            return false;
        }
    }

    private DruidDataSource getDruidDataSource(Integer dataSourceId) {
        return dataSourceCache.computeIfAbsent(dataSourceId, id -> {
            DataSource config = dataSourceService.getById(id);
            if (config == null) throw new RuntimeException("数据源不存在: " + id);
            return createDruidDataSource(config);
        });
    }

    private DruidDataSource createDruidDataSource(DataSource dsConfig) {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(dsConfig.getUrl());
        ds.setUsername(dsConfig.getUsername());
        ds.setPassword(dsConfig.getPassword());
        ds.setDriverClassName(getDriverClassName(dsConfig.getType()));

        // 连接池优化配置
        ds.setInitialSize(5);
        ds.setMinIdle(5);
        ds.setMaxActive(20);
        ds.setMaxWait(60000);
        ds.setTimeBetweenEvictionRunsMillis(60000);
        ds.setMinEvictableIdleTimeMillis(300000);
        ds.setValidationQuery(getValidationQuery(dsConfig.getType()));
        ds.setTestWhileIdle(true);
        ds.setTestOnBorrow(false);
        ds.setTestOnReturn(false);
        ds.setPoolPreparedStatements(true);
        ds.setMaxPoolPreparedStatementPerConnectionSize(20);

        try {
            ds.setFilters("stat,wall,slf4j"); // wall 防火墙可额外防御注入
            ds.init();
            log.info("动态数据源创建成功: {} ({})", dsConfig.getName(), dsConfig.getType());
        } catch (SQLException e) {
            log.error("动态数据源初始化失败: {}", dsConfig.getName(), e);
            throw new RuntimeException("数据源初始化失败", e);
        }
        return ds;
    }

    private String getDriverClassName(String type) {
        return switch (type.toLowerCase()) {
            case "mysql" -> "com.mysql.cj.jdbc.Driver";
            case "oracle" -> "oracle.jdbc.OracleDriver";
            case "sqlserver" -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case "postgresql" -> "org.postgresql.Driver";
            case "clickhouse" -> "com.clickhouse.jdbc.ClickHouseDriver";
            default -> throw new IllegalArgumentException("不支持的数据库类型: " + type);
        };
    }

    private String getValidationQuery(String type) {
        return switch (type.toLowerCase()) {
            case "mysql", "postgresql", "sqlserver", "clickhouse" -> "SELECT 1";
            case "oracle" -> "SELECT 1 FROM DUAL";
            default -> "SELECT 1";
        };
    }

    @PreDestroy
    public void destroy() {
        log.info("正在关闭所有动态数据源...");
        dataSourceCache.values().forEach(ds -> {
            try {
                ds.close();
            } catch (Exception e) {
                log.warn("关闭数据源时异常", e);
            }
        });
        dataSourceCache.clear();
        jdbcTemplateCache.clear();
        log.info("所有动态数据源已安全关闭");
    }
}