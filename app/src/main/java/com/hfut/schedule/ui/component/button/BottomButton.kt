package com.hfut.schedule.ui.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hfut.schedule.ui.component.dialog.FakeButton

@Composable
fun BottomButton(
    onClick : () -> Unit,
    text : String,
    enable : Boolean = true,
    textColor : Color = MaterialTheme.colorScheme.primary,
    containerColor : Color = MaterialTheme.colorScheme.secondaryContainer
) {
    Column {
        Box(Modifier.background(if(enable) containerColor else MaterialTheme.colorScheme.surfaceContainer)) {
            FakeButton(
                text = text,
                textColor = if (enable) textColor else Color.Gray,
                onClick = if (enable) onClick else {
                    {}
                },
                modifier = Modifier.fillMaxWidth(),
                contentColor = MaterialTheme.colorScheme.primary,
                containerColor = Color.Transparent
            )
        }
    }
}
