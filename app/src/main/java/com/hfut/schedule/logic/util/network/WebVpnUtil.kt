package com.hfut.schedule.logic.util.network

import com.hfut.schedule.App.MyApplication
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object WebVpnUtil {
    private val key = "wrdvpnisthebest!".toByteArray(Charsets.UTF_8)
    private val iv = "wrdvpnisthebest!".toByteArray(Charsets.UTF_8)

    private fun getCiphertext(plaintext: String): String {
        val cipher = Cipher.getInstance("AES/CFB/NoPadding")
        val secretKey = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return encrypted.joinToString("") { "%02x".format(it) }
    }

    private fun getPlaintext(ciphertext: String): String {
        val cipher = Cipher.getInstance("AES/CFB/NoPadding")
        val secretKey = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        val bytes = ciphertext.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val decrypted = cipher.doFinal(bytes)
        return String(decrypted, Charsets.UTF_8)
    }

    fun getWebVpnUrl(url: String): String = try {
        val parts = url.split("://")
        val protocol = parts[0]
        val addr = parts[1]

        val hostParts = addr.split("/")
        val hostPort = hostParts[0]
        val domain = hostPort.split(":")[0]
        val port = if (":" in hostPort) "-" + hostPort.split(":")[1] else ""
        val cph = getCiphertext(domain)
        val path = hostParts.drop(1).joinToString("/")

        val keyHex = iv.joinToString("") { "%02x".format(it) }

        MyApplication.WEBVPN_URL + "$protocol$port/$keyHex$cph/$path"
    } catch (e : Exception) {
        MyApplication.WEBVPN_URL
    }

    fun getOrdinaryUrl(url: String): String = try {
        val parts = url.split("/")
        val protocol = parts[3]
        val keyCph = parts[4]

        val cipherText = keyCph.substring(32) // 前32字符是iv的hex
        val hostname = getPlaintext(cipherText)
        val path = parts.drop(5).joinToString("/")

         "$protocol://$hostname/$path"
    } catch (e : Exception) {
        MyApplication.WEBVPN_URL
    }
}
