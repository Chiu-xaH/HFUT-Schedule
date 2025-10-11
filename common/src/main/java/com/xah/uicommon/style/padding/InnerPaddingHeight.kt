package com.xah.uicommon.style.padding

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xah.uicommon.style.APP_HORIZONTAL_DP

@Composable
fun InnerPaddingHeight(innerPadding : PaddingValues,isTopOrBottom : Boolean) {
    if(isTopOrBottom) {
        Spacer(Modifier.height(innerPadding.calculateTopPadding()))
    } else {
        Spacer(Modifier.height(innerPadding.calculateBottomPadding()))
        Spacer(Modifier.height(APP_HORIZONTAL_DP).navigationBarsPadding())
    }
}