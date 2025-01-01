package com.hfut.schedule.ui.activity.grade.main

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.enums.GradeBarItems
import com.hfut.schedule.logic.beans.NavigationBarItemData
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.activity.grade.analysis.GradeCountUI
import com.hfut.schedule.ui.activity.grade.grade.community.GradeItemUI
import com.hfut.schedule.ui.activity.grade.grade.jxglstu.GradeItemUIJXGLSTU
import com.hfut.schedule.ui.utils.MyCard
import com.hfut.schedule.ui.utils.Round
import com.hfut.schedule.ui.utils.bottomBarBlur
import com.hfut.schedule.ui.utils.topBarBlur
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeUI(ifSaved : Boolean,vm : NetWorkViewModel) {

    val switchblur = SharePrefs.prefs.getBoolean("SWITCHBLUR",  AndroidVersion.canBlur)
    var blur by remember { mutableStateOf(switchblur) }
    val hazeState = remember { HazeState() }
    val navController = rememberNavController()
    val context = LocalContext.current
    var animation by remember { mutableIntStateOf(prefs.getInt("ANIMATION", MyApplication.Animation)) }

    var showSearch by remember { mutableStateOf(false) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet= false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("说明") }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    Infos()
                }
            }
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
                    title = { Text("成绩") },
                    actions = {
                        Row {
                            if(!ifSaved) {
                                IconButton(onClick = {
                                    showSearch = !showSearch
                                }) {
                                    Icon(painter = painterResource(id = R.drawable.search), contentDescription = "")
                                }
                            }
                            IconButton(onClick = {
                                showBottomSheet = true
                            }) {
                                Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")
                            }
                            IconButton(onClick = {
                                (context as? Activity)?.finish()
                            }) {
                                Icon(Icons.Filled.Close, contentDescription = "")
                            }
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
                NavigationBar(containerColor = Color.Transparent ,
                    modifier = Modifier.bottomBarBlur(hazeState, blur)
                      //  .hazeChild(state = hazeState, blurRadius = MyApplication.Blur, tint = Color.Transparent, noiseFactor = 0f)
                ) {

                    val items = listOf(
                        NavigationBarItemData(
                            GradeBarItems.GRADE.name,"学期", painterResource(R.drawable.article), painterResource(
                                R.drawable.article_filled)
                        ),
                        NavigationBarItemData(
                            GradeBarItems.COUNT.name,"统计", painterResource(R.drawable.leaderboard),
                            painterResource(R.drawable.leaderboard_filled)
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
                                if (!selected) {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
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
//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                .fillMaxSize()
//        ){
            NavHost(navController = navController,
                startDestination = GradeBarItems.GRADE.name,
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
                composable(GradeBarItems.GRADE.name) {
                    Scaffold {
                        if (ifSaved) GradeItemUI(vm,innerPadding)
                        else GradeItemUIJXGLSTU(innerPadding,vm,showSearch)
                    }

                }
                composable(GradeBarItems.COUNT.name) {
                    Scaffold {
                        GradeCountUI(innerPadding)
                    }
                }
            }
//        }
    }
}

@Composable
fun Infos() {
    MyCard {
        ListItem(
            headlineContent = { Text(text = "平时因数") },
            supportingContent = { Text(text = "平时因数=除去期末成绩各项平均分/期末分数,可大致反映最终成绩平时分占比;\n越接近1则平衡,越>1则表明最终成绩可能更靠平时分,越<1表明最终成绩可能因平时分拖后腿")}
        )
    }
    MyCard {
        ListItem(
            headlineContent = { Text(text = "绩点与均分") },
            supportingContent = { Text(text = "满绩 4.3 均分 95-100\n绩点 3.7 均分 85-89\n绩点 3.3 均分 83-84\n绩点 3.0 均分 78-82\n2.7之后不清楚,欢迎联系开发者补充")}
        )
    }
    MyCard{
        ListItem(
            headlineContent = { Text(text = "校务行") },
            supportingContent = { Text(text = "微信小程序搜校务行，注意宣区选择 合肥工业大学（宣城校区），学号为账号，身份证后六位为密码（包括最后的X）")}
        )
    }
}