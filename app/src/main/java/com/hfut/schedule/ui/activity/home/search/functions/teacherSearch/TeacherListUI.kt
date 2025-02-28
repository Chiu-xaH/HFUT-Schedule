package com.hfut.schedule.ui.activity.home.search.functions.teacherSearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.utils.components.CardNormalColor
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.URLImage
import com.hfut.schedule.ui.utils.components.WebDialog
import com.hfut.schedule.ui.utils.components.WebViewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherListUI(vm: NetWorkViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var links by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("教师主页") }
    val dataList = getTeacherList(vm)

    WebDialog(showDialog,{ showDialog = false },links,title)

//    if (showDialog) {
//        androidx.compose.ui.window.Dialog(
//            onDismissRequest = { showDialog = false },
//            properties = DialogProperties(usePlatformDefaultWidth = false)
//        ) {
//            Scaffold(
//                modifier = Modifier.fillMaxSize(),
//                topBar = {
//                    TopAppBar(
//                        colors = TopAppBarDefaults.mediumTopAppBarColors(
//                            containerColor = Color.Transparent,
//                            titleContentColor = MaterialTheme.colorScheme.primary,
//                        ),
//                        actions = {
//                            Row{
//                                IconButton(onClick = { Starter.startWebUrl(links) }) { Icon(
//                                    painterResource(id = R.drawable.net), contentDescription = "") }
//                                IconButton(onClick = { showDialog = false }) { Icon(painterResource(id = R.drawable.close), contentDescription = "") }
//                            }
//                        },
//                        title = { Text(title) }
//                    )
//                },
//            ) { innerPadding ->
//                Column(
//                    modifier = Modifier
//                        .padding(innerPadding)
//                        .fillMaxSize()
//                ) {
//                    WebViewScreen(url = links)
//                }
//            }
//        }
//    }
//

    LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 11.dp))  {
        items(dataList.size) { index->
            val item = dataList[index]
            item?.let {
                MyCustomCard(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp), hasElevation = false, containerColor = CardNormalColor()) {
                    TransplantListItem(
                        headlineContent = { Text(text = it.name) },
                        supportingContent = {
                            Column {
                                Spacer(modifier = Modifier.height(2.dp))
                                URLImage(url = MyApplication.TeacherURL + it.picUrl, width = 120.dp, height = 120.dp)
                            }
                        },
//                        trailingContent = {
//                            Icon(Icons.Filled.ArrowForward, contentDescription = "")
//                        },
                        modifier = Modifier.clickable {
                            links = it.url
                            title = it.name
                            showDialog = true
                        }
                    )
                }
            }
        }
    }
}




