package com.hfut.schedule.ui.nav.destination

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.network.api.model.response.json.haile.HaiLeNearPositionBean
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.home.search.function.huiXin.washing.HaiLeDetailScreen
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.res
import com.xah.navigation.util.LocalNavDependencies

data class HaiLeWashingDetailDestination(
    val item: HaiLeNearPositionBean
) : NavDestination() {
    override val key = "washing_detail_${item.id}"
    override val title = res(R.string.navigation_label_washing_detail)
    override val description = item.name
    override val icon = R.drawable.near_me

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        HaiLeDetailScreen(vm,item,description)
    }
}