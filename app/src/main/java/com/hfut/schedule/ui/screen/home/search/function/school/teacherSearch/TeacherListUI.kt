package com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.icon.departmentIcon
import com.hfut.schedule.ui.component.network.URLImage
   
import com.hfut.schedule.ui.style.InnerPaddingHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherListUI(vm: NetWorkViewModel,innerPadding : PaddingValues) {
    val uiState by vm.teacherSearchData.state.collectAsState()
    val dataList = (uiState as UiState.Success).data.teacherData

    LazyColumn() {
        item { InnerPaddingHeight(innerPadding,true) }
        item { Spacer(modifier = Modifier.height(4.dp)) }
        items(dataList.size) { index->
            val item = dataList[index]
            item.let {
                val department = it.department.replace("&nbsp;","").substringBefore("（")
                val icon = departmentIcon(department)
                val jobList = listOf(it.job,it.gtutor ,it.doctorTutor).filter { it.isNotEmpty() && it.isNotBlank() }
                StyleCardListItem(
                    headlineContent = {
                        Text(text = it.name, fontWeight = FontWeight.Bold)
                    },
                    trailingContent = {
                        URLImage(url = MyApplication.TEACHER_URL + it.picUrl, width = 100.dp, height = 120.dp)
                    },
                    overlineContent = {
                        Text(department)
                    },
                    leadingContent = {
                        DepartmentIcons(department)
                    },
                    supportingContent = {
                        Text(jobList.toString().replace("[","").replace("]",""))
                    },
                    modifier = Modifier.clickable {
                        Starter.startWebView(it.url,it.name, icon = icon)
                    }
                )
            }
        }
        item { InnerPaddingHeight(innerPadding,false) }
    }
}




