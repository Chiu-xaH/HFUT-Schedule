package com.hfut.schedule.ui.activity.home.cube.items.NavUIs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.AndroidVersion.canBlur
import com.hfut.schedule.logic.utils.data.SharePrefs.saveBoolean
import com.hfut.schedule.ui.activity.home.cube.items.subitems.AnimationSetting
import com.hfut.schedule.ui.activity.home.cube.items.subitems.monet.MonetColorItem
import com.hfut.schedule.ui.utils.BlurManager
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.TransplantListItem

@Composable
fun UIScreen(navController: NavController, innerPaddings : PaddingValues,
             showlable : Boolean,
             showlablechanged :(Boolean) -> Unit,
             blur : Boolean,
             blurchanged :(Boolean) -> Unit,) {
    var animationBlur by remember { mutableStateOf(BlurManager.getValue()) }
    var animation by remember { mutableStateOf(true) }
    // Design your second screen here
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)) {
        Spacer(modifier = Modifier.height(5.dp))

        saveBoolean("SWITCH",true,showlable)
        saveBoolean("SWITCHBLUR",true,blur)



        TransplantListItem(
            headlineContent = { Text(text = "底栏标签") },
            supportingContent = { Text(text = "屏幕底部的Tab栏底栏标签") },
            leadingContent = { Icon(painterResource(R.drawable.label), contentDescription = "Localized description",) },
            trailingContent = { Switch(checked = showlable, onCheckedChange = showlablechanged) },
            modifier = Modifier.clickable { showlablechanged.invoke(showlable) }
        )

        DividerTextExpandedWith("动效") {
            TransplantListItem(
                headlineContent = { Text(text = "层级实时模糊") },
                supportingContent = {
                    if(canBlur) {
                        Text(text = "开启后将会转换部分层级渲染为实时渐变模糊,此过程会加大性能压力")
                    } else {
                        Text(text = "需为 Android 13+")
                    }
                },
                leadingContent = { Icon(painterResource(R.drawable.deblur), contentDescription = "Localized description",) },
                trailingContent = {  Switch(checked = blur, onCheckedChange = blurchanged, enabled = canBlur ) },
                modifier = Modifier.clickable { blurchanged.invoke(blur) }
            )
            TransplantListItem(
                headlineContent = { Text(text = "运动实时模糊") },
                supportingContent = { Text(text = "开启后部分运动状态的组件伴随实时模糊,此过程可能会加大性能压力") },
                leadingContent = { Icon(painterResource(R.drawable.motion_mode), contentDescription = "Localized description",) },
                trailingContent = {
                    Switch(
                        checked = animationBlur,
                        onCheckedChange = {
                            animationBlur = it
                            BlurManager.setValue(animationBlur)
                        }
                    )
                },
                modifier = Modifier.clickable {
                    animationBlur = !animationBlur
                    BlurManager.setValue(animationBlur)
                }
            )
            val cor = rememberCoroutineScope()

            TransplantListItem(
                headlineContent = { Text(text = "底栏转场动画") },
                supportingContent = {
                    Text("自定义底栏切换页面进行转场的动画")
                },
                leadingContent = { Icon(painterResource(R.drawable.animation), contentDescription = "Localized description",) },
            )
            AnimationSetting()
        }

        DividerTextExpandedWith("主题色") {
            MonetColorItem()
        }


    }

}