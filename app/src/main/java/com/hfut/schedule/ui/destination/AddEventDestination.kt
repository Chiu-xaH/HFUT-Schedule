package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.focus.funiction.AddEventScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.container.container.SharedContent
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.common.util.language.UiText
import com.xah.common.util.language.res

data class AddEventDestination(
    val id : Int?,
    val origin : String
) : NavDestination() {
    override val key: String = "add_event_${origin}_$id"
    override val title: UiText = res(R.string.navigation_label_add_event)
    override val icon = R.drawable.add

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val eventId = if(id == null || id <= 0) {
            -1
        } else {
            id
        }
        AddEventScreen(vm,eventId,origin)
    }
}
