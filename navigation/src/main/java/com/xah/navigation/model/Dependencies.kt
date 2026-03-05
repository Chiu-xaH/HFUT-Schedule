package com.xah.navigation.model

import kotlin.reflect.KClass


class Dependencies {
    val map = mutableMapOf<Pair<KClass<*>, String?>, Any?>()

    inline fun <reified T> put(value: T, tag: String? = null) {
        map[T::class to tag] = value  // value 可以是 null
    }

    inline fun <reified T> get(tag: String? = null): T {
        return map[T::class to tag] as T  // T 可以是 String?，as T 就能转
    }
}