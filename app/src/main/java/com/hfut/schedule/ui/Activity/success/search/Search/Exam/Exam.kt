package com.hfut.schedule.ui.Activity.success.search.Search.Exam

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BadgedBox
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.UIUtils.EmptyUI

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Exam(vm : LoginSuccessViewModel) {
    val sheetState_Exam = rememberModalBottomSheetState()
    var showBottomSheet_Exam by remember { mutableStateOf(false) }
    val CommuityTOKEN = prefs.getString("TOKEN","")
    CommuityTOKEN?.let { vm.Exam(it) }

    ListItem(
        headlineContent = { Text(text = "考试  ${getNewExam().size} 门") },
        leadingContent = {
            BadgedBox(badge = {
                if(prefs.getString("ExamNum","0") != getNewExam().size.toString())
                Badge(){
                    Text(text = getNewExam().size.toString())
                }
            }) {
                Icon(painterResource(R.drawable.draw), contentDescription = "Localized description",)
            }
        },
        modifier = Modifier.clickable {
            showBottomSheet_Exam = true
            Save("ExamNum", getNewExam().size.toString())
            getExam()
        }
    )

    if (showBottomSheet_Exam) {
        
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Exam = false
            },
            sheetState = sheetState_Exam
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("考试") }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    if(getExam().size == 0) EmptyUI()
                    else
                    LazyColumn { items(getExam().size) { item -> ExamItems(item,false) } }
                }
            }
        }
    }
}
@Composable
fun ExamItems(item : Int,status : Boolean) {
    var date = GetDate.Date_yyyy_MM_dd
    val todaydate = date?.substring(0, 4) + date?.substring(5, 7)  + date?.substring(8, 10)
    val get = getExam()[item].formatEndTime
    //判断考完试不显示信息

    val examdate = (get?.substring(0,4)+ get?.substring(5, 7) ) + get?.substring(8, 10)

    val st = getExam()[item].formatStartTime

    @Composable
    fun Item() {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Column() {
                Card(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 15.dp,
                            vertical = 5.dp
                        ),
                    shape = MaterialTheme.shapes.medium,
                ){
                    ListItem(
                        headlineContent = { getExam()[item].courseName?.let { Text(text = it) } },
                        overlineContent = { Text(text = st?.substring(5,st.length - 3) + "~" + get?.substring(11,get.length-3)) },
                        supportingContent = { getExam()[item].place?.let { Text(text = it) } },
                        leadingContent = {
                            if(status==true) Icon(painterResource(R.drawable.draw), contentDescription = "Localized description",)
                            else if(examdate.toInt() >= todaydate.toInt()) Icon(painterResource(R.drawable.schedule), contentDescription = "Localized description",)
                            else Icon(Icons.Filled.Check, contentDescription = "Localized description",)
                        },
                        trailingContent = {if(examdate.toInt() < todaydate.toInt()) Text(text = "已结束")},
                        colors =  if(examdate.toInt() >= todaydate.toInt())
                            ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        else ListItemDefaults.colors(),
                        modifier = Modifier.clickable {},
                    )
                }
            }
        }
    }

    if(status == true){
        if(examdate.toInt() >= todaydate.toInt()){
            Item()
        }
    } else{
        Item()
    }

}