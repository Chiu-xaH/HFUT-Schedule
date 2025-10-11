package com.xah.uicommon.component.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.xah.uicommon.style.APP_HORIZONTAL_DP

@Composable
fun BottomTip(str : String?) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP), horizontalArrangement = Arrangement.Center) {
        str?.let { Text(text = it, color = Color.Gray, fontSize = 14.sp) }
    }
}
