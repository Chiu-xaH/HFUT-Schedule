package com.hfut.schedule.ui.model

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

data class NavigationBarItemData(
    val route: String,
    val label: String,
    val icon: Int,
    val filledIcon: Int
) {
    fun toDynamic() : NavigationBarItemDataDynamic {
        return NavigationBarItemDataDynamic(
            route = route,
            label = label,
            icon = { selected -> NavigationBarItemDynamicIcon(selected,icon,filledIcon) },
            badge = null
        )
    }
}

data class NavigationBarItemDataDynamic(
    val route: String,
    val label: String,
    val icon: @Composable (Boolean) -> Unit, // 动态图标，传入 selected
    val badge: (@Composable BoxScope.() -> Unit)? = null // 可选 badge
)

@Composable
fun NavigationBarItemDynamicIcon(selected : Boolean,icon : Int,filledIcon: Int) {
    Icon(
        painterResource(
            if (selected) filledIcon
            else icon
        ),
        contentDescription = null
    )
}

@Composable
fun NavigationBarItemDynamicIconModern(
    selected: Boolean,
    avdResource: Int,
) {
    key(avdResource) {
        // 资源变了就重组，以免外部手动更新后依旧使用缓存
        val image = AnimatedImageVector.animatedVectorResource(avdResource)
        val painter = rememberAnimatedVectorPainter(
            animatedImageVector = image,
            atEnd = selected
        )
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

// 新(实验)版
@DslMarker
annotation class InternalNavigationBar

@RequiresOptIn(
    message = "实验性组件， 可能存在意料之外的问题",
    level = RequiresOptIn.Level.ERROR
)
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalNavigationBarApi

enum class NavigationBarArrangement {
    HORIZONTAL,
    VERTICAL,
}

enum class IconType {
    STATIC,
    ANIMATED,
}

internal class NavigationBarItemRegistry {
    val entries = mutableStateListOf<NavigationBarItemEntry>()
    var initialized by mutableStateOf(false)
}

@Stable
sealed interface NavigationBarIcon {
    data class Resource(
        val resId: Int,
        val contentDescription: String? = null,
    ) : NavigationBarIcon

    data class Compose(val content: @Composable (active: Boolean) -> Unit) : NavigationBarIcon
    data class Canvas(val content: @Composable (active: Boolean) -> Unit) : NavigationBarIcon
}

@Stable
@ExperimentalNavigationBarApi
data class NewNavigationBarItemData(
    val icon: NavigationBarIcon,
    val iconType: IconType = IconType.STATIC,
    val text: String? = null,
    val badge: (@Composable () -> Unit)? = null,
    val selected: Boolean = false,
    val onClick: () -> Unit = {},
    val route: String
)

internal class NavigationBarItemEntry(
    icon: NavigationBarIcon,
    iconType: IconType,
    text: String?,
    badge: (@Composable () -> Unit)?,
    selected: Boolean,
    var onClick: () -> Unit,
    route: String
) {
    var icon by mutableStateOf(icon)
    var iconType by mutableStateOf(iconType)
    var text by mutableStateOf(text)
    var badge by mutableStateOf(badge)
    var selected by mutableStateOf(selected)
    var route by mutableStateOf(route)

    @OptIn(ExperimentalNavigationBarApi::class)
    fun snapshot() = NewNavigationBarItemData(
        icon = icon,
        iconType = iconType,
        text = text,
        badge = badge,
        selected = selected,
        onClick = { onClick() },
        route = route
    )
}

@InternalNavigationBar
class NavigationBarScope internal constructor(
    private val registry: NavigationBarItemRegistry,
) {
    @Composable
    @ExperimentalNavigationBarApi
    fun NavigationBarItem(
        selected: Boolean,
        onClick: () -> Unit,
        icon: NavigationBarIcon,
        iconType: IconType = IconType.STATIC,
        text: String? = null,
        badge: (@Composable () -> Unit)? = null,
        route: String
    ) {
        val entry = remember(registry) {
            NavigationBarItemEntry(icon, iconType, text, badge, selected, onClick, route)
        }
        SideEffect {
            entry.icon = icon
            entry.iconType = iconType
            entry.text = text
            entry.badge = badge
            entry.selected = selected
            entry.onClick = onClick
        }
        DisposableEffect(registry, entry) {
            registry.entries += entry
            onDispose { registry.entries -= entry }
        }
    }
}

@Composable
@ExperimentalNavigationBarApi
fun NavigationBar(
    modifier: Modifier = Modifier,
    hazeModifier: Modifier = Modifier,
    // 撑满
    fillTrack: Boolean = true,
    arrangement: NavigationBarArrangement = NavigationBarArrangement.VERTICAL,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    trackHeight: Dp = if (arrangement == NavigationBarArrangement.HORIZONTAL) 54.dp else 64.dp,
    // 指示器颜色
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    // 选中项内容颜色
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    // 阴影
    elevation: Dp = 12.dp,
    indicatorPadding: Dp = 6.dp,
    // 指示器默认大小
    indicatorNormalScale: Float = 1f,
    // 指示器按下时的缩小比例
    indicatorPressedScale: Float = 0.96f,
    // 缩小耗时
    indicatorAnimationDurationMillis: Int = 200,
    // 启用拖动选择
    dragEnabled: Boolean = true,
    // 自瞄辅助
    aimAssist: Boolean = false,
    aimAssistAnimationDurationMillis: Int = 20, // 参数越小， 吸附越快
    // 实时预览， 和自瞄是解耦的， 可以只吸附不触发 onClick
    preview: Boolean = false,
    // 振动启用
    hapticsEnabled: Boolean = true,
    // 未选中的选项按下变色
    itemPressedColor: Color = Color.Gray,
    // 变色耗时
    itemPressedColorAnimationDurationMillis: Int = 150,
    // 未选中的选项默认大小
    itemNormalScale: Float = 1f,
    // 它被按下时的缩小比例
    itemPressedScale: Float = 0.89f,
    // 缩小耗时
    itemScaleAnimationDurationMillis: Int = 300,
    // 类型
    itemScaleEasing: Easing = EaseOut,
    // Item 额外间隔
    itemHorizontalPadding: Dp = 32.dp,
    content: @Composable NavigationBarScope.() -> Unit,
) {
    val registry = remember { NavigationBarItemRegistry() }
    val scope = remember(registry) { NavigationBarScope(registry) }
    scope.content()
    SideEffect { registry.initialized = true }
    val items = registry.entries.map { it.snapshot() }
    val selectedIndex = items.indexOfFirst { it.selected }
    val trackHeight = trackHeight
    if (!registry.initialized || items.isEmpty()) {
        Box(modifier = modifier
            .fillMaxWidth()
            .height(trackHeight))
        return
    }
    val touchExplorationEnabled = rememberTouchExplorationEnabled()
    val effectiveDragEnabled = dragEnabled && !touchExplorationEnabled
    val effectiveAimAssist = aimAssist && !touchExplorationEnabled
    val effectivePreview = preview && !touchExplorationEnabled
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val rowTextStyle = LocalTextStyle.current
    val itemTextStyle = if (arrangement == NavigationBarArrangement.HORIZONTAL) {
        rowTextStyle
    } else {
        MaterialTheme.typography.labelMedium
    }
    val minimumIndicatorScale = minOf(indicatorNormalScale, indicatorPressedScale)
    val desiredCompactWidthPx = remember(
        items,
        arrangement,
        itemTextStyle,
        itemHorizontalPadding,
        indicatorPadding,
        minimumIndicatorScale,
        density,
    ) {
        with(density) {
            val iconWidth = 24.dp.toPx()
            val iconTextSpacing = 8.dp.toPx()
            val minimumItemWidth = 48.dp.toPx()
            val manualPadding = itemHorizontalPadding.toPx()
            val visualInset = indicatorPadding.toPx()
            items.sumOf { item ->
                val textWidth = item.text?.let { text ->
                    textMeasurer.measure(
                        text = text,
                        style = itemTextStyle,
                        maxLines = 1,
                        softWrap = false,
                    ).size.width.toFloat()
                } ?: 0f
                val contentWidth = if (arrangement == NavigationBarArrangement.HORIZONTAL) {
                    iconWidth + if (item.text == null) 0f else iconTextSpacing + textWidth
                } else {
                    max(iconWidth, textWidth)
                }.coerceAtLeast(minimumItemWidth)
                ceil(
                    (contentWidth + manualPadding * 2f) / minimumIndicatorScale +
                            visualInset * 2f,
                ).toInt()
            }
        }
    }
    val compactWidth = with(density) { desiredCompactWidthPx.toDp() }
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight),
        contentAlignment = Alignment.Center,
    ) {
        val trackWidth = if (fillTrack) maxWidth else minOf(maxWidth - 24.dp, compactWidth)

        Surface(
            modifier = Modifier
                .width(trackWidth.coerceAtLeast(1.dp))
                .height(trackHeight),
            shape = CircleShape,
            color = trackColor,
            shadowElevation = elevation,
        ) {
            var measuredWidth by remember { mutableIntStateOf(0) }
            val indicatorOffset = remember { Animatable(0f) }
            val indicatorWidth = remember { Animatable(0f) }
            val scope = rememberCoroutineScope()
            val hapticFeedback = LocalHapticFeedback.current
            val currentSelectedIndex by rememberUpdatedState(selectedIndex)
            val currentItems by rememberUpdatedState(items)
            var indicatorDragging by remember { mutableStateOf(false) }
            var indicatorPressed by remember { mutableStateOf(false) }
            var lastDragIndex by remember { mutableIntStateOf(selectedIndex) }
            var dragAccepted by remember { mutableStateOf(false) }
            var dragGrabOffsetFromCenter by remember { mutableFloatStateOf(0f) }
            var indicatorDownPositionInTrack by remember { mutableFloatStateOf(Float.NaN) }
            var dragProgress by remember { mutableFloatStateOf(selectedIndex.toFloat()) }
            var itemWidths by remember(items.size) {
                mutableStateOf(List(items.size) { 0 })
            }
            val interactionSources = remember(items.size) {
                List(items.size) { MutableInteractionSource() }
            }
            val geometry = remember(measuredWidth, itemWidths) {
                itemGeometry(measuredWidth, itemWidths)
            }
            val indicatorScale by animateFloatAsState(
                targetValue = if (indicatorPressed || indicatorDragging) {
                    indicatorPressedScale
                } else {
                    indicatorNormalScale
                },
                animationSpec = tween(durationMillis = 3/2 * indicatorAnimationDurationMillis, easing = EaseOut),
                label = "indicatorTouchScale",
            )

            LaunchedEffect(selectedIndex, geometry) {
                if (geometry.isReady && !indicatorDragging) {
                    val targetLeft = geometry.starts[selectedIndex]
                    val targetWidth = geometry.widths[selectedIndex]
                    dragProgress = selectedIndex.toFloat()
                    if (indicatorWidth.value == 0f) {
                        indicatorOffset.snapTo(targetLeft)
                        indicatorWidth.snapTo(targetWidth)
                    } else {
                        coroutineScope {
                            launch {
                                indicatorOffset.animateTo(
                                    targetValue = targetLeft,
                                    animationSpec = tween(
                                        durationMillis = indicatorAnimationDurationMillis,
                                        easing = FastOutSlowInEasing,
                                    ),
                                )
                            }
                            launch {
                                indicatorWidth.animateTo(
                                    targetValue = targetWidth,
                                    animationSpec = tween(
                                        durationMillis = indicatorAnimationDurationMillis,
                                        easing = FastOutSlowInEasing,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
            Box(
                modifier = hazeModifier
                    .fillMaxSize()
                    .onSizeChanged { measuredWidth = it.width }
                    .pointerInput(
                        geometry,
                        hapticsEnabled,
                        effectiveDragEnabled,
                        effectiveAimAssist,
                        aimAssistAnimationDurationMillis,
                        effectivePreview,
                        indicatorPadding,
                        indicatorAnimationDurationMillis,
                    ) {
                        // Powered by Fable 5
                        if (!geometry.isReady || !effectiveDragEnabled) return@pointerInput
                        val indicatorPaddingPx = indicatorPadding.toPx()
                        detectHorizontalDragGestures(
                            onDragStart = { position ->
                                val indicatorLeft = indicatorOffset.value
                                val currentWidth = indicatorWidth.value
                                dragAccepted = position.x in
                                        indicatorLeft..(indicatorLeft + currentWidth)
                                if (dragAccepted) {
                                    val downPosition = indicatorDownPositionInTrack
                                        .takeUnless { it.isNaN() } ?: position.x
                                    dragGrabOffsetFromCenter = downPosition -
                                            (indicatorLeft + currentWidth / 2f)
                                    indicatorDragging = true
                                    dragProgress = progressForPosition(
                                        indicatorLeft + currentWidth / 2f,
                                        geometry.centers,
                                    )
                                    lastDragIndex =
                                        dragProgress.roundToInt().coerceIn(items.indices)
                                    if (hapticsEnabled) {
                                        hapticFeedback.performHapticFeedback(
                                            HapticFeedbackType.TextHandleMove,
                                        )
                                    }
                                }
                            },
                            onHorizontalDrag = { change, _ ->
                                if (dragAccepted) {
                                    change.consume()
                                    val crossedIndex: Int
                                    val frame: IndicatorFrame
                                    if (aimAssist) {
                                        crossedIndex = geometry.indexAtPosition(change.position.x)
                                        dragProgress = crossedIndex.toFloat()
                                        val itemStart = geometry.starts[crossedIndex]
                                        val itemWidth = geometry.widths[crossedIndex]
                                        val positionInItem = ((change.position.x - itemStart) /
                                                itemWidth).coerceIn(0f, 1f)
                                        val insetContentWidth =
                                            (itemWidth - indicatorPaddingPx * 2f).coerceAtLeast(0f)
                                        val assistTravelPx = indicatorPaddingPx +
                                                insetContentWidth * (1f - indicatorPressedScale) / 2f
                                        val resistedShift =
                                            (positionInItem * 2f - 1f) * assistTravelPx
                                        frame = IndicatorFrame(
                                            left = itemStart + resistedShift,
                                            width = itemWidth,
                                        )
                                    } else {
                                        val absoluteCenter =
                                            (change.position.x - dragGrabOffsetFromCenter).coerceIn(
                                                geometry.centers.first(),
                                                geometry.centers.last(),
                                            )
                                        dragProgress = progressForPosition(
                                            absoluteCenter,
                                            geometry.centers,
                                        )
                                        frame = geometryAtProgress(geometry, dragProgress)
                                        crossedIndex = dragProgress.roundToInt()
                                            .coerceIn(items.indices)
                                    }
                                    if (crossedIndex != lastDragIndex) {
                                        lastDragIndex = crossedIndex
                                        if (hapticsEnabled) {
                                            hapticFeedback.performHapticFeedback(
                                                HapticFeedbackType.TextHandleMove,
                                            )
                                        }
                                        if (preview) {
                                            currentItems[crossedIndex].onClick()
                                        }
                                    }
                                    scope.launch {
                                        if (aimAssist) {
                                            coroutineScope {
                                                launch {
                                                    indicatorOffset.animateTo(
                                                        frame.left,
                                                        tween(
                                                            aimAssistAnimationDurationMillis,
                                                            easing = FastOutSlowInEasing,
                                                        ),
                                                    )
                                                }
                                                launch {
                                                    indicatorWidth.animateTo(
                                                        frame.width,
                                                        tween(
                                                            aimAssistAnimationDurationMillis,
                                                            easing = FastOutSlowInEasing,
                                                        ),
                                                    )
                                                }
                                            }
                                        } else {
                                            indicatorOffset.snapTo(frame.left)
                                            indicatorWidth.snapTo(frame.width)
                                        }
                                    }
                                }
                            },
                            onDragEnd = {
                                if (dragAccepted) {
                                    indicatorDragging = false
                                    val target = dragProgress.roundToInt().coerceIn(items.indices)
                                    if (hapticsEnabled) {
                                        hapticFeedback.performHapticFeedback(
                                            HapticFeedbackType.TextHandleMove,
                                        )
                                    }
                                    scope.launch {
                                        coroutineScope {
                                            launch {
                                                indicatorOffset.animateTo(
                                                    geometry.starts[target],
                                                    tween(
                                                        indicatorAnimationDurationMillis,
                                                        easing = FastOutSlowInEasing,
                                                    ),
                                                )
                                            }
                                            launch {
                                                indicatorWidth.animateTo(
                                                    geometry.widths[target],
                                                    tween(
                                                        indicatorAnimationDurationMillis,
                                                        easing = FastOutSlowInEasing,
                                                    ),
                                                )
                                            }
                                        }
                                    }
                                    currentItems[target].onClick()
                                }
                                dragAccepted = false
                            },
                            onDragCancel = {
                                indicatorDragging = false
                                dragAccepted = false
                                scope.launch {
                                    val target = currentSelectedIndex
                                    coroutineScope {
                                        launch {
                                            indicatorOffset.animateTo(
                                                geometry.starts[target],
                                                tween(
                                                    indicatorAnimationDurationMillis,
                                                    easing = FastOutSlowInEasing,
                                                ),
                                            )
                                        }
                                        launch {
                                            indicatorWidth.animateTo(
                                                geometry.widths[target],
                                                tween(
                                                    indicatorAnimationDurationMillis,
                                                    easing = FastOutSlowInEasing,
                                                ),
                                            )
                                        }
                                    }
                                }
                            },
                        )
                    }
            ) {
                if (geometry.isReady) {
                    Box(
                        Modifier
                            .graphicsLayer { translationX = indicatorOffset.value }
                            .width(with(density) { indicatorWidth.value.toDp() })
                            .fillMaxHeight()
                    ) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(indicatorPadding)
                                .graphicsLayer {
                                    scaleX = indicatorScale
                                    scaleY = indicatorScale
                                }
                                .background(indicatorColor, CircleShape),
                        )
                    }
                }

                NavigationItemsLayer(
                    items = items,
                    arrangement = arrangement,
                    color = contentColor,
                    interactive = false,
                    interactionSources = interactionSources,
                    hapticsEnabled = hapticsEnabled,
                    itemPressedColor = itemPressedColor,
                    itemPressedColorAnimationDurationMillis = itemPressedColorAnimationDurationMillis,
                    itemNormalScale = itemNormalScale,
                    itemPressedScale = itemPressedScale,
                    itemScaleAnimationDurationMillis = itemScaleAnimationDurationMillis,
                    itemScaleEasing = itemScaleEasing,
                    itemHorizontalPadding = itemHorizontalPadding,
                    indicatorContentPadding = indicatorPadding,
                    minimumIndicatorScale = minimumIndicatorScale,
                    onItemWidthChanged = { index, width ->
                        if (itemWidths[index] != width) {
                            itemWidths = itemWidths.toMutableList().also { it[index] = width }
                        }
                    },
                )

                if (geometry.isReady) {
                    NavigationItemsLayer(
                        items = items,
                        arrangement = arrangement,
                        color = selectedContentColor,
                        interactive = true,
                        interactionSources = interactionSources,
                        hapticsEnabled = hapticsEnabled,
                        itemPressedColor = itemPressedColor,
                        itemPressedColorAnimationDurationMillis = itemPressedColorAnimationDurationMillis,
                        itemNormalScale = itemNormalScale,
                        itemPressedScale = itemPressedScale,
                        itemScaleAnimationDurationMillis = itemScaleAnimationDurationMillis,
                        itemScaleEasing = itemScaleEasing,
                        itemHorizontalPadding = itemHorizontalPadding,
                        indicatorContentPadding = indicatorPadding,
                        minimumIndicatorScale = minimumIndicatorScale,
                        modifier = Modifier.drawWithContent {
                            val paddingPx = with(density) { indicatorPadding.toPx() }
                                .coerceAtMost(minOf(indicatorWidth.value, size.height) / 2f)
                            val contentWidth = (indicatorWidth.value - paddingPx * 2f)
                                .coerceAtLeast(0f)
                            val contentHeight = (size.height - paddingPx * 2f).coerceAtLeast(0f)
                            val scaledWidth = contentWidth * indicatorScale
                            val scaledHeight = contentHeight * indicatorScale
                            val left = indicatorOffset.value + paddingPx +
                                    (contentWidth - scaledWidth) / 2f
                            val top = paddingPx + (contentHeight - scaledHeight) / 2f
                            val path = Path().apply {
                                addRoundRect(
                                    RoundRect(
                                        left = left,
                                        top = top,
                                        right = left + scaledWidth,
                                        bottom = top + scaledHeight,
                                        radiusX = scaledHeight / 2f,
                                        radiusY = scaledHeight / 2f,
                                    ),
                                )
                            }
                            clipPath(path) { this@drawWithContent.drawContent() }
                        },
                    )
                }

                if (geometry.isReady && !touchExplorationEnabled) {
                    Box(
                        Modifier
                            .graphicsLayer { translationX = indicatorOffset.value }
                            .width(with(density) { indicatorWidth.value.toDp() })
                            .fillMaxHeight()
                            .clearAndSetSemantics {
                                hideFromAccessibility()
                            }
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = false)
                                    indicatorDownPositionInTrack =
                                        indicatorOffset.value + down.position.x
                                    indicatorPressed = true
                                    do {
                                        val event = awaitPointerEvent()
                                    } while (event.changes.any { it.pressed })
                                    indicatorPressed = false
                                    indicatorDownPositionInTrack = Float.NaN
                                }
                            },
                    )
                }
            }
        }
    }
}

@Stable
private data class ItemGeometry(
    val starts: List<Float>,
    val widths: List<Float>,
    val centers: List<Float>,
) {
    val isReady: Boolean get() = widths.isNotEmpty() && widths.all { it > 0f }

    fun indexAtPosition(position: Float): Int {
        val clamped = position.coerceIn(starts.first(), starts.last() + widths.last())
        return starts.indices.firstOrNull { clamped < starts[it] + widths[it] }
            ?: starts.lastIndex
    }
}

private data class IndicatorFrame(val left: Float, val width: Float)

private fun itemGeometry(trackWidth: Int, measuredItemWidths: List<Int>): ItemGeometry {
    if (trackWidth <= 0 || measuredItemWidths.isEmpty()) {
        return ItemGeometry(emptyList(), emptyList(), emptyList())
    }
    val widths = if (measuredItemWidths.all { it > 0 }) {
        measuredItemWidths.map(Int::toFloat)
    } else {
        List(measuredItemWidths.size) { trackWidth.toFloat() / measuredItemWidths.size }
    }
    var cursor = 0f
    val starts = widths.map { width -> cursor.also { cursor += width } }
    return ItemGeometry(
        starts = starts,
        widths = widths,
        centers = starts.indices.map { starts[it] + widths[it] / 2f },
    )
}

private fun progressForPosition(position: Float, centers: List<Float>): Float {
    if (centers.size == 1 || position <= centers.first()) return 0f
    if (position >= centers.last()) return centers.lastIndex.toFloat()
    val index = centers.indexOfLast { it <= position }.coerceAtMost(centers.lastIndex - 1)
    val segment = centers[index + 1] - centers[index]
    return index + (position - centers[index]) / segment
}

private fun geometryAtProgress(geometry: ItemGeometry, progress: Float): IndicatorFrame {
    val lower = progress.toInt().coerceIn(geometry.widths.indices)
    val upper = (lower + 1).coerceAtMost(geometry.widths.lastIndex)
    val fraction = (progress - lower).coerceIn(0f, 1f)
    return IndicatorFrame(
        left = geometry.starts[lower] +
                (geometry.starts[upper] - geometry.starts[lower]) * fraction,
        width = geometry.widths[lower] +
                (geometry.widths[upper] - geometry.widths[lower]) * fraction,
    )
}

@Composable
@OptIn(ExperimentalNavigationBarApi::class)
private fun NavigationItemsLayer(
    modifier: Modifier = Modifier,
    items: List<NewNavigationBarItemData>,
    arrangement: NavigationBarArrangement,
    color: Color,
    interactive: Boolean,
    interactionSources: List<MutableInteractionSource>,
    hapticsEnabled: Boolean,
    itemPressedColor: Color,
    itemPressedColorAnimationDurationMillis: Int,
    itemNormalScale: Float,
    itemPressedScale: Float,
    itemScaleAnimationDurationMillis: Int,
    itemScaleEasing: Easing,
    itemHorizontalPadding: Dp,
    indicatorContentPadding: Dp,
    minimumIndicatorScale: Float,
    onItemWidthChanged: ((index: Int, width: Int) -> Unit)? = null
) {
    // Powered by Fable 5
    SubcomposeLayout(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (interactive) {
                    Modifier.selectableGroup()
                } else {
                    Modifier.clearAndSetSemantics {
                        hideFromAccessibility()
                    }
                },
            )
    ) { constraints ->
        val minimumItemWidth = 48.dp.roundToPx()
        val measurementConstraints = constraints.copy(
            minWidth = 0,
            maxWidth = Constraints.Infinity,
            minHeight = 0,
        )
        val measuredContent = subcompose(NavigationItemsSlot.Measurement) {
            items.forEach { item ->
                NavigationItemMeasurementContent(item, arrangement)
            }
        }.map { measurable ->
            measurable.measure(measurementConstraints)
        }
        val naturalWidths = measuredContent.map { placeable ->
            placeable.width.coerceAtLeast(minimumItemWidth)
        }
        val allocatedWidths = allocateItemWidths(
            contentWidths = naturalWidths,
            availableWidth = constraints.maxWidth,
            horizontalPadding = itemHorizontalPadding.roundToPx(),
            indicatorPadding = indicatorContentPadding.roundToPx(),
            minimumIndicatorScale = minimumIndicatorScale,
        )

        val contentMeasurables = subcompose(NavigationItemsSlot.Content) {
            items.forEachIndexed { index, item ->
                NavigationBarItemCell(
                    item = item,
                    index = index,
                    arrangement = arrangement,
                    color = color,
                    interactive = interactive,
                    interactionSource = interactionSources[index],
                    selected = item.selected,
                    active = item.selected,
                    hapticsEnabled = hapticsEnabled,
                    itemPressedColor = itemPressedColor,
                    itemPressedColorAnimationDurationMillis =
                        itemPressedColorAnimationDurationMillis,
                    itemNormalScale = itemNormalScale,
                    itemPressedScale = itemPressedScale,
                    itemScaleAnimationDurationMillis = itemScaleAnimationDurationMillis,
                    itemScaleEasing = itemScaleEasing,
                    onWidthChanged = onItemWidthChanged,
                )
            }
        }
        val placeables = contentMeasurables.mapIndexed { index, measurable ->
            measurable.measure(
                constraints.copy(
                    minWidth = allocatedWidths[index],
                    maxWidth = allocatedWidths[index],
                    minHeight = constraints.maxHeight,
                    maxHeight = constraints.maxHeight,
                ),
            )
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            var x = 0
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(x, 0)
                x += allocatedWidths[index]
            }
        }
    }
}

