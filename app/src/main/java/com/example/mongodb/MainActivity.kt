package com.example.mongodb

import android.os.Bundle
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


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationHost()
        }
    }
}
/*
@kotlin.OptIn(ExperimentalMaterialApi::class)
@Composable
fun UIPrincipal(usuarios: List<Usuario>, response: Boolean){

    val usuarios = remember { mutableStateOf<List<Usuario>>(emptyList()) }

    val errorMessage = remember { mutableStateOf<String?>(null) }

    var refreshing by remember { mutableStateOf(false) }

    suspend fun recargarDatos() {
        refreshing = true
        delay(2000) // Simulación de carga
        refreshing = false
    }

    val pullRefreshState = rememberPullRefreshState(refreshing, {
        // Acción al hacer pull
       CoroutineScope(Dispatchers.IO).launch{
           recargarDatos()
       }
    })

    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)){
        if(!response) {
            LazyColumn {
                items(usuarios.size) { index ->
                    val usuario = usuarios[index]
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE0F7FA))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row {
                                Text(
                                    text = "Nombre: ${usuario.nombre}", // Asumiendo que tienes un campo `nombre`
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Row {
                                Text(
                                    text = "Edad: ${usuario.edad}", // Asumiendo que tienes un campo `nombre`
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }


                    }

                }
            }
        }else{
            Column(Modifier.align(Alignment.Center)) {
                Text("Fallo en la conexion")
            }
        }

        PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}
*/

@Composable
fun NavigationHost(){
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "UIPrincipal") {
            composable("UIPrincipal") {
                UIPrincipal()
            }




            composable("seePost/{user}/{title}/{content}/{date}/{time}"){
                backStackEntry ->
                    val user = backStackEntry.arguments?.getString("user")?: " "
                    val title = backStackEntry.arguments?.getString("title")?: " "
                    val content = backStackEntry.arguments?.getString("content")?: " "
                    val date = backStackEntry.arguments?.getString("date")?: " "
                    val time = backStackEntry.arguments?.getString("time")?: " "
                seePost(user,title,content,date,time)

            }
        }

}

@kotlin.OptIn(ExperimentalMaterialApi::class)
@Composable
fun UIPrincipal() {
    val usuarios = remember { mutableStateOf<List<Usuario>>(emptyList()) }
    val posts = remember { mutableStateOf<List<Post>>(emptyList())}
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isRefreshing = remember { mutableStateOf(false) }

    fun cargarUsuarios() {
        isRefreshing.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getUsuarios().execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        usuarios.value = response.body() ?: emptyList()
                        errorMessage.value = null
                    } else {
                        errorMessage.value = "Error ${response.code()}"
                    }
                    isRefreshing.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage.value = "Fallo: ${e.message}"
                    isRefreshing.value = false
                }
            }
        }
    }

    fun cargarPosts() {
        isRefreshing.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getPosts().execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        posts.value = response.body() ?: emptyList()
                        errorMessage.value = null
                    } else {
                        errorMessage.value = "Error ${response.code()}"
                    }
                    isRefreshing.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage.value = "Fallo: ${e.message}"
                    isRefreshing.value = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {

        cargarPosts()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = { cargarPosts() }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {

        if (errorMessage.value != null) {
            // Mostrar error pero mantener el gesto de pull
            Box(modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)){
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

                ) {
                                Text(text = "Error en el servidor", fontSize = 18.sp)
                                Text(text = errorMessage.value ?: "", fontSize = 14.sp)
                }
            }

        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(posts.value) { post ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE0F7FA))
                            .padding(16.dp)
                            .clickable {

                            }
                    ) {
                        Column {
                            Text(
                                text = "${post.user}",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                text = "${post.title}",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                text = "${post.content}",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                text = "${post.date}",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                text = "${post.time}",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
        // Este indicador debe estar *fuera* del condicional para que funcione siempre
        PullRefreshIndicator(
            refreshing = isRefreshing.value,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

fun seePost(user : String, title: String,content: String,date:String,time:String){

}




