package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.DEEPLINK_SCHEME
import com.hfut.schedule.ui.screen.home.search.function.other.AiScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.res
import com.xah.navigation.util.LocalNavDependencies

data class AiDestination(
    val keyword : String? = null,
) : NavDestination() {
    override val key = "${KEY}_$keyword"
    override val title = TITLE
    override val icon = ICON

    companion object {
        const val KEY = "ai"
        val TITLE = res(R.string.navigation_label_ai)
        val ICON = R.drawable.wand_stars
    }

    fun toDeepLink() : String = "$DEEPLINK_SCHEME$KEY?keyword=$keyword"

    @Composable
    override fun Content() {
        val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
        AiScreen(networkVm,keyword)
    }
}