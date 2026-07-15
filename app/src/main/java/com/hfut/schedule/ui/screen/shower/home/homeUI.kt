package com.hfut.schedule.ui.screen.shower.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.shower.home.function.GuaGuaPersonInfoUI
import com.hfut.schedule.ui.screen.shower.home.function.StartShowerUI
import com.hfut.schedule.ui.screen.shower.home.function.UseCodeUI
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.HazeState

@Composable
fun GuaguaStart(innerPadding : PaddingValues, vm : NetWorkViewModel, hazeState: HazeState) {
//    val hazeState = remember { HazeState() }
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

        InnerPaddingHeight(innerPadding,true)
        DividerTextExpandedWith(text = "个人信息",false) {
            GuaGuaPersonInfoUI(vm,hazeState)
        }

        DividerTextExpandedWith(text = "开始洗浴") {
            StartShowerUI(vm,hazeState)
        }

        DividerTextExpandedWith("使用码",openBlurAnimation = false) {
            UseCodeUI(vm,hazeState)
        }
        InnerPaddingHeight(innerPadding,false)
    }
}

