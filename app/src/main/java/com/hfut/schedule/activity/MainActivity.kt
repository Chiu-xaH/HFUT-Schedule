package com.hfut.schedule.activity

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.ui.screen.MainHost
import com.hfut.schedule.ui.screen.home.cube.screen.AppTransitionInitializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    @Composable
    override fun UI() = MainHost(
        super.networkVm,
        super.loginVm,
        super.uiVm,
        intent.getBooleanExtra("nologin",true),
        false,
        intent.getStringExtra("route"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            AppTransitionInitializer.init()
        }
//        //当用户以本应用打开TXT文件时进行读取操作
//        intent?.data?.let { uri ->
//            checkAndRequestStoragePermission(this)
//            val content = readTextFromUri(uri)
//            // 处理读取到的文本内容
//            DataBaseManager.customCourseTableDao.let {
//                lifecycleScope.launch {
//                    async { it.insert(CustomCourseTableEntity(title = "课表"+(it.count()+1).toString(), contentJson = content)) }.await()
//                    launch { showToast("导入课表成功 请于课程表右上角切换") }
//                }
//            }
//        }
    }

//    //打开方式txt
//    private fun readTextFromUri(uri: Uri): String {
//        val stringBuilder = StringBuilder()
//        contentResolver.openInputStream(uri)?.use { inputStream ->
//            BufferedReader(InputStreamReader(inputStream)).use { reader ->
//                var line = reader.readLine()
//                while (line != null) {
//                    stringBuilder.append(line)
//                    line = reader.readLine()
//                }
//            }
//        }
//        return stringBuilder.toString()
//    }
}




