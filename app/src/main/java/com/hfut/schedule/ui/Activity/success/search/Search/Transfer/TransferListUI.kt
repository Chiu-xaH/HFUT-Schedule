package com.hfut.schedule.ui.Activity.success.search.Search.Transfer

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.Activity.success.search.Search.TotalCourse.courseIcons
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.ScrollText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TransferUI(vm: LoginSuccessViewModel,campus: CampusId) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = SharePrefs.prefs.getString("redirect", "")

   // val campus =

    LaunchedEffect(key1 = campus) {
        loading = true
        delay(700)
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
            val list = getTransfer(vm)
            LazyColumn {
                items(list.size) {item ->
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        var department = list[item].department.nameZh
                        if(department.contains("（")) department = department.substringBefore("（")
                        if(department.contains("(")) department = department.substringBefore("(")
                        ListItem(
                            headlineContent = { Text(text = list[item].major.nameZh) },
                            supportingContent = { list[item].registrationConditions?.let { Text(text = it) } },
                            overlineContent = { ScrollText(text = department + " 已申请 " + list[item].applyStdCount.toString() + " / " + list[item].preparedStdCount) },
                            leadingContent = { courseIcons(list[item].department.nameZh) },
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