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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.Language
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.parse.SemesterParser.getSemesterWithoutSuspend
import com.hfut.schedule.logic.util.parse.SemesterParser.parseSemester
import com.hfut.schedule.logic.util.parse.SemesterParser.reverseGetSemester
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.file.cleanCache
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveBoolean
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showDevelopingToast
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.CustomSingleChoiceRow
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.DateRangePickerModal
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.media.SimpleVideo
import com.hfut.schedule.ui.component.media.checkOrDownloadVideo
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.cube.Screen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getDefaultStartTerm
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.xah.transition.util.TransitionBackHandler
import com.xah.uicommon.component.slider.CustomSlider
import com.xah.uicommon.component.text.BottomTip
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.RowHorizontal
import com.xah.uicommon.style.padding.InnerPaddingHeight
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* 本kt文件已完成多语言文案适配 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    navController: NavHostController,
    innerPaddings: PaddingValues,
) {
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionBackHandler(navController,enablePredictive) {
        scale = it
    }
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)
        .scale(scale)) {
        Spacer(modifier = Modifier.height(5.dp))

        val controlCenter by DataStoreManager.enableControlCenterGesture.collectAsState(initial = false)
        val enableShowOutOfDateEvent by DataStoreManager.enableShowOutOfDateEvent.collectAsState(initial = false)

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


        val video by produceState<String?>(initialValue = null) {
            scope.launch {
                delay(AppAnimationManager.ANIMATION_SPEED*1L)
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
                        Text(stringResource(R.string.app_settings_control_center_description))
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.flash_on),null)
                    },
                    trailingContent = {
                        Switch(checked = controlCenter, onCheckedChange = { scope.launch { DataStoreManager.saveControlCenter(!controlCenter) } })
                    },
                    modifier = Modifier.clickable {
                        scope.launch { DataStoreManager.saveControlCenter(!controlCenter) }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(stringResource(R.string.app_settings_study_interaction_title)) },
                    supportingContent = {
                        Text(stringResource(R.string.app_settings_study_interaction_description))
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.gesture),null)
                    },
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.GestureStudyScreen.route)
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
                    modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
                ) {
                    scope.launch {
                        DataStoreManager.saveLanguage(it)
                    }
                }

                PaddingHorizontalDivider()

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
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.app_settings_display_overdue_events_on_focus_title)) },
                    supportingContent = {
                        Text(stringResource(R.string.app_settings_display_overdue_events_on_focus_description))
                    },
                    leadingContent = { Icon(
                        painterResource(R.drawable.search_activity),
                        contentDescription = "Localized description"
                    ) },
                    trailingContent = { Switch(checked = enableShowOutOfDateEvent, onCheckedChange = {
                        scope.launch {
                            DataStoreManager.saveShowOutOdDateEvent(!enableShowOutOfDateEvent)
                        }
                    }) },
                    modifier = Modifier.clickable {
                        scope.launch {
                            DataStoreManager.saveShowOutOdDateEvent(!enableShowOutOfDateEvent)
                        }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(
                        stringResource(
                            R.string.app_settings_free_flow_of_school_net_of_xc_campus_title,
                            formatDecimal(freeFeevalue.toDouble(), 0)
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
                            DataStoreManager.saveMaxFlow(formatDecimal(freeFeevalue.toDouble(),0).toInt())
                        }
                    },
                    steps = 37,
                    valueRange = 10f..200f,
                    modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.app_settings_default_calendar_account_title)) },
                    supportingContent = {
                        Text(stringResource(R.string.app_settings_default_calendar_account_description))
                    },
                    leadingContent = { Icon(
                        painterResource(R.drawable.calendar_add_on),
                        contentDescription = "Localized description"
                    ) },
                    modifier = Modifier.clickable { navController.navigate(Screen.CalendarScreen.route) }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.app_settings_auto_fill_captcha_title)) },
                    supportingContent = {
                        Text(text = stringResource(R.string.app_settings_auto_fill_captcha_description))
                    },
                    leadingContent = { Icon(
                        painterResource(R.drawable.center_focus_strong),
                        contentDescription = "Localized description"
                    ) },
                    modifier = Modifier.clickable { navController.navigate(Screen.DownloadScreen.route) }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.app_settings_ai_title)) },
                    supportingContent = {
                        Text(text = stringResource(R.string.app_settings_ai_description))
                    },
                    leadingContent = { Icon(
                        painterResource(R.drawable.wand_stars),
                        contentDescription = "Localized description"
                    ) },
                    modifier = Modifier.clickable { showDevelopingToast() }
                )
            }
        }
        DividerTextExpandedWith(stringResource(R.string.app_settings_widget_half_title)) {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = {
                        Text(stringResource(R.string.app_settings_widget_focus_title))
                    },
                    supportingContent = {
                        Text(stringResource(R.string.app_settings_widget_focus_description))
                    },
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.FocusWidgetSettingsScreen.route)
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.widgets),null)
                    },
                )
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
        DividerTextExpandedWith(stringResource(R.string.app_settings_storage_half_title)) {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(stringResource(R.string.app_settings_backup_and_restore_title)) },
                    leadingContent = { Icon(painterResource(R.drawable.database),null)},
                    supportingContent = {
                        Text(stringResource(R.string.app_settings_backup_and_restore_description))
                    },
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.BackupScreen.route)
                    }
                )
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
            headlineContent = { Text(text = stringResource(R.string.app_settings_current_term_title)) },
            supportingContent = {
                Text(text = parseSemester(if(autoTerm) getSemesterWithoutSuspend() else autoTermValue))
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
                    Icon(Icons.Filled.KeyboardArrowLeft,null)
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
                    Icon(Icons.Filled.KeyboardArrowRight,null)
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

