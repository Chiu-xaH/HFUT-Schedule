package com.hfut.schedule.activity.main

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.logic.utils.PermissionManager.checkAndRequestStoragePermission
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.parse.getCelebration
import com.hfut.schedule.ui.activity.home.main.saved.Add
import com.hfut.schedule.ui.activity.home.main.saved.NoNetWork
import com.hfut.schedule.ui.activity.home.main.saved.getNum
import com.hfut.schedule.ui.activity.login.First
//import com.hfut.schedule.ui.activity.login.FirstUI
import com.hfut.schedule.ui.activity.login.LoginUI
import com.hfut.schedule.ui.activity.login.UseAgreementUI
import com.hfut.schedule.ui.utils.NavigateAndAnimationManager
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.Party
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private val startAcitivity = prefs.getBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false)

//    val switchServer = SharePrefs.prefs.getBoolean("SWITCHSERVER", true)
    val switchUpload = prefs.getBoolean("SWITCHUPLOAD",true )
    var value = 0

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun UI() {
        val navController = rememberNavController()
        val first = if(prefs.getBoolean("canUse",false)) First.HOME.name else First.USE_AGREEMENT.name
        NavHost(
            navController = navController,
            startDestination = first,
            enterTransition = {
                NavigateAndAnimationManager.fadeAnimation.enter
            },
            exitTransition = {
                NavigateAndAnimationManager.fadeAnimation.exit
            }
        ) {
            composable(First.HOME.name) {
                @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                @Composable
                fun MainUI() {
                    if(startAcitivity && intent.getBooleanExtra("nologin",true)) {
                        NoNetWork(super.networkVm,super.loginVm,super.uiVm)
                    } else LoginUI(super.loginVm)
                }
                // 如果庆祝为true则庆祝
                Party(show = getCelebration()) { MainUI() }
            }
            composable(First.USE_AGREEMENT.name) {
                Party(timeSecond = 3L) {
                    UseAgreementUI(navController)
                }
            }
        }

    }

    //打开方式txt
    private fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }
    @SuppressLint("SuspiciousIndentation", "MissingInflatedId", "UnspecifiedRegisterReceiverFlag")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //当用户以本应用打开TXT文件时进行读取操作
        intent?.data?.let { uri ->
            checkAndRequestStoragePermission(this)
            val content = readTextFromUri(uri)
            // 处理读取到的文本内容
            Add("课表"+(getNum() +4).toString())
            SharePrefs.saveString("SCHEDULE" + getNum(), content)
            MyToast("导入课表${getNum() +3}成功 请于课程表右上角切换")
        }

        lifecycleScope.launch {
                //为登录做准备
                if(!(startAcitivity && intent.getBooleanExtra("nologin",true))) {
                    launch { super.loginVm.getCookie() }
                    launch { SharePrefs.saveString("tip","0") }
                    launch {  super.loginVm.getKey() }
                    launch {
                        async {super.loginVm.getTicket()}.await()
                        async {
                            Handler(Looper.getMainLooper()).post{
                                super.loginVm.webVpnTicket.observeForever { result ->
                                    if (result != null) {
                                        if (result.contains("wengine_vpn_ticketwebvpn_hfut_edu_cn")) {
                                            val ticket = result.substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=").substringBefore(";")
                                            super.loginVm.putKey(ticket)
                                        }
                                    }
                                }
                            }
                        }
                        async {
                            Handler(Looper.getMainLooper()).post{
                                super.loginVm.status.observeForever { result ->
                                    // Log.d("sss",result.toString())
                                    if (result == 200) {
                                        super.loginVm.getKeyWebVpn()
                                    }
                                }
                            }
                        }
                    }
                }
                //检查更新
                launch { super.uiVm.getUpdate() }
                //从服务器获取信息
                launch { super.loginVm.My() }
                //上传用户统计数据
                if(switchUpload && value == 0) {
                    launch {
                        super.networkVm.postUser()
                        value++
                    }
                }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            1 -> {
                if(grantResults.isNotEmpty() && grantResults[0]  == PackageManager.PERMISSION_GRANTED) {

                } else Toast.makeText(this,"拒绝权限后某些功能将不可用",Toast.LENGTH_SHORT).show()
            }
        }
    }

}




