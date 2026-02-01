package com.xah.uicommon.util.language

/**
 * 实现基类 传入任意枚举
 * @param code 实际记忆的值 如果能保证枚举类的顺序后期不会调整，也可以去除code，用ordinal代替
 */
interface BaseChoice {
    val label: UiText
    val code: Int
}