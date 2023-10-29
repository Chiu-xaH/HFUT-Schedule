package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.R
import com.hfut.schedule.ui.ViewModel.JxglstuViewModel

class UIAcitivity : ComponentActivity() {
    private val vm by lazy { ViewModelProvider(this).get(JxglstuViewModel::class.java) }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui)
        val datumButton : Button = findViewById(R.id.DatumButton)
        val textButton : Button = findViewById(R.id.TextButton)
        val getButton : Button = findViewById(R.id.GetButton)
        val get2Button : Button = findViewById(R.id.Get2Button)

        datumButton.setOnClickListener {
            val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val cookie = prefs.getString("redirect", "")
            cookie?.let { it1 -> Log.d("传送", it1) }
            //
            vm.jxglstulogin(cookie!!)
        }
       textButton.setOnClickListener {
           val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
           val cookie = prefs.getString("redirect", "")
           vm.getDatum(cookie!!)
       }
        getButton.setOnClickListener {
            val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val cookie = prefs.getString("redirect", "")
            vm.getDatum2(cookie!!)
        }
         get2Button.setOnClickListener {
              val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
             val cookie = prefs.getString("redirect", "")
              vm.getCourse(cookie!!)
}

    }
}

