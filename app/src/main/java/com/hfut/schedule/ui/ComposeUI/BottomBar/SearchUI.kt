package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ui.ViewModel.JxglstuViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(vm : JxglstuViewModel) {
    //待开发
    // 考试安排 //培养方案 //空教室 //一卡通
    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val cookie = prefs.getString("redirect", "")

    CoroutineScope(Job()).apply {
        launch { vm.getExam(cookie!!) }
        launch { vm.getProgram(cookie!!) }
        launch { vm.getGrade(cookie!!) }
    }




   // ListItem(
     //   headlineText = { Text(text = "暂未开发 敬请期待") },
        // supportingText =
       // leadingContent = {
         //   Icon(
           //     painterResource(R.drawable.error),
             //   contentDescription = "Localized description",
           // )
       // },
        //modifier = Modifier.clickable{

        //}
    //)
}