package com.hfut.schedule.ui.screen.home.search.function.school.dormitoryScore


import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.dialog.MenuChip
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTabRow
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.CenterScreen
import com.hfut.schedule.ui.component.status.EmptyUI
import com.hfut.schedule.ui.component.status.PrepareSearchUI
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.ui.style.color.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.xah.transition.component.TransitionScaffold
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

private const val HEFEI_TAB = 0
private const val XUANCHENG_TAB = 1

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DormitoryScoreScreen(
    vm: NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val route = remember { AppNavRoute.DormitoryScore.route }

    val titles = remember { listOf("合肥","宣城") }
    val pagerState = rememberPagerState(pageCount = { titles.size }, initialPage =
        when(getCampus()) {
            Campus.XUANCHENG -> XUANCHENG_TAB
            Campus.HEFEI -> HEFEI_TAB
        }
    )
    var code by remember { mutableStateOf(prefs.getString("Room","") ?: "") }

    var buildingNumber by remember { mutableStateOf(prefs.getString("BuildNumber", "0") ?: "0") }
    var directionIsSouth by remember { mutableStateOf(prefs.getBoolean("NS",true)) }
    var roomNumber by remember { mutableStateOf(prefs.getString("RoomNumber", "") ?: "") }
    var showitem by remember { mutableStateOf(false) }
    var showitem4 by remember { mutableStateOf(false) }

    var menuOffset by remember { mutableStateOf<DpOffset?>(null) }

    menuOffset?.let {
        DropdownMenu(expanded = showitem, onDismissRequest = { showitem = false }, offset = it) {
            DropdownMenuItem(text = { Text(text = "北一号楼") }, onClick = { buildingNumber =  "1"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "北二号楼") }, onClick = {  buildingNumber =  "2"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "北三号楼") }, onClick = {  buildingNumber =  "3"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "北四号楼") }, onClick = {  buildingNumber =  "4"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "北五号楼") }, onClick = {  buildingNumber =  "5"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南六号楼") }, onClick = {  buildingNumber =  "6"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南七号楼") }, onClick = {  buildingNumber =  "7"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南八号楼") }, onClick = {  buildingNumber =  "8"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南九号楼") }, onClick = {  buildingNumber =  "9"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南十号楼") }, onClick = {  buildingNumber = "10"
                showitem = false})
        }
    }

    val uiState by vm.dormitoryResult.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        vm.dormitoryResult.clear()
        showitem4 = false
        saveString("BuildNumber", buildingNumber)
        saveString("RoomNumber", roomNumber)
        SharedPrefs.saveBoolean("NS",true,directionIsSouth)
        saveString("Room",code)
        val direction = if (directionIsSouth) "S" else "N"
        (buildingNumber + direction + roomNumber).let {
            saveString("XUANQUroom",it)
            vm.searchDormitoryXuanCheng(it)
        }
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        vm.dormitoryResult.emitPrepare()
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Column {
                    MediumTopAppBar(
                        scrollBehavior = scrollBehavior,
                        colors = topBarTransplantColor(),
                        title = { Text(AppNavRoute.DormitoryScore.label) },
                        navigationIcon = {
                            TopBarNavigationIcon(navController,animatedContentScope,route, AppNavRoute.DormitoryScore.icon)
                        },
                        actions = {
                            Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                                if(showitem4)
                                    IconButton(onClick = {roomNumber = roomNumber.replaceFirst(".$".toRegex(), "")}) {
                                        Icon(painter = painterResource(R.drawable.backspace), contentDescription = "description") }
                                FilledTonalIconButton(onClick = {
                                    scope.launch { refreshNetwork() }
                                }) { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }
                            }
                        }
                    )
                    CustomTabRow(pagerState,titles)
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).fillMaxSize()
            ) {
                HorizontalPager(state = pagerState) { page ->
                    when(page) {
                        HEFEI_TAB -> {
                            CenterScreen {
                                EmptyUI("需要合肥校区在读生贡献数据源")
                            }
                        }
                        XUANCHENG_TAB -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = APP_HORIZONTAL_DP, vertical = 0.dp), horizontalArrangement = Arrangement.Start){

                                    MenuChip (
                                        label = { Text(text = "楼栋 $buildingNumber") },
                                    )  {
                                        menuOffset = it
                                        showitem = true
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    AssistChip(
                                        onClick = {directionIsSouth = !directionIsSouth},
                                        label = { Text(text = "南北 ${if (directionIsSouth) "S" else "N"}") },
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    AssistChip(
                                        onClick = { showitem4 = !showitem4 },
                                        label = { Text(text = "寝室 $roomNumber") },
                                    )
                                }


                                AnimatedVisibility(
                                    visible = showitem4,
                                    enter = slideInVertically(
                                        initialOffsetY = { -40 }
                                    ) + expandVertically(
                                        expandFrom = Alignment.Top
                                    ) + scaleIn(
                                        transformOrigin = TransformOrigin(0.5f, 0f)
                                    ) + fadeIn(initialAlpha = 0.3f),
                                    exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
                                ){
                                    Column {
                                        Row (modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)){
                                            OutlinedCard{
                                                LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
                                                    item {
                                                        Text(text = " 选取寝室号", modifier = Modifier.padding(10.dp))
                                                    }
                                                    item {
                                                        LazyRow {
                                                            items(5) { items ->
                                                                IconButton(onClick = {
                                                                    if (roomNumber.length < 3)
                                                                        roomNumber = roomNumber + items.toString()
                                                                    else Toast.makeText(
                                                                        MyApplication.context,
                                                                        "三位数",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }) { Text(text = items.toString()) }
                                                            }
                                                        }
                                                    }
                                                    item {
                                                        LazyRow {
                                                            items(5) { items ->
                                                                val num = items + 5
                                                                IconButton(onClick = {
                                                                    if (roomNumber.length < 3)
                                                                        roomNumber = roomNumber + num
                                                                    else Toast.makeText(
                                                                        MyApplication.context,
                                                                        "三位数",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }) { Text(text = num.toString()) }
                                                            }
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(10.dp))
                                            }
                                        }
                                        Spacer(Modifier.height(CARD_NORMAL_DP*2))
                                    }

                                }

                                CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchUI() }) {
                                    val list = (uiState as UiState.Success).data

                                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP- CARD_NORMAL_DP)) {
                                        items(list.size,key = { it }) { item ->
                                            val listItem = list[item]
                                            SmallCard(modifier = Modifier.padding(horizontal = CARD_NORMAL_DP, vertical = CARD_NORMAL_DP)) {
                                                TransplantListItem(
                                                    headlineContent = { Text(text = listItem.date) },
                                                    supportingContent = { Text(text =  "${listItem.score} 分")}
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}