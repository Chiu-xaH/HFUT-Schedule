package com.hfut.schedule.ui.screen.shower.cube

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.ShowerScreen
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveBoolean
import com.hfut.schedule.logic.util.sys.Starter.loginGuaGua
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP


import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.xah.transition.util.navigateAndClear
import com.hfut.schedule.ui.util.navigateAndSave
import com.hfut.schedule.ui.style.textFiledTransplant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuaGuaSettings(innerPadding: PaddingValues,navHostController: NavHostController) {
    val switch_usecode = prefs.getBoolean("SWITCHUSECODE",false)
    var autoUseCode by remember { mutableStateOf(switch_usecode) }
    saveBoolean("SWITCHUSECODE",true,autoUseCode)
       Column(modifier = Modifier
           .fillMaxSize()
           .verticalScroll(rememberScrollState())) {
           InnerPaddingHeight(innerPadding,true)
           DividerTextExpandedWith("设置") {
               MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                   TransplantListItem(
                       headlineContent = { Text(text = "刷新登录状态") },
                       supportingContent = { Text(text = "呱呱物联只允许登录一端，在使用小程序后需要重新登录") },
                       leadingContent = {
                           Icon(painterResource(id = R.drawable.rotate_right), contentDescription = "")
                       },
                       modifier = Modifier.clickable { navHostController.navigateAndSave(ShowerScreen.LOGIN.name) }
                   )
                   PaddingHorizontalDivider()
                   TransplantListItem(
                       headlineContent = { Text(text = "预加载使用码") },
                       supportingContent = { Text(text = "打开后将主动加载使用码，即使您不需要使用时") },
                       leadingContent = {
                           Icon(painterResource(id = R.drawable.reset_iso), contentDescription = "")
                       },
                       trailingContent = {
                           Switch(checked = autoUseCode, onCheckedChange = { autoUseCode = it })
                       },
                       modifier = Modifier.clickable { autoUseCode = !autoUseCode }
                   )
                   PaddingHorizontalDivider()

                   TransplantListItem(
                       headlineContent = { Text(text = "修改loginCode") },
                       supportingContent = { Text(text = "保持多端loginCode一致可实现多端登录") },
                       leadingContent = {
                           Icon(painterResource(id = R.drawable.cookie), contentDescription = "")
                       }
                   )
                   EditLoginCode()
               }
           }
           InnerPaddingHeight(innerPadding,false)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLoginCode(isOnLogin: Boolean = false, onClickLogin: (() -> Unit)? = null) {
    var input by remember { mutableStateOf(SharedPrefs.prefs.getString("loginCode","") ?: "") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = APP_HORIZONTAL_DP),
            value = input,
            onValueChange = {
                input = it
                SharedPrefs.saveString("loginCode",input)
            },
            label = { Text("loginCode" ) },
            singleLine = true,
            trailingIcon = {
                if(isOnLogin) {
                    onClickLogin?.let {
                        IconButton(
                            onClick = onClickLogin
                        ) {
                            Icon(painterResource(R.drawable.login),null)
                        }
                    }
                } else {
                    IconButton(
                        onClick = {
                            ClipBoardUtils.copy(input)
                        }) {
                        Icon(painter = painterResource(R.drawable.copy_all), contentDescription = "description")
                    }
                }
            },
            shape = MaterialTheme.shapes.medium,
            colors = textFiledTransplant(),
            supportingText = {
                if(isOnLogin)
                    Text("填写上方手机号,填写loginCode,点击右侧登录")
            }
        )
    }
}