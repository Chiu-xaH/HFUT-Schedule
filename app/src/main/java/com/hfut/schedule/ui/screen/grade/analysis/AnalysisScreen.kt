package com.hfut.schedule.ui.screen.grade.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.style.align.CenterScreen


@Composable
fun AnalysisScreen(vm : NetWorkViewModel, innerPadding : PaddingValues) {
    CenterScreen {
        DevelopingIcon()
    }
//    val scrollState = rememberScrollState()
//    Column(modifier = Modifier.verticalScroll(scrollState)) {
//        InnerPaddingHeight(innerPadding,true)
//        repeat (100){
//            CardListItem(
//                headlineContent = { Text("测") }
//            )
//        }
//        DividerTextExpandedWith("平均分数与绩点") { }
//        InnerPaddingHeight(innerPadding,false)
//    }
}