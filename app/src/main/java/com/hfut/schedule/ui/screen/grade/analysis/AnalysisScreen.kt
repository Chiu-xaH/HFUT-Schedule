package com.hfut.schedule.ui.screen.grade.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.viewmodel.network.NetWorkViewModel


@Composable
fun AnalysisScreen(vm : NetWorkViewModel, innerPadding : PaddingValues) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        InnerPaddingHeight(innerPadding,true)
        DividerTextExpandedWith("平均分数与绩点") { }
        InnerPaddingHeight(innerPadding,false)
    }
}