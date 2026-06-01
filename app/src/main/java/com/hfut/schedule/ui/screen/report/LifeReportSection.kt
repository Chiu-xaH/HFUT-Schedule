package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

@Composable
fun LifeReportSection(vm: NetWorkViewModel, semester: Int) {
    val dormitoryInfo by vm.dormitoryFromCommunityResp.state.collectAsState()
    val dormitoryUsers by vm.dormitoryInfoFromCommunityResp.state.collectAsState()
    val dormitoryScore by vm.dormitoryScoreResp.state.collectAsState()

    val termInfo = remember(semester) { parseSemesterInt(semester) }

    LaunchedEffect(Unit) {
        try {
            val token = prefs.getString("TOKEN", "") ?: ""
            if (token.isNotEmpty()) {
                vm.dormitoryFromCommunityResp.clear()
                vm.getDormitory(token)
                vm.dormitoryInfoFromCommunityResp.clear()
                vm.getDormitoryInfo(token)
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(semester) {
        try {
            val token = prefs.getString("TOKEN", "") ?: ""
            if (token.isEmpty()) return@LaunchedEffect
            val semStr = termInfo?.dormitoryName ?: return@LaunchedEffect
            vm.dormitoryScoreResp.clear()
            vm.getDormitoryScore(token, null, semStr)
        } catch (_: Exception) {}
    }

    DividerTextExpandedWith("生活报表", false) {
        when (dormitoryInfo) {
            is UiState.Success -> {
                val info = (dormitoryInfo as UiState.Success).data
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text("宿舍信息") },
                        headlineContent = { Text("${info.campus} ${info.dormitory}", style = MaterialTheme.typography.titleMedium) }
                    )
                    TransplantListItem(
                        overlineContent = { Text("房间号") },
                        headlineContent = { Text(info.room, style = MaterialTheme.typography.headlineMedium) }
                    )
                }
            }
            is UiState.Error -> {
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(headlineContent = { Text("暂无宿舍信息") }, supportingContent = { Text("请先在宿舍页面加载数据") })
                }
            }
            else -> {
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(headlineContent = { Text("加载宿舍信息中...") })
                }
            }
        }

        when (dormitoryUsers) {
            is UiState.Success -> {
                val users = (dormitoryUsers as UiState.Success).data
                if (users.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = { Text("室友") },
                            headlineContent = { Text("${users.size}人", style = MaterialTheme.typography.titleMedium) }
                        )
                        users.forEach { user ->
                            TransplantListItem(headlineContent = { Text(user.realname) }, supportingContent = { Text(user.username) })
                        }
                    }
                }
            }
            else -> {}
        }

        when (dormitoryScore) {
            is UiState.Success -> {
                val scores = (dormitoryScore as UiState.Success).data
                if (scores.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = { Text("${termInfo?.displayName ?: ""} 卫生评分") },
                            headlineContent = { Text("卫生评分", style = MaterialTheme.typography.titleMedium) }
                        )
                        scores.forEach { score ->
                            TransplantListItem(
                                headlineContent = { Text(score.title) },
                                trailingContent = { Text(score.value, style = MaterialTheme.typography.titleMedium) }
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(headlineContent = { Text("暂无卫生评分数据") })
                    }
                }
            }
            is UiState.Error -> {
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(headlineContent = { Text("卫生评分加载失败") }, supportingContent = { Text("请确认已通过CAS登录社区") })
                }
            }
            else -> {}
        }
    }
}
