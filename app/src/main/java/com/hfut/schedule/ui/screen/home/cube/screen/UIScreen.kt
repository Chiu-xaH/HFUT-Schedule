package com.hfut.schedule.ui.screen.home.cube.screen

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.util.extractColor
import com.hfut.schedule.ui.util.hsvToLong
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.util.longToHexColor
import com.hfut.schedule.ui.util.longToHue
import com.hfut.schedule.ui.util.parseColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.home.cube.sub.AnimationSetting
import com.hfut.schedule.ui.screen.home.cube.sub.TransitionExample
import com.xah.uicommon.style.align.ColumnVertical
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.style.align.RowHorizontal
import com.hfut.schedule.ui.util.AppAnimationManager
import com.xah.transition.state.TransitionState
import com.xah.transition.style.TransitionLevel
import com.xah.transition.util.TransitionPredictiveBackHandler
import com.xah.uicommon.component.slider.CustomSlider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


suspend fun initTransition() = withContext(Dispatchers.IO) {
    val transition = DataStoreManager.transitionLevel.first()
    val motionBlur = DataStoreManager.enableMotionBlur.first()
//    TransitionState.firstStartRoute = AppNavRoute.Home.route
//    TransitionState.transitionBackgroundStyle.motionBlur = motionBlur
    TransitionState.transitionBackgroundStyle.level = TransitionLevel.entries.find { it.code == transition } ?: TransitionLevel.NONE
}


