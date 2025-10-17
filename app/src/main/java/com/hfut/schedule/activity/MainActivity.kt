package com.hfut.schedule.activity

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.screen.MainHost
import com.hfut.schedule.ui.screen.home.cube.screen.AppTransitionInitializer
import com.hfut.schedule.ui.util.GlobalUIStateHolder.postedUse
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
        intent.getStringExtra("route"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            launch {
                AppTransitionInitializer.init()
            }
            launch {
                LargeStringDataManager.moveLargeJson(this@MainActivity)
            }
            //上传用户统计数据
            launch(Dispatchers.IO) {
                val switchUpload = prefs.getBoolean("SWITCHUPLOAD",true)
                if(
                    switchUpload && // 用户决定
                    postedUse == false && // 全局只传一次
                    !AppVersion.isPreview() && // Preview内部版本不传
                    !AppVersion.isInDebugRunning() // 跑在Avd的测试机不传
                ) {
                    networkVm.postUser()
                    postedUse = true
                }
            }
        }
    }
}




