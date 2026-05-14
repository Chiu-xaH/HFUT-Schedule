package com.hfut.schedule.ui.style.color

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
        focusedContainerColor = cardNormalColor(true),
        unfocusedContainerColor = if(isColorCopy) cardNormalColor(true) else Color.Unspecified,
    )
}


@Composable
fun textFiledAllTransplant() : TextFieldColors {
    return TextFieldDefaults.colors(
        errorIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
    )
}
