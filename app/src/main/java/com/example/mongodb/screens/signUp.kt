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

import androidx.compose.material3.TextField
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


import androidx.compose.material.Text
import androidx.compose.material.TextField


@Composable
fun signUp(navController: NavController){
    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    Box(Modifier.fillMaxSize().background(Color.Black)){
        Box(Modifier.fillMaxSize()){
            Column(modifier = Modifier.align(Alignment.TopStart).padding(start = 20.dp, top = 20.dp, end = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.align(Alignment.Start)) {
                    Text(
                        "¿Cómo te llamas?",
                        fontSize = 27.sp,
                        textAlign = TextAlign.Left,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                }
                Row(Modifier.align(Alignment.Start)) {
                    Text(
                        "Ingresa tu nombre completo.",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Left,
                        color = Color.White,
                        )
                }
                Row(Modifier.align(Alignment.Start).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {name = it},
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                        label = { Text("Nombre", color = Color.White)},
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
                    OutlinedTextField(
                        value = surname,
                        onValueChange = {surname = it},
                        label = { Text("Apellidos", color = Color.White) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)  ,
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
                   Button(onClick = {},
                       modifier = Modifier.weight(1f),
                       colors = ButtonDefaults.buttonColors(
                           containerColor = Color.Cyan
                       ),

                       ) {
                       Text("Siguiente", fontSize = 15.sp, color = Color.Black)
                   }
                }

            }
        }
    }
}