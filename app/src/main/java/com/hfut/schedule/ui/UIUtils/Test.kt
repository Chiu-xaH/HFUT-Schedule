package com.hfut.schedule.ui.UIUtils

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class Item(var item: String, var isNew: Boolean)
//在这里放测试布局
@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun Tests() {
   // val list = remember { List(4){ "Item $it" }.toMutableStateList() }
    var refreshing by remember { mutableStateOf(false) }
    // 用协程模拟一个耗时加载
    val scope = rememberCoroutineScope()
    val state = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            refreshing = true
            delay(1000) // 模拟数据加载
            //list+="Item ${list.size+1}"
            refreshing = false
        }
    })
    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(state)){
        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            //...
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),

            // 固定两列
            columns = GridCells.Fixed(2),
            content = {
                items(2) {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 4.dp,
                                vertical = 5.dp
                            ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        ListItem(
                            headlineContent = { Text(text = "余额 ￥110.50") },
                            // modifier = Modifier.width(185.dp),
                            supportingContent = { Text(text = "待圈存 ￥$120.50") },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.account_balance_wallet),
                                    contentDescription = "Localized description",
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        )
                    }
                }
            }
        )
        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
    }
}

