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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.AndroidVersion.canBlur
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.SharePrefs.saveBoolean
import com.hfut.schedule.ui.activity.home.cube.items.subitems.monet.MonetColorItem
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun UIScreen(navController: NavController, innerPaddings : PaddingValues,
             showlable : Boolean,
             showlablechanged :(Boolean) -> Unit,
             blur : Boolean,
             blurchanged :(Boolean) -> Unit,) {
    // Design your second screen here
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)) {
        Spacer(modifier = Modifier.height(5.dp))

        saveBoolean("SWITCH",true,showlable)
        saveBoolean("SWITCHBLUR",true,blur)

        var sliderPosition by remember { mutableStateOf((prefs.getInt("ANIMATION", MyApplication.Animation)).toFloat()) }
        val bd = BigDecimal(sliderPosition.toString())
        val str = bd.setScale(0, RoundingMode.HALF_UP).toString()
        SharePrefs.saveInt("ANIMATION",sliderPosition.toInt())

        ListItem(
            headlineContent = { Text(text = "底栏标签") },
            supportingContent = { Text(text = "屏幕底部的Tab栏底栏标签") },
            leadingContent = { Icon(painterResource(R.drawable.label), contentDescription = "Localized description",) },
            trailingContent = { Switch(checked = showlable, onCheckedChange = showlablechanged) },
            modifier = Modifier.clickable { showlablechanged }
        )
        ListItem(
            headlineContent = { Text(text = "实时模糊") },
            supportingContent = {
                if(canBlur) {
                    Text(text = "开启后将会转换部分渲染为实时模糊")
                } else {
                    Text(text = "需为 Android 13+")
                }
            },
            leadingContent = { Icon(painterResource(R.drawable.deblur), contentDescription = "Localized description",) },
            trailingContent = {  Switch(checked = blur, onCheckedChange = blurchanged, enabled = canBlur ) },
            modifier = Modifier.clickable { blurchanged }
        )

        ListItem(
            headlineContent = { Text(text = "转场动画") },
            supportingContent = {
                Column {
                    Text(text = "时长 $str 毫秒 重启后生效")
                    Slider(
                        value = sliderPosition,
                        onValueChange = {
                            sliderPosition = it
                            SharePrefs.saveInt("ANIMATION",sliderPosition.toInt())
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.secondary,
                            activeTrackColor = MaterialTheme.colorScheme.secondary,
                            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        steps = 19,
                        valueRange = 0f..1000f
                    )
                }
            },
            leadingContent = { Icon(painterResource(R.drawable.animation), contentDescription = "Localized description",) },
            trailingContent = {  Switch(checked = true, onCheckedChange = null, enabled = false ) },
            modifier = Modifier.clickable {  }
        )

        MonetColorItem()
    }

}