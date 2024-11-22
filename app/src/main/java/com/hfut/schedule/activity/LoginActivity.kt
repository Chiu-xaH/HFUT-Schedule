package com.hfut.schedule.activity

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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.login.main.LoginUI
import com.hfut.schedule.ui.Activity.success.cube.Settings.Update.checkAndRequestStoragePermission
import com.hfut.schedule.ui.Activity.success.main.saved.Add
import com.hfut.schedule.ui.Activity.success.main.saved.NoNetWork
import com.hfut.schedule.ui.Activity.success.main.saved.getNum
import com.hfut.schedule.ui.UIUtils.MyToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private val startAcitivity = prefs.getBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false)

    val switchServer = SharePrefs.prefs.getBoolean("SWITCHSERVER", true)
    val switchUpload = prefs.getBoolean("SWITCHUPLOAD",true )
    var value = 0

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun UI() {
        if(startAcitivity && intent.getBooleanExtra("nologin",true)) {
            NoNetWork(super.networkVm,super.loginVm,super.uiVm)
        } else LoginUI(super.loginVm)
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
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation", "MissingInflatedId", "UnspecifiedRegisterReceiverFlag")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.data?.let { uri ->
            val content = readTextFromUri(uri)
            // 处理读取到的文本内容
            Add("课表"+(getNum() +4).toString())
            SharePrefs.Save("SCHEDULE" + getNum(), content)
            MyToast("导入课表${getNum() +3}成功 请于课程表右上角切换")
        }


        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_CALENDAR),1)

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CALENDAR),1)


        checkAndRequestStoragePermission(this)
        lifecycleScope.launch {
                if(!(startAcitivity && intent.getBooleanExtra("nologin",true))) {
                    launch { super.loginVm.getCookie() }
                    launch { SharePrefs.Save("tip","0") }
                    launch {  super.loginVm.getKey() }
                    launch {
                        async {super.loginVm.getTicket()}.await()
                        async {
                            Handler(Looper.getMainLooper()).post{
                                super.loginVm.webVpnTicket.observeForever { result ->
                                    // Log.d("sss",result.toString())
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
                launch { super.uiVm.getUpdate() }
                launch { super.loginVm.My() }
                if(switchServer) { launch { super.networkVm.getData() } }
                if(switchUpload && value == 0) {
                    launch {
                    super.networkVm.postUser()
                    value++
                } }
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            1 -> {
                if(grantResults.isNotEmpty() && grantResults[0]  == PackageManager.PERMISSION_GRANTED) {

                }else Toast.makeText(this,"拒绝权限后添加日程将不可用",Toast.LENGTH_SHORT).show()
            }
        }
    }

}




