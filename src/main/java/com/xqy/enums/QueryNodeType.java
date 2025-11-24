package com.xqy.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum QueryNodeType {

//    节点类型 (多行/单行/单列/单值)
    
    ROWS("ROWS", "多行"),
    ROW("ROW", "单行"),
    COLUMN("COLUMN", "单列-数组"),
    VALUE("VALUE", "单值"),

    ;
    /**
     * 类型值
     */
    @EnumValue
    private final String value;

    /**
     * 描述
     */
    private final String description;

    QueryNodeType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据值获取枚举
     *
     * @param value 类型值
     * @return 枚举
     */
    public static QueryNodeType fromValue(String value) {
        for (QueryNodeType type : QueryNodeType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的API类型: " + value);
    }

    /**
     * 根据值获取枚举（安全方法）
     *
     * @param value 类型值
     * @return 枚举，不存在返回null
     */
    public static QueryNodeType fromValueSafely(String value) {
        if (value == null) {
            return null;
        }
        for (QueryNodeType type : QueryNodeType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

}
