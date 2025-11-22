package com.xqy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqy.entity.ApiInfo;
import com.xqy.mapper.ApiInfoMapper;
import com.xqy.service.ApiInfoService;
import org.springframework.stereotype.Service;

/**
 * API信息Service实现类
 */
@Service
public class ApiInfoServiceImpl extends ServiceImpl<ApiInfoMapper, ApiInfo> implements ApiInfoService {
}
