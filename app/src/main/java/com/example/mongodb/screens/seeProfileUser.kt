package com.example.mongodb.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.LikeInformation
import com.example.mongodb.model.PostWithAvatar
import com.example.mongodb.model.UserData
import com.example.mongodb.network.RetrofitClient
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import androidx.compose.material.Text as MaterialText


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun seeProfileUser(idTarget: String, navController: NavController) {
    val context = LocalContext.current
    val currentUser = SecurePrefs(context).getCurrentUserData()
    val userData = remember { mutableStateOf<UserData?>(null) }
    val isRefreshing = remember { mutableStateOf(false) }
    val isFollowing = remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Peliculas", "Series", "Videojuegos", "Música", "Libros")
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }
    var userPosts by remember { mutableStateOf<List<PostWithAvatar>>(emptyList()) }

    suspend fun fetchUserProfile() {
        isRefreshing.value = true
        error = null
        try {
            val user = RetrofitClient.getInstance(context).getUserProfile(idTarget)
            if (user != null) {
                userData.value = user
                isFollowing.value = user.followers?.contains(currentUser.id) == true
            } else {
                error = "No se pudo encontrar el perfil del usuario."
            }
        } catch (e: retrofit2.HttpException) {
            error = "Error al cargar el perfil: Código ${e.code()}"
        } catch (e: Exception) {
            error = "Error de red: Revisa tu conexión."
            e.printStackTrace()
        } finally {
            isRefreshing.value = false
        }
    }

    // 2. FUNCIÓN PARA SEGUIR/DEJAR DE SEGUIR
    fun handleToggleFollow() {
        scope.launch {
            try {
                // Asumimos que toggleFollow también es 'suspend fun'. ¡HAY QUE REVISAR ApiService!
                RetrofitClient.getInstance(context).toggleFollow(idTarget, currentUser.id)
                // Actualizamos el estado localmente para una respuesta instantánea
                isFollowing.value = !isFollowing.value
                // Refrescamos los datos del perfil en segundo plano para actualizar el contador
                fetchUserProfile()
            } catch (e: Exception) {
                error = "No se pudo realizar la acción. Inténtalo de nuevo."
                e.printStackTrace()
            }
        }
    }

    fun cargarUserPosts() {
        // Seguridad: no hacer nada si aún no tenemos los datos del usuario
        if (currentUser == null) {
            isRefreshing.value = false // Si se estaba refrescando, lo detenemos
            return
        }

        // 1. Llamamos al endpoint correcto, pasándole el ID del usuario actual
        val call = RetrofitClient.getInstance(context).getUserPosts(idTarget)

        // 2. Usamos enqueue para la llamada asíncrona (no bloquea la UI)
        call.enqueue(object : retrofit2.Callback<List<com.example.mongodb.model.Post>> {
            override fun onResponse(
                call: retrofit2.Call<List<com.example.mongodb.model.Post>>,
                response: retrofit2.Response<List<com.example.mongodb.model.Post>>
            ) {
                if (response.isSuccessful) {
                    val postsDelUsuario = response.body()
                    if (postsDelUsuario != null) {
                        // 3. Adaptamos los datos para que coincidan con lo que la UI espera
                        // La UI espera PostWithAvatar, pero recibimos Post. Los "mapeamos".
                        userPosts = postsDelUsuario.map { post ->
                            PostWithAvatar(
                                post = post, // La información del post que acabamos de recibir
                                userAvatar = currentUser.avatar, // El avatar del usuario actual que ya tenemos
                            )
                        }
                    }
                } else {
                    // Manejo del error de la respuesta
                    Toast.makeText(context, "Error al cargar posts: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
                // Importante: Indicar que el refresco ha terminado, tanto si tuvo éxito como si no
                isRefreshing.value = false
            }

            override fun onFailure(call: retrofit2.Call<List<com.example.mongodb.model.Post>>, t: Throwable) {
                // Manejo del fallo de red
                Toast.makeText(context, "Fallo de red al cargar posts: ${t.message}", Toast.LENGTH_SHORT).show()
                // Importante: Indicar que el refresco ha terminado
                isRefreshing.value = false
            }
        })
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = {
            scope.launch {
                fetchUserProfile() // Llama a la lógica centralizada al refrescar
                cargarUserPosts()
            }
        }
    )
    LaunchedEffect(key1 = idTarget) {
        fetchUserProfile()
        cargarUserPosts()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        userData.value?.let { user ->
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = "${currentUser.ip}${user.avatar}",
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
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "@${user.userName}",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = user.biography ?: "",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Géneros que le gustan:",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        FlowRow(
                            mainAxisSpacing = 8.dp,
                            crossAxisSpacing = 8.dp
                        ) {
                            user.genres?.forEach { genre ->
                                Box(
                                    modifier = Modifier
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            shape = RoundedCornerShape(50)
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
                                .padding(start = 8.dp, bottom = 15.dp)
                                .clickable(
                                    onClick = {
                                        navController.navigate("seguidoresUser/${user.id}")
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${user.followers?.size ?: 0} Seguidores |",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = " ${user.following?.size ?: 0} Seguidos",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            text = "Fecha de nacimiento: ${user.birthDate}",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Se unió en: ${user.birthDate}",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp, bottom = 18.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Button(
                            onClick = { handleToggleFollow()},
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
                    }
                }


                /*item {
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
                                    MaterialText(
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
                        0 -> Text("Contenido de Peliculas", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                        1 -> Text("Contenido de Series", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                        2 -> Text("Contenido de Videojuegos", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                        3 -> Text("Contenido de Música", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                        4 -> Text("Contenido de Libros", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                        else -> Text("Contenido no disponible", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                    }
                }*/

                items(userPosts.sortedByDescending { it.post.date }) { post ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .height(400.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .clickable {
                                navController.navigate("VerPostScreen/${post.post.id}")
                            }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ){
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(2f)
                                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)

                            ) {
                                Column(
                                    modifier = Modifier.weight(7.5f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,

                                        ) {
                                        AsyncImage(
                                            model = "${currentUser.ip}${user.avatar}" ,
                                            contentDescription = "Imagen de usuario",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .padding(top = 8.dp)
                                                .size(52.dp)
                                                .clip(CircleShape)
                                        )
                                        androidx.compose.material3.Text(
                                            text = "@" + post.post.user,
                                            fontSize = 18.sp,
                                            modifier = Modifier
                                                .padding(top = 10.dp, start = 8.dp)
                                                .weight(2f), // Da más espacio al usuario, pero deja sitio para la fecha
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                    Row(

                                    ) {
                                        androidx.compose.material3.Text(
                                            text = post.post.mediaName,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .weight(2f)
                                                .padding(top = 8.dp, bottom = 0.dp),

                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                    Row(){
                                        if(post.post.postType=="SPOILER"){
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                            ){
                                                Row(){
                                                    androidx.compose.material3.Text(
                                                        text = "SPOILER ALERT",
                                                        fontSize = 24.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.Red,
                                                        modifier = Modifier.padding(top = 8.dp)
                                                    )
                                                }
                                                Row(){
                                                    androidx.compose.material3.Text(
                                                        text = "Presiona para ver detalles (Bajo tu propio riesgo)",
                                                        fontSize = 16.sp,
                                                        color = Color.Red,
                                                        modifier = Modifier
                                                    )
                                                }
                                            }
                                        }else{
                                            androidx.compose.material3.Text(
                                                text = getText(post.post.content),

                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                                Column(
                                    modifier = Modifier.weight(1f)
                                ){
                                    var likeNumber by remember { mutableIntStateOf(post.post.likes.toInt()) }

                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(30.dp),
                                        horizontalArrangement = Arrangement.Start
                                    ){
                                        IconButton(
                                            onClick = {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    try {
                                                        val likeInformation = LikeInformation(
                                                            currentUser.id,
                                                            currentUser.userName,
                                                            currentUser.avatar
                                                        )
                                                        val response = RetrofitClient.getInstance(context).likePost(post.post.id, likeInformation)
                                                        withContext(Dispatchers.Main) {
                                                            if (response.isSuccessful) {
                                                                Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                                                                likeNumber = response.body()?.likes?.toInt() ?: likeNumber
                                                            } else {
                                                                Toast.makeText(context, "It was not possible to like", Toast.LENGTH_SHORT).show()
                                                                likeNumber = response.body()?.likes?.toInt() ?: likeNumber
                                                            }
                                                        }
                                                    } catch (e: Exception){
                                                        withContext(Dispatchers.Main){
                                                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }
                                            },

                                            ) {
                                            androidx.compose.material3.Icon(
                                                Icons.Default.ArrowCircleUp,
                                                contentDescription = "Abrir menú",
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                        androidx.compose.material3.Text(
                                            text = likeNumber.toString(),
                                            fontSize = 20.sp,
                                            modifier = Modifier
                                                .clickable {

                                                }
                                                .padding(top = 2.dp),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1.2f)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.weight(0.2f)
                                ){

                                }

                                Row(
                                    modifier = Modifier.weight(0.5f)
                                ){
                                    androidx.compose.material3.Text(
                                        text = post.post.postType,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                Row(
                                    modifier = Modifier.weight(3f)
                                ){
                                    AsyncImage(
                                        model = currentUser.ip + post.post.mediaImg,
                                        contentDescription = "Imagen del medio",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                }
                                Row(
                                    modifier = Modifier.weight(0.2f)
                                ){

                                }

                                Row(
                                    modifier = Modifier.weight(0.5f)
                                ){
                                    androidx.compose.material3.Text(
                                        text = timeAgo(LocalDateTime.parse(post.post.date)),
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }

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
