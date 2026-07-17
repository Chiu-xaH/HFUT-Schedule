package com.hfut.schedule.application

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.model.AppStatus
import com.hfut.schedule.logic.model.Location
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.CourseLiveUpdateScheduler
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.network.helper.Constant
import com.hfut.schedule.ui.nav.deepLinks
import com.hfut.schedule.ui.util.state.GlobalEventHolder
import com.xah.navigation.registry.DeepLinkRegistry
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Collections

class MyApplication : Application() {

    // 这里必须严格限制，不许放入太耗时的方法，不是不得不在Activity之前初始化的函数别放这里，放Activity里或者Compose里都可以
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        // 暴露全局Context
        context = applicationContext
        // 初始化日志工具
        LogUtil.init(APP_NAME)
        // 控制SharedNav库的日志 需要联调排查SharedNav错误时传入debug=BuildConfig.DEBUG，平常时不需要开启日志。要不然Debug包动画有点卡
        com.sharednav.common.util.LogUtil.init("SharedNav(${APP_NAME})",true)
        // 注册DeepLink
        DeepLinkRegistry.init(deepLinks)
        GlobalScope.launch {
            // 初始化周数（为课程表服务）
            DateTimeManager.initCurrentWeekValue()
            // Added by @Junpgle 为实时通知服务，如需修改代码请找Ta
            if (DataStoreManager.enableLiveCourseReminder.first()) {
                CourseLiveUpdateScheduler.scheduleAll()
                CourseLiveUpdateScheduler.showCurrentWindowCourses()
            }
        }
        // 监听应用前后台变化
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    GlobalEventHolder.appStatusChanged.emit(AppStatus.FOREGROUND)
                }

                override fun onStop(owner: LifecycleOwner) {
                    GlobalEventHolder.appStatusChanged.emit(AppStatus.BACKGROUND)
                }
            }
        )
        // 监听Activity栈
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(a: Activity, b: Bundle?) {
                activities.add(a)
            }

            override fun onActivityDestroyed(a: Activity) {
                activities.remove(a)
            }

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {
                // Added by @Junpgle 为实时通知服务，如需修改代码请找Ta
                GlobalScope.launch {
                    if (DataStoreManager.enableLiveCourseReminder.first()) {
                        CourseLiveUpdateScheduler.showCurrentWindowCourses()
                    }
                }
            }

            override fun onActivitySaveInstanceState(
                activity: Activity,
                outState: Bundle
            ) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}
        })
    }

    companion object {
        // 全局动画速度ANIMATION_SPEED=400ms已经迁移到AnimationManager
        @SuppressLint("StaticFieldLeak")
        // 全局上下文
        lateinit var context: Context
        // 方格默认高度
        const val CALENDAR_SQUARE_HEIGHT = 70f
        const val CALENDAR_SQUARE_TEXT_PADDING = 1.35f
        const val CALENDAR_SQUARE_ALPHA = 0.6f
        const val SWIPE = 5f
        // HAZE模糊半径
        const val BLUR_RADIUS = 20
        // 宣城校区免费流量额度 GiB
        const val DEFAULT_MAX_FREE_FLOW = 200
        // 最大周
        const val MAX_WEEK = 20
        // 三个校区的经纬度 来自高德地图坐标拾取器
        val campusLocations by lazy {
            mapOf<Campus, Location>(
                Campus.FCH to Location(117.20346, 31.77014),
                Campus.TXL to Location(117.29597, 31.843905),
                Campus.XC to Location(118.710182, 30.903593)
            )
        }
        // 贡献者
        val contributors by lazy {
            // 通过 https://api.github.com/users/${用户名}获取ID
            mapOf(
                Constant.GITHUB_DEVELOPER_NAME to 116127902,
                "tinyvan" to 27542299,
                "linsui" to 36977733,
                "James-Zhang2" to 175417444,
                "Today1337" to 110648923,
                "zxbmmmmmmmmm" to 96322503,
                "Junpgle" to 81464408,
            )
        }
        // App名称
        const val APP_NAME = "聚在工大"
        // 邮箱后缀
        const val EMAIL = "@mail.hfut.edu.cn"
        // 启动台遮罩
        const val CONTROL_CENTER_BACKGROUND_MASK_ALPHA = 0.125f
        // Activity栈
        private val activities = Collections.synchronizedList(mutableListOf<Activity>())
        // 安全地退出App
        fun exitAppSafely() {
            activities.toList().forEach { activity ->
                activity.finish()
            }
        }
        // 获取当前Activity
        fun getCurrentActivity() = activities.lastOrNull()
    }
}

