package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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


        vm.getCookie()
        //得到AESKey
        vm.getKey()
      //  Thread.sleep(3000)


        showPskCheckBox.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked)  passwordET.transformationMethod = HideReturnsTransformationMethod.getInstance()
                      else  passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
        }//显示密码开关

        savePskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) Toast.makeText(this,"待开发，敬请期待",Toast.LENGTH_SHORT).show()
            else Toast.makeText(this,"待开发，敬请期待",Toast.LENGTH_SHORT).show()
        }

        loginButton.setOnClickListener {
            loading.visibility == View.VISIBLE

            val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val key = prefs.getString("cookie", "")

                val inputAES = passwordET.editableText.toString()
                val username = accountET.editableText.toString()
                val outputAES = key?.let { it1 -> AESEncrypt.encrypt(inputAES, it1) }

                outputAES?.let { it1 -> vm.login(username, it1,"LOGIN_FLAVORING=" + key) }

              AlertDialog.Builder(this).apply {
                setMessage("提交成功，点击验证")
                setTitle("提示")
                setPositiveButton("验证") { dialog, which ->
                    //Log.d("检查",vm.location.value.toString())
                    //Log.d("检查20",vm.code.value.toString())
                    Thread.sleep(1000)
                    if (vm.code.value.toString() == null )
                        Toast.makeText(MyApplication.context,"请检查是否点击了登录或输入账密",Toast.LENGTH_SHORT).show()
                    if (vm.code.value.toString() =="XXX")
                        Toast.makeText(MyApplication.context,"网络连接失败",Toast.LENGTH_SHORT).show()
                    if (vm.code.value.toString() == "401")
                        Toast.makeText(MyApplication.context,"密码错误",Toast.LENGTH_SHORT).show()
                    if (vm.code.value.toString() == "200")
                        Toast.makeText(MyApplication.context,"请输入正确的账号",Toast.LENGTH_SHORT).show()
                    if (vm.code.value.toString() == "302" ) {
                        if (vm.location.value.toString() == "https://cas.hfut.edu.cn/cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin&exception.message=A+problem+occurred+restoring+the+flow+execution+with+key+%27e1s1%27")
                            Toast.makeText(MyApplication.context,"重定向失败，请重新进入App登录",Toast.LENGTH_SHORT).show()
                        else {
                            Toast.makeText(MyApplication.context,"登陆成功",Toast.LENGTH_SHORT).show()
                            val it = Intent(MyApplication.context,LoginSuccessAcitivity::class.java).apply {
                                putExtra("Grade",username.substring(2,4))
                            }
                            startActivity(it)
                        }

                    }
                }
                show()
            }

        }

        }
    }




