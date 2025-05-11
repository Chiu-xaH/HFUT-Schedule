package com.hfut.schedule.ui.screen.shower.bill

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.SimpleUiState
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.viewmodel.network.GuaGuaViewModel

@Composable
fun GuaguaBills(innerPadding: PaddingValues, vm: GuaGuaViewModel) {
    val uiState by vm.billsResult.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        vm.billsResult.clear()
        vm.getBills()
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val response = (uiState as SimpleUiState.Success).data
        val list = response.data
        LazyColumn {
            item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
            item { Spacer(modifier = Modifier.height(5.dp)) }
            items(list.size) {item ->
                val info = list[item].description
                AnimationCardListItem(
                    headlineContent = {
                        ScrollText(text =
                            if(info.contains("热水表: ")) info.substringAfter(": ")
                            else info
                        )
                    },
                    supportingContent = {
                        val t = if(info.contains("热水")) {
                            "-￥${list[item].xfMoney}"
                        } else if(info.contains("充值")) {
                            "+￥${list[item].dealMoney}"
                        } else {
                            "处理 ￥${list[item].dealMoney} 扣费 ￥${list[item].xfMoney}"
                        }
                        Text(text = t)
                    },
                    overlineContent = {
                        Text(text = list[item].dealDate +  " " + list[item].dealMark)
                    },
                    leadingContent = {
                        Icon(
                            painterResource(id =
                                if(info.contains("热水")) {
                                    R.drawable.bathtub
                                } else if(info.contains("充值")) {
                                    R.drawable.add_circle
                                } else {
                                    R.drawable.paid
                                }
                            ), contentDescription = "")
                    },
                    index = item
                )
            }
            item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
        }
    }
}
