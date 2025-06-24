package com.example.mongodb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.UserIdImg
import com.example.mongodb.model.UserData
import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun seguidoresUser(userId: String, navController: NavController) {
    val context = LocalContext.current
    var userData by remember { mutableStateOf<UserData?>(null) }
    var seguidores by remember { mutableStateOf<List<UserIdImg>>(emptyList()) }
    var seguidos by remember { mutableStateOf<List<UserIdImg>>(emptyList()) }
    val scope = rememberCoroutineScope()

    val EncryptedSharedPreferences = SecurePrefs(context)
    val currentUserData = EncryptedSharedPreferences.getCurrentUserData()

    LaunchedEffect(userId) {
        withContext(Dispatchers.IO) {
            val response = RetrofitClient.getInstance(context).getUserProfile(userId).execute()
            if (response.isSuccessful) {
                val user = response.body()
                userData = user
                // Cargar info de seguidores
                val seguidoresList = user?.followers?.mapNotNull { id ->
                    val res = RetrofitClient.getInstance(context).getUserIdImgById(id).execute()
                    if (res.isSuccessful) res.body() else null
                } ?: emptyList()
                // Cargar info de seguidos
                val seguidosList = user?.following?.mapNotNull { id ->
                    val res = RetrofitClient.getInstance(context).getUserIdImgById(id).execute()
                    if (res.isSuccessful) res.body() else null
                } ?: emptyList()
                withContext(Dispatchers.Main) {
                    seguidores = seguidoresList
                    seguidos = seguidosList
                }
            }
        }
    }

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
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .padding(8.dp),
            ){
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
                            .padding(vertical = 8.dp)
                    ) {
                        AsyncImage(
                            model = "http://" + currentUserData.ip + user.img,
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
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .padding(8.dp),
            ){
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
                            .padding(vertical = 8.dp)
                    ) {
                        AsyncImage(
                            model = "http://" + currentUserData.ip + user.img,
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
}