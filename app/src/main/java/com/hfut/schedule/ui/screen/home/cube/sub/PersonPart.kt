package com.hfut.schedule.ui.screen.home.cube.sub

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
import androidx.compose.ui.res.stringResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.align.ColumnVertical

/* 本kt文件已完成多语言文案适配 */
@Composable
fun PersonPart() {
    var expandItems by remember { mutableStateOf(prefs.getBoolean("expandPerson",false)) }
    val startDate = getPersonInfo().startDate
    val endDate = getPersonInfo().endDate
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Column() {
            CustomCard(color = MaterialTheme.colorScheme.secondaryContainer) {
                TransplantListItem(
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.person), contentDescription = "")},
                    headlineContent = { Text(text = getPersonInfo().name ?: stringResource(R.string.settings_person_info_default_name))  },
                    trailingContent = {
                        Row {
                            ColumnVertical {
                                if(AppVersion.isInDebugRunning()) {
                                    Text(stringResource(R.string.settings_person_info_tag_debug))
                                }
                                if(AppVersion.isPreview()) {
                                    Text(stringResource(R.string.settings_person_info_tag_preview))
                                }
                                if(startDate != null && endDate != null && startDate != "" && endDate != "") {
                                    Text(text = stringResource(
                                        R.string.settings_person_info_tag_normal,
                                        formatDecimal(
                                            DateTimeManager.getPercent(
                                                startDate,
                                                endDate
                                            ), 1
                                        )
                                    ))
                                } else { null }
                            }
                        }
                    },
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
                        getPersonInfo().studentId?.let{
                            Row {
                                TransplantListItem(
                                    overlineContent = { Text(text = stringResource(R.string.settings_person_info_student_id_description)) },
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
                                    overlineContent = { getPersonInfo().campus?.let { Text(text = it) } },
                                    leadingContent = { DepartmentIcons(name = it) },
                                    headlineContent = {  ScrollText(text = it)  },
                                    modifier = Modifier.weight(0.5f),
//                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                            }
                        }
                        getPersonInfo().className?.let {
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

