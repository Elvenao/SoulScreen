package com.example.mongodb.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mongodb.SecurePrefs

@Composable
fun changePass(navController: NavController) {
    var newContraseña by remember { mutableStateOf("") }
    var newConfirmContraseña by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var context = LocalContext.current

    val EncryptedSharedPreferences = SecurePrefs(context)
    val currentUserData = EncryptedSharedPreferences.getCurrentUserData()

    BackHandler {
        navController.navigate("ConfiguracionScreen") {
            popUpTo("ConfiguracionScreen") { inclusive = true }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { androidx.compose.material.Text("Cambiar Contraseña", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary) },
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
                Spacer(modifier = Modifier.height(15.dp))
            }

            item {
                Text("Nueva Contraseña:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Box{
                    TextField(
                        value = newContraseña,
                        onValueChange = { if (it.length <= 100) newContraseña = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(MaterialTheme.colorScheme.surface),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        androidx.compose.material.Text(
                            text = "${newContraseña.length} / 100",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                }
                Text("Confirmar Nueva Contraseña:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Box{
                    TextField(
                        value = newConfirmContraseña,
                        onValueChange = { if (it.length <= 100) newConfirmContraseña = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(MaterialTheme.colorScheme.surface),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        androidx.compose.material.Text(
                            text = "${newConfirmContraseña.length} / 100",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                }
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(15.dp))
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            /*
                            if (newContraseña.isNotEmpty() && newConfirmContraseña.isNotEmpty()) {
                                if (newContraseña == newConfirmContraseña) {
                                    val userId = currentUserData?.id ?: return@Button
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val response = RetrofitClient.getInstance(context).updatePassword(userId, newContraseña)
                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                                                navController.navigate("ConfiguracionScreen") {
                                                    popUpTo("ConfiguracionScreen") { inclusive = true }
                                                }
                                            } else {
                                                Toast.makeText(context, "Error al actualizar contraseña", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                            }
                            
                             */
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Guardar Cambios", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }




        }
    }
}