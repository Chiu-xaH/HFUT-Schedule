package com.hfut.schedule.ui.screen.home.search.function.mail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.hfut.schedule.logic.util.network.SimpleUiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.WebDialog
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.supabase.login.getSchoolEmail
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.RowHorizontal
import com.hfut.schedule.ui.util.UIStateHolder.isSupabaseRegistering
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Mail(ifSaved : Boolean, vm : NetWorkViewModel,vmUI: UIViewModel, hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }
    TransplantListItem(
        headlineContent = { Text(text = "邮箱") },
        overlineContent = { ScrollText(text = MyApplication.EMAIL) },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.mail), contentDescription = "") },
        modifier = Modifier.clickable {
            if(ifSaved) refreshLogin()
            else
                showBottomSheet = true
        }
    )

    if (showBottomSheet ) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            isFullExpand = false,
            showBottomSheet = showBottomSheet
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("学生邮箱", isPaddingStatusBar = false)
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    MailUI(vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun MailUI(vm: NetWorkViewModel) {
    var url by remember { mutableStateOf("") }
    var used by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    WebDialog(showDialog,{ showDialog = false },url,getSchoolEmail() ?: "邮箱")

    val uiState by vm.mailData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        val token = prefs.getString("bearer","")
        token?.let {
            vm.mailData.clear()
            vm.getMailURL(it)
        }
    }

    LaunchedEffect(used) {
        refreshNetwork()
    }

    CommonNetworkScreen(uiState, loadingText = "正在登录邮箱 若加载过长请重新打开", isCenter = false, onReload = refreshNetwork) {
        val response = (uiState as SimpleUiState.Success).data
        RowHorizontal {
            Button(
                onClick = {
                    response?.data.let {
                        if(it != null) {
                            url = it
                            showDialog = true
                            used = !used
                        } else {
                            showToast( "错误 " + response?.msg)
                        }
                    }
                }
            ) {
                Text("进入邮箱")
            }
        }
        if(isSupabaseRegistering.value) {
            RowHorizontal {
                Button(
                    onClick = {
                        Starter.startWebUrl(url)
                        showToast("请检查最新收件箱 来自Supabase Auth的邮件 点击链接Confirm")
                    }
                ) {
                    Text("注册激活请选此处")
                }
            }
        }
    }
}

data class MailResponse(
    val msg : String?,
    val data : String?
)

