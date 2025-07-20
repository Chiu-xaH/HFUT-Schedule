package com.hfut.schedule.ui.screen.home.search.function.my.webLab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.WebURLEntity
import com.hfut.schedule.logic.database.entity.WebURLType
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.ShareTo
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.webview.WebDialog
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@Composable
fun StorageWeb(hazeState : HazeState) {
    val types = WebURLType.entries
    val scope = rememberCoroutineScope()

    var showDialogWebView by remember { mutableStateOf(false) }
    var showDialogDel by remember { mutableStateOf(false) }

    var selectedDelTitle by remember { mutableStateOf("") }
    var selectedDelId by remember { mutableIntStateOf(0) }
    var selectedUrl by remember { mutableStateOf("") }

    val list by produceState(initialValue = emptyList<WebURLEntity>(), key1 = showDialogDel, key2 = showDialogWebView) {
        if(showDialogDel == false || showDialogWebView == false)
            value = DataBaseManager.webUrlDao.get().sortedBy { it.id }.reversed()
    }

    WebDialog(showDialogWebView,{ showDialogWebView = false }, selectedUrl,selectedDelTitle)


    if(showDialogDel) {
        LittleDialog(
            onDismissRequest = { showDialogDel = false },
            onConfirmation = {
                if(selectedDelId > 0) {
                    scope.launch {
                        DataBaseManager.webUrlDao.del(selectedDelId)
                        showDialogDel = false
                    }
                } else {
                    showToast("ID异常")
                    showDialogDel = false
                }
            },
            dialogText = "要删除 $selectedDelTitle 吗",
            hazeState = hazeState
        )
    }

    Column {
        if(list.isEmpty()) {
            StyleCardListItem(
                headlineContent = { Text("开始添加你的网页收藏夹") },
                supportingContent = {
                    Text("右下角手动添加 或 在打开网页时点击右侧星星按钮")
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.info),null)
                }
            )
        } else {
            for(item in list) {
                with(item) {
                    val t = types.find { type == it.name }!!
                    MyCustomCard(containerColor = cardNormalColor()) {
                        Column {

                            TransplantListItem(
                                headlineContent = { Text(name) },
                                overlineContent = {
                                    ScrollText(url)
                                },
                                leadingContent = {
                                    Icon(painterResource(
                                        when(t) {
                                            WebURLType.ADDED -> R.drawable.add_2
                                            WebURLType.SUPABASE -> R.drawable.arrow_downward
                                            WebURLType.COLLECTION -> R.drawable.star_filled
                                        }
                                    ),null)
                                },
                                modifier = Modifier.combinedClickable(
                                    onClick = {
                                        selectedDelTitle = name
                                        selectedUrl = url
                                        showDialogWebView = true
                                    },
                                    onDoubleClick = {},
                                    onLongClick = {
                                        selectedDelTitle = name
                                        selectedDelId = id
                                        showDialogDel = true
                                    }
                                )
                            )
                            HorizontalDivider()
                            Row(modifier = Modifier.align(Alignment.End)) {
                                Text(
                                    text = "来源: " + when(t) {
                                        WebURLType.ADDED -> "手动添加"
                                        WebURLType.SUPABASE -> "云端导入"
                                        WebURLType.COLLECTION -> "收藏"
                                    },
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    modifier = Modifier.align(Alignment.Bottom).padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP - 5.dp)
                                )
                                Text(
                                    text = "复制链接",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp,
                                    modifier = Modifier.align(Alignment.Bottom).padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP - 5.dp).clickable {
                                        ClipBoardUtils.copy(url)
                                    }
                                )
                                Text(
                                    text = "分享",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp,
                                    modifier = Modifier.align(Alignment.Top).padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP - 5.dp).clickable {
                                        ShareTo.shareString(url)
                                    }
                                )
                                Text(
                                    text = "删除",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp,
                                    modifier = Modifier.align(Alignment.Top).padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP - 5.dp).clickable {
                                        selectedDelTitle = name
                                        selectedDelId = id
                                        showDialogDel = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(60.dp + APP_HORIZONTAL_DP))
        }
    }
}