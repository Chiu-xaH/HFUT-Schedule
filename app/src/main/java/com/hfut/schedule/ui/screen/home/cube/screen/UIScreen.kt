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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
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
import com.hfut.schedule.ui.component.media.SimpleVideo
import com.hfut.schedule.ui.component.media.checkOrDownloadVideo
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.input.CustomTextField
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
import com.hfut.schedule.ui.util.webview.isThemeDark
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
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.hfut.schedule.ui.component.button.CustomSingleChoiceRow
import com.hfut.schedule.ui.util.layout.measureDpSize
import com.xah.uicommon.component.text.ScrollText

private val styleList = ColorStyle.entries


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun UIScreen(innerPaddings : PaddingValues,navController : NavHostController) {
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)

    var scale by remember { mutableFloatStateOf(1f) }
    TransitionBackHandler(navController,enablePredictive) {
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
fun UISettingsScreen(modifier : Modifier = Modifier, innerPaddings: PaddingValues, isControlCenter : Boolean ) {
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


        DividerTextExpandedWith("深浅色",contentColor=contentColor) {
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
                    leadingContent = { Icon(painterResource(
                        when(currentColorModeIndex) {
                            ColorMode.DARK.code -> R.drawable.dark_mode
                            ColorMode.LIGHT.code -> R.drawable.light_mode
                            else -> R.drawable.routine
                        }
                    ), contentDescription = "Localized description",) },
                )
                // 三个状态，三个选项
                val options = remember {
                    listOf(
                        ColorMode.LIGHT to "浅色",
                        ColorMode.DARK to "深色",
                        ColorMode.AUTO to "跟随系统"
                    )
                }

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP)
                        .padding(bottom = APP_HORIZONTAL_DP),
                    // 不撑满就会出现神秘文本测量问题 😎
                ) {
                    options.forEachIndexed { index, (mode, label) ->
                        val isSelected = currentColorModeIndex == mode.code
                        val scrollState = rememberScrollState()
                        val textOverflow = scrollState.canScrollBackward || scrollState.canScrollForward

                        // 有个缺点是不能为某一个选项单独设置宽度，如果在上面的 Row 里面指定 space 那么在下面的自定义颜色中又会导致边框堆叠
                        SegmentedButton(
                            selected = isSelected,
                            onClick = {
                                scope.launch {
                                    DataStoreManager.saveColorMode(mode)
                                }
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size,
                                baseShape = MaterialTheme.shapes.small // 圆角
                            ),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary,
                                activeContentColor = MaterialTheme.colorScheme.onPrimary,
                                activeBorderColor = MaterialTheme.colorScheme.primary,
                                // pC 描边与选中颜色背景一致，但是相邻选项之间感觉少一条线
                                inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            icon = {
                                if (!textOverflow) {
                                    SegmentedButtonDefaults.Icon(isSelected)
                                }
                            },
                            label = {
                                Text(
                                    modifier = Modifier
                                        .horizontalScroll(scrollState),
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                )
//                                Text(
//                                    text = label,
//                                    style = MaterialTheme.typography.bodyMedium
//                                )
                            }
                        )
                    }
                }
            }
        }
        DividerTextExpandedWith("主题色",contentColor=contentColor) {
            CustomCard(color = backgroundColor) {
                Colors(isControlCenter)
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
                        val d = styleList.find { it.code == customColorStyle }?.description
                        TransplantListItem(
                            headlineContent = { Text(text = "浓度 | ${d}") },
                            leadingContent = { Icon(painterResource(R.drawable.invert_colors), contentDescription = "Localized description",) },
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
        DividerTextExpandedWith("特效",contentColor=contentColor) {
            CustomCard(color = backgroundColor) {
                TransplantListItem(
                    headlineContent = { Text(text = "运动模糊") },
                    supportingContent = {
                        Text(text = "部分组件的运动过程伴随实时模糊效果" + if(!AppVersion.CAN_MOTION_BLUR) "(需为 Android 12+)" else "")
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
                        Text(text = "层级模糊")
                    },
                    supportingContent = {
                        Text(text = "部分层级使用渐进式或带背景色的实时模糊" )
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
                        Text(text = "着色器渲染")
                    },
                    supportingContent = {
                        Text(text = "部分内容渲染为折射或镜面的材质效果" + (
                                if(!AppVersion.CAN_SHADER)
                                    "(需为Android 13+)"
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
                        Text(text = "相机实时渲染")
                    },
                    supportingContent = {
                        Text(text = "将摄像头的画面实时渲染在UI图层上，以实现支持组件化的模糊效果，开启后将带来一些渲染压力")
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
        DividerTextExpandedWith("动效",contentColor=contentColor) {
            CustomCard(color = backgroundColor) {
                TransplantListItem(
                    headlineContent = {
                        Column {
                            Text(text = "转场动画等级" + " | " + "Level${transition} (${transitionLevels.find { it.code == transition}?.title})")
                        }
                    },
                    supportingContent = {
                        Text(text = "界面打开关闭时背景伴随特效与容器共享\n平衡性能与美观,推荐为Level3")
                    },
                    leadingContent = { Icon(painterResource(R.drawable.airline_stops), contentDescription = "Localized description",) },
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
        DividerTextExpandedWith("课程表",contentColor=contentColor) {
            CustomCard(color = backgroundColor) {
                CalendarUISettings()
            }
        }
        DividerTextExpandedWith("底栏",contentColor=contentColor) {
            CustomCard(color = backgroundColor) {
                TransplantListItem(
                    headlineContent = {
                        Text("显示所有底栏标签")
                    },
                    supportingContent = {
                        Text("显示全部的底栏标签或仅选中项")
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
//@Preview
fun WidgetPreview(res : Int) {
    val shapeC = RoundedCornerShape(16.dp)
    // 自转（绕 Z 轴）
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val phaseAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationZ"
    )

    // 倾斜幅度（度数），可以调节（越大越明显）
    // 我们也让幅度缓慢往复，避免过于死板（可去掉）
    val tiltTransition by infiniteTransition.animateFloat(
        initialValue = 5f,      // 最小倾斜角度（度）
        targetValue = 10f,      // 最大倾斜角度（度）
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt"
    )

    // 将角度转换为 radians 以做 sin/cos
    val rad = Math.toRadians(phaseAngle.toDouble())

    // 倾斜在 X、Y 的分量（度）
    val rotationXc = (tiltTransition * sin(rad)).toFloat()
    val rotationYc = (-tiltTransition * cos(rad)).toFloat()

    // cameraDistance（像素），让 3D 效果更真实；值可调整
    val density = LocalDensity.current
    val cameraDistancePx = with(density) { 10.dp.toPx() } // 值越大透视越弱
    val color = MaterialTheme.colorScheme.onSurface
    val shadow = with(density) { APP_HORIZONTAL_DP.toPx() }

    Image(
        painterResource(res),
        null,
        modifier = Modifier
            .graphicsLayer {
                clip = true
                shape = shapeC
                // 先做 Z 轴自转，再做 X/Y 倾斜
                rotationX = rotationXc
                rotationY = rotationYc
                // camera 距离，防止 3D 透视过强
                this.cameraDistance = cameraDistancePx
                shadowElevation = shadow
                ambientShadowColor = color
                spotShadowColor = color
            }
    )
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
                    showToast("已设置背景")
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
                    Text("方格内显示教师")
                },
//                supportingContent = {
//                    Column {
//                        Row {
//                            FilterChip(
//                                onClick = {
//                                    scope.launch {
//                                        DataStoreManager.saveCalendarShowTeacher(ShowTeacherConfig.NONE)
//                                    }
//                                },
//                                label = { Text(text = ShowTeacherConfig.NONE.description) },
//                                selected = enableCalendarShowTeacher == ShowTeacherConfig.NONE.code
//                            )
//                            Spacer(modifier = Modifier.width(10.dp))
//                            FilterChip(
//                                onClick = {
//                                    scope.launch {
//                                        DataStoreManager.saveCalendarShowTeacher(ShowTeacherConfig.ALL)
//                                    }
//                                },
//                                label = { Text(text = ShowTeacherConfig.ALL.description) },
//                                selected = enableCalendarShowTeacher == ShowTeacherConfig.ALL.code
//                            )
//                        }
//                        FilterChip(
//                            onClick = {
//                                scope.launch {
//                                    DataStoreManager.saveCalendarShowTeacher(ShowTeacherConfig.ONLY_MULTI)
//                                }
//                            },
//                            label = { Text(text = ShowTeacherConfig.ONLY_MULTI.description) },
//                            selected = enableCalendarShowTeacher == ShowTeacherConfig.ONLY_MULTI.code
//                        )
//                    }
//                },
                leadingContent = {
                    Icon(painterResource(R.drawable.group), null)
                },
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = APP_HORIZONTAL_DP)
                    .padding(bottom = APP_HORIZONTAL_DP),
            ) {
                val options = remember { ShowTeacherConfig.entries }
                options.forEachIndexed { index, config ->
                    val isSelected = enableCalendarShowTeacher == config.code
                    val scrollState = rememberScrollState()
                    val textOverflow = scrollState.canScrollBackward || scrollState.canScrollForward
                    SegmentedButton(
                        selected = isSelected,
                        onClick = {
                            scope.launch {
                                DataStoreManager.saveCalendarShowTeacher(config)
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size,
                            baseShape = MaterialTheme.shapes.small
                        ),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = MaterialTheme.colorScheme.primary,
                            activeContentColor = MaterialTheme.colorScheme.onPrimary,
                            activeBorderColor = MaterialTheme.colorScheme.primary,
                            inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        icon = {
                            if (!textOverflow) {
                                SegmentedButtonDefaults.Icon(isSelected)
                            }
                        },
                        label = {
                            Text(
                                modifier = Modifier
                                    .horizontalScroll(scrollState),
                                text = config.description,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    )
                }
            }


            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = {
                    Text("合并冲突方格")
                },
                supportingContent = {
                    if (!tiny)
                        Text("打开后，将冲突项目以最早开始时间和最晚结束时间合并成一个方格")
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
                Text("背景图片")
            },
            supportingContent = {
                if(!tiny)
                    Text("选择图片作为课程表的背景，同时也会改变色彩")
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
                    Text("前景混色 ${formatDecimal((customSquareAlpha*100).toDouble(),0)}%")
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.visibility),null)
                },
                supportingContent = {
                    if(!tiny)
                        Text("值越小，方格和按钮等内容越透明")
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
                    Text("方格高度(旧) ${formatDecimal(calendarSquareHeight.toDouble(),0)}")
                },
                supportingContent = {
                    if(!tiny)
                        Text("自定义方格的高度(默认值为125)")
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
                Text("方格高度 ${formatDecimal(calendarSquareHeightNew.toDouble(),1)}")
            },
            supportingContent = {
                if(!tiny)
                    Text("自定义方格的高度(默认值为${formatDecimal(MyApplication.CALENDAR_SQUARE_HEIGHT_NEW.toDouble(),0)});在课程表界面双指捏合可临时缩放方格的高度")
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
                Text("方格文字大小 ${formatDecimal(calendarSquareTextSize.toDouble()*100,0)}%")
            },
            supportingContent = {
                if(!tiny)
                    Text("自定义方格内文字的大小及其行距(默认值为100%)")
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
                Text("方格文字行距 ${formatDecimal(calendarSquareTextPadding.toDouble(),2)}倍")
            },
            supportingContent = {
                if(!tiny)
                    Text("自定义方格内文字的行距(默认值为1.35倍)，越小则每行之间越紧密")
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
