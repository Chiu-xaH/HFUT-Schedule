package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.logic.AESEncrypt
import com.hfut.schedule.R
import com.hfut.schedule.logic.network.ServiceCreator.JxglstuServiceCreator
import com.hfut.schedule.ui.ViewModel.LoginViewModel

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

        //if (Build.VERSION.SDK_INT > 9) {
           // val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
          //  StrictMode.setThreadPolicy(policy)
      //  }

        val accountET: EditText = findViewById(R.id.AccountET)
        val passwordET: EditText = findViewById(R.id.PasswordET)
        val loginButton: Button = findViewById(R.id.LoginButton)
        val checkButton : Button =findViewById(R.id.CheckButton)
        val showPskCheckBox : CheckBox = findViewById(R.id.ShowPskCheckBox)
        val savePskCheckBox : CheckBox = findViewById(R.id.SavePskCheckBox)

        Toast.makeText(this,"请稍等，正在获取登录所需信息",Toast.LENGTH_SHORT).show()


        vm.getCookie()
        //得到AESKey
        vm.getKey()
        Thread.sleep(3000)
       val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val key = prefs.getString("cookie", "")

        showPskCheckBox.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked)  passwordET.transformationMethod = HideReturnsTransformationMethod.getInstance()
                      else  passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
        }//显示密码开关

        savePskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) Toast.makeText(this,"待开发，敬请期待",Toast.LENGTH_SHORT).show()
            else Toast.makeText(this,"待开发，敬请期待",Toast.LENGTH_SHORT).show()
        }

        loginButton.setOnClickListener {
                val inputAES = passwordET.editableText.toString()
                val username = accountET.editableText.toString()
                val outputAES = key?.let { it1 -> AESEncrypt.encrypt(inputAES, it1) }

                outputAES?.let { it1 -> vm.login(username, it1,"LOGIN_FLAVORING=" + key) }

        }

        checkButton.setOnClickListener {
            Log.d("检查",vm.location.value.toString())
            Log.d("检查20",vm.code.value.toString())
            if (vm.code.value.toString() == null )
                Toast.makeText(this,"请检查是否点击了登录或输入账密",Toast.LENGTH_SHORT).show()
            if (vm.code.value.toString() =="XXX")
                Toast.makeText(this,"网络连接失败",Toast.LENGTH_SHORT).show()
            if (vm.code.value.toString() == "401")
                Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show()
            if (vm.code.value.toString() == "200")
                Toast.makeText(this,"请输入正确的账号",Toast.LENGTH_SHORT).show()
            if (vm.code.value.toString() == "302" ) {
                if (vm.location.value.toString() == "https://cas.hfut.edu.cn/cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin&exception.message=A+problem+occurred+restoring+the+flow+execution+with+key+%27e1s1%27")
                    Toast.makeText(this,"重定向失败，请重新进入App登录",Toast.LENGTH_SHORT).show()
                else {
                    Toast.makeText(this,"登陆成功",Toast.LENGTH_SHORT).show()
                    val it = Intent(this,UIAcitivity::class.java)
                    startActivity(it)
                }

            }
        }

        }



        }




