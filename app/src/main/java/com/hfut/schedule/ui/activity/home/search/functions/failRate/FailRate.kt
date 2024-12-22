package com.hfut.schedule.ui.activity.home.search.functions.failRate

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.utils.Round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


fun Click(vm: NetWorkViewModel, input : String, page : Int) {
    val CommuityTOKEN = SharePrefs.prefs.getString("TOKEN","")
    CommuityTOKEN?.let { vm.SearchFailRate(it,input,page.toString()) }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FailRate(vm: NetWorkViewModel) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }


    androidx.compose.material3.ListItem(
        headlineContent = { Text(text = "挂科率") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.monitoring), contentDescription = "")},
        modifier = Modifier.clickable { showBottomSheet = true }
    )


    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
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
                        title = { Text("挂科率") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {

                    FailRateSearch(vm)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FailRateSearch(vm: NetWorkViewModel) {
    var input by remember { mutableStateOf( "") }
    var onclick by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 15.dp),
            value = input,
            onValueChange = {
                input = it
            },
            label = { Text("输入科目名称" ) },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    // shape = RoundedCornerShape(5.dp),
                    onClick = {
                        CoroutineScope(Job()).launch{
                            async{
                                Click(vm, input, 1)
                                loading = true
                                onclick = true
                                Handler(Looper.getMainLooper()).post{
                                    vm.FailRateData.value = "{}"
                                }
                            }.await()
                            async {
                                Handler(Looper.getMainLooper()).post{
                                    vm.FailRateData.observeForever { result ->
                                        if(result.contains("操作成功"))
                                            loading = false
                                    }
                                }
                            }
                        }
                    }) {
                    Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                }
            },
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
            ),
        )
    }
    Spacer(modifier = Modifier.height(10.dp))


    if(onclick){
        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                CircularProgressIndicator()
            }
        }////加载动画居中，3s后消失

        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) { FailRateUI(vm) }
    }
    Spacer(modifier = Modifier.height(30.dp))
}
var permit = 1
@Composable
fun ApiToFailRate(input : String,vm: NetWorkViewModel) {
    var loading by remember { mutableStateOf(true) }
    if(permit == 1)
    CoroutineScope(Job()).launch {
        async{
            Click(vm, input, 1)
            Handler(Looper.getMainLooper()).post{
                vm.FailRateData.value = "{}"
            }
        }.await()
        async {
            Handler(Looper.getMainLooper()).post{
                vm.FailRateData.observeForever { result ->
                    if(result.contains("操作成功")) {
                        permit++
                        loading = false
                    }
                }
            }
        }
    }
    Box {
        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                CircularProgressIndicator()
            }
        }
        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) { FailRateUI(vm) }
    }

}