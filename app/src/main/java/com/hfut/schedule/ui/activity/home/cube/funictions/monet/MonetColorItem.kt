package com.hfut.schedule.ui.activity.home.cube.funictions.monet

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.utils.MyToast

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun MonetColorItem() {
    var expandItems by remember { mutableStateOf(prefs.getBoolean("expandMonet",false)) }
    var text by remember { mutableStateOf("") }
    text = when(expandItems){
        true -> "收回"
        false -> "展开"
    }
    val switch_color = prefs.getBoolean("SWITCHCOLOR",AndroidVersion.sdkInt < 31)
    var DynamicSwitch by remember { mutableStateOf(switch_color) }

        ListItem(
            headlineContent = { Text(text = "取色算法") },
            supportingContent = {
            Column {
                Text(text = if(AndroidVersion.sdkInt >= 31)"若无法调用Android 12+的原生动态取色的系统,可使用莫奈取色算法,点击${text}" else "仅 Android 12+ 支持原生取色 点击${text}")
                Row {
                    FilterChip(
                        onClick = {
                            DynamicSwitch = true
                            MyToast("切换算法后,重启应用生效")
                            SharePrefs.saveBoolean("SWITCHCOLOR", true, DynamicSwitch)
                        },
                        label = { Text(text = "莫奈取色") }, selected = DynamicSwitch)
                    Spacer(modifier = Modifier.width(10.dp))
                    FilterChip(
                        onClick = {
                            MyToast("切换算法后,重启应用生效")
                            expandItems = false
                            SharePrefs.saveBoolean("expandMonet",false,expandItems)
                            DynamicSwitch = false
                            SharePrefs.saveBoolean("SWITCHCOLOR", true, DynamicSwitch)
                        },
                        label = { Text(text = "原生取色") },
                        selected = !DynamicSwitch,
                        enabled = AndroidVersion.sdkInt >= 31 )
                }
            }
                            },
            leadingContent = { Icon(painterResource(R.drawable.color), contentDescription = "Localized description",) },
            trailingContent = { if(DynamicSwitch) {
                IconButton(onClick = {
                    expandItems = !expandItems
                    SharePrefs.saveBoolean("expandMonet",false,expandItems)
                }) { Icon( if(!expandItems)Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp, contentDescription = "")}
            }
                              },
            modifier = Modifier.clickable {
                if(DynamicSwitch) {
                    expandItems = !expandItems
                    SharePrefs.saveBoolean("expandMonet",false,expandItems)
                } else MyToast("未打开")
            }
        )


    AnimatedVisibility(
        visible = expandItems,
        enter = slideInVertically(
            initialOffsetY = { -40 }
        ) + expandVertically(
            expandFrom = Alignment.Top
        ) + scaleIn(
            // Animate scale from 0f to 1f using the top center as the pivot point.
            transformOrigin = TransformOrigin(0.5f, 0f)
        ) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .requiredHeight(240.dp)) {
            MonetUI()
        }
    }

    Spacer(modifier = Modifier.height(5.dp))

}