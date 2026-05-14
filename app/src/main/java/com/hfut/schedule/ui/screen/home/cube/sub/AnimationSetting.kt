package com.hfut.schedule.ui.screen.home.cube.sub

//import androidx.compose.ui.tooling.preview.Preview

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
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// DataStore 用法
@Composable
//@Preview
fun AnimationSetting(speed : Int = AppAnimationManager.ANIMATION_SPEED) {
    if(speed == 0) return
    val lists = listOf(
        AppAnimationManager.centerAnimation,
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
private fun AnimationCard(animation :  AppAnimationManager. TransferAnimation, currentAnimationIndex : Int, index : Int,speed : Int) {
    val isSelected = currentAnimationIndex == index

    val cor = rememberCoroutineScope()
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedCard (
            modifier = Modifier
                .width(90.dp)
                .height(160.dp)
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
                            .background(MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(11.dp))
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
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(.5f), shape = RoundedCornerShape(11.dp))
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
