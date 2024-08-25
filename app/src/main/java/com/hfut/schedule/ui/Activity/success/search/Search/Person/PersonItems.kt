package com.hfut.schedule.ui.Activity.success.search.Search.Person

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.ClipBoard
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.search.Search.LoginWeb.getIdentifyID
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.ScrollText
import org.jsoup.Jsoup




@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PersonItems(ifSaved : Boolean) {

   // val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)


    val cookie = SharePrefs.prefs.getString("redirect", "")
    val photo = prefs.getString("photo",null)




    val studentnumber = getPersonInfo().username
    val name = getPersonInfo().name
    val chineseid = getPersonInfo().chineseID



    //SharePrefs.Save("ChineseId",chineseid)

    val benorsshuo = getPersonInfo().benshuo
    val yuanxi = getPersonInfo().department
    val zhuanye = getPersonInfo().major
    val classes = getPersonInfo().classes
    val school = getPersonInfo().school
    val home = getPersonInfo().home

    var show by remember { mutableStateOf(false) }
    val blurSize by animateDpAsState(targetValue = if (!show) 10.dp else 0.dp, label = "")
    var showPicture by remember { mutableStateOf(false) }
    val sizePicture by animateDpAsState(targetValue = if (!showPicture) 130.dp else 0.dp, label = "")
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
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {

            DividerText(text = "账号信息")
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                ListItem(
                    headlineContent = { Text(text = "姓名   ${name}") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.signature),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        ClipBoard.copy(name)
                        MyToast("已复制到剪切板")
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
                        ClipBoard.copy(studentnumber)
                        MyToast("已复制到剪切板")
                    }
                )

                ListItem(
                    headlineContent = { ScrollText(text = "身份证号   ${chineseid}") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.tag),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        ClipBoard.copy(chineseid)
                        MyToast("已复制到剪切板")
                    }
                )
            }


            DividerText(text = "就读信息")

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                ListItem(
                    headlineContent = { Text(text = "校区   ${school}") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.location_city),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        ClipBoard.copy(school)
                        MyToast("已复制到剪切板")
                    }
                )


                ListItem(
                    headlineContent = { ScrollText(text = "院系   ${yuanxi}") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.local_library),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        ClipBoard.copy(yuanxi)
                        MyToast("已复制到剪切板")
                    }
                )


                ListItem(
                    headlineContent = { ScrollText(text = "专业   ${zhuanye}") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.square_foot),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        ClipBoard.copy(zhuanye)
                        MyToast("已复制到剪切板")
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
                        ClipBoard.copy(classes)
                        MyToast("已复制到剪切板")
                    }
                )
            }

            DividerText(text = "密码信息")
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                ListItem(
                    headlineContent = { Text(text = "密码") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.key),
                            contentDescription = "Localized description",
                        )
                    },
                    trailingContent = {
                        FilledTonalIconButton(onClick = { show = !show }) {
                            Icon(painter = painterResource(id = if(show)R.drawable.visibility else R.drawable.visibility_off), contentDescription = "")
                        }
                    }
                )
                Column(modifier = Modifier.blur(radius = blurSize)) {
                    ListItem(
                        headlineContent = { prefs.getString("Password","")?.let { Text(text = it) } },
                        overlineContent = { Text(text = "CAS统一认证密码")},
                        modifier = Modifier.clickable {
                            ClipBoard.copy(prefs.getString("Password",""))
                            MyToast("已复制到剪切板")
                        }
                    )
                    ListItem(
                        headlineContent = {
                            if (chineseid != null) { Text(text = "Hfut@#\$%${chineseid.takeLast(6)}") }
                        },
                        overlineContent = { Text(text = "教务系统初始密码")},
                        modifier = Modifier.clickable {
                            if (chineseid != null) {
                                ClipBoard.copy("Hfut@#\$%${chineseid.takeLast(6)}")
                                MyToast("已复制到剪切板")
                            }
                        }
                    )
                    ListItem(
                        headlineContent = { getIdentifyID()?.let { Text(text = it) } },
                        overlineContent = { Text(text = "一卡通&校园网初始密码")},
                        modifier = Modifier.clickable {
                            ClipBoard.copy(getIdentifyID())
                            MyToast("已复制到剪切板")
                        }
                    )
                }

            }


            DividerText(text = "其他信息")

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                ListItem(
                    headlineContent = { Text(text = "类别   ${benorsshuo}") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.school),
                            contentDescription = "Localized description",
                        )
                    },
      //              trailingContent = {
        //                FilledTonalIconButton(onClick = { showPicture = !showPicture }) {
          //  //                Icon(painter = painterResource(id = if(showPicture)R.drawable.visibility else R.drawable.visibility_off), contentDescription = "")
           //             }
            //        },
                    modifier = Modifier.clickable {
                        ClipBoard.copy(benorsshuo)
                        MyToast("已复制到剪切板")
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
                        ClipBoard.copy(home)
                        MyToast("已复制到剪切板")
                    }
                )
                ListItem(
                    headlineContent = { Text(text = "学籍照") },
                    trailingContent = {
                        if(photo != null) {
                            val byteArray = Base64.decode(photo, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(byteArray , 0, byteArray.size)
                            val imageBitmap = bitmap.asImageBitmap()
                            Image(bitmap = imageBitmap,
                                contentDescription = "Displayed image",
                                modifier = Modifier
                                    .size(130.dp)
                                    .padding(10.dp))
                        }

                    },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.background_replace),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.combinedClickable(
                        onDoubleClick = null,
                        onClick = { },
                        onLongClick = {
                            //保存
                        }
                    )
                )

            }
        }
    }
}

data class PersonInfo(val name : String?,
                      val username: String?,
                      val chineseID : String?,
                      val classes : String?,
                      val major : String?,
                      val department : String?,
                      val school : String?,
                      val benshuo : String?,
                      val home: String?)

fun getPersonInfo() : PersonInfo {
 //   val cookie = SharePrefs.prefs.getString("redirect", "")
 //   val photo = prefs.getString("photo",null)
    try {
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

        //SharePrefs.Save("ChineseId",chineseid)

        val benorsshuo =infoMap[elements?.get(8)?.text()]
        val yuanxi =infoMap[elements?.get(10)?.text()]
        val zhuanye =infoMap[elements?.get(12)?.text()]
        val classes =infoMap[elements?.get(16)?.text()]
        val school =infoMap[elements?.get(18)?.text()]
        val home =infoMap[elements?.get(80)?.text()]
        return PersonInfo(name,studentnumber,chineseid,classes,zhuanye,yuanxi, school,benorsshuo,home)
    } catch (_:Exception) {
        return PersonInfo(null,null,null,null,null,null,null,null,null)
    }
}