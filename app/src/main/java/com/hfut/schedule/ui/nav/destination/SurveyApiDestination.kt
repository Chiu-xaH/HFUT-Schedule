package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.res
import com.xah.navigation.util.LocalNavDependencies

class SurveyApiDestination(
    val lessonCode : String,
) : NavDestination() {
    override val key = "survey_$lessonCode"
    override val description = lessonCode
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_survey)
        val ICON = R.drawable.verified
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        // TODO
    }
}