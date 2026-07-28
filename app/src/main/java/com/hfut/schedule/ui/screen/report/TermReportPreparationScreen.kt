package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.status.CustomLineProgressIndicator
import com.xah.common.ui.style.APP_HORIZONTAL_DP

enum class ReportPreparationStatus {
    LOADING,
    COMPLETED,
    PARTIAL,
    FAILED,
    UNAVAILABLE,
    TIMED_OUT
}

data class ReportPreparationItem(
    val title: String,
    val status: ReportPreparationStatus,
    val details: List<ReportPreparationDetail>,
    val suggestion: String
)

data class ReportPreparationDetail(
    val title: String,
    val status: ReportPreparationStatus
)

@Composable
fun TermReportPreparationScreen(
    items: List<ReportPreparationItem>,
    canEnter: Boolean,
    isGraduating: Boolean,
    onRetry: () -> Unit,
    onEnter: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier
) {
    val finishedCount = items.count { it.status != ReportPreparationStatus.LOADING }
    val progress = if (items.isEmpty()) 0f else finishedCount.toFloat() / items.size
    val timedOutItems = items.filter { it.status == ReportPreparationStatus.TIMED_OUT }
    val issueItems = items.filter {
        it.status == ReportPreparationStatus.PARTIAL ||
        it.status == ReportPreparationStatus.FAILED ||
            it.status == ReportPreparationStatus.UNAVAILABLE
    }
    val hasIssue = timedOutItems.isNotEmpty() || issueItems.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.celebration),
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (isGraduating) "正在准备毕业报告" else "正在准备学期报告",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = when {
                timedOutItems.isNotEmpty() ->
                    "等待已超过 30 秒，仍可进入报告查看已经加载的内容"
                issueItems.isNotEmpty() && canEnter ->
                    "请求已经结束，部分板块的数据暂时不可用"
                canEnter -> "所有板块均已准备完成"
                else -> "正在加载各板块数据，请保持网络连接"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        CustomLineProgressIndicator(
            value = progress,
            text = "$finishedCount/${items.size}",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        items.forEach { item ->
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = { Text(item.title) },
                    leadingContent = {
                        when (item.status) {
                            ReportPreparationStatus.LOADING -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            ReportPreparationStatus.COMPLETED -> {
                                Icon(
                                    painterResource(R.drawable.check_circle),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            ReportPreparationStatus.TIMED_OUT -> {
                                Icon(
                                    painterResource(R.drawable.timer),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                            ReportPreparationStatus.PARTIAL -> {
                                Icon(
                                    painterResource(R.drawable.warning),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            else -> {
                                Icon(
                                    painterResource(R.drawable.warning),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                )

                if (item.status != ReportPreparationStatus.COMPLETED) {
                    PaddingHorizontalDivider()
                    item.details.forEach { detail ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 56.dp, end = 20.dp, top = 6.dp, bottom = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = detail.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            when (detail.status) {
                                ReportPreparationStatus.COMPLETED -> {
                                    Text(
                                        "√",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                ReportPreparationStatus.LOADING -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 1.5.dp
                                    )
                                }
                                else -> {
                                    Text(
                                        "×",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                    if (item.status != ReportPreparationStatus.LOADING) {
                        Text(
                            text = item.suggestion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(
                                start = 56.dp,
                                end = 20.dp,
                                top = 6.dp,
                                bottom = 16.dp
                            )
                        )
                    } else {
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasIssue) {
                TextButton(onClick = onRetry) {
                    Text("重新请求")
                }
                Spacer(Modifier.weight(1f))
            }
            Button(
                enabled = canEnter,
                onClick = onEnter,
                modifier = if (hasIssue) Modifier else Modifier.fillMaxWidth()
            ) {
                Text(if (hasIssue) "继续进入报告" else "进入报告")
            }
        }
        Spacer(Modifier.height(APP_HORIZONTAL_DP))
    }
}
