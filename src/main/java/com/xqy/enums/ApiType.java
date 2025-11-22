package com.xqy.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * API类型枚举
 */
@Getter
public enum ApiType{

    /**
     * 分页查询
     */
    PAGE("PAGE", "分页查询"),

    /**
     * 列表查询
     */
    LIST("LIST", "列表查询"),

    /**
     * 对象查询
     */
    MAP("MAP", "对象查询");

    /**
     * 类型值
     */
    @EnumValue
    private final String value;

    /**
     * 描述
     */
    private final String description;

    ApiType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据值获取枚举
     *
     * @param value 类型值
     * @return 枚举
     */
    public static ApiType fromValue(String value) {
        for (ApiType type : ApiType.values()) {
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
    public static ApiType fromValueSafely(String value) {
        if (value == null) {
            return null;
        }
        for (ApiType type : ApiType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
