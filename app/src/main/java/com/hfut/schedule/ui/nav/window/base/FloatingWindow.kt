package com.hfut.schedule.ui.nav.window.base

import com.xah.common.ui.model.text.UiText
import com.xah.floating.model.Window

abstract class FloatingWindow : Window() {

    open val description : String? = null

    abstract val title : UiText
}