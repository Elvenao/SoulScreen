@file:kotlin.OptIn(ExperimentalMaterialApi::class)

package com.example.mongodb.screens

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.MultimediaIdImg
import com.example.mongodb.model.UserData
import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class, ExperimentalMaterialApi::class)
@Composable
fun gustos(navController: NavController) {
    val context = LocalContext.current
    var userData by remember { mutableStateOf<UserData?>(null) }
    var likes by remember { mutableStateOf<List<MultimediaIdImg>>(emptyList()) }
    var dislikes by remember { mutableStateOf<List<MultimediaIdImg>>(emptyList()) }
    val EncryptedSharedPreferences = SecurePrefs(context)
    val currentUserData = EncryptedSharedPreferences.getCurrentUserData()
    var selectedIndex by remember { mutableStateOf(2) } // "Mis gustos" seleccionado

    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }
    var isRefreshing by remember { mutableStateOf(false) } // <-- ¡AÑADE ESTA LÍNEA!



    suspend fun fetchLikesData() {
        isRefreshing = true
        error = null // Limpia errores previos
        try {
            // 1. Obtener el perfil directamente (gracias a que ahora es 'suspend')
            val user = RetrofitClient.getInstance(context).getUserProfile(currentUserData.id)

            if (user != null) {
                userData = user // Actualiza el estado del usuario

                // 2. Lanzar las llamadas para obtener detalles de LIKES en PARALELO
                val likesDeferred = user.like?.map { likeId ->
                    scope.async(Dispatchers.IO) { // Ejecuta cada llamada en un hilo de fondo
                        RetrofitClient.getInstance(context).getNameAndImgById(likeId)
                    }
                }

                // 3. Lanzar las llamadas para obtener detalles de DISLIKES en PARALELO
                val dislikesDeferred = user.dislike?.map { dislikeId ->
                    scope.async(Dispatchers.IO) {
                        RetrofitClient.getInstance(context).getNameAndImgById(dislikeId)
                    }
                }

                // 4. Esperar a que TODAS las llamadas terminen y actualizar el estado
                likes = likesDeferred?.awaitAll()?.filterNotNull() ?: emptyList()
                dislikes = dislikesDeferred?.awaitAll()?.filterNotNull() ?: emptyList()

            } else {
                error = "No se encontró el perfil del usuario."
            }
        } catch (e: retrofit2.HttpException) { // Error de servidor (ej: 404, 500)
            error = "Error al cargar datos: Código ${e.code()}"
        } catch (e: Exception) { // Otros errores (ej: sin conexión)
            error = "Error de red: Revisa tu conexión."
            e.printStackTrace() // Imprime el error en la consola para depuración
        } finally {
            // 5. Asegurarse de que el indicador de carga se desactive siempre
            isRefreshing = false
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                fetchLikesData()
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        fetchLikesData()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary,
                tonalElevation = 0.dp
            ) {
                val items = listOf("Posts", "Explorar", "Mis gustos")
                val icons = listOf(
                    Icons.Default.Home,
                    Icons.Default.Search,
                    Icons.Default.AssignmentTurnedIn
                )
                items.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            when (index) {
                                0 -> navController.navigate("Posts")
                                1 -> navController.navigate("ExplorarScreen")
                                2 -> {} // Ya estás aquí
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = label,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        label = {
                            Text(
                                label,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pullRefresh(pullRefreshState) // Habilita deslizar para refrescar
        ) {
            Column(Modifier.fillMaxSize()) {
                // Muestra un mensaje si ocurre un error
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // El Row que ya tenías, ahora dentro de la Columna
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp)
                ) {
                    // ... Aquí van tus dos columnas de Likes y Dislikes (sin cambios) ...
                    // Columna de Likes
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                        ) {
                            Text("Likes", color = MaterialTheme.colorScheme.onPrimary)
                        }

// Lista de elementos que le gustan al usuario
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(likes) { media ->
                                // Fila para cada película/serie
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable { navController.navigate("multimedia/${media.id}") }, // Navega al detalle
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    AsyncImage(
                                        model = currentUserData.ip + media.img, // Asumiendo que 'media.img' ya es la URL completa
                                        contentDescription = "Póster de ${media.name}",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = media.name,
                                        color = MaterialTheme.colorScheme.onSurface // Color corregido para ser visible
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    // Columna de Dislikes
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                        ) {
                            Text("Dislikes", color = MaterialTheme.colorScheme.onPrimary)
                        }

// Lista de elementos que NO le gustan al usuario
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(dislikes) { media ->
                                // Fila para cada película/serie
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable { navController.navigate("multimedia/${media.id}") }, // Navega al detalle
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                                ) {
                                    AsyncImage(
                                        model = media.img, // Asumiendo que 'media.img' ya es la URL completa
                                        contentDescription = "Póster de ${media.name}",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = media.name,
                                        color = MaterialTheme.colorScheme.onSurface // Color corregido para ser visible
                                    )
                                }
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
}