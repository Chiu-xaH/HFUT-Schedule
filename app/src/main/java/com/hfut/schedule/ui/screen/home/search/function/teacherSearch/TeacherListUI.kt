package com.hfut.schedule.ui.screen.home.search.function.teacherSearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.URLImage
import com.hfut.schedule.ui.component.WebDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherListUI(vm: NetWorkViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var links by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("教师主页") }
    val dataList = getTeacherList(vm)

    WebDialog(showDialog,{ showDialog = false },links,title)


    LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 11.dp))  {
        items(dataList.size) { index->
            val item = dataList[index]
            item?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors =  CardDefaults.cardColors(containerColor = cardNormalColor())
                ) {
                    TransplantListItem(
                        headlineContent = { Text(text = it.name) },
                        supportingContent = {
                            Column {
                                Spacer(modifier = Modifier.height(2.dp))
                                URLImage(url = MyApplication.TEACHER_URL + it.picUrl, width = 120.dp, height = 120.dp)
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




