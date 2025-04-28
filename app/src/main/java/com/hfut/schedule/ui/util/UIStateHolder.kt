package com.hfut.schedule.ui.util

import androidx.compose.runtime.mutableStateOf

// 跨Activity的类似UIViewModel
object UIStateHolder {
    var isSupabaseRegistering = mutableStateOf(false)
}
