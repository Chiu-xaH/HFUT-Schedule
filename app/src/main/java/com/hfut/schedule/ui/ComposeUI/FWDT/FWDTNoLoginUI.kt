package com.hfut.schedule.ui.ComposeUI.FWDT

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.hfut.schedule.ViewModel.FWDTViewMoodel
import com.hfut.schedule.logic.Encrypt.FWDTPsk
import com.hfut.schedule.logic.SharePrefs.prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@Composable
fun CodeImg() {

   // Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center){
     ///   AsyncImage(
        //    model = "http://172.31.248.26:8088/Login/GetValidateCode",
          //  contentDescription = "",
            //modifier = Modifier.size(200.dp))
   // }
}
@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FWDTNotLoginUI(vm : FWDTViewMoodel) {
    var s by remember { mutableStateOf(false) }
    //获取身份证号作为密码并Base64加密
    val outputBase64 = FWDTPsk.encodeBase64(FWDTPsk.GetFWDTPsk())

    val user = prefs.getString("Username", "")


    CoroutineScope(Job()).launch {
        delay(1000)
        s = true
    }
    Column(modifier = Modifier.fillMaxWidth()) {

        var input by remember { mutableStateOf( "") }

        Spacer(modifier = Modifier.height(30.dp))

        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 40.dp),
                value = input,
                onValueChange = {input = it },
                label = { Text("验证码" ) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                    unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        //提交验证码
                        user?.let { vm.login(it,outputBase64,input) }
                    }) { Icon(painter = painterResource(R.drawable.login), contentDescription = "description") }
                }
            )
        }

        CodeImg()

    }

}