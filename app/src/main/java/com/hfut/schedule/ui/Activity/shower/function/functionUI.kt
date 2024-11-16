package com.hfut.schedule.ui.Activity.shower.function

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.ClipBoard
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.Activity.success.search.Search.More.LoginGuaGua
import com.hfut.schedule.ui.UIUtils.MyToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuaGuaSettings(innerPadding: PaddingValues) {
       Column(modifier = Modifier
           .fillMaxSize()
           .verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        ListItem(
            headlineContent = { Text(text = "刷新登录状态") },
            supportingContent = { Text(text = "呱呱物联只允许登录一端，在使用小程序后需要重新登录") },
            leadingContent = {
                Icon(painterResource(id = R.drawable.rotate_right), contentDescription = "")
            },
            modifier = Modifier.clickable { LoginGuaGua() }
        )
           ListItem(
               headlineContent = { Text(text = "修改loginCode") },
               supportingContent = { Text(text = "保持多端loginCode一致可实现多端登录") },
               leadingContent = {
                   Icon(painterResource(id = R.drawable.cookie), contentDescription = "")
               }
           )
        EditLoginCode()
        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLoginCode() {
    var input by remember { mutableStateOf(SharePrefs.prefs.getString("loginCode","") ?: "") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 15.dp),
            value = input,
            onValueChange = {
                input = it
                SharePrefs.Save("loginCode",input)
            },
            label = { Text("loginCode" ) },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        ClipBoard.copy(input)
                        MyToast("已复制")
                    }) {
                    Icon(painter = painterResource(R.drawable.copy_all), contentDescription = "description")
                }
            },
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
            ),
        )
    }
}