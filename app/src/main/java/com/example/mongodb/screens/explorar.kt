package com.example.mongodb.screens

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.Multimedia
import com.example.mongodb.model.Usuario
import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun explorar(navController: NavController){
     val busqueda = remember { mutableStateOf("") }
     val Multimedia = remember { mutableStateOf<List<Multimedia>>(emptyList()) }
     var selectedIndex by remember { mutableStateOf(1) }
     val usuarios = remember { mutableStateOf<List<Usuario>>(emptyList()) }
    val context = LocalContext.current
    val EncryptedSharedPreferences = SecurePrefs(context)
    val currentUserData = EncryptedSharedPreferences.getCurrentUserData()


    Scaffold(
        topBar = {
            MyTopBar(
                onBackClick = {
                    navController.navigate("Posts") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSkipClick = {

                },
                true,
                true,
                true,
                "SoulScreen",
                null,
                true
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
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
                                1 -> {}
                                2 -> navController.navigate("GustosScreen")
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = label,
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        label = {
                            Text(
                                label,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
                modifier = Modifier
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .fillMaxSize()


            ) {
                Column(
                    modifier = Modifier
                        
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            value = busqueda.value,
                            onValueChange = {
                                busqueda.value = it
                                if (busqueda.value.startsWith("@")) {
                                    val input = busqueda.value.removePrefix("@")
                                    Multimedia.value = emptyList()
                                    usuarios.value = emptyList()
                                    buscarUsuarios(input, usuarios)

                                }else{
                                    Multimedia.value = emptyList()
                                    usuarios.value = emptyList()
                                    buscarPeliculas(busqueda.value,Multimedia)

                                }
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                                focusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
                                unfocusedBorderColor = Color.Gray,
                                textColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                                cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomEnd = 12.dp,
                                bottomStart = 12.dp
                            )

                        )

                    }
                    if (Multimedia.value.isNotEmpty()) {
                        LazyColumn() {
                            items(Multimedia.value) { multimedia ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .background(androidx.compose.material3.MaterialTheme.colorScheme.tertiary)
                                        .padding(10.dp)
                                        .clickable {
                                            navController.navigate("DetalleMediaScreen/${multimedia.id}") // Navegar a detalle de la película
                                        },
                                    verticalAlignment = Alignment.CenterVertically,

                                ) {
                                    // Imagen a la izquierda
                                    AsyncImage(
                                        model = "${currentUserData.ip}${multimedia.poster}",
                                        contentDescription = "Imagen del medio",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .width(90.dp)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(8.dp))
                                    )

                                    Spacer(modifier = Modifier.width(12.dp)) // espacio entre imagen y texto

                                    // Columna con la información a la derecha
                                    Column(
                                        modifier = Modifier.fillMaxHeight(),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Row {
                                            Text(
                                                text = multimedia.name,
                                                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                                fontSize = 20.sp
                                            )
                                        }
                                        Row {
                                            Text(
                                                text = multimedia.descripcion,
                                                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                                            )
                                        }

                                        // Puedes añadir más info aquí (fecha, descripción, etc.)
                                    }
                                }
                                Spacer(Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.background).height(3.dp))
                            }
                        }
                    } else if (usuarios.value.isNotEmpty()) {
                        LazyColumn() {
                            items(usuarios.value) { usuario ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .background(androidx.compose.material3.MaterialTheme.colorScheme.tertiary)
                                        .padding(10.dp)
                                        .clickable {
                                            if(usuario.id == currentUserData.id){
                                                navController.navigate("ProfileScreen")
                                            }else{
                                                navController.navigate("seeprofileuser/${usuario.id}")
                                            }
                                            
                                        },
                                    verticalAlignment = Alignment.CenterVertically,

                                    ) {
                                    // Imagen a la izquierda
                                    AsyncImage(
                                        model = "${currentUserData.ip}${usuario.avatar}",
                                        contentDescription = "Imagen del medio",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .width(90.dp)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(8.dp))
                                    )

                                    Spacer(modifier = Modifier.width(12.dp)) // espacio entre imagen y texto

                                    // Columna con la información a la derecha
                                    Column(
                                        modifier = Modifier.fillMaxHeight(),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.Top) {
                                            Text(
                                                text = "@"+usuario.userName,
                                                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                                fontSize = 20.sp
                                            )
                                        }
                                        Row {
                                            val seguidores = usuario.followers?.size ?: 0
                                            val siguiendo = usuario.following?.size ?: 0
                                            Text(
                                                text = "Seguidores: $seguidores  | ",
                                                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                                fontSize = 18.sp
                                            )
                                            Text(
                                                text = " Siguiendo: $siguiendo",
                                                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                                fontSize = 18.sp
                                            )
                                        }


                                        // Puedes añadir más info aquí (fecha, descripción, etc.)
                                    }
                                }

                                Spacer(Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.background).height(3.dp))
                            }
                        }
                    }else{
                        Box(){
                            
                        }
                    }


                }

            }

    }



    }

@OptIn(UnstableApi::class)
fun buscarUsuarios(input: String, Usuarios: MutableState<List<Usuario>>){
    if (input.isBlank()) return
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val resp = RetrofitClient.instance.getUsuariosBusqueda(input)
            if (resp.isSuccessful) {
                val lista = resp.body().orEmpty()
                if (lista.isNullOrEmpty()) {
                    // No hubo resultados → limpiar la lista
                    Usuarios.value = emptyList()
                } else {
                    // Sí hubo resultados → mostrar lista
                    Usuarios.value = lista
                }
                
            }
        } catch (e: Exception) {
            Log.e("Buscador", "error: ${e.message}")
        }
    }
}

@OptIn(UnstableApi::class)
fun buscarPeliculas(input: String, Multimedia: MutableState<List<Multimedia>>) {
    // sólo lanza la búsqueda si hay texto
    if (input.isBlank()) return
    // cancelable coroutine ligada al scope de composición
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val resp = RetrofitClient.instance.getMoviesBusqueda(input)
            if (resp.isSuccessful) {
                val lista = resp.body().orEmpty()
                if (lista.isNullOrEmpty()) {
                    // No hubo resultados → limpiar la lista
                    Multimedia.value = emptyList()
                } else {
                    // Sí hubo resultados → mostrar lista
                    Multimedia.value = lista
                }



            }
        } catch (e: Exception) {
            Log.e("Buscador", "error: ${e.message}")
        }
    }
}

