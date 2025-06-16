package com.example.mongodb.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mongodb.SecurePrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun profileScreen(
    navController: NavController
) {
    val EncryptedSharedPreferences = SecurePrefs(LocalContext.current)
    val currentUserData = EncryptedSharedPreferences.getCurrentUserData()
    val isRefreshing = remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Peliculas", "Series", "Videojuegos", "Música", "Libros")

    BackHandler {
        navController.navigate("UIPrincipal") {
            popUpTo("UIPrincipal") { inclusive = true }
        }
    }

    fun cargarUsuario() {
        //TODO: Implementar la lógica para cargar los datos del usuario
        isRefreshing.value = true
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            withContext(Dispatchers.Main) {
                isRefreshing.value = false
            }
        }
    }
    LaunchedEffect(Unit) {
        cargarUsuario()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = { cargarUsuario() }
    )

    if (currentUserData == null) {
        // Handle the case where user data is not available
        Text(text = "No user data available")
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ){
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally
            , modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White)
                .padding(16.dp)
        ){
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    /*modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                        .padding(16.dp)*/
                ) {
                    AsyncImage(
                        model = currentUserData.avatar,
                        contentDescription = "Imagen desde URL",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(128.dp)
                            .padding(8.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = currentUserData.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        color = Color.Black
                    )
                    Text(
                        text = "@${currentUserData.userName}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                    )
                    Text(
                        text = "${currentUserData.biography}",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                    )
                    Text(
                        text = "Generos favoritos: ${currentUserData.genres?.joinToString(", ")}.",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp, bottom = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "n Seguidores |",
                            fontSize = 18.sp
                        )
                        Text(
                            text = " n Seguidos",
                            fontSize = 18.sp,
                        )
                    }
                    Text(
                        text = "Fecha de nacimiento: ${currentUserData.birthDate}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )

                    Text(
                        text = "Se unió en: ${currentUserData.birthDate}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 18.dp)
                    )
                }
            }

            item{
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 2.dp,
                )
            }

            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    backgroundColor = Color.White,
                    contentColor = Color(0xFF7C4DFF),
                    edgePadding = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTabIndex == index) Color(0xFF7C4DFF) else Color.Gray,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        )
                    }
                }
                when (selectedTabIndex) {
                    0 -> {
                        // Contenido para Peliculas
                        Text(text = "Contenido de Peliculas", modifier = Modifier.padding(16.dp))
                    }
                    1 -> {
                        // Contenido para Series
                        Text(text = "Contenido de Series", modifier = Modifier.padding(16.dp))
                    }
                    2 -> {
                        // Contenido para Videojuegos
                        Text(text = "Contenido de Videojuegos", modifier = Modifier.padding(16.dp))
                    }
                    3 -> {
                        // Contenido para Música
                        Text(text = "Contenido de Música", modifier = Modifier.padding(16.dp))
                    }
                    4 -> {
                        // Contenido para Libros
                        Text(text = "Contenido de Libros", modifier = Modifier.padding(16.dp))
                    }

                    else -> {
                        // Manejo de caso por defecto
                        Text(text = "Contenido no disponible", modifier = Modifier.padding(16.dp))
                    }
                }


            }

        }

        PullRefreshIndicator(
            refreshing = isRefreshing.value,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}