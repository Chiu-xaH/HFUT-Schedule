package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.MyApplication
import com.hfut.schedule.logic.AESEncrypt
import com.hfut.schedule.R
import com.hfut.schedule.ui.ViewModel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

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
       // val savePskCheckBox : CheckBox = findViewById(R.id.SavePskCheckBox)
        val loading : ProgressBar = findViewById(R.id.Loading)
        val aboutButton : Button = findViewById(R.id.AboutButton)
        val backButton : Button = findViewById(R.id.BackButton)
        val tv : TextView = findViewById(R.id.tv)
        val noIntButton : Button = findViewById(R.id.NoIntButton)

        val job = Job()
        val scope = CoroutineScope(job)


        checkDate("2023-09-01","2024-02-01") // 定义一个函数，超出日期不允许使用

        Toast.makeText(this,"本应用为预览版，不代表最终正式版本！",Toast.LENGTH_SHORT).show()


        val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val username = prefs.getString("Username", "")
        val password = prefs.getString("Password","")

        //填充密码账号
      //  if (prefs.getString("状态","") == "0") {
        //    savePskCheckBox.isChecked
             accountET.setText(username)
             passwordET.setText(password)
      //  }else {
     //       accountET.setText(null)
     //       passwordET.setText(null)
     //   }



        scope.apply {
            launch { vm.getCookie() }
            launch {  vm.getKey() }
        }//协程并行执行，提高效率



        showPskCheckBox.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked)  passwordET.transformationMethod = HideReturnsTransformationMethod.getInstance()
                      else  passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
        }//显示密码开关

         //保存密码
       // savePskCheckBox.setOnCheckedChangeListener { _, isChecked ->
          //  val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
          //  if (isChecked) { sp.edit().putString("状态","0").apply() }
         //   else { sp.edit().putString("状态","1").apply() }
      //  }


        noIntButton.setOnClickListener {
            Toast.makeText(this,"读取上一次登录保存的数据",Toast.LENGTH_SHORT).show()
            val it = Intent(MyApplication.context,DatumActivity::class.java)
            startActivity(it)
        }

        aboutButton.setOnClickListener {
             AlertDialog.Builder(this).apply {
                setTitle("关于本应用")
                setMessage("本应用开发原因是课表会发生变动,本地无法满足实时性\n本应用既可获取教务的课表,也会获取之后会自动保存本地,使用离线课表\n我的能力有限,如果你有更好的建议或有问题,可反馈联系我(•ิ_•ิ)")
                setPositiveButton("好") { dialog, which ->
                    //操作
                }
                 show()
            }

        }

        backButton.setOnClickListener {
            Toast.makeText(this,"电子邮件:zsh0908@outlook.com",Toast.LENGTH_SHORT).show()
            val it = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:zsh0908@outlook.com"))
            startActivity(it)
        }

        loginButton.setOnClickListener {
            loading.visibility = View.VISIBLE
           //

            Thread {
                Thread.sleep(2000)
                runOnUiThread { loading.visibility = View.INVISIBLE }
            }.start()

            val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val key = prefs.getString("cookie", "")

                val inputAES = passwordET.editableText.toString()
                val username = accountET.editableText.toString()
                val outputAES = key?.let { it1 -> AESEncrypt.encrypt(inputAES, it1) }

            val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
            if(sp.getString("Username","") != username){ sp.edit().putString("Username", username).apply() }
            if(sp.getString("Password","") != inputAES){ sp.edit().putString("Password", inputAES).apply() }

                outputAES?.let { it1 -> vm.login(username, it1,"LOGIN_FLAVORING=" + key) }




            CoroutineScope(Job()).launch {

                delay(1000)

             //   if (vm.code.value == null)

                if (vm.code.value.toString() == "XXX" || vm.code.value == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(MyApplication.context, "连接Host失败,请检查网络配置", Toast.LENGTH_SHORT).show()
                        tv.setText("可能网络未连接或使用代理\n也可能为DNS问题,请更换网络")
                    }

                }
                if (vm.code.value.toString() == "401")
                    withContext(Dispatchers.Main) { Toast.makeText(MyApplication.context, "密码错误", Toast.LENGTH_SHORT).show() }


                if (vm.code.value.toString() == "200")
                    withContext(Dispatchers.Main) { Toast.makeText(MyApplication.context, "请输入正确的账号", Toast.LENGTH_SHORT) .show()}

                if (vm.code.value.toString() == "302") {

                    if (vm.location.value.toString() == MyApplication.RedirectURL) {
                        Toast.makeText(MyApplication.context, "重定向失败，请重新进入App登录", Toast.LENGTH_SHORT).show()
                        tv.setText("登录按钮已不可用，请从新进入App")
                        loginButton.isClickable = false
                    }

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

    fun checkDate(startDate: String, endDate: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

        if (currentDate < startDate || currentDate > endDate) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("提示")
                .setMessage("请保证日期在2023-2024第一学期内，否则应用已过期，请更新")
                .setPositiveButton("获取更新") { dialog, which ->
                    //跳转至浏览器打开URL
                    finish() }
                .setCancelable(false)
            builder.show()
        }
    }
}





