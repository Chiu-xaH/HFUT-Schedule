package com.hfut.schedule.ui.screen.home.calendar.multi

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.community.FriendsList
import com.hfut.schedule.logic.model.community.FriendsResopnse
import com.hfut.schedule.logic.util.network.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.BottomTip
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

fun getFriendsList() : List<FriendsList?> {
    return try {
        val json = prefs.getString("feiends","")

        val data = Gson().fromJson(json,FriendsResopnse::class.java)
        if(data.success) {
            data.result
        } else {
            emptyList()
        }
    } catch(_ : Exception) {
        emptyList()
    }
}

fun getFriendsCourse(studentId : String,vm : NetWorkViewModel) {
    val CommuityTOKEN = SharedPrefs.prefs.getString("TOKEN","")
    CommuityTOKEN?.let { vm.getCoursesFromCommunity(it,studentId) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseUI(vm: NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("好友")
                }
            ) {innerPadding->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    FriendsSetting(vm)
                }
            }
        }
    }
//    MyCustomCard {
        StyleCardListItem(
            headlineContent = { Text(text = "从文件导入") },
            supportingContent = {
                Text(text = "通过本应用分享课表会生成文本文件，他人接收文件并以本应用打开方式即可导入")
            },
            leadingContent = {
                Icon(painterResource(id = R.drawable.attach_file), contentDescription = "")
            },
            modifier = Modifier.clickable {
                showToast("请在外部应用选择文件以本应用打开")
            }
        )
//    }

//    MyCustomCard {
        StyleCardListItem(
            headlineContent = { Text(text = "向他人申请好友课表") },
            supportingContent = {
                Text(text = "输入学号,发送申请,对方同意后即可查看对方课表")
            },
            leadingContent = {
                Icon(painterResource(id = R.drawable.group), contentDescription = "")
            },
            modifier = Modifier.clickable {
                showBottomSheet = true
            }
        )
//    }
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsSetting(vm : NetWorkViewModel) {
    val scope = rememberCoroutineScope()
    val token = remember { prefs.getString("TOKEN","") }
    val refreshNetwork : suspend () -> Unit = {
        token?.let {
            vm.openFriend(it)
            vm.applyFriendsResponse.clear()
            vm.getApplying(it)
        }
    }
    // 初始加载
    LaunchedEffect(Unit) {
        vm.addFriendApplyResponse.emitPrepare()
        refreshNetwork()
    }
    val uiState by vm.applyFriendsResponse.state.collectAsState()
    val uiStateAdd by vm.addFriendApplyResponse.state.collectAsState()
    val friendList = remember { getFriendsList() }
    var input by remember { mutableStateOf("") }
    DividerTextExpandedWith(text = "申请新的好友") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = appHorizontalDp()),
                value = input,
                onValueChange = {
                    input = it
                },
                label = { Text("输入学号" ) },
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                token?.let {
                                    vm.addFriendApplyResponse.clear()
                                    vm.addFriendApply(it,input)
                                }
                            }
                        }) {
                        Icon(painter = painterResource(R.drawable.person_add), contentDescription = "description")
                    }
                },
                shape = MaterialTheme.shapes.medium,
                colors = textFiledTransplant(),
            )
        }
        CommonNetworkScreen(uiStateAdd, isFullScreen = false, onReload = { showToast("禁止刷新") }) {
            val msg = (uiStateAdd as UiState.Success).data
            BottomTip(str = msg)
        }
    }
    DividerTextExpandedWith(text = "好友列表(您目前可以查看的课表)") {
//        MyCustomCard {
            for(i in friendList.indices) {
                StyleCardListItem(
                    headlineContent = { friendList[i]?.let { Text(text = it.realname) } },
                    leadingContent = { Icon(painterResource(id = R.drawable.person), contentDescription = "")},
                    overlineContent = { friendList[i]?.let { Text(text = it.userId) }},
                    trailingContent = {
                        FilledTonalIconButton(onClick = {
                            showToast("正在开发")
                        }) {
                            Icon(painterResource(id = R.drawable.delete), contentDescription = "")
                        }
                    }
                )
            }
//        }
    }

    DividerTextExpandedWith(text = "申请列表(同意后对方可查看你的课表)") {
        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
            val applyList = (uiState as UiState.Success).data
            Column {
                for(i in applyList.indices) {
                    StyleCardListItem(
                        headlineContent = { applyList[i]?.let { ScrollText(text = it.applyUsername) } },
                        leadingContent = { Icon(painterResource(id = R.drawable.person_add), contentDescription = "")},
                        overlineContent = { applyList[i]?.let { Text(text = it.applyUserId) } },
                        trailingContent = {
                            Row(modifier = Modifier.padding(horizontal = appHorizontalDp())) {
                                FilledTonalButton(onClick = {
                                    val id = applyList[i]?.id
                                    id?.let { token?.let { it1 ->
                                        vm.checkApplying(it1, it,true)
                                        scope.launch { refreshNetwork() }
                                    } }
                                }) {
                                    Text(text = "同意")
                                }
                                Spacer(modifier = Modifier.width(appHorizontalDp()))
                                FilledTonalButton(onClick = {
                                    val id = applyList[i]?.id
                                    token?.let { id?.let { it1 ->
                                        vm.checkApplying(it, it1,false)
                                        scope.launch { refreshNetwork() }
                                    } }
                                }) {
                                    Text(text = "拒绝")
                                }
                            }

                        }
                    )
                }
            }
        }
    }
}