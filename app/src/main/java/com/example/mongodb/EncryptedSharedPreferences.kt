package com.example.mongodb

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences

import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import androidx.core.content.edit
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import com.example.mongodb.model.CurrentUserData

class SecurePrefs(context: Context) {
    private val secretKey = Keys.hmacShaKeyFor("8da949392%1!5423_381j39ja2$6asdfas12".toByteArray())
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val prefs = EncryptedSharedPreferences.create(
        "secure_prefs",                   // Nombre del archivo
        masterKeyAlias,                   // Alias de la clave maestra
        context,                          // Contexto
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
        val name = claims["name"] as String
        val birthDate = claims["birthDate"] as String
        val avatar =  claims["avatar"] as String
        val userData = CurrentUserData(id,userName,name,birthDate,avatar)
        return userData
    }
}