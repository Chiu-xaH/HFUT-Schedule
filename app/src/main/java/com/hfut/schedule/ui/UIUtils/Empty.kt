package com.hfut.schedule.ui.UIUtils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R

@Composable
fun EmptyUI() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
        Icon(painter = painterResource(id = R.drawable.manga), contentDescription = "",Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
        Text(text = "结果为空", color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun DevelopingUI() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
        Icon(painter = painterResource(id = R.drawable.sdk), contentDescription = "",Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
        Text(text = "正在开发", color = MaterialTheme.colorScheme.primary)
    }
}