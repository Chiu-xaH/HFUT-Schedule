package com.hfut.schedule.ui.component.status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.xah.uicommon.style.APP_HORIZONTAL_DP

@Composable
fun EmptyIcon(text: String = "结果为空") = StatusIcon(R.drawable.upcoming,text)
@Composable
fun DevelopingIcon() = StatusIcon(R.drawable.sdk,"正在开发")
@Composable
fun SuccessfulIcon() = StatusIcon(Icons.Filled.Check,"成功")
@Composable
fun ErrorIcon(string: String) = StatusIcon(Icons.Filled.Close,string)
@Composable
fun PrepareSearchIcon() = StatusIcon(R.drawable.search,"开始搜索")

@Composable
fun StatusIcon(iconId : Int, text : String, padding : Dp = APP_HORIZONTAL_DP/2) = BaseStatusUI(text = text,padding = padding) {
    Icon(
        painterResource(iconId),
        contentDescription = "",
        Modifier.size(100.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun StatusIcon(painter : ImageVector, text : String, padding : Dp = APP_HORIZONTAL_DP/2) = BaseStatusUI(text = text,padding = padding) {
    Icon(
        painter,
        contentDescription = "",
        Modifier.size(100.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun BaseStatusUI(text : String, padding : Dp = APP_HORIZONTAL_DP/2,icon : @Composable () -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            icon()
        }
        Spacer(Modifier.height(padding))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                textAlign = TextAlign.Center
            )
        }
    }
}
