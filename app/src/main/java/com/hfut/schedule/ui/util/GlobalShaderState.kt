package com.hfut.schedule.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import com.xah.shader.state.ShaderState
import com.xah.shader.state.rememberShaderState

@Composable
fun GlobalShaderStateInit() {
    val state = rememberShaderState()
    LaunchedEffect(state) {
        GlobalUiStateHolder.shaderState = state
    }
}
