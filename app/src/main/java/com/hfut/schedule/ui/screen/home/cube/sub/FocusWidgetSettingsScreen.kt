package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.receiver.widget.focus.FocusWidget
import com.hfut.schedule.receiver.widget.focus.hasFocusWidget
import com.hfut.schedule.receiver.widget.focus.refreshFocusWidget
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.home.cube.screen.WidgetPreview
import com.xah.transition.util.TransitionBackHandler
import com.xah.uicommon.component.slider.CustomSlider
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.RowHorizontal
import com.xah.uicommon.style.padding.InnerPaddingHeight
import kotlinx.coroutines.launch

@Composable
fun FocusWidgetSettingsScreen(
   innerPadding : PaddingValues,
   navController : NavHostController
) {
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionBackHandler(navController,enablePredictive) {
        scale = it
    }
    FocusWidgetSettingsUI(
        innerPadding,
        modifier = Modifier.scale(scale)
    )
}

@Composable
fun FocusWidgetSettingsUI(
    innerPadding : PaddingValues,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val focusWidgetCount by produceState(initialValue = 0) {
        value = hasFocusWidget(context)
    }
    val scope = rememberCoroutineScope()
    val textSize by DataStoreManager.focusWidgetTextSize.collectAsState(initial = 1f)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column (modifier = Modifier
            .verticalScroll(rememberScrollState())
        ) {

            InnerPaddingHeight(innerPadding,true)

            DividerTextExpandedWith("预览") {
                CustomCard(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Spacer(Modifier.height(APP_HORIZONTAL_DP))

                    RowHorizontal {
                        WidgetPreview(R.drawable.focus_widget_preview)
                    }
                    Spacer(Modifier.height(APP_HORIZONTAL_DP))

                }
            }

            DividerTextExpandedWith("配置") {
                CustomCard(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        headlineContent = {
                            Text("内容文字大小 ${formatDecimal(textSize.toDouble()*100,0)}%")
                        },
                        supportingContent = { Text("调整方格的文字大小(默认为100%)") },
                        leadingContent = {
                            Icon(painterResource(R.drawable.translate),null)
                        },
                    )

                    CustomSlider(
                        value = textSize,
                        onValueChange = {
                            scope.launch { DataStoreManager.saveFocusWidgetTextSize(it) }
                        },
                        onValueChangeFinished = {
                            scope.launch {
                                refreshFocusWidget(context)
                                showToast("重启App生效")
                            }
                        },
                        modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
                        valueRange = 0.25f..2f,
                        showProcessText = true,
                        processText = formatDecimal(textSize.toDouble()*100,0)
                    )
                }
            }
            DividerTextExpandedWith("状态") {
                CustomCard(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    if(focusWidgetCount == 0) {
                        TransplantListItem(
                            headlineContent = { Text("当前未添加组件") },
                            leadingContent = {
                                Icon(painterResource(R.drawable.upcoming),null)
                            },
                        )
                    } else {
                        TransplantListItem(
                            headlineContent = { Text("全部刷新") },
                            modifier = Modifier.clickable {
                                scope.launch {
                                    refreshFocusWidget(context)
                                    showToast("刷新完成")
                                }
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.rotate_right),null)
                            },
                            supportingContent = { Text("当前有 $focusWidgetCount 个组件；由于Android的底层机制，小组件需尽可能地节省电量，故显示的是静态内容，刷新相当于重新绘制，App在添加组件时已默认将每30分钟自动刷新的任务发送至系统队列中") },
                        )
                    }
                }
            }

            InnerPaddingHeight(innerPadding,false)
        }
    }
}