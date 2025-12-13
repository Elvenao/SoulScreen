package com.example.mongodb.screens

import android.text.format.DateUtils.formatDateTime
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.NuevoComentarioRequest
import com.example.mongodb.model.PostWithAvatar
import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.tertiary)
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Avatar del usuario
                                        AsyncImage(
                                            model = currentUserData.ip + post.userAvatar,
                                            contentDescription = "Avatar de usuario",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    if (post.post.userId == currentUserData.id) {
                                                        navController.navigate("ProfileScreen")
                                                    } else {
                                                        navController.navigate("seeprofileuser/${post.post.userId}")
                                                    }
                                                }
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = post.post.user,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                            Text(
                                                text = timeAgo(LocalDateTime.parse(post.post.date)),
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) // Color más sutil
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // --- CUERPO DEL POST: TÍTULO Y CONTENIDO ---
                                    // Título del post
                                    Text(
                                        text = post.post.title,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Contenido completo del post (sin "Ver mas...")
                                    Text(
                                        text = post.post.content,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // --- INFORMACIÓN EXTRA: "Acerca de" y PostType ---
                                    // Fila para el tipo de post y la imagen del medio
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = "Acerca de: ${post.post.mediaName}",
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                            Text(
                                                text = "Tipo: ${post.post.postType}",
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }

                                        AsyncImage(
                                            model = currentUserData.ip + post.post.mediaImg, // Agregado el protocolo http
                                            contentDescription = "Imagen del medio",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(40.dp) // Tamaño consistente
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }
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
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(end = 16.dp)
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
                            if (comentarios.isNotEmpty()) {
                                Column(Modifier.padding(bottom = 20.dp)) {
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