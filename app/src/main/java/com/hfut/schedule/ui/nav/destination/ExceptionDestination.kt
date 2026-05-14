package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.util.ExceptionScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res


data class ExceptionDestination(
    val exception : Throwable
) : NavDestination() {
    override val key = "exception_${exception.hashCode()}"
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_exception)
        val ICON =  R.drawable.error
    }

    @Composable
    override fun Content() {
        ExceptionScreen(exception)
    }
}