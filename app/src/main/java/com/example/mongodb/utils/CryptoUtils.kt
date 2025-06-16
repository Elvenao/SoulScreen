package com.example.mongodb.utils


import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {
    private val key = "8da949392%1!5423"
    fun decryptAES(base64Encrypted: String): String {
        val fullData = Base64.getDecoder().decode(base64Encrypted)
        val iv = fullData.sliceArray(0 until 16)
        val encryptedData = fullData.sliceArray(16 until fullData.size)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

        val decryptedBytes = cipher.doFinal(encryptedData)
        return String(decryptedBytes, Charsets.UTF_8)
    }
    
}

