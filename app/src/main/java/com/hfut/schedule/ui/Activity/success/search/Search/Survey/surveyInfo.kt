package com.hfut.schedule.ui.Activity.success.search.Search.Survey

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.Enums.PostMode
import com.hfut.schedule.logic.datamodel.Jxglstu.PostSurvey
import com.hfut.schedule.logic.datamodel.Jxglstu.blankQuestionAnswer
import com.hfut.schedule.logic.datamodel.Jxglstu.radioQuestionAnswer
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.ScrollText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun surveyInfo(id : Int,vm: LoginSuccessViewModel) {
    var loading by remember { mutableStateOf(true) }
    val cookie = SharePrefs.prefs.getString("redirect", "")


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
            CircularProgressIndicator()
        }
    }////加载动画居中，3s后消失


    AnimatedVisibility(
        visible = !loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        surveyList(vm)
    }
}
@Composable
fun surveyList(vm : LoginSuccessViewModel) {
    val choiceList = getSurveyChoice(vm)
    val inputList = getSurveyInput(vm)
    //saveList(vm,choiceList,inputList)

    LazyColumn {
        items(choiceList.size) {item ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                ListItem(
                    headlineContent = { Text(text = choiceList[item].title) },
                    leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                    supportingContent = {
                        val list = getOption(choiceList[item])
                        val radioOptions = list
                        val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

                        Column {
                           for(i in list.indices) {
                               // ListItem(headlineContent = { Text(text = list[i].name ) }, trailingContent = { Text(text = list[i].score.toString() + "分")}, leadingContent = {

                              //  })

                               Row(
                                   Modifier
                                       .fillMaxWidth()
                                       .height(56.dp)
                                      // .weight(.1f)
                                       .selectable(
                                           selected = (list[i] == selectedOption),
                                           onClick = {
                                               onOptionSelected(list[i])
                                           },
                                           role = Role.RadioButton
                                       )
                                       .padding(horizontal = 15.dp),
                                   verticalAlignment = Alignment.CenterVertically
                               ) {
                                   RadioButton(
                                       selected = (list[i] == selectedOption),
                                       onClick = null
                                   )
                                   Text(
                                       text = list[i].score.toString() + "分-" + list[i].name,
                                       style = MaterialTheme.typography.bodyLarge,
                                       modifier = Modifier.padding(start = 10.dp)
                                   )
                               }
                            }
                        }
                    },
                    modifier = Modifier.clickable {

                    },
                )
            }
        }
        items(inputList.size) {item ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                ListItem(
                    headlineContent = { Text(text = inputList[item].title) },
                    leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable {

                    },
                )
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
fun postResult(vm : LoginSuccessViewModel, mode : PostMode) : Boolean {

    val cookie = SharePrefs.prefs.getString("redirect", "")
    val token = prefs.getString("SurveyCookie","")
    return when(mode) {
        PostMode.NORMAL -> {
            MyToast("正在开发")
            false
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
fun postResult(vm: LoginSuccessViewModel,goodMode: Boolean): JsonObject {
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

    // 转换为JsonObject

    return Gson().toJsonTree(postSurvey).asJsonObject
}
