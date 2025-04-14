package com.hfut.schedule.ui.style

import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.hfut.schedule.ui.component.cardNormalColor

@Composable
fun textFiledTransplant() : TextFieldColors {
    return TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
//        focusedContainerColor = cardNormalColor(),
        unfocusedContainerColor = cardNormalColor(),
    )
}