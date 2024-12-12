package com.hfut.schedule.ui.activity.life.main

import androidx.compose.runtime.Composable
import com.hfut.schedule.viewmodel.NetWorkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun LifeUI(vm : NetWorkViewModel) {
    CoroutineScope(Job()).launch {
        async {
            vm.getWeatherWarn()
        }.await()
    }
}