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
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val isPreview = LocalInspectionMode.current
    val encryptedSharedPreferences = remember {
        if (isPreview) null else SecurePrefs(context)
    }
    val refreshToken = encryptedSharedPreferences?.getRefreshToken()

    fun cargarSeguidores() {
        isRefreshing = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.getInstance(context).getUserProfile(userId).execute()



                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        val seguidoresList = user?.followers?.mapNotNull { id ->
                            val res = RetrofitClient.getInstance(context).getUserIdImgById(id).execute()
                            if (res.isSuccessful) res.body() else null
                        } ?: emptyList()
                        // Cargar info de seguidos
                        val seguidosList = user?.following?.map { id ->
                            async{
                                val res = RetrofitClient.getInstance(context).getUserIdImgById(id).execute()
                                if (res.isSuccessful) res.body() else null
                            }
                        }?.awaitAll()?.filterNotNull() ?: emptyList()
                        withContext(Dispatchers.Main) {
                            seguidores = seguidoresList
                            seguidos = seguidosList
                            isLoading = false
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            isLoading = false
                        }
                    }
                    isRefreshing = false
                }
            } catch (e: Exception) {

                isRefreshing = false
            }



        }
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { cargarSeguidores() }
    )
    LaunchedEffect(userId) {
        isLoading = true
        withContext(Dispatchers.IO) {
            val response = RetrofitClient.getInstance(context).getUserProfile(userId).execute()
            if (response.isSuccessful) {
                val user = response.body()
                // Cargar info de seguidores
                val seguidoresList = user?.followers?.mapNotNull { id ->
                    val res = RetrofitClient.getInstance(context).getUserIdImgById(id).execute()
                    if (res.isSuccessful) res.body() else null
                } ?: emptyList()
                // Cargar info de seguidos
                val seguidosList = user?.following?.map { id ->
                    async{
                        val res = RetrofitClient.getInstance(context).getUserIdImgById(id).execute()
                        if (res.isSuccessful) res.body() else null
                    }
                }?.awaitAll()?.filterNotNull() ?: emptyList()
                withContext(Dispatchers.Main) {
                    seguidores = seguidoresList
                    seguidos = seguidosList
                    isLoading = false
                }
            }else{
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("CARGANDO")
        }
    } else {
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
        }
    }

}


