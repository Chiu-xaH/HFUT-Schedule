package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.util.ExceptionScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.res


data class ExceptionDestination(
    val exception : Throwable
) : NavDestination() {
    override val key = "exception_${exception.hashCode()}"
    override val title = res(R.string.navigation_label_exception)
    override val icon = R.drawable.bug_report

    @Composable
    override fun Content() {
        ExceptionScreen(exception)
    }
}