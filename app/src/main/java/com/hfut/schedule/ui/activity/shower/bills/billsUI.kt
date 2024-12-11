package com.hfut.schedule.ui.activity.shower.bills

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.GuaGuaViewModel
import com.hfut.schedule.ui.utils.MyCard
import com.hfut.schedule.ui.utils.ScrollText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun GuaguaBills(innerPadding: PaddingValues, vm: GuaGuaViewModel) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }

    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async{ vm.getBills() }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.billsResult.observeForever { result ->
                        if (result != null) {
                            if(result.contains("成功")) {
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

            val list = getGuaguaBills(vm)

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn {
                item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                item { Spacer(modifier = Modifier.height(5.dp)) }
                items(list.size) {item ->
                    MyCard {
                        val info = list[item].description
                        ListItem(
                            headlineContent = {
                                ScrollText(text =
                                if(info.contains("热水表: ")) info.substringAfter(": ")
                                else info
                                )
                            },
                            supportingContent = {
                                val t = if(info.contains("热水")) {
                                    "-￥${list[item].xfMoney}"
                                } else if(info.contains("充值")) {
                                    "+￥${list[item].dealMoney}"
                                } else {
                                    "处理 ￥${list[item].dealMoney} 扣费 ￥${list[item].xfMoney}"
                                }
                                Text(text = t)
                            },
                            overlineContent = {
                                Text(text = list[item].dealDate +  " " + list[item].dealMark)
                            },
                            leadingContent = {
                                Icon(
                                    painterResource(id =
                                if(info.contains("热水")) {
                                    R.drawable.bathtub
                                } else if(info.contains("充值")) {
                                    R.drawable.add_circle
                                } else {
                                    R.drawable.paid
                                }
                                ), contentDescription = "")
                            },)
                    }
                }
                item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
            }
        }
    }
}
