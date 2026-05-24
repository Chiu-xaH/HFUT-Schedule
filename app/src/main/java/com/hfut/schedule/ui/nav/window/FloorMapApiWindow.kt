package com.hfut.schedule.ui.nav.window

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.xah.common.ui.util.text

data class FloorMapApiWindow(
    val classroom : String
) : FloatingWindow() {
    override val key = "floor_map_${classroom}"
    override val title = text("教室导向")

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun BoxScope.Content() {
       // TODO
    }
}