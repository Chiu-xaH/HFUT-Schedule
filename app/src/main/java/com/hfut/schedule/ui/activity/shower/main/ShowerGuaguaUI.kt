package com.hfut.schedule.ui.activity.shower.main

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.GuaGuaViewModel
import com.hfut.schedule.logic.enums.ShowerBarItems
import com.hfut.schedule.logic.beans.NavigationBarItemData
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.DataStoreManager
import com.hfut.schedule.logic.utils.SharePrefs
//import com.hfut.schedule.ui.activity.card.function.main.turnToBottomBar
import com.hfut.schedule.ui.activity.shower.bills.GuaguaBills
import com.hfut.schedule.ui.activity.shower.function.GuaGuaSettings
import com.hfut.schedule.ui.activity.shower.home.main.GuaguaStart
import com.hfut.schedule.ui.utils.NavigateAndAnimationManager
import com.hfut.schedule.ui.utils.NavigateAndAnimationManager.currentPage
import com.hfut.schedule.ui.utils.NavigateAndAnimationManager.turnTo
import com.hfut.schedule.ui.utils.NavigateAndAnimationManager.turnToAndClear
import com.hfut.schedule.ui.utils.style.bottomBarBlur
import com.hfut.schedule.ui.utils.style.topBarBlur
import com.hfut.schedule.viewmodel.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowerGuaGua(vm: GuaGuaViewModel,netVm : NetWorkViewModel) {
    val animation by remember { mutableStateOf(SharePrefs.prefs.getInt("ANIMATION", MyApplication.Animation)) }
    val navController = rememberNavController()
    val context = LocalContext.current
    val switchblur = SharePrefs.prefs.getBoolean("SWITCHBLUR",  AndroidVersion.canBlur)
    val blur by remember { mutableStateOf(switchblur) }
    val hazeState = remember { HazeState() }

    val currentAnimationIndex by DataStoreManager.animationTypeFlow.collectAsState(initial = 0)
    var targetPage by remember { mutableStateOf(ShowerBarItems.HOME) }
    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(targetPage) {
            currentPage = targetPage.page
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState, blur),
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text("洗浴-呱呱物联") },
                    actions = {
                        IconButton(onClick = {
                            (context as? Activity)?.finish()
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "")
                        }
                    }
                )
//                if(!blur)
//                    Divider()
            }
        },
        bottomBar = {
            Column {
//                if(!blur)
//                    Divider()
                NavigationBar(containerColor = Color.Transparent,
                    modifier = Modifier
                        .bottomBarBlur(hazeState, blur)) {

                    val items = listOf(
                        NavigationBarItemData(
                            ShowerBarItems.HOME.name,"开始", painterResource(R.drawable.bathtub), painterResource(
                                R.drawable.bathtub_filled)
                        ),
                        NavigationBarItemData(
                            ShowerBarItems.BILLS.name,"账单", painterResource(R.drawable.receipt_long), painterResource(
                                R.drawable.receipt_long_filled)
                        ),
                        NavigationBarItemData(
                            ShowerBarItems.FUNCTION.name,"选项", painterResource(R.drawable.deployed_code),
                            painterResource(R.drawable.deployed_code_filled)
                        )
                    )
                    items.forEach { item ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale = animateFloatAsState(
                            targetValue = if (isPressed) 0.8f else 1f, // 按下时为0.9，松开时为1
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                            label = "" // 使用弹簧动画
                        )
                        val route = item.route
                        val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
                        NavigationBarItem(
                            selected = selected,
                            modifier = Modifier.scale(scale.value),
                            interactionSource = interactionSource,
                            onClick = {
                                //     atEnd = !atEnd
                                if(currentAnimationIndex == 2) {
                                    when(item) {
                                        items[0] -> targetPage = ShowerBarItems.HOME
                                        items[1] -> targetPage = ShowerBarItems.BILLS
                                        items[2] -> targetPage = ShowerBarItems.FUNCTION
                                    }
                                }
                                if (!selected) { turnToAndClear(navController, route) }
                            },
                            label = { Text(text = item.label) },
                            icon = {
                                BadgedBox(badge = {}) { Icon(if(selected)item.filledIcon else item.icon, contentDescription = item.label) }
                            }
                        )
                    }
                }
            }

        }
    ) {innerPadding ->
        val animation = NavigateAndAnimationManager.getAnimationType(currentAnimationIndex,targetPage.page)

        NavHost(navController = navController,
            startDestination = ShowerBarItems.HOME.name,
            enterTransition = { animation.enter },
            exitTransition = { animation.exit },
            modifier = Modifier
                .haze(
                    state = hazeState,
                    //backgroundColor = MaterialTheme.colorScheme.surface,
                )) {
            composable(ShowerBarItems.HOME.name) {
                Scaffold {
                    GuaguaStart(vm,innerPadding,netVm)
                }
            }
            composable(ShowerBarItems.BILLS.name) {
                Scaffold {
                    GuaguaBills(innerPadding, vm)
                }

            }
            composable(ShowerBarItems.FUNCTION.name) {
                Scaffold {
                    GuaGuaSettings(innerPadding)
                }
            }
        }
    }
}




