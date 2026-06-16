package com.hfut.schedule.ui.screen.home.cube.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.Language
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.parse.SemesterParser.getSemesterWithoutSuspend
import com.hfut.schedule.logic.util.parse.SemesterParser.parseSemester
import com.hfut.schedule.logic.util.parse.SemesterParser.reverseGetSemester

import com.hfut.schedule.logic.util.parse.roundOff
import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.storage.file.cleanCache
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.ShowTeacherConfig
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveBoolean
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showDevelopingToast
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.DateRangePickerModal
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.media.SimpleVideo
import com.hfut.schedule.ui.component.media.checkOrDownloadVideo
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.SettingsBackupDestination
import com.hfut.schedule.ui.nav.destination.SettingsCalendarDestination
import com.hfut.schedule.ui.nav.destination.SettingsDeepLinkDestination
import com.hfut.schedule.ui.nav.destination.SettingsFocusCardDestination
import com.hfut.schedule.ui.nav.destination.SettingsFocusWidgetDestination
import com.hfut.schedule.ui.nav.destination.SettingsOcrDestination
import com.hfut.schedule.ui.nav.destination.SettingsShortcutEditDestination
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getDefaultStartTerm
import com.sharednav.common.util.NoneRoundShape
import com.xah.common.ui.component.slider.CustomSlider
import com.xah.common.ui.component.status.CustomSingleChoiceRow
import com.xah.common.ui.component.text.BottomTip
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.RowHorizontal
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.container.component.base.SharedContainer
import com.xah.navigation.util.LocalNavController
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/* 本kt文件已完成多语言文案适配 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationSettingsScreen(innerPaddings: PaddingValues, ) {
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navTopController = LocalNavController.current

    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)
    ) {
        Spacer(modifier = Modifier.height(5.dp))
        val enableInfiniteWheelPicker by DataStoreManager.enableInfiniteWheelPicker.collectAsState(initial = true)
        val controlCenter by DataStoreManager.enableControlCenterGesture.collectAsState(initial = false)
//        val enableShowOutOfDateEvent by DataStoreManager.enableShowOutOfDateEvent.collectAsState(initial = false)

        val switch_update = prefs.getBoolean("SWITCHUPDATE",true)
        var showSUpdate by remember { mutableStateOf(switch_update) }
        saveBoolean("SWITCHUPDATE",true,showSUpdate)

        val switch_show_ended = prefs.getBoolean("SWITCHSHOWENDED",true)
        var showEnded by remember { mutableStateOf(switch_show_ended) }
        saveBoolean("SWITCHSHOWENDED",true,showEnded)

        val scope = rememberCoroutineScope()
        val maxFlow by DataStoreManager.maxFlow.collectAsState(initial = MyApplication.DEFAULT_MAX_FREE_FLOW)
        var freeFeevalue by remember { mutableFloatStateOf(maxFlow.toFloat()) }
        LaunchedEffect(maxFlow) {
            freeFeevalue = maxFlow.toFloat()
        }
        val language by DataStoreManager.language.collectAsState(initial = Language.AUTO.code)


        val navController = LocalNavController.current
        val video by produceState<String?>(initialValue = null) {
            scope.launch {
                navController.awaitTransition()
                value = checkOrDownloadVideo(context,"example_gesture.mp4","https://chiu-xah.github.io/videos/example_gesture.mp4")
            }
        }
        CustomCard (
            modifier = Modifier
                .aspectRatio(16 / 9f)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            video?.let {
                SimpleVideo(
                    filePath = it,
                    aspectRatio = 16/9f,
                )
            }
        }

        DividerTextExpandedWith(stringResource(R.string.app_settings_interaction_half_title)) {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.app_settings_predictive_back_gesture_title)) },
                    supportingContent = {
                        if(AppVersion.CAN_PREDICTIVE) {
                            Text(text = stringResource(R.string.app_settings_predictive_back_gesture_description_supported))
                        } else {
                            Text(text = stringResource(R.string.app_settings_predictive_back_gesture_description_unsupported))
                        }
                    },
                    leadingContent = { Icon(painterResource(R.drawable.swipe_left), contentDescription = "Localized description",) },
                    trailingContent = {
                        Switch(enabled = AppVersion.CAN_PREDICTIVE,checked = enablePredictive, onCheckedChange = { scope.launch { DataStoreManager.savePredict(!enablePredictive) }})
                    },
                    modifier = Modifier.clickable {
                        scope.launch { DataStoreManager.savePredict(!enablePredictive) }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(stringResource(R.string.app_settings_control_center_title)) },
                    supportingContent = {
                        Text("整改升级中，敬请期待")
//                        Text(stringResource(R.string.app_settings_control_center_description))
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.flash_on),null)
                    },
                    trailingContent = {
                        Switch(enabled = false, checked = controlCenter, onCheckedChange = { scope.launch { DataStoreManager.saveControlCenter(!controlCenter) } })
                    },
                    modifier = Modifier.clickable {
//                        scope.launch { DataStoreManager.saveControlCenter(!controlCenter) }
                    }
                )
            }
        }
        DividerTextExpandedWith(stringResource(R.string.app_settings_calendar_half_title)) {
            CalendarSettingsUI(false)
        }
        DividerTextExpandedWith(stringResource(R.string.app_settings_preferences_half_title)) {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.app_settings_language_title)) },
                    leadingContent = { Icon(
                        painterResource(R.drawable.translate),
                        contentDescription = "Localized description"
                    ) },
                )

                CustomSingleChoiceRow<Language> (
                    selected = language,
                    enabled = false,
                    modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
                ) {
                    scope.launch {
                        showDevelopingToast()
                        DataStoreManager.saveLanguage(it)
                    }
                }

                PaddingHorizontalDivider()
                /* TODO 延缓发布，预留
                TransplantListItem(
                    headlineContent = { Text(text = "无限滚轮") },
                    supportingContent = {
                        Text("使滚轮组件可无限滚动，没有起点和终点")
                    },
                    leadingContent = { Icon(
                        painterResource(R.drawable.toll),
                        contentDescription = "Localized description"
                    ) },
                    trailingContent = { Switch(checked = enableInfiniteWheelPicker, onCheckedChange = {
                        scope.launch {
                            DataStoreManager.saveInfiniteWheelPicker(!enableInfiniteWheelPicker)
                        }
                    }) },
                    modifier = Modifier.clickable {
                        scope.launch {
                            DataStoreManager.saveInfiniteWheelPicker(!enableInfiniteWheelPicker)
                        }
                    }
                )
                WheelPicker(
                    data = IntArray(9) { it+1 }.toList(),
                    modifier = Modifier.padding(horizontal =  APP_HORIZONTAL_DP),
                    onSelect = { _,_ -> }
                ) {
                    val description = "选项${it}"
                    Text(description)
                }
                PaddingHorizontalDivider()
                 */

                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.app_settings_display_overdue_courses_on_focus_title)) },
                    supportingContent = {
                        Text(stringResource(R.string.app_settings_display_overdue_courses_on_focus_description))
                    },
                    leadingContent = { Icon(
                        painterResource(R.drawable.search_activity),
                        contentDescription = "Localized description"
                    ) },
                    trailingContent = { Switch(checked = showEnded, onCheckedChange = { ch -> showEnded = ch}) },
                    modifier = Modifier.clickable { showEnded = !showEnded }
                )
