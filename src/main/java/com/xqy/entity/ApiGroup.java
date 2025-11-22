package com.xqy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * API分组实体类
 * 支持多级分组结构
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("api_group")
public class ApiGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 父级分组ID，0表示根节点
     */
    private Integer parentId;

    /**
     * 分组名称
     */
    private String groupName;
}
