package com.hfut.schedule.ui.Activity.success.search.Search.SchoolCard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.activity.CardActivity
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.calendar.nonet.getCourseINFO
import com.hfut.schedule.ui.Activity.success.focus.Focus.MySchedule
import com.hfut.schedule.ui.Activity.success.focus.Focus.TodayUI
import com.hfut.schedule.ui.Activity.success.focus.Focus.getToday
import com.hfut.schedule.ui.Activity.success.search.Search.Lab.LabUI
import com.hfut.schedule.ui.UIUtils.ScrollText


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodayAndCard(vmUI : UIViewModel) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by  remember { mutableStateOf(false) }
    if (showBottomSheet ) {
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
                        title = { Text("Today") }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    TodayInfo()
                }
            }
        }
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        ){
            Row {
                Box(modifier = Modifier.weight(.5f)) {
                    SchoolCardItem(vmUI)
                }
                Box(modifier = Modifier
                    .weight(.5f)
                    .clickable {
                        showBottomSheet = true
                    }) {
                    TodayUI()
                }
            }
        }
    }

}
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SchoolCardItem(vmUI : UIViewModel) {

    val interactionSource2 = remember { MutableInteractionSource() }
    val isPressed2 by interactionSource2.collectIsPressedAsState()
    val scale2 = animateFloatAsState(
        targetValue = if (isPressed2) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    var card = prefs.getString("card","00")
  //  val bd = BigDecimal(card)
  //  val str = bd.setScale(2, RoundingMode.HALF_UP).toString()
    val test = vmUI.CardValue.value?.balance ?: card

    ListItem(
        headlineContent = { ScrollText(text = "￥$test") },
        overlineContent = { Text(
            if (test != null) {
            if(test.length <= 4){
                "余额不足"
            } else "一卡通"
        } else "一卡通")},
        leadingContent = { Icon(painterResource(R.drawable.credit_card), contentDescription = "Localized description",) },
        trailingContent={
                FilledTonalIconButton(
                    modifier = Modifier
                        .scale(scale2.value)
                        .size(30.dp),
                    interactionSource = interactionSource2,
                    onClick = { StartApp.openAlipay(MyApplication.AlipayCardURL) },
                ) { Icon( painterResource(R.drawable.add), contentDescription = "Localized description",) }
        },
        colors = (
            if (test != null) {
                if(test.length <= 4){
                    ListItemDefaults.colors(MaterialTheme.colorScheme.errorContainer)
                } else ListItemDefaults.colors()
            } else ListItemDefaults.colors()
        ),
        modifier = Modifier.clickable {
            val it = Intent(MyApplication.context, CardActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            MyApplication.context.startActivity(it)
        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodayInfo() {
        if(getToday()?.todayExam?.courseName != null) {
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium

            ){
                ListItem(
                    headlineContent = { Text(text = getToday()?.todayExam?.courseName.toString()) },
                    overlineContent = { Text(text = getToday()?.todayExam?.startTime + "~" + getToday()?.todayExam?.endTime) },
                    supportingContent = { Text(text = getToday()?.todayExam?.place.toString())},
                    leadingContent = { Icon(painter = painterResource(R.drawable.draw), contentDescription = "")},
                )
            }
        }
        if(getToday()?.todayCourse?.courseName != null) {
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium

            ){
                ListItem(
                    headlineContent = { Text(text = getToday()?.todayCourse?.courseName.toString()) },
                    overlineContent = { Text(text = getToday()?.todayCourse?.startTime + "~" + getToday()?.todayCourse?.endTime +  "  " +  getToday()?.todayCourse?.place)},
                    supportingContent = { getToday()?.todayCourse?.className?.let { Text(text = it) } },
                    leadingContent = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "")},
                )
            }
        }
        if(getToday()?.bookLending?.bookName != null) {
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium

            ){
                ListItem(
                    headlineContent = { Text(text = getToday()?.bookLending?.bookName.toString()) },
                    supportingContent = { Text(text = "归还时间 " + getToday()?.bookLending?.returnTime) },
                    overlineContent = { Text(text = "借阅于 " + getToday()?.bookLending?.outTime + "\n应还于 " + getToday()?.bookLending?.dueTime )},
                    leadingContent = { Icon(painter = painterResource(R.drawable.book), contentDescription = "")},
                )
            }
        }
        if(getToday()?.todayActivity?.activityName != null) {
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium

            ){
                ListItem(
                    headlineContent = { Text(text = getToday()?.todayActivity?.activityName.toString()) },
                    overlineContent = { Text(text = getToday()?.todayActivity?.startTime.toString()) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.schedule), contentDescription = "")},
                )
            }
        }
}