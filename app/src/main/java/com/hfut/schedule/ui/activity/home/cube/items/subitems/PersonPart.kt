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
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.beans.UserInfo
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.DateTimeManager
import com.hfut.schedule.logic.utils.ReservDecimal
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.DepartmentIcons

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
//                        horizontal = 15.dp,
//                        vertical = 4.dp
//                    ),
//                shape = MaterialTheme.shapes.medium,
//                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
//            )
            MyCustomCard(containerColor = MaterialTheme.colorScheme.primaryContainer)
            {

                
                ListItem(
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.person), contentDescription = "")},
                    headlineContent = { Text(text = getPersonInfo().name ?: "游客")  },
                    trailingContent = {
                        if(startDate != null && endDate != null && startDate != "" && endDate != "") {
                            Text(text = "已陪伴 ${ReservDecimal.reservDecimal(DateTimeManager.getPercent(startDate,endDate),1)}%")
                        } else { null }
                      //  FilledTonalIconButton(onClick = {
                        //    expandItems = !expandItems
                          //  SharePrefs.SaveBoolean("expandPerson",true,expandItems)
                        //}) {
                          //  Icon(painterResource(id = if(expandItems) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "")
                       // }
                        },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
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
                                ListItem(
                                    overlineContent = { Text(text = "学号") },
                                    headlineContent = {  ScrollText(text = it)  },
                                    leadingContent = {
                                        Icon(
                                            painterResource(R.drawable.tag),
                                            contentDescription = "Localized description",
                                        )
                                    },
                                    modifier = Modifier.weight(0.5f),
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                            }
                        }
                        getPersonInfo().department?.let {
                            Row {
                                ListItem(
                                    overlineContent = { getPersonInfo().school?.let { Text(text = it) } },
                                    leadingContent = { DepartmentIcons(name = it) },
                                    headlineContent = {  ScrollText(text = it)  },
                                    modifier = Modifier.weight(0.5f),
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                            }
                        }
                        getPersonInfo().classes?.let {
                            Row {
                                ListItem(
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
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimesUI() {
   // val startYear = ("20" + getUserInfo().studentID?.substring(0,2)).toIntOrNull()

}

@RequiresApi(Build.VERSION_CODES.O)
fun getUserInfo() : UserInfo {

    val date = DateTimeManager.Date_yyyy_MM_dd
    val time = "${DateTimeManager.formattedTime_Hour}:${DateTimeManager.formattedTime_Minute}:00"
    val dateTime = "$date $time"


    val appVersion = APPVersion.getVersionName()
    val androidSDK = AndroidVersion.sdkInt
    val device = Build.MODEL

    return UserInfo(getPersonInfo().name,getPersonInfo().username,dateTime, appVersionName = appVersion, systemVersion = androidSDK, deviceName = device)
}

