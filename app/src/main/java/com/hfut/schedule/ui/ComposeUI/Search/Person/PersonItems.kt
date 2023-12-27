package com.hfut.schedule.ui.ComposeUI.Search.Person

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import org.jsoup.Jsoup




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonItems() {

    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)


    val info = prefs.getString("info","")


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
        }
    }

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
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("个人信息") }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ListItem(
                headlineContent = { Text(text = "姓名   ${name}") },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.signature),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable {}
            )

            ListItem(
                headlineContent = { Text(text = "学号   ${studentnumber}") },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.badge),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable {}
            )




            ListItem(
                headlineContent = { Text(text = "类别   ${benorsshuo}") },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.school),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable {}
            )

            ListItem(
                headlineContent = { Text(text = "校区   ${school}") },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.location_city),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable {}
            )


            ListItem(
                headlineContent = { Text(text = "院系   ${yuanxi}") },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.local_library),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable {}
            )


            ListItem(
                headlineContent = { Text(text = "专业   ${zhuanye}") },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.square_foot),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable {}
            )

            ListItem(
                headlineContent = { Text(text = "班级   ${classes}") },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.sensor_door),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable {}
            )


            ListItem(
                headlineContent = { Text(text = "生源地   ${home}") },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.home),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable {}
            )

            ListItem(
                headlineContent = { Text(text = "身份证号   ${chineseid}") },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.tag),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable {}
            )


            ListItem(
                headlineContent = { Text(text = "寝室   待开发") },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.bed),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable {}
            )
        }
    }

}