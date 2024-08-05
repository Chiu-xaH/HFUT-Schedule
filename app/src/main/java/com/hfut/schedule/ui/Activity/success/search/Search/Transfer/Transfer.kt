package com.hfut.schedule.ui.Activity.success.search.Search.Transfer

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.Jxglstu.MyApplyResponse
import com.hfut.schedule.logic.datamodel.Jxglstu.TransferData
import com.hfut.schedule.logic.datamodel.Jxglstu.TransferResponse
import com.hfut.schedule.logic.datamodel.Jxglstu.courseType
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.Activity.success.search.Search.More.Login
import com.hfut.schedule.ui.Activity.success.search.Search.Survey.teacherList
import com.hfut.schedule.ui.Activity.success.search.Search.TotalCourse.courseIcons
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.ScrollText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.math.BigDecimal
import java.math.RoundingMode

enum class CampusId {
    HEFEI,XUANCHENG
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Transfer(ifSaved : Boolean,vm : LoginSuccessViewModel){
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val sheetState_apply = rememberModalBottomSheetState()
    var showBottomSheet_apply by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = "转专业") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.compare_arrows), contentDescription = "") },
        modifier = Modifier.clickable {
            if(ifSaved) Login() else
                showBottomSheet = true
        }
    )
    if (showBottomSheet_apply) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_apply = false },
            sheetState = sheetState_apply
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("转专业申请") },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    MyApply(vm)
                }
            }
        }
    }
    if (showBottomSheet) {
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
                        title = { Text("转专业") },
                        actions = {
                            FilledTonalButton(onClick = { showBottomSheet_apply = true }, modifier = Modifier.padding(horizontal = 15.dp)) {
                                Text(text = "我的申请")
                            }
                        }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    TransferUI(vm)
                }
            }
        }
    }
}
@Composable
fun TransferUI(vm: LoginSuccessViewModel) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = SharePrefs.prefs.getString("redirect", "")

    val campus = if(getCampus()?.contains("宣城") == true) CampusId.XUANCHENG else CampusId.HEFEI

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
                        ListItem(
                            headlineContent = { Text(text = list[item].major.nameZh) },
                            supportingContent = { list[item].registrationConditions?.let { Text(text = it) } },
                            overlineContent = { Text(text = list[item].department.nameZh)},
                            leadingContent = { courseIcons(list[item].department.nameZh) },
                            // trailingContent = {  FilledTonalIconButton(onClick = {
                            //        MyToast("正在开发")
                            //  }) { Icon(painter = painterResource(id = R.drawable.add_task), contentDescription = "") } },
                            trailingContent = {
                                Text(text = " 已申请 " + list[item].applyStdCount.toString() + " / " + list[item].preparedStdCount)
                            }
                        )
                    }
                }
            }
        }
    }

}

fun getCampus() : String? {
    val info = SharePrefs.prefs.getString("info","")


    val doc = info?.let { Jsoup.parse(it) }
    val elements = doc?.select("dl dt, dl dd")

    val infoMap = mutableMapOf<String, String>()
    if (elements != null) {
        for (i in 0 until elements.size step 2) {
            val key = elements[i].text()
            val value = elements[i+1].text()
            infoMap[key] = value
        }
    }

    return infoMap[elements?.get(18)?.text()]

}

fun getTransfer(vm : LoginSuccessViewModel) : MutableList<TransferData> {
    val list = mutableListOf<TransferData>()
    return try {
        val json = vm.transferData.value
        val data = Gson().fromJson(json, TransferResponse::class.java).data
        for (i in data.indices) {
            val planCount = data[i].preparedStdCount
            val count = data[i].applyStdCount
            val request = data[i].registrationConditions
            val department = data[i].department
            val major = data[i].major
            list.add(TransferData(request, department, major, planCount, count))
        }
        list
    } catch (e : Exception) {
        list
    }
}

fun getMyTransfer(vm : LoginSuccessViewModel) : TransferData {
  //  val list = mutableListOf<TransferData>()
    return try {
        val json = vm.myApplyData.value
        Gson().fromJson(json, MyApplyResponse::class.java).models[0].changeMajorSubmit

       // list
    } catch (e : Exception) {
        //list
        TransferData(null, courseType(""), courseType(""),0,0)
    }
}

@Composable
fun MyApply(vm: LoginSuccessViewModel) {


    val info = SharePrefs.prefs.getString("info","")
    val doc = info?.let { Jsoup.parse(it) }
    val elements = doc?.select("dl dt, dl dd")
    val infoMap = mutableMapOf<String, String>()
    if (elements != null) {
        for (i in 0 until elements.size step 2) {
            val key = elements[i].text()
            val value = elements[i+1].text()
            infoMap[key] = value
        }
    }
    val yuanxi =infoMap[elements?.get(10)?.text()]
    val zhuanye =infoMap[elements?.get(12)?.text()]


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
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
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
                        headlineContent = { zhuanye?.let { ScrollText(text = it) } },
                        overlineContent = { yuanxi?.let { ScrollText(text = it) } },
                        modifier = Modifier.weight(.4f)
                    )
                 //   ListItem(
                   //     headlineContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "") },
                     //   overlineContent = { Text(text = "")},
                       // modifier = Modifier.weight(.2f)
                  //  )
                    ListItem(
                        headlineContent = { ScrollText(text = data.major.nameZh)},
                        overlineContent = { ScrollText(text = data.department.nameZh) },
                        leadingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "") },
                        modifier = Modifier.weight(.6f)
                    )
                }

                Row {
                    ListItem(
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.group), contentDescription = "")},
                        overlineContent = { ScrollText(text = "已申请/计划录取") },
                        headlineContent = { Text(text = "${data.applyStdCount} / ${data.preparedStdCount}", fontWeight = FontWeight.Bold ) },
                        modifier = Modifier.weight(.5f)
                    )
                    ListItem(
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.percent), contentDescription = "")},
                        headlineContent = { Text(text = "${num} %", fontWeight = FontWeight.Bold) },
                        overlineContent = { Text(text = "通过概率")},
                        modifier = Modifier.weight(.5f)
                    )
                }


            }
        }
    }


}

fun getApplyStatus(vm : LoginSuccessViewModel) : Boolean? {
   return try {
       val json = vm.myApplyData.value
       val data = Gson().fromJson(json, MyApplyResponse::class.java).models[0].applyStatus
       data == "ACCEPTED"
   } catch (_:Exception) {
       null
   }
}