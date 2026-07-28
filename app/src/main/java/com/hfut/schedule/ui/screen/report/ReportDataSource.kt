package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xah.common.ui.component.text.BottomTip

@Composable
fun ReportDataSourceText(sources: Iterable<String>) {
    val sourceText = sources.filter(String::isNotBlank).distinct().joinToString("、")
    if (sourceText.isEmpty()) return

    Spacer(modifier = Modifier.height(8.dp))
    BottomTip("数据源：$sourceText")
}

@Composable
fun ReportDataSourceText(vararg sources: String) {
    ReportDataSourceText(sources.asIterable())
}
