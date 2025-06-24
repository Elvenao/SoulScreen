package com.example.mongodb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun welcomeScreen(navController: NavController){
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)){
        Box(Modifier
            .fillMaxSize() // Esto es lo importante
            .padding(horizontal = 25.dp, vertical = 70.dp)
        ){
            Column(Modifier.align(Alignment.Center)) {
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Entérate de las opiniones que tienen tus amigos acerca del contenido multimedia", fontSize = 27.sp, textAlign = TextAlign.Left, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }

            }
            Column(Modifier.align(Alignment.BottomCenter)){
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Button(onClick = {
                        navController.navigate("logIn")
                    },
                        Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.inverseSurface
                        )
                    ) {
                        Text("Iniciar Sesión", color =MaterialTheme.colorScheme.background, fontSize = 15.sp)
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    // Línea izquierda
                    Divider(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.inverseSurface
                    )

                    // Texto "o"
                    Text(
                        "o",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.inverseSurface
                    )

                    // Línea derecha
                    Divider(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Button(onClick = {
                        navController.navigate("signUp")
                    },
                        Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("Crear Cuenta", color = MaterialTheme.colorScheme.background, fontSize = 15.sp)
                    }
                }
            }

        }
    }
}