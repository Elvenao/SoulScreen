package com.example.mongodb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun logIn(){
    var user by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    Box(Modifier.fillMaxSize().background(Color.Cyan)){
        Box(Modifier.fillMaxSize().background(Color.White).padding(horizontal = 20.dp, vertical = 35.dp)) {
            Column(Modifier.padding(start=10.dp, end=10.dp).align(Alignment.Center)) {

                Row(Modifier.align(Alignment.CenterHorizontally).padding(bottom = 20.dp)) {
                    Text("SoulScreen", fontSize = 40.sp)
                }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Text("¡Bienvenido!", fontSize = 26.sp)
                }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Usuario", fontSize = 26.sp)
                }
                TextField(
                    value = user,
                    onValueChange = {
                        user = it
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .padding(start = 15.dp, end = 15.dp, top = 20.dp),
                    textStyle = TextStyle(
                        fontSize = 20.sp
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),

                )

                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Contraseña", fontSize = 26.sp)
                }
                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .padding(start = 15.dp, end = 15.dp, top = 20.dp),

                )
            }
        }
    }
}