package com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.icon.departmentIcon
import com.hfut.schedule.ui.component.network.UrlImage
   
import com.hfut.schedule.ui.style.padding.InnerPaddingHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherListUI(
    vm: NetWorkViewModel,
    innerPadding : PaddingValues,
) {
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
                CardListItem(
                    headlineContent = {
                        Text(text = it.name, fontWeight = FontWeight.Bold)
                    },
                    trailingContent = {
                        UrlImage(url = MyApplication.TEACHER_URL + it.picUrl, width = 100.dp, height = 120.dp)
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




