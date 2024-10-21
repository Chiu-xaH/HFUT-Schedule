package com.hfut.schedule.logic.utils

import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import java.security.MessageDigest
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

    fun md5Hash(input: String): String {
        // 获取MD5实例
        val md = MessageDigest.getInstance("MD5")

        // 将输入字符串转换为字节数组并进行MD5加密
        val digest = md.digest(input.toByteArray())

        // 将加密后的字节数组转换为16进制字符串
        val hexString = digest.joinToString("") { "%02x".format(it) }

        // 返回最后10位
        return if (hexString.length > 10) hexString.takeLast(10) else hexString
    }

}

