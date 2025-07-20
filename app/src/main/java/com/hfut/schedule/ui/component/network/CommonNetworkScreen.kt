package com.hfut.schedule.ui.component.network

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
import com.hfut.schedule.logic.util.development.getExceptionDetail
import com.hfut.schedule.logic.util.development.getKeyStackTrace
import com.hfut.schedule.logic.util.network.state.LISTEN_ERROR_CODE
import com.hfut.schedule.logic.util.network.state.PARSE_ERROR_CODE
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.status.LoadingUI
import com.hfut.schedule.ui.component.status.CenterScreen
import com.hfut.schedule.ui.component.status.EmptyUI
import com.hfut.schedule.ui.component.status.ErrorUI
import com.hfut.schedule.ui.component.status.StatusUI
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
            Spacer(Modifier.height(APP_HORIZONTAL_DP *1.5f))
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
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = if(isFullScreen) Modifier.verticalScroll(rememberScrollState()) else Modifier)  {
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
                            Spacer(Modifier.height(APP_HORIZONTAL_DP *1.5f))
                            if(!showDetail)
                                Button(onClick = {
                                    showDetail = true
                                }) {
                                    Text("详细报错信息")
                                }
                            else
                                Button(onClick = {
                                    e?.let { ClipBoardUtils.copy(getExceptionDetail(it)) }
                                    showToast("请截图或粘贴详细错误信息，并注明功能，发送邮件")
                                    Starter.emailMe()
                                }) {
                                    Text( "上报开发者")
                                }
                        }
                        LISTEN_ERROR_CODE -> {
                            StatusUI(
                                R.drawable.arrow_split,
                                e?.message ?: "网络请求顺序出错 请联系开发者"
                            )
                            Spacer(Modifier.height(APP_HORIZONTAL_DP *1.5f))
                            Button(onClick = {
                                showToast("请截图并注明功能，发送邮件")
                                Starter.emailMe()
                            }) {
                                Text( "上报开发者")
                            }
                        }
                        401 -> {
                            StatusUI(R.drawable.login, "登录状态失效")
                            Spacer(Modifier.height(APP_HORIZONTAL_DP *1.5f))
                            Button(onClick = {
                                Starter.refreshLogin()
                            }) {
                                Text("刷新登陆状态")
                            }
                        }
                        403 -> {
                            StatusUI(
                                R.drawable.lock,
                                "禁止操作 可能原因: 密码不正确或无权利进行操作"
                            )
                        }
                        404 -> {
                            EmptyUI("404 未找到")
                        }
                        in 500..599 -> {
                            StatusUI(
                                R.drawable.net,
                                "500错误 可能的原因: \n1.智慧社区(Community)接口登陆状态失效,需重新刷新登陆状态\n2.API发生变更，APP对接失败\n3.暂时关闭了API(如选课、转专业等周期性活动)"
                            )
                        }
                        else -> {
                            // 网络出错
                            val code = codeInt?.toString() ?: ""
                            val eMsg = e?.message
                            if(eMsg?.contains("Unable to resolve host",ignoreCase = true) == true || eMsg?.contains("Failed to connect to",ignoreCase = true) == true ||  eMsg?.contains("Connection reset",ignoreCase = true) == true) {
                                StatusUI(R.drawable.link_off, "网络连接失败")
                            } else if(eMsg?.contains("10000ms") == true) {
                                StatusUI(R.drawable.link_off, "网络连接超时")
                            } else if(eMsg?.contains("The coroutine scope") == true) {
                                StatusUI(
                                    R.drawable.rotate_right,
                                    "操作过快,前面的请求尚未完成\n(若为教务相关功能,请回到聚焦页面下拉刷新)"
                                )
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

suspend fun <T> onListenStateHolder(
    response : StateHolder<T>,
    onError : ((Int?, Throwable?) -> Unit)? = null,
    onSuccess : suspend (T) -> Unit
) = withContext(Dispatchers.Main) {
    // 只收集第一次流
    val state = response.state.first { it !is UiState.Loading }
    when (state) {
        is UiState.Success -> {
            val data = state.data
            onSuccess(data)
        }
        is UiState.Error -> {
            val codeInt = state.code
            val e = state.exception
            if(onError == null) {
                val text = when(codeInt) {
                    PARSE_ERROR_CODE -> {
                        "解析数据错误 ${e?.message?.substringBefore(":")}"
                    }
                    401 -> {
                        "登录状态失效"
                    }
                    403 -> {
                        "禁止操作 可能原因: 密码不正确或无权利进行操作"
                    }
                    in 500..599 -> {
                        "500错误 可能的原因: \n1.智慧社区(Community)接口登陆状态失效,需重新刷新登陆状态\n2.API发生变更，APP对接失败\n3.暂时关闭了API(如选课、转专业等周期性活动)"
                    }
                    else -> {
                        // 网络出错
                        val code = codeInt?.toString() ?: ""
                        val eMsg = e?.message
                        if(eMsg?.contains("Unable to resolve host",ignoreCase = true) == true || eMsg?.contains("Failed to connect to",ignoreCase = true) == true ||  eMsg?.contains("Connection reset",ignoreCase = true) == true) {
                            "网络连接失败"
                        } else if(eMsg?.contains("10000ms") == true) {
                            "网络连接超时"
                        } else {
                            "错误 $code $e"
                        }
                    }
                }
                showToast(text)
            } else {
                onError(codeInt,e)
            }
        }
        else -> {}
    }
}


suspend fun <T,F> onListenStateHolderForNetwork(
    response : StateHolder<T>,
    resultHolder : StateHolder<F>?,
    onSuccess : suspend (T) -> Unit
) = withContext(Dispatchers.IO) {
    // 只收集第一次流
    val state = response.state.first()
    when (state) {
        is UiState.Success -> {
            val data = state.data
            onSuccess(data)
        }
        is UiState.Error -> {
            val codeInt = state.code
            val e = state.exception
            resultHolder?.emitError(e,codeInt) ?: e?.message?.let { showToast(it) }
        }
        is UiState.Loading -> {
            val t = "本操作依赖于上一网络请求，上一网络请求处于加载状态"
            resultHolder?.emitError(Exception(t),LISTEN_ERROR_CODE) ?: showToast(t)
        }
        is UiState.Prepare -> {
            val t = "本操作依赖于上一网络请求，上一网络请求处于未发起"
            resultHolder?.emitError(Exception(t),LISTEN_ERROR_CODE) ?: showToast(t)
        }
    }
}