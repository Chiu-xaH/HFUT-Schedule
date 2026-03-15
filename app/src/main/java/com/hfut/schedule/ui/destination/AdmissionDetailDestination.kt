package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.school.admission.AdmissionRegionScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.common.ui.util.res

data class AdmissionDetailDestination(
    val index : Int,
    val type : String
) : NavDestination() {
    override val key = "admission_region_${index}_$type"
    override val title = res(R.string.navigation_label_admission_detail)
    override val icon = R.drawable.publics

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        AdmissionRegionScreen(vm,type,index)
    }
}