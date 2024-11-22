package com.hfut.schedule.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.Activity.card.main.CardUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun UI() {
        CardUI(super.networkVm,super.uiVm)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.loginVm.My()
    }
}

