package com.hfut.schedule.ui.activity.home.cube.items.subitems

import androidx.camera.core.processing.SurfaceProcessorNode.In
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.saveBoolean
import com.hfut.schedule.logic.utils.data.SharePrefs.saveString
import com.hfut.schedule.ui.utils.components.appHorizontalDp
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockUI(hazeState: HazeState) {
    val switch_pin = SharePrefs.prefs.getBoolean("SWITCHPIN",false)
    var pin by remember { mutableStateOf(switch_pin) }
    saveBoolean("SWITCHPIN", false,pin)

    var psk = SharePrefs.prefs.getString("pins",null)
    // var password by remember { mutableStateOf(psk ?: "") }
    var input by remember { mutableStateOf("") }
    // Save("pins",password )
    var showDialog by remember { mutableStateOf(psk == null) }


    var sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    TransplantListItem(
        headlineContent = { Text(text = "需要密码") },
        supportingContent = { Text(text = "在调用支付时选择是否需要验证") },
        leadingContent = {
            Icon(painter = painterResource(id = R.drawable.lock), contentDescription = "")
        },
        trailingContent = {
            Switch(checked = pin, onCheckedChange = {_->
                showDialog = true
            })
        }
    )

    // if(pin) {
    if (showDialog) {
        // if(password.length != 6)
        HazeBottomSheet (
            onDismissRequest = { showDialog = false },
            autoShape = false,
            hazeState = hazeState,
            showBottomSheet = showDialog
//            sheetState = sheetState,
            // shape = Round(sheetState)
        ) {
            Column {
                Spacer(Modifier.height(appHorizontalDp()*1.5f))
                CirclePoint(text = if(!pin)"录入新密码" else "请输入密码", password = input)
                Spacer(modifier = Modifier.height(20.dp))
                KeyBoard(
                    modifier = Modifier.padding(horizontal = appHorizontalDp()),
                    onKeyClick = { num ->
                        if (input.length < 6) {
                            input += num.toString()
                        }
                        if(input.length == 6) {
                            if(pin) {
                                psk = SharePrefs.prefs.getString("pins",null)
                                if(input == psk) {
                                    saveString("pins",null)
                                    pin = false
                                    MyToast("已移除密码")
                                    showDialog = false
                                } else {
                                    input = ""
                                }

                            } else {
                                saveString("pins",input)
                                pin = true
                                showDialog = false
                                MyToast("新建密码成功 密码为${input}")
                            }
                        }
                    },
                    onBackspaceClick = {
                        if (input.isNotEmpty()) {
                            input = input.dropLast(1)
                        }
                    }
                )
            }
        }
    }
    if(pin)
        TransplantListItem(
            headlineContent = { Text(text = "生物识别") },
            supportingContent = { Text(text = "调用指纹传感器以免密码") },
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.how_to_reg), contentDescription = "")
            },
            modifier = Modifier.clickable {
                MyToast("正在开发")
            }
        )
    //}
}

@Composable
fun KeyBoard(modifier : Modifier = Modifier, onKeyClick: (Int) -> Unit, onBackspaceClick: () -> Unit) {
    Column(modifier = modifier) {
        for(i in 1 until 8 step 3) {
            Divider()
            Row {
                Key(num = i, modifier = Modifier
                    .weight(.33f)
                    .height(65.dp),onKeyClick = onKeyClick,
                )
                //Divider()
                Key(num = i+1,modifier = Modifier
                    .weight(.33f)
                    .height(65.dp),onKeyClick = onKeyClick,)
                //Divider()
                Key(num = i+2,modifier = Modifier
                    .weight(.33f)
                    .height(65.dp),onKeyClick = onKeyClick,)
            }
        }
        Divider()
        Row {
            TextButton(onClick = { /*TODO*/ }, modifier = Modifier
                .weight(.33f)
                .height(65.dp)
                ,shape = RoundedCornerShape(0.dp)
            ) {
                Text("", fontSize = 13.sp)
            }
            //Divider()
            Key(num = 0,modifier = Modifier
                .weight(.33f)
                .height(65.dp),onKeyClick = onKeyClick
            )
            //Divider()
            TextButton(onClick =  onBackspaceClick , modifier = Modifier
                .weight(.33f)
                .height(65.dp),shape = RoundedCornerShape(0.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.backspace), contentDescription = "", modifier = Modifier.size(30.dp))
            }
        }
        // Spacer(modifier = Modifier.height(10.dp))
    }
}
@Composable
fun Key(num : Int, modifier: Modifier = Modifier, onKeyClick: (Int) -> Unit) {
    TextButton(onClick = { onKeyClick(num) }, modifier = modifier, shape = RoundedCornerShape(0.dp)) {
        Text(num.toString(), fontSize = 28.sp)
    }
}

@Composable
fun CirclePoint(modifier: Modifier = Modifier, text : String, password : String,num : Int = 6) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
            Text(text = text)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
            for(i in 0 until num) {
                Icon(painter = painterResource(id = if(i < password.length) R.drawable.circle_filled else R.drawable.circle), contentDescription = "",modifier = Modifier.padding(7.dp).size(17.dp),tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}