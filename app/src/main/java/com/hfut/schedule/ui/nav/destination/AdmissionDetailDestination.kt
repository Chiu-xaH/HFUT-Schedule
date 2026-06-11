package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.AdmissionMapBean
import com.hfut.schedule.ui.screen.home.search.function.school.admission.AdmissionRegionScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class AdmissionDetailDestination(
    val bean : Map.Entry<String, List<AdmissionMapBean>>,
    val typeStr : String
) : NavDestination() {
    override val key = "admission_region_${bean.hashCode()}_$typeStr"
    override val description = typeStr
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_admission_detail)
        val ICON = R.drawable.publics
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        AdmissionRegionScreen(vm,bean,typeStr)
    }
}