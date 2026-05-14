package com.xah.common.logic

import android.os.Handler
import android.os.Looper
import android.util.ArrayMap
import androidx.core.util.Preconditions
import java.util.concurrent.ConcurrentHashMap

/**
 * 所有的Dependencies映射表管理类
 */
class Dependency private constructor() {

    private val dependencies = ConcurrentHashMap<Any, Any>()
    private val providers = ArrayMap<Any, LazyDependencyCreator<*>>()

    companion object {
        private lateinit var instance: Dependency

        const val BG_LOOPER_NAME = "background_looper"
        const val MAIN_LOOPER_NAME = "main_looper"
        const val BG_HANDLER_NAME = "bg_handler"
        const val MAIN_HANDLER_NAME = "main_handler"

        val BG_LOOPER = DependencyKey<Looper>(BG_LOOPER_NAME)
        val MAIN_LOOPER = DependencyKey<Looper>(MAIN_LOOPER_NAME)

        val BG_HANDLER = DependencyKey<Handler>(BG_HANDLER_NAME)
        val MAIN_HANDLER = DependencyKey<Handler>(MAIN_HANDLER_NAME)

        fun initInstance() {
            instance = Dependency()
        }

        fun putProvider(key: Any, creator: LazyDependencyCreator<*>) {
            instance.providers[key] = creator
        }

        fun <T : Any> get(cls: Class<T>): T {
            return instance.getDependency(cls)
        }

        fun <T : Any> get(key: DependencyKey<T>): T {
            return instance.getDependency(key)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getDependency(key: Any): T {
        var obj = dependencies[key] as? T
        if (obj == null) {
            synchronized(dependencies) {
                obj = dependencies[key] as? T
                if (obj == null) {
                    obj = createDependency(key)
                    dependencies[key] = obj
                }
            }
        }
        return obj!!
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> createDependency(key: Any): T {
        Preconditions.checkArgument(key is DependencyKey<*> || key is Class<*>)

        val provider = providers[key] as? LazyDependencyCreator<T>
            ?: throw IllegalArgumentException(
                "Unsupported dependency $key. ${providers.size} providers known."
            )
        return provider.createDependency()
    }

    interface LazyDependencyCreator<T> {
        fun createDependency(): T
    }

    class DependencyKey<V>(private val displayName: String) {
        override fun toString(): String = displayName
    }
}