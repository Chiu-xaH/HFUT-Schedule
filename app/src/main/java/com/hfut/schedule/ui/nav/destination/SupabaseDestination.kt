package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.supabase.SupabaseHome
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.common.ui.util.res
import com.xah.navigation.util.LocalNavDependencies

object SupabaseDestination : NavDestination() {
    override val key = "supabase_home"
    override val title = res(R.string.navigation_label_supabase)
    override val icon = R.drawable.cloud

    @Composable
    override fun Content() {
        val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val uiVm = LocalNavDependencies.current.get<UIViewModel>()
        SupabaseHome(networkVm,uiVm)
    }
}