package com.hfut.schedule.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.community.courseFailRateDTOList
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.util.language.res
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

data class FailRateDetailDestination(
    val courseName : String,
    val lessonId : String,
    val bean : List<courseFailRateDTOList>
) : NavDestination() {
    override val key = "fail_rate_detail_${courseName}_${lessonId}"
    override val title = res(R.string.navigation_label_fail_rate)
    override val icon = R.drawable.group

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
        val hazeState = rememberHazeState(blurEnabled = blur)

        Scaffold(
            modifier = Modifier.Companion.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.Companion.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    title = { Text("${title.asString()}: $courseName") },
                    colors = topBarTransplantColor(),
                    navigationIcon = {
                        TopBarNavigationIcon()
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .hazeSource(hazeState)
            ) {
                val detailList = bean
                LazyColumn {
                    item { InnerPaddingHeight(innerPadding,true) }
                    items(detailList.size,key = { it }){ item ->
                        val dataItem = detailList[item]
                        val rate = (1 - dataItem.successRate) * 100
                        CardListItem(
                            headlineContent = {  Text("平均分 ${dataItem.avgScore}") },
                            supportingContent = { Text("人数: 挂科 ${dataItem.failCount} | 总 ${dataItem.totalCount}") },
                            overlineContent = { Text(text = "${dataItem.xn}年 第${dataItem.xq}学期")},
                            leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                            trailingContent = { Text("挂科率 ${String.format("%.2f", rate)} %") },
                        )
                    }
                    item { InnerPaddingHeight(innerPadding,true) }
                    item { Spacer(Modifier.height(APP_HORIZONTAL_DP).navigationBarsPadding()) }
                }
            }
        }
    }
}