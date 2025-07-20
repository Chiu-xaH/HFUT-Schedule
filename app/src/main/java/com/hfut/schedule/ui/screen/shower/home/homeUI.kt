package com.hfut.schedule.ui.screen.shower.home

//import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.viewmodel.network.GuaGuaViewModel
import com.hfut.schedule.ui.screen.shower.home.function.GuaGuaPersonInfoUI
import com.hfut.schedule.ui.screen.shower.home.function.StartShowerUI
import com.hfut.schedule.ui.screen.shower.home.function.UseCodeUI
import com.hfut.schedule.ui.component.text.DividerText
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun GuaguaStart(vm: GuaGuaViewModel, innerPadding : PaddingValues, netWm : NetWorkViewModel, hazeState: HazeState, navHostController: NavHostController) {
//    val hazeState = remember { HazeState() }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {


        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        DividerTextExpandedWith(text = "个人信息",false) {
            GuaGuaPersonInfoUI(netWm,hazeState)
        }

        DividerTextExpandedWith(text = "开始洗浴") {
            StartShowerUI(vm,hazeState)
        }

        DividerTextExpandedWith("脱机使用 (无需APP 输入使用码)") {
            UseCodeUI(vm,hazeState,navHostController)
        }
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
    }
}

