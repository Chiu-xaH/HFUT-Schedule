package com.hfut.schedule.ui.screen.shower

//import com.hfut.schedule.ui.activity.card.function.main.turnToBottomBar
//import com.hfut.schedule.ui.utils.NavigateAndAnimationManager.turnTo

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.ShowerBarItems
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.HazeBottomBar
import com.hfut.schedule.ui.screen.shower.bill.GuaguaBills
import com.hfut.schedule.ui.screen.shower.cube.GuaGuaSettings
import com.hfut.schedule.ui.screen.shower.home.GuaguaStart
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.AppAnimationManager.currentPage
import com.hfut.schedule.viewmodel.network.GuaGuaViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.util.navigation.currentRouteWithoutArgs
import com.xah.common.style.color.topBarTransplantColor
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
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
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
                            Icon(painterResource(R.drawable.arrow_back), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        },
        bottomBar = {
            HazeBottomBar(hazeState,items,navController)
        }
    ) {innerPadding ->
        NavHost(navController = navController,
            startDestination = ShowerBarItems.HOME.name,
            enterTransition = {
                AppAnimationManager.centerAnimation.enter
            },
            exitTransition = {
                AppAnimationManager.centerAnimation.exit
            },
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




