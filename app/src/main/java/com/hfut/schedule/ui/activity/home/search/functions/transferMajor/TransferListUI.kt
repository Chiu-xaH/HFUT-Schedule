package com.hfut.schedule.ui.activity.home.search.functions.transferMajor

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.hfut.schedule.logic.beans.Jxglstu.TransferData
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.reEmptyLiveDta
import com.hfut.schedule.ui.utils.MyCard
import com.hfut.schedule.ui.utils.MyToast
import com.hfut.schedule.ui.utils.ScrollText
import com.hfut.schedule.ui.utils.schoolIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferUI(vm: NetWorkViewModel, campus: CampusId) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = SharePrefs.prefs.getString("redirect", "")

   // val campus =

    LaunchedEffect(key1 = campus) {
        loading = true
        reEmptyLiveDta(vm.transferData)
        refresh = true
    }
    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async{ cookie?.let { vm.getTransfer(it,campus)} }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.transferData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("转专业")) {
                                loading = false
                                refresh = false
                            }
                        }
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
        ) {
            var input by remember { mutableStateOf("") }
            Column {

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
                        label = { Text("搜索学院或专业" ) },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {}) {
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
                val list = getTransfer(vm)
                val searchList = mutableListOf<TransferData>()
                list.forEach { item->
                    if(item.department.nameZh.contains(input) || item.major.nameZh.contains(input)) {
                        searchList.add(item)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn {
                    items(searchList.size) {item ->
                        MyCard {
                            var department = searchList[item].department.nameZh
                            if(department.contains("（")) department = department.substringBefore("（")
                            if(department.contains("(")) department = department.substringBefore("(")
                            ListItem(
                                headlineContent = { Text(text = searchList[item].major.nameZh) },
                                supportingContent = { searchList[item].registrationConditions?.let { Text(text = it) } },
                                overlineContent = { ScrollText(text = department + " 已申请 " + searchList[item].applyStdCount.toString() + " / " + searchList[item].preparedStdCount) },
                                leadingContent = { schoolIcons(searchList[item].department.nameZh) },
                                trailingContent = {  FilledTonalIconButton(onClick = {
                                    MyToast("正在开发")
                                }) { Icon(painter = painterResource(id = R.drawable.add_task), contentDescription = "") } },
                            )
                        }
                    }
                }
            }
        }
    }
}