@Composable
@OptIn(ExperimentalNavigationBarApi::class)
private fun NavigationBarItemCell(
    item: NewNavigationBarItemData,
    index: Int,
    arrangement: NavigationBarArrangement,
    color: Color,
    interactive: Boolean,
    interactionSource: MutableInteractionSource,
    selected: Boolean,
    active: Boolean,
    hapticsEnabled: Boolean,
    itemPressedColor: Color,
    itemPressedColorAnimationDurationMillis: Int,
    itemNormalScale: Float,
    itemPressedScale: Float,
    itemScaleAnimationDurationMillis: Int,
    itemScaleEasing: Easing,
    onWidthChanged: ((index: Int, width: Int) -> Unit)?,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val pressed by interactionSource.collectIsPressedAsState()
    val visuallyPressed = pressed && !selected
    val animatedScale by animateFloatAsState(
        targetValue = if (visuallyPressed) itemPressedScale else itemNormalScale,
        animationSpec = tween(
            durationMillis = itemScaleAnimationDurationMillis,
            easing = itemScaleEasing,
        ),
        label = "navigationItemScale",
    )
    val animatedColor by animateColorAsState(
        targetValue = if (visuallyPressed) itemPressedColor else color,
        animationSpec = tween(
            durationMillis = itemPressedColorAnimationDurationMillis,
            easing = EaseOut,
        ),
        label = "navigationItemColor",
    )

    LaunchedEffect(pressed) {
        if (interactive && pressed && !selected && hapticsEnabled) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .then(
                if (onWidthChanged != null) {
                    Modifier.onSizeChanged { onWidthChanged(index, it.width) }
                } else {
                    Modifier
                },
            )
            .scale(animatedScale)
            .then(
                if (interactive) {
                    Modifier.selectable(
                        selected = selected,
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Tab,
                        onClick = {
                            if (!selected) {
                                if (hapticsEnabled) {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.TextHandleMove,
                                    )
                                }
                                item.onClick()
                            }
                        },
                    )
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        // 用 CompositionLocal 穿透， 避免每一处都写 contentColor
        CompositionLocalProvider(LocalContentColor provides animatedColor) {
            NavigationItemContent(
                item = item,
                arrangement = arrangement,
                active = active,
            )
        }
    }
}

private enum class NavigationItemsSlot { Measurement, Content }

@Composable
@OptIn(ExperimentalNavigationBarApi::class)
private fun NavigationItemMeasurementContent(
    item: NewNavigationBarItemData,
    arrangement: NavigationBarArrangement,
) {
    if (arrangement == NavigationBarArrangement.HORIZONTAL) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(24.dp))
            item.text?.let {
                Text(it, maxLines = 1, overflow = TextOverflow.Visible)
            }
        }
    } else {
        Column(
            modifier = Modifier.padding(vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Box(Modifier.size(24.dp))
            item.text?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                )
            }
        }
    }
}


