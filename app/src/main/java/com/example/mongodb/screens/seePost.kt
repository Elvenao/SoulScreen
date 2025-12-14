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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.Colors
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
                                "Detalles del post",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.height(60.dp)
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
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                // Cabecera
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp), // Padding para darle altura y aire
                                    verticalAlignment = Alignment.Top, // Alinear al tope para que el título no "empuje" al avatar hacia abajo
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    //Avatar y titulo
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable {
                                                if (post.post.userId == currentUserData.id) {
                                                    navController.navigate("ProfileScreen")
                                                } else {
                                                    navController.navigate("seeprofileuser/${post.post.userId}")
                                                }
                                            }
                                        ) {
                                            AsyncImage(
                                                model = currentUserData.ip + post.userAvatar,
                                                contentDescription = "Avatar de usuario",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clip(CircleShape)
                                            )

                                            Spacer(modifier = Modifier.width(12.dp))

                                            // Nombre y fecha
                                            Column {
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
                                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                                )
                                            }
                                        }

                                        // titulo
                                        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre info de autor y título
                                        Text(
                                            text = post.post.title,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            // El padding derecho evita que el texto se pegue a la imagen del media
                                            modifier = Modifier.padding(end = 16.dp)
                                        )
                                    }

                                    // Imagen
                                    // Se mantiene a la derecha, alineada con el conjunto
                                    AsyncImage(
                                        model = currentUserData.ip + post.post.mediaImg,
                                        contentDescription = "Imagen del medio",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                }

                                //Cuerpo del post
                                Text(
                                    text = post.post.content,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp) // Añadido padding
                                )

                                // acerca de
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                ) {
                                    Text(
                                        text = "ACERCA DE",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.tertiary)
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${post.post.mediaName} • ${post.post.postType}",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp), // Padding horizontal para que no toque los bordes
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start // Centra el contenido horizontalmente
                            ) {
                                Text(
                                    text = "Comentarios",
                                    fontSize = 22.sp, // Un tamaño un poco más sutil
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Text(
                                    text = "Crear comentario",
                                    fontSize = 18.sp,
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
                                        .height(80.dp), // Altura fija
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 12.sp
                                    ),
                                    maxLines = 5
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Text(
                                        text = "${comments.value.length}/200",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                    )

                                    Button(
                                        onClick = {
                                            if (comments.value.isNotBlank()) {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    try {
                                                        val request = NuevoComentarioRequest(
                                                            userId = currentUserData.id,
                                                            userName = currentUserData.userName,
                                                            userMedia = currentUserData.avatar,
                                                            comentario = comments.value,
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
                                        shape = RoundedCornerShape(50),
                                        enabled = comments.value.isNotBlank(),

                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                            disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                                            contentColor = MaterialTheme.colorScheme.onPrimary,
                                            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                                        )
                                    ) {
                                        Text(
                                            text = "Enviar",
                                            fontSize = 14.sp
                                        )
                                    }
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
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp), // Espacio entre cada comentario
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.tertiary // Color de fondo sutil
                                            )
                                        ) {
                                            // Usamos un Row para alinear la imagen y el texto
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.Top // Alinea los elementos en la parte superior
                                            ) {
                                                // FOTO DE PERFIL DEL USUARIO QUE COMENTÓ
                                                AsyncImage(
                                                    model = comentario.userMedia,
                                                    contentDescription = "Avatar de ${comentario.userName}",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape) // Avatar circular
                                                )

                                                // Espacio entre la foto y el texto
                                                Spacer(modifier = Modifier.width(12.dp))

                                                // COLUMNA PARA EL NOMBRE, FECHA Y TEXTO DEL COMENTARIO
                                                Column(modifier = Modifier.weight(1f)) {
                                                    // Fila para el nombre y la fecha
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = comentario.userName,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onPrimary,
                                                            fontSize = 16.sp
                                                        )
                                                        Text(
                                                            text = timeAgo(LocalDateTime.parse(comentario.fecha)), // Usando timeAgo
                                                            fontSize = 12.sp,
                                                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                                        )
                                                    }

                                                    // El texto del comentario
                                                    Text(
                                                        text = comentario.comentario,
                                                        color = MaterialTheme.colorScheme.onPrimary,
                                                        modifier = Modifier.padding(top = 4.dp),
                                                        fontSize = 15.sp
                                                    )
                                                }
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