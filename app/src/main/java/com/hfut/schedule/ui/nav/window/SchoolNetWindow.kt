package com.hfut.schedule.ui.nav.window

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.LoginWebScaUI
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.util.res
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy

data class SchoolNetWindow(
    private val vm : NetWorkViewModel
): FloatingWindow() {
    companion object {
        const val KEY = "school_net"
    }

    override val key = KEY
    override val title = res(R.string.navigation_label_school_net)

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun BoxScope.Content() {
        CenterScreen {
            SharedContent(
                key = key,
                contentStrategy = ContentStrategy.Shared(keepShowContainer = false),
                shape = MaterialTheme.shapes.largeIncreased,
                modifier = Modifier
                    .padding(APP_HORIZONTAL_DP)
                    .navigationBarsPadding()
                    .statusBarsPadding()
            ) {
                LoginWebScaUI(vm)
            }
        }
    }
}