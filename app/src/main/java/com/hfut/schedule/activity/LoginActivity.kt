package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.MyApplication
import com.hfut.schedule.logic.AESEncrypt
import com.hfut.schedule.R
import com.hfut.schedule.ui.ViewModel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : ComponentActivity() {
    private val vm by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        //透明状态栏

        setContentView(R.layout.login)

       // if (Build.VERSION.SDK_INT > 9) {
         //   val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
           // StrictMode.setThreadPolicy(policy)
        //}

        val accountET: EditText = findViewById(R.id.AccountET)
        val passwordET: EditText = findViewById(R.id.PasswordET)
        val loginButton: Button = findViewById(R.id.LoginButton)
        val showPskCheckBox : CheckBox = findViewById(R.id.ShowPskCheckBox)
        val savePskCheckBox : CheckBox = findViewById(R.id.SavePskCheckBox)
        val loading : ProgressBar = findViewById(R.id.Loading)

        val job = Job()
        val scope = CoroutineScope(job)

        scope.apply {
            launch { vm.getCookie() }
            launch {  vm.getKey() }
        }//协程并行执行，提高效率


        showPskCheckBox.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked)  passwordET.transformationMethod = HideReturnsTransformationMethod.getInstance()
                      else  passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
        }//显示密码开关

        savePskCheckBox.setOnCheckedChangeListener { _, isChecked -> Toast.makeText(this,"待开发，敬请期待",Toast.LENGTH_SHORT).show() }

        loginButton.setOnClickListener {
            loading.visibility = View.VISIBLE

            Thread {
                Thread.sleep(1000)
                runOnUiThread { loading.visibility = View.GONE }
            }.start()

            val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val key = prefs.getString("cookie", "")

                val inputAES = passwordET.editableText.toString()
                val username = accountET.editableText.toString()
                val outputAES = key?.let { it1 -> AESEncrypt.encrypt(inputAES, it1) }

                outputAES?.let { it1 -> vm.login(username, it1,"LOGIN_FLAVORING=" + key) }

            val job = Job()
            CoroutineScope(job).launch {

                delay(1000)

                if (vm.code.value.toString() == null)
                    withContext(Dispatchers.Main) { Toast.makeText(MyApplication.context, "请检查是否点击了登录或输入账密", Toast.LENGTH_SHORT).show() }

                if (vm.code.value.toString() == "XXX")
                    withContext(Dispatchers.Main) { Toast.makeText(MyApplication.context, "网络连接失败", Toast.LENGTH_SHORT).show() }

                if (vm.code.value.toString() == "401")
                    withContext(Dispatchers.Main) { Toast.makeText(MyApplication.context, "密码错误", Toast.LENGTH_SHORT).show() }

                if (vm.code.value.toString() == "200")
                    withContext(Dispatchers.Main) { Toast.makeText(MyApplication.context, "请输入正确的账号", Toast.LENGTH_SHORT) .show()}

                if (vm.code.value.toString() == "302") {

                    if (vm.location.value.toString() == MyApplication.RedirectURL)
                        withContext(Dispatchers.Main) { Toast.makeText(MyApplication.context, "重定向失败，请重新进入App登录", Toast.LENGTH_SHORT).show() }

                    else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(MyApplication.context, "登陆成功", Toast.LENGTH_SHORT).show()
                            val it = Intent(MyApplication.context, LoginSuccessAcitivity::class.java).apply { putExtra("Grade", username.substring(2, 4)) }
                            startActivity(it)
                        }
                    }
                }
            }



        }

        }
    }




