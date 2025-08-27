package com.hfut.schedule.ui.screen.supabase.login

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.xah.uicommon.component.status.LoadingUI
 
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.util.GlobalUIStateHolder.isSupabaseRegistering
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.launch

private const val TAB_LOGIN = 0
private const val TAB_REG = 1

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupabaseLoginScreen(vm : NetWorkViewModel,navHostController: NavHostController) {
    val context = LocalActivity.current
    var hidden by rememberSaveable { mutableStateOf(true) }
    var loading by rememberSaveable { mutableStateOf(false) }
    val titles = remember { listOf("登录","注册") }


    val pagerState = rememberPagerState(pageCount = { titles.size })

    var isReg by remember { mutableStateOf(false) }

    val username by remember { mutableStateOf(getPersonInfo().studentId + MyApplication.EMAIL) }
    var password by remember { mutableStateOf(prefs.getString("SupabasePsk","") ?: "") }
    var password2 by remember { mutableStateOf("") }

    // 创建一个动画值，根据按钮的按下状态来改变阴影的大小
    val showTip by remember { mutableStateOf("如需共建分享信息，需要在开发者搭建的平台登录您的账号，首次使用请注册，注册之前需确认校园邮箱可用(首次使用校园邮箱请先取信息门户激活)才可收到邮件并完成注册") }
    val scope = rememberCoroutineScope()



    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                colors = topAppBarColors(containerColor = Color.Transparent, titleContentColor = MaterialTheme.colorScheme.primary),
                title = {
                    Text(
                        text = "聚在工大信息平台" , modifier = Modifier.padding(start = 10.dp)
                    )
                },
                actions = {
                    Row {
                        IconButton(onClick = {
                            context?.finish()
                        }) {
                            Icon(painterResource(id = R.drawable.close), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                navigationIcon  = {
                    Column(modifier = Modifier
                        .padding(horizontal = 23.dp)) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Supabase",
                            fontSize = 38.sp,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Spacer(modifier = Modifier.height(5.dp))

            Column(modifier = Modifier.padding(horizontal = 25.dp-APP_HORIZONTAL_DP)) {
                CustomTabRow(pagerState,titles)
            }

            Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))

            HorizontalPager(state = pagerState) { page ->

                LaunchedEffect(page) {
                    isReg = page == TAB_REG
                    hidden = page == TAB_LOGIN
                }

                Scaffold {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                            TextField(
                                enabled = false,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 25.dp),
                                value = username,
                                onValueChange = {  },
                                label = { Text("账号(为保证注册用户为在校生,不可修改)" ) },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = textFiledTransplant(),
                                leadingIcon = { Icon( painterResource(R.drawable.mail), contentDescription = "Localized description") },
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                            TextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 25.dp),
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("密码") },
                                singleLine = true,
                                colors = textFiledTransplant(),
                                visualTransformation = if (hidden) PasswordVisualTransformation()
                                else VisualTransformation.None,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                leadingIcon = { Icon(painterResource(R.drawable.key), contentDescription = "Localized description") },
                                trailingIcon = {
                                    IconButton(onClick = { hidden = !hidden }) {
                                        Icon(painter = if (hidden) painterResource(R.drawable.visibility_off) else painterResource(R.drawable.visibility), contentDescription = null)
                                    }
                                },
                                shape = MaterialTheme.shapes.medium,
                                supportingText = if(page == TAB_LOGIN) {
                                    { Text(showTip)}
                                } else null,
                            )
                        }

                        if(isReg) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                                TextField(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 25.dp),
                                    value = password2,
                                    onValueChange = { password2 = it },
                                    label = { Text("确认密码") },
                                    singleLine = true,
                                    colors = textFiledTransplant(),
                                    supportingText = { Text(showTip)},
                                    visualTransformation = if (hidden) PasswordVisualTransformation()
                                    else VisualTransformation.None,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    leadingIcon = { Icon(painterResource(R.drawable.key), contentDescription = "Localized description") },
                                    trailingIcon = {
                                        IconButton(onClick = { hidden = !hidden }) {
                                            Icon(painter = if (hidden) painterResource(R.drawable.visibility_off) else painterResource(R.drawable.visibility), contentDescription = null)
                                        }
                                    },
                                    shape = MaterialTheme.shapes.medium
                                )
                            }
                        }


                        Spacer(modifier = Modifier.height(25.dp))

                        if(loading) {
                            LoadingUI("网络节点稍慢 请稍后")
                        } else {
                            Button(
                                onClick = {
                                    scope.launch {
                                        if(isReg) {
                                            isSupabaseRegistering.value = true
                                            regSupabase(password,vm) { loading = it }
                                        } else {
                                            loading = true
                                            loginSupabaseWithPassword(password,vm,navHostController)
                                            loading = false
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp).scale(scale.value),
                                interactionSource = interactionSource,
                                shape = MaterialTheme.shapes.medium,
                                enabled = if(isReg) password2 == password else true
                            ) { Text(if(isReg) "注册" else "登录") }
                        }
                    }
                }
            }
        }
    }
}





