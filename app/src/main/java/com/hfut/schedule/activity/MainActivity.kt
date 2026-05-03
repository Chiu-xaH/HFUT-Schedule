package com.hfut.schedule.activity

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.shortcut.AppShortcutManager
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.receiver.widget.focus.hasFocusWidget
import com.hfut.schedule.receiver.widget.focus.refreshFocusWidget
import com.hfut.schedule.ui.nav.destination.TestDestination
import com.hfut.schedule.ui.screen.MainHost
import com.hfut.schedule.ui.util.state.GlobalStateHolder.postedUse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    @Composable
    override fun UI() = MainHost(
        super.networkVm,
        super.loginVm,
        super.uiVm,
        intent.getBooleanExtra("login", false),
        false,
//        TestDestination::class.java.name,
        intent.getStringExtra("route"),
    )

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
                    postedUse == false && // 全局只传一次
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




