package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.welcome.UpdateSuccessScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.common.util.language.UiText
import com.xah.common.util.language.res

object UpdateSuccessfullyDestination : NavDestination() {
    override val key: String = "update_success"
    override val title: UiText = res(R.string.navigation_label_update_successfully)
    override val icon = R.drawable.settings

    @Composable
    override fun Content() {
        UpdateSuccessScreen()
    }
}