package com.hfut.schedule.ui.screen.fix.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.WebDialog
 
import com.hfut.schedule.ui.component.cardNormalColor
import dev.chrisbanes.haze.HazeState

private data class SupportItemBean(val title : String,val android : String,val url : String?,val list: List<Boolean?>)

@Composable
fun Support(innerPadding : PaddingValues) {
    val items = listOf(
        // 华为 小米 oppo vivo 荣耀 三星 魅族 类原生
        SupportItemBean("开屏页面","Android 8+","https://github.com/Chiu-xaH/HFUT-Schedule/blob/main/docs/CONTRAST.md#%e8%bf%9b%e5%ba%a6%e5%ae%9e%e6%97%b6%e9%80%9a%e7%9f%a5", listOf(
            false,true,true,true,true,true,true,true
        )),
        SupportItemBean("全局动态取色","Android 12+","https://github.com/Chiu-xaH/HFUT-Schedule/blob/main/docs/CONTRAST.md#%E5%8A%A8%E6%80%81%E5%8F%96%E8%89%B2", listOf(
            false,true,true,true,false,true,null,true
        )),
        SupportItemBean("图标动态取色","Android 12+","https://github.com/Chiu-xaH/HFUT-Schedule/blob/main/docs/CONTRAST.md#%E5%8A%A8%E6%80%81%E5%8F%96%E8%89%B2", listOf(
            false,true,null,false,false,true,false,true
        )),
        SupportItemBean("层级实时模糊","Android 12+","https://github.com/Chiu-xaH/HFUT-Schedule/blob/main/docs/CONTRAST.md#%E5%B1%82%E7%BA%A7%E5%AE%9E%E6%97%B6%E6%A8%A1%E7%B3%8A", listOf(
            true,true,true,true,true,true,true,true
        )),
        SupportItemBean("预测式返回","Android 13+","https://github.com/Chiu-xaH/HFUT-Schedule/blob/main/docs/CONTRAST.md#%E9%A2%84%E6%B5%8B%E5%BC%8F%E8%BF%94%E5%9B%9E", listOf(
            false,false,true,false,false,true,null,true
        )),
        SupportItemBean("16KB页大小","Android 15+","https://github.com/Chiu-xaH/HFUT-Schedule/blob/main/docs/CONTRAST.md#16kb%E9%A1%B5%E5%A4%A7%E5%B0%8F", listOf(
            false,false,null,null,null,null,null,true
        )),
        SupportItemBean("进度实时通知","Android 16+","https://github.com/Chiu-xaH/HFUT-Schedule/blob/main/docs/CONTRAST.md#%e8%bf%9b%e5%ba%a6%e5%ae%9e%e6%97%b6%e9%80%9a%e7%9f%a5", listOf(
            false,null,null,null,null,null,null,true
        ))
    )
    LazyColumn(modifier = Modifier.padding(innerPadding)) {
        item {
            TransplantListItem(
                headlineContent = { Text("聚在工大致力于为每个用户提供平等的服务，但由于不同手机厂商对Android系统的定制，以及Android版本的不同，导致最终效果往往不同，但以下的特性均不影响APP的功能") },
                leadingContent = {
                    Icon(painterResource(R.drawable.info),null)
                }
            )
        }

        items(items.size) { index ->
            val item = items[index]
            SupportItem(item)
        }
    }
}

@Composable
private fun SupportChip(title: String,isSupported : Boolean?) = AssistChip(
    onClick = {},
    label = { Text(title) },
    leadingIcon = {
        when(isSupported) {
            true -> Icon(Icons.Filled.Check,null)
            false -> Icon(Icons.Filled.Close,null, tint = MaterialTheme.colorScheme.error)
            null -> Text("未知")
        }
    }
)
@Composable
private fun ErrorSupportChip(title : String) = SupportChip(title,isSupported = false)
@Composable
private fun CanSupportChip(title : String) = SupportChip(title,isSupported = true)

@Composable
private fun SupportItem(item : SupportItemBean) {
    var url by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    WebDialog(showDialog = showDialog, url = url, title = "Github",showChanged = { showDialog = false })
    val list = item.list
    MyCustomCard(containerColor = cardNormalColor()) {
        Column {
            TransplantListItem(
                headlineContent = {
                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                },
                supportingContent = {
                    // 支持机型
                    Column {
//                        Text("支持情况")
                        Row {
                            SupportChip("HUAWEI",isSupported = list[0])
                            Spacer(Modifier.width(10.dp))
                            SupportChip("MI",isSupported = list[1])
                        }
                        Row {
                            SupportChip("OPPO",isSupported = list[2])
                            Spacer(Modifier.width(10.dp))
                            SupportChip("vivo",isSupported = list[3])
                        }
                        Row {
                            SupportChip("HONOR",isSupported = list[4])
                            Spacer(Modifier.width(10.dp))
                            SupportChip("SAMSUNG",isSupported = list[5])
                        }
                        Row {
                            SupportChip("MEIZU",isSupported = list[6])
                            Spacer(Modifier.width(10.dp))
                            SupportChip("(类)原生",isSupported = list[7])
                        }
                    }
                }
            )
            HorizontalDivider()
            Row(modifier = Modifier.align(Alignment.End)) {
                Text(
                    text = item.android,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Bottom).padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP - 5.dp)
                )
                item.url?.let {
                    Text(
                        text = "预览效果",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Top).padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP - 5.dp).clickable {
                            url = it
                            showDialog = true
                        }
                    )
                }
            }
        }
    }
}
