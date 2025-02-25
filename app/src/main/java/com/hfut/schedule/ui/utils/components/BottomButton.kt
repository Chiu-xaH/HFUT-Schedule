package com.hfut.schedule.ui.utils.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomButton(onClick : () -> Unit,text : String,color : Color = MaterialTheme.colorScheme.secondaryContainer) {
    Column {
//        Divider()
        Box(Modifier.background(color)) {
            FilledTonalButton(
                onClick = onClick,
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(text)
            }
        }
    }
}
