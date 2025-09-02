package com.hfut.schedule.ui.screen.home.search.function.my.webLab

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.WebURLEntity
import com.hfut.schedule.logic.database.entity.WebURLType
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.ShareTo
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CardBottomButton
import com.hfut.schedule.ui.component.container.CardBottomButtons
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.screen.AppNavRoute

import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@Composable
fun StorageWeb(hazeState : HazeState) {
    val types = WebURLType.entries
    val scope = rememberCoroutineScope()

    var showDialogDel by remember { mutableStateOf(false) }

    var selectedDelTitle by remember { mutableStateOf("") }
    var selectedDelId by remember { mutableIntStateOf(0) }

    val list by produceState(initialValue = emptyList<WebURLEntity>(), key1 = showDialogDel) {
        if(showDialogDel == false)
            value = DataBaseManager.webUrlDao.get().sortedBy { it.id }.reversed()
    }



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
    val context = LocalContext.current

    Column {
        if(list.isEmpty()) {
            CardListItem(
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
                    CustomCard(color = cardNormalColor()) {
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
                                        Starter.startWebView(context,url,name, icon = AppNavRoute.WebNavigation.icon)
                                    },
                                    onDoubleClick = {},
                                    onLongClick = {
                                        selectedDelTitle = name
                                        selectedDelId = id
                                        showDialogDel = true
                                    }
                                )
                            )
                            CardBottomButtons(
                                listOf(
                                    CardBottomButton(
                                        "来源: " + when(t) {
                                            WebURLType.ADDED -> "手动添加"
                                            WebURLType.SUPABASE -> "云端导入"
                                            WebURLType.COLLECTION -> "收藏"
                                        }
                                    ),
                                    CardBottomButton("复制") {
                                        ClipBoardUtils.copy(url)
                                    },
                                    CardBottomButton("分享") {
                                        ShareTo.shareString(url)
                                    },
                                    CardBottomButton("删除") {
                                        selectedDelTitle = name
                                        selectedDelId = id
                                        showDialogDel = true
                                    }
                                )
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(60.dp + APP_HORIZONTAL_DP))
        }
    }
}