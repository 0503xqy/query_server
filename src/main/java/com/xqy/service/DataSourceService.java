package com.xqy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xqy.entity.DataSource;

/**
 * 数据源Service接口
 */
public interface DataSourceService extends IService<DataSource> {
    
    /**
     * 获取默认数据源
     *
     * @return 默认数据源
     */
    DataSource getDefaultDataSource();
    
    /**
     * 设置默认数据源
     *
     * @param id 数据源ID
     */
    void setDefaultDataSource(Integer id);
}
