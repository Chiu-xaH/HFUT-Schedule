package com.hfut.schedule.App

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.ui.unit.dp

class MyApplication : Application() {
    companion object {
        // 全局动画速度ANIMATION_SPEED=400ms已经迁移到AnimationManager
        @SuppressLint("StaticFieldLeak")
        // 全局上下文
        lateinit var context: Context
        // 全局模糊半径
        val BLUR_RADIUS = 20.dp
        // 宣城校区免费流量额度 GB
        const val MAX_FREE_FLOW = 30
        // 默认每页数量
        const val PAGE_SIZE = 30
        // 教务系统
        const val JXGLSTU_URL = "http://jxglstu.hfut.edu.cn/eams5-student/"
        // Github
        const val GITHUB_API_URL = "https://api.github.com/"
        const val GITHUB_RAW_URL = "https://raw.githubusercontent.com/"
        // WEBVPN 校外访问
        const val WEBVPN_URL = "https://webvpn.hfut.edu.cn/"
        // 教务系统-WEBVPN 校外访问
        const val JXGLSTU_WEBVPN_URL = WEBVPN_URL + "/http/" + "77726476706e69737468656265737421faef469034247d1e760e9cb8d6502720ede479/eams5-student/"
        // 通知公告
        const val NEWS_URL = "https://news.hfut.edu.cn/"
        // 合工大教务 微信UNI-APP
        const val UNI_APP_URL = "https://jwglapp.hfut.edu.cn/"
        // 更新渠道
        const val GITEE_UPDATE_URL = "https://gitee.com/chiu-xah/HFUT-Schedule/"
        // 智慧社区
        const val COMMUNITY_URL = "https://community.hfut.edu.cn/"
        // 慧新易校
        const val HUIXIN_URL = "http://121.251.19.62/"
        // 乐跑云运动
        const val LEPAO_URL = "http://210.45.246.53:8080/"
        // 支付宝打开URL
        private const val ALIPAY_URL = "alipays://platformapi/startapp?appId=20000067&url="
        // 支付宝 校园卡
        const val ALIPAY_CARD_URL = ALIPAY_URL + "https://ur.alipay.com/_4kQhV32216tp7bzlDc3E1k"
        // 支付宝 趣智校园
        const val ALIPAY_HOT_WATER_URL = ALIPAY_URL + "https://ur.alipay.com/_3B2YzKjbV75xL9a2oapcNz"
        // 信息门户
        const val ONE_URL = "https://one.hfut.edu.cn/"
        // CAS统一认证
        const val CAS_LOGIN_URL = "https://cas.hfut.edu.cn/"
        // API
        const val MY_API_URL = "https://chiu-xah.github.io/"
        // 宣城校区寝室分数
        const val DORMITORY_SCORE_URL = "http://39.106.82.121/"
        // 宣城校区校园网
        const val LOGIN_WEB_XC_URL = "http://172.18.3.3/"
        const val LOGIN_WEB_XC2_URL = "http://172.18.2.2/"
        // 合肥校区校园网
        const val LOGIN_WEB_HEFEI_URL = "http://172.16.200.11/"
        // 登陆失败 重定向
        const val REDIRECT_URL = CAS_LOGIN_URL + "cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin&exception.message=A+problem+occurred+restoring+the+flow+execution+with+key+%27e1s1%27"
        // 呱呱物联小程序
        const val SHOWER_URL = "https://bathing.hfut.edu.cn/"
        // 宣城校区通知公告
        const val NEWS_XC_URL = "https://xc.hfut.edu.cn/"
        // 教师检索
        const val TEACHER_URL = "https://faculty.hfut.edu.cn/"
        // Github用户头像
        const val GITHUB_USER_IMAGE_URL = "https://avatars.githubusercontent.com/u/"
        // 和风天气
        const val QWEATHER_URL = "https://devapi.qweather.com/v7/"
        // 学工系统
        const val STU_URL = "https://stu.hfut.edu.cn/"
        // 智慧后勤 宣城
        const val REPAIR_XC_URL = "http://xcfw.hfut.edu.cn/school/"
        // 智慧后勤 合肥
        const val REPAIR_URL = "http://zhhq.hfut.edu.cn/school/"
        // 老图书馆 校园网
        const val OLD_LIBRARY_URL = "http://210.45.242.5:8080/"
        // 新图书馆
        const val NEW_LIBRARY_URL = "https://lib.hfut.edu.cn/"
        // 座位预约 校园网
        const val LIBRARY_SEAT = "http://210.45.242.57/"
        // 就业
        const val WORK_URL = "https://gdjy.hfut.edu.cn/"
        // 就业 宣城
        const val WORK_XC_URL = "https://xcjy.hfut.edu.cn/"
        // 就业检索
        const val WORK_SEARCH_URL = "https://dc.bysjy.com.cn/"
        // 大创
        const val IETP_URL = "http://dcxt.hfut.edu.cn/"
        // 校友
        const val ALUMNI_URL = "https://xypt.hfut.edu.cn/"
        // 体育平台
        const val PE_URL = "https://bdlp.hfut.edu.cn/"
        // Supabase
        const val SUPABASE_URL = "https://uadgxvstybecnhqemxvj.supabase.co/"
        // Vercel
        const val VERCEL_FORECAST_URL = "https://consumption-forecast.vercel.app/"
        // Github常量池
        const val GITHUB_USER_ID = 116127902
        const val GITHUB_DEVELOPER_NAME = "Chiu-xaH"
        const val GITHUB_REPO_NAME = "HFUT-Schedule"
        // 邮箱
        const val EMAIL = "@mail.hfut.edu.cn"
        // WEBVPN COOKIE前缀
        const val WEBVPN_COOKIE_HEADER = "wengine_vpn_ticketwebvpn_hfut_edu_cn="
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}