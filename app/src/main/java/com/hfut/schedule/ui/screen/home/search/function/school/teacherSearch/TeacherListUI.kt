package com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.icon.departmentIcon
import com.hfut.schedule.ui.component.icon.filterDepartmentName
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.xah.common.ui.style.align.CenterScreen

import com.xah.common.ui.style.padding.InnerPaddingHeight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherListUI(
    vm: NetWorkViewModel,
    innerPadding : PaddingValues,
    filterName : String? = null,
) {
    val uiState by vm.teacherSearchData.state.collectAsState()
    val dataList = (uiState as NetworkUiState.Success).data.teacherData.let {
        filterName?.let { t ->
            it.filter { it.name == t }
        } ?: it
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    if(dataList.isEmpty()) {
        CenterScreen {
            EmptyIcon("暂未收录")
        }
    } else {
        LazyColumn {
            item { InnerPaddingHeight(innerPadding,true) }
            item { Spacer(modifier = Modifier.height(CARD_NORMAL_DP*2)) }
            items(dataList.size) { index->
                val item = dataList[index]
                item.let {
                    val department = it.department.replace("&nbsp;","").filterDepartmentName()
                    val icon = departmentIcon(department)
                    val jobList = listOf(it.job,it.tutor ,it.doctorTutor).filter { it.isNotEmpty() && it.isNotBlank() }
                    CardListItem(
                        headlineContent = {
                            Text(text = it.name, fontWeight = FontWeight.Bold)
                        },
                        trailingContent = {
                            UrlImage(
                                url = Constant.TEACHER_URL + it.picUrl,
                                modifier = Modifier.size(width = 100.dp, height = 120.dp),
                                shape = MaterialTheme.shapes.small,
                                contentScale = ContentScale.FillBounds
                            )
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
                            scope.launch {
                                Starter.startWebUrlInner(context,it.url,it.name, icon = icon)
                            }
                        }
                    )
                }
            }
            item { InnerPaddingHeight(innerPadding,false) }
        }
    }
}




