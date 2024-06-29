package com.hfut.schedule.ui.Activity.success.search.Search.Program

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.search.Search.More.Login
import com.hfut.schedule.ui.Activity.success.search.Search.TotalCourse.courseIcons
import com.hfut.schedule.ui.UIUtils.MyToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Program(vm : LoginSuccessViewModel,ifSaved : Boolean) {
    val sheetState_Program = rememberModalBottomSheetState()
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    var show by remember { mutableStateOf(true) }
    //getProgram()
    ListItem(
        headlineContent = { Text(text = "培养方案") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.conversion_path),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            if (prefs.getString("program","")?.contains("children") == false) {
                Login()
                MyToast("首次登录以缓存培养方案")
            }
            else showBottomSheet_Program = true
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
                        title = { Text("培养方案") },
                        actions = {
                            Row (modifier = Modifier.padding(horizontal = 15.dp)){
                                FilledTonalButton(
                                    onClick = { MyToast("正在开发") },
                                ) {
                                    Text(text = "筛选")
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                FilledTonalButton(
                                    onClick = { MyToast("正在开发") },
                                ) {
                                    Text(text = "排序方式")
                                }
                            }
                        }
                    )
                },
            ) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramUI2()
                    if(show)
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            headlineContent = { Text(text = "部分功能与信息未完全开发,选修课列表暂未开发") },
                            trailingContent = { IconButton(onClick = { show = false }) {
                                Icon(Icons.Filled.Close, contentDescription = "",)
                            }},
                            leadingContent = { Icon(painterResource(id = R.drawable.info), contentDescription = "Localized description") },
                            modifier = Modifier.clickable {},
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun ProgramUI() {
  //  val list =
        val json = prefs.getString("programJSON","")
       // try {
            val listType = object : TypeToken<List<ProgramShow>>() {}.type
            val list: List<ProgramShow> = Gson().fromJson(json,listType)
      //  }

    var sum = 0.0
    for(i in list.indices) {
        val credits = list[i].credit
        if (credits != null) { sum += credits }
    }

    LazyColumn {
        item {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                ListItem(
                    headlineContent = { Text(text = "合计 ${list.size} 门 ${sum} 学分") },
                    supportingContent = { Text(text = "不含选修课!")},
                 //   overlineContent = { },
                 //   trailingContent = { Text(text = "学分 " +  list[item].credit)},
                    colors = ListItemDefaults.colors(MaterialTheme.colorScheme.primaryContainer),
                    leadingContent = { Icon(painterResource(id = R.drawable.conversion_path), contentDescription = "Localized description") },
                    modifier = Modifier.clickable {},
                )
            }
        }
        items(list.size) {item ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                ListItem(
                    headlineContent = { Text(text = list[item].name) },
                    supportingContent = { Text(text = list[item].school + "  第" + list[item].term[0] + "学期")},
                    overlineContent = { list[item].type?.let { Text(text = it) } },
                    trailingContent = { Text(text = "学分 " +  list[item].credit)},
                    leadingContent = { Icon(painterResource(id = R.drawable.calendar), contentDescription = "Localized description") },
                    modifier = Modifier.clickable {},
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramUI2() {
    val sheetState_Program = rememberModalBottomSheetState()
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    val listOne = getProgramListOne()
    var title by remember { mutableStateOf("培养方案") }
    var num by remember { mutableStateOf(0) }
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
                        title = { Text(title) }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){

                    ProgramUIInfo(num)

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    LazyColumn {
        items(listOne.size) {item ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                ListItem(
                    headlineContent = { Text(text = listOne[item].type + " | 学分要求 " + listOne[item].requiedCredits) },
                    trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "")},
                 //   leadingContent = { Icon(painterResource(id = R.drawable.calendar), contentDescription = "Localized description") },
                    modifier = Modifier.clickable {
                        showBottomSheet_Program = true
                        num = item
                        title = listOne[item].type.toString()
                                                  },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramUIInfo(num : Int) {
    val listTwo = getProgramListTwo(num)
    var show by remember { mutableStateOf(true) }
    val sheetState_Program = rememberModalBottomSheetState()
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("培养方案") }
    var num2 by remember { mutableStateOf(0) }
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
                        title = { Text(title) }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramUIInfo2(num,num2)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
    listTwo.forEach{ item->
        show = item.type != "直接跳转"
    }
    Log.d("show",show.toString())
    if(show) {
        LazyColumn {
            items(listTwo.size) {item ->
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 5.dp),
                    shape = MaterialTheme.shapes.medium,
                ){
                    ListItem(
                        headlineContent = { Text(text = listTwo[item].type + " | 学分要求 " + listTwo[item].requiedCredits) },
                        trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "")},
                        //   leadingContent = { Icon(painterResource(id = R.drawable.calendar), contentDescription = "Localized description") },
                        modifier = Modifier.clickable {
                            showBottomSheet_Program = true
                            num2 = item
                            title = listTwo[item].type.toString()
                        },
                    )
                }
            }
        }
    } else {
       ProgramUIInfo2(num,num2)
    }
}

@Composable
fun ProgramUIInfo2(num1 : Int,num2 : Int) {
    val listThree = getProgramListThree(num1,num2)
    listThree.sortBy { it.term }
    LazyColumn {
        items(listThree.size) {item ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                ListItem(
                    headlineContent = { Text(text = listThree[item].name) },
                    supportingContent = { Text(text = listThree[item].depart) },
                    overlineContent = { Text(text = "第" + listThree[item].term + "学期 | 学分 ${listThree[item].credit}")},
                    leadingContent = { courseIcons(name = listThree[item].depart) },
                    modifier = Modifier.clickable {
                    },
                )
            }
        }
    }
}