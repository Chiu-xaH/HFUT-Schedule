package com.xah.common.ui.style.padding

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xah.common.ui.style.APP_HORIZONTAL_DP

val navigationBarHeightPadding = APP_HORIZONTAL_DP/3*4
@Composable
fun NavigationBarSpacer() {
    Spacer(Modifier.height(navigationBarHeightPadding))
}