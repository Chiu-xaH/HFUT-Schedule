package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.xah.common.ui.component.status.LoadingUI
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.chart.PieChart
import com.xah.common.ui.component.chart.PieChartData
import com.xah.common.ui.style.APP_HORIZONTAL_DP

@Composable
fun LibraryReportSection(vm: NetWorkViewModel, periodLabel: String = "本学期") {
    val libraryStatus by vm.libraryStatusResp.state.collectAsState()
    val borrowedBooks by vm.libraryBorrowedResp.state.collectAsState()

    LaunchedEffect(Unit) {
        val token = prefs.getString(SharedPrefs.LIBRARY_TOKEN, "") ?: ""
        if (token.isNotEmpty() && libraryStatus !is UiState.Success) {
            vm.libraryStatusResp.clear()
            vm.getLibraryStatus(token)
        }
    }

    DividerTextExpandedWith("图书馆报表") {
        val token = prefs.getString(SharedPrefs.LIBRARY_TOKEN, "") ?: ""
        if (token.isEmpty()) {
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = { Text("未登录图书馆") },
                    supportingContent = { Text("请先通过CAS登录图书馆后再查看") }
                )
            }
            return@DividerTextExpandedWith
        }

        when (libraryStatus) {
            is UiState.Success -> {
                val status = (libraryStatus as UiState.Success).data

                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text("图书馆概览") },
                        headlineContent = {
                            Text("${periodLabel}借阅", style = MaterialTheme.typography.titleMedium)
                        }
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            TransplantListItem(
                                overlineContent = { Text("借阅次数", style = MaterialTheme.typography.bodySmall) },
                                headlineContent = {
                                    Text("${status.borrowCount}", style = MaterialTheme.typography.headlineSmall)
                                },
                                usePadding = false,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            TransplantListItem(
                                overlineContent = { Text("书架藏书", style = MaterialTheme.typography.bodySmall) },
                                headlineContent = {
                                    Text("${status.bookShelfCount}", style = MaterialTheme.typography.headlineSmall)
                                },
                                usePadding = false,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            TransplantListItem(
                                overlineContent = { Text("预约次数", style = MaterialTheme.typography.bodySmall) },
                                headlineContent = {
                                    Text("${status.reserveCount}", style = MaterialTheme.typography.headlineSmall)
                                },
                                usePadding = false,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        
                        if (status.borrowCount > 0 || status.reserveCount > 0 || status.entrustCount > 0) {
                            Box(modifier = Modifier.weight(1.5f)) {
                                PieChart(
                                    data = listOf(
                                        PieChartData("借阅", status.borrowCount.toFloat()),
                                        PieChartData("预约", status.reserveCount.toFloat()),
                                        PieChartData("委托", status.entrustCount.toFloat())
                                    ).filter { it.value > 0 },
                                    modifier = Modifier.fillMaxWidth().height(160.dp),
                                    pieModifier = Modifier.size(120.dp),
                                    title = "借阅统计"
                                )
                            }
                        }
                    }
                }

                when (borrowedBooks) {
                    is UiState.Success -> {
                        val books = (borrowedBooks as UiState.Success).data
                        if (books.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                            CustomCard(color = cardNormalColor()) {
                                TransplantListItem(
                                    overlineContent = { Text("当前借阅") },
                                    headlineContent = {
                                        Text("共 ${books.size} 本", style = MaterialTheme.typography.titleMedium)
                                    }
                                )
                                books.take(5).forEach { book ->
                                    TransplantListItem(
                                        headlineContent = { Text(book.libraryDetail.detail.title) },
                                        supportingContent = { Text(book.libraryDetail.detail.authors) }
                                    )
                                }
                                if (books.size > 5) {
                                    TransplantListItem(
                                        headlineContent = {
                                            Text("...还有 ${books.size - 5} 本", style = MaterialTheme.typography.bodySmall)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
            is UiState.Error -> {
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        headlineContent = { Text("加载失败") },
                        supportingContent = { Text("图书馆服务暂时不可用") }
                    )
                }
            }
            else -> {
                CustomCard(color = cardNormalColor()) {
                    LoadingUI()
                }
            }
        }
    }
}
