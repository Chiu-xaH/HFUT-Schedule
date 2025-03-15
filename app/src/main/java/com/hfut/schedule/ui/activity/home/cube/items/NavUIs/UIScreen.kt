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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.logic.utils.VersionUtils
import com.hfut.schedule.logic.utils.data.SharePrefs.saveBoolean
import com.hfut.schedule.ui.activity.home.cube.items.subitems.AnimationSetting
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.num

@Composable
fun UIScreen(navController: NavController, innerPaddings : PaddingValues,
             showlable : Boolean,
             showlablechanged :(Boolean) -> Unit,
             blur : Boolean,
             blurchanged :(Boolean) -> Unit,) {
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
        TransplantListItem(
            headlineContent = { Text(text = "色彩控制") },
            supportingContent = { Text(text = "原莫奈取色功能已移除 若有需求可使用此APP控制色彩") },
            leadingContent = { Icon(painterResource(R.drawable.color), contentDescription = "Localized description",) },
            trailingContent = { Icon(Icons.Filled.ArrowForward, null) },
            modifier = Modifier.clickable { Starter.startWebUrl("https://github.com/Mahmud0808/ColorBlendr/releases/latest") }
        )

        DividerTextExpandedWith("动效") {
            TransplantListItem(
                headlineContent = { Text(text = "层级实时模糊") },
                supportingContent = {
                    if(VersionUtils.canBlur) {
                        Text(text = "开启后将会转换部分层级渲染为实时模糊,此过程会加大性能压力")
                    } else {
                        Text(text = "需为 Android 13+")
                    }
                },
                leadingContent = { Icon(painterResource(R.drawable.deblur), contentDescription = "Localized description",) },
                trailingContent = {  Switch(checked = blur, onCheckedChange = blurchanged, enabled = VersionUtils.canBlur ) },
                modifier = Modifier.clickable { blurchanged.invoke(blur) }
            )
            TransplantListItem(
                headlineContent = { Text(text = "运动模糊") },
                supportingContent = {
                    Text(text = "部分组件的运动会伴随实时模糊效果,此过程会加大性能压力")
                },
                leadingContent = { Icon(painterResource(R.drawable.deblur), contentDescription = "Localized description",) },
                trailingContent = {  Switch(checked = true, onCheckedChange = {}, enabled =false) },
                modifier = Modifier.clickable { MyToast("默认开启") }
            )

            TransplantListItem(
                headlineContent = { Text(text = "底栏转场动画") },
                supportingContent = {
                    Text("自定义底栏切换页面进行转场的动画")
                },
                leadingContent = { Icon(painterResource(R.drawable.animation), contentDescription = "Localized description",) },
            )
            AnimationSetting()
        }
    }
}