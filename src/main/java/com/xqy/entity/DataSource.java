package com.xqy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 数据源实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_source")
public class DataSource extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源类型 (mysql/oracle/sqlserver/postgresql)
     */
    private String type;

    /**
     * 数据源连接URL
     */
    private String url;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否默认数据源 (0:否 1:是)
     */
    private Boolean isDefault;

    /**
     * 描述
     */
    private String description;
}
