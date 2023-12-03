package com.hfut.schedule.logic

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.hfut.schedule.MyApplication

object OpenAlipay {
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            val packageManager = context.packageManager
            val info = packageManager.getPackageInfo(packageName, 0)
            info != null
        } catch (e: Exception) {
            // 处理异常或根据需要记录异常信息
            false
        }
    }

    fun openAlipay() {
        val alipayPackageName = "com.eg.android.AlipayGphone"
        val isAlipayInstalled = isAppInstalled(MyApplication.context, alipayPackageName)

        //if (isAlipayInstalled) {
            // 打开支付宝应用
            val intent = Intent(Intent.ACTION_DEFAULT, Uri.parse(MyApplication.AlipayURL))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(intent)
      //  } else {
          //  Toast.makeText(MyApplication.context, "未安装支付宝", Toast.LENGTH_SHORT).show()
      //  }
    }
}


