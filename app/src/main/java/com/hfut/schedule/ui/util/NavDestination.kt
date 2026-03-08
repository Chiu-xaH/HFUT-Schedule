package com.hfut.schedule.ui.util

import com.xah.navigation.model.dest.SharedDestination
import com.xah.uicommon.util.language.UiText

abstract class NavDestination : SharedDestination() {
    abstract val title : UiText
    open val description : String? = null
}