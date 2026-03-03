package com.hfut.schedule.ui.destination

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.MainScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.UiText
import com.xah.uicommon.util.language.res

object HomeDestination : NavDestination() {
    override val key: String = "home"
    override val title: UiText = res(R.string.navigation_label_home)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val vmUI = LocalNavDependencies.current.get<UIViewModel>()
        val celebrationText = LocalNavDependencies.current.get<String?>()
        val isLogin = LocalNavDependencies.current.get<Boolean>()
        MainScreen(vm,vmUI,celebrationText,isLogin)
    }
}