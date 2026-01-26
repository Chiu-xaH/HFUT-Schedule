package com.xah.uicommon.util

import android.util.Log

object LogUtil {
    @Volatile
    var enable = true
    var tag: String = this::class.java.name
    // 找到后为stackIndex赋值，以后再用就不用遍历了
    var stackIndex : Int? = null

    private enum class Level {
        VERBOSE,INFO,DEBUG,WARN,ERROR
    }
    
    fun verbose(msg : String = "") = baseLog(Level.VERBOSE,msg)
    fun info(msg : String = "") = baseLog(Level.INFO,msg)
    fun debug(msg : String = "") = baseLog(Level.DEBUG,msg)
    fun warn(msg : String = "") = baseLog(Level.WARN,msg)
    fun error(throwable: Throwable,msg : String = "") = baseLog(Level.ERROR,msg,throwable)
    fun error(msg : String = "") = baseLog(Level.ERROR,msg)

    private fun findCaller(): StackTraceElement? {
        try {
            val tranceElements = Thread.currentThread().stackTrace
            val finalStack = if(stackIndex != null && stackIndex!! >= 0 && stackIndex!! < tranceElements.size) {
                tranceElements[stackIndex!!]
            } else {
                tranceElements.firstOrNull { element ->
                    element.className != this::class.java.name && element.className != Thread::class.java.name && element.className != "dalvik.system.VMStack"
                }
            }
            return finalStack
        } catch (e : Exception) {
            LogUtil.error(e)
            return null
        }
    }

    private fun baseLog(type: Level,msg : String, throwable: Throwable? = null) {
        if(!enable) {
            return
        }
        val element = findCaller()
        val text = if(element == null) msg else "(${element.fileName}:${element.lineNumber}) ${element.methodName}()${if(msg.isEmpty()) "" else " : $msg"}"
        when(type) {
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