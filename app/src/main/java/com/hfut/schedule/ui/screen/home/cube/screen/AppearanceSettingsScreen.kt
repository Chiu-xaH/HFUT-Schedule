package com.hfut.schedule.ui.screen.home.cube.screen

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.ShowTeacherConfig
import com.hfut.schedule.logic.util.sys.ClipBoardHelper
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.CustomSingleChoiceRow
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.media.SimpleVideo
import com.hfut.schedule.ui.component.media.checkOrDownloadVideo
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.home.cube.sub.AnimationSetting
import com.hfut.schedule.ui.util.color.ColorMode
import com.hfut.schedule.ui.util.color.ColorStyle
import com.hfut.schedule.ui.util.color.extractColor
import com.hfut.schedule.ui.util.color.hsvToLong
import com.hfut.schedule.ui.util.color.longToHexColor
import com.hfut.schedule.ui.util.color.longToHue
import com.hfut.schedule.ui.util.color.parseColor
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.xah.mirror.shader.scaleMirror
import com.xah.mirror.style.mask
import com.xah.transition.state.TransitionConfig
import com.xah.transition.style.TransitionLevel
import com.xah.transition.util.TransitionBackHandler
import com.xah.uicommon.component.slider.CustomSlider
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.ColumnVertical
import com.xah.uicommon.style.align.RowHorizontal
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/* 本kt文件已完成多语言文案适配 */
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AppearanceSettingsScreen(innerPaddings : PaddingValues, navController : NavHostController) {
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)

    var scale by remember { mutableFloatStateOf(1f) }
    TransitionBackHandler(navController,enablePredictive) {
        scale = it
    }
    SharedAppearanceSettingsScreen(
        Modifier
            .verticalScroll(rememberScrollState())
            .scale(scale),
        innerPaddings,
        false
    )
}

private suspend fun persistImage(context: Context, uri: Uri): String? {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val file = File(context.filesDir, "custom_background_${System.currentTimeMillis()}.jpg")
            inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            LogUtil.error(e)
            null
        }
    }
}

private suspend fun deleteCustomBackground(context: Context) = withContext(Dispatchers.IO) {
    try {
        // 清空 DataStore 记录
        DataStoreManager.saveCustomBackground(null)
        // 批量删除所有以 custom_background_ 开头的文件
        context.filesDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("custom_background_")) {
                file.delete()
            }
        }
    } catch (e: Exception) {
        LogUtil.error(e)
    }
}



