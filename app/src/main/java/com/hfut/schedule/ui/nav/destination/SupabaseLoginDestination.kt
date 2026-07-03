package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.supabase.login.SupabaseLoginScreen
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavDependencies

object SupabaseLoginDestination : NavDestination() {
    override val key = "supabase_login"
    override val title = text("共建平台-登录")
    override val icon = R.drawable.login

    @Composable
    override fun Content() {
        val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
        SupabaseLoginScreen(networkVm)
    }
}