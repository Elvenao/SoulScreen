package com.example.mongodb

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.mongodb.model.Usuario
import com.example.mongodb.model.Post
import com.example.mongodb.ui.theme.MongoDBTheme

import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mongodb.screens.seePost
import com.example.mongodb.screens.logIn
import com.example.mongodb.screens.signUp
import com.example.mongodb.screens.welcomeScreen
import kotlin.math.sign

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.security.crypto.EncryptedSharedPreferences
import org.json.JSONObject

import com.example.mongodb.model.RefreshTokenRequest
import com.example.mongodb.screens.Home

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationHost()
        }
    }
}

fun isTokenValid(token: String): Boolean {
    try {
        val parts = token.split(".")
        if (parts.size != 3) return false

        val payloadJson = String(Base64.decode(parts[1], Base64.DEFAULT))
        val jsonObject = JSONObject(payloadJson)
        val exp = jsonObject.getLong("exp") // tiempo de expiración en segundos

        val currentTime = System.currentTimeMillis() / 1000 // en segundos
        return exp > currentTime
    } catch (e: Exception) {
        return false
    }
}

@Composable
fun NavigationHost(){
    var initialDestination = "welcomeScreen"
    val context = LocalContext.current
    val encryptedSharedPreferences = remember{SecurePrefs(context)}
    val accessToken = encryptedSharedPreferences.getAccessToken()
    val refreshToken = encryptedSharedPreferences.getRefreshToken()
    if (!accessToken.isNullOrEmpty() && isTokenValid(accessToken)) {
        initialDestination = "Posts"
    } else if (!refreshToken.isNullOrEmpty()) {
        RetrofitClient.getInstance(context).refreshToken(RefreshTokenRequest(refreshToken))
    } else {
        initialDestination = "welcomeScreen"
    }
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = initialDestination) {
        composable("UIPrincipal") {
            Home(navController)
        }
        composable("seePost/{user}/{title}/{content}/{date}/{time}") { backStackEntry ->
            val user = backStackEntry.arguments?.getString("user") ?: " "
            val title = backStackEntry.arguments?.getString("title") ?: " "
            val content = backStackEntry.arguments?.getString("content") ?: " "
            val date = backStackEntry.arguments?.getString("date") ?: " "
            val time = backStackEntry.arguments?.getString("time") ?: " "
            seePost(user, title, content, date, time)
        }

        composable("logIn") {
            logIn(navController)
        }
        composable("signUp") {
            signUp(navController)
        }
        composable("welcomeScreen") {
            welcomeScreen(navController)
        }

        composable("seePost") {

        }
        composable("Posts") {
            Home(navController)
        }
    }
}