@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun SharedAppearanceSettingsScreen(modifier : Modifier = Modifier, innerPaddings: PaddingValues, isControlCenter : Boolean ) {
    val backgroundColor =  if(isControlCenter) {
        MaterialTheme.colorScheme.surface.copy(1- MyApplication.CONTROL_CENTER_BACKGROUND_MASK_ALPHA)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if(isControlCenter) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.primary
    }
    val styleList = remember { ColorStyle.entries }
    Column(modifier = modifier) {
        if(!isControlCenter) {
            InnerPaddingHeight(innerPaddings,true)
        }

        val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)

        val webViewDark by DataStoreManager.enableForceWebViewDark.collectAsState(initial = true)
        val currentPureDark by DataStoreManager.enablePureDark.collectAsState(initial = false)
        val motionBlur by DataStoreManager.enableMotionBlur.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)
        val transition by DataStoreManager.transitionLevel.collectAsState(initial = TransitionLevel.MEDIUM.code)
        val currentColorModeIndex by DataStoreManager.colorMode.collectAsState(initial = ColorMode.AUTO.code)
        val customColor by DataStoreManager.customColor.collectAsState(initial = -1L)
        val customColorStyle by DataStoreManager.customColorStyle.collectAsState(initial = ColorStyle.DEFAULT.code)
        val showBottomBarLabel by DataStoreManager.showBottomBarLabel.collectAsState(initial = true)
        val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)
        val enableCameraDynamicRecord by DataStoreManager.enableCameraDynamicRecord.collectAsState(initial = false)

        LaunchedEffect(enableLiquidGlass) {
            TransitionConfig.enableMirror = enableLiquidGlass
        }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current


        val pickMultipleMediaForColor = rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let { imageUri ->
                scope.launch {
                    extractColor(imageUri)?.let {
                        DataStoreManager.saveCustomColor(it)
                    }
                }
            }
        }
        val transitionLevels = remember { TransitionLevel.entries }

        LaunchedEffect(transition) {
            TransitionConfig.transitionBackgroundStyle.level = transitionLevels.find { it.code == transition } ?: TransitionLevel.NONE
        }
        val useDynamicColor = customColor == -1L
        var hue by remember { mutableFloatStateOf(180f) }
        LaunchedEffect(customColor) {
            hue = customColor.let {
                if(useDynamicColor) {
                    180f
                } else {
                    longToHue(it)
                }
            }
        }
        var showColorDialog by remember { mutableStateOf(false) }
        if(showColorDialog) {
            Dialog(
                onDismissRequest = { showColorDialog = false }
            ) {
                var input by remember { mutableStateOf(longToHexColor(customColor)) }
                val parse = parseColor(input)
                Column(modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
                    .padding(vertical = APP_HORIZONTAL_DP)) {
                    CustomTextField(
                        label = { Text(stringResource(R.string.appearance_settings_tips_input_color)) },
                        input = input,
                        trailingIcon = {
                            parse?.let {
                                FilledTonalIconButton(
                                    onClick = {  },
                                    colors = IconButtonDefaults. filledTonalIconButtonColors(containerColor = Color(it) )
                                ) { }
                            }
                        }
                    ) { input = it }
                    Spacer(Modifier.height(APP_HORIZONTAL_DP/2))
                    Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    DataStoreManager.saveCustomColor(parse!!)
                                    showColorDialog = false
                                }
                            },
                            enabled = parse != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f)
                        ) {
                            Text(stringResource(R.string.appearance_settings_button_input_color_set))
                        }
                        Spacer(Modifier.width(APP_HORIZONTAL_DP))
                        FilledTonalButton (
                            onClick = {
                                showColorDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f)
                        ) {
                            Text(stringResource(R.string.appearance_settings_button_input_color_cancel))
                        }
                    }

                }
            }
        }

        if(!isControlCenter) {
            val video by produceState<String?>(initialValue = null) {
                scope.launch {
                    delay(AppAnimationManager.ANIMATION_SPEED*1L)
                    value = checkOrDownloadVideo(context,"example_color.mp4","https://chiu-xah.github.io/videos/example_color.mp4")
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
        }


        DividerTextExpandedWith(stringResource(R.string.appearance_settings_theme_half_title),contentColor=contentColor) {
            CustomCard(color = backgroundColor) {
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.appearance_settings_pure_black_background_title)) },
                    supportingContent = { Text(text = stringResource(R.string.appearance_settings_pure_black_background_description)) },
                    leadingContent = { Icon(painterResource(R.drawable.contrast), contentDescription = "Localized description") },
                    trailingContent = {
                        Switch(checked = currentPureDark, onCheckedChange = { scope.launch { DataStoreManager.savePureDark(!currentPureDark) } })
                    },
                    modifier = Modifier.clickable {
                        scope.launch { DataStoreManager.savePureDark(!currentPureDark) }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.appearance_settings_enforce_web_page_dark_theme_title)) },
                    supportingContent = { Text(text = stringResource(R.string.appearance_settings_enforce_web_page_dark_theme_description)) },
                    leadingContent = { Icon(painterResource(R.drawable.syringe), contentDescription = "Localized description") },
                    trailingContent = {
                        Switch(checked = webViewDark, onCheckedChange = { scope.launch { DataStoreManager.saveWebViewDark(!webViewDark) } })
                    },
                    modifier = Modifier.clickable {
                        scope.launch { DataStoreManager.saveWebViewDark(!webViewDark) }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.appearance_settings_theme_title)) },
                    leadingContent = { Icon(painterResource(
                        when(currentColorModeIndex) {
                            ColorMode.DARK.code -> R.drawable.dark_mode
                            ColorMode.LIGHT.code -> R.drawable.light_mode
                            else -> R.drawable.routine
                        }
                    ), contentDescription = "Localized description",) },
                )
                CustomSingleChoiceRow<ColorMode>(
                    selected = currentColorModeIndex,
                    modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP)
                ) {
                    scope.launch {
                        DataStoreManager.saveColorMode(it)
                    }
                }
            }
        }
        DividerTextExpandedWith(stringResource(R.string.appearance_settings_color_half_title),contentColor=contentColor) {
            CustomCard(color = backgroundColor) {
                Colors(isControlCenter)
                DividerTextExpandedWith(stringResource(R.string.appearance_settings_default_color_half_title)) {
                    TransplantListItem(
                        headlineContent = { Text(text = stringResource(R.string.appearance_settings_dynamic_color_title)) },
                        leadingContent = { Icon(painterResource(R.drawable.palette), contentDescription = "Localized description") },
                        trailingContent = {
                            if(useDynamicColor) {
                                Icon(painterResource(R.drawable.check),null)
                            }
                        },
                        modifier = Modifier.clickable {
                            scope.launch {
                                DataStoreManager.saveCustomColor(-1L)
                            }
                        }
                    )
                }
                DividerTextExpandedWith(stringResource(R.string.appearance_settings_pick_color_half_title)) {
                    if(!useDynamicColor) {
                        val d = styleList.find { it.code == customColorStyle }?.description?.asString()
                        TransplantListItem(
                            headlineContent = {
                                d?.let {
                                    Text(text = stringResource(
                                        R.string.appearance_settings_color_bright_title,
                                        it
                                    ))
                                }
                            },
                            leadingContent = { Icon(painterResource(R.drawable.invert_colors), contentDescription = "Localized description") },
                        )

                        CustomSlider(
                            value = customColorStyle.toFloat(),
                            onValueChange = { value ->
                                scope.launch {
                                    val target = styleList.find { it.code == formatDecimal(value.toDouble(),0).toInt() }
                                        ?: return@launch
                                    DataStoreManager.saveCustomColorStyle(target)
                                }
                            },
                            modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
                            steps = 2,
                            valueRange = 0f..3f,
                            showProcessText = true,
                            processText = d
                        )
                        PaddingHorizontalDivider()
                    }
                    TransplantListItem(
                        headlineContent = { Text(text = stringResource(R.string.appearance_settings_pick_color_by_photo_title)) },
                        leadingContent = { Icon(painterResource(R.drawable.image), contentDescription = "Localized description") },

                        modifier = Modifier.clickable {
                            scope.launch {
                                pickMultipleMediaForColor.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        }
                    )
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text(text = stringResource(R.string.appearance_settings_pick_color_by_hue_band_title)) },
                        leadingContent = { Icon(painterResource(R.drawable.colorize), contentDescription = "Localized description") },
                        supportingContent = {
                            Text(stringResource(R.string.appearance_settings_pick_color_by_hue_band_description))
                        },
                        trailingContent = {
                            if(!useDynamicColor) {
                                val color = hsvToLong(hue,1f,1f)
                                val text = longToHexColor(color)
                                ColumnVertical {
                                    FilledTonalIconButton(
                                        onClick = { ClipBoardHelper.copy(text) },
                                        colors = IconButtonDefaults. filledTonalIconButtonColors(containerColor = Color(color) )
                                    ) { }
                                    Text(text)
                                }
                            }
                        },
                        modifier = Modifier.clickable {
                            showColorDialog = true
                        }
                    )
                    Slider(
                        value = hue,
                        onValueChange = {
                            hue = it
                            val color =  hsvToLong(hue,1f,1f)
                            scope.launch {
                                DataStoreManager.saveCustomColor(color)
                            }
                        },
                        valueRange = 0f..360f,
                        colors = SliderDefaults.colors(
                            thumbColor = if(useDynamicColor) Color.Transparent else MaterialTheme.colorScheme.outline,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .padding(bottom = APP_HORIZONTAL_DP)
                            .height(APP_HORIZONTAL_DP * 2)
                            .drawBehind {
                                val trackHeight = (APP_HORIZONTAL_DP * 2 - 5.dp).toPx() // 色相带高度
                                val centerY = size.height / 2f // thumb 居中
                                val top = centerY - trackHeight / 2f
                                val rect = Rect(0f, top, size.width, top + trackHeight)

                                // 在滑轨上绘制渐变（色相带）
                                val colors =
                                    (0..360 step 10).map { hue -> Color.hsv(hue.toFloat(), 1f, 1f) }
                                val gradient = Brush.horizontalGradient(colors = colors)
                                drawRoundRect(
                                    brush = gradient,
                                    size = rect.size,
                                    topLeft = rect.topLeft,
                                    cornerRadius = CornerRadius(APP_HORIZONTAL_DP.value)
                                )
                            }
                    )
                }
            }
        }
        DividerTextExpandedWith(stringResource(R.string.appearance_settings_effect_half_title),contentColor=contentColor) {
            CustomCard(color = backgroundColor) {
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.appearance_settings_motion_blur_title)) },
                    supportingContent = {
                        Text(text = stringResource(
                            R.string.appearance_settings_motion_blur_description_supported,
                            if (!AppVersion.CAN_MOTION_BLUR) stringResource(R.string.appearance_settings_motion_blur_description_unsupported) else ""
                        ))
                    },
                    leadingContent = { MotionBlurIcon(motionBlur) },
                    trailingContent = {  Switch(checked = motionBlur, onCheckedChange = { scope.launch { DataStoreManager.saveMotionBlur(!motionBlur) } },enabled = AppVersion.CAN_MOTION_BLUR) },
                    modifier = Modifier.clickable {
                        if(AppVersion.CAN_MOTION_BLUR) {
                            scope.launch { DataStoreManager.saveMotionBlur(!motionBlur) }
                        }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = {
                        Text(text = stringResource(R.string.appearance_settings_layer_blur_title))
                    },
                    supportingContent = {
                        Text(text = stringResource(R.string.appearance_settings_layer_blur_description))
                    },
                    leadingContent = {
                        HazeBlurIcon(blur)
                    },
                    modifier = Modifier.clickable {
                        scope.launch { DataStoreManager.saveHazeBlur(!blur) }
                    },
                    trailingContent = {  Switch(checked = blur, onCheckedChange = { scope.launch { DataStoreManager.saveHazeBlur(!blur) } }) },
               )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = {
                        Text(text = stringResource(R.string.appearance_settings_shader_title))
                    },
                    supportingContent = {
                        Text(text = stringResource(R.string.appearance_settings_shader_description_supported) + (
                                if(!AppVersion.CAN_SHADER)
                                    stringResource(R.string.appearance_settings_shader_description_unsupported)
                                else
                                    ""
                        ) )
                    },
                    leadingContent = {
                        ShaderIcon(enableLiquidGlass)
                    },
                    trailingContent = {
                        Switch(checked = enableLiquidGlass, enabled = AppVersion.CAN_SHADER, onCheckedChange = {
                            scope.launch {
                                DataStoreManager.saveLiquidGlass(!enableLiquidGlass)
                            }
                        })
                    },
                    modifier = Modifier.clickable {
                        if(AppVersion.CAN_SHADER) {
                            scope.launch {
                                DataStoreManager.saveLiquidGlass(!enableLiquidGlass)
                            }
                        }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = {
                        Text(text = stringResource(R.string.appearance_settings_camera_render_real_time_title))
                    },
                    supportingContent = {
                        Text(text = stringResource(R.string.appearance_settings_camera_render_real_time_description))
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.monochrome_photos),null)
                    },
                    trailingContent = {
                        Switch(checked = enableCameraDynamicRecord, onCheckedChange = {
                            scope.launch {
                                DataStoreManager.saveCameraDynamicRecord(!enableCameraDynamicRecord)
                            }
                        })
                    },
                    modifier = Modifier.clickable {
                        scope.launch {
                            DataStoreManager.saveCameraDynamicRecord(!enableCameraDynamicRecord)
                        }
                    }
                )
            }
        }
        DividerTextExpandedWith(stringResource(R.string.appearance_settings_dynamic_effect_half_title),contentColor=contentColor) {
            CustomCard(color = backgroundColor) {
                TransplantListItem(
                    headlineContent = {
                        Column {
                            Text(text = stringResource(
                                R.string.appearance_settings_transition_level_title,
                                transition,
                                transitionLevels.find { it.code == transition }?.title ?: ""
                            ))
                        }
                    },
                    supportingContent = {
                        Text(text = stringResource(R.string.appearance_settings_transition_level_description))
                    },
                    leadingContent = { Icon(painterResource(R.drawable.airline_stops), contentDescription = "Localized description") },
                )
                CustomSlider(
                    value = transition.toFloat(),
                    onValueChange = { value ->
                        val level = transitionLevels.find { it.code == value.toInt() } ?: return@CustomSlider
                        scope.launch { DataStoreManager.saveTransition(level) }
                    },
                    steps = transitionLevels.size-2,
                    modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
                    valueRange = 0f..(transitionLevels.size-1).toFloat(),
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.appearance_settings_transition_bottom_bar_title)) },
                    supportingContent = {
                        Text(stringResource(R.string.appearance_settings_transition_bottom_bar_description))
                    },
                    leadingContent = { Icon(painterResource(R.drawable.animation), contentDescription = "Localized description") },
                )
                AnimationSetting()
                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
            }
        }
        DividerTextExpandedWith(stringResource(R.string.appearance_settings_calendar_half_title),contentColor=contentColor) {
            CustomCard(color = backgroundColor) {
                CalendarUISettings()
            }
        }
        DividerTextExpandedWith(stringResource(R.string.appearance_settings_bottom_bar_half_title),contentColor=contentColor) {
            CustomCard(color = backgroundColor) {
                TransplantListItem(
                    headlineContent = {
                        Text(stringResource(R.string.appearance_settings_always_display_bottom_bar_labels_title))
                    },
                    supportingContent = {
                        Text(stringResource(R.string.appearance_settings_always_display_bottom_bar_labels_description))
                    },
                    modifier = Modifier.clickable {
                        scope.launch { DataStoreManager.saveShowBottomBarLabel(!showBottomBarLabel) }
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.label),null)
                    },
                    trailingContent = {  Switch(checked = showBottomBarLabel, onCheckedChange = { scope.launch { DataStoreManager.saveShowBottomBarLabel(!showBottomBarLabel) } }) },
                )
            }
        }

        if(!isControlCenter) {
            InnerPaddingHeight(innerPaddings,false)
        } else {
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

@Composable
fun CalendarUISettings(
    tiny : Boolean  = false
) {
    val calendarSquareHeight by DataStoreManager.calendarSquareHeight.collectAsState(initial = MyApplication.CALENDAR_SQUARE_HEIGHT)
    val calendarSquareHeightNew by DataStoreManager.calendarSquareHeightNew.collectAsState(initial = MyApplication.CALENDAR_SQUARE_HEIGHT_NEW)
    val calendarSquareTextSize by DataStoreManager.calendarSquareTextSize.collectAsState(initial = 1f)
    val calendarSquareTextPadding by DataStoreManager.calendarSquareTextPadding.collectAsState(initial = MyApplication.CALENDAR_SQUARE_TEXT_PADDING)
    val enableMergeSquare by DataStoreManager.enableMergeSquare.collectAsState(initial = false)
    val enableCalendarShowTeacher by DataStoreManager.enableCalendarShowTeacher.collectAsState(initial = ShowTeacherConfig.ONLY_MULTI.code)
    val customBackground by DataStoreManager.customBackground.collectAsState(initial = "")
    val customSquareAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = MyApplication.CALENDAR_SQUARE_ALPHA)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val useCustomBackground = customBackground != ""
    val pickMultipleMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { imageUri ->
            scope.launch {
                deleteCustomBackground(context)
                val savedPath = persistImage(context, imageUri)
                savedPath?.let {
                    DataStoreManager.saveCustomBackground(it)
                    showToast(context.getString(R.string.appearance_settings_toast_set_calendar_background))
                    extractColor(imageUri)?.let { color ->
                        DataStoreManager.saveCustomColor(color)
                    }
                }
            }
        }
    }


    Column {
        if(!tiny){
            TransplantListItem(
                headlineContent = {
                    Text(stringResource(R.string.appearance_settings_display_teachers_title))
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
                    Text(stringResource(R.string.appearance_settings_merge_conflict_calendar_squares_title))
                },
                supportingContent = {
                    if (!tiny)
                        Text(stringResource(R.string.appearance_settings_merge_conflict_calendar_square_description))
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
        }
        if(!tiny)
            PaddingHorizontalDivider()
        TransplantListItem(
            headlineContent = {
                Text(stringResource(R.string.appearance_settings_calendar_background_title))
            },
            supportingContent = {
                if(!tiny)
                    Text(stringResource(R.string.appearance_settings_calendar_background_description))
            },
            modifier = Modifier.clickable {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            leadingContent = {
                Icon(painterResource(R.drawable.image),null)
            },
            trailingContent = {
                if(useCustomBackground) {
                    FilledTonalIconButton(
                        onClick = {
                            scope.launch {
                                deleteCustomBackground(context)
                            }
                        }
                    ) {
                        Icon(painterResource(R.drawable.delete),null)
                    }
                }
            }
        )

        if(useCustomBackground) {
//            var squareAlpha by remember { mutableFloatStateOf(customSquareAlpha) }
            if(!tiny)
                PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = {
                    Text(
                        stringResource(
                            R.string.appearance_settings_calendar_square_alpha_title,
                            formatDecimal((customSquareAlpha * 100).toDouble(), 0)
                        ))
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.visibility),null)
                },
                supportingContent = {
                    if(!tiny)
                        Text(stringResource(R.string.appearance_settings_calendar_square_alpha_description))
                }
            )
            CustomSlider(
                value = customSquareAlpha,
                onValueChange = {
                    scope.launch { DataStoreManager.saveCustomSquareAlpha(it) }
                },
//                onValueChangeFinished =  {
//                },
                modifier = Modifier.let {
                    if(tiny) it
                    else it.padding(bottom = APP_HORIZONTAL_DP)
                },
                valueRange = 0f..1f,
                showProcessText = true
            )
        }
        if(!tiny) {
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = {
                    Text(
                        stringResource(
                            R.string.appearance_settings_old_calendar_square_height_title,
                            formatDecimal(calendarSquareHeight.toDouble(), 0)
                        ))
                },
                supportingContent = {
                    if(!tiny)
                        Text(stringResource(R.string.appearance_settings_old_calendar_square_height_description))
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.height),null)
                },
            )

            CustomSlider(
                value = calendarSquareHeight,
                onValueChange = {
                    scope.launch { DataStoreManager.saveCalendarSquareHeight(it) }
                },
                modifier = Modifier.let {
                    if(tiny) it
                    else it.padding(bottom = APP_HORIZONTAL_DP)
                },
                valueRange = 50f..200f,
                showProcessText = true,
                steps = 149,
                processText = formatDecimal(calendarSquareHeight.toDouble(),0)
            )
            PaddingHorizontalDivider()
        }

        TransplantListItem(
            headlineContent = {
                Text(
                    stringResource(
                        R.string.appearance_settings_calendar_square_height_title,
                        formatDecimal(calendarSquareHeightNew.toDouble(), 1)
                    ))
            },
            supportingContent = {
                if(!tiny)
                    Text(
                        stringResource(
                            R.string.appearance_settings_calendar_square_height_description,
                            formatDecimal(MyApplication.CALENDAR_SQUARE_HEIGHT_NEW.toDouble(), 0)
                        ))
            },
            leadingContent = {
                Icon(painterResource(R.drawable.height),null)
            },
        )

        CustomSlider(
            value = calendarSquareHeightNew,
            onValueChange = {
                scope.launch { DataStoreManager.saveCalendarSquareHeightNew(it) }
            },
            modifier = Modifier.let {
                if(tiny) it
                else it.padding(bottom = APP_HORIZONTAL_DP)
            },
            valueRange = 25f..125f,
            showProcessText = true,
            steps = 99,
            processText = formatDecimal(calendarSquareHeightNew.toDouble(),1)
        )
        if(!tiny)
            PaddingHorizontalDivider()
        TransplantListItem(
            headlineContent = {
                Text(
                    stringResource(
                        R.string.appearance_settings_calendar_square_text_size_title,
                        formatDecimal(calendarSquareTextSize.toDouble() * 100, 0)
                    ))
            },
            supportingContent = {
                if(!tiny)
                    Text(stringResource(R.string.appearance_settings_calendar_square_text_size_description))
            },
            leadingContent = {
                Icon(painterResource(R.drawable.translate),null)
            },
        )

        CustomSlider(
            value = calendarSquareTextSize,
            onValueChange = {
                scope.launch { DataStoreManager.saveCalendarSquareTextSize(it) }
            },
            modifier = Modifier.let {
                if(tiny) it
                else it.padding(bottom = APP_HORIZONTAL_DP)
            },
            valueRange = 0.25f..2f,
            showProcessText = true,
            processText = formatDecimal(calendarSquareTextSize.toDouble()*100,0)
        )
        if(!tiny)
            PaddingHorizontalDivider()
        TransplantListItem(
            headlineContent = {
                Text(
                    stringResource(
                        R.string.appearance_settings_calendar_square_text_line_padding_title,
                        formatDecimal(calendarSquareTextPadding.toDouble(), 2)
                    ))
            },
            supportingContent = {
                if(!tiny)
                    Text(stringResource(R.string.appearance_settings_calendar_square_text_line_padding_description))
            },
            leadingContent = {
                Icon(painterResource(R.drawable.translate),null)
            },
        )

        CustomSlider(
            value = calendarSquareTextPadding,
            onValueChange = {
                scope.launch { DataStoreManager.saveCalendarSquareTextPadding(it) }
            },
            modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
            valueRange = 1f..2f,
            showProcessText = true,
            processText = formatDecimal(calendarSquareTextPadding.toDouble(),2)
        )
    }
}


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
private fun LoopingRectangleCenteredTrail2(animationSpeed: Int) {
    if(animationSpeed == 0) return
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp - APP_HORIZONTAL_DP*4
    val boxSize = 65.dp
    val delta = (screenWidth - boxSize) / 2  // 偏移范围：中心左右最多偏移量

    val offsetX = remember { Animatable(-delta.value) } // 从左边开始（相对中心）

    val trailList = remember { mutableStateListOf<Float>() }
    val maxTrailCount = 10

    val primary = MaterialTheme.colorScheme.primary
    val container = MaterialTheme.colorScheme.primaryContainer


    LaunchedEffect(animationSpeed) {
        delay(AppAnimationManager.ANIMATION_SPEED*1L)
        while (true) {
            offsetX.animateTo(
                targetValue = delta.value,
                animationSpec = tween(animationSpeed, easing = FastOutSlowInEasing)
            )
            delay(animationSpeed.toLong())

            offsetX.animateTo(
                targetValue = -delta.value,
                animationSpec = tween(animationSpeed, easing = FastOutSlowInEasing)
            )
            delay(animationSpeed.toLong())
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            trailList.add(0, offsetX.value)
            if (trailList.size > maxTrailCount) {
                trailList.removeAt(trailList.lastIndex)
            }
            delay(15L)
        }
    }

    // 容器为中心对称坐标系
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(boxSize + APP_HORIZONTAL_DP),
        contentAlignment = Alignment.Center
    ) {
        // 拖影
        trailList.forEachIndexed { index, trail ->
            val alpha = ((maxTrailCount - index).toFloat() / maxTrailCount).coerceIn(0f, 1f)
            val sizeFactor = 1f - (index * 0.015f)

            Box(
                modifier = Modifier
                    .offset { IntOffset(trail.dp.roundToPx(), 0) }
                    .size(boxSize)
                    .graphicsLayer {
                        this.alpha = alpha * 0.4f
                    }
                    .clip(MaterialTheme.shapes.medium)
                    .background(lerp(container, primary, alpha))
            )
        }

        // 主体
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.dp.roundToPx(), 0) }
                .size(boxSize)
                .clip(MaterialTheme.shapes.medium)
                .background(primary)
        )
    }
}

