package com.hfut.schedule

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AESEncrypt {

    fun encrypt(input:String,password:String): String{

        val cipher = Cipher.getInstance("AES")
        val keySpec:SecretKeySpec? = SecretKeySpec(password.toByteArray(),"AES")
        cipher.init(Cipher.ENCRYPT_MODE,keySpec)
        val encrypt = cipher.doFinal(input.toByteArray())

        return Base64.encodeToString(encrypt,Base64.NO_WRAP)
    }

}
