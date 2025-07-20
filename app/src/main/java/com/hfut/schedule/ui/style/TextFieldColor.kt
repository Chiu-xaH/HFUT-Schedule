package com.hfut.schedule.ui.style

import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.hfut.schedule.ui.component.container.cardNormalColor

@Composable
fun textFiledTransplant(isColorCopy : Boolean = true) : TextFieldColors {
    return TextFieldDefaults.colors(
        errorIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
//        focusedContainerColor = cardNormalColor(),
        unfocusedContainerColor = if(isColorCopy) cardNormalColor() else Color.Unspecified,
    )
}