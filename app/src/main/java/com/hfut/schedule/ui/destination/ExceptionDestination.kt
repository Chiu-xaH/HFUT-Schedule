package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.util.NavigationExceptionScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.res


data class ExceptionDestination(
    val exception : String
) : NavDestination() {
    override val key = "exception_$exception"
    override val title = res(R.string.navigation_label_exception)

    @Composable
    override fun Content() {
        NavigationExceptionScreen(exception)
    }
}