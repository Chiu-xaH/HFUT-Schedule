package com.hfut.schedule.activity.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.ShowerScreen
import com.hfut.schedule.logic.model.xwx.XwxLoginInfo
import com.hfut.schedule.logic.model.xwx.XwxLoginResponseBody
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.shower.ShowerGuaGua
import com.hfut.schedule.ui.screen.shower.login.ShowerLogin
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.viewmodel.network.GuaGuaViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.network.XwxViewModel
import com.xah.uicommon.style.padding.InnerPaddingHeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class XwxActivity : BaseActivity() {
    @Composable
    override fun UI() {
        val networkVm = viewModel { XwxViewModel() }
        val navController = rememberNavController()
        val first = if(intent.getStringExtra("FIRST") == ShowerScreen.HOME.name) ShowerScreen.HOME.name else ShowerScreen.LOGIN.name
        NavHost(
            navController = navController,
            startDestination = first,
            enterTransition = {
                AppAnimationManager.fadeAnimation.enter
            },
            exitTransition = {
                AppAnimationManager.fadeAnimation.exit
            }
        ) {
            // 主UI
            composable(ShowerScreen.HOME.name) {
                XwxMainScreen(networkVm,navController)
            }
            // 游客模式
            composable(ShowerScreen.LOGIN.name) {
                XwxLoginScreen(networkVm,navController)
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

suspend fun getXwxLogin() : XwxLoginInfo? = withContext(Dispatchers.IO) {
    return@withContext try {
        val jStr = LargeStringDataManager.read(MyApplication.context, LargeStringDataManager.XWX_USER_INFO)
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

/*
宣区选择 合肥工业大学（宣城校区），学号为账号，身份证后六位为密码（包括最后的X）
 */
@Composable
fun XwxMainScreen(
    vm: XwxViewModel,
    navController : NavHostController
) {
//    Column(
//        modifier = Modifier.verticalScroll(rememberScrollState())
//    ) {
//        InnerPaddingHeight(innerPadding,true)
//        DividerTextExpandedWith("选项") {
//            CustomCard(
//                color = cardNormalColor()
//            ) {
//                TransplantListItem(
//                    headlineContent = { Text("文件申请") },
//                    modifier = Modifier.clickable {
//
//                    }
//                )
//                PaddingHorizontalDivider()
//                TransplantListItem(
//                    headlineContent = { Text("刷新登录状态") },
//                    modifier = Modifier.clickable {
//
//                    }
//                )
//            }
//        }
//        DividerTextExpandedWith("功能") {
//            CustomCard(
//                color = cardNormalColor()
//            ) {
//
//            }
//        }
//        InnerPaddingHeight(innerPadding,false)
//    }
}