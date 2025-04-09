package com.hfut.manage.ui

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.hfut.manage.logic.utils.AndroidVersion
import com.hfut.manage.datamodel.enums.MainBarItem
import com.hfut.manage.MyApplication
import com.hfut.manage.R
import com.hfut.manage.datamodel.data.GithubResponse
import com.hfut.manage.datamodel.enums.PrefsKey
import com.hfut.manage.logic.utils.SharePrefs
import com.hfut.manage.logic.utils.SharePrefs.prefs
import com.hfut.manage.viewmodel.NetViewModel
import com.hfut.manage.viewmodel.UIViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class NavigationBarItemData(val route: String, val label: String, val icon: Painter, val filledIcon: Painter)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUI(vm : NetViewModel) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = VersionUtils.canBlur)

    val hazeState = remember { HazeState() }
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    modifier = Modifier.hazeChild(state = hazeState, blurRadius = MyApplication.Blur, tint = Color.Transparent, noiseFactor = 0f),
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = if(blur).50f else 1f),
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text("管理端") },
                    actions = {

                    }
                )
                Divider()
            }
        },
        bottomBar = {
            Divider()
            NavigationBar(containerColor = if(blur) MaterialTheme.colorScheme.primaryContainer.copy(.25f) else ListItemDefaults.containerColor ,
                modifier = Modifier
                    .hazeChild(state = hazeState, blurRadius = MyApplication.Blur, tint = Color.Transparent, noiseFactor = 0f)) {

                val items = listOf(
                    NavigationBarItemData(
                        MainBarItem.Home.name,"主页", painterResource(id = R.drawable.home),
                        painterResource(id = R.drawable.home_filled)
                        )
                    ,
                    NavigationBarItemData(
                        MainBarItem.Status.name,"状态", painterResource(id = R.drawable.monitor_heart),
                        painterResource(id = R.drawable.monitor_heart_filled)
                      )
                    ,
                    NavigationBarItemData(
                        MainBarItem.Settings.name,"选项", painterResource(id = R.drawable.deployed_code),
                     painterResource(id = R.drawable.deployed_code_filled)
                    )
                   )
                items.forEach { item ->
                    val route = item.route
                    val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
                    NavigationBarItem(
                        selected = selected,
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
                            Icon(if(selected)item.filledIcon else item.icon, contentDescription = item.label)
                        }
                    )
                }
            }
        }
    ) {innerPadding ->
        NavHost(navController = navController, startDestination = MainBarItem.Home.name, modifier = Modifier
            .haze(
                state = hazeState,
                backgroundColor = MaterialTheme.colorScheme.surface,
            )) {
            composable(MainBarItem.Home.name) {
                Home(innerPadding,vm)
            }
            composable(MainBarItem.Status.name) {

            }
            composable(MainBarItem.Settings.name) {

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(innerPadding: PaddingValues,vm : NetViewModel) {
   /// Column(modifier = Modifier.padding(innerPadding)) {
   //     Button(onClick = {
   //         vm.addData(1000,"安卓客户端","来自安卓")
  //      }) {
   //         Text(text = "添加数据")
  //      }
  //  }
    Column {
        Box(modifier = Modifier
            .fillMaxHeight()
        ) {
            val scrollstate = rememberLazyListState()
            val shouldShowAddButton by remember { derivedStateOf { scrollstate.firstVisibleItemScrollOffset == 0 } }
            LazyColumn(state = scrollstate) {
                item {
                    Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                    Spacer(modifier = Modifier.height(5.dp))
                }
                item { addFocus(vm) }
                item {
                    removeFocus(vm)
                }
                item {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        TransplantListItem(
                            headlineContent = {  Text(text = "添加通知") },
                            leadingContent = { Icon(painterResource(id = R.drawable.notifications), contentDescription = "Localized description") },
                            trailingContent = {Icon(Icons.Filled.Add, contentDescription = "Localized description",)},
                            modifier = Modifier.clickable {},
                        )
                    }
                }
                item {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            headlineContent = {  Text(text = "删除通知") },
                            leadingContent = { Icon(painterResource(id = R.drawable.notifications), contentDescription = "Localized description") },
                            trailingContent = { Icon(Icons.Filled.Close, contentDescription = "Localized description",) },
                            modifier = Modifier.clickable {},
                        )
                    }
                }
                item {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            headlineContent = {  Text(text = "添加实验室项") },
                            leadingContent = { Icon(painterResource(id = R.drawable.science), contentDescription = "Localized description") },
                            trailingContent = { Icon(Icons.Filled.Add, contentDescription = "Localized description",) },
                            modifier = Modifier.clickable {},
                        )
                    }
                }
                item {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            headlineContent = {  Text(text = "移除实验室项") },
                            leadingContent = { Icon(painterResource(id = R.drawable.science), contentDescription = "Localized description") },
                            trailingContent = { Icon(Icons.Filled.Close, contentDescription = "Localized description",) },
                            modifier = Modifier.clickable {},
                        )
                    }
                }
                item {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            headlineContent = {  Text(text = "云控参数管理") },
                            leadingContent = { Icon(painterResource(id = R.drawable.cloud_upload), contentDescription = "Localized description") },
                            modifier = Modifier.clickable {},
                        )
                    }
                }
                item {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            headlineContent = {  Text(text = "强制预警") },
                            leadingContent = { Icon(painterResource(id = R.drawable.info), contentDescription = "Localized description") },
                            modifier = Modifier.clickable {},
                        )
                    }
                }
                item {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            headlineContent = {  Text(text = "终止使崩溃") },
                            // overlineContent = { Text(text = "${item["日期时间"]}") },
                            leadingContent = { Icon(Icons.Filled.Refresh, contentDescription = "Localized description",) },
                            modifier = Modifier.clickable {},
                        )
                    }
                }
                item { userAnalysesUI(vm) }
                item {
                    Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
    }

}

fun getURL() : String {
    val json = prefs.getString(PrefsKey.GUTHUB.name,"")
    return try {
        Gson().fromJson(json,GithubResponse::class.java).API
    } catch (e : Exception) {
        "http://www.chiuxah.xyz/"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun userAnalysesUI(vm : NetViewModel) {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
    ){
        ListItem(
            headlineContent = {  Text(text = "用户回传统计") },
            // overlineContent = { Text(text = "${item["日期时间"]}") },
            leadingContent = { Icon(painterResource(id = R.drawable.person), contentDescription = "Localized description") },
            modifier = Modifier.clickable { showBottomSheet = true },
        )
    }



    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("用户数据") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    userInfoUI(vm)
                }
            }
        }
    }
}

@Composable
fun userInfoUI(vm : NetViewModel) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    CoroutineScope(Job()).launch{
        async{ vm. }.await()
        async {
            Handler(Looper.getMainLooper()).post{
                vm.result.observeForever { result ->
                    if (result != null) {
                        //        Log.d("sssss",result)
                        if(result.contains("Schedule")) {
                            loading = false
                            refresh = false
                        }
                    }
                }
            }
        }
    }
}