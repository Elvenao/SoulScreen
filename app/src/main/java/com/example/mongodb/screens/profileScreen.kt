package com.example.mongodb.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.RefreshTokenRequest
import com.example.mongodb.network.RetrofitClient
import com.google.accompanist.flowlayout.FlowRow
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
    val context = LocalContext.current
    val EncryptedSharedPreferences = SecurePrefs(LocalContext.current)
    val refreshToken = EncryptedSharedPreferences.getRefreshToken()
    val currentUserData = EncryptedSharedPreferences.getCurrentUserData()
    val isRefreshing = remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Peliculas", "Series", "Videojuegos", "Música", "Libros")
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navController.navigate("openCamera/takenPhoto")
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_LONG).show()
        }
    }
    BackHandler {
        navController.navigate("UIPrincipal") {
            popUpTo("UIPrincipal") { inclusive = true }
        }
    }

    fun cargarUsuario(refreshToken: String?) {
        //TODO: Implementar la lógica para cargar los datos del usuario
        isRefreshing.value = true
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            refreshToken?.let {
                RefreshTokenRequest(
                    it
                )
            }?.let { RetrofitClient.getInstance(context).refreshToken(it) }
            withContext(Dispatchers.Main) {
                isRefreshing.value = false
            }
        }
    }
    LaunchedEffect(Unit) {
        cargarUsuario(refreshToken)
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = { cargarUsuario(refreshToken) }
    )

    if (currentUserData == null) {
        // Handle the case where user data is not available
        Text(text = "No user data available", color= Color.Red, modifier = Modifier.padding(16.dp))
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .background(MaterialTheme.colorScheme.background)
    ){
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally
            , modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ){
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AsyncImage(
                        model = currentUserData.avatar,
                        contentDescription = "Imagen desde URL",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(128.dp)
                            .padding(12.dp)
                            .clip(CircleShape)
                            .clickable {
                                val hasCameraPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasCameraPermission) {
                                    navController.navigate("openCamera/takenPhoto")
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                    )
                    Text(
                        text = currentUserData.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "@${currentUserData.userName}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "${currentUserData.biography}",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Generos que me interesan: ",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    FlowRow(
                        mainAxisSpacing = 8.dp,
                        crossAxisSpacing = 8.dp) {
                        currentUserData.genres?.forEach { genre ->
                            Box(
                                modifier = Modifier
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        shape = RoundedCornerShape(50) // 50% de redondeo (circular)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = genre,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp, bottom = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "n Seguidores |",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = " n Seguidos",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = "Fecha de nacimiento: ${currentUserData.birthDate}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Text(
                        text = "Se unió en: ${currentUserData.birthDate}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 18.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            item{
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.secondary,
                    thickness = 2.dp,
                )
            }

            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.secondary,
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
                                    color = if (selectedTabIndex == index) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
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
                        Text(text = "Contenido de Peliculas", modifier = Modifier.padding(16.dp),color = MaterialTheme.colorScheme.onPrimary)
                    }
                    1 -> {
                        // Contenido para Series
                        Text(text = "Contenido de Series", modifier = Modifier.padding(16.dp),color = MaterialTheme.colorScheme.onPrimary)
                    }
                    2 -> {
                        // Contenido para Videojuegos
                        Text(text = "Contenido de Videojuegos", modifier = Modifier.padding(16.dp),color = MaterialTheme.colorScheme.onPrimary)
                    }
                    3 -> {
                        // Contenido para Música
                        Text(text = "Contenido de Música", modifier = Modifier.padding(16.dp),color = MaterialTheme.colorScheme.onPrimary)
                    }
                    4 -> {
                        // Contenido para Libros
                        Text(text = "Contenido de Libros", modifier = Modifier.padding(16.dp),color = MaterialTheme.colorScheme.onPrimary)
                    }

                    else -> {
                        // Manejo de caso por defecto
                        Text(text = "Contenido no disponible", modifier = Modifier.padding(16.dp),color = MaterialTheme.colorScheme.onPrimary)
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