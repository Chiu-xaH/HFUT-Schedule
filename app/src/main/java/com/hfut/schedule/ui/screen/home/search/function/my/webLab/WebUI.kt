package com.hfut.schedule.ui.screen.home.search.function.my.webLab

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.WebURLType
import com.hfut.schedule.logic.database.entity.WebUrlDTO
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.screen.news.department.SchoolsUI
import com.hfut.schedule.ui.component.text.BottomTip
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.RowHorizontal
import com.hfut.schedule.ui.util.AppAnimationManager
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebUI(hazeState: HazeState) {
    var showBottomSheet_Web by remember { mutableStateOf(false) }
    var showBottomSheet_Add by remember { mutableStateOf(false) }


    TransplantListItem(
        headlineContent = { Text(text = "网址导航") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.explore),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable { showBottomSheet_Web = true }
    )
    if(showBottomSheet_Add) {
        val scope = rememberCoroutineScope()
        var inputName by remember { mutableStateOf("") }
        var inputUrl by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showBottomSheet_Add = false}) {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)) {
                Column(modifier = Modifier.padding(vertical = APP_HORIZONTAL_DP)) {
                    CustomTextField(
                        input = inputName,
                        label = { Text("名称") },
                        singleLine = false
                    ) { inputName = it }
                    Spacer(Modifier.height(APP_HORIZONTAL_DP))

                    CustomTextField(
                        input = inputUrl,
                        label = { Text("链接") },
                        singleLine = false,
                        supportingText = { Text("请输入http://或https://开头的完整链接")}
                    ) { inputUrl = it }
                    Spacer(Modifier.height(APP_HORIZONTAL_DP/2))

                    RowHorizontal(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                        FilledTonalButton (
                            onClick = {
                                showBottomSheet_Add = false
                            },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth().weight(.5f)
                        ) {
                            Text("取消")
                        }
                        Spacer(Modifier.width(APP_HORIZONTAL_DP/2))

                        Button(
                            onClick = {
                                scope.launch {
                                    if(!isWebUrlValid(inputUrl)) {
                                        showToast("不合理链接")
                                        return@launch
                                    }
                                    if(inputName.isEmpty() || inputName.isBlank()) {
                                        showToast("空名")
                                        return@launch
                                    }
                                    DataBaseManager.webUrlDao.insert(WebUrlDTO(
                                        name = inputName,
                                        url = inputUrl,
                                        type = WebURLType.ADDED
                                    ).toEntity())
                                    showToast("添加完成")
                                    showBottomSheet_Add = false
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth().weight(.5f)
                        ) {
                            Text("保存")
                        }
                    }
                }
            }
        }
    }



    if (showBottomSheet_Web) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Web = false },
            showBottomSheet = showBottomSheet_Web,
            hazeState = hazeState,
        ) {
            val state = rememberScrollState()
            val scroll by remember { derivedStateOf { !state.isScrollInProgress } }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("网址导航") {
                        Schools(hazeState)
                    }
                },
                floatingActionButton = {
                    AnimatedVisibility(
                        visible = scroll,
                        enter = AppAnimationManager.centerFadeAnimation.enter,
                        exit = AppAnimationManager.centerFadeAnimation.exit
                    ) {
                        FloatingActionButton(
                            onClick = { showBottomSheet_Add = true }
                        ) {
                            Icon(painterResource(R.drawable.add_2),null)
                        }
                    }
                }
            ) { innerPadding ->

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(state)
                        .fillMaxSize()
                ) {
                    DividerTextExpandedWith(text = "固定项") {
                        WebItem()
                    }

                    DividerTextExpandedWith(text = "实验室") {
                        LabUI()
                    }
                    DividerTextExpandedWith(text = "本地收藏夹(点击刷新)") {
                        StorageWeb(hazeState)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Schools(hazeState: HazeState) {
    var showBottomSheet_School by remember { mutableStateOf(false) }

    if (showBottomSheet_School) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_School = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_School
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("学院")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    SchoolsUI(null)
                }
            }
        }
    }

    FilledTonalButton(
        onClick = { showBottomSheet_School = true }
    ) {
        Text(text = "学院集锦")
    }
}

fun isWebUrlValid(url: String): Boolean {
    val checkUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
        "http://$url"
    } else url
    return Patterns.WEB_URL.matcher(checkUrl).matches()
}
