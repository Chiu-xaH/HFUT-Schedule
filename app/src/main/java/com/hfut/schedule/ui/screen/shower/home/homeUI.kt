package com.hfut.schedule.ui.screen.shower.home

//import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hfut.schedule.viewmodel.network.GuaGuaViewModel
import com.hfut.schedule.ui.screen.shower.home.function.GuaGuaPersonInfoUI
import com.hfut.schedule.ui.screen.shower.home.function.StartShowerUI
import com.hfut.schedule.ui.screen.shower.home.function.UseCodeUI
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@Composable
fun GuaguaStart(vm: GuaGuaViewModel, innerPadding : PaddingValues, netWm : NetWorkViewModel, hazeState: HazeState, navHostController: NavHostController) {
//    val hazeState = remember { HazeState() }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {


        InnerPaddingHeight(innerPadding,true)
        DividerTextExpandedWith(text = "个人信息",false) {
            GuaGuaPersonInfoUI(netWm,hazeState)
        }

        DividerTextExpandedWith(text = "开始洗浴") {
            StartShowerUI(vm,hazeState)
        }

        DividerTextExpandedWith("脱机使用 (无需APP 输入使用码)",openBlurAnimation = false) {
            UseCodeUI(vm,hazeState,navHostController)
        }
        InnerPaddingHeight(innerPadding,false)
    }
}

