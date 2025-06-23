package com.example.mongodb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mongodb.model.Post

import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.NuevoComentarioRequest
import com.example.mongodb.model.PostWithAvatar
import kotlin.compareTo
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.lazy.items
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun seePost(id:String, navController: NavController){
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isRefreshing = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val post = remember { mutableStateOf<PostWithAvatar?>(null) }
    val EncryptedSharedPreferences = SecurePrefs(context)
    val currentUserData = EncryptedSharedPreferences.getCurrentUserData()
    var comments = remember { mutableStateOf("")}
    val scrollState = rememberScrollState()

    fun cargarPosts() {
        isRefreshing.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.getInstance(context).verPost(id).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        post.value = response.body()
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
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Crear nuevo post",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.height(80.dp)
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.background,
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    verticalArrangement = Arrangement.Top,
                ) {
                    item {
                        post.value?.let { post ->
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.tertiary)
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AsyncImage(
                                            model = "http://" + currentUserData.ip + post.userAvatar,
                                            contentDescription = "Imagen de usuario",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .padding(8.dp)
                                        )
                                        Text(
                                            text = post.post.user,
                                            fontSize = 24.sp,
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .weight(2f), // Da más espacio al usuario, pero deja sitio para la fecha
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                        Spacer(modifier = Modifier.weight(1f)) // Empuja la fecha a la derecha
                                        Text(
                                            text = post.post.date,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Tipo de post: " + post.post.postType,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Acerca de: " + post.post.mediaId,
                                            fontSize = 16.sp,
                                            modifier = Modifier
                                                .weight(1f),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )

                                        AsyncImage(
                                            model = "http://" + currentUserData.ip + post.post.mediaImg,
                                            contentDescription = "Imagen del medio",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .height(32.dp)
                                                .width(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }

                                    Text(
                                        text = post.post.title,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .padding(vertical = 8.dp, horizontal = 2.dp)
                                            .fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )

                                    Text(
                                        text = post.post.content,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(8.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )

                                    Text(
                                        text = post.post.time,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Text(
                                    text = "Comentarios",
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                TextField(
                                    value = comments.value,
                                    onValueChange = { if (it.length <= 200) comments.value = it },
                                    label = {
                                        Text(
                                            "Añadir comentario",
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .height(120.dp), // Altura fija
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 16.sp
                                    ),
                                    maxLines = 4
                                )
                                Text(
                                    text = "${comments.value.length}/200",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.align(Alignment.End).padding(end = 16.dp)
                                )
                                // Dentro de tu Composable, después del TextField:
                                Button(
                                    onClick = {
                                        if (comments.value.isNotBlank()) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val request = NuevoComentarioRequest(
                                                        userId = currentUserData.id,
                                                        userName = currentUserData.userName,
                                                        comentario = comments.value
                                                    )
                                                    val response = RetrofitClient.getInstance(context)
                                                        .commentPost(id, request)
                                                    withContext(Dispatchers.Main) {
                                                        if (response.isSuccessful) {
                                                            comments.value = ""
                                                            cargarPosts()
                                                        } else {
                                                            val errorBody = response.errorBody()?.string()
                                                            errorMessage.value = "Error al comentar: ${response.code()} - $errorBody"
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    withContext(Dispatchers.Main) {
                                                        errorMessage.value = "Fallo: ${e.message}"
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(end = 16.dp, top = 8.dp)
                                ) {
                                    Text("Enviar")
                                }
                            }

                        }
                    }
                    item {
                        post.value?.post?.comments?.let { comentarios ->
                            if (!comentarios.isNullOrEmpty()) {
                                Column {
                                    comentarios.forEach { comentario ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = comentario.userName,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                                Text(
                                                    text = formatearFecha(comentario.fecha),
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                                Text(
                                                    text = comentario.comentario,
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "No hay comentarios aún.",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
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

fun formatearFecha(fechaIso: String?): String {
    return try {
        val fecha = LocalDateTime.parse(fechaIso)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        fecha.format(formatter)
    } catch (e: Exception) {
        fechaIso ?: ""
    }
}