package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey.SurveyScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

data class SurveyDestination(
    val ifSaved : Boolean
) : NavDestination() {
    override val key = "survey_$ifSaved"
    override val title = res(R.string.navigation_label_survey)

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        SurveyScreen(ifSaved,vm)
    }
}