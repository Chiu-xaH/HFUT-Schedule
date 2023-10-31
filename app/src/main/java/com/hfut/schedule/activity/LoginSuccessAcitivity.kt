package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.R
import com.hfut.schedule.ui.viewmodel.JxglstuViewModel

class LoginSuccessAcitivity : ComponentActivity() {
    private val vm by lazy { ViewModelProvider(this).get(JxglstuViewModel::class.java) }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_success)
        val loginJxglstuButton : Button = findViewById(R.id.LoginJxglstuButton)
        val datumButton : Button = findViewById(R.id.DatumButton)
        val courseButton : Button =findViewById(R.id.CourseButton)


        loginJxglstuButton.setOnClickListener {
            val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val cookie = prefs.getString("redirect", "")
            cookie?.let { it1 -> Log.d("传送", it1) }
            vm.Jxglstulogin(cookie!!)
        }

        datumButton.setOnClickListener {
            val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val cookie = prefs.getString("redirect", "")
            //vm.getDatum(cookie!!)
            vm.getStudentId(cookie!!)

            val grade = intent.getStringExtra("Grade")

            ////////////////////////////////////////////////////////////////////////////
            AlertDialog.Builder(this).apply {
                setMessage("点击获取第一个JSON")
                setTitle("测试开发用")
                setPositiveButton("好") { dialog, which ->
                    Thread.sleep(1000)
                    vm.getLessonIds(cookie,grade!!)
                }
                show()
            }
            ////////////////////////////////////////////////////////////////////////////
        }

        courseButton.setOnClickListener {
            val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val cookie = prefs.getString("redirect", "")
            vm.getDatum(cookie!!)
            val it = Intent(this,DatumActivity::class.java)
            startActivity(it)
        }
    }
}




