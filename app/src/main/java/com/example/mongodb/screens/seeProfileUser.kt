package com.example.mongodb.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import com.google.accompanist.flowlayout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.mongodb.model.UserData
import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.ExperimentalLayoutApi


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
fun seeProfileUser(username: String, navController: NavController) {
    val context = LocalContext.current
    val currentUser = SecurePrefs(context).getCurrentUserData()
    val userData = remember { mutableStateOf<UserData?>(null) }
    val isRefreshing = remember { mutableStateOf(false) }
    val isFollowing = remember { mutableStateOf(false) }

    fun cargarPerfil() {
        isRefreshing.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.getInstance(context).getUserProfile(username).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            userData.value = it
                            isFollowing.value = it.followers.contains(currentUser.userName)
                        }
                    }
                    isRefreshing.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al cargar perfil", Toast.LENGTH_SHORT).show()
                    isRefreshing.value = false
                }
            }
        }
    }

    fun toggleFollow() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.getInstance(context)
                    .toggleFollow(username, currentUser.userName)
                    .execute()
                if (response.isSuccessful) {
                    isFollowing.value = !isFollowing.value
                }
            } catch (_: Exception) {}
        }
    }

    LaunchedEffect(Unit) {
        cargarPerfil()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = { cargarPerfil() }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .background(MaterialTheme.colorScheme.background)
    ) {

    userData.value?.let { user ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                AsyncImage(
                    model = "http://${currentUser.ip}${user.avatar}",
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                        .padding(12.dp)
                        .clip(CircleShape)
                )

                Text(
                    text = user.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "@${user.userName}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = user.biography ?: "",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                Button(
                    onClick = { toggleFollow() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowing.value) Color.Gray else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = if (isFollowing.value) "Siguiendo" else "Seguir",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Text(
                    text = "Fecha de nacimiento: ${user.birthDate}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Se unió en: ${user.birthDate}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = "Géneros que le gustan:",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                FlowRow(
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    user.genres?.forEach { genre ->
                        Box(
                            modifier = Modifier
                                .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(50))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(text = genre, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
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
