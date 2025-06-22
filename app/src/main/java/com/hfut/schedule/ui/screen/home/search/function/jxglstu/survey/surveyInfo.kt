package com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.PostMode
import com.hfut.schedule.logic.model.jxglstu.PostSurvey
import com.hfut.schedule.logic.model.jxglstu.SurveyResponse
import com.hfut.schedule.logic.model.jxglstu.blankQuestionAnswer
import com.hfut.schedule.logic.model.jxglstu.radioQuestionAnswer
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.custom.CustomTextField
import com.hfut.schedule.ui.component.LargeButton
import com.hfut.schedule.ui.component.custom.LittleDialog
 
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.onListenStateHolder
import com.hfut.schedule.ui.style.RowHorizontal
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SurveyInfoUI(id : Int, vm: NetWorkViewModel,scope : CoroutineScope,onDismiss : () -> Unit) {
    val uiState by vm.surveyData.state.collectAsState()

    val refreshNetwork: suspend () -> Unit = {
        val cookie = if (!vm.webVpn) prefs.getString(
            "redirect",
            ""
        ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
        cookie?.let {
            vm.surveyData.clear()
            vm.getSurveyToken(it,id.toString())
            vm.getSurvey(it,id.toString())
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork, isFullScreen = false) {
        SurveyList(vm, scope) {
            onDismiss()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SurveyList(vm: NetWorkViewModel, scope: CoroutineScope,onResult : () -> Unit) {
    val uiState by vm.surveyData.state.collectAsState()
    val bean = (uiState as UiState.Success).data
    var postMode by remember { mutableStateOf(PostMode.NORMAL) }
    var showDialog by remember { mutableStateOf(false) }
    var showTextField by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("好") }

    if(showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                scope.launch {
                    async { selectMode(vm,postMode,bean,input) }.await()
                    launch {
                        showDialog = false
                        onResult()
                    }
                }
            },
            dialogTitle = "确定提交",
            dialogText = "实名制上网,理性填表,不可修改",
            conformText = "提交",
            dismissText = "返回"
        )
    }

    Column (modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP).navigationBarsPadding()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Icon(
                painterResource(R.drawable.arrow_upward),
                contentDescription = "",
                Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        RowHorizontal {
            Text("请选择发送(留言默认为:'好')", color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(APP_HORIZONTAL_DP))
        if(showTextField) {
            CustomTextField(
                input = input,
                trailingIcon = {
                    IconButton(
                        onClick = { showTextField = false }
                    ) {
                        Icon(Icons.Filled.Close,null)
                    }
                },
                label = {
                    Text("填写评价")
                },
                modifier = Modifier,
                singleLine = false
            ) { input = it }
        } else {
            LargeButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    showTextField = true
                },
                text = "自定义留言",
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                icon = R.drawable.edit,
            )
        }
        Spacer(Modifier.height(APP_HORIZONTAL_DP/2))
        RowHorizontal {
            LargeButton(
                modifier = Modifier.fillMaxWidth().weight(.5f),
                onClick = {
                    postMode = PostMode.GOOD
                    showDialog = true
                },
                text = "好评 100分",
                icon = R.drawable.thumb_up,
            )
            Spacer(Modifier.width(APP_HORIZONTAL_DP/2))
            LargeButton (
                modifier = Modifier.fillMaxWidth().weight(.5f),
                onClick = {
                    postMode = PostMode.BAD
                    showDialog = true
                },
                icon = R.drawable.thumb_down,
                text = "差评 0分",
                containerColor = MaterialTheme.colorScheme.error
            )
        }
        Spacer(Modifier.height(APP_HORIZONTAL_DP))
    }
}

@SuppressLint("SuspiciousIndentation")
suspend fun selectMode(vm : NetWorkViewModel, mode : PostMode,bean: SurveyResponse,comment: String = "好") = withContext(Dispatchers.IO) {

    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
//    val token = prefs.getString("SurveyCookie","")
    // 主线程监听 StateFlow
    onListenStateHolder(vm.surveyToken) { token ->
        when(mode) {
            PostMode.NORMAL -> {
                //vm.postSurvey("$cookie;$token", postResultNormal(vm))
                showToast("提交完成")
            }
            PostMode.GOOD ->  {
                vm.postSurvey("$cookie;$token", postResult(true, bean, comment))
                showToast("提交完成")
            }
            PostMode.BAD -> {
                vm.postSurvey("$cookie;$token", postResult(false,bean, comment))
                showToast("提交完成")
            }
        }
    }
//    withContext(Dispatchers.Main) {
//        // 只收集第一次流
//        val state = vm.surveyToken.state.first { it !is SimpleUiState.Loading }
//        when (state) {
//            is SimpleUiState.Success -> {
//                val token = state.data
//
//            }
//            is SimpleUiState.Error -> {
//                showToast("错误 " + state.exception?.message)
//            }
//            else -> {}
//        }
//    }
}

//true为好评，false为差评
fun postResult(goodMode: Boolean,bean : SurveyResponse,comment : String = "好"): JsonObject {
    val surveyAssoc = getSurveyAssoc(bean)
    val lessonSurveyTaskAssoc = prefs.getInt("teacherID", 0)
    val choiceList = getSurveyChoice(bean)
    val inputList = getSurveyInput(bean)
    val choiceNewList = mutableListOf<radioQuestionAnswer>()
    val inputNewList = mutableListOf<blankQuestionAnswer>()

    for (i in choiceList.indices) {
        val id = choiceList[i].id
        // 默认拿第一个选项为好评，拿最后一个为差评
        val option = if(goodMode) choiceList[i].options[0].name else choiceList[i].options.last().name
        choiceNewList.add(radioQuestionAnswer(id, option))
    }

    for (j in inputList.indices) {
        val id = inputList[j].id
        inputNewList.add(blankQuestionAnswer(id, comment))
    }

    // 组装数据
    val postSurvey = PostSurvey(surveyAssoc, lessonSurveyTaskAssoc, choiceNewList, inputNewList)

    return Gson().toJsonTree(postSurvey).asJsonObject
}
