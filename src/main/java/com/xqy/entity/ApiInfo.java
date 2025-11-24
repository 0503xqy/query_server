package com.xqy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xqy.enums.ApiType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * API信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("api_info")
public class ApiInfo extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 所属分组ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long groupId;

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
    private ApiType apiType;

    /**
     * 根查询节点ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long rootQueryNodeId;

    /**
     * API描述
     */
    private String apiDescription;
}
