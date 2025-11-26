package com.hfut.schedule.ui.screen.xwx

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.model.xwx.XwxLoginInfo
import com.hfut.schedule.logic.model.xwx.XwxLoginResponseBody
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.viewmodel.network.XwxViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

suspend fun getXwxLogin(context : Context) : XwxLoginInfo? = withContext(Dispatchers.IO) {
    return@withContext try {
        val jStr = LargeStringDataManager.read(context, LargeStringDataManager.XWX_USER_INFO)
        withContext(Dispatchers.Default) {
            with(Gson().fromJson(jStr, XwxLoginResponseBody::class.java).result) {
                XwxLoginInfo(
                    data[0],
                    token
                )
            }
        }
    } catch (e : Exception) {
        e.printStackTrace()
        null
    }
}

suspend fun checkXwxLogin(vm: XwxViewModel,context : Context) : Boolean = withContext(Dispatchers.IO) {
    val data = vm.functionsResp.state.first()
    when(data) {
        is UiState.Error<*> -> {
            return@withContext false
        }
        is UiState.Success<*> -> {
            return@withContext true
        }
        else -> {
            // 检查
            val userInfo = getXwxLogin(context)
            if(userInfo == null) {
                return@withContext false
            }
            vm.functionsResp.clear()
            vm.getFunctions(
                schoolCode = userInfo.data.schoolCode,
                username = userInfo.data.userId,
                token = userInfo.token
            )
            val result = vm.functionsResp.state.first()
            when(result) {
                is UiState.Success<*> -> {
                    return@withContext true
                }
                else ->  {
                    return@withContext false
                }
            }
        }
    }
}

@Composable
fun XwxLoginScreen(
    vm: XwxViewModel,
    navController : NavHostController
) {

}


/*
宣区选择 合肥工业大学（宣城校区），学号为账号，身份证后六位为密码（包括最后的X）
 */
@Composable
fun XwxMainScreen(
    vm: XwxViewModel,
    navController : NavHostController
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        DividerTextExpandedWith("选项") {
            CustomCard(
                color = cardNormalColor()
            ) {
                TransplantListItem(
                    headlineContent = { Text("文件申请") },
                    modifier = Modifier.clickable {

                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("刷新登录状态") },
                    modifier = Modifier.clickable {

                    }
                )
            }
        }
        DividerTextExpandedWith("功能") {
            CustomCard(
                color = cardNormalColor()
            ) {

            }
        }
    }
}