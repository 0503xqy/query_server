package com.xqy.util;

import com.xqy.enums.BaseEnum;

/**
 * 枚举工具类
 */
public class EnumUtils {

    /**
     * 根据值获取枚举（抛出异常）
     *
     * @param enumClass 枚举类
     * @param value     枚举值
     * @param <E>       枚举类型
     * @param <T>       值类型
     * @return 枚举对象
     */
    public static <E extends Enum<E> & BaseEnum<T>, T> E fromValue(Class<E> enumClass, T value) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E enumConstant : enumConstants) {
            if (enumConstant.getValue().equals(value)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("未知的枚举值: " + value + ", 枚举类: " + enumClass.getSimpleName());
    }

    /**
     * 根据值获取枚举（安全方法，返回null）
     *
     * @param enumClass 枚举类
     * @param value     枚举值
     * @param <E>       枚举类型
     * @param <T>       值类型
     * @return 枚举对象，不存在返回null
     */
    public static <E extends Enum<E> & BaseEnum<T>, T> E fromValueSafely(Class<E> enumClass, T value) {
        if (value == null) {
            return null;
        }
        E[] enumConstants = enumClass.getEnumConstants();
        for (E enumConstant : enumConstants) {
            if (enumConstant.getValue().equals(value)) {
                return enumConstant;
            }
        }
        return null;
    }

    /**
     * 根据值获取枚举（返回默认值）
     *
     * @param enumClass    枚举类
     * @param value        枚举值
     * @param defaultValue 默认值
     * @param <E>          枚举类型
     * @param <T>          值类型
     * @return 枚举对象，不存在返回默认值
     */
    public static <E extends Enum<E> & BaseEnum<T>, T> E fromValueOrDefault(Class<E> enumClass, T value, E defaultValue) {
        E result = fromValueSafely(enumClass, value);
        return result != null ? result : defaultValue;
    }
}
