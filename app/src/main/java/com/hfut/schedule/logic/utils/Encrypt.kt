package com.hfut.schedule.logic.utils

import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
object Encrypt {
    fun AESencrypt(input:String, password:String): String{
        val cipher = Cipher.getInstance("AES")
        val keySpec:SecretKeySpec? = SecretKeySpec(password.toByteArray(),"AES")
        cipher.init(Cipher.ENCRYPT_MODE,keySpec)
        val encrypt = cipher.doFinal(input.toByteArray())
        return Base64.encodeToString(encrypt,Base64.NO_WRAP)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun encodeToBase64(input: String): String {
        return java.util.Base64.getEncoder().encodeToString(input.toByteArray(Charsets.UTF_8))
    }

}

