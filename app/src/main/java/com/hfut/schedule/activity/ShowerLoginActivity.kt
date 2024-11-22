package com.hfut.schedule.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hfut.schedule.ui.Activity.shower.login.ShowerLogin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowerLoginActivity : BaseActivity() {
    @Composable
    override fun UI() {
        ShowerLogin(super.showerVm)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),1)
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

                }else Toast.makeText(this,"拒绝权限后不可扫码", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