@Composable
private fun MotionBlurIcon(motionBlur : Boolean) {
    //MaterialTheme.colorScheme.surfaceContainer
    Box(modifier = Modifier
        .size(24.dp)
        .background(Color.Transparent, shape = CircleShape)){
        val infiniteTransition = rememberInfiniteTransition()
        val scale by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = AppAnimationManager.ANIMATION_SPEED * 3,
                    easing = FastOutSlowInEasing,
                    delayMillis = AppAnimationManager.ANIMATION_SPEED
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
        // 模糊 2.5 ~ 0
        val blur by infiniteTransition.animateFloat(
            initialValue = 5f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = AppAnimationManager.ANIMATION_SPEED * 3,
                    easing = FastOutSlowInEasing,
                    delayMillis = AppAnimationManager.ANIMATION_SPEED
                ),
                repeatMode = RepeatMode.Reverse
            )
        )

        // 旋转，每个周期旋转固定角度（累加效果）
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 60f, // 一圈
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = AppAnimationManager.ANIMATION_SPEED * 3 / 2,
                    easing = LinearEasing
                ), // 线性旋转
                repeatMode = RepeatMode.Restart
            )
        )

        Icon(
            painter = painterResource(R.drawable.filter_vintage),
            contentDescription = "Localized description",
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = rotation
                }
                .blur(radius = if (motionBlur) blur.dp else 0.dp)
                .scale(scale)
        )
        Icon(
            painter = painterResource(R.drawable.filter_vintage),
            contentDescription = "Localized description",
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = -rotation
                }
                .blur(radius = if (motionBlur) (2.5 - blur).dp else 0.dp)
                .scale(1 - scale)
        )
    }
}


