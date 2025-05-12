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
import com.hfut.schedule.logic.util.network.SimpleStateHolder
import com.hfut.schedule.logic.util.network.SimpleUiState
import com.hfut.schedule.logic.util.sys.ClipBoard
import com.hfut.schedule.logic.util.sys.Starter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//@Composable
//fun CommonNetworkScreen(
//    uiState: UiState<*,*>,
//    modifier: Modifier = Modifier.fillMaxSize(),
//    successContent : @Composable () -> Unit,
//) = Box(modifier = modifier) {
//    when (uiState) {
//        UiState.Prepare -> {
//            CenterScreen {
//                EmptyUI()
//            }
//        }
//        is UiState.Error -> {
//            CenterScreen {
//                val e = uiState.errorBody
//                ErrorUI("错误 $e")
//            }
//        }
//        UiState.Loading -> {
//            LoadingScreen()
//        }
//        is UiState.Success -> {
//            successContent()
//        }
//    }
//}


@Composable
fun CommonNetworkScreen(
    uiState: SimpleUiState<*>,
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
        SimpleUiState.Prepare -> {
            prepareContent?.let {
                CenterScreen { it() }
            }
        }
        // 错误UI 数据解析使用了TRY CATCH,数据解析错误时跳转到这里（待开发） 或者网络请求失败
        is SimpleUiState.Error -> {
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
                            StatusUI(R.drawable.net, "服务器错误 可能原因: APP自身原因对接服务端失败或对方已关闭了通道")
                        }
                        else -> {
                            // 网络出错
                            val code = codeInt?.toString() ?: ""
                            val eMsg = e?.message
                            if(eMsg?.contains("Unable to resolve host") == true || eMsg?.contains("Failed to connect to") == true) {
                                StatusUI(R.drawable.link_off, "网络连接失败")
                            } else if(eMsg?.contains("10000ms") == true) {
                                StatusUI(R.drawable.link_off, "网络连接超时")
                            } else {
                                ErrorUI("网络错误 $code $e")
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
        SimpleUiState.Loading -> {
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
        is SimpleUiState.Success -> {
            successContent()
        }
    }
}

suspend fun <T> onListenStateHolder(response : SimpleStateHolder<T>,onSuccess : (T) -> Unit) = withContext(Dispatchers.Main) {
    // 只收集第一次流
    val state = response.state.first { it !is SimpleUiState.Loading }
    when (state) {
        is SimpleUiState.Success -> {
            val data = state.data
            onSuccess(data)
        }
        is SimpleUiState.Error -> {
            showToast("错误 " + state.exception?.message)
        }
        else -> {}
    }
}