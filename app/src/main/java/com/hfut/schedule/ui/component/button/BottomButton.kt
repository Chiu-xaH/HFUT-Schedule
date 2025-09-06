package com.hfut.schedule.ui.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomButton(
    onClick : () -> Unit,
    text : String,
    enable : Boolean = true,
    color : Color = if(enable) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainer
) {
    Column {
        Box(Modifier.background(color)) {
            FilledTonalButton(
                onClick = if(enable) onClick else {{}},
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(text,color = if(enable) MaterialTheme.colorScheme.primary else Color.Gray)
            }
        }
    }
}
