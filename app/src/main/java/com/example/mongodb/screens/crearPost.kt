package com.example.mongodb.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.Post
import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.String

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun crearPost(navController: NavController) {
    //Todo: Validar textos, que no esten vacios, que el titulo no sea muy largo, que el contenido no sea muy largo, que la opcion seleccionada sea valida y datos del multimedia
    val EncryptedSharedPreferences = SecurePrefs(LocalContext.current)
    val currentUserData = EncryptedSharedPreferences.getCurrentUserData()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val opciones = listOf("SPOILER", "Opinion", "Meme","Consejos","Pregunta","Debate","Noticia", "Curiosidad","Otro")
    var seleccion by remember { mutableStateOf(opciones[0]) }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear nuevo post") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Text("Titulo del post.", modifier = Modifier.padding(bottom = 8.dp))
                TextField(
                    value = title,
                    onValueChange = { if (it.length <= 80) title = it },
                    label = { Text("Título") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${title.length} / 80",
                        style = MaterialTheme.typography.caption
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("¿A que contenido te refieres?", modifier = Modifier.padding(bottom = 8.dp))
                TextField(
                    value = "Pelicula 1",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Media") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Contenido del post", modifier = Modifier.padding(bottom = 8.dp))
                TextField(
                    value = content,
                    onValueChange = { if(it.length<=300) content=it },
                    label = { Text("¿Qué estás pensando?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                )
                {
                    Text(
                        text = "${content.length} / 300",
                        style = MaterialTheme.typography.caption
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Selecciona el tipo de post.", modifier = Modifier.padding(bottom = 8.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = seleccion,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selecciona un tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        opciones.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    seleccion = opcion
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if(title.isEmpty() || content.isEmpty()) {
                            Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val fechaHoraActual = LocalDateTime.now()
                        val fechaHoraFormateada = fechaHoraActual.format(formato)
                        val fecha = fechaHoraFormateada.split(" ")[0]
                        val time = fechaHoraFormateada.split(" ")[1]
                        val post = Post(
                            id=null,
                            user=currentUserData?.userName ?: "",
                            userId=currentUserData?.id ?: "",
                            title=title,
                            content=content,
                            date= fecha,
                            time= time,
                            userImg =  currentUserData?.avatar.toString(),
                            mediaId="0",
                            mediaImg = "/Images/no_photo.jpg",
                            postType = seleccion,
                            comments = emptyList()
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = RetrofitClient.getInstance(context).crearPost(post)
                                withContext (Dispatchers.Main){
                                    if (response.body()?.success ?: false){
                                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    }
                                }
                            }catch (e: Exception) {
                                 Log.d("Error", e.toString())
                            }
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Publicar")
                }
            }
        }
    }
}