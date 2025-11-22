package com.xqy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqy.entity.ApiGroup;
import com.xqy.mapper.ApiGroupMapper;
import com.xqy.service.ApiGroupService;
import org.springframework.stereotype.Service;

/**
 * API分组Service实现类
 */
@Service
public class ApiGroupServiceImpl extends ServiceImpl<ApiGroupMapper, ApiGroup> implements ApiGroupService {
}
