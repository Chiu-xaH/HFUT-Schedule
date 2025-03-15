package com.hfut.schedule.ui.activity.home.cube.items.subitems

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
import com.hfut.schedule.logic.beans.UserInfo
import com.hfut.schedule.logic.utils.DateTimeUtils
import com.hfut.schedule.logic.utils.VersionUtils
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.parse.formatDecimal
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import com.hfut.schedule.ui.utils.components.DepartmentIcons
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.TransplantListItem

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
//                        horizontal = AppHorizontalDp(),
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
                        if(startDate != null && endDate != null && startDate != "" && endDate != "") {
                            Text(text = "已陪伴 ${formatDecimal(DateTimeUtils.getPercent(startDate,endDate),1)}%")
                        } else { null }
                        },
//                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable {
                        expandItems = !expandItems
                        SharePrefs.saveBoolean("expandPerson",true,expandItems)
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


fun getUserInfo() : UserInfo {

    val date = DateTimeUtils.Date_yyyy_MM_dd
    val time = "${DateTimeUtils.formattedTime_Hour}:${DateTimeUtils.formattedTime_Minute}:00"
    val dateTime = "$date $time"


    val appVersion = VersionUtils.getVersionName()
    val androidSDK = VersionUtils.sdkInt
    val device = Build.MODEL

    return UserInfo(getPersonInfo().name,getPersonInfo().username,dateTime, appVersionName = appVersion, systemVersion = androidSDK, deviceName = device)
}

