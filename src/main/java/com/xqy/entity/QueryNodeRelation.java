package com.xqy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xqy.enums.RelationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 查询节点关系实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("query_node_relation")
public class QueryNodeRelation extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 节点ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long nodeId;

    /**
     * 父节点ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    /**
     * 关系类型 (父/子/兄弟)
     */
    private RelationType relationType;
}
