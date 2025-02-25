package com.hfut.schedule.ui.activity.home.cube.items.subitems

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.utils.DataStoreManager
import com.hfut.schedule.ui.utils.NavigateAndAnimationManager
import com.hfut.schedule.ui.utils.NavigateAndAnimationManager.ANIMATION_SPEED
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// DataStore 用法
@Composable
@Preview
fun AnimationSetting() {
    val lists = listOf(
        NavigateAndAnimationManager.upDownAnimation,
        NavigateAndAnimationManager.centerAnimation,
        NavigateAndAnimationManager.getLeftRightAnimation(0),
        NavigateAndAnimationManager.fadeAnimation,
        NavigateAndAnimationManager.nullAnimation
    )
    val currentAnimationIndex by DataStoreManager.animationTypeFlow.collectAsState(initial = 0)

    LazyRow() {
        item { Spacer(Modifier.width(10.dp)) }
        items(lists.size) { index ->
            Spacer(Modifier.width(5.dp))
            AnimationCard(lists[index],currentAnimationIndex,index)
        }
        item {
            Spacer(Modifier.width(AppHorizontalDp()))
        }
    }
}

@Composable
fun AnimationCard(animation :  NavigateAndAnimationManager. TransferAnimation, currentAnimationIndex : Int, index : Int) {
    val isSelected = currentAnimationIndex == index

    val cor = rememberCoroutineScope()
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedCard (
            modifier = Modifier
                .width(100.dp)
                .height(180.dp)
                .clickable {
                    // 点击选择应用动画 标记selected=true selected只能存在一个true
                    cor.launch { DataStoreManager.saveAnimationType(index) }
                },
            border = CardDefaults.outlinedCardBorder(enabled =  isSelected),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box (
                modifier = Modifier.fillMaxSize().padding(5.dp),
            ) {
                var visible by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    while (true) {
                        visible = !visible
                        delay((ANIMATION_SPEED * 2).toLong()) // 延迟时间可以根据需要调整
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
                            .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(14.dp))
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
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(.5f), shape = RoundedCornerShape(14.dp))
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
