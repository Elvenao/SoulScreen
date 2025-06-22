package com.example.mongodb.screens
import android.content.Context
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun categoryChoosing(navController: NavController){
    //var categories by remember { mutableStateOf(emptyList()) }
    val categories = remember { mutableStateOf<List<Category>>(emptyList())}
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current



    LaunchedEffect(Unit) {
        cargarCategories(categories, errorMessage, context)
    }
    Box(Modifier.fillMaxSize().background(Color.Black)){

        Scaffold(
                topBar = {
                    MyTopBar(
                        onBackClick = { /* Acción para volver */ },
                        onSkipClick = { /* Acción para omitir */ }
                    )
                }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        mainAxisSpacing = 8.dp,
                        crossAxisSpacing = 8.dp
                    ) {
                        categories.value.forEach { category ->
                            Button(
                                onClick = {},
                                modifier = Modifier
                                    .border(1.dp, Color.White, shape = RoundedCornerShape(8.dp))
                                    .background(Color.Transparent),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                category.category?.let { Text(it, color = Color.White) }
                            }
                        }
                    }
                }
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
    onSkipClick: () -> Unit
) {
    TopAppBar(
        title = {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    text = "¿Qué te gusta?",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
        },
        actions = {
            OutlinedButton(
                onClick = onSkipClick,
                border = BorderStroke(1.dp, Color.Black),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text("Omitir", color = Color.Black)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        )
    )
}
