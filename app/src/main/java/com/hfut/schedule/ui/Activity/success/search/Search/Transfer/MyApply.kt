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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.Activity.success.search.Search.Person.getPersonInfo
import com.hfut.schedule.ui.UIUtils.ScrollText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun MyApply(vm: LoginSuccessViewModel) {

    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = SharePrefs.prefs.getString("redirect", "")

    val campus = if(getCampus()?.contains("宣城") == true) CampusId.XUANCHENG else CampusId.HEFEI

    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async{ cookie?.let { vm.getMyApply(it,campus)} }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.myApplyData.observeForever { result ->
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
            val data = getMyTransfer(vm)
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                val res = data.preparedStdCount.toDouble() / data.applyStdCount.toDouble() * 100.0
                val bd = BigDecimal(res.toString())
                val num = bd.setScale(1, RoundingMode.HALF_UP).toString()
                ListItem(headlineContent = { Text(text = if(getApplyStatus(vm) == true) "转入申请已通过" else " 状态未知或未通过", fontSize = 28.sp) })


                Row {
                    ListItem(
                        headlineContent = { getPersonInfo().major?.let { ScrollText(text = it) } },
                        overlineContent = { getPersonInfo().department?.let { ScrollText(text = it) } },
                        modifier = Modifier.weight(.4f)
                    )
                    //   ListItem(
                    //     headlineContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "") },
                    //   overlineContent = { Text(text = "")},
                    // modifier = Modifier.weight(.2f)
                    //  )
                    ListItem(
                        headlineContent = { ScrollText(text = data.major.nameZh) },
                        overlineContent = { ScrollText(text = data.department.nameZh) },
                        leadingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "") },
                        modifier = Modifier.weight(.6f)
                    )
                }

                Row {
                    ListItem(
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.group), contentDescription = "") },
                        overlineContent = { ScrollText(text = "已申请/计划录取") },
                        headlineContent = { Text(text = "${data.applyStdCount} / ${data.preparedStdCount}", fontWeight = FontWeight.Bold ) },
                        modifier = Modifier.weight(.5f)
                    )
                    ListItem(
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.percent), contentDescription = "") },
                        headlineContent = { Text(text = "${num} %", fontWeight = FontWeight.Bold) },
                        overlineContent = { Text(text = "通过概率") },
                        modifier = Modifier.weight(.5f)
                    )
                }


            }
        }
    }


}