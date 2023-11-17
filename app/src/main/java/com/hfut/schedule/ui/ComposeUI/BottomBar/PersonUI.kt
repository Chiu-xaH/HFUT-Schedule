package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.activity.LoginActivity
import com.hfut.schedule.ViewModel.JxglstuViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jsoup.Jsoup


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen(vm : JxglstuViewModel) {


    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)


            val info = prefs.getString("info","")


    if (info == null) {
        val cookie = prefs.getString("redirect", "")
        vm.getInfo(cookie!!)
        Toast.makeText(MyApplication.context, "数据为空,刷新尝试", Toast.LENGTH_SHORT).show()
    }

    val doc = info?.let { Jsoup.parse(it) }
            val studentnumber = doc?.select("li.list-group-item.text-right:contains(学号) span")?.last()?.text()
            val name = doc?.select("li.list-group-item.text-right:contains(中文姓名) span")?.last()?.text()
            val chineseid = doc?.select("li.list-group-item.text-right:contains(证件号) span")?.last()?.text()
    val elements = doc?.select("dl dt, dl dd")

    val infoMap = mutableMapOf<String, String>()
    if (elements != null) {
        for (i in 0 until elements.size step 2) {
            val key = elements[i].text()
            val value = elements[i+1].text()
            infoMap[key] = value
           // Log.d(i.toString(),value)
        }
    }
   // var benorsshuo by remember { mutableStateOf(infoMap[elements?.get(8)?.text()] ?: "") }
    //var yuanxi by remember { mutableStateOf(infoMap[elements?.get(10)?.text()] ?: "") }
    //var zhuanye by remember { mutableStateOf(infoMap[elements?.get(12)?.text()] ?: "") }
   // var classes by remember { mutableStateOf(infoMap[elements?.get(16)?.text()] ?: "") }
   // var school by remember { mutableStateOf(infoMap[elements?.get(18)?.text()] ?: "") }
   // var home by remember { mutableStateOf(infoMap[elements?.get(80)?.text()] ?: "") }
    val benorsshuo =infoMap[elements?.get(8)?.text()]
    val yuanxi =infoMap[elements?.get(10)?.text()]
    val zhuanye =infoMap[elements?.get(12)?.text()]
    val classes =infoMap[elements?.get(16)?.text()]
    val school =infoMap[elements?.get(18)?.text()]
    val home =infoMap[elements?.get(80)?.text()]






    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("嗨  亲爱的 ${name} 同学") }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            //.background()插入背景
        ) {
          Column(modifier = Modifier
              .fillMaxWidth()) {
             // Spacer(modifier = Modifier.height(15.dp))

              ListItem(
                  headlineContent = { Text(text = "姓名   ${name}") },
                  leadingContent = {
                      Icon(
                          painterResource(R.drawable.signature),
                          contentDescription = "Localized description",
                      )
                  },
                  modifier = Modifier.clickable {

                  }
              )

              ListItem(
                  headlineContent = { Text(text = "学号   ${studentnumber}") },
                  leadingContent = {
                      Icon(
                          painterResource(R.drawable.badge),
                          contentDescription = "Localized description",
                      )
                  },
                  modifier = Modifier.clickable {

                  }
              )




              ListItem(
                  headlineContent = { Text(text = "类别   ${benorsshuo}") },
                  leadingContent = {
                      Icon(
                          painterResource(R.drawable.school),
                          contentDescription = "Localized description",
                      )
                  },
                  modifier = Modifier.clickable {

                  }
              )

              ListItem(
                  headlineContent = { Text(text = "校区   ${school}") },
                  leadingContent = {
                      Icon(
                          painterResource(R.drawable.location_city),
                          contentDescription = "Localized description",
                      )
                  },
                  modifier = Modifier.clickable {

                  }
              )


              ListItem(
                  headlineContent = { Text(text = "院系   ${yuanxi}") },
                  leadingContent = {
                      Icon(
                          painterResource(R.drawable.local_library),
                          contentDescription = "Localized description",
                      )
                  },
                  modifier = Modifier.clickable {

                  }
              )


              ListItem(
                  headlineContent = { Text(text = "专业   ${zhuanye}") },
                  leadingContent = {
                      Icon(
                          painterResource(R.drawable.square_foot),
                          contentDescription = "Localized description",
                      )
                  },
                  modifier = Modifier.clickable {

                  }
              )

              ListItem(
                  headlineContent = { Text(text = "班级   ${classes}") },
                  leadingContent = {
                      Icon(
                          painterResource(R.drawable.sensor_door),
                          contentDescription = "Localized description",
                      )
                  },
                  modifier = Modifier.clickable {

                  }
              )


              ListItem(
                  headlineContent = { Text(text = "生源地   ${home}") },
                  leadingContent = {
                      Icon(
                          painterResource(R.drawable.home),
                          contentDescription = "Localized description",
                      )
                  },
                  modifier = Modifier.clickable {

                  }
              )

              ListItem(
                  headlineContent = { Text(text = "身份证号   ${chineseid}") },
                  leadingContent = {
                      Icon(
                          painterResource(R.drawable.tag),
                          contentDescription = "Localized description",
                      )
                  },
                  modifier = Modifier.clickable {

                  }
              )

          }
        }
    }
    //待开发
}

