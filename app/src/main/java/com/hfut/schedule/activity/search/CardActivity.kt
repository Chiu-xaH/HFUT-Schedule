package com.hfut.schedule.activity.search

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.main.BaseActivity
import com.hfut.schedule.ui.activity.card.main.CardUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardActivity : BaseActivity() {
    @Composable
    override fun UI() {
        CardUI(super.networkVm,super.uiVm)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.loginVm.My()
    }
}

