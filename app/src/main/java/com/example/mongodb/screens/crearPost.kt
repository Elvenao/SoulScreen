package com.example.mongodb.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.Multimedia
import com.example.mongodb.model.Post
import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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
    var pelicula by remember { mutableStateOf("") }
    var peliculas = remember { mutableListOf<Multimedia>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear nuevo post", color = MaterialTheme.colorScheme.onPrimary)},
                backgroundColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(80.dp)
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Top,
        ) {
            item {
                Text("Titulo del post.", modifier = Modifier.padding(bottom = 8.dp), color = MaterialTheme.colorScheme.onBackground)
                TextField(
                    value = title,
                    onValueChange = { if (it.length <= 80) title = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(MaterialTheme.colorScheme.surface),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${title.length} / 80",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("¿A que contenido te refieres?", modifier = Modifier.padding(bottom = 8.dp), color = MaterialTheme.colorScheme.onPrimary)

                Seleccionado()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Contenido del post", modifier = Modifier.padding(bottom = 8.dp),color = MaterialTheme.colorScheme.onPrimary)
                TextField(
                    value = content,
                    onValueChange = { if(it.length<=300) content=it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                )
                {
                    Text(
                        text = "${content.length} / 300",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Selecciona el tipo de post.", modifier = Modifier.padding(bottom = 8.dp)
                    .background(MaterialTheme.colorScheme.background), color = MaterialTheme.colorScheme.onPrimary)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = seleccion,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        opciones.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(
                                    opcion,
                                    color = MaterialTheme.colorScheme.onPrimary) },
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
                            id =null,
                            user =currentUserData?.userName ?: "",
                            userId =currentUserData?.id ?: "",
                            title =title,
                            content =content,
                            date = fecha,
                            time = time,
                            mediaId ="0",
                            mediaImg = "/Images/Movies/no_photo.jpg",
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
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text("Publicar", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}


@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun BuscadorPeliculas(
    onSeleccionar: (String) -> Unit
) {
    // 1. FocusRequester y KeyboardController
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 2. Estado de la búsqueda
    var query by remember { mutableStateOf("") }
    val peliculas = remember { mutableStateListOf<Multimedia>() }
    var expanded by remember { mutableStateOf(false) }

    // 3. Lógica de llamada a la API

    fun buscarPeliculas(input: String) {
        // sólo lanza la búsqueda si hay texto
        if (input.isBlank()) return
        // cancelable coroutine ligada al scope de composición
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = RetrofitClient.instance.getMoviesBusqueda(input)
                if (resp.isSuccessful) {
                    val lista = resp.body().orEmpty()
                    peliculas.clear()
                    peliculas.addAll(lista)
                    expanded = lista.isNotEmpty()
                }
            } catch (e: Exception) {
                Log.e("Buscador", "error: ${e.message}")
            }
        }
    }

    // 4. UI
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = it
            // si abrimos manualmente, pedimos foco y mostramos teclado
            if (it) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { text ->
                query = text
                if (text.isBlank()) {
                    peliculas.clear()
                    expanded = false
                } else {
                    buscarPeliculas(text)
                }
            },
            label = { Text("Buscar película", color = MaterialTheme.colorScheme.onPrimary) },
            modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary)
                           // aquí se ancla el dropdown
                .focusRequester(focusRequester),
            trailingIcon = {
               
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                // aquí defines el color de fondo del campo
                backgroundColor = MaterialTheme.colorScheme.surface,
                // color del texto y del placeholder
                textColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),


        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background)

        ) {
            peliculas.forEach { película ->
                DropdownMenuItem(
                    text = { Text(película.name, color = Color.White) },
                    onClick = {
                        query = película.name
                        onSeleccionar(película.name)
                        expanded = false
                    },
                    
                    
                )
            }
        }
    }
}

@Composable
fun Seleccionado() {
    var peliculaSeleccionada by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // TextField que solo muestra la selección


        // Aquí está el buscador en sí, que mostrará el dropdown

        BuscadorPeliculas(
            onSeleccionar = { seleccion ->
                peliculaSeleccionada = seleccion
            }
        )
        

        Spacer(modifier = Modifier.height(8.dp))
    }
}
