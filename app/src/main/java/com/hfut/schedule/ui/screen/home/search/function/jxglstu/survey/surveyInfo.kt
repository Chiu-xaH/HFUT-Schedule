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
import com.hfut.schedule.logic.network.util.StatusCode
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.dialog.LittleDialog
 
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.xah.uicommon.style.align.RowHorizontal
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SurveyInfoUI(teacherId : Int, vm: NetWorkViewModel, scope : CoroutineScope, onDismiss : () -> Unit) {
    val uiState by vm.surveyData.state.collectAsState()

    val refreshNetwork: suspend () -> Unit = {
        val cookie = getJxglstuCookie()
        cookie?.let {
            vm.surveyData.clear()
            vm.getSurveyToken(it,teacherId.toString())
            vm.getSurvey(it,teacherId.toString())
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork, isFullScreen = false) {
        SurveyList(vm, scope,teacherId) {
            onDismiss()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SurveyList(
    vm: NetWorkViewModel,
    scope: CoroutineScope,
    teacherId : Int,
    onResult : () -> Unit
) {
    val uiState by vm.surveyData.state.collectAsState()
    val bean = (uiState as UiState.Success).data
    var postMode by remember { mutableStateOf(PostMode.HIGH) }
    var showDialog by remember { mutableStateOf(false) }
    var showTextField by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("好") }

    if(showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                scope.launch {
                    launch {
                        postSurvey(vm,postMode,bean,input,teacherId)
                        showDialog = false
                        onResult()
                    }
                }
            },
            dialogTitle = "二次确认",
            dialogText = "提交后结果将不可查看与修改",
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
                    postMode = PostMode.MEDIUM
                    showDialog = true
                },
                text = "中等偏好",
                icon = R.drawable.thumb_up,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.width(APP_HORIZONTAL_DP/2))
            LargeButton (
                modifier = Modifier.fillMaxWidth().weight(.5f),
                onClick = {
                    postMode = PostMode.LOW
                    showDialog = true
                },
                icon = R.drawable.thumb_down,
                text = "中等偏差",
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(Modifier.height(APP_HORIZONTAL_DP/2))
        RowHorizontal {
            LargeButton(
                modifier = Modifier.fillMaxWidth().weight(.5f),
                onClick = {
                    postMode = PostMode.HIGH
                    showDialog = true
                },
                text = "好评 100分",
                icon = R.drawable.thumb_up,
            )
            Spacer(Modifier.width(APP_HORIZONTAL_DP/2))
            LargeButton (
                modifier = Modifier.fillMaxWidth().weight(.5f),
                onClick = {
                    postMode = PostMode.NONE
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
suspend fun postSurvey(vm : NetWorkViewModel, mode : PostMode, bean: SurveyResponse, comment: String = "好",teacherId : Int) = withContext(Dispatchers.IO) {
    // 主线程监听 StateFlow
    val token = vm.surveyToken.state.first() as? UiState.Success
    token ?: return@withContext
    val cookie = getJxglstuCookie()
    val result = vm.postSurvey("$cookie;${token.data}", postResult(mode, bean, comment,teacherId))
    if(result == StatusCode.OK.code) {
        showToast("提交成功")
    } else {
        showToast("提交失败 $result")
    }
}

//true为好评，false为差评
fun postResult(
    mode: PostMode,
    bean : SurveyResponse,
    comment : String = "好",
    teacherId : Int,
): JsonObject {
    val surveyAssoc = bean.lessonSurveyLesson.surveyAssoc
    val inputList = bean.survey.blankQuestions
    val inputNewList = mutableListOf<blankQuestionAnswer>()

    for (j in inputList.indices) {
        val id = inputList[j].id
        inputNewList.add(blankQuestionAnswer(id, comment))
    }

    // 组装数据
    val postSurvey = PostSurvey(surveyAssoc, teacherId, getLevel(mode,bean), inputNewList)

    return Gson().toJsonTree(postSurvey).asJsonObject
}


// 0~4
private fun getLevel(
    mode : PostMode,
    bean : SurveyResponse,
)  : List<radioQuestionAnswer> {
    val choiceList = bean.survey.radioQuestions
    val choiceNewList = mutableListOf<radioQuestionAnswer>()
    // 默认拿第一个选项为好评，拿最后一个为差评

    for (i in choiceList.indices) {
        val id = choiceList[i].id
        val optionList = choiceList[i].options
        val level = when(mode) {
            PostMode.LOW -> {
                PostMode.LOW.level
            }
            PostMode.HIGH -> {
                0
            }
            PostMode.NONE -> {
                optionList.size-1
            }
            PostMode.MEDIUM -> {
                PostMode.MEDIUM.level
            }
        }
        val finalIndex = if(level > optionList.size || level < 0) {
            0
        } else {
            level
        }
        val option = optionList[finalIndex].name
        choiceNewList.add(radioQuestionAnswer(id, option))
    }
    return choiceNewList
}
