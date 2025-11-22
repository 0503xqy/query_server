package com.xqy.dto;

import com.xqy.enums.RelationType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 查询节点树形结构DTO
 * 用于展示节点的层级关系
 */
@Data
public class QueryNodeTreeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
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
     * 数据源名称
     */
    private String dataSourceName;

    /**
     * 节点描述
     */
    private String nodeDescription;

    /**
     * 父节点ID
     */
    private Integer parentId;

    /**
     * 关系类型 (父/子/兄弟)
     */
    private RelationType relationType;

    /**
     * 子节点列表
     */
    private List<QueryNodeTreeDto> childrenList;

    /**
     * 是否有子节点
     */
    private Boolean hasChildren;

    /**
     * 节点层级（从0开始）
     */
    private Integer level;
}