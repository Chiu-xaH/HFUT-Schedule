package com.hfut.schedule.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.getExceptionDetail
import com.hfut.schedule.logic.util.getKeyStackTrace
import com.hfut.schedule.logic.util.network.PARSE_ERROR_CODE
import com.hfut.schedule.logic.util.network.StateHolder
import com.hfut.schedule.logic.util.network.UiState
import com.hfut.schedule.logic.util.sys.ClipBoard
import com.hfut.schedule.logic.util.sys.Starter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CommonNetworkScreen(
    uiState: UiState<*>,
    isFullScreen : Boolean = true,
    modifier: Modifier = if(isFullScreen) Modifier.fillMaxSize() else Modifier,
    loadingText : String? = null,
    onReload: (suspend () -> Unit)?, // 新增刷新回调
    prepareContent : (@Composable () -> Unit)? = null,
    successContent : @Composable () -> Unit
) = Box(modifier = modifier) {
    val scope = rememberCoroutineScope()
    val refreshUI = @Composable {
        onReload?.let {
            Spacer(Modifier.height(appHorizontalDp()*1.5f))
            Button(onClick = { scope.launch { it.invoke() }}) {
                Text("重新加载")
            }
        }
    }
    when (uiState) {
        // 准备状态UI 例如手动搜索时第一次什么也不显示
        UiState.Prepare -> {
            prepareContent?.let {
                CenterScreen { it() }
            }
        }
        // 错误UI 数据解析使用了TRY CATCH,数据解析错误时跳转到这里（待开发） 或者网络请求失败
        is UiState.Error -> {
            val e = uiState.exception
            val codeInt = uiState.code
            val ui =  @Composable {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(rememberScrollState()))  {
                    when(codeInt) {
                        PARSE_ERROR_CODE -> {
                            var showDetail by remember { mutableStateOf(false) }
                            var text by remember { mutableStateOf("") }
                            LaunchedEffect(showDetail) {
                                if(showDetail) {
                                    e?.let {
                                        text = getKeyStackTrace(it)
                                    }
                                } else {
                                    text = "解析数据错误 ${e?.message?.substringBefore(":")}"
                                }
                            }

                            // 解析出错
                            ErrorUI(text)
                            Spacer(Modifier.height(appHorizontalDp()*1.5f))
                            if(!showDetail)
                                Button(onClick = {
                                    showDetail = true
                                }) {
                                    Text("详细报错信息")
                                }
                            else
                                Button(onClick = {
                                    e?.let { ClipBoard.copy(getExceptionDetail(it)) }
                                    showToast("请截图或粘贴详细错误信息，并注明功能，发送邮件")
                                    Starter.emailMe()
                                }) {
                                    Text( "上报开发者")
                                }
                        }
                        401 -> {
                            StatusUI(R.drawable.login, "登录状态失效")
                            Spacer(Modifier.height(appHorizontalDp()*1.5f))
                            Button(onClick = {
                                Starter.refreshLogin()
                            }) {
                                Text("刷新登陆状态")
                            }
                        }
                        403 -> {
                            StatusUI(R.drawable.lock, "禁止操作 可能原因: 密码不正确或无权利进行操作")
                        }
                        in 500..599 -> {
                            StatusUI(R.drawable.net, "服务器错误 可能的原因: \n1.智慧社区(Community)接口登陆状态失效,需重新刷新登陆状态\n2.对方API发生变更，APP对接失败\n3.对方暂时关闭了API(如选课等周期性活动)")
                        }
                        else -> {
                            // 网络出错
                            val code = codeInt?.toString() ?: ""
                            val eMsg = e?.message
                            if(eMsg?.contains("Unable to resolve host",ignoreCase = true) == true || eMsg?.contains("Failed to connect to",ignoreCase = true) == true ||  eMsg?.contains("Connection reset",ignoreCase = true) == true) {
                                StatusUI(R.drawable.link_off, "网络连接失败")
                            } else if(eMsg?.contains("10000ms") == true) {
                                StatusUI(R.drawable.link_off, "网络连接超时")
                            } else {
                                ErrorUI("错误 $code $e")
                            }
                            refreshUI()
                        }
                    }
                }
            }

            if(isFullScreen) {
                CenterScreen {
                    ui()
                }
            } else {
                ui()
            }
        }
        // 加载UI
        UiState.Loading -> {
            val ui = @Composable {
                LoadingUI(loadingText)
            }
            if(isFullScreen) {
                CenterScreen {
                    ui()
                }
            } else {
                ui()
            }
        }
        // 主UI
        is UiState.Success -> {
            successContent()
        }
    }
}

suspend fun <T> onListenStateHolder(response : StateHolder<T>, onError : (() -> Unit)? = null, onSuccess : (T) -> Unit) = withContext(Dispatchers.Main) {
    // 只收集第一次流
    val state = response.state.first { it !is UiState.Loading }
    when (state) {
        is UiState.Success -> {
            val data = state.data
            onSuccess(data)
        }
        is UiState.Error -> {
            if(onError == null) {
                showToast("错误 " + state.exception?.message)
            } else {
                onError()
            }
        }
        else -> {}
    }
}