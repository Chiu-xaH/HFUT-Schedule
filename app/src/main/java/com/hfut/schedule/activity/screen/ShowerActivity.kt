package com.hfut.schedule.activity.screen

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.activity.BaseActivity
import com.hfut.schedule.logic.enumeration.ShowerScreen
import com.hfut.schedule.ui.screen.shower.login.ShowerLogin
import com.hfut.schedule.ui.screen.shower.ShowerGuaGua
import com.hfut.schedule.ui.util.NavigateAnimationManager


class ShowerActivity : BaseActivity() {
    @Composable
    override fun UI() {
        val navController = rememberNavController()
        val first = if(intent.getStringExtra("FIRST") == ShowerScreen.HOME.name) ShowerScreen.HOME.name else ShowerScreen.LOGIN.name
        NavHost(
            navController = navController,
            startDestination = first,
            enterTransition = {
                NavigateAnimationManager.fadeAnimation.enter
            },
            exitTransition = {
                NavigateAnimationManager.fadeAnimation.exit
            }
        ) {
            // 主UI
            composable(ShowerScreen.HOME.name) {
                ShowerGuaGua(super.showerVm,super.networkVm,navController)
            }
            // 游客模式
            composable(ShowerScreen.LOGIN.name) {
                ShowerLogin(super.showerVm,super.networkVm,navController)
            }
        }
    }
    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onRequestPermissionsResult(requestCode, permissions, grantResults)",
        "com.hfut.schedule.activity.BaseActivity"
    )
    )
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array< String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when(requestCode) {
//            1 -> {
//                if(grantResults.isNotEmpty() && grantResults[0]  == PackageManager.PERMISSION_GRANTED) {
//
//                } else Toast.makeText(this,"拒绝权限后不可扫码", Toast.LENGTH_SHORT).show()
//            }
//        }
    }
}
