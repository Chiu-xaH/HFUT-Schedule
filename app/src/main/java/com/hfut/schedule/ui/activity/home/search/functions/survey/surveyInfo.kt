package com.hfut.schedule.ui.activity.home.search.functions.survey

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import com.hfut.schedule.ui.utils.components.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.enums.PostMode
import com.hfut.schedule.logic.beans.jxglstu.PostSurvey
import com.hfut.schedule.logic.beans.jxglstu.blankQuestionAnswer
import com.hfut.schedule.logic.beans.jxglstu.radioQuestionAnswer
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.style.RowHorizontal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun surveyInfo(id : Int,vm: NetWorkViewModel) {
    var loading by remember { mutableStateOf(true) }

    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")

    CoroutineScope(Job()).launch{
        async { cookie?.let { vm.getSurveyToken(it,id.toString()) } }
        async{ cookie?.let { vm.getSurvey(it,id.toString()) } }.await()
        async {
            Handler(Looper.getMainLooper()).post{
                vm.surveyData.observeForever { result ->
                    if (result != null) {
                        if(result.contains("lessonSurveyLesson")) {
                            loading = false
                        }
                    }
                }
            }
        }
    }

    Box() {
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
        }////加载动画居中，3s后消失


        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SurveyList(vm)
        }
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyList(vm : NetWorkViewModel) {
    val choiceList = getSurveyChoice(vm)
    val inputList = getSurveyInput(vm)
    //saveList(vm,choiceList,inputList)
    var input by remember { mutableStateOf("") }
    val choiceNewList = mutableListOf<radioQuestionAnswer>()
    val inputNewList = mutableListOf<blankQuestionAnswer>()

   // val scrollstate = rememberLazyListState()
   // val shouldShowAddButton by remember { derivedStateOf { scrollstate.firstVisibleItemScrollOffset == 0 } }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Icon(
                painterResource(R.drawable.arrow_upward),
                contentDescription = "",
                Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        RowHorizontal {
            Text("请选择右上角发送", color = MaterialTheme.colorScheme.primary)
        }
        RowHorizontal {
            Text( "评(100分 \"好\") / 差评(0分 \"好\")", color = MaterialTheme.colorScheme.primary)
        }
    }

    //下面是评教题目，不给用户开放了，直接显示两个大按钮，好评或差评
//    fun postResultNormal(vm : NetWorkViewModel) : JsonObject {
//        val surveyAssoc = getSurveyAssoc(vm)
//        val lessonSurveyTaskAssoc = prefs.getInt("teacherID", 0)
//        val postSurvey = PostSurvey(surveyAssoc, lessonSurveyTaskAssoc, choiceNewList, inputNewList)
//        return Gson().toJsonTree(postSurvey).asJsonObject
//    }
//    Box() {
//        LazyColumn() {
//            items(inputList.size) {item ->
//                Card(
//                    elevation = CardDefaults.cardElevation(defaultElevation = 1.75.dp),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
//                    shape = MaterialTheme.shapes.medium,
//                ) {
//                    inputNewList.add(blankQuestionAnswer(inputList[item].id,input))
//                    ListItem(
//                        headlineContent = { Text(text = inputList[item].title) },
//                        leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
//                        supportingContent = {
//                            //输入框
//                            TextField(
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .padding(horizontal = AppHorizontalDp()),
//                                value = input,
//                                onValueChange = {
//                                    input = it
//                                    inputNewList.add(blankQuestionAnswer(inputList[item].id,input))
//                                },
//                                label = { Text("输入内容" ) },
//                                singleLine = true,
//                                shape = MaterialTheme.shapes.medium,
//                                colors = TextFieldDefaults.textFieldColors(
//                                    focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
//                                    unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
//                                ),
//                                // leadingIcon = { Icon( painterResource(R.drawable.search), contentDescription = "Localized description") },
//                            )
//                        },
//                        modifier = Modifier.clickable {
//
//                        },
//                    )
//                }
//            }
//            items(choiceList.size) {item ->
//                MyCard {
//                    ListItem(
//                        headlineContent = { Text(text = choiceList[item].title) },
//                        leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
//                        supportingContent = {
//                            val list = getOption(choiceList[item])
//                            val radioOptions = list
//                            val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
//
//                            Column {
//                                for(i in list.indices) {
//                                   // choiceNewList.add(radioQuestionAnswer(choiceList[item].id,selectedOption.name))
//                                    Row(
//                                        Modifier
//                                            .fillMaxWidth()
//                                            .height(56.dp)
//                                            // .weight(.1f)
//                                            .selectable(
//                                                selected = (list[i] == selectedOption),
//                                                onClick = {
//                                                    onOptionSelected(list[i])
//                                                    choiceNewList.add(radioQuestionAnswer(choiceList[item].id,selectedOption.name))
//                                                          },
//                                                role = Role.RadioButton
//                                            )
//                                            .padding(horizontal = AppHorizontalDp()),
//                                        verticalAlignment = Alignment.CenterVertically
//                                    ) {
//                                        RadioButton(
//                                            selected = (list[i] == selectedOption),
//                                            onClick = null
//                                        )
//                                        Text(
//                                            text = list[i].score.toString() + "分-" + list[i].name,
//                                            style = MaterialTheme.typography.bodyLarge,
//                                            modifier = Modifier.padding(start = 10.dp)
//                                        )
//                                    }
//                                }
//                            }
//                        },
//                        modifier = Modifier.clickable {
//
//                        },
//                    )
//                }
//            }
//        }
//    /*    AnimatedVisibility(
//            visible = shouldShowAddButton,
//            enter = scaleIn() ,
//            exit = scaleOut(),
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                // .padding(innerPaddings)
//                .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
//        ) {
//            if (shouldShowAddButton) {  */
//                FloatingActionButton(
//                    onClick = { Log.d("c",postResultNormal(vm).toString()) },
//                    modifier = Modifier
//                        .align(Alignment.BottomEnd)
//                        // .padding(innerPaddings)
//                        .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
//                ) { Icon(painterResource(id = R.drawable.arrow_upward), "Add Button") }
//         //   }
//     //   }
//    }
}

@SuppressLint("SuspiciousIndentation")
fun selectMode(vm : NetWorkViewModel, mode : PostMode) : Boolean {

    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
    val token = prefs.getString("SurveyCookie","")
    return when(mode) {
        PostMode.NORMAL -> {
            //vm.postSurvey("$cookie;$token", postResultNormal(vm))
            
            MyToast("提交完成")
            true
        }

        PostMode.GOOD ->  {
            vm.postSurvey("$cookie;$token", postResult(vm,true))
            MyToast("提交完成")
            true
        }

        PostMode.BAD -> {
            vm.postSurvey("$cookie;$token", postResult(vm,false))
            MyToast("提交完成")
            true
        }
    }
}

//true为好评，false为差评
fun postResult(vm: NetWorkViewModel, goodMode: Boolean): JsonObject {
    val surveyAssoc = getSurveyAssoc(vm)
    val lessonSurveyTaskAssoc = prefs.getInt("teacherID", 0)
    val choiceList = getSurveyChoice(vm)
    val inputList = getSurveyInput(vm)
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
        inputNewList.add(blankQuestionAnswer(id, "好"))
    }

    // 组装数据
    val postSurvey = PostSurvey(surveyAssoc, lessonSurveyTaskAssoc, choiceNewList, inputNewList)

    return Gson().toJsonTree(postSurvey).asJsonObject
}
