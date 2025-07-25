package com.hfut.schedule.ui.screen.home.search.function.school.sport.lepao

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP

import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.style.textFiledTransplant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoSet() {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val savedToken = SharedPrefs.prefs.getString("Yuntoken","")
    var inputToken by remember { mutableStateOf(savedToken ?: "") }


    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    Scaffold(
                modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("信息配置") {
                        FilledTonalIconButton(
//                            modifier = Modifier.scale(scale.value).padding(25.dp),
                            interactionSource = interactionSource,
                            onClick = {
                                // showBottomSheet_inputchanged
                                SharedPrefs.saveString("Yuntoken",inputToken)
                                Toast.makeText(MyApplication.context,"已保存",Toast.LENGTH_SHORT).show()
                            }
                        ) { Icon(Icons.Filled.Check, contentDescription = "") }
                    }
                },
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding).fillMaxSize()
                ) {
//                    MyCustomCard{
                        StyleCardListItem(
                            headlineContent = { Text(text = "请通过抓包获取常用设备的token,多设备登录会带来风险,所以要手动填写")},
                            leadingContent = { Icon(painterResource(id = R.drawable.info), contentDescription = "")}
                        )
//                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = APP_HORIZONTAL_DP),
                            value = inputToken,
                            onValueChange = { inputToken = it },
                            label = { Text("token") },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = textFiledTransplant(),
                            leadingIcon = { Icon( painterResource(R.drawable.key), contentDescription = "Localized description") }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
}
