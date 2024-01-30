package com.hfut.schedule.ui.ComposeUI.Search.TotalCourse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseTotalUI() {

    var num = 0.0
    for(i in 0 until getCourse().size) {
        val credit = getCourse()[i].credit
        num += credit
    }
    var numItem by remember { mutableStateOf(0) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("${getCourse()[numItem].courseName}") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    DetailItems(numItem)
                }
            }
        }
    }

    LazyColumn {
        item { Another(num) }
        items(getCourse().size) { item ->
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
                            overlineContent = { Text(text = getCourse()[item].trainingCategoryName_dictText)},
                            headlineContent = {  Text(getCourse()[item].courseName) },
                            supportingContent = { Text("学分 " + getCourse()[item].credit.toString()) },
                            trailingContent = { Icon( Icons.Filled.ArrowForward, contentDescription = "")},
                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = { Icon(painterResource(R.drawable.calendar_view_month), contentDescription = "Localized description",) },
                            modifier = Modifier.clickable {
                                numItem = item
                                showBottomSheet = true
                                                          },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Another(num : Double) {
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
                    headlineContent = {  Text("学分合计 ${num}") },
                    leadingContent = { Icon(painterResource(R.drawable.category), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable {},
                    colors = ListItemDefaults.colors(MaterialTheme.colorScheme.errorContainer)
                )
            }
        }
    }
}

@Composable
fun DetailItems(item : Int) {

    val lists = getDetailCourse(item,getCourse()[item].courseName)

    LazyColumn {
        item{
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
                    ) {
                        ListItem(
                            overlineContent = { Text(text = getCourse()[item].trainingCategoryName_dictText) },
                            headlineContent = { Text("学分 " + getCourse()[item].credit.toString() + " | " + "教师 " + lists[0].teacher ) },
                            supportingContent = { Text("班级 " + getCourse()[item].className) },
                            trailingContent = {
                            },
                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.calendar),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier.clickable {},
                        )
                    }
                }
            }
        }
        items(lists.size) { items ->
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
                    ) {
                        ListItem(
                            overlineContent = { Text(text = "时间 "+ lists[items].classTime) },
                            headlineContent = { Text("周 "+ lists[items].weekCount.toString()) },
                            supportingContent = { Text("周 ${lists[items].sectionCount}  第 ${lists[items].section} 节") },
                            trailingContent = {
                            },
                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.calendar),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier.clickable {},
                        )
                    }
                }
            }
        }
    }
}