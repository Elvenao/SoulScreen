package com.example.mongodb

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.mongodb.model.CurrentUserData
import com.example.mongodb.utils.CryptoUtils
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys

class SecurePrefs(context: Context) {
    private val secretKey = Keys.hmacShaKeyFor("8da949392%1!5423_381j39ja2$6asdfas12".toByteArray())
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAccessToken(token: String) {
        prefs.edit() { putString("access_token", token) }
    }

    fun getAccessToken(): String? {
        return prefs.getString("access_token", null)
    }

    fun clearAccessToken() {
        prefs.edit() { remove("access_token") }
    }

    fun saveRefreshToken(token: String) {
        prefs.edit() { putString("access_refreshtoken", token) }
    }

    fun getRefreshToken(): String? {
        return prefs.getString("access_refreshtoken", null)
    }

    fun clearRefreshToken() {
        prefs.edit() { remove("access_refreshtoken") }
    }

    fun getCurrentUserData():CurrentUserData{
        val refreshToken = getRefreshToken()
        val claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(refreshToken)
            .body
        val id = claims["id"] as String
        val userName = claims["userName"] as String
        var name = claims["name"] as String
        name = CryptoUtils.decryptAES(name)
        var birthDate = claims["birthDate"] as String
        birthDate = CryptoUtils.decryptAES(birthDate)
        val genres = (claims["genres"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        val biography = claims["biography"] as? String ?: ""
        var avatar =  claims["avatar"] as? String ?: ""
        val ip = claims["ip"] as? String ?: ""
        val joiningDate = claims["joiningDate"] as? String?: ""
        avatar = "http://$ip$avatar"
        val following = (claims["following"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        val followers = (claims["followers"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        val userData = CurrentUserData(id,userName,name,birthDate, biography,genres,avatar,ip,joiningDate,following,followers)
        return userData
    }



}