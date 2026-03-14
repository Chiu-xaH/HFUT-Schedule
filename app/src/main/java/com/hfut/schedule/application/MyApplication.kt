package com.hfut.schedule.application

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.model.Location
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.network.util.Constant
import com.xah.shared.LogUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Collections

class MyApplication : Application() {
    companion object {
        // 全局动画速度ANIMATION_SPEED=400ms已经迁移到AnimationManager
        @SuppressLint("StaticFieldLeak")
        // 全局上下文
        lateinit var context: Context
        // 方格默认高度
        const val CALENDAR_SQUARE_HEIGHT = 125f
        const val CALENDAR_SQUARE_HEIGHT_NEW = 70f
        const val CALENDAR_SQUARE_TEXT_PADDING = 1.35f
        const val CALENDAR_SQUARE_ALPHA = 0.6f
        const val SWIPE = 5f
        // HAZE模糊半径
        const val BLUR_RADIUS = 20
        // 宣城校区免费流量额度 GiB
        const val DEFAULT_MAX_FREE_FLOW = 30
        // 支付宝打开URL
        private const val ALIPAY_URL = "alipays://platformapi/startapp?appId=20000067&url="
        private const val ALIPAY_INNER_URL = "https://ur.alipay.com/"
        // 支付宝 校园卡
        const val ALIPAY_CARD_URL = ALIPAY_URL + ALIPAY_INNER_URL + "_4kQhV32216tp7bzlDc3E1k"
        // 支付宝 趣智校园
        const val ALIPAY_HOT_WATER_URL = ALIPAY_URL + ALIPAY_INNER_URL + "_3B2YzKjbV75xL9a2oapcNz"
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
            // https://api.github.com/users/${key}
            mapOf(
                Constant.GITHUB_DEVELOPER_NAME to 116127902,
                "tinyvan" to 27542299,
                "linsui" to 36977733,
                "James-Zhang2" to 175417444,
                "Today1337" to 110648923,
                "zxbmmmmmmmmm" to 96322503
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
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        LogUtil.tag = APP_NAME
        GlobalScope.launch {
            DateTimeManager.initCurrentWeekValue()
        }
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(a: Activity, b: Bundle?) {
                activities.add(a)
            }

            override fun onActivityDestroyed(a: Activity) {
                activities.remove(a)
            }

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivitySaveInstanceState(
                activity: Activity,
                outState: Bundle
            ) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}
        })
    }
}

