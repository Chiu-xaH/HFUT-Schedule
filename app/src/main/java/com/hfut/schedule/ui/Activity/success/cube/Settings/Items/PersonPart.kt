package com.hfut.schedule.ui.Activity.success.cube.Settings.Items

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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.MonetColor.MonetUI
import com.hfut.schedule.ui.UIUtils.ScrollText
import kotlinx.coroutines.delay
import org.jsoup.Jsoup

@Composable
fun PersonPart() {


    val info = SharePrefs.prefs.getString("info","")


    val doc = info?.let { Jsoup.parse(it) }
    val studentnumber = doc?.select("li.list-group-item.text-right:contains(学号) span")?.last()?.text()
    val name = doc?.select("li.list-group-item.text-right:contains(中文姓名) span")?.last()?.text()
  //  val chineseid = doc?.select("li.list-group-item.text-right:contains(证件号) span")?.last()?.text()
    val elements = doc?.select("dl dt, dl dd")

    val infoMap = mutableMapOf<String, String>()
    if (elements != null) {
        for (i in 0 until elements.size step 2) {
            val key = elements[i].text()
            val value = elements[i+1].text()
            infoMap[key] = value
        }
    }

    //SharePrefs.Save("ChineseId",chineseid)

    //val benorsshuo =infoMap[elements?.get(8)?.text()]
    val yuanxi =infoMap[elements?.get(10)?.text()]
    val zhuanye =infoMap[elements?.get(12)?.text()]
    val classes =infoMap[elements?.get(16)?.text()]
    val school =infoMap[elements?.get(18)?.text()]
    //val home =infoMap[elements?.get(80)?.text()]


    var expandItems by remember { mutableStateOf(prefs.getBoolean("expandPerson",true)) }



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
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
            ){

                
                ListItem(
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.person), contentDescription = "")},
                    headlineContent = { name?.let { Text(text = it) } },
                    trailingContent = { Icon(painterResource(id = if(expandItems) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "")},
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable {
                        expandItems = !expandItems
                        SharePrefs.SaveBoolean("expandPerson",true,expandItems)
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
                        Row {
                            ListItem(
                                overlineContent = { Text(text = "学号") },
                                headlineContent = { studentnumber?.let { ScrollText(text = it) } },
                                leadingContent = {
                                    Icon(
                                        painterResource(R.drawable.badge),
                                        contentDescription = "Localized description",
                                    )
                                },
                                modifier = Modifier.weight(0.5f),
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                            ListItem(
                                overlineContent = { Text(text = "班级") },
                                leadingContent = {
                                    Icon(
                                        painterResource(R.drawable.sensor_door),
                                        contentDescription = "Localized description",
                                    )
                                },
                                headlineContent = { classes?.let { ScrollText(text = it)} },
                                modifier = Modifier.weight(0.5f),
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                        Row {
                            ListItem(
                                overlineContent = { Text(text = "校区") },
                                leadingContent = {
                                    Icon(
                                        painterResource(R.drawable.location_city),
                                        contentDescription = "Localized description",
                                    )
                                },
                                headlineContent = { school?.let { ScrollText(text = it) } },
                                modifier = Modifier.weight(0.5f),
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                            ListItem(
                                overlineContent = { Text(text = "院系") },
                                leadingContent = {
                                    Icon(
                                        painterResource(R.drawable.local_library),
                                        contentDescription = "Localized description",
                                    )
                                },
                                headlineContent = { yuanxi?.let { ScrollText(text = it) } },
                                modifier = Modifier.weight(0.5f),
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                        Row {
                            ListItem(
                                overlineContent = { Text(text = "专业") },
                                leadingContent = {
                                    Icon(
                                        painterResource(R.drawable.square_foot),
                                        contentDescription = "Localized description",
                                    )
                                },
                                headlineContent = { zhuanye?.let { ScrollText(text = it) } }
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


