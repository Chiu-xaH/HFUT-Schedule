package com.hfut.schedule.activity.shower

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.main.BaseActivity
import com.hfut.schedule.ui.activity.shower.main.ShowerGuaGua
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowerActivity : BaseActivity() {
    @Composable
    override fun UI() {
        ShowerGuaGua(super.showerVm,super.networkVm)
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array< String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            1 -> {
                if(grantResults.isNotEmpty() && grantResults[0]  == PackageManager.PERMISSION_GRANTED) {

                } else Toast.makeText(this,"拒绝权限后不可扫码", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
