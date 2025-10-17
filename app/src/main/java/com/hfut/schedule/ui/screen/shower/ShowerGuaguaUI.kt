package com.hfut.schedule.ui.screen.shower

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.GuaGuaViewModel
import com.hfut.schedule.logic.enumeration.ShowerBarItems
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.component.button.HazeBottomBar
import com.xah.uicommon.style.padding.NavigationBarSpacer
//import com.hfut.schedule.ui.activity.card.function.main.turnToBottomBar
import com.hfut.schedule.ui.screen.shower.bill.GuaguaBills
import com.hfut.schedule.ui.screen.shower.cube.GuaGuaSettings
import com.hfut.schedule.ui.screen.shower.home.GuaguaStart
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.AppAnimationManager.currentPage
//import com.hfut.schedule.ui.utils.NavigateAndAnimationManager.turnTo

import com.hfut.schedule.ui.util.navigateForBottomBar
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.util.currentRouteWithoutArgs
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
private val items = listOf(
    NavigationBarItemData(
        ShowerBarItems.HOME.name,"开始", R.drawable.bathtub,R.drawable.bathtub_filled
    ),
    NavigationBarItemData(
        ShowerBarItems.BILLS.name,"账单", R.drawable.receipt_long,  R.drawable.receipt_long_filled
    ),
    NavigationBarItemData(
        ShowerBarItems.FUNCTION.name,"选项", R.drawable.deployed_code,R.drawable.deployed_code_filled
    )
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowerGuaGua(vm: GuaGuaViewModel, netVm : NetWorkViewModel, navHostController: NavHostController) {
    val navController = rememberNavController()
    val context = LocalActivity.current
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val currentAnimationIndex by DataStoreManager.animationType.collectAsState(initial = 0)
    val targetPage = when(navController.currentRouteWithoutArgs()) {
        ShowerBarItems.HOME.name -> ShowerBarItems.HOME
        ShowerBarItems.BILLS.name -> ShowerBarItems.BILLS
        ShowerBarItems.FUNCTION.name -> ShowerBarItems.FUNCTION
        else -> ShowerBarItems.HOME
    }
    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(targetPage) {
            currentPage = targetPage.page
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
//        modifier = Modifier.fillMaxSize(),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text("洗浴-呱呱物联") },
                    navigationIcon = {
                        IconButton(onClick = {
                            context?.finish()
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        },
        bottomBar = {
            HazeBottomBar(hazeState,items,navController)
        }
    ) {innerPadding ->
        val animation = AppAnimationManager.getAnimationType(currentAnimationIndex,targetPage.page)

        NavHost(navController = navController,
            startDestination = ShowerBarItems.HOME.name,
            enterTransition = { animation.enter },
            exitTransition = { animation.exit },
            modifier = Modifier
                .hazeSource(
                    state = hazeState
                    //backgroundColor = MaterialTheme.colorScheme.surface,
                )
        ) {
            composable(ShowerBarItems.HOME.name) {
                Scaffold {
                    GuaguaStart(vm,innerPadding,netVm, hazeState = hazeState, navHostController)
                }
            }
            composable(ShowerBarItems.BILLS.name) {
                Scaffold {
                    GuaguaBills(innerPadding, vm)
                }

            }
            composable(ShowerBarItems.FUNCTION.name) {
                Scaffold (containerColor = MaterialTheme.colorScheme.surfaceContainer){
                    GuaGuaSettings(innerPadding,navHostController)
                }
            }
        }
    }
}




