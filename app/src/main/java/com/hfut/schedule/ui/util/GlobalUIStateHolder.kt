package com.hfut.schedule.ui.util

import androidx.compose.runtime.mutableStateOf

// 跨Activity的类似UIViewModel
object GlobalUIStateHolder {
    var isSupabaseRegistering = mutableStateOf(false)
}
