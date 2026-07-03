package com.hfut.schedule.activity

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.receiver.widget.focus.hasFocusWidget
import com.hfut.schedule.receiver.widget.focus.refreshFocusWidget
import com.hfut.schedule.ui.nav.destination.AgreementDestination
import com.hfut.schedule.ui.nav.destination.ExceptionDestination
import com.hfut.schedule.ui.nav.destination.HomeDestination
import com.hfut.schedule.ui.nav.destination.UpdateSuccessfullyDestination
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.MainHost
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder.postedUse
import com.xah.navigation.registry.DeepLinkRegistry
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    @Composable
    override fun UI() {
        val startDestination: NavDestination =
            // 接口1 Intent
            intent.getStringExtra("route")?.let { route ->
                parseStartDestinationByRoute(route)
            } ?:
            // 接口2 DeepLink
            intent?.data?.let { deeplink ->
                val destination = DeepLinkRegistry.parse(deeplink)
                if(destination == null) {
                    ExceptionDestination(Exception("打开深度链接($deeplink)失败,请发起跳转方确认接口是否正确"))
                } else {
                    destination as NavDestination
                }
            } ?:
            // 默认预设第一屏
            getDefaultStartDestination()

        MainHost(
            super.networkVm,
            super.loginVm,
            intent.getBooleanExtra("login", false),
            false,
            startDestination,
        )
    }

    private fun parseStartDestinationByRoute(route : String) : NavDestination {
        return try {
            val finalClassName = if(route.split(".").size == 1) {
                "com.hfut.schedule.ui.nav.destination.$route"
            } else {
                route
            }
            val clazz = Class.forName(finalClassName)
            clazz.getField("INSTANCE").get(null) as NavDestination
        } catch (e : Exception) {
            LogUtil.error(e)
            ExceptionDestination(e)
        }
    }
    private fun getDefaultStartDestination() : NavDestination {
        return if(prefs.getBoolean("canUse",false)) {
            if(!haveImportantUpdate()) {
                HomeDestination()
            } else {
                UpdateSuccessfullyDestination
            }
        } else {
            AgreementDestination
        }
    }

    // 比较版本号 前2位相同则不显示 否则显示
    private fun haveImportantUpdate() : Boolean {
        try {
            val lastVersionName =  prefs.getString("versionName", "上版本") ?: return true
            val nowVersionName = AppVersion.getVersionName()

            if(lastVersionName == nowVersionName) {
                return false
            }

            if(lastVersionName == "上版本") {
                return true
            }

            val lastVersion = lastVersionName.split('.')
            if(lastVersion.size < 2) {
                return false
            }

            val nowVersion = nowVersionName.split('.')
            if(nowVersion.size < 2) {
                return false
            }

            return !(nowVersion[1] == lastVersion[1] && nowVersion[0] == lastVersion[0])
        } catch (e : Exception) {
            LogUtil.error(e)
            return false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            launch {
                if(hasFocusWidget(this@MainActivity) > 0) {
                    // 立刻刷新小组件
                    refreshFocusWidget(this@MainActivity)
                }
            }
            launch {
                // 大文本迁移
                LargeStringDataManager.moveLargeJson()
            }
            launch(Dispatchers.IO) {
                // 埋点，上传用户统计数据
                val switchUpload = prefs.getBoolean("SWITCHUPLOAD",true)
                if(
                    switchUpload && // 用户决定
                    !postedUse && // 全局只传一次
                    !AppVersion.isDev && // 内部版本不传
                    !AppVersion.isRunningOnAvd && // 跑在Avd的测试机不传
                    !AppVersion.isDebug  // Debug版本不传
                ) {
                    networkVm.postUser()
                    postedUse = true
                }
            }
        }
    }
}




