package com.hfut.schedule.ui.screen.home.cube.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveBoolean
import com.hfut.schedule.ui.screen.home.cube.sub.AnimationSetting
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.TransplantListItem
import kotlinx.coroutines.launch

@Composable
fun UIScreen(navController: NavController, innerPaddings : PaddingValues,
             showlable : Boolean,
             showlablechanged :(Boolean) -> Unit,
             ) {
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)) {
        Spacer(modifier = Modifier.height(5.dp))

        saveBoolean("SWITCH",true,showlable)
        val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)

        val currentPureDark by DataStoreManager.pureDarkFlow.collectAsState(initial = false)
        val motionBlur by DataStoreManager.motionBlurFlow.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)
        val transition by DataStoreManager.transitionFlow.collectAsState(initial = false)
        val isCenterAnimation by DataStoreManager.motionAnimationTypeFlow.collectAsState(initial = false)
        val currentColorModeIndex by DataStoreManager.colorModeFlow.collectAsState(initial = DataStoreManager.ColorMode.AUTO.code)

        val cor = rememberCoroutineScope()

        DividerTextExpandedWith("标签") {
            TransplantListItem(
                headlineContent = { Text(text = "底栏标签") },
                supportingContent = { Text(text = "屏幕底部的Tab栏底栏标签") },
                leadingContent = { Icon(painterResource(R.drawable.label), contentDescription = "Localized description",) },
                trailingContent = { Switch(checked = showlable, onCheckedChange = showlablechanged) },
                modifier = Modifier.clickable { showlablechanged.invoke(showlable) }
            )
        }

        DividerTextExpandedWith("色彩") {
            TransplantListItem(
                headlineContent = { Text(text = "主题色彩") },
                supportingContent = { Text(text = "原自定义取色功能已移除 仅原生取色(Android 12+)") },
                leadingContent = { Icon(painterResource(R.drawable.color), contentDescription = "Localized description",) },
            )
            TransplantListItem(
                headlineContent = { Text(text = "纯黑深色背景") },
                supportingContent = { Text(text = "OLED屏使用此模式在深色模式时可获得不发光的纯黑背景") },
                leadingContent = { Icon(painterResource(R.drawable.contrast), contentDescription = "Localized description",) },
                trailingContent = {
                    Switch(checked = currentPureDark, onCheckedChange = { cor.launch { DataStoreManager.savePureDark(!currentPureDark) } })
                }
            )
            TransplantListItem(
                headlineContent = { Text(text = "深浅色") },
                supportingContent = {
                    Row {
                        FilterChip(
                            onClick = {
                                cor.launch { DataStoreManager.saveColorMode(DataStoreManager.ColorMode.LIGHT) }
                            },
                            label = { Text(text = "浅色") }, selected = currentColorModeIndex == DataStoreManager.ColorMode.LIGHT.code
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        FilterChip(
                            onClick = {
                                cor.launch { DataStoreManager.saveColorMode(DataStoreManager.ColorMode.DARK) }
                            },
                            label = { Text(text = "深色") }, selected = currentColorModeIndex == DataStoreManager.ColorMode.DARK.code
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        FilterChip(
                            onClick = {
                                cor.launch { DataStoreManager.saveColorMode(DataStoreManager.ColorMode.AUTO) }
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

        DividerTextExpandedWith("动效") {
            TransplantListItem(
                headlineContent = { Text(text = "层级实时模糊") },
                supportingContent = {
                    Text(text = "开启后将会转换部分层级渲染为实时模糊,此过程会加大性能压力" )
                },
                leadingContent = { Icon(painterResource(R.drawable.deblur), contentDescription = "Localized description",) },
                trailingContent = {  Switch(checked = blur, onCheckedChange = { cor.launch {  DataStoreManager.saveHazeBlur(!blur) } } ) },
                modifier = Modifier.clickable {
                    cor.launch {  DataStoreManager.saveHazeBlur(!blur) }
                }
            )

            TransplantListItem(
                headlineContent = { Text(text = "运动模糊") },
                supportingContent = {
                    if(AppVersion.CAN_MOTION_BLUR) {
                        Text(text = "部分UI的运动会伴随实时模糊效果,此过程会加大动画过程的压力")
                    } else {
                        Text(text = "需为 Android 12+")
                    }
                },
                leadingContent = { Icon(painterResource(R.drawable.motion_mode), contentDescription = "Localized description",) },
                trailingContent = {  Switch(checked = motionBlur, onCheckedChange = { cor.launch { DataStoreManager.saveMotionBlur(!motionBlur) } },enabled = AppVersion.CAN_MOTION_BLUR) },
                modifier = Modifier.clickable {
                    if(AppVersion.CAN_MOTION_BLUR) {
                        cor.launch { DataStoreManager.saveMotionBlur(!motionBlur) }
                    }
                }
            )
            TransplantListItem(
                headlineContent = { Text(text = "增强转场动画") },
                supportingContent = {
                    Text(text = "转场动画伴随较高强度的实时细节渲染，可能会在某些设备上掉帧")
                },
                leadingContent = { Icon(painterResource(R.drawable.transition_fade), contentDescription = "Localized description",) },
                trailingContent = {  Switch(checked = transition, onCheckedChange = { cor.launch { DataStoreManager.saveTransition(!transition) } }) },
                modifier = Modifier.clickable { cor.launch { DataStoreManager.saveTransition(!transition) } }
            )
            TransplantListItem(
                headlineContent = { Text(text = "转场动画曲线") },
                supportingContent = {
                    Row {
                        FilterChip(
                            onClick = {
                                cor.launch { DataStoreManager.saveMotionAnimation(true) }
                            },
                            label = { Text(text = "向中间运动") }, selected = isCenterAnimation
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        FilterChip(
                            onClick = {
                                cor.launch { DataStoreManager.saveMotionAnimation(false) }
                            },
                            label = { Text(text = "直接展开") }, selected = !isCenterAnimation
                        )
                    }
                },
                leadingContent = { Icon(painterResource(R.drawable.moving), contentDescription = "Localized description",) },
            )

            TransplantListItem(
                headlineContent = { Text(text = "底栏转场动画") },
                supportingContent = {
                    Text("自定义底栏切换页面进行转场的动画")
                },
                leadingContent = { Icon(painterResource(R.drawable.animation), contentDescription = "Localized description",) },
            )
            AnimationSetting()
            Spacer(Modifier.height(innerPaddings.calculateBottomPadding()))
        }
    }
}