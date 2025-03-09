package com.hfut.schedule.App

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.utils.DateTimeUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        // 全局上下文
        lateinit var context: Context
        // 全局模糊半径
        val BLUR_RADIUS = 20.dp
        // 全局动画速度
        const val ANIMATION_SPEED = 400
        // 宣城校区免费流量额度
        const val MAX_FREE_FLOW = 50
        // 教务系统
        const val JXGLSTU_URL = "http://jxglstu.hfut.edu.cn/eams5-student/"
        // Github
        const val GITHUB_URL = "https://api.github.com/"
        // WEBVPN 校外访问
        const val JXGLSTU_WEBVPN_URL = "https://webvpn.hfut.edu.cn/http/77726476706e69737468656265737421faef469034247d1e760e9cb8d6502720ede479/eams5-student/"
        // WEBVPN 校外访问
        const val WEBVPN_URL = "https://webvpn.hfut.edu.cn/"
        // 通知公告
        const val NEWS_URL = "https://news.hfut.edu.cn/"
        // 合工大教务 UNIAPP
        const val JWGLAPP_URL = "https://jwglapp.hfut.edu.cn/"
        // 更新渠道
        const val GITEE_UPDATE_URL = "https://gitee.com/chiu-xah/HFUT-Schedule/"
        // 校园卡内网平台（哈尔滨中新）
        const val ELECTRIC_URL = "http://172.31.248.26:8988/"
        // 智慧社区
        const val COMMUNITY_URL = "https://community.hfut.edu.cn/"
        // 指尖工大 慧新易校
        const val ZJGD_URL = "http://121.251.19.62/"
        // 乐跑云运动
        const val LEPAO_URL = "http://210.45.246.53:8080/"
        // 支付宝 校园卡
        const val ALIPAY_CARD_URL = "alipays://platformapi/startapp?appId=20000067&url=https://ur.alipay.com/_4kQhV32216tp7bzlDc3E1k"
        // 支付宝 趣智校园
        const val ALIPAY_HOT_WATER_URL = "alipays://platformapi/startapp?appId=20000067&url=https://ur.alipay.com/_3B2YzKjbV75xL9a2oapcNz"
        // 信息门户
        const val ONE_URL = "https://one.hfut.edu.cn/"
        // CAS统一认证
        const val CAS_LOGIN_URL = "https://cas.hfut.edu.cn/"
        // API
        const val MY_API_URL = "https://chiu-xah.github.io/"
        // 宣城校区寝室分数
        const val DORMITORY_SCORE_URL = "http://39.106.82.121/"
        // 宣城校区校园网
        const val LOGIN_WEB_XUANCHENG_URL = "http://172.18.3.3/"
        const val LOGIN_WEB_XUANCHENG2_URL = "http://172.18.2.2/"
        // 登陆失败 重定向
        const val REDIRECT_URL = "https://cas.hfut.edu.cn/cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin&exception.message=A+problem+occurred+restoring+the+flow+execution+with+key+%27e1s1%27"
        // 呱呱物联小程序
        const val GUAGUA_SHOWER_URL = "https://guagua.klcxkj-qzxy.cn/"
        // 宣城校区通知公告
        const val NEWS_XUANCHENG_URL = "https://xc.hfut.edu.cn/"
        // 教师检索
        const val TEACHER_URL = "https://faculty.hfut.edu.cn/"
        // Github用户头像
        const val GITHUB_USER_IMAGE_URL = "https://avatars.githubusercontent.com/u/"
        // 和风天气
        const val QWEATHER_URL = "https://devapi.qweather.com/v7/"
        // 学工系统
        const val STU_URL = "https://stu.hfut.edu.cn/"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}