package com.hfut.schedule.logic.util.network

import android.util.Base64
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.network.util.GenerateQWeather
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec

object Crypto {
    @JvmStatic
    fun uuid32(): String {
        val chars = "0123456789abcdefghijklmnopqrstuvwxyz"
        return (1..32)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }
    @JvmStatic
    fun encryptAES(input:String, password:String): String {
        return try {
            val cipher = Cipher.getInstance("AES")
            val keySpec = SecretKeySpec(password.toByteArray(),"AES")
            cipher.init(Cipher.ENCRYPT_MODE,keySpec)
            val encrypt = cipher.doFinal(input.toByteArray())
            Base64.encodeToString(encrypt,Base64.NO_WRAP)
        } catch (e:Exception) {
            "NULL"
        }
    }
    @JvmStatic
    fun getHuiXinAuth() = "Basic " + encodeToBase64("mobile_service_platform:mobile_service_platform_secret")
    @JvmStatic
    fun encodeToBase64(input: String): String =java.util.Base64.getEncoder().encodeToString(input.toByteArray(Charsets.UTF_8))

    fun rsaEncrypt(plainText: String, publicKeyBase64: String = MyApplication.UNI_APP_LOGIN_RSA_PUBLIC_KEY): String {
        // 解析公钥
        val keyBytes = Base64.decode(publicKeyBase64, Base64.DEFAULT)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(keySpec)

        // 创建 Cipher
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        // 加密
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())

        // 返回 Base64
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }

    @JvmStatic
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

    //校务行用的，就是微信小程序逆向JS
    @JvmStatic
    fun encryptXiaoWuXing(plainText: String): String {
        val key = "JL$<&*l9~67?:#5p"
        val iv = "{g;,9~l4'/sw`885"

        val keyBytes = key.toByteArray(Charsets.UTF_8)
        val ivBytes = iv.toByteArray(Charsets.UTF_8)

        val keySpec = SecretKeySpec(keyBytes, "AES")
        val ivSpec = IvParameterSpec(ivBytes)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        return java.util.Base64.getEncoder().encodeToString(encryptedBytes)
    }
    @JvmStatic
    fun decryptXiaoWuXing(cipherText: String): String {
        val key = "JL$<&*l9~67?:#5p"
        val iv = "{g;,9~l4'/sw`885"

        val keyBytes = key.toByteArray(Charsets.UTF_8)
        val ivBytes = iv.toByteArray(Charsets.UTF_8)

        val keySpec = SecretKeySpec(keyBytes, "AES")
        val ivSpec = IvParameterSpec(ivBytes)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

        val decodedBytes = java.util.Base64.getDecoder().decode(cipherText)
        val decryptedBytes = cipher.doFinal(decodedBytes)

        return String(decryptedBytes, Charsets.UTF_8)
    }

    @JvmStatic
    fun encryptAesECB(plainText: String, key: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    @JvmStatic
    fun generateRandomHexString(): String {
        var result = ""
        repeat(16) {
            val randomHex = (0..15).random().toString(16) // 生成随机16进制数
            result += randomHex
        }
        return result.uppercase() // 转换为大写
    }
    @JvmStatic
    fun encryptTimestamp(): String {
        // 1. 密钥
        val keyStr = "b1o2s3s4s5o6f7t8"
        val keyBytes = keyStr.toByteArray(Charsets.UTF_8)
        val secretKey = SecretKeySpec(keyBytes, "AES")

        // 2. 时间戳
        val timestamp = System.currentTimeMillis().toString()
        val dataBytes = timestamp.toByteArray(Charsets.UTF_8)

        // 3. AES/ECB/PKCS5Padding 加密（Java 里 PKCS5Padding == CryptoJS 的 PKCS7）
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(dataBytes)

        // 4. Base64 输出
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }

    //用于生成和风天气密钥
    @JvmStatic
    fun getQWeatherAuth() : String {
        return "Bearer " + GenerateQWeather()
            .generate(
            "MC4CAQAwBQYDK2VwBCIEILpLcCmyt8JbbBaEMiBvA9ys3RLb2v63rWhuFC83KDGZ",
            "4HTJ5N7F37",
            "THB3UBK56Q"
        )
    }
    @JvmStatic
    fun getSupabasePublicKey() : String =
        encodeToBase64("{\"alg\":\"HS256\",\"typ\":\"JWT\"}").replace("=","") +
                "." +encodeToBase64("{\"iss\":\"supabase\",\"ref\":\"uadgxvstybecnhqemxvj\",\"role\":\"anon\",\"iat\":1744637621,\"exp\":2060213621}").replace("=","") + "." +
                decryptXiaoWuXing("KyeSUi9QTi1x6PYvq5W/kvSs6LWMdvq1/7cGFYlElE7ewQ7JMV1PjTZw+nfShdGb")
}

fun main() {
    println(Crypto.uuid32())
}