@Composable
private fun ShaderIcon(shader : Boolean) {
    val scale by animateFloatAsState(
        if(shader) 0.6f else 1f
    )
    val color = MaterialTheme.colorScheme.onSurface

    Box(modifier = Modifier
        .size(24.dp)
        .background(Color.Transparent, shape = CircleShape)){
        Icon(
            painter = painterResource(R.drawable.filter_vintage),
            contentDescription = "Localized description",
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .scaleMirror(scale, clipShape = CircleShape)
                .mask(
                    color = MaterialTheme.colorScheme.onSurface,
                    targetAlpha = 0.1f,
                    show = shader
                )
        )
    }
}


@Composable
private fun HazeBlurIcon(blur : Boolean) {
    val size = 24.dp
    Box(modifier = Modifier.size(size)) {
        Icon(
            painter = painterResource(R.drawable.filter_vintage),
            contentDescription = "Localized description",
            modifier = Modifier
                .matchParentSize()
                .drawWithContent {
                    // 只绘制左半部分原图
                    val halfWidth = size.toPx() / 2
                    clipRect(right = halfWidth) {
                        this@drawWithContent.drawContent()
                    }
                }
        )
        Icon(
            painter = painterResource(R.drawable.filter_vintage),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .drawWithContent {
                    val halfWidth = size.toPx() / 2
                    // 只绘制右半部分，并加模糊
                    clipRect(left = halfWidth) {
                        this@drawWithContent.drawContent()
                    }
                }
                .blur(if (blur) 2.5.dp else 0.dp) // 模糊半径
        )
    }
}

@Composable
private fun Colors(isControlCenter : Boolean) {
    val list = listOf(
        Pair(MaterialTheme.colorScheme.primary,"Primary"),
        Pair(MaterialTheme.colorScheme.secondary,"Secondary"),
        Pair(MaterialTheme.colorScheme.primaryContainer,"PrimaryContainer"),
        Pair(MaterialTheme.colorScheme.secondaryContainer,"SecondaryContainer"),
        Pair(MaterialTheme.colorScheme.surfaceVariant,"SurfaceVariant"),
        Pair(
            if(isControlCenter)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceContainer,
            if(isControlCenter)
                "Surface"
            else
                "SurfaceContainer"
        )
    )
    RowHorizontal(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = APP_HORIZONTAL_DP, bottom = APP_HORIZONTAL_DP - 10.dp)) {
        val size = 40.dp
        val padding = APP_HORIZONTAL_DP/2

        LazyRow {
            items(list.size,key = { list[it].second}) { index ->
                val item = list[index]
                Surface(
                    color = item.first,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(
                            end =
                                if (index == list.size - 1) 0.dp
                                else padding
                        )
                        .size(size)
                        .clip(CircleShape)
                        .clickable {
                            showToast(item.second)
                        }
                ) {}
            }
        }
    }
}
