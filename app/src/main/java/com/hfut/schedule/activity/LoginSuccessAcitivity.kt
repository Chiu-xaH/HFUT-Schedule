package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ui.ViewModel.JxglstuViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginSuccessAcitivity : ComponentActivity() {

    private val vm by lazy { ViewModelProvider(this).get(JxglstuViewModel::class.java) }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_success)
        val datumButton : Button = findViewById(R.id.DatumButton)
        val progressBar: ProgressBar = findViewById(R.id.prgressbar)
        val selfButton : Button = findViewById(R.id.SelfButton)
        val examButton : Button = findViewById(R.id.ExamButton)

            val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val cookie = prefs.getString("redirect", "")
            //cookie?.let { it1 -> Log.d("传送", it1) }
            vm.Jxglstulogin(cookie!!)
        //课程表
        datumButton.setOnClickListener {

            progressBar.visibility = View.VISIBLE
           // datumButton.isClickable = false

            Toast.makeText(this,"正在请求网络数据，请等待",Toast.LENGTH_SHORT).show()

            Thread {
                Thread.sleep(4500)
                runOnUiThread { progressBar.visibility = View.GONE }
            }.start()

            val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val cookie = prefs.getString("redirect", "")
            vm.getStudentId(cookie!!)
            val grade = intent.getStringExtra("Grade")


            val job = Job()
            val scope = CoroutineScope(job)
            scope.apply {
                launch {
                    delay(1000)
                    vm.getLessonIds(cookie,grade!!)
                }
                launch {
                    delay(2500)
                    vm.getDatum(cookie!!)
                }
                launch {
                    delay(3500)
                    val it = Intent(MyApplication.context,DatumActivity::class.java)
                    startActivity(it)
                }
            }
        }
        //个人主页
        selfButton.setOnClickListener {
            val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val cookie = prefs.getString("redirect", "")
            vm.Self(cookie!!)
            Toast.makeText(this,"获取成功，但是我还没写界面，憋着",Toast.LENGTH_SHORT).show()
        }
        //考试安排
        examButton.setOnClickListener {
            Toast.makeText(this,"等啥时候有考试再开发",Toast.LENGTH_SHORT).show()
        }


    }
}




