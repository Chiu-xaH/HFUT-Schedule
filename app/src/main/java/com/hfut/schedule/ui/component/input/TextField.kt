package com.hfut.schedule.ui.component.input

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.style.color.textFiledTransplant

@Composable
fun CustomTextField(
    input : String ,
    label : @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    singleLine: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
    modifier: Modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
    onValueChange : (String) -> Unit
) {
    Row {
        TextField(
            modifier = modifier
                .weight(1f),
            value = input,
            onValueChange = onValueChange,
            label = label,
            trailingIcon = trailingIcon,
            leadingIcon = leadingIcon,
            supportingText = supportingText,
            isError = isError,
            singleLine = singleLine,
            enabled = enabled,
            shape = shape,
            colors = textFiledTransplant(),
        )
    }
}