val animationList =  DataStoreManager.AnimationSpeed.entries.sortedBy { it.speed }
val styleList = DataStoreManager.ColorStyle.entries


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun UIScreen(innerPaddings : PaddingValues,navController : NavHostController) {
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionPredictiveBackHandler(navController) {
        scale = it
    }
    UISettingsScreen(
        Modifier
            .verticalScroll(rememberScrollState())
            .scale(scale),
        innerPaddings,
        false
    )
}


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun UISettingsScreen(modifier : Modifier = Modifier, innerPaddings: PaddingValues, isControlCenter : Boolean ) {
    val backgroundColor = if(isControlCenter) {
        cardNormalColor()
    } else {
        MaterialTheme.colorScheme.surface
    }
    Column(modifier = modifier) {
        if(!isControlCenter) {
            InnerPaddingHeight(innerPaddings,true)
        }

        val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)

        val webViewDark by DataStoreManager.enableForceWebViewDark.collectAsState(initial = true)
        val currentPureDark by DataStoreManager.enablePureDark.collectAsState(initial = false)
        val motionBlur by DataStoreManager.enableMotionBlur.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)
        val transition by DataStoreManager.transitionLevel.collectAsState(initial = TransitionLevel.NONE.code)
        val currentColorModeIndex by DataStoreManager.colorMode.collectAsState(initial = DataStoreManager.ColorMode.AUTO.code)
        val customColor by DataStoreManager.customColor.collectAsState(initial = -1L)
        val customBackground by DataStoreManager.customBackground.collectAsState(initial = "")
        val customBackgroundAlpha by DataStoreManager.customBackgroundAlpha.collectAsState(initial = 1f)
        val customColorStyle by DataStoreManager.customColorStyle.collectAsState(initial = DataStoreManager.ColorStyle.DEFAULT.code)

        val scope = rememberCoroutineScope()

        val pickMultipleMedia = rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let { imageUri ->
                scope.launch {
                    DataStoreManager.saveCustomBackground(imageUri)
                    showToast("已设置背景")
                    extractColor(imageUri)?.let {
                        DataStoreManager.saveCustomColor(it)
                    }
                }
            }
        }
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
        val hazeBlurLevels = remember { HazeBlurLevel.entries }

        LaunchedEffect(transition) {
            TransitionState.transitionBackgroundStyle.level = transitionLevels.find { it.code == transition } ?: TransitionLevel.NONE
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
                        label = { Text("输入 ARGB Hex 值") },
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
                            Text("设置")
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
                            Text("取消")
                        }
                    }

                }
            }
        }

        DividerTextExpandedWith("深浅色") {
            CustomCard(color = backgroundColor) {
                TransplantListItem(
                    headlineContent = { Text(text = "纯黑深色背景") },
                    supportingContent = { Text(text = "OLED屏使用此模式在深色模式时可获得不发光的纯黑背景") },
                    leadingContent = { Icon(painterResource(R.drawable.contrast), contentDescription = "Localized description",) },
                    trailingContent = {
                        Switch(checked = currentPureDark, onCheckedChange = { scope.launch { DataStoreManager.savePureDark(!currentPureDark) } })
                    },
                    modifier = Modifier.clickable {
                        scope.launch { DataStoreManager.savePureDark(!currentPureDark) }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "强制网页深色模式") },
                    supportingContent = { Text(text = "将强制深色的代码注入到网页中，以尝试适配应用的深色模式，如有网页显示异常，请暂时关闭") },
                    leadingContent = { Icon(painterResource(R.drawable.syringe), contentDescription = "Localized description",) },
                    trailingContent = {
                        Switch(checked = webViewDark, onCheckedChange = { scope.launch { DataStoreManager.saveWebViewDark(!webViewDark) } })
                    },
                    modifier = Modifier.clickable {
                        scope.launch { DataStoreManager.saveWebViewDark(!webViewDark) }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "深浅色") },
                    supportingContent = {
                        Row {
                            FilterChip(
                                onClick = {
                                    scope.launch { DataStoreManager.saveColorMode(DataStoreManager.ColorMode.LIGHT) }
                                },
                                label = { Text(text = "浅色") }, selected = currentColorModeIndex == DataStoreManager.ColorMode.LIGHT.code
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            FilterChip(
                                onClick = {
                                    scope.launch { DataStoreManager.saveColorMode(DataStoreManager.ColorMode.DARK) }
                                },
                                label = { Text(text = "深色") }, selected = currentColorModeIndex == DataStoreManager.ColorMode.DARK.code
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            FilterChip(
                                onClick = {
                                    scope.launch { DataStoreManager.saveColorMode(DataStoreManager.ColorMode.AUTO) }
                                },
                                label = { Text(text = "跟随系统") }, selected = currentColorModeIndex == DataStoreManager.ColorMode.AUTO.code
                            )
                        }
                    },
                    leadingContent = { Icon(painterResource(
                        when(currentColorModeIndex) {
                            DataStoreManager.ColorMode.DARK.code -> R.drawable.dark_mode
                            DataStoreManager.ColorMode.LIGHT.code -> R.drawable.light_mode
                            else -> R.drawable.routine
                        }
                    ), contentDescription = "Localized description",) },
                )
            }
        }
        DividerTextExpandedWith("主题色") {
            CustomCard(color = backgroundColor) {
                RowHorizontal(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = APP_HORIZONTAL_DP, bottom = APP_HORIZONTAL_DP - 10.dp)) {
                    FilledTonalIconButton(
                        onClick = { showToast("Primary") },
                        colors = IconButtonDefaults. filledTonalIconButtonColors(containerColor =  MaterialTheme.colorScheme.primary )
                    ) { }
                    FilledTonalIconButton(
                        onClick = { showToast("Secondary") },
                        colors = IconButtonDefaults. filledTonalIconButtonColors(containerColor =  MaterialTheme.colorScheme.secondary )
                    ) { }
                    FilledTonalIconButton(
                        onClick = { showToast("PrimaryContainer") },
                        colors = IconButtonDefaults. filledTonalIconButtonColors(containerColor =  MaterialTheme.colorScheme.primaryContainer )
                    ) { }
                    FilledTonalIconButton(
                        onClick = { showToast("SecondaryContainer") },
                        colors = IconButtonDefaults. filledTonalIconButtonColors(containerColor =  MaterialTheme.colorScheme.secondaryContainer )
                    ) { }
                    FilledTonalIconButton(
                        onClick = { showToast("SurfaceVariant") },
                        colors = IconButtonDefaults. filledTonalIconButtonColors(containerColor =  MaterialTheme.colorScheme.surfaceVariant )
                    ) { }
                    FilledTonalIconButton(
                        onClick = { showToast("SurfaceContainer") },
                        colors = IconButtonDefaults. filledTonalIconButtonColors(containerColor =  MaterialTheme.colorScheme.surfaceContainer )
                    ) { }
                }
                DividerTextExpandedWith("默认取色") {
                    TransplantListItem(
                        headlineContent = { Text(text = "原生取色") },
                        leadingContent = { Icon(painterResource(R.drawable.palette), contentDescription = "Localized description",) },
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
                DividerTextExpandedWith("自定义取色") {
                    if(!useDynamicColor) {
                        TransplantListItem(
                            headlineContent = { Text(text = "风格") },
                            leadingContent = { Icon(painterResource(R.drawable.invert_colors), contentDescription = "Localized description",) },
                            supportingContent = {
                                Row {
                                    for(i in styleList.indices) {
                                        val item = styleList[i]
                                        FilterChip(
                                            onClick = {
                                                scope.launch { DataStoreManager.saveCustomColorStyle(item) }
                                            },
                                            label = {
                                                Text(text = item.description)
                                            },
                                            selected = customColorStyle == item.code
                                        )
                                        if(i != styleList.size-1) {
                                            Spacer(modifier = Modifier.width(5.dp))
                                        }
                                    }
                                }
                            }
                        )
                        PaddingHorizontalDivider()
                    }
                    TransplantListItem(
                        headlineContent = { Text(text = "选择图片取色") },
                        leadingContent = { Icon(painterResource(R.drawable.image), contentDescription = "Localized description",) },

                        modifier = Modifier.clickable {
                            scope.launch {
                                pickMultipleMediaForColor.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        }
                    )
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text(text = "色相带取色") },
                        leadingContent = { Icon(painterResource(R.drawable.colorize), contentDescription = "Localized description",) },
                        supportingContent = {
                            Text("点击手动输入颜色值")
                        },
                        trailingContent = {
                            if(!useDynamicColor) {
                                val color = hsvToLong(hue,1f,1f)
                                val text = longToHexColor(color)
                                ColumnVertical {
                                    FilledTonalIconButton(
                                        onClick = { ClipBoardUtils.copy(text) },
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
        DividerTextExpandedWith("动效") {
            CustomCard(color = backgroundColor) {
                TransplantListItem(
                    headlineContent = { Text(text = "运动模糊") },
                    supportingContent = {
                        if(AppVersion.CAN_MOTION_BLUR) {
                            Text(text = "组件的运动过程伴随实时模糊效果\n平衡性能与美观,推荐开启")
                        } else {
                            Text(text = "需为 Android 12+")
                        }
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
                        Text(text = "层级实时模糊" + " | " + "Level${blur+1} (${hazeBlurLevels.find { it.code == blur}?.title})")
                    },
                    supportingContent = {
                        Text(text = "层级使用实时模糊区分\n平衡性能与美观,推荐为Level2" )
                    },
                    leadingContent = {
                        HazeBlurIcon(blur)
                    },
                )
                CustomSlider(
                    value = blur.toFloat(),
                    onValueChange = { value ->
                        val level = hazeBlurLevels.find { it.code == value.toInt() } ?: return@CustomSlider
                        scope.launch { DataStoreManager.saveHazeBlur(level) }
                    },
                    modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
                    steps = 1,
                    valueRange = 0f..2f,
                )
                PaddingHorizontalDivider()
//                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
//                LoopingRectangleCenteredTrail2(it)
//                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = {
                        Column {
                            Text(text = "转场背景特效" + " | " + "Level${transition+1} (${transitionLevels.find { it.code == transition}?.title})")
                        }
                    },
                    supportingContent = {
                        Text(text = "界面打开关闭时背景伴随特效\n平衡性能与美观,推荐为Level2")
                    },
                    leadingContent = { Icon(painterResource(R.drawable.transition_fade), contentDescription = "Localized description",) },
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
//                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
                RowHorizontal {
                    TransitionExample()
                }
                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "底栏转场动画") },
                    supportingContent = {
                        Text("底栏切换时进行转场的动画\n平衡性能与美观,推荐为向中心运动或淡入淡出")
                    },
                    leadingContent = { Icon(painterResource(R.drawable.animation), contentDescription = "Localized description",) },
                )
                AnimationSetting()
                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
            }
        }

        DividerTextExpandedWith("课程表背景") {
            val useCustomBackground = customBackground != ""
            CustomCard(color = backgroundColor) {
                TransplantListItem(
                    headlineContent = {
                        Text("自定义图片")
                    },
                    supportingContent = {
                        Text(if(!useCustomBackground) "选择图片，作为课程表的背景，同时也会改变色彩" else "混色(值越小，图片越淡) ${formatDecimal((customBackgroundAlpha*100).toDouble(),0)}%")
                    },
                    modifier = Modifier.clickable {
                        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.image),null)
                    },
                    trailingContent = {
                        if(useCustomBackground) {
                            FilledTonalButton(
                                onClick = {
                                    scope.launch {
                                        DataStoreManager.saveCustomBackground(null)
                                    }
                                }
                            ) {
                                Text("清除")
                            }
                        }
                    }
                )
//                TransplantListItem(
//                    headlineContent = {
//                        Text("随机背景")
//                    },
//                    supportingContent = {
//                        Text(if(!useCustomBackground) "以必应每日壁纸作为课程表的背景，同时也会改变色彩，每天会更换" else "混色(值越小，图片越淡) ${formatDecimal((customBackgroundAlpha*100).toDouble(),0)}%")
//                    },
//                    modifier = Modifier.clickable {
//
//                    },
//                    leadingContent = {
//                        Icon(painterResource(R.drawable.image),null)
//                    },
//                    trailingContent = {
//                        CustomSwitch(checked = false,)
//                    }
//                )
                if(useCustomBackground) {
                    var alpha by remember { mutableFloatStateOf(customBackgroundAlpha) }
                    CustomSlider(
                        value = alpha,
                        onValueChange = {
                            alpha = it
                        },
                        onValueChangeFinished =  {
                            scope.launch { DataStoreManager.saveCustomBackgroundAlpha(alpha) }
                        },
                        modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
                        valueRange = 0f..1f,
                        showProcessText = true
                    )
                }
            }
        }
        if(!isControlCenter) {
            InnerPaddingHeight(innerPaddings,false)
        } else {
            Spacer(Modifier.navigationBarsPadding())
        }
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

    // 居中颜色插值（基于正向偏移百分比）
//    val containColor = MaterialTheme.colorScheme.primary
//        remember(offsetX.value) {
//        val fraction = ((offsetX.value + delta.value) / (2 * delta.value)).coerceIn(0f, 1f)
//        lerp(container, primary, fraction)
//    }

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
                    .size(
                        boxSize
//                            * sizeFactor
                    )
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
    Box(modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.surfaceContainer, shape = CircleShape)){
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
                .blur(radius = if(motionBlur) blur.dp else 0.dp)
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
                .blur(radius = if(motionBlur) (2.5-blur).dp else 0.dp)
                .scale(1-scale)
        )
    }
}


@Composable
private fun HazeBlurIcon(blur : Int) {
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
                .blur(if (blur >= HazeBlurLevel.MID.code) 2.5.dp else 0.dp) // 模糊半径
        )
    }
}