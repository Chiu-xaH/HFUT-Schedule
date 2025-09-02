package com.hfut.schedule.ui.component.container

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.style.special.coverBlur
import com.hfut.schedule.ui.util.AppAnimationManager.ANIMATION_SPEED
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.shimmerEffect


val CARD_NORMAL_DP : Dp = 2.5.dp


data class CardBottomButton(
    val text : String,
    val show : Boolean = true,
    val clickable :  (() -> Unit)? = null
)
private val CARD_BOTTOM_BUTTON_SIZE = 14.sp
@Composable
fun ColumnScope.CardBottomButtons(buttons : List<CardBottomButton>) {
    PaddingHorizontalDivider()
    LazyRow (modifier = Modifier.align(Alignment.End).padding(horizontal = APP_HORIZONTAL_DP)) {
        items(buttons.size,key = { it }) { index ->
            val bean = buttons[index]
            with(bean) {
                if(show) {
                    Spacer(Modifier.width(APP_HORIZONTAL_DP))
                    Text(
                        text = text,
                        color =
                            if(clickable == null)
                                MaterialTheme.colorScheme.onSurface
                            else {
                                if(text.contains("删除")) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            },
                        fontSize = CARD_BOTTOM_BUTTON_SIZE,
                        modifier = Modifier
                            .padding(vertical = APP_HORIZONTAL_DP - 5.dp)
                            .let {
                                clickable?.let { click ->
                                    it.clickable { click() }
                                } ?: it
                            }
                    )
                }
            }
        }
    }
}


@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    color : Color? = null,
    shadow : Dp = 0.dp,
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable () -> Unit
) {
    val baseModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP)

    Card(
        modifier = baseModifier.then(modifier),
        shape = shape,
        elevation =  CardDefaults. cardElevation(shadow),
        colors = if(color == null) CardDefaults.cardColors() else CardDefaults.cardColors(containerColor = color)
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
fun SmallCard(
    modifier: Modifier = Modifier.fillMaxSize(),
    color : Color? = null,
    shadow : Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(shadow),
        colors = CardDefaults.cardColors(containerColor = color ?: cardNormalColor())
    ) {
        content()
    }
}

val SEARCH_FUC_CARD_HEIGHT = 72.dp
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
private fun PCardListItem(
    headlineContent :  @Composable () -> Unit,
    overlineContent  : @Composable() (() -> Unit)? = null,
    supportingContent : @Composable() (() -> Unit)? = null,
    trailingContent : @Composable() (() -> Unit)? = null,
    leadingContent : @Composable() (() -> Unit)? = null,
    color : Color? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    shadow: Dp = 0.dp,
    modifier: Modifier = Modifier,
    cardModifier : Modifier = Modifier
) {
    CustomCard( color = color, modifier = cardModifier, shape = shape, shadow = shadow) {
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
fun CardListItem(
    headlineContent :  @Composable () -> Unit,
    overlineContent  : @Composable() (() -> Unit)? = null,
    supportingContent : @Composable() (() -> Unit)? = null,
    trailingContent : @Composable() (() -> Unit)? = null,
    leadingContent : @Composable() (() -> Unit)? = null,
    color : Color? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    shadow: Dp = 0.dp,
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier,
) {
    PCardListItem(
        headlineContent, overlineContent, supportingContent, trailingContent,leadingContent, modifier = modifier, cardModifier = cardModifier,
        color = color ?: cardNormalColor(),
        shape = shape, shadow = shadow
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
    CardListItem(
        headlineContent,
        overlineContent,
        supportingContent,
        trailingContent,
        leadingContent,
        color,
        shape,
        modifier=modifier,
        cardModifier=cardModifier
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

    CustomCard(
        modifier = modifier
//            .graphicsLayer {
//            scaleX = animatedProgress.value
//            scaleY = animatedProgress.value
//        }
        ,
        color = containerColor,
    ) {
        content()
    }
}


@Composable
fun cardNormalColor() : Color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = .05f)
@Composable
fun largeCardColor() : Color = MaterialTheme.colorScheme.surfaceVariant


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
        //下面的内容
        content()
    }
}

//加载大卡片
@Composable
fun LoadingLargeCard(
    title: String,
    loading : Boolean,
    prepare : Boolean ,
    rightTop: @Composable() (() -> Unit)? = null,
    leftTop: @Composable() (() -> Unit)? = null,
    color : CardColors = CardDefaults.cardColors(containerColor = largeCardColor()),
    content: @Composable () -> Unit
) {
    val speed = ANIMATION_SPEED / 2
    val scale = animateFloatAsState(
        targetValue = if (loading) 0.9f else 1f, // 按下时为0.9，松开时为1
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
        Column (modifier = Modifier
            .coverBlur(loading)
            .let { if(!prepare && loading) it.shimmerEffect() else it }
            .scale(scale.value)
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
            content()
        }
    }
}





