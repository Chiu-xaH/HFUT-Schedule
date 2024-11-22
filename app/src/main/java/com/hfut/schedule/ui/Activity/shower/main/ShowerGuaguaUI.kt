package com.hfut.schedule.ui.Activity.shower.main

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
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.hfut.schedule.ViewModel.GuaGuaViewModel
import com.hfut.schedule.logic.Enums.ShowerBarItems
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.Activity.card.function.main.turnToBottomBar
import com.hfut.schedule.ui.Activity.shower.bills.GuaguaBills
import com.hfut.schedule.ui.Activity.shower.function.GuaGuaSettings
import com.hfut.schedule.ui.Activity.shower.home.GuaguaStart
import com.hfut.schedule.ui.UIUtils.bottomBarBlur
import com.hfut.schedule.ui.UIUtils.topBarBlur
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowerGuaGua(vm: GuaGuaViewModel) {
    val animation by remember { mutableStateOf(SharePrefs.prefs.getInt("ANIMATION",MyApplication.Animation)) }
    val navController = rememberNavController()
    val context = LocalContext.current
    val switchblur = SharePrefs.prefs.getBoolean("SWITCHBLUR", AndroidVersion.sdkInt >= 32)
    val blur by remember { mutableStateOf(switchblur) }
    val hazeState = remember { HazeState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState, blur),
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = if(blur) 0f else 1f),
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
                if(!blur)
                    Divider()
            }
        },
        bottomBar = {
            Column {
                if(!blur)
                    Divider()
                NavigationBar(containerColor = if(blur) Color.Transparent else ListItemDefaults.containerColor ,
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
                                if (!selected) { turnToBottomBar(navController, route) }
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
        NavHost(navController = navController,
            startDestination = ShowerBarItems.HOME.name,
            enterTransition = {
                scaleIn(animationSpec = tween(durationMillis = animation)) +
                        expandVertically(expandFrom = Alignment.Top,animationSpec = tween(durationMillis = animation))
            },
            exitTransition = {
                scaleOut(animationSpec = tween(durationMillis = animation)) +
                        shrinkVertically(shrinkTowards = Alignment.Top,animationSpec = tween(durationMillis = animation))
            },
            modifier = Modifier
                .haze(
                    state = hazeState,
                    //backgroundColor = MaterialTheme.colorScheme.surface,
                )) {
            composable(ShowerBarItems.HOME.name) {
                Scaffold {
                    GuaguaStart(vm,innerPadding)
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




