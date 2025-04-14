package com.hfut.schedule.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hfut.schedule.ui.style.textFiledTransplant

@Composable
fun CustomTextField(
    input : String = "",
    label : @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    onValueChange : (String) -> Unit
) {
    Row {
        TextField(
            modifier = modifier
                .weight(1f)
                .padding(horizontal = appHorizontalDp()),
            value = input,
            onValueChange = onValueChange,
            label = label,
            trailingIcon = trailingIcon,
            leadingIcon = leadingIcon,
            supportingText = supportingText,
            isError = isError,
            singleLine = singleLine,
            enabled = enabled,
            shape = MaterialTheme.shapes.medium,
            colors = textFiledTransplant(),
        )
    }
}