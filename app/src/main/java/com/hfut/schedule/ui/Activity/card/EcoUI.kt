package com.hfut.schedule.ui.Activity.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.ModifierLocalConsumer
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.UIUtils.MyToast

@Composable
fun EcoUI(innerPadding : PaddingValues) {
    Column {
        Spacer(modifier = Modifier.padding(innerPadding.calculateTopPadding()))
        Box {
            Text(text = "肥工教务通致力于帮助减少碳足迹,环保消费\n(本页面正在开发,敬请期待)", modifier = Modifier.align(Alignment.Center).padding(horizontal = 15.dp))
        }
        Spacer(modifier = Modifier.padding(innerPadding.calculateBottomPadding()))
    }
}