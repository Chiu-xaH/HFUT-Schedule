package com.hfut.schedule

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hfut.schedule.ui.theme.肥工课程表Theme
import java.sql.RowId

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        val AccountET : EditText = findViewById(R.id.AccountET)
        val PasswordET : EditText = findViewById(R.id.PasswordET)
        val LoginButton : Button = findViewById(R.id.LoginButton)

        val account = AccountET.text.toString()
        val password = PasswordET.text.toString()



        LoginButton.setOnClickListener {
            //登录操作，POST内容
            if (account != null && password != null) {
                //
                val AESinput = PasswordET.editableText.toString()

            } else Toast.makeText(this,"密码或学号为空",Toast.LENGTH_SHORT).show()
        }
    }
}
