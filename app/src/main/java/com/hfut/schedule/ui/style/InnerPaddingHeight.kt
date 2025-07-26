package com.hfut.schedule.ui.style

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun InnerPaddingHeight(innerPadding : PaddingValues,isTopOrBottom : Boolean) =
    Spacer(Modifier.height(
        if(isTopOrBottom) {
            innerPadding.calculateTopPadding()
        } else {
            innerPadding.calculateBottomPadding()
        }
    ))