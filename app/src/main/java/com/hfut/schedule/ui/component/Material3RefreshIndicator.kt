package com.hfut.schedule.ui.component

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// 适配M3取色的刷新指示器
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RefreshIndicator(
    refreshing: Boolean,
    state: PullRefreshState,
    modifier: Modifier = Modifier
) = PullRefreshIndicator(refreshing = refreshing, state =  state, modifier = modifier,backgroundColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.secondary,scale = true)