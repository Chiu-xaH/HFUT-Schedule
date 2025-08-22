package com.hfut.schedule.ui.style.padding

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP

@Composable
fun InnerPaddingHeight(innerPadding : PaddingValues,isTopOrBottom : Boolean) {
    if(isTopOrBottom) {
        Spacer(Modifier.height(innerPadding.calculateTopPadding()))
    } else {
        Spacer(Modifier.height(innerPadding.calculateBottomPadding()))
        Spacer(Modifier.height(APP_HORIZONTAL_DP).navigationBarsPadding())
    }
}