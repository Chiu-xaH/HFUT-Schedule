package com.hfut.schedule.logic.util.helper

import kotlin.properties.ReadOnlyProperty

/**
 * 例如 val motionBlur by autoKey() 得到字符串"motionBlur"
 */
fun generateKey(): ReadOnlyProperty<Any?, String> = ReadOnlyProperty { _, property ->
    property.name
}
