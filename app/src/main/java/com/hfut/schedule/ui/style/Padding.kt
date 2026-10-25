package com.hfut.schedule.ui.style

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import com.xah.common.logic.util.LogUtil
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.padding.getNavigationBarHeight

fun Modifier.navigationBarSupplementPadding() : Modifier = composed {
    val navigationBarHeight = getNavigationBarHeight()
    return@composed this.padding(
        bottom = if(navigationBarHeight > APP_HORIZONTAL_DP) {
            0.dp
        } else {
            APP_HORIZONTAL_DP - navigationBarHeight
        }
    )
}

fun Modifier.appNavigationBarPadding() : Modifier = this
    .navigationBarSupplementPadding()
    .navigationBarsPadding()