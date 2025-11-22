package com.xqy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * API信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("api_info")
public class ApiInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属分组ID
     */
    private Integer groupId;

    /**
     * API名称
     */
    private String apiName;

    /**
     * API路径
     */
    private String apiPath;

    /**
     * API请求方法 (GET/POST/PUT/DELETE等)
     */
    private String apiMethod;

    /**
     * API类型 (分页/列表/对象)
     */
    private String apiType;

    /**
     * 根查询节点ID
     */
    private String rootQueryNodeId;

    /**
     * API描述
     */
    private String apiDescription;
}
