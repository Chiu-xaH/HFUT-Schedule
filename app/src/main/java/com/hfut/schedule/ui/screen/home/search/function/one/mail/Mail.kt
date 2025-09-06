package com.hfut.schedule.ui.screen.home.search.function.one.mail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.TransplantListItem
   
 
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.supabase.login.getSchoolEmail
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.GlobalUIStateHolder.isSupabaseRegistering
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.component.text.ScrollText
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Mail(
    vm : NetWorkViewModel,
    hazeState: HazeState
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    TransplantListItem(
        headlineContent = { ScrollText(text = "校园邮箱") },
//        overlineContent = { ScrollText(text = MyApplication.EMAIL) },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.mail), contentDescription = "") },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )

    if (showBottomSheet ) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            autoShape = false,
            isFullExpand = true,
            showBottomSheet = showBottomSheet
        ) {
            Column{
                HazeBottomSheetTopBar("校园邮箱", isPaddingStatusBar = false)
                MailUI(vm)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun MailUI(vm: NetWorkViewModel) {
    var used by remember { mutableStateOf(false) }

    val uiState by vm.mailData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        val token = prefs.getString("bearer","")
        token?.let {
            vm.mailData.clear()
            vm.getMailURL(it)
        }
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(used) {
        refreshNetwork()
    }
    val context = LocalContext.current
    DividerTextExpandedWith (getSchoolEmail() ?: MyApplication.EMAIL) {
        CommonNetworkScreen(uiState, loadingText = "正在登录邮箱", isFullScreen = false, onReload = refreshNetwork) {
            val response = (uiState as UiState.Success).data
            Column {
                Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                    LargeButton(
                        onClick = {
                            scope.launch {
                                response.data.let {
                                    if(it != null) {
                                        used = !used
                                        Starter.startWebView(context,it,getSchoolEmail() ?: "邮箱", icon = R.drawable.mail)
                                    } else {
                                        showToast( "错误 " + response.msg)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().weight(.5f),
                        text = "进入邮箱",
                        icon = R.drawable.mail
                    )
                    Spacer(Modifier.width(APP_HORIZONTAL_DP/2))
                    LargeButton (
                        onClick = {
                            response.data.let {
                                if(it != null) {
                                    Starter.startWebUrl(context,it)
                                    used = !used
                                } else {
                                    showToast( "错误 " + response.msg)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().weight(.5f),
                        text = "在浏览器打开",
                        icon = R.drawable.net,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
    DividerTextExpandedWith("使用说明") {
        CardListItem(
            headlineContent = {
                Text("一些邮件，可能在应用内无法跳转链接，需要选择右侧按钮以用浏览器打开")
            },
            leadingContent = {
                Icon(painterResource(R.drawable.net),null)
            }
        )
        CardListItem(
            headlineContent = {
                Text("若为首次使用，请前往信息门户(点击此项)进入邮箱，进行激活")
            },
            modifier = Modifier.clickable {
                Starter.startWebUrl(context,MyApplication.ONE_URL)
            },
            leadingContent = {
                Icon(Icons.Default.ArrowForward,null)
            }
        )
        if(isSupabaseRegistering.value) {
            CardListItem(
                headlineContent = {
                    Text("共建平台注册激活请选择在浏览器使用，并检查最新收件箱 来自Supabase Auth的邮件 点击链接并Confirm")
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.database),null)
                }
            )
        }
    }
}

data class MailResponse(
    val msg : String?,
    val data : String?
)

