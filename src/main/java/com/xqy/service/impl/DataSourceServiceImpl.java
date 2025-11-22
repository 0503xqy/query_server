package com.xqy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqy.entity.DataSource;
import com.xqy.mapper.DataSourceMapper;
import com.xqy.service.DataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据源Service实现类
 */
@Slf4j
@Service
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource> implements DataSourceService {

    /**
     * 获取默认数据源
     */
    @Override
    public DataSource getDefaultDataSource() {
        LambdaQueryWrapper<DataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataSource::getIsDefault, true);
        return this.getOne(wrapper);
    }

    /**
     * 设置默认数据源
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultDataSource(Integer id) {
        // 先将所有数据源设置为非默认
        LambdaUpdateWrapper<DataSource> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(DataSource::getIsDefault, false);
        this.update(updateWrapper);
        
        // 设置指定数据源为默认
        DataSource dataSource = this.getById(id);
        if (dataSource != null) {
            dataSource.setIsDefault(true);
            this.updateById(dataSource);
            log.info("设置数据源为默认: {}", dataSource.getName());
        } else {
            throw new RuntimeException("数据源不存在: " + id);
        }
    }
}
