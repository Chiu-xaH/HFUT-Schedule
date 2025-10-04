package com.hfut.schedule.ui.screen.fix

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.FixBarItems
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.button.HazeBottomBar
import com.hfut.schedule.ui.screen.fix.about.AboutUI
import com.hfut.schedule.ui.screen.fix.fix.FixUI
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.AppAnimationManager.currentPage
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.util.currentRouteWithoutArgs
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private val items = listOf(
    NavigationBarItemData(
        FixBarItems.Fix.name,"修复", R.drawable.build, R.drawable.build_filled
    ),
    NavigationBarItemData(
        FixBarItems.About.name,"关于", R.drawable.info, R.drawable.info_filled
    )
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fix(vm : NetWorkViewModel) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val navController = rememberNavController()
    val currentAnimationIndex by DataStoreManager.animationType.collectAsState(initial = 0)
    val targetPage = when(navController.currentRouteWithoutArgs()) {
        FixBarItems.Fix.name -> FixBarItems.Fix
        FixBarItems.About.name -> FixBarItems.About
        else -> FixBarItems.Fix
    }
    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(targetPage) {
            currentPage = targetPage.page
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val context = LocalActivity.current
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text("修复与检测") },
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
            startDestination = FixBarItems.Fix.name,
            enterTransition = { animation.enter },
            exitTransition = { animation.exit },
            modifier = Modifier.hazeSource(state = hazeState)
        ) {
            composable(FixBarItems.Fix.name) {
                Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    FixUI(innerPadding = innerPadding,vm,navController)
                }

            }
            composable(FixBarItems.About.name) {
                Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    AboutUI(innerPadding = innerPadding, vm,false,navController,hazeState)
                }
            }
        }
    }
}