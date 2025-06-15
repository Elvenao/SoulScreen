package com.example.mongodb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.mongodb.ui.theme.DarkCyan


import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility

import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.Icon
import androidx.compose.material.IconButton

import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction



@Composable
fun signUp_Email(navController: NavController, nombre: String, apellidos: String, birthDate: String){
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize().background(Color.Black)){
        Box(Modifier.fillMaxSize()){
            Column(modifier = Modifier.align(Alignment.TopStart).padding(start = 20.dp, top = 20.dp, end = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.align(Alignment.Start)) {
                    Text(
                        "¿Cual es tu correo electrónico?",
                        fontSize = 27.sp,
                        textAlign = TextAlign.Left,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                }
                Row(Modifier.align(Alignment.Start)) {
                    Text(
                        "Ingresa tu correo electronico y crea una contraseña",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Left,
                        color = Color.White,
                    )
                }
                Row(Modifier.align(Alignment.Start).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {email = it},
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                        label = { Text("Correo Electronico", color = Color.White)},
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            color = Color.White
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Cyan,
                            unfocusedBorderColor = Color.Gray,
                            textColor = Color.White,
                            cursorColor = Color.Cyan
                        )
                    )

                    
                }
                Row(Modifier.align(Alignment.Start).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = {password = it},
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                        label = { Text("Contraseña", color = Color.White)},
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = icon, contentDescription = description, tint = Color.White)
                            }
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            color = Color.White
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Cyan,
                            unfocusedBorderColor = Color.Gray,
                            textColor = Color.White,
                            cursorColor = Color.Cyan
                        )
                    )
                }
                Row(Modifier.align(Alignment.Start).fillMaxWidth()){
                    Button(onClick = {
                        navController.navigate("")
                    },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Cyan,
                            disabledContainerColor = DarkCyan
                        ),
                        enabled = isEmptyEmail(email)
                        ) {
                        Text("Siguiente", fontSize = 15.sp, color = Color.Black)
                    }
                }

            }
        }
    }
}

fun isEmptyEmail(email: String): Boolean{
    return !email.isNullOrEmpty()
}