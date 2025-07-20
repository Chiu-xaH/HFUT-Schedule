package com.hfut.schedule.activity

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomCourseTableEntity
import com.hfut.schedule.logic.util.sys.PermissionSet.checkAndRequestStoragePermission
import com.hfut.schedule.ui.screen.MainHost
import com.hfut.schedule.logic.util.sys.showToast
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : BaseActivity() {

    @Composable
    override fun UI() = MainHost(
        super.networkVm,
        super.loginVm,
        super.uiVm,
        intent.getBooleanExtra("nologin",true),
        false,
        false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //当用户以本应用打开TXT文件时进行读取操作
        intent?.data?.let { uri ->
            checkAndRequestStoragePermission(this)
            val content = readTextFromUri(uri)
            // 处理读取到的文本内容
            DataBaseManager.customCourseTableDao.let {
                lifecycleScope.launch {
                    async { it.insert(CustomCourseTableEntity(title = "课表"+(it.count()+1).toString(), contentJson = content)) }.await()
                    launch { showToast("导入课表成功 请于课程表右上角切换") }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onRequestPermissionsResult(requestCode, permissions, grantResults)")
    )
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) = super.onRequestPermissionsResult(requestCode, permissions, grantResults)

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
}




