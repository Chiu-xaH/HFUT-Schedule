package com.xah.common.logic.util

import android.util.Log
import com.xah.common.logic.BuildConfig
import java.time.LocalDateTime

/**
 * 可直接跟踪到日志打印的位置（堆栈），提升开发效率
 * debug包打印所有级别日志，release包只打印error级别日志
 * 暂存error级别的日志的堆栈，方便导出
 */
object LogUtil {
    private var tag: String = this::class.java.name
    // 找到后为stackIndex赋值，以后再用就不用遍历了
    private var stackIndex : Int? = null

    private enum class Level(val priority: Int) {
        NONE(-1),
        VERBOSE(0),
        DEBUG(1),
        INFO(2),
        WARN(3),
        ERROR(4)
    }

    data class ErrorEntry(
        val throwable: Throwable,
        val timestamp: LocalDateTime = LocalDateTime.now()
    )

    const val DEFAULT_MAX_CACHE = 300
    private var maxCacheSize: Int = DEFAULT_MAX_CACHE
    // 用 ArrayDeque 做有界环形缓冲，超出自动丢弃最旧的
    private val errorCache = ArrayDeque<ErrorEntry>(DEFAULT_MAX_CACHE)
    private val cacheLock = Any()

    fun getCachedLogs(): List<ErrorEntry> = synchronized(cacheLock) {
        errorCache.toList()
    }

    fun clearCache() = synchronized(cacheLock) {
        errorCache.clear()
    }

    fun getCachedLogsSize() = errorCache.size


    private fun addToCache(throwable: Throwable) = synchronized(cacheLock) {
        if (errorCache.size >= maxCacheSize) {
            errorCache.removeFirst()
        }
        errorCache.addLast(ErrorEntry(throwable))
    }

    fun init(
        tagName : String,
        debug : Boolean = BuildConfig.DEBUG,
        maxCacheSize: Int = DEFAULT_MAX_CACHE
    ) {
        tag = tagName
        this.maxCacheSize = maxCacheSize
        minLevel = if(debug) {
            // Debug状态下，打印所有日志，并将其暂存
            Level.VERBOSE
        } else {
            // Release状态下，打印WARN及其以上日志，并将其暂存
            if (BuildConfig.DEBUG) Level.VERBOSE else Level.ERROR
        }
    }

    private var minLevel: Level = if (BuildConfig.DEBUG) Level.VERBOSE else Level.WARN

    fun verbose(msg : String = "") = baseLog(Level.VERBOSE,msg)
    fun info(msg : String = "") = baseLog(Level.INFO,msg)
    fun debug(msg : String = "") = baseLog(Level.DEBUG,msg)
    fun warn(msg : String = "") = baseLog(Level.WARN,msg)
    fun error(throwable: Throwable,msg : String = "") = baseLog(Level.ERROR,msg,throwable)
    fun error(msg : String = "") = baseLog(Level.ERROR,msg)

    private fun findCaller(): StackTraceElement? {
        val stack = Thread.currentThread().stackTrace
        return try {
            if (stackIndex != null && stackIndex!! in stack.indices) {
                stack[stackIndex!!]
            } else {
                val element = stack.firstOrNull { element ->
                    element.className != this::class.java.name && element.className != Thread::class.java.name && element.className != "dalvik.system.VMStack"
                }
                stackIndex = element?.let { stack.indexOf(it) }
                element
            }
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun baseLog(type: Level,msg : String, throwable: Throwable? = null) {
        if (type.priority < minLevel.priority) {
            return
        }

        val element = findCaller()
        val text = if(element == null) msg else "(${element.fileName}:${element.lineNumber}) ${element.methodName}()${if(msg.isEmpty()) "" else " : $msg"}"

        throwable?.let { addToCache(it) }

        when(type) {
            Level.NONE -> return
            Level.VERBOSE -> Log.v(tag,text)
            Level.INFO -> Log.i(tag,text)
            Level.DEBUG -> Log.d(tag,text)
            Level.WARN -> Log.w(tag,text)
            Level.ERROR -> {
                if(throwable != null) {
                    Log.e(tag,text,throwable)
                } else {
                    Log.e(tag,text)
                }
            }
        }
    }
}