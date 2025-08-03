package com.hfut.schedule.ui.component.container

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.ui.style.appBlur
import com.hfut.schedule.ui.util.AppAnimationManager.ANIMATION_SPEED

@Composable
fun MyCustomCard(
    modifier: Modifier = Modifier,
    containerColor : Color? = null,
    hasElevation : Boolean = false,
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = if(hasElevation) 1.75.dp else 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP),
        shape = shape,
        colors = if(containerColor == null) CardDefaults.cardColors() else CardDefaults.cardColors(containerColor = containerColor)
    ) {
        content()
    }
}

@Composable
fun mixedCardNormalColor(): Color {
    val overlay = cardNormalColor()
    val base = MaterialTheme.colorScheme.surface
    return overlay.compositeOver(base)
}
// 小卡片
@Composable
fun SmallCard(modifier: Modifier = Modifier.fillMaxSize(), color : Color? = null,content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = color ?: cardNormalColor())
    ) {
        content()
    }
}

@Composable
fun TransplantListItem(
    headlineContent :  @Composable () -> Unit,
    overlineContent  : @Composable() (() -> Unit)? = null,
    supportingContent : @Composable() (() -> Unit)? = null,
    trailingContent : @Composable() (() -> Unit)? = null,
    leadingContent : @Composable() (() -> Unit)? = null,
    colors : Color? = null,
    usePadding : Boolean = true,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        colors = ListItemDefaults.colors(containerColor = colors ?: Color.Transparent) ,
        trailingContent = trailingContent,
        leadingContent = leadingContent,
        modifier = modifier.padding(horizontal =
            if(leadingContent == null) {
                if (usePadding) 2.dp else 0.dp
            } else {
                0.dp
            }
        )
    )
}

@Composable
private fun CardListItem(
    headlineContent :  @Composable () -> Unit,
    overlineContent  : @Composable() (() -> Unit)? = null,
    supportingContent : @Composable() (() -> Unit)? = null,
    trailingContent : @Composable() (() -> Unit)? = null,
    leadingContent : @Composable() (() -> Unit)? = null,
    hasElevation : Boolean = false,
    containerColor : Color? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    modifier: Modifier = Modifier,
    cardModifier : Modifier = Modifier
) {
    MyCustomCard(hasElevation = hasElevation, containerColor = containerColor, modifier = cardModifier, shape = shape) {
        TransplantListItem(
            headlineContent = headlineContent,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            trailingContent = trailingContent,
            leadingContent = leadingContent,
            usePadding = false,
            modifier = modifier
        )
    }
}


@Composable
fun StyleCardListItem(
    headlineContent :  @Composable () -> Unit,
    overlineContent  : @Composable() (() -> Unit)? = null,
    supportingContent : @Composable() (() -> Unit)? = null,
    trailingContent : @Composable() (() -> Unit)? = null,
    leadingContent : @Composable() (() -> Unit)? = null,
    color : Color? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier,
) {
    CardListItem(
        headlineContent, overlineContent, supportingContent, trailingContent,leadingContent, modifier = modifier, cardModifier = cardModifier,
        hasElevation = false,
        containerColor = color ?: cardNormalColor(),
        shape = shape
    )
}
// 用在LazyColumn
@Composable
fun AnimationCardListItem(
    headlineContent :  @Composable () -> Unit,
    overlineContent  : @Composable() (() -> Unit)? = null,
    supportingContent : @Composable() (() -> Unit)? = null,
    trailingContent : @Composable() (() -> Unit)? = null,
    leadingContent : @Composable() (() -> Unit)? = null,
    color : Color? = null,
    index : Int,
    scale : Float = 0.8f,
    shape: Shape = MaterialTheme.shapes.medium,
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier,
) {
//    val animatedProgress = remember { Animatable(scale) }

//    LaunchedEffect(index) {
//        animatedProgress.animateTo(
//            targetValue = 1f,
//            animationSpec = tween(ANIMATION_SPEED, easing = EaseInOutQuad)
//        )
//    }
    StyleCardListItem(
        headlineContent,
        overlineContent,
        supportingContent,
        trailingContent,
        leadingContent,
        color,
        shape,
        modifier,
        cardModifier
//            .graphicsLayer {
//            scaleX = animatedProgress.value
//            scaleY = animatedProgress.value
//        },
    )
}

@Composable
fun AnimationCustomCard(
    modifier: Modifier = Modifier,
    containerColor : Color? = null,
    hasElevation : Boolean = false,
    index : Int = 1,
    scale : Float = 0.8f,
    content: @Composable () -> Unit) {
//    val animatedProgress = remember { Animatable(scale) }

//    LaunchedEffect(index) {
//        animatedProgress.animateTo(
//            targetValue = 1f,
//            animationSpec = tween(ANIMATION_SPEED, easing = EaseInOutQuad)
//        )
//    }

    MyCustomCard(
        modifier = modifier
//            .graphicsLayer {
//            scaleX = animatedProgress.value
//            scaleY = animatedProgress.value
//        }
        ,
        containerColor = containerColor,
        hasElevation = hasElevation,
    ) {
        content()
    }
}


@Composable
fun cardNormalColor() : Color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = .05f)
@Composable
fun largeCardColor() : Color = MaterialTheme.colorScheme.surfaceVariant

val CARD_NORMAL_DP : Dp = 2.5.dp

val APP_HORIZONTAL_DP : Dp = 15.dp

@Composable
fun LargeCard(
    title: String,
    modifier : Modifier = Modifier,
    rightTop:  @Composable() (() -> Unit)? = null,
    leftTop:  @Composable() (() -> Unit)? = null,
    color : CardColors = CardDefaults.cardColors(containerColor = largeCardColor()),
    content: @Composable () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = APP_HORIZONTAL_DP),
        modifier = modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
        colors = color
    ) {
        TransplantListItem(
            headlineContent = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(top = APP_HORIZONTAL_DP/6, bottom = 0.dp)
                )
            },
            trailingContent = rightTop,
            leadingContent = leftTop,
            usePadding = false
        )
//        PaddingHorizontalDivider(isDashed = true)
        //下面的内容
        content()
    }
}

//加载大卡片
@Composable
fun LoadingLargeCard(
    title: String,
    loading : Boolean,
    rightTop: @Composable() (() -> Unit)? = null,
    leftTop: @Composable() (() -> Unit)? = null,
    color : CardColors = CardDefaults.cardColors(containerColor = largeCardColor()),
    content: @Composable () -> Unit
) {
    val speed = ANIMATION_SPEED / 2
    val scale = animateFloatAsState(
        targetValue = if (loading) 0.9f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(speed, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    val scale2 = animateFloatAsState(
        targetValue = if (loading) 0.97f else 1f, // 按下时为0.9，松开时为1
        animationSpec = tween(speed, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = APP_HORIZONTAL_DP),
        modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP, vertical = 5.dp).scale(scale2.value),
        shape = MaterialTheme.shapes.medium,
        colors = color
    ) {
        //下面的内容
        Column (modifier = appBlur(loading).scale(scale.value)) {
            TransplantListItem(
                headlineContent = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 28.sp,
                        modifier = Modifier.padding(top = APP_HORIZONTAL_DP/6, bottom = 0.dp)
                    )
                },
                trailingContent = rightTop,
                leadingContent = leftTop,
                usePadding = false
            )
//            PaddingHorizontalDivider(isDashed = true)
            content()
        }
    }
}


