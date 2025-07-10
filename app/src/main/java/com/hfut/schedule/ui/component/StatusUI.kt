package com.hfut.schedule.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R

@Composable
fun EmptyUI(text: String = "结果为空") = StatusUI(R.drawable.upcoming,text)
@Composable
fun DevelopingUI() = StatusUI(R.drawable.sdk,"正在开发")
@Composable
fun SuccessUI() = StatusUI2(Icons.Filled.Check,"成功")
@Composable
fun ErrorUI(string: String) = StatusUI2(Icons.Filled.Close,string)
@Composable
fun PrepareSearchUI() = StatusUI(R.drawable.search,"开始搜索")
@Composable
fun CenterScreen(content : @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.align(Alignment.Center)) {
            content()
        }
    }
}

@Composable
fun StatusUI(iconId : Int, text : String, padding : Dp = APP_HORIZONTAL_DP) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = "",
                Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(padding))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(
                horizontal = APP_HORIZONTAL_DP
            ))
        }
    }
}

@Composable
fun StatusUI2(painter : ImageVector, text : String, padding : Dp = APP_HORIZONTAL_DP) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Icon(
                 painter,
                contentDescription = "",
                Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
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
