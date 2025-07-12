package com.hfut.schedule.ui.screen.home.cube.sub

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.component.DepartmentIcons
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.custom.ScrollText
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.style.ColumnVertical

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PersonPart() {
    var expandItems by remember { mutableStateOf(prefs.getBoolean("expandPerson",false)) }
    val startDate = getPersonInfo().startDate
    val endDate = getPersonInfo().endDate
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Column() {
//             {
//
//            }
//            Card(
//                elevation = CardDefaults.cardElevation(
//                    defaultElevation = 1.75.dp
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(
//                        horizontal = APP_HORIZONTAL_DP,
//                        vertical = 4.dp
//                    ),
//                shape = MaterialTheme.shapes.medium,
//                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
//            )
            MyCustomCard(containerColor = MaterialTheme.colorScheme.primaryContainer, hasElevation = false)
            {


                TransplantListItem(
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.person), contentDescription = "")},
                    headlineContent = { Text(text = getPersonInfo().name ?: "游客")  },
                    trailingContent = {
                        Row {
                            ColumnVertical {
                                if(AppVersion.isInDebugRunning()) {
                                    Text("运行在开发设备")
                                }
                                if(AppVersion.isPreview()) {
                                    Text("内部测试版本")
                                }
                                if(startDate != null && endDate != null && startDate != "" && endDate != "") {
                                    Text(text = "已过 ${formatDecimal(DateTimeManager.getPercent(startDate,endDate),1)}%")
                                } else { null }
                            }
                        }
                                      },
//                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable {
                        expandItems = !expandItems
                        SharedPrefs.saveBoolean("expandPerson",true,expandItems)
                    }
                )

                AnimatedVisibility(
                    visible = expandItems,
                    enter = slideInVertically(
                        initialOffsetY = { -40 }
                    ) + expandVertically(
                        expandFrom = Alignment.Top
                    ) + scaleIn(
                        transformOrigin = TransformOrigin(0.5f, 0f)
                    ) + fadeIn(initialAlpha = 0.3f),
                    exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
                ) {
                    Column {
                        getPersonInfo().username?.let{
                            Row {
                                TransplantListItem(
                                    overlineContent = { Text(text = "学号") },
                                    headlineContent = {  ScrollText(text = it)  },
                                    leadingContent = {
                                        Icon(
                                            painterResource(R.drawable.tag),
                                            contentDescription = "Localized description",
                                        )
                                    },
                                    modifier = Modifier.weight(0.5f),
//                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                            }
                        }
                        getPersonInfo().department?.let {
                            Row {
                                TransplantListItem(
                                    overlineContent = { getPersonInfo().school?.let { Text(text = it) } },
                                    leadingContent = { DepartmentIcons(name = it) },
                                    headlineContent = {  ScrollText(text = it)  },
                                    modifier = Modifier.weight(0.5f),
//                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                            }
                        }
                        getPersonInfo().classes?.let {
                            Row {
                                TransplantListItem(
                                    overlineContent = {  Text(text = it)  },
                                    leadingContent = {
                                        Icon(
                                            painterResource(R.drawable.square_foot),
                                            contentDescription = "Localized description",
                                        )
                                    },
                                    headlineContent = { getPersonInfo().major?.let { ScrollText(text = it) } }
                                    ,
                                    modifier = Modifier.weight(1f),
//                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

