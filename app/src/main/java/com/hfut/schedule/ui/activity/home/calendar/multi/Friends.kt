package com.hfut.schedule.ui.activity.home.calendar.multi

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.utils.components.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.community.ApplyFriendResponse
import com.hfut.schedule.logic.beans.community.ApplyingLists
import com.hfut.schedule.logic.beans.community.ApplyingResponse
import com.hfut.schedule.logic.beans.community.FriendsList
import com.hfut.schedule.logic.beans.community.FriendsResopnse
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.utils.components.BottomTip
import com.hfut.schedule.ui.utils.components.DividerText
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.style.textFiledTransplant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
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
    val CommuityTOKEN = SharePrefs.prefs.getString("TOKEN","")
    CommuityTOKEN?.let { vm.GetCourse(it,studentId) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseUI(vm: NetWorkViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState, modifier = Modifier,
            shape = Round(sheetState)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("好友") },
                    )
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
    MyCustomCard {
        ListItem(
            headlineContent = { Text(text = "从文件导入") },
            supportingContent = {
                Text(text = "通过本应用分享课表会生成文本文件，他人接收文件并以本应用打开方式即可导入")
            },
            leadingContent = {
                Icon(painterResource(id = R.drawable.attach_file), contentDescription = "")
            },
            modifier = Modifier.clickable {
                MyToast("请在外部应用选择文件以本应用打开")
            }
        )
    }

    MyCustomCard {
        ListItem(
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
    }
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsSetting(vm : NetWorkViewModel) {
    var count = 0
    val CommuityTOKEN = SharePrefs.prefs.getString("TOKEN","")
    var loading by remember { mutableStateOf(true) }


    fun loadData() {
        CoroutineScope(Job()).launch {
            async {
                if(count == 0) {
                    CommuityTOKEN?.let { vm.openFriend(it) }
                    count++
                }
            }
            async { loading = true }.await()
            async {
                CommuityTOKEN?.let { vm.getApplying(it) } }.await()
            async {
                Handler(Looper.getMainLooper()).post {
                    vm.applyData.observeForever { result ->
                        if (result != null && result.contains("success")) {
                            loading = false
                        }
                    }
                }
            }
        }
    }
    // 初始加载
    LaunchedEffect(Unit) {
        loadData()
    }
    val friendList = getFriendsList()
    var input by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    DividerTextExpandedWith(text = "申请新的好友") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 15.dp),
                value = input,
                onValueChange = {
                    input = it
                },
                label = { Text("输入学号" ) },
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            CoroutineScope(Job()).launch {
                                async { CommuityTOKEN?.let { vm.addApply(it,input) } }.await()
                                async {
                                    Handler(Looper.getMainLooper()).post {
                                        vm.applyResponseMsg.observeForever { result ->
                                            if (result != null && result.contains("success")) {
                                                msg = getMsg(result)
                                            }
                                        }
                                    }
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
        if(msg != "")
            BottomTip(str = msg)
    }
    DividerTextExpandedWith(text = "好友列表(您目前可以查看的课表)") {
        MyCustomCard {
            for(i in friendList.indices) {
                ListItem(
                    headlineContent = { friendList[i]?.let { Text(text = it.realname) } },
                    leadingContent = { Icon(painterResource(id = R.drawable.person), contentDescription = "")},
                    overlineContent = { friendList[i]?.let { Text(text = it.userId) }},
                    trailingContent = {
                        FilledTonalIconButton(onClick = {
                            MyToast("正在开发")
                        }) {
                            Icon(painterResource(id = R.drawable.delete), contentDescription = "")
                        }
                    }
                )
            }
        }
    }

    DividerTextExpandedWith(text = "申请列表(同意后对方可查看你的课表)") {
        Box {
            AnimatedVisibility(
                visible = loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    LoadingUI()
                }
            }


            AnimatedVisibility(
                visible = !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MyCustomCard {
                    val applyList = getApplyingList(vm)
                    for(i in applyList.indices) {
                        ListItem(
                            headlineContent = { applyList[i]?.let { ScrollText(text = it.applyUsername) } },
                            leadingContent = { Icon(painterResource(id = R.drawable.person_add), contentDescription = "")},
                            overlineContent = { applyList[i]?.let { Text(text = it.applyUserId) } },
                            trailingContent = {
                                Row(modifier = Modifier.padding(horizontal = 15.dp)) {
                                    FilledTonalButton(onClick = {
                                        val id = applyList[i]?.id
                                        id?.let { CommuityTOKEN?.let { it1 -> vm.checkApplying(it1, it,true) } }
                                        loading = true
                                        loadData()
                                    }) {
                                        Text(text = "同意")
                                    }
                                    Spacer(modifier = Modifier.width(15.dp))
                                    FilledTonalButton(onClick = {
                                        val id = applyList[i]?.id
                                        CommuityTOKEN?.let { id?.let { it1 -> vm.checkApplying(it, it1,false) } }
                                        loading = true
                                        loadData()
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
}

fun getMsg(json : String) : String {
    return try {
        val data = Gson().fromJson(json,ApplyFriendResponse::class.java)
        data.message
    } catch (_:Exception) {
        "解析出错"
    }
}

fun getApplyingList(vm: NetWorkViewModel) : List<ApplyingLists?> {
    return try {
        val json = vm.applyData.value
        Gson().fromJson(json,ApplyingResponse::class.java).result.records
    } catch (e:Exception) {
        emptyList()
    }
}