//                PaddingHorizontalDivider()
//                TransplantListItem(
//                    headlineContent = { Text(text = stringResource(R.string.app_settings_display_overdue_events_on_focus_title)) },
//                    supportingContent = {
//                        Text(stringResource(R.string.app_settings_display_overdue_events_on_focus_description))
//                    },
//                    leadingContent = { Icon(
//                        painterResource(R.drawable.search_activity),
//                        contentDescription = "Localized description"
//                    ) },
//                    trailingContent = { Switch(checked = enableShowOutOfDateEvent, onCheckedChange = {
//                        scope.launch {
//                            DataStoreManager.saveShowOutOdDateEvent(!enableShowOutOfDateEvent)
//                        }
//                    }) },
//                    modifier = Modifier.clickable {
//                        scope.launch {
//                            DataStoreManager.saveShowOutOdDateEvent(!enableShowOutOfDateEvent)
//                        }
//                    }
//                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(
                        stringResource(
                            R.string.app_settings_free_flow_of_school_net_of_xc_campus_title,
                            freeFeevalue.roundOffString(0)
                        ))},
                    supportingContent = {
                        Text(
                            stringResource(
                                R.string.app_settings_free_flow_of_school_net_of_xc_campus_description,
                                MyApplication.DEFAULT_MAX_FREE_FLOW
                            ))
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.net),null)
                    },
                )
                CustomSlider(
                    value = freeFeevalue,
                    onValueChange = {
                        freeFeevalue = it
                    },
                    onValueChangeFinished = {
                        scope.launch {
                            DataStoreManager.saveMaxFlow(freeFeevalue.roundOff(0).toInt())
                        }
                    },
                    stepPadding = 10f,
                    valueRange = 10f..500f,
                    modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
                )
                PaddingHorizontalDivider()
                SharedContainer(
                    key = SettingsCalendarDestination.key,
                    shape = NoneRoundShape,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        colors = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = stringResource(R.string.app_settings_default_calendar_account_title)) },
                        supportingContent = {
                            Text(stringResource(R.string.app_settings_default_calendar_account_description))
                        },
                        leadingContent = { Icon(
                            painterResource(R.drawable.calendar_add_on),
                            contentDescription = "Localized description"
                        ) },
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsCalendarDestination)
                        }
                    )
                }
                PaddingHorizontalDivider()
                SharedContainer(
                    key = SettingsFocusCardDestination.key,
                    shape = NoneRoundShape,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        colors = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = stringResource(R.string.app_settings_focus_card_title)) },
                        supportingContent = { Text(text = stringResource(R.string.app_settings_focus_card_description)) },
                        leadingContent = { Icon(painterResource(R.drawable.lightbulb), contentDescription = "Localized description",) },
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsFocusCardDestination)
                        }
                    )
                }
                PaddingHorizontalDivider()
                SharedContainer(
                    key = SettingsOcrDestination.key,
                    shape = NoneRoundShape,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        colors = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = stringResource(R.string.app_settings_auto_fill_captcha_title)) },
                        supportingContent = {
                            Text(text = stringResource(R.string.app_settings_auto_fill_captcha_description))
                        },
                        leadingContent = { Icon(
                            painterResource(R.drawable.center_focus_strong),
                            contentDescription = "Localized description"
                        ) },
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsOcrDestination)
                        }
                    )
                }
                PaddingHorizontalDivider()
                SharedContainer(
                    key = SettingsShortcutEditDestination.key,
                    shape = NoneRoundShape,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        colors = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = stringResource(R.string.app_settings_shortcut_title)) },
                        supportingContent = {
                            Text(text = stringResource(R.string.app_settings_shortcut_description))
                        },
                        leadingContent = { Icon(
                            painterResource(R.drawable.keyboard_command_key),
                            contentDescription = "Localized description"
                        ) },
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsShortcutEditDestination)
                        }
                    )
                }
                PaddingHorizontalDivider()
                SharedContainer(
                    key = SettingsDeepLinkDestination.key,
                    shape = MaterialTheme.shapes.medium.copy(
                        topStart = CornerSize(0.dp),
                        topEnd = CornerSize(0.dp),
                    ),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        colors = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = SettingsDeepLinkDestination.title.asString()) },
                        supportingContent = {
                            Text(text = "向外界应用与网页提供跳转能力")
                        },
                        leadingContent = { Icon(
                            painterResource(SettingsDeepLinkDestination.icon),
                            contentDescription = "Localized description"
                        ) },
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsDeepLinkDestination)
                        }
                    )
                }
                /*
                PaddingHorizontalDivider()
                SharedContainer(
                    key = SettingsLiveUpdateDestination.key,
                    shape = MaterialTheme.shapes.medium.copy(
                        topStart = CornerSize(0.dp),
                        topEnd = CornerSize(0.dp),
                    ),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        colors = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = stringResource(R.string.app_settings_live_update_title)) },
                        supportingContent = {
                            if(AppVersion.CAN_LIVE_UPDATE) {
                                Text(text = stringResource(R.string.app_settings_live_update_description_supported))
                            } else {
                                Text(text = stringResource(R.string.app_settings_live_update_description_unsupported))
                            }
                        },
                        leadingContent = { Icon(
                            painterResource(R.drawable.notifications_active),
                            contentDescription = "Localized description"
                        ) },
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsLiveUpdateDestination)
                        }
                    )
                }
                 */
            }
        }
        DividerTextExpandedWith(stringResource(R.string.app_settings_storage_half_title)) {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                SharedContainer(
                    key = SettingsBackupDestination.key,
                    shape = MaterialTheme.shapes.medium.copy(
                        bottomEnd = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp),
                    ),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        colors = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(stringResource(R.string.app_settings_backup_and_restore_title)) },
                        leadingContent = { Icon(painterResource(R.drawable.database),null)},
                        supportingContent = {
                            Text(stringResource(R.string.app_settings_backup_and_restore_description))
                        },
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsBackupDestination)
                        }
                    )
                }
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(stringResource(R.string.app_settings_clear_cache_title)) },
                    leadingContent = { Icon(painterResource(R.drawable.mop),null)},
                    supportingContent = {
                        Text(stringResource(R.string.app_settings_clear_cache_description))
                    },
                    modifier = Modifier.clickable {
                        scope.launch {
                            val result = async { cleanCache(context) }.await()
                            showToast(
                                context.getString(R.string.app_settings_toast_clear_cache_done, result)
                            )
                        }
                    }
                )
            }
        }
        DividerTextExpandedWith(stringResource(R.string.app_settings_widget_half_title)) {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                SharedContainer(
                    key = SettingsFocusWidgetDestination.key,
                    shape = MaterialTheme.shapes.medium.copy(
                        bottomStart = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp),
                    ),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        colors = MaterialTheme.colorScheme.surface,
                        headlineContent = {
                            Text(stringResource(R.string.app_settings_widget_focus_title))
                        },
                        supportingContent = {
                            Text(stringResource(R.string.app_settings_widget_focus_description))
                        },
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsFocusWidgetDestination)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.widgets),null)
                        },
                    )
                }

                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = {
                        Text(stringResource(R.string.app_settings_widget_data_title))
                    },
                    supportingContent = {
                        Text(stringResource(R.string.app_settings_widget_data_description))
                    },
                    modifier = Modifier.clickable {
                        showDevelopingToast()
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.widgets),null)
                    },
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = {
                        Text(stringResource(R.string.app_settings_school_net_title))
                    },
                    supportingContent = {
                        Text(stringResource(R.string.app_settings_school_net_description))
                    },
                    modifier = Modifier.clickable {
                        showDevelopingToast()
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.widgets),null)
                    },
                )
            }
        }
        InnerPaddingHeight(innerPaddings,false)
    }
}
@Composable
fun CalendarSettingsUI(
    isInBottomSheet : Boolean ,
) {
    val containerColor = if(isInBottomSheet) cardNormalColor() else MaterialTheme.colorScheme.surface
    val autoTerm by DataStoreManager.enableAutoTerm.collectAsState(initial = true)
    val defaultCalendar by DataStoreManager.defaultCalendar.collectAsState(initial = CourseType.JXGLSTU.code)
    val autoTermValue by DataStoreManager.customTermValue.collectAsState(initial = getSemesterWithoutSuspend())
    val enableMergeSquare by DataStoreManager.enableMergeSquare.collectAsState(initial = false)
    val enableCalendarShowTeacher by DataStoreManager.enableCalendarShowTeacher.collectAsState(initial = ShowTeacherConfig.ONLY_MULTI.code)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val defaultStartDate = remember { getDefaultStartTerm() }
    val termStartDate by DataStoreManager.termStartDate.collectAsState(initial = defaultStartDate)
    var showSelectDateDialog by remember { mutableStateOf(false) }
    if(showSelectDateDialog)
        DateRangePickerModal(
            text = "",
            onSelected = {
                scope.launch {
                    DataStoreManager.saveTermStartDate(it.second)
                }
            },
            allowSelectPrevious = true
        ) { showSelectDateDialog = false }


    CustomCard(color = containerColor) {
        if(!isInBottomSheet) {
            TransplantListItem(
                headlineContent = { Text(text = stringResource(R.string.app_settings_default_calendar_title)) },
                supportingContent = {
                    Text(text =
                        if(defaultCalendar == CourseType.COMMUNITY.code)
                            stringResource(R.string.app_settings_default_calendar_community_description)
                        else if(defaultCalendar == CourseType.UNI_APP.code)
                            stringResource(R.string.app_settings_default_calendar_uni_app_description)
                        else
                            stringResource(R.string.app_settings_default_calendar_jxglstu_description)
                    )
                },
                leadingContent = { Icon(
                    painterResource(R.drawable.calendar),
                    contentDescription = "Localized description"
                ) },
            )

            val options = remember {
                listOf(
                    CourseType.UNI_APP,
                    CourseType.JXGLSTU,
                    CourseType.COMMUNITY
                )
            }

            CustomSingleChoiceRow(
                options = options,
                selected = defaultCalendar,
                modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
            ) {
                scope.launch {
                    DataStoreManager.saveDefaultCalendar(it)
                }
            }
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = {
                    Text(stringResource(R.string.app_settings_display_teachers_title))
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.group), null)
                },
            )
            CustomSingleChoiceRow<ShowTeacherConfig>(
                selected = enableCalendarShowTeacher,
                modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
            ) {
                scope.launch {
                    DataStoreManager.saveCalendarShowTeacher(it)
                }
            }

            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = {
                    Text(stringResource(R.string.app_settings_merge_conflict_calendar_squares_title))
                },
                supportingContent = {
                    Text(stringResource(R.string.app_settings_merge_conflict_calendar_square_description))
                },
                modifier = Modifier.clickable {
                    scope.launch {
                        DataStoreManager.saveMergeSquare(!enableMergeSquare)
                    }
                },
                trailingContent = {
                    Switch(checked = enableMergeSquare, onCheckedChange = {
                        scope.launch {
                            DataStoreManager.saveMergeSquare(!enableMergeSquare)
                        }
                    })
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.arrow_split), null)
                },
            )
            PaddingHorizontalDivider()
        }
        TransplantListItem(
            headlineContent = { Text(text = stringResource(R.string.app_settings_current_term_title)) },
            supportingContent = {
                Text(text = parseSemester(if(autoTerm) getSemesterWithoutSuspend() else autoTermValue)!!)
            },
            modifier = Modifier.clickable {
                showToast(context.getString(R.string.app_settings_toast_change_current_term_unsupported))
            }
        )
        TransplantListItem(
            headlineContent = { Text(text = stringResource(R.string.app_settings_auto_calculate_term_title)) },
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.approval), contentDescription = "")
            },
            supportingContent = {
                Text(text = stringResource(R.string.app_settings_auto_calculate_term_description))
            },
            trailingContent = {
                Switch(checked = autoTerm, onCheckedChange = { scope.launch { DataStoreManager.saveAutoTerm(!autoTerm) }})
            },
            modifier = Modifier.clickable {
                scope.launch { DataStoreManager.saveAutoTerm(!autoTerm) }
            }
        )
        if(!autoTerm) {
            RowHorizontal {
                FilledTonalButton (
                    onClick = { scope.launch {
                        if(autoTermValue >= 0) {
                            DataStoreManager.saveAutoTermValue(autoTermValue-20)
                        }
                    } }
                ) {
                    Icon(painterResource(R.drawable.keyboard_arrow_left),null)
                }
                Spacer(Modifier.width(APP_HORIZONTAL_DP))
                FilledTonalButton(
                    onClick = { scope.launch {
                        reverseGetSemester(DateTimeManager.Date_yyyy_MM)?.let { DataStoreManager.saveAutoTermValue(it) }
                    } }
                ) {
                    Icon(painterResource(R.drawable.refresh),null)
                }
                Spacer(Modifier.width(APP_HORIZONTAL_DP))
                FilledTonalButton(
                    onClick = { scope.launch {
                        DataStoreManager.saveAutoTermValue(autoTermValue+20)
                    } }
                ) {
                    Icon(painterResource(R.drawable.keyboard_arrow_right),null)
                }
            }
            Spacer(Modifier.height(APP_HORIZONTAL_DP))
        }
        PaddingHorizontalDivider()
        TransplantListItem(
            headlineContent = { Text(text = stringResource(R.string.app_settings_start_date_of_term_title)) },
            supportingContent = {
                Column {
                    Text(text = termStartDate, fontWeight = FontWeight.Bold)
                    Text(text = stringResource(R.string.app_settings_start_date_of_term_description))
                }
            },
            trailingContent = {
                FilledTonalIconButton(
                    onClick = {
                        scope.launch {
                            DataStoreManager.saveTermStartDate(defaultStartDate)
                        }
                    }
                ) {
                    Icon(painterResource(R.drawable.rotate_right),null)
                }
            },
            leadingContent = {
                Icon(painterResource(R.drawable.start), contentDescription = "Localized description")
            },
            modifier = Modifier.clickable {
                showSelectDateDialog = true
            }
        )
    }
    if(isInBottomSheet) {
        BottomTip(stringResource(R.string.app_settings_tips_start_date_of_term))
    }
}

