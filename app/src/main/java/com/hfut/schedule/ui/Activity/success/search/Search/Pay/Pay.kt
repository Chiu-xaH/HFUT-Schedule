package com.hfut.schedule.ui.Activity.success.search.Search.Pay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.logic.datamodel.PayData
import com.hfut.schedule.logic.datamodel.PayResponse
import com.hfut.schedule.logic.utils.ClipBoard
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.ScrollText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pay(ifSaved : Boolean) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
 //   var showDialog by remember { mutableStateOf(false) }

    val url = "http://pay.hfut.edu.cn/payment/mobileOnlinePay"
    ListItem(
        headlineContent = { Text(text = "学费") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.paid), contentDescription = "") },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )
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
                        title = { Text("学费") },
                        actions = {
                            FilledTonalButton(
                                onClick = { StartApp.StartUri(url) },
                                modifier = Modifier.padding(horizontal = 15.dp)
                            ) {
                                Text(text = "缴费")
                            }
                        }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    PayUI(url)
                }
            }
        }
    }
}

@Composable
fun PayUI(url : String) {
    val data = getPay()
    DividerText(text = "欠缴费用")
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        ListItem(
            headlineContent = { Text(text = "￥${data.total}", fontSize = 28.sp) }
        )
        Row {
            ListItem(
                headlineContent = { ScrollText(text = "学费 ￥${data.xf}") },
                modifier = Modifier.weight(.5f)
            )
            ListItem(
                headlineContent = { ScrollText(text = "体检费 ￥${data.dstjf}") },
                modifier = Modifier.weight(.5f)
            )

        }
        Row {
            ListItem(
                headlineContent = { ScrollText(text = "住宿费 ￥${data.zsf}") },
                modifier = Modifier.weight(.5f)
            )
            ListItem(
                headlineContent = { ScrollText(text = "军训费 ￥${data.dsjxf}") },
                modifier = Modifier.weight(.5f)
            )
        }
    }
    DividerText(text = "缴费方式")

        ListItem(
            headlineContent = { Text(text = "提前在中国农业银行卡预存费用,学校到期自动扣取") },
            leadingContent = { Icon(
            painter = painterResource(id = R.drawable.credit_card),
            contentDescription = ""
        )})
        ListItem(
            headlineContent = { Text(text = "点击右上角打开链接即可调用电子支付(Apple Pay通道)") },
            leadingContent = {Icon(
                painter = painterResource(id = R.drawable.net),
                contentDescription = ""
            ) },
            modifier = Modifier.clickable { StartApp.StartUri(url) }
        )
        ListItem(
            headlineContent = { Text(text = "点击此处复制链接到剪切板，在微信/支付宝等中打开链接即可走对应的软件支付") },
            leadingContent = { Icon(
                painter = painterResource(id = R.drawable.barcode),
                contentDescription = ""
            )},
            modifier = Modifier.clickable {
                ClipBoard.copy(url)
                MyToast("已复制到剪切板")
            }
        )
  //  }
    DividerText(text = "防骗警告")
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        ListItem(
            headlineContent = { Text(text = "电子支付只能通过学校缴费平台官方链接(右上角按钮提供)发起,其余线上途径均需谨慎甄别!") },
            leadingContent = {Icon(
                painter = painterResource(id = R.drawable.error),
                contentDescription = ""
            )}
        )
    }
}


fun getPay() : PayData {
    return try {
        val json = prefs.getString("Onepay","")
        Gson().fromJson(json,PayResponse::class.java).data
    } catch (e : Exception) {
        PayData("0","0","0","0","0")
    }
}