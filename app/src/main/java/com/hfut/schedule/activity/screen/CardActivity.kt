package com.hfut.schedule.activity.screen

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.ui.screen.card.CardUI

@Deprecated("为KMP适配计划的开始做铺垫，即将被合入至SharedNav统一管理")
class CardActivity : BaseActivity() {
    @Composable
    override fun UI() {
        CardUI(super.networkVm,super.uiVm)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.networkVm.getMyApi()
    }
}

