package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.focus.funiction.AddEventScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.res

data class AddEventDestination(
    val id : Int?,
    val origin : String
) : NavDestination() {
    override val key: String = "add_event_${origin}_$id"
    override val title: UiText = TITLE
    override val description = "日程 $id"
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_add_event)
        val ICON = R.drawable.add
    }

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
