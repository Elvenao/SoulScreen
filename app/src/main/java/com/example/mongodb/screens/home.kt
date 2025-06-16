package com.example.mongodb.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getCurrentCompositionErrors
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.security.crypto.EncryptedSharedPreferences
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.Post
import com.example.mongodb.model.RefreshTokenRequest
import com.example.mongodb.model.Usuario
import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import coil.compose.AsyncImage
import com.example.mongodb.BottomBar


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Home(navController:NavController) {
    val usuarios = remember { mutableStateOf<List<Usuario>>(emptyList()) }
    val posts = remember { mutableStateOf<List<Post>>(emptyList())}
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isRefreshing = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val encryptedSharedPreferences = remember {
        if (isPreview) null else SecurePrefs(context)
    }
    val refreshToken = encryptedSharedPreferences?.getRefreshToken()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val EncryptedSharedPreferences = SecurePrefs(context)
    val currentUserData = EncryptedSharedPreferences.getCurrentUserData()
    var selectedIndex by remember { mutableStateOf(0) }


    fun cargarUsuarios() {
        isRefreshing.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.getInstance(context).getUsuarios().execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        usuarios.value = response.body() ?: emptyList()
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

    fun cargarPosts() {

        isRefreshing.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if(refreshToken != null){
                    val responseToken = RetrofitClient.getInstance(context)
                        .refreshToken(RefreshTokenRequest(refreshToken))
                        .execute()

                    if (responseToken.isSuccessful && responseToken.body() != null) {
                        val tokenResponse = responseToken.body()!!
                        // Guarda los nuevos tokens
                        encryptedSharedPreferences?.saveAccessToken(tokenResponse.accessToken)
                        encryptedSharedPreferences?.saveRefreshToken(tokenResponse.refreshToken)
                        val response = RetrofitClient.getInstance(context).getPosts().execute()
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                posts.value = response.body() ?: emptyList()
                                errorMessage.value = null

                            } else {
                                errorMessage.value = "Error ${response.code()}"
                                Toast.makeText(context, errorMessage.value, Toast.LENGTH_SHORT).show()

                            }
                            isRefreshing.value = false
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            navController.navigate("welcomeScreen") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                errorMessage.value = "HOla"
                isRefreshing.value = false
            }



        }
    }

    LaunchedEffect(Unit) {
        cargarPosts()
        //cargarUsuarios()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = { cargarPosts() }
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxHeight(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clickable {
                                    navController.navigate("profileScreen") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                        ) {
                            AsyncImage(
                                model = currentUserData.avatar,
                                contentDescription = "Imagen desde URL",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .padding(8.dp)
                            )
                            Column {
                                Text(
                                    text = currentUserData.name,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                                Text(
                                    text = "@${currentUserData.userName}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "n Seguidores",
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "n Seguidos",
                                fontSize = 15.sp,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }



                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clickable{
                                navController.navigate("profileScreen") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "UserIcon",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(24.dp)
                            )
                            Text(
                                text = "Perfil",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clickable{
                                Toast.makeText(context, "Configuracion", Toast.LENGTH_SHORT).show()
                                /*
                                navController.navigate("Configuracion") {
                                    popUpTo(0) { inclusive = true }
                                }*/
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings Icon",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(24.dp)
                            )
                            Text(
                                text = "Configuración",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clickable{
                                Toast.makeText(context, "Ayuda", Toast.LENGTH_SHORT).show()
                            }
                        ){
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Help Icon",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(24.dp)
                            )
                            Text(
                                text = "Ayuda",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clickable{
                                Toast.makeText(context, "Soporte", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Support Icon",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(24.dp)
                            )
                            Text(
                                text = "Contacta con Soporte",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                    item{
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    cerrarSesion(context, navController)
                                }
                                .padding(4.dp,0.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Cerrar Sesión",
                                    fontSize = 15.sp,
                                    color = Color.Red,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        }
                    }

                }
            }

        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) { Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("") },
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        drawerState.open() // <- abre el drawer
                                    }
                                }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                                }
                            }
                        )
                    },
                    bottomBar = {
                        BottomBar(selectedIndex) { index ->
                            selectedIndex = index
                            when (index) {
                                0 -> navController.navigate("Posts")
                                1 -> navController.navigate("amigosScreen")
                                2 -> navController.navigate("explorarScreen")
                            }
                        }
                    }
                ) { innerPadding ->
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

                                Text(text = "Error al conectarse. Revisa tu conexión a internet.", fontSize = 18.sp)
                            }
                        }

                    } else {
                    LazyColumn(contentPadding = innerPadding,
                        modifier = Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState())
                    ) {
                        items(posts.value) { post ->
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .background(Color(0xFFE0F7FA))
                                    .padding(16.dp)
                                    .clickable {

                                    }
                            ) {
                                Column {
                                    Text(
                                        text = "${post.user}",
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "${post.title}",
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "${post.content}",
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "${post.date}",
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "${post.time}",
                                        fontSize = 20.sp,
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
}

fun cerrarSesion(context: Context,navController: NavController){
    Toast.makeText(context, "Cerrando Sesion", Toast.LENGTH_SHORT).show()
    val sharedPreferences = SecurePrefs(context)
    sharedPreferences.clearAccessToken()
    sharedPreferences.clearRefreshToken()
    navController.navigate("welcomeScreen"){
        popUpTo(0) { inclusive = true }
    }
}

@Preview
@Composable
fun prev () {
    val nav : NavHostController= rememberNavController()
    Home(nav)
}