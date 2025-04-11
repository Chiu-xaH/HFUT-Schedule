package com.hfut.schedule.activity.screen

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.BaseActivity
import com.hfut.schedule.ui.screen.card.CardUI

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

