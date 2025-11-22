package com.xqy.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 节点关系类型枚举
 */
@Getter
public enum RelationType {

    /**
     * 子节点关系
     */
    CHILD("CHILD", "子节点关系"),

    /**
     * 兄弟关系
     */
    SIBLING("SIBLING", "兄弟关系");

    /**
     * 类型值
     */
    @EnumValue
    private final String value;

    /**
     * 描述
     */
    private final String description;

    RelationType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据值获取枚举
     *
     * @param value 类型值
     * @return 枚举
     */
    public static RelationType fromValue(String value) {
        for (RelationType type : RelationType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的关系类型: " + value);
    }

    /**
     * 根据值获取枚举（安全方法，返回null而不是抛异常）
     *
     * @param value 类型值
     * @return 枚举，不存在返回null
     */
    public static RelationType fromValueSafely(String value) {
        if (value == null) {
            return null;
        }
        for (RelationType type : RelationType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
