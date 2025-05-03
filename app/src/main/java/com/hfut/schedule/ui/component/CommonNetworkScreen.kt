package com.hfut.schedule.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hfut.schedule.logic.util.network.SimpleUiState
import com.hfut.schedule.logic.util.network.UiState

@Composable
fun CommonNetworkScreen(
    uiState: UiState<*,*>,
    modifier: Modifier = Modifier.fillMaxSize(),
    successContent : @Composable () -> Unit,
) = Box(modifier = modifier) {
    when (uiState) {
        UiState.Empty -> {
            CenterScreen {
                EmptyUI()
            }
        }
        is UiState.Error -> {
            CenterScreen {
                val e = uiState.errorBody
                ErrorUI("错误 $e")
            }
        }
        UiState.Loading -> {
            LoadingScreen()
        }
        is UiState.Success -> {
            successContent()
        }
    }
}


@Composable
fun CommonNetworkScreen(
    uiState: SimpleUiState<*>,
    isCenter : Boolean = true,
    modifier: Modifier = Modifier.fillMaxSize(),
    loadingText : String? = null,
    successContent : @Composable () -> Unit) = Box(modifier = modifier) {
    when (uiState) {
        SimpleUiState.Empty -> {
//            val ui = @Composable {
//                EmptyUI()
//            }
//            if(isCenter) {
//                CenterScreen {
//                    ui()
//                }
//            } else {
//                ui()
//            }
        }
        is SimpleUiState.Error -> {
            val e = uiState.exception

            val ui = @Composable {
                ErrorUI("错误 " + e?.message)
            }
            if(isCenter) {
                CenterScreen {
                    ui()
                }
            } else {
                ui()
            }
        }
        SimpleUiState.Loading -> {
            val ui = @Composable {
                LoadingUI(loadingText)
            }
            if(isCenter) {
                CenterScreen {
                    ui()
                }
            } else {
                ui()
            }
        }
        is SimpleUiState.Success -> {
            successContent()
        }
    }
}