package com.hfut.schedule.ui.style.align

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun RowHorizontal(modifier: Modifier = Modifier.fillMaxWidth(),content: @Composable () -> Unit) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        content() // 插入传入的布局内容
    }
}


@Composable
fun ColumnVertical(modifier: Modifier = Modifier,content: @Composable () -> Unit) {
    Column(modifier,horizontalAlignment = Alignment.CenterHorizontally) {
        content()
    }
}