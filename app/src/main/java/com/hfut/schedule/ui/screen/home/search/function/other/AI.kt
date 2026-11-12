package com.hfut.schedule.ui.screen.home.search.function.other

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.enumeration.ChatRole
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.model.response.json.ai.AiChatChoice
import com.hfut.schedule.network.api.model.response.json.ai.AiChatMsg
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.button.containerBackDrop
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.status.StatusIcon
import com.hfut.schedule.ui.nav.destination.AiDestination
import com.hfut.schedule.ui.style.color.textFiledAllTransplant
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.logic.util.LogUtil
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AI() {
    val navController = LocalNavController.current
    TransplantListItem(
        headlineContent = { ScrollText(text = AiDestination.title.asString()) },
        leadingContent = {
            Box() {
                Icon(painterResource(AiDestination.icon), contentDescription = null)
            }
        },
        modifier = Modifier.clickable {
            navController.push(AiDestination)
        }
    )
}

@Composable
fun RotatingRainbowGlow(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f

        // 角度方向彩虹渐变
        val sweepBrush = Brush.sweepGradient(
            colors = listOf(
                Color.Red.copy(.2f),
                Color.Yellow.copy(.2f),
                Color.Green.copy(.2f),
                Color.Cyan.copy(.2f),
                Color.Blue.copy(.2f),
                Color.Magenta.copy(.2f),
                Color.Red.copy(.2f) // 闭环
            ),
            center = center
        )

        // 画彩色光晕
        drawCircle(
            brush = sweepBrush,
            radius = radius,
            center = center
        )

        // 叠加一层“向外透明”的遮罩
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Black
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center,
            blendMode = BlendMode.DstIn
        )
    }
}



@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AIScreen(
    vm : NetWorkViewModel
) {
    val hazeState = rememberHazeBlur()
    val backdrop = rememberLayerBackdrop()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var input by remember { mutableStateOf(EMPTY_STRING) }
    val uiState by vm.aiChatResp.state.collectAsState()
    val apiKey by DataStoreManager.apiKey.collectAsState(initial = EMPTY_STRING)

    LaunchedEffect(Unit) {
        if(uiState is NetworkUiState.Success) {
            return@LaunchedEffect
        }
        vm.aiChatResp.emitPrepare()
    }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(AiDestination.title.asString()) },
                navigationIcon = {
                    TopBarNavigationIcon()
                },
                actions = {
                    if(uiState is NetworkUiState.Success) {
                        LiquidButton(
                            onClick = {
                                // TODO Dialog
                                input = EMPTY_STRING
                                vm.aiChatResp.emitPrepare()
                            },
                            isCircle = true,
                            backdrop = backdrop,
                            modifier = Modifier.padding(end = APP_HORIZONTAL_DP)
                        ) {
                            Icon(
                                painterResource(R.drawable.delete),
                                null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.bottomBarBlur(hazeState)
            ) {
                CustomTextField(
                    colors = textFiledAllTransplant(),
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = APP_HORIZONTAL_DP)
                        .padding(horizontal = APP_HORIZONTAL_DP)
                        .containerBackDrop(backdrop, MaterialTheme.shapes.medium),
                    input = input,
                    singleLine = false,
                    trailingIcon = {
                         IconButton(
                            enabled = input.isNotEmpty() && uiState !is NetworkUiState.Loading,
                            onClick = {
                                scope.launch {
                                    val oldList = (uiState as? NetworkUiState.Success)?.data ?: emptyList()
                                    val newMsg = AiChatMsg(
                                        role = ChatRole.USER.name.lowercase(),
                                        content = input
                                    )
                                    vm.chatWithAi(
                                        apiKey,
                                        oldList.map { it.message } + newMsg
                                    )
                                    when(val uiState = vm.aiChatResp.state.first()) {
                                        is NetworkUiState.Success -> {
                                            val newData = uiState.data
                                            vm.aiChatResp.emitData(
                                                oldList + AiChatChoice(newMsg,EMPTY_STRING) + newData
                                            )
                                            input = EMPTY_STRING
                                        }
                                        is NetworkUiState.Error -> {
                                            val errorData = uiState
                                            // 复原对话
                                            vm.aiChatResp.emitData(oldList)
                                            showToast(errorData.exception?.message ?: "错误")
                                            errorData.exception?.let {
                                                LogUtil.error(it)
                                            }
                                        }
                                        else -> Unit
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painterResource(R.drawable.arrow_upward),
                                null
                            )
                        }
                    },
                    label = {
                        Text("输入内容")
                    }
                ) {
                    input = it
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .backDropSource(backdrop)
        ) {
            CommonNetworkScreen(
                uiState,
                onReload = {
                    input = EMPTY_STRING
                    vm.aiChatResp.emitPrepare()
                },
                prepareContent = {
                    StatusIcon(
                        R.drawable.send_money,
                        text("发送消息以开始聊天")
                    )
                }
            ) {
                val data = (uiState as NetworkUiState.Success).data
                LazyColumn {
                    item { InnerPaddingHeight(innerPadding,true) }
                    items(data.size, key = { it }) { index ->
                        val item = data[index]
                        ChatMsgUI(item)
                    }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
            }
        }
    }
}

@Composable
private fun ChatMsgUI(item: AiChatChoice) {
    val role = when (item.message.role.uppercase()) {
        ChatRole.USER.name -> ChatRole.USER
        ChatRole.ASSISTANT.name -> ChatRole.ASSISTANT
        ChatRole.SYSTEM.name -> ChatRole.SYSTEM
        else -> null
    }

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP)) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = when(role) {
                ChatRole.USER -> MaterialTheme.colorScheme.secondaryContainer
                ChatRole.ASSISTANT -> cardNormalColor()
                else -> MaterialTheme.colorScheme.errorContainer
            },
            modifier = Modifier
                .align(
                    if(role == ChatRole.USER) {
                        Alignment.CenterEnd
                    } else {
                        Alignment.CenterStart
                    }
                )
                .padding(vertical = CARD_NORMAL_DP*2)
        ) {
            if(role == ChatRole.USER) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = APP_HORIZONTAL_DP/3, horizontal = APP_HORIZONTAL_DP/2)
                ) {
                    Text(item.message.content)
                    /*
                    Icon(
                        painterResource(R.drawable.person),
                        null,
//                        modifier = Modifier.padding(start = BUTTON_PADDING)
                    )
                     */
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = APP_HORIZONTAL_DP/3, horizontal = APP_HORIZONTAL_DP/2)
                ) {
                    /*
                    Icon(
                        painterResource(
                            when(role) {
                                ChatRole.ASSISTANT -> R.drawable.wand_stars
                                else -> R.drawable.more_vert
                            }
                        ),
                        null,
                        modifier = Modifier.padding(end = BUTTON_PADDING)
                    )
                     */
                    Text(item.message.content)
                }
            }
        }
    }
}