@Composable
@OptIn(ExperimentalNavigationBarApi::class)
private fun NavigationItemContent(
    item: NewNavigationBarItemData,
    arrangement: NavigationBarArrangement,
    active: Boolean,
) {
    val icon: @Composable () -> Unit = {
        Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
            NavigationIconContent(item, active)
            val iconContent: @Composable () -> Unit = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .then(
                            if (item.text != null) {
                                Modifier.clearAndSetSemantics { }
                            } else {
                                Modifier
                            },
                        ), contentAlignment = Alignment.Center) {
                    NavigationIconContent(item, active)
                }
            }
            key(item.badge) {
                if (item.badge == null) {
                    iconContent()
                } else {
                    BadgedBox(
                        badge = { item.badge.invoke() },
                        content = { iconContent() },
                    )
                }
            }
        }
    }

    if (arrangement == NavigationBarArrangement.HORIZONTAL) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            item.text?.let {
                Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    } else {
        Column(
            modifier = Modifier.padding(vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            icon()
            item.text?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun rememberTouchExplorationEnabled(): Boolean {
    // 查询当前是否启用了 TalkBack 类似的读屏应用
    // 如果其他地方有用到可以把它放到工具类里面
    val context = LocalContext.current
    val accessibilityManager = remember(context) {
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
    }
    var enabled by remember(accessibilityManager) {
        mutableStateOf(accessibilityManager?.isTouchExplorationEnabled == true)
    }
    DisposableEffect(accessibilityManager) {
        val listener = AccessibilityManager.TouchExplorationStateChangeListener {
            enabled = it
        }
        accessibilityManager?.addTouchExplorationStateChangeListener(listener)
        onDispose {
            accessibilityManager?.removeTouchExplorationStateChangeListener(listener)
        }
    }
    return enabled
}

@Composable
@OptIn(ExperimentalAnimationGraphicsApi::class, ExperimentalNavigationBarApi::class)
private fun NavigationIconContent(item: NewNavigationBarItemData, active: Boolean) {
    when (val icon = item.icon) {
        is NavigationBarIcon.Compose -> icon.content(active)
        is NavigationBarIcon.Canvas -> icon.content(
            item.iconType == IconType.ANIMATED && active,
        )
        is NavigationBarIcon.Resource -> {
            if (item.iconType == IconType.ANIMATED) {
                key(icon.resId) {
                    val image = AnimatedImageVector.animatedVectorResource(icon.resId)
                    Icon(
                        painter = rememberAnimatedVectorPainter(image, atEnd = active),
                        contentDescription = icon.contentDescription,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            } else {
                Icon(
                    painter = painterResource(icon.resId),
                    contentDescription = icon.contentDescription,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

private fun allocateItemWidths(
    contentWidths: List<Int>,
    availableWidth: Int,
    horizontalPadding: Int,
    indicatorPadding: Int,
    minimumIndicatorScale: Float,
): List<Int> {
    /* 大致原理 ↓
    *  空间足够？ (desiredTotal <= availableWidth)
    *   - 是: 按期望宽度比例撑满可用空间
    *   - 否: 内容能放下？ (contentTotal <= availableWidth) ， 这个放不下的部分 Powered by Fable 5
    *      - 是: 基础内容 + 平均分配剩余空间
    *      - 否: 空间不足， 按内容宽度比例压缩
    */
    if (contentWidths.isEmpty()) return emptyList()
    val contentTotal = contentWidths.sum().coerceAtLeast(1)
    val desiredWidths = contentWidths.map { contentWidth ->
        ceil(
            (contentWidth + horizontalPadding * 2f) / minimumIndicatorScale +
                    indicatorPadding * 2f,
        ).toInt()
    }
    val desiredTotal = desiredWidths.sum()
    val widths = if (desiredTotal <= availableWidth) {
        allocateProportionally(
            weights = desiredWidths,
            availableWidth = availableWidth,
        ).toMutableList()
    } else if (contentTotal <= availableWidth) {
        val availableExtraPerItem = (availableWidth - contentTotal) / contentWidths.size
        contentWidths.map { it + availableExtraPerItem }.toMutableList()
    } else {
        // 放不下了再压缩
        allocateProportionally(contentWidths, availableWidth).toMutableList()
    }
    widths[widths.lastIndex] += availableWidth - widths.sum()
    return widths
}

private fun allocateProportionally(weights: List<Int>, availableWidth: Int): List<Int> {
    val totalWeight = weights.sum().coerceAtLeast(1)
    var allocated = 0
    return weights.mapIndexed { index, weight ->
        if (index == weights.lastIndex) {
            (availableWidth - allocated).coerceAtLeast(0)
        } else {
            val width = (weight.toFloat() / totalWeight * availableWidth)
                .roundToInt()
                .coerceAtLeast(0)
            allocated += width
            width
        }
    }
}
