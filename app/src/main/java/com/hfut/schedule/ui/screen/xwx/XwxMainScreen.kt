package com.hfut.schedule.ui.screen.xwx

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.XwxScreen
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.model.xwx.XwxLoginInfo
import com.hfut.schedule.logic.model.xwx.XwxLoginResponseBody
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getXwxPsk
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.navigateAndClear
import com.hfut.schedule.viewmodel.network.XwxViewModel
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun getXwxLogin(context : Context) : XwxLoginInfo? = withContext(Dispatchers.IO) {
    return@withContext try {
        val jStr = LargeStringDataManager.read(context, LargeStringDataManager.XWX_USER_INFO)
        withContext(Dispatchers.Default) {
            with(Gson().fromJson(jStr, XwxLoginResponseBody::class.java).result) {
                XwxLoginInfo(
                    data[0],
                    token
                )
            }
        }
    } catch (e : Exception) {
        e.printStackTrace()
        null
    }
}

suspend fun checkXwxLogin(vm: XwxViewModel,context : Context) : Boolean = withContext(Dispatchers.IO) {
    val data = vm.functionsResp.state.first()
    when(data) {
        is UiState.Error<*> -> {
            return@withContext false
        }
        is UiState.Success<*> -> {
            return@withContext true
        }
        else -> {
            // 检查
            val userInfo = getXwxLogin(context)
            if(userInfo == null) {
                return@withContext false
            }
            vm.functionsResp.clear()
            vm.getFunctions(
                schoolCode = userInfo.data.schoolCode,
                username = userInfo.data.userId,
                token = userInfo.token
            )
            val result = vm.functionsResp.state.first()
            when(result) {
                is UiState.Success<*> -> {
                    return@withContext true
                }
                else ->  {
                    return@withContext false
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XwxLoginScreen(
    vm: XwxViewModel,
    navController : NavHostController
) {
    val activity = LocalActivity.current
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = {
                    Text(
                        text = "登录", modifier = Modifier.padding(start = 10.dp)
                    )
                },
                actions = {
                    Row {
                        IconButton(onClick = {
                            activity?.finish()
                        }) {
                            Icon(painterResource(id = R.drawable.close), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                navigationIcon  = {
                    Column(modifier = Modifier
                        .padding(horizontal = APP_HORIZONTAL_DP)
                        .padding(start = 3.5.dp)) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "校务行",
                            fontSize = 38.sp,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                }
            )
        },
    ) {innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            LoginUI(vm,navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginUI(vm : XwxViewModel, navHostController: NavHostController) {
    val context = LocalContext.current
    var selectSchoolUi by remember { mutableStateOf(false) }

    var hidden by rememberSaveable { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf(prefs.getString("Username", "") ?: "") }
    val savedInfo by produceState<XwxLoginInfo?>(initialValue = null) {
        value = getXwxLogin(context)
    }

    var schoolCode by remember { mutableLongStateOf(-1L) }

    LaunchedEffect(savedInfo) {
        if(savedInfo != null) {
            username = savedInfo!!.data.userId
            schoolCode = savedInfo!!.data.schoolCode
        }
    }
    var school by remember { mutableStateOf(when(getCampusRegion()) {
        CampusRegion.HEFEI -> {
            "合肥工业大学"
        }
        CampusRegion.XUANCHENG -> {
            "宣城校区"
        }
    }) }

    LaunchedEffect(Unit) {
        getXwxPsk()?.let { psk ->
            password = psk
        }
    }
    val refreshNetwork = suspend {
        vm.schoolListResp.clear()
        vm.getSchoolList()
    }
    val uiState by vm.schoolListResp.state.collectAsState()
    LaunchedEffect(Unit) {
        if(uiState is UiState.Success) {
            return@LaunchedEffect
        }
        refreshNetwork()
    }

    val scope = rememberCoroutineScope()
    val selectedSchool = schoolCode != -1L
    if(selectSchoolUi) {
        BackHandler {
            selectSchoolUi = false
        }
        CommonNetworkScreen(uiState = uiState, onReload = refreshNetwork, loadingText = "正在加载学校列表") {
            val list = (uiState as UiState.Success).data.filter { it.schoolName.contains(school) }
            LazyColumn {
                item {
                    CustomTextField (
                        input = school,
                        onValueChange = { school = it },
                        label = { Text("搜索学校") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                painterResource(R.drawable.search),
                                contentDescription = "Localized description"
                            )
                        }
                    )
                }
                item {
                    Spacer(Modifier.height(CARD_NORMAL_DP))
                }
                items(list.size) { index ->
                    val item = list[index]
                    CardListItem(
                        headlineContent = { Text(item.schoolName) },
                        trailingContent = {
                            if (schoolCode == item.schoolCode) {
                                Icon(painterResource(R.drawable.check), null)
                            }
                        },
                        leadingContent = {
                            UrlImage(MyApplication.XWX_PICTURE_URL + item.iconUrl, useCut = false, width = 50.dp, height = 50.dp)
                        },
                        modifier = Modifier.clickable {
                            schoolCode = item.schoolCode
                            selectSchoolUi = false
                        }
                    )
                }
            }
        }
    } else {
        Column(modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))

            CardListItem(
                color = if(!selectedSchool) MaterialTheme.colorScheme.secondaryContainer else cardNormalColor(),
                headlineContent = {
                    Text(if(!selectedSchool)"请选择学校" else "已选(${schoolCode})" )
                },
                modifier = Modifier.clickable {
                    selectSchoolUi = !selectSchoolUi
                },
                cardModifier =  Modifier
                    .padding(horizontal = 25.dp-APP_HORIZONTAL_DP)
            )

            Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                TextField(
                    enabled = selectedSchool,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 25.dp),
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("账号(一般为学号)" ) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = textFiledTransplant(),
                    leadingIcon = { Icon( painterResource(R.drawable.person), contentDescription = "Localized description") },
                    trailingIcon = {
                        IconButton(onClick = {
                            username = ""
                            password = ""
                        }) { Icon(painter = painterResource(R.drawable.close), contentDescription = "description") }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                TextField(
                    enabled = selectedSchool,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 25.dp),
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码(一般为身份证号后6位)") },
                    singleLine = true,
                    colors = textFiledTransplant(),
                    visualTransformation = if (hidden) PasswordVisualTransformation()
                    else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = { Icon(painterResource(R.drawable.key), contentDescription = "Localized description") },
                    trailingIcon = {
                        IconButton(onClick = { hidden = !hidden }) {
                            val icon =
                                if (hidden) painterResource(R.drawable.visibility_off)
                                else painterResource(R.drawable.visibility)
                            val description =
                                if (hidden) "展示密码"
                                else "隐藏密码"
                            Icon(painter = icon, contentDescription = description)
                        }
                    },
                    shape = MaterialTheme.shapes.medium
                )
            }


            Spacer(modifier = Modifier.height(20.dp))

            Button(
                enabled = selectedSchool,
                onClick = {
                    scope.launch {
                        vm.loginResp.clear()
                        vm.login(schoolCode,username,password)
                        val result = vm.loginResp.state.first()
                        when(result) {
                            is UiState.Error<*> -> {
                                showToast("登陆失败")
                            }
                            is UiState.Loading -> {
                                showToast("操作过快，正在加载")
                            }
                            is UiState.Prepare -> {
                                showToast("操作过快")
                            }
                            is UiState.Success<*> -> {
                                showToast("登陆成功")
                                navHostController.navigateAndClear(XwxScreen.HOME.name)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("登录")
            }
        }
    }

}



