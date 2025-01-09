package com.hfut.schedule.ui.utils.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.hfut.schedule.App.MyApplication

@Composable
fun MyCard(modifier: Modifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 15.dp, vertical = 4.dp),containerColor : Color? = null,content: @Composable () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 1.75.dp),
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = if(containerColor == null) CardDefaults.cardColors() else CardDefaults.cardColors(containerColor = containerColor)
    ) {
        content()
    }
}


@Composable
fun LargeCard(
    title: String,
    modifier : Modifier = Modifier,
    rightTop: @Composable() (() -> Unit?)? = null,
    leftTop: @Composable() (() -> Unit?)? = null,
    color : CardColors = CardDefaults.cardColors(),
    content: @Composable () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
        modifier = modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
        colors = color
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 28.sp
                )
            },
            trailingContent = { rightTop?.let { it() } },
            leadingContent = {
                leftTop?.let { it() }
            }
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
    rightTop: @Composable() (() -> Unit?)? = null,
    leftTop: @Composable() (() -> Unit?)? = null,
    color : CardColors = CardDefaults.cardColors(),
    content: @Composable () -> Unit
) {
    val speed = MyApplication.Animation / 2
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

    val blurSize by animateDpAsState(
        targetValue = if (loading) 10.dp else 0.dp, label = ""
        ,animationSpec = tween(speed, easing = LinearOutSlowInEasing),
    )

    LargeCard(
        title,
        modifier = Modifier.scale(scale2.value),
        rightTop,
        leftTop,
        color = color,
        content = {
            Column (modifier = Modifier
                .blur(blurSize)
                .scale(scale.value)) {
                content()
            }
        }
    )
}

//封存按钮大卡片
@Composable
fun PrepareLoadLargeCard(
    title: String,
    loading: Boolean,
    rightTop: @Composable (() -> Unit?)? = null,
    leftTop: @Composable (() -> Unit?)? = null,
    color: CardColors = CardDefaults.cardColors(),
    switchAutoRefresh: Boolean = false,
    actionButtonContent: @Composable () -> Unit = {
        Text("加载")
    },
    actionButtonOnClick: () -> Unit = {
        //按钮点击逻辑，通常传入refresh()函数，通过网络请求更新数据
    },
    content: @Composable () -> Unit
) {
    var showButton by remember { mutableStateOf(true) }
    LoadingLargeCard(
        title,
        loading,
        rightTop,
        leftTop,
        color,
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                if(!switchAutoRefresh) {
                    AnimatedVisibility(
                        visible = showButton,
                        exit = fadeOut(tween(durationMillis = MyApplication.Animation)) + scaleOut(
                            tween(durationMillis = MyApplication.Animation)
                        ),
                        modifier = Modifier.align(Alignment.Center).zIndex(1f)
                    ) {
                        val blurSizeButton by animateDpAsState(
                            targetValue = if (!showButton) 4.dp else 0.dp, label = ""
                            ,animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
                        )

                        Button(
                            onClick = {
                                showButton = false
                                actionButtonOnClick.invoke()
                            },
                            modifier = Modifier.blur(blurSizeButton),
                            elevation = ButtonDefaults.elevatedButtonElevation()
                        ) {
                            actionButtonContent()
                        }
                    }
                }
                content()
            }
        }
    )
}


@Preview
@Composable
fun Previews() {
    Scaffold { innerPadding->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            //FilledCardExample()
        }
    }
}
