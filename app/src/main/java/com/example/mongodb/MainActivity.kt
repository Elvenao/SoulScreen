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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mongodb.screens.seePost
import com.example.mongodb.screens.logIn
import com.example.mongodb.screens.signUp_Name
import com.example.mongodb.screens.welcomeScreen
import com.example.mongodb.screens.signUp_Email
import kotlin.math.sign

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.security.crypto.EncryptedSharedPreferences
import org.json.JSONObject

import com.example.mongodb.model.RefreshTokenRequest
import com.example.mongodb.screens.Home
import com.example.mongodb.screens.categoryChoosing
import com.example.mongodb.screens.crearPost
import com.example.mongodb.screens.signUp_BirthDate
import com.example.mongodb.screens.profileScreen
import com.example.mongodb.screens.signUp_Name
import com.example.mongodb.screens.signUp_UserName

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
            signUp_Name(navController)
        }
        composable("welcomeScreen") {
            welcomeScreen(navController)
        }

        composable("seePost") {

        }
        composable("signUp_BirthDate/{nombre}/{apellidos}"){ backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre")?: " "
            val apellidos = backStackEntry.arguments?.getString("apellidos")?: " "
            signUp_BirthDate(navController, nombre, apellidos)
        }
        composable("Posts") {
            Home(navController)
        }
        composable("signUp_Email/{nombre}/{apellidos}/{birthDate}/{userName}"){ backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre")?: " "
            val apellidos = backStackEntry.arguments?.getString("apellidos")?: " "
            val birthDate = backStackEntry.arguments?.getString("birthDate")?: " "
            val userName = backStackEntry.arguments?.getString("userName")?: " "
            signUp_Email(navController,nombre,apellidos,birthDate, userName)
        }
        composable("signUp_UserName/{nombre}/{apellidos}/{birthDate}"){backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre")?: " "
            val apellidos = backStackEntry.arguments?.getString("apellidos")?: " "
            val birthDate = backStackEntry.arguments?.getString("birthDate")?: " "
            signUp_UserName(navController,nombre,apellidos,birthDate)
        }

        composable("profileScreen") {
            profileScreen(navController)
        }

        composable("crearPostScreen") {
            crearPost(navController)
        }

        composable("categoryChoosing"){
            categoryChoosing(navController)
        }

    }
}



@Composable
fun BottomBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFF8E24AA) // Morado
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = selectedIndex == 0,
            onClick = { onItemSelected(0) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Amigos") },
            label = { Text("Amigos") },
            selected = selectedIndex == 1,
            onClick = { /*onItemSelected(1)*/ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Explorar") },
            label = { Text("Explorar") },
            selected = selectedIndex == 2,
            onClick = { /*onItemSelected(2)*/ }
        )
    }
}






