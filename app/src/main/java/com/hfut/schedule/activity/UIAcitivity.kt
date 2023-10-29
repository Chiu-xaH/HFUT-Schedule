package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.R
import com.hfut.schedule.logic.network.ServiceCreator.JxglstuServiceCreator
import com.hfut.schedule.ui.ViewModel.JxglstuViewModel
import com.hfut.schedule.ui.ViewModel.LoginViewModel
import java.net.URL

class UIAcitivity : ComponentActivity() {
    private val vm by lazy { ViewModelProvider(this).get(JxglstuViewModel::class.java) }
    //private val vm2 by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui)
        val DatumButton : Button = findViewById(R.id.DatumButton)

       val ticket =  "?ticket=" + intent.getStringExtra("ticket")
        ticket?.let { Log.d("传递过来", it) }

        DatumButton.setOnClickListener {
           ///var ticket = vm2.location.value.toString()
            Log.d("y",ticket)
           // vm.jxglstu(ticket!!)
        }


    }
}

