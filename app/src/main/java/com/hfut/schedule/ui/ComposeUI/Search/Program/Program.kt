package com.hfut.schedule.ui.ComposeUI.Search.Program

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.Community.ProgramResponse
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.ComposeUI.Search.Electric.WebViewScreen
import com.hfut.schedule.ui.ComposeUI.Search.Electric.WebViewScreen2
import com.hfut.schedule.ui.ComposeUI.Search.Exam.getExam
import org.jsoup.Jsoup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Program(vm : LoginSuccessViewModel) {
    val sheetState_Program = rememberModalBottomSheetState()
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    var view by rememberSaveable { mutableStateOf("") }
    val CommuityTOKEN = prefs.getString("TOKEN","")
    CommuityTOKEN?.let { vm.GetProgram(it) }
    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = "培养方案") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.conversion_path),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
           // val program = prefs.getString("program", " <h2 class=\"info-title\"><i style=\"color: #ffa200;\" class=\"fa fa-warning highlight\"></i>未获取到</h2>")
            showDialog = true
            //val doc = Jsoup.parse(program)
          //  view = doc.select("h2.info-title").text()
        }
    )


    if (showBottomSheet_Program ) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Program = false },
            sheetState = sheetState_Program
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text(getProgramItem()["name"].toString()) }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramUI()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }



    if (showDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        actions = { IconButton(onClick = { showDialog = false }) {
                            Icon(painterResource(id = R.drawable.close), contentDescription = "")
                        }
                        },
                        title = { Text("培养方案") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    prefs.getString("redirect", "")?.let {
                        WebViewScreen2(url = "https://jxglstu.hfut.edu.cn/eams5-student/for-std/program/info/${prefs.getString("studentId", "99999")}", it)
                    }
                }
            }
        }
    }
}

@Composable
fun ProgramUI() {
    Spacer(modifier = Modifier.height(10.dp))
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
                    headlineContent = { Text(text = "学分   已完成 ${getProgramItem()["got"]} / 要求 ${getProgramItem()["require"]}") },
                    leadingContent = { Icon(painterResource(id = R.drawable.hotel_class), contentDescription = "Localized description") },
                    modifier = Modifier.clickable {},
                )

               // prefs.getString("redirect", "")?.let { Log.d("cookie", it) }
            }
        }
    }

}

fun getProgramItem() : Map<String, Any> {
    val json = prefs.getString("Program",MyApplication.NullProgram)
    val result = Gson().fromJson(json,ProgramResponse::class.java).result
    val name = result.majorName
    val require = result.totalCreditRequirement
    val got = result.totalCreditDone
    return mapOf("name" to name, "require" to require, "got" to got)
}