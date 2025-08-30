package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.util.AppAnimationManager
import com.xah.transition.component.containerShare
import com.xah.transition.component.titleElementShare
import com.xah.transition.state.TransitionState
import com.xah.uicommon.style.align.CenterScreen

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// DataStore 用法
@Composable
//@Preview
fun AnimationSetting(speed : Int = AppAnimationManager.ANIMATION_SPEED) {
    if(speed == 0) return
    val lists = listOf(
        AppAnimationManager.upDownAnimation,
        AppAnimationManager.centerAnimation,
        AppAnimationManager.getLeftRightAnimation(0),
        AppAnimationManager.fadeAnimation,
        AppAnimationManager.nullAnimation
    )
    val currentAnimationIndex by DataStoreManager.animationType.collectAsState(initial = 1)

    LazyRow() {
        item { Spacer(Modifier.width(10.dp)) }
        items(lists.size) { index ->
            Spacer(Modifier.width(5.dp))
            AnimationCard(lists[index],currentAnimationIndex,index,speed)
        }
        item {
            Spacer(Modifier.width(APP_HORIZONTAL_DP))
        }
    }
}

@Composable
fun AnimationCard(animation :  AppAnimationManager. TransferAnimation, currentAnimationIndex : Int, index : Int,speed : Int) {
    val isSelected = currentAnimationIndex == index

    val cor = rememberCoroutineScope()
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedCard (
            modifier = Modifier
                .width(108.dp)
                .height(190.dp)
                .clickable {
                    // 点击选择应用动画 标记selected=true selected只能存在一个true
                    cor.launch { DataStoreManager.saveAnimationType(index) }
                },
            colors =  CardDefaults. outlinedCardColors(containerColor = CardDefaults.outlinedCardColors().containerColor.copy(.6f)),
            border = CardDefaults.outlinedCardBorder(enabled =  isSelected),
            shape = RoundedCornerShape(14.dp),
        ) {
            Box (
                modifier = Modifier.fillMaxSize().padding(4.dp),
            ) {
                var visible by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    while (true) {
                        visible = !visible
                        delay((speed * 3).toLong()) // 延迟时间可以根据需要调整
                    }
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = visible,
                    enter = animation.enter,
                    exit = animation.exit,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(11.dp))
                    ) {
                        Text("2", modifier = Modifier.align(Alignment.Center))
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = !visible,
                    enter = animation.enter,
                    exit = animation.exit,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(.5f), shape = RoundedCornerShape(11.dp))
                    ) {
                        Text("1", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(text = animation.remark, style = MaterialTheme.typography.titleSmall,
            color = if(isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray,
            fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal
            )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
//@Preview
fun TransitionExample() {
    OutlinedCard (
        modifier = Modifier
            .width(108.dp)
            .height(190.dp),
        colors =  CardDefaults. outlinedCardColors(containerColor = CardDefaults.outlinedCardColors().containerColor.copy(.6f)),
        shape = RoundedCornerShape(14.dp),
    ) {
        Box (
            modifier = Modifier.fillMaxSize().padding(4.dp),
        ) {
            var isExpand by remember { mutableStateOf(false) }
            var index by remember { mutableIntStateOf(0) }
            LaunchedEffect(Unit) {
                while (true) {
                    delay(AppAnimationManager.ANIMATION_SPEED*3L)
                    isExpand = !isExpand
                    if(isExpand == false) {
                        delay(AppAnimationManager.ANIMATION_SPEED*2L)
                        index = ((index+1) % 4)
                    }
                }
            }
            SharedTransitionLayout() {
                AnimatedContent(
                    targetState = isExpand,
                    transitionSpec = {
                        fadeIn(animationSpec = tween()) togetherWith fadeOut(animationSpec = tween())
                    },
                    label = ""
                ) { targetLabel ->
                    Box(modifier = Modifier.clip(RoundedCornerShape(11.dp))) {
                        if(targetLabel) {
                            ScreenApp(index,this@SharedTransitionLayout,this@AnimatedContent)
                        } else {
                            ScreenBackground(isExpand,index,this@SharedTransitionLayout,this@AnimatedContent)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ScreenBackground(
    isExpanded : Boolean,
    index : Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val backgroundColor by animateFloatAsState(
        targetValue = if(isExpanded) 0.7f else 0f,
        animationSpec = spring(
            dampingRatio = TransitionState.curveStyle.dampingRatio,
            stiffness = TransitionState.curveStyle.stiffness.toFloat(),
        )
    )


    Box() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .drawWithContent {
                    drawContent()
                    drawRect(Color.Black.copy(alpha = backgroundColor))
                }

        ) {
            Text("首页", modifier = Modifier.align(Alignment.Center), fontSize = 14.sp)
        }
        Icon(
            painterResource(R.drawable.menu),
            null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 6.dp, top = 6.dp).align(Alignment.TopEnd).size(11.dp)
        )
        Row (modifier = Modifier.padding(bottom = 4.dp).align(Alignment.BottomCenter)){
            Spacer(Modifier.width(4.dp))
            repeat(4) { i ->
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .let {
                            if(i != index) {
                                it.drawWithContent {
                                    drawContent()
                                    drawRect(Color.Black.copy(alpha = backgroundColor))
                                }
                            } else it
                        }
                        .containerShare(sharedTransitionScope,animatedContentScope,"test$i", roundShape = RoundedCornerShape(6.dp))
                ) {
                }
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ScreenApp(
    index : Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .containerShare(sharedTransitionScope,animatedContentScope,"test$index", roundShape = RoundedCornerShape(6.dp))
    ) {
        Icon(
            painterResource(com.xah.transition.R.drawable.arrow_back),
            null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 6.dp, top = 6.dp).align(Alignment.TopStart).size(11.dp)
        )
        Icon(
            painterResource(R.drawable.menu),
            null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 6.dp, top = 6.dp).align(Alignment.TopEnd).size(11.dp)
        )
        Text((index+1).toString(), modifier = Modifier.align(Alignment.Center).titleElementShare(sharedTransitionScope,animatedContentScope,"test$index"))
    }
}

