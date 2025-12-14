package com.example.mongodb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.UserData
import com.example.mongodb.model.UserIdImg
import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun seguidoresUser(userId: String, navController: NavController) {
    val context = LocalContext.current
    var userData by remember { mutableStateOf<UserData?>(null) }
    var seguidores by remember { mutableStateOf<List<UserIdImg>>(emptyList()) }
    var seguidos by remember { mutableStateOf<List<UserIdImg>>(emptyList()) }
    val scope = rememberCoroutineScope()

    val EncryptedSharedPreferences = SecurePrefs(context)
    val currentUserData = EncryptedSharedPreferences.getCurrentUserData()
    var isRefreshing by remember { mutableStateOf(false) }
    val isPreview = LocalInspectionMode.current
    val encryptedSharedPreferences = remember {
        if (isPreview) null else SecurePrefs(context)
    }
    val refreshToken = encryptedSharedPreferences?.getRefreshToken()

    var error by remember { mutableStateOf<String?>(null) } // Para mostrar mensajes de error

    // 1. LA NUEVA FUNCIÓN CENTRALIZADA PARA CARGAR DATOS
    suspend fun fetchFollowData() {
        error = null // Limpia errores previos al recargar
        try {
            // Obtener el perfil del usuario directamente (gracias al 'suspend' del Paso 1)
            val user = RetrofitClient.getInstance(context).getUserProfile(userId)

            if (user != null) {
                // Lanzar las llamadas para obtener detalles EN PARALELO para más eficiencia
                val seguidoresDeferred = user.followers?.map { followerId ->
                    scope.async(Dispatchers.IO) { // Ejecuta cada llamada en un hilo de fondo
                        RetrofitClient.getInstance(context).getUserIdImgById(followerId)
                    }
                }

                val seguidosDeferred = user.following?.map { followingId ->
                    scope.async(Dispatchers.IO) {
                        RetrofitClient.getInstance(context).getUserIdImgById(followingId)
                    }
                }

                // Esperar a que todas las llamadas terminen y filtrar los nulos
                seguidores = seguidoresDeferred?.awaitAll()?.filterNotNull() ?: emptyList()
                seguidos = seguidosDeferred?.awaitAll()?.filterNotNull() ?: emptyList()

            } else {
                error = "No se encontró el perfil del usuario."
            }
        } catch (e: retrofit2.HttpException) { // Error de servidor (ej: 404, 500)
            error = "Error al cargar el perfil: Código ${e.code()}"
        } catch (e: Exception) { // Otros errores (ej: sin conexión a internet)
            error = "Error de red: Revisa tu conexión."
            e.printStackTrace() // Imprime el error en la consola para depuración
        } finally {
            // Asegurarse de que los indicadores de carga se desactiven siempre
            isRefreshing = false
        }
    }

// 2. EL NUEVO LaunchedEffect PARA LA CARGA INICIAL
    LaunchedEffect(key1 = userId) {
        isRefreshing = true // <-- USA isRefreshing en lugar de isLoading
        fetchFollowData()
    }

// 3. EL NUEVO pullRefreshState QUE USA LA FUNCIÓN CENTRALIZADA
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                fetchFollowData() // Llama a la lógica centralizada al refrescar
            }
        }
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState))
    {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)

        ) {
            // Columna de Seguidores
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Text("Seguidores", color = MaterialTheme.colorScheme.onPrimary)
                }
                LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    items(seguidores) { user ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (user.id == currentUserData.id) {
                                        navController.navigate("ProfileScreen")
                                    } else {
                                        navController.navigate("seeProfileUser/${user.id}")
                                    }
                                }
                                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                        ) {
                            AsyncImage(
                                model = currentUserData.ip + user.img,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(user.username, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Columna de Seguidos
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Text("Seguidos", color = MaterialTheme.colorScheme.onPrimary)
                }
                LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    items(seguidos) { user ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (user.id == currentUserData.id) {
                                        navController.navigate("ProfileScreen")
                                    } else {
                                        navController.navigate("seeProfileUser/${user.id}")
                                    }
                                }
                                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                        ) {
                            AsyncImage(
                                model = currentUserData.ip + user.img,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(user.username, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
        androidx.compose.material.pullrefresh.PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
            )
    }



}


