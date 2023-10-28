package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.hfut.schedule.R

class UIAcitivity : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui)
        val DatuButton : Button = findViewById(R.id.DatumButton)
        DatuButton.setOnClickListener {
            //待开发//待开发//待开发
        }
       //待开发//待开发//待开发
    }
}

