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
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.nav.destination.PersonInfoDestination
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.align.ColumnVertical
import com.xah.container.component.base.sharedContainer
import com.xah.navigation.util.LocalNavController

/* 本kt文件已完成多语言文案适配 */
@Composable
fun PersonPart() {
    var expandItems by rememberSaveable { mutableStateOf(false) }
    val startDate = remember { getPersonInfo().startDate }
    val endDate = remember { getPersonInfo().endDate }
    val navController = LocalNavController.current
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Column {
            CustomCard(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = NoneRoundShape,
                modifier = Modifier.sharedContainer(
                    PersonInfoDestination.key,
                    MaterialTheme.shapes.medium,
                    MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                TransplantListItem(
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.person), contentDescription = "")},
                    headlineContent = { Text(text = getPersonInfo().getNameFinally())  },
                    trailingContent = {
                        Row {
                            ColumnVertical {
                                if(startDate != null && endDate != null && startDate != "" && endDate != "") {
                                    val percent = DateTimeManager.getPercent(startDate,endDate)
                                    val str = if(percent in 0.0 ..< 1.0) {
                                        "已过 ${(percent*100).roundOffString(1)}%"
                                    } else if(percent < 0f) {
                                        "待入学"
                                    } else {
                                        "已毕业"
                                    }
                                    Text(text = str)
                                } else { null }
                            }
                        }
                    },
                    modifier = Modifier.combinedClickable(
                        onLongClick = {
                            expandItems = !expandItems
                        },
                        onClick = {
                            navController.push(PersonInfoDestination)
                        },
                        onDoubleClick = {
                            showToast("长按展开，单击进入个人信息页面")
                        }
                    )
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
                        getPersonInfo().getStudentIdFinally()?.let{
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

