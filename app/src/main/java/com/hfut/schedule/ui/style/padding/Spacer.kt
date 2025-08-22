package com.hfut.schedule.ui.style.padding

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP

val navigationBarHeightPadding = APP_HORIZONTAL_DP/3*4
@Composable
fun NavigationBarSpacer() {
    Spacer(Modifier.height(navigationBarHeightPadding))
}