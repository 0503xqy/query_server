package com.xqy.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus自动填充处理器
 * 自动填充创建人、创建时间、更新人、更新时间
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");
        
        // 自动填充创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        
        // 自动填充更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        // 自动填充创建人（这里默认为system，实际项目中应该从登录用户获取）
        this.strictInsertFill(metaObject, "createBy", String.class, getCurrentUser());
        
        // 自动填充更新人
        this.strictInsertFill(metaObject, "updateBy", String.class, getCurrentUser());
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
        
        // 自动填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        // 自动填充更新人
        this.strictUpdateFill(metaObject, "updateBy", String.class, getCurrentUser());
    }

    /**
     * 获取当前用户
     * TODO: 实际项目中应该从Security Context或Session中获取当前登录用户
     */
    private String getCurrentUser() {
        // 这里先返回默认值，实际项目中应该从登录用户获取
        return "system";
    }
}
