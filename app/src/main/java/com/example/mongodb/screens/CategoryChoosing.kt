package com.example.mongodb.screens
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import com.example.mongodb.model.Category
import com.example.mongodb.model.Post
import com.example.mongodb.network.RetrofitClient
import com.example.mongodb.ui.theme.DarkCyan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.UpdateCategoriesRequest
import com.google.accompanist.flowlayout.FlowRow
import org.json.JSONArray

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun categoryChoosing(navController: NavController){
    //var categories by remember { mutableStateOf(emptyList()) }
    val categories = remember { mutableStateOf<List<Category>>(emptyList())}
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val categoriesSelected = remember { mutableStateOf<List<String>>(emptyList()) }
    val selectedStates = remember { mutableStateListOf<Boolean>()  }


    LaunchedEffect(Unit) {
        cargarCategories(categories, errorMessage, context)
    }

    LaunchedEffect(categories.value) {
        selectedStates.clear()
        selectedStates.addAll(List(categories.value.size) { false })
    }
    Box(Modifier.fillMaxSize().background(Color.Black)){

        Scaffold(
                topBar = {
                    MyTopBar(
                        onBackClick = { /* Acción para volver */ },
                        onSkipClick = {
                            navController.navigate("Posts"){
                                popUpTo(0){inclusive = true}
                            }
                        } ,
                        true,
                        true,
                        true,
                        "¿Qué te gusta?",
                        "Omitir"
                    )
                }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize() // Ocupa toda la pantalla
            ) {
                Column(modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)) {
                    FlowRow(
                        //modifier = Modifier
                        //    .fillMaxWidth()
                        //    .padding(16.dp),
                        mainAxisSpacing = 8.dp,
                        crossAxisSpacing = 8.dp
                    ) {
                        categories.value.forEachIndexed { index, category ->
                            val isSelected = selectedStates.getOrNull(index) == true
                            Button(
                                onClick = {
                                    selectedStates[index] = !isSelected
                                    val currentList = categoriesSelected.value
                                    val categoryName = category.category ?: return@Button

                                    categoriesSelected.value = if (selectedStates[index]) {
                                        // Agregar si está seleccionada y no está en la lista
                                        if (categoryName !in currentList) currentList + categoryName else currentList
                                    } else {
                                        // Quitar si se deseleccionó
                                        currentList - categoryName
                                    }
                                    Log.e("Lista", categoriesSelected.value.toString())
                                },
                                shape = RoundedCornerShape(20.dp), // Define la forma aquí
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color.Black else Color.Transparent,
                                    contentColor = Color.Black
                                ),
                                border = BorderStroke(1.dp, Color.Black), // Si quieres borde
                                modifier = Modifier
                                    .defaultMinSize(
                                        minHeight = 0.dp,
                                        minWidth = 0.dp
                                    ) // Opcional para evitar padding extra
                            ) {
                                category.category?.let {
                                    Text(
                                        it,
                                        color = if (isSelected) Color.White else Color.Black,
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                saveCategories(categoriesSelected.value,context,navController)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Cyan,
                                disabledContainerColor = DarkCyan
                            ),

                            ) {
                            Text("Guardar", color = Color.Black, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
fun saveCategories(categories: List<String>, context: Context, navController: NavController){
    val securePrefs = SecurePrefs(context)
    val id = securePrefs.getCurrentUserData()
    val categorias = UpdateCategoriesRequest(categories)
    Log.d("ERRORSOTE", "SIII")
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitClient.getInstance(context).updateCategories(id.id,categorias)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("ERRORSOTE", "SIIIUUU3")
                    if (body != null) {
                        Toast.makeText(context, body.message, Toast.LENGTH_SHORT).show()
                    }
                    navController.navigate("Posts"){
                        popUpTo(0){inclusive = true }
                    }
                } else {
                    response.body()?.let { Log.d("ERRORSOTE", it.message) }
                }

            }
        } catch (e :Exception){
            e.message?.let { Log.d("ERRORSOTE", it) }
            Log.d("ERRORSOTE", "SIIIUUU")
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
fun cargarCategories(categories: MutableState<List<Category>>, errorMessage: MutableState<String?>,context: Context) {

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitClient.getInstance(context).getCategories().execute()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    categories.value = response.body() ?: emptyList()
                    Log.d("ADEROR", "Nose que pasa")
                    errorMessage.value = null
                } else {
                    errorMessage.value = "Error ${response.code()}"
                    Log.d("ADEROR", "Adios")
                }

            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorMessage.value = "Fallo: ${e.message}"
                Log.d("ADEROR", errorMessage.value!!)

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    onBackClick: () -> Unit,
    onSkipClick: () -> Unit,
    backActivated: Boolean,
    skipActivated: Boolean,
    titleActivated: Boolean,
    titleText: String?,
    skipText: String?
) {
    TopAppBar(
        title = {
            if(titleActivated){
                Box(Modifier.fillMaxWidth()) {
                    if (titleText != null) {
                        Text(
                            text = titleText,
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White,
                            fontSize = 25.sp
                        )
                    }
                }
            }

        },

        navigationIcon = {
            if(backActivated){
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
            }

        },
        actions = {
            if(skipActivated){
                OutlinedButton(
                    onClick = onSkipClick,
                    border = BorderStroke(1.dp, Color.Black),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    if (skipText != null) {
                        Text(skipText, color = Color.Black)
                    }
                }
            }

        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        )
    )
}


