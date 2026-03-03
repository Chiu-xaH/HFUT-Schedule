package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.welcome.UpdateSuccessScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.UiText
import com.xah.uicommon.util.language.res

object UpdateSuccessDestination : NavDestination() {
    override val key: String = "update_success"
    override val title: UiText = res(R.string.navigation_label_update_successfully)

    @Composable
    override fun Content() {
        UpdateSuccessScreen()
    }
}