package com.hfut.schedule.ui.utils.style

import androidx.compose.material3.CardColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable

@Composable
fun CardForListColor() : CardColors {
    return CardColors(containerColor = ListItemDefaults.containerColor, contentColor = ListItemDefaults.containerColor, disabledContainerColor = ListItemDefaults.containerColor, disabledContentColor = ListItemDefaults.containerColor)
}

