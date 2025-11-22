package com.xqy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xqy.enums.RelationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询节点关系实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("query_node_relation")
public class QueryNodeRelation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 节点ID
     */
    private Integer nodeId;

    /**
     * 父节点ID
     */
    private Integer parentId;

    /**
     * 关系类型 (父/子/兄弟)
     */
    private RelationType relationType;
}
