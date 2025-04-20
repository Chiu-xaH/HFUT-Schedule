package com.hfut.schedule.ui.screen.supabase.login

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.SupabaseScreen
import com.hfut.schedule.logic.util.network.reEmptyLiveDta
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.Starter.loginSupabase
import com.hfut.schedule.logic.util.sys.Starter.startSupabase
import com.hfut.schedule.ui.component.CustomTabRow
import com.hfut.schedule.ui.component.LoadingUI
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.search.function.person.getPersonInfo
import com.hfut.schedule.ui.screen.supabase.login.UIStateHolder.isSupabaseRegistering
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.util.navigateAndClear
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAB_LOGIN = 0
private const val TAB_REG = 1

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupabaseLoginScreen(vm : NetWorkViewModel,vmUI: UIViewModel,navHostController: NavHostController) {
    val context = LocalActivity.current
    var hidden by rememberSaveable { mutableStateOf(true) }
    var loading by rememberSaveable { mutableStateOf(false) }


    val pagerState = rememberPagerState(pageCount = { 2 })

    var isReg by remember { mutableStateOf(false) }
    val titles = remember { listOf("登录","注册") }

    val username by remember { mutableStateOf(getPersonInfo().username + MyApplication.EMAIL) }
    var password by remember { mutableStateOf(prefs.getString("SupabasePsk","") ?: "") }
    var password2 by remember { mutableStateOf("") }

    // 创建一个动画值，根据按钮的按下状态来改变阴影的大小
    val showTip by remember { mutableStateOf("如需共建分享信息，需要在开发者搭建的平台登录您的账号，首次使用请注册") }
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
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "聚在工大信息平台  " ,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    }
                },
                actions = {
                    Row {
                        IconButton(onClick = {
                            context?.finish()
                        }) {
                            Icon(painterResource(id = R.drawable.logout), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
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
    ) {innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Spacer(modifier = Modifier.height(5.dp))

            CustomTabRow(pagerState,titles, modifier = Modifier.padding(horizontal = 20.dp))

            Spacer(modifier = Modifier.height(15.dp))

            HorizontalPager(state = pagerState) { page ->

                LaunchedEffect(page) {
                    isReg = page == TAB_REG
                    hidden = page == TAB_LOGIN
                }

                Scaffold {
                    Column {

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
                                        } else
                                            loginSupabaseWithPassword(password,vm,navHostController) { loading = it }
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

// 逻辑层
fun getSchoolEmail() : String? = getPersonInfo().username?.let { it + MyApplication.EMAIL }

suspend fun loginSupabaseWithPassword(password : String,vm: NetWorkViewModel,navHostController: NavHostController,onResult: (Boolean) -> Unit) = withContext(Dispatchers.IO) {
    Handler(Looper.getMainLooper()).post {
        vm.supabaseLoginResp.observeForever { result ->
            if (result != null) {
                onResult(false)
                if (result.contains("error")) {
                    showToast("密码错误或未注册")
                } else {
                    CoroutineScope(Job()).launch {
                        launch { saveSupabaseRefreshToken(vm) }
                        val isSuccessful = async { saveSupabaseJwt(vm) }.await()
                        launch {
                            if(isSuccessful) {
                                Handler(Looper.getMainLooper()).post { navHostController.navigateAndClear(SupabaseScreen.HOME.name) }
                                showToast("登录成功")
                            }
                        }
                    }
                }
            }
        }
    }
    launch { saveString("SupabasePsk",password) }
    async { onResult(true) }.await()
    launch { vm.supabaseLoginWithPassword(password) }
}

private suspend fun regSupabase(password : String, vm: NetWorkViewModel,onResult : (Boolean) -> Unit) = withContext(Dispatchers.IO) {
    Handler(Looper.getMainLooper()).post {
        vm.supabaseRegResp.observeForever { result ->
            result?.let {
                onResult(false)
                showToast(
                    if (it.contains("email_verified"))
                        "已注册，请回到 查询中心-邮箱 检查收件箱，点击链接激活成功后再来登录"
                    else
                        "已存在账号或账号不合法"
                )
            }
        }
    }
    launch { saveString("SupabasePsk",password) }
    async { onResult(true) }.await()
    launch { vm.supabaseReg(password) }
}

private suspend fun saveSupabaseJwt(vm: NetWorkViewModel) : Boolean = withContext(Dispatchers.IO) {
    val token = getJwt(vm) ?: return@withContext false
    DataStoreManager.saveSupabaseJwt(token)
    return@withContext true
}

private suspend fun saveSupabaseRefreshToken(vm: NetWorkViewModel) : Boolean = withContext(Dispatchers.IO) {
    val token = getRefreshToken(vm) ?: return@withContext false
    DataStoreManager.saveSupabaseRefreshToken(token)
    return@withContext true
}
// 一键登录
fun loginSupabaseWithCheck(jwt : String,refreshToken: String,vm: NetWorkViewModel,onResult : (Boolean) -> Unit) {

    if(jwt.isEmpty() || jwt.isBlank()) {
        onResult(false)
        loginSupabase()
        return
    }

    lateinit var checkObserver: Observer<Boolean?> // 延迟初始化观察者
    lateinit var loginObserver: Observer<String?> // 延迟初始化观察者


    checkObserver = Observer { result ->
        if(result != null) {
            if (result) {
                // 登录有效
                onResult(false)
                startSupabase()
                vm.supabaseCheckResp.removeObserver(checkObserver)
                return@Observer
            } else {
                // 需要刷新
                vm.supabaseCheckResp.removeObserver(checkObserver)
                // 只能使用密码登录法
                if(refreshToken.isEmpty() || refreshToken.isBlank()) {
                    onResult(false)
                    loginSupabase()
                    return@Observer
                }
                // 使用刷新登录
                vm.supabaseLoginWithRefreshToken(refreshToken)
                vm.supabaseLoginResp.observeForever(loginObserver)
                return@Observer
            }
        }
    }

    loginObserver = Observer { result ->
        if(result != null) {
            onResult(false)
            if (!result.contains("error")) {
                // 登录成功 保存新的JWT与TOKEN
                CoroutineScope(Job()).launch {
                    async {
                        launch { saveSupabaseJwt(vm) }
                        launch { saveSupabaseRefreshToken(vm) }
                    }.await()
                    launch {
                        startSupabase()
                        vm.supabaseLoginResp.removeObserver(loginObserver)
                        return@launch
                    }
                }
            } else {
                // 只能使用密码登录法
                vm.supabaseLoginResp.removeObserver(loginObserver)
                onResult(true)
                loginSupabase()
                vm.supabaseLoginResp.removeObserver(loginObserver)
                return@Observer
            }
        }
    }

    CoroutineScope(Job()).launch {
        val isLogin = async { vm.supabaseCheckResp.value }.await()
        if(isLogin == true) {
            onResult(false)
            startSupabase()
            return@launch
        } else {
            async { reEmptyLiveDta(vm.supabaseCheckResp) }.await()
            async { onResult(true) }.await()
            async { vm.supabaseCheckJwt(jwt) }.await()
            launch {
                Handler(Looper.getMainLooper()).post {
                    vm.supabaseCheckResp.observeForever(checkObserver)
                }
            }
        }
    }
}

private fun getJwt(vm: NetWorkViewModel) : String? = try {
    Gson().fromJson(vm.supabaseLoginResp.value,SupabaseLoginResponse::class.java).token
} catch (e : Exception) {
    null
}
private fun getRefreshToken(vm: NetWorkViewModel) : String? = try {
    Gson().fromJson(vm.supabaseLoginResp.value,SupabaseLoginResponse::class.java).refreshToken
} catch (e : Exception) {
    null
}


private data class SupabaseLoginResponse(
    @SerializedName("access_token") val token : String,
    @SerializedName("refresh_token") val refreshToken : String
)


object UIStateHolder {
    var isSupabaseRegistering = mutableStateOf<Boolean>(false)
}
