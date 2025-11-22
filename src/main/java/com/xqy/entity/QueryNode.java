package com.xqy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 查询节点实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("query_node")
public class QueryNode extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型 (多行/单行/单列/单值)
     */
    private String nodeType;

    /**
     * SQL内容
     */
    private String sqlContent;

    /**
     * 节点参数 (JSON格式)
     */
    private String params;

    /**
     * 动态执行脚本
     */
    private String script;

    /**
     * 关联数据源ID
     */
    private Integer dataSourceId;

    /**
     * 节点描述
     */
    private String nodeDescription;
}
