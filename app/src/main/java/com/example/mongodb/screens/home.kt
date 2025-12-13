package com.example.mongodb.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.LikeInformation
import com.example.mongodb.model.PostWithAvatar
import com.example.mongodb.model.RefreshTokenRequest
import com.example.mongodb.model.Usuario
import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import kotlin.text.append


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Home(navController:NavController) {
    val usuarios = remember { mutableStateOf<List<Usuario>>(emptyList()) }
    val posts = remember { mutableStateOf<List<PostWithAvatar>>(emptyList())}
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
    val request = ImageRequest.Builder(context)
        .data(currentUserData.avatar + "?t=${System.currentTimeMillis()}")
        .diskCachePolicy(CachePolicy.DISABLED)
        .memoryCachePolicy(CachePolicy.DISABLED)
        .build()

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
            ModalDrawerSheet(modifier = Modifier
                    .width(280.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background),
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
                                model = request,
                                contentDescription = "Imagen desde URL",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(8.dp)
                                    .clip(CircleShape)
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
                                .padding(10.dp)
                                .clickable(
                                    onClick = {
                                        navController.navigate("seguidoresUser/${currentUserData.id}")
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentUserData.followers?.size.toString()+" Seguidores",
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = currentUserData.following?.size.toString()+" Seguidos",
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
                                .clickable {
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
                                .clickable {
                                    navController.navigate("ConfiguracionScreen") {
                                        popUpTo(0) { inclusive = true }
                                    }
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
                                .clickable {
                                    navController.navigate("AyudaScreen") {
                                        popUpTo(0) { inclusive = true }
                                    }
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
                                .clickable {
                                    Toast.makeText(
                                        context,
                                        "Mandar a numero de emilio",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                                .padding(4.dp, 0.dp),
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                        Text(
                            "SoulScreen",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
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
                                            0 -> {}
                                            1 -> navController.navigate("ExplorarScreen")
                                            2 -> navController.navigate("GustosScreen")
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
                    } ,
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("crearPostScreen")
                            },
                            backgroundColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Nuevo post")
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
                    ) {
                        items(posts.value) { post ->
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .height(300.dp)
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
                                                    model = currentUserData.ip + post.userAvatar,
                                                    contentDescription = "Imagen de usuario",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .padding(top = 8.dp)
                                                        .size(52.dp)
                                                        .clip(CircleShape)
                                                )
                                                Text(
                                                    text = "@" + post.post.user,
                                                    fontSize = 18.sp,
                                                    modifier = Modifier
                                                        .padding(top = 10.dp, start = 8.dp)
                                                        .weight(2f), // Da más espacio al usuario, pero deja sitio para la fecha
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Row(

                                            ) {
                                                Text(
                                                    text = post.post.mediaName,
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier
                                                        .weight(2f)
                                                        .padding(top = 8.dp, bottom = 0.dp),

                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Row(){
                                                if(post.post.postType=="SPOILER"){
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxHeight()
                                                    ){
                                                        Row(){
                                                            Text(
                                                                text = "SPOILER ALERT",
                                                                fontSize = 24.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color= Color.Red,
                                                                modifier = Modifier.padding(top=8.dp)
                                                            )
                                                        }
                                                        Row(){
                                                            Text(
                                                                text = "Presiona para ver detalles (Bajo tu propio riesgo)",
                                                                fontSize = 16.sp,
                                                                color = Color.Red,
                                                                modifier = Modifier
                                                            )
                                                        }
                                                    }
                                                }else{
                                                    Text(
                                                        text = getText(post.post.content),
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
                                                                    currentUserData.id,
                                                                    currentUserData.userName,
                                                                    currentUserData.avatar
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
                                                    Icon(Icons.Default.ArrowCircleUp, contentDescription = "Abrir menú")
                                                }
                                                Text(
                                                    text = likeNumber.toString(),
                                                    fontSize = 20.sp,
                                                    modifier = Modifier
                                                        .clickable {

                                                        }
                                                        .padding(top = 2.dp)
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
                                            Text(
                                                text = post.post.postType,
                                                fontSize = 20.sp,
                                            )
                                        }
                                        Row(
                                            modifier = Modifier.weight(3f)
                                        ){
                                            AsyncImage(
                                                model = currentUserData.ip + post.post.mediaImg,
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
                                            Text(
                                                text = timeAgo(LocalDateTime.parse(post.post.date)),
                                                fontSize = 14.sp,
                                            )
                                        }

                                    }
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

fun getText(content: String): AnnotatedString {
    if (content.length < 150) {
        // Si el texto es corto, lo devolvemos como un AnnotatedString sin estilos.
        return AnnotatedString(content)
    } else {
        // Si es largo, construimos el texto con estilos.
        var cutIndex = 150
        for (i in 150 downTo 0) {
            if (content[i] == ' ') {
                cutIndex = i
                break
            }
        }
        val initialText = content.substring(0, cutIndex)

        // Usamos buildAnnotatedString para crear texto con múltiples estilos
        return buildAnnotatedString {
            // 1. Añadimos la primera parte del texto (sin estilo especial)
            append(initialText)
            append(" ") // Añadimos un espacio

            // 2. Añadimos el "Ver mas..." con un estilo de color azul
            withStyle(style = SpanStyle(color = Color.Gray)) {
                append("Ver mas...")
            }
        }
    }
}

fun timeAgo(from: LocalDateTime): String {
    val now = LocalDateTime.now()
    val duration = Duration.between(from, now)

    val seconds = duration.seconds
    val minutes = duration.toMinutes()
    val hours = duration.toHours()
    val days = duration.toDays()

    return when {
        seconds < 60 -> "Justo ahora"
        minutes == 1L -> "Hace 1 minuto"
        minutes < 60 -> "Hace $minutes minutos"
        hours == 1L -> "Hace 1 hora"
        hours < 24 -> "Hace $hours horas"
        days == 1L -> "Ayer"
        days < 7 -> "Hace $days días"
        days < 30 -> "Hace ${days / 7} semanas"
        days < 365 -> "Hace ${days / 30} meses"
        else -> "Hace ${days / 365} años"
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

suspend fun likePost(context: Context, userId: String, userName: String, avatar: String, idPost: String ){

}

@Preview
@Composable
fun prev () {
    val nav : NavHostController= rememberNavController()
    Home(nav)
}