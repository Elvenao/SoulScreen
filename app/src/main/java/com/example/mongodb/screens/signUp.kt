package com.example.mongodb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun signUp_Name(navController: NavController){
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellidos by rememberSaveable { mutableStateOf("") }
    Box(Modifier.fillMaxSize().background(Color.Black)){
        Scaffold(
            topBar = {
                MyTopBar(
                    onBackClick = { navController.navigate("welcomeScreen"){
                        popUpTo(0){inclusive = true}
                    } },
                    onSkipClick = {

                    } ,
                    true,
                    false,
                    false,
                    null,
                    null
                    ,true
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(Modifier.align(Alignment.Start)) {
                        Text(
                            "¿Cómo te llamas?",
                            fontSize = 27.sp,
                            textAlign = TextAlign.Left,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )

                    }
                    Row(Modifier.align(Alignment.Start)) {
                        Text(
                            "Ingresa tu nombre completo.",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Left,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    Row(
                        Modifier.align(Alignment.Start).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { newText ->
                                // Elimina solo los espacios al final
                                val trimmed = newText.replace(Regex("\\s+$"), "")
                                nombre = trimmed
                            },

                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f),
                            label = { Text("Nombre(s)", color = MaterialTheme.colorScheme.onPrimary) },
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                unfocusedBorderColor = Color.Gray,
                                textColor = MaterialTheme.colorScheme.onPrimary,
                                cursorColor = MaterialTheme.colorScheme.secondary
                            ),
                            singleLine = true,
                        )

                    }
                    Row(
                        Modifier.align(Alignment.Start).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = apellidos,
                            onValueChange = { newText ->
                                // Elimina solo los espacios al final
                                val trimmed = newText.replace(Regex("\\s+$"), "")
                                apellidos = trimmed
                            },
                            label = { Text("Apellidos", color = MaterialTheme.colorScheme.onPrimary) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                unfocusedBorderColor = Color.Gray,
                                textColor = MaterialTheme.colorScheme.onPrimary,
                                cursorColor = MaterialTheme.colorScheme.secondary
                            ),
                            singleLine = true,
                        )
                    }
                    Row(Modifier.align(Alignment.Start).fillMaxWidth()) {
                        Button(
                            onClick = {
                                navController.navigate("signUp_BirthDate/${nombre}/${apellidos}")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                disabledContainerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            enabled = isEmpty(nombre, apellidos),


                            ) {
                            Text("Siguiente", fontSize = 15.sp, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }

                }
            }
        }
    }
}

