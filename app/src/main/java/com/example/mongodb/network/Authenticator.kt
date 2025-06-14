package com.example.mongodb.network

import android.content.Context
import com.example.mongodb.SecurePrefs
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val context: Context,
    private val api: ApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val securePrefs = SecurePrefs(context)
        val refreshToken = securePrefs.getRefreshToken() ?: return null

        val tokenResponse = api.refreshToken(refreshToken).execute()
        if (tokenResponse.isSuccessful) {
            val newAccessToken = tokenResponse.body()?.accessToken ?: return null

            securePrefs.saveAccessToken(newAccessToken)

            return response.request().newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }

        return null
    }
}