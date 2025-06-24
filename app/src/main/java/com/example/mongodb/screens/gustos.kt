package com.example.mongodb.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.MultimediaIdImg
import com.example.mongodb.model.UserData
import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun gustos(navController: NavController) {
    val context = LocalContext.current
    var userData by remember { mutableStateOf<UserData?>(null) }
    var likes by remember { mutableStateOf<List<MultimediaIdImg>>(emptyList()) }
    var dislikes by remember { mutableStateOf<List<MultimediaIdImg>>(emptyList()) }
    val EncryptedSharedPreferences = SecurePrefs(context)
    val currentUserData = EncryptedSharedPreferences.getCurrentUserData()
    var selectedIndex by remember { mutableStateOf(2) } // "Mis gustos" seleccionado
    val isRefreshing = remember { mutableStateOf(false) }

    fun cargarPerfil() {
        isRefreshing.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.getInstance(context).getUserProfile(currentUserData.id).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            userData = user
                            // Cargar likes y dislikes en segundo plano
                            CoroutineScope(Dispatchers.IO).launch {
                                val likesList = user.like.orEmpty().mapNotNull { id ->
                                    val res = RetrofitClient.getInstance(context).getNameAndImgById(id)
                                    if (res.isSuccessful) res.body() else null
                                }
                                val dislikesList = user.dislike.orEmpty().mapNotNull { id ->
                                    val res = RetrofitClient.getInstance(context).getNameAndImgById(id)
                                    if (res.isSuccessful) res.body() else null
                                }
                                withContext(Dispatchers.Main) {
                                    likes = likesList
                                    dislikes = dislikesList
                                    isRefreshing.value = false
                                }
                            }
                        }
                    } else {
                        isRefreshing.value = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    isRefreshing.value = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarPerfil()
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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Columna de Likes
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                        .fillMaxWidth()
                        .padding(8.dp),
                ){
                    Text("Likes", color = MaterialTheme.colorScheme.onPrimary)
                }
                // Mostrar los IDs de likes para depuración
                Text("IDs: ${userData?.like?.joinToString() ?: "(vacío)"}", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(8.dp))
                // Mostrar resultado de la consulta a getNameAndImgById para cada ID
                userData?.like?.forEach { id ->
                    val res = likes.find { it.id == id }
                    if (res != null) {
                        Text("✔️ $id: ${res.name}", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 8.dp))
                    } else {
                        Text("❌ $id: No encontrado", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    items(likes) { media ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Puedes navegar a detalles del medio si lo deseas
                                    // navController.navigate("mediaDetail/${media.id}")
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            AsyncImage(
                                model = "http://" + currentUserData.ip + media.img,
                                contentDescription = "Poster",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(media.name, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Columna de Dislikes
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                        .fillMaxWidth()
                        .padding(8.dp),
                ){
                    Text("Dislikes", color = MaterialTheme.colorScheme.onPrimary)
                }
                // Mostrar los IDs de dislikes para depuración
                Text("IDs: ${userData?.dislike?.joinToString() ?: "(vacío)"}", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(8.dp))
                // Mostrar resultado de la consulta a getNameAndImgById para cada ID
                userData?.dislike?.forEach { id ->
                    val res = dislikes.find { it.id == id }
                    if (res != null) {
                        Text("✔️ $id: ${res.name}", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 8.dp))
                    } else {
                        Text("❌ $id: No encontrado", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    items(dislikes) { media ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Puedes navegar a detalles del medio si lo deseas
                                    // navController.navigate("mediaDetail/${media.id}")
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            AsyncImage(
                                model = "http://" + currentUserData.ip + media.img,
                                contentDescription = "Poster",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(media.name, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}