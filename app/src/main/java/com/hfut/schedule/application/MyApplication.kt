package com.hfut.schedule.application

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.model.Location
import com.hfut.schedule.logic.util.network.WebVpnUtil
import com.xah.uicommon.util.LogUtil
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
        // 默认每页数量
        const val DEFAULT_PAGE_SIZE = 30
        const val GITEE_TOKEN = "bf51445a2add374b70205ca460f9a696"
        // 教务系统 有时需校园网
        const val JXGLSTU_URL = "http://jxglstu.hfut.edu.cn/eams5-student/"
        // Github
        const val GITHUB_API_URL = "https://api.github.com/"
        const val GITHUB_RAW_URL = "https://raw.githubusercontent.com/"
        // WEBVPN 有时无法访问
        const val WEBVPN_URL = "https://webvpn.hfut.edu.cn/"
        // 教务系统-WEBVPN 校外访问
        val JXGLSTU_WEBVPN_URL by lazy { WebVpnUtil.getWebVpnUrl(JXGLSTU_URL) }
        // 通知公告 有些通知需校园网
        const val NEWS_URL = "https://news.hfut.edu.cn/"
        // 办事大厅
        const val OFFICE_HALL_URL = "https://ehall.hfut.edu.cn/"
        // 校务行
        private const val XWX_HOST = "https://xwx.gzzmedu.com"
        const val XWX_URL = "$XWX_HOST:9080/"
        const val XWX_PICTURE_URL = "$XWX_HOST:6899/"
        // 合工大教务 有时需校园网
        const val UNI_APP_URL = "https://jwglapp.hfut.edu.cn/"
        const val UNI_APP_HOME_PAGE_URL = UNI_APP_URL + "uniapp/"
        const val UNI_APP_LOGIN_RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCFY5N+9UX+0BF+xz1svFguI4CIDvmQTfINkOZ1HOO3ltBNHGQTUirUPQTyEph/+q/l8b16YYw3I2fyTH6y15s3tHf5jMei+R/20jFRGo5udwVJUwq/RozKQIRzCtPYkXG4YWBnHKhXalZ5K2fhd5i/QtB016nVugH/7eiBDWbKVwIDAQAB"
        // 更新
        const val GITEE_URL = "https://gitee.com/"
        const val GITEE_UPDATE_URL = GITEE_URL + "chiu-xah/HFUT-Schedule/"
        // 智慧社区
        const val COMMUNITY_URL = "https://community.hfut.edu.cn/"
        // 慧新易校
        const val HUI_XIN_URL = "http://121.251.19.62/"
        // 指间工大
        const val ZHI_JIAN_URL = "https://zjgd.hfut.edu.cn:8181/"
        // 乐跑云运动
        const val LE_PAO_URL = "http://210.45.246.53:8080/"
        // 支付宝打开URL
        private const val ALIPAY_URL = "alipays://platformapi/startapp?appId=20000067&url="
        private const val ALIPAY_INNER_URL = "https://ur.alipay.com/"
        // 支付宝 校园卡
        const val ALIPAY_CARD_URL = ALIPAY_URL + ALIPAY_INNER_URL + "_4kQhV32216tp7bzlDc3E1k"
        // 支付宝 趣智校园
        const val ALIPAY_HOT_WATER_URL = ALIPAY_URL + ALIPAY_INNER_URL + "_3B2YzKjbV75xL9a2oapcNz"
        // 信息门户
        const val ONE_URL = "https://one.hfut.edu.cn/"
        // CAS统一认证
        const val CAS_LOGIN_URL = "https://cas.hfut.edu.cn/"
        // API
        const val MY_API_URL = "https://chiu-xah.github.io/"
        // 宣城校区 寝室分数
        const val DORMITORY_SCORE_URL = "http://39.106.82.121/"
        // 宣城校区 校园网
        const val LOGIN_WEB_XC_URL = "http://172.18.3.3/"
        const val LOGIN_WEB_XC2_URL = "http://172.18.2.2/"
        // 合肥校区 校园网
        const val LOGIN_WEB_HEFEI_URL = "http://172.16.200.11/"
        // 呱呱物联
        const val SHOWER_URL = "https://bathing.hfut.edu.cn/"
        // 宣城校区 通知公告 有些通知需校园网
        const val NEWS_XC_URL = "https://xc.hfut.edu.cn/"
        // 教师检索 有时需校园网
        const val TEACHER_URL = "http://121.251.19.138/"
        // Github用户头像
        const val GITHUB_USER_IMAGE_URL = "https://avatars.githubusercontent.com/u/"
        // 和风天气
        const val Q_WEATHER_URL = "https://devapi.qweather.com/v7/"
        // 学工系统
        const val STU_URL = "https://stu.hfut.edu.cn/"
        // 智慧后勤 报修 宣城
        const val REPAIR_XC_URL = "http://xcfw.hfut.edu.cn/school/"
        // 智慧后勤 座位预约 报修 合肥
        const val REPAIR_URL = "http://zhhq.hfut.edu.cn/school/"
        // 老图书馆 需校园网
        const val OLD_LIBRARY_URL = "http://210.45.242.5:8080/"
        // 新图书馆 有时需校园网
        const val NEW_LIBRARY_URL = "https://lib.hfut.edu.cn/"
        // 座位预约 校园网
        const val LIBRARY_SEAT = "http://210.45.242.57/"
        // 就业
        const val WORK_URL = "https://gdjy.hfut.edu.cn/"
        // 就业 宣城
        const val WORK_XC_URL = "https://xcjy.hfut.edu.cn/"
        // 就业检索
        const val WORK_SEARCH_URL = "https://dc.bysjy.com.cn/"
        // 大创 校园网
        const val IETP_URL = "http://dcxt.hfut.edu.cn/"
        // 校友平台
        const val ALUMNI_URL = "https://xypt.hfut.edu.cn/"
        // 体测平台 校园网
        const val PE_URL = "https://bdlp.hfut.edu.cn/"
        const val PE_HOME_URL = PE_URL + "bdlp_h5_fitness_test/view/main/newIndex.html"
        // 指尖工大 扫码登录
        const val WX_URL = "https://wx.hfut.edu.cn/"
        // 教务处 有些通知需校园网
        const val ACADEMIC_URL = "https://jwc.hfut.edu.cn/"
        // 宣城校区 教务处 有些通知需校园网
        const val XC_ACADEMIC_URL = "https://xcjwb.hfut.edu.cn/"
        // Supabase 新加坡服务器 可裸连
        const val SUPABASE_URL = "https://uadgxvstybecnhqemxvj.supabase.co/"
        // 海乐生活 洗衣机
        const val WASHING_URL = "https://yshz-user.haier-ioc.com/"
        // 本科招生
        const val UNDERGRADUATE_ADMISSION_URL = "https://bkzs.hfut.edu.cn/"
        const val ADMISSION_COOKIE_HEADER = "zhaosheng.hfut.session.id="
        // 缴费 有时需校园网
        const val PAY_FEE_URL = "http://pay.hfut.edu.cn/payment/mobileOnlinePay"
        // 学信网
        const val XUE_XIN_URL = "https://my.chsi.com.cn/archive/wap/gdjy/index.action"
        // 研讨间预约 校园网
        const val MEETING_ROOM_URL = "http://210.45.243.31:81"
        // U校园
        const val UNIPUS_URL = "https://u.unipus.cn/"
        const val UNIPUS_AI_URL = "https://ucloud.unipus.cn/"
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
        const val GITHUB_URL = "https://github.com/"
        // Github常量池
        const val GITHUB_DEVELOPER_NAME = "Chiu-xaH"
        const val GITHUB_REPO_NAME = "HFUT-Schedule"
        // 贡献者
        val contributors by lazy {
            mapOf(
                GITHUB_DEVELOPER_NAME to 116127902,
                "tinyvan" to 27542299,
                "linsui" to 36977733,
                "James-Zhang2" to 175417444,
                "Today1337" to 110648923,
            )
        }
        // App名称
        const val APP_NAME = "聚在工大"
        // 仓库地址
        const val GITHUB_REPO_URL = "$GITHUB_URL$GITHUB_DEVELOPER_NAME/$GITHUB_REPO_NAME"
        // 邮箱后缀
        const val EMAIL = "@mail.hfut.edu.cn"
        // WEBVPN COOKIE前缀
        const val WEBVPN_COOKIE_HEADER = "wengine_vpn_ticketwebvpn_hfut_edu_cn="
        // PC UA
        const val PC_UA = "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17"
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

