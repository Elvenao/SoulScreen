package com.example.mongodb.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.collection.MutableScatterSet
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.mongodb.SecurePrefs
import com.example.mongodb.core.DatePickerField
import com.example.mongodb.model.Category
import com.example.mongodb.model.CurrentUserData
import com.example.mongodb.model.UpdateCategoriesRequest
import com.example.mongodb.model.UpdateProfileRequest
import com.example.mongodb.network.RetrofitClient
import com.example.mongodb.ui.theme.DarkCyan
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun EditProfile(navController: NavController){
    //var categories by remember { mutableStateOf(emptyList()) }
    val categories = remember { mutableStateOf<List<Category>>(emptyList())}
    val categoriesString = remember { mutableStateOf<List<String>>(emptyList())}

    val EncryptedSharedPreferences = SecurePrefs(LocalContext.current)
    val currentUserData = EncryptedSharedPreferences.getCurrentUserData()
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val genres = currentUserData.genres ?: emptyList()
    val categoriesSelected = remember { mutableStateOf(genres) }
    val selectedStates = remember { mutableStateListOf<Boolean>()  }
    var biography by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        userName = currentUserData.userName
        biography = currentUserData.biography ?: ""
        birthDate = currentUserData.birthDate
        loadInformation(categories, errorMessage, context,categoriesString)
    }
    
    LaunchedEffect(categories.value) {
        selectedStates.clear()
        categoriesString.value.forEach { category ->
            selectedStates.add(
                categoriesSelected.value.contains(category)
            )
        }
    }
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)){

        Scaffold(
                topBar = {
                    MyTopBarProfile(
                        onBackClick = { navController.navigate("profileScreen"){
                            popUpTo(0){inclusive = true}
                        }},
                        onSkipClick = {

                        } ,
                        true,
                        true,
                        true,
                        "Editar Perfil",
                        "     ",
                        true
                    )
                }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize() // Ocupa toda la pantalla
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Biografía",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        TextField(
                            value = biography,
                            onValueChange = { biography = it },
                            label = {
                                Text(
                                    "Añadir Biografía",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .height(150.dp),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 12.sp
                            ),
                            maxLines = 5
                        )
                    }
                    Column {
                        DatePickerField(
                            modifier = Modifier.height(65
                                .dp)
                                .fillMaxWidth(),
                            label = "Fecha de Nacimiento",
                            value = birthDate,
                            onDateSelected = { selectedDate ->
                                birthDate = selectedDate
                            }
                        )
                    }
                    Column {
                        Text(
                            text = "Nombre de Usuario",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        TextField(
                            value = userName,
                            onValueChange = { userName = it },
                            label = {
                                Text(
                                    "Cambiar username",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .height(60.dp),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 12.sp
                            ),
                            maxLines = 5
                        )
                    }
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
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.inverseSurface else Color.Transparent,
                                    contentColor = Color.Black
                                ),
                                border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.secondary), // Si quieres borde
                                modifier = Modifier
                                    .defaultMinSize(
                                        minHeight = 0.dp,
                                        minWidth = 0.dp
                                    ) // Opcional para evitar padding extra
                            ) {
                                category.category?.let {
                                    Text(
                                        it,
                                        color = if (isSelected) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onPrimary,
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
                                saveInformation(categoriesSelected.value,context,navController, biography,birthDate,userName,currentUserData)
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
fun saveInformation(categories: List<String>, context: Context, navController: NavController, biography: String,birthDate: String,userName:String, currentUserData: CurrentUserData){
    val securePrefs = SecurePrefs(context)
    val id = securePrefs.getCurrentUserData()
    val information = UpdateProfileRequest(userName,currentUserData.name,biography,categories,birthDate)
    Log.d("ERRORSOTE", "SIII")
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitClient.getInstance(context).updateProfile(id.id,information)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("ERRORSOTE", "SIIIUUU3")
                    if (body != null) {
                        Toast.makeText(context, body.message, Toast.LENGTH_SHORT).show()
                    }
                    navController.navigate("posts") {
                        popUpTo("posts") { inclusive = true }
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
fun loadInformation(categories: MutableState<List<Category>>, errorMessage: MutableState<String?>,context: Context, categoriesString: MutableState<List<String>>) {

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitClient.getInstance(context).getCategories().execute()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    categories.value = response.body() ?: emptyList()
                    categoriesString.value =
                        categories.value.map { category ->
                            category.category as String
                        }
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
fun MyTopBarProfile(
    onBackClick: () -> Unit,
    onSkipClick: () -> Unit,
    backActivated: Boolean,
    skipActivated: Boolean,
    titleActivated: Boolean,
    titleText: String?,
    skipText: String?,
    centrar: Boolean
) {
    TopAppBar(
        title = {
            if(titleActivated){
                Box(Modifier.fillMaxWidth()) {
                    if (titleText != null) {
                        Text(
                            text = titleText,
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.inverseSurface,
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
                        tint = MaterialTheme.colorScheme.inverseSurface,
                    )
                }
            }

        },
        actions = {
            if (skipActivated) {
                var color: Color
                var otherColor: Color
                if (centrar) {
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    otherColor =  androidx.compose.material3.MaterialTheme.colorScheme.primary
                }
                else{
                    color = Color.White
                    otherColor = Color.Black
                }

                OutlinedButton(
                    onClick = onSkipClick,
                    border = BorderStroke(1.dp, otherColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = androidx.compose.material.ButtonDefaults.outlinedButtonColors(
                        backgroundColor = color
                    )
                ) {
                    if (skipText != null) {
                        Text(skipText, color = Color.Black)
                    }
                }
            }

        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}


