package com.hfut.schedule.logic.util.dev

import android.content.Context
import android.os.Build
import android.os.Debug
import android.os.Handler
import android.os.Looper
import android.os.Trace
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 方法耗时自动采样器
 * by MyFlicker AI
 * Vibe Coding写的，不确定能不能用，暂时放着，不要调用！
 */


class MethodTracer(private val appName: String = "MyMethodTracer") {

    private var isTracing = false
    private var autoStopRunnable: Runnable? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * 开始采样
     * @param durationMs   自动停止时间，默认 5 秒，0 = 不自动停止
     * @param bufferSize   缓冲区大小，默认 16MB
     * @param intervalUs   采样间隔，默认 500μs（越小越精确，开销越大）
     */
    fun start(
        durationMs: Long = 5000,
        bufferSize: Int = 16 * 1024 * 1024,
        intervalUs: Int = 500
    ) {
        if (isTracing) {
            Log.w(TAG, "Already tracing, skip.")
            return
        }

        isTracing = true
        Debug.startMethodTracingSampling(appName, bufferSize, intervalUs)
        Log.i(TAG, "Tracing started. interval=${intervalUs}μs, buffer=${bufferSize / 1024 / 1024}MB")

        // 自动停止
        if (durationMs > 0) {
            autoStopRunnable = Runnable { stop() }
            mainHandler.postDelayed(autoStopRunnable!!, durationMs)
            Log.i(TAG, "Auto-stop after ${durationMs}ms")
        }
    }

    /** 停止采样 */
    fun stop() {
        if (!isTracing) return

        Debug.stopMethodTracing()
        isTracing = false

        autoStopRunnable?.let { mainHandler.removeCallbacks(it) }
        autoStopRunnable = null

        Log.i(TAG, "Tracing stopped. File: /sdcard/Android/data/$appName/files/$appName.trace")
    }

    companion object {
        private const val TAG = "MethodTracer"
    }
}


/**
 * 轻量级方法耗时追踪器
 *
 * 用法：
 *   1. AppTracer.start(context)       → 开始录制
 *   2. 代码中用 AppTracer.section("name") { ... } 埋点
 *   3. AppTracer.stop()               → 停止录制
 *   4. AppTracer.pullTrace()          → adb 命令导出
 */
object AppTracer {

    private const val TAG = "AppTracer"
    private var isTracing = false
    private var traceFile: String = ""
    private var autoStopRunnable: Runnable? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    // ======================== 启动 / 停止 ========================

    /**
     * 开始录制 System Trace
     * @param durationMs 自动停止时间，默认 10 秒，传 0 则不自动停止
     */
    fun start(context: Context, durationMs: Long = 10_000) {
        if (isTracing) {
            Log.w(TAG, "Already tracing, skip.")
            return
        }

        // 生成带时间戳的文件名
        val timestamp = SimpleDateFormat("MMdd_HHmmss", Locale.US).format(Date())
        traceFile = "app_trace_$timestamp"

        // 启动 System Trace（低开销，能看到自定义 section + 帧信息 + 系统调度）
        Trace.beginSection("AppTracer.start")
        Trace.endSection()

        // 使用 Debug.startMethodTracingSampling 录制 Java 方法耗时
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Debug.startMethodTracingSampling(
                traceFile,
                32 * 1024 * 1024,  // 32MB buffer
                500                 // 500μs 采样间隔
            )
        } else {
            Debug.startMethodTracing(traceFile, 32 * 1024 * 1024)
        }

        isTracing = true
        Log.i(TAG, "Tracing started → $traceFile")
        Trace.beginSection("TracingStarted")

        if (durationMs > 0) {
            autoStopRunnable = Runnable { stop() }
            mainHandler.postDelayed(autoStopRunnable!!, durationMs)
        }
    }

    /** 停止录制 */
    fun stop() {
        if (!isTracing) return

        Trace.endSection() // 匹配 TracingStarted

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Debug.stopMethodTracing()
        } else {
            Debug.stopMethodTracing()
        }

        autoStopRunnable?.let { mainHandler.removeCallbacks(it) }
        autoStopRunnable = null
        isTracing = false
        Log.i(TAG, "Tracing stopped. File: $traceFile")
        Log.i(TAG, "Pull: adb pull /sdcard/Android/data/${getPackageName()}/files/$traceFile.trace")
    }

    // ======================== 埋点 API ========================

    /**
     * 标记一段代码的耗时（推荐用法）
     * 在 Perfetto / Profiler 中直接显示为带耗时的色块
     */
    inline fun <T> section(name: String, block: () -> T): T {
        Trace.beginSection(name)
        try {
            return block()
        } finally {
            Trace.endSection()
        }
    }

    /**
     * 手动开始一个 section（用于跨方法的场景）
     */
    fun beginSection(name: String) {
        Trace.beginSection(name)
    }

    /**
     * 手动结束当前 section
     */
    fun endSection() {
        Trace.endSection()
    }

    // ======================== 工具方法 ========================

    /** 获取 trace 文件路径（导出用） */
    fun getTraceFilePath(): String {
        return "/sdcard/Android/data/${getPackageName()}/files/$traceFile.trace"
    }

    /** 打印导出命令 */
    fun printPullCommand() {
        Log.i(TAG, "adb pull ${getTraceFilePath()}")
    }

    private fun getPackageName(): String {
        // 从当前进程获取包名
        return try {
            val clazz = Class.forName("android.app.ActivityThread")
            val method = clazz.getMethod("currentPackageName")
            method.invoke(null) as String
        } catch (e: Exception) {
            "unknown"
        }
    }
}