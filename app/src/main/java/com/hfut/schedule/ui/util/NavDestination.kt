package com.hfut.schedule.ui.util

import com.hfut.schedule.R
import com.xah.navigation.model.dest.Destination
import com.xah.uicommon.util.language.UiText

abstract class NavDestination : Destination() {
    abstract val title : UiText
    open val description : String? = null
    open val icon : Int = R.drawable.stacks
}