package com.example.mongodb.screens

import android.content.Context
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button


import androidx.compose.material.OutlinedTextField

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ButtonDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.mongodb.network.RetrofitClient
import com.example.mongodb.ui.theme.DarkCyan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.example.mongodb.model.LoginRequest
import retrofit2.HttpException
import kotlin.math.log

@Composable
fun logIn(navController: NavController){
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    var loggedIn by rememberSaveable { mutableStateOf(false) }
    Box(Modifier.fillMaxSize().background(Color.Cyan)){
        Box(Modifier.fillMaxSize().background(Color.Black).padding(horizontal = 20.dp, vertical = 35.dp)) {
            Column(Modifier.padding(start=10.dp, end=10.dp).align(Alignment.Center), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                Row(Modifier.align(Alignment.CenterHorizontally).padding(bottom = 20.dp)) {
                    Text("SoulScreen", fontSize = 40.sp, color = Color.White)
                }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Text("¡Bienvenido!", fontSize = 26.sp, color = Color.White)
                }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Usuario", fontSize = 26.sp, color = Color.White)
                }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {email = it},
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(60.dp),
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
                }


                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Contraseña", fontSize = 26.sp, color = Color.White)
                }

                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = {password = it},
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(60.dp),
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
                }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Button(
                        onClick = {
                            IniciarSesion(email, password, context, navController)

                        },
                        enabled = isEmpty(email,password),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(

                            containerColor = Color.Cyan,
                            disabledContainerColor =  DarkCyan
                            

                        ),
                    ) {
                        Text("Iniciar Sesion", color = Color.Black)
                    }
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
fun IniciarSesion(email: String, password: String, context: Context, navController: NavController): Boolean{
    val loginRequest = LoginRequest(email,password)
    var errorMessage = ""
    var result: Boolean = false
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitClient.instance.logIn(loginRequest)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.success) {
                        // Inicio de sesión exitoso
                        // Aquí podrías guardar el token, navegar a otra pantalla, etc.

                        Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()
                        navController.navigate("Posts")
                        errorMessage = ""
                        result = true
                    }else{
                        errorMessage = loginResponse?.message ?: "Error desconocido"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    errorMessage = "Credenciales incorrectas"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
                
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.e("LOGIN", "Excepción: ${e.message}")
                // Captura la respuesta cruda si existe
                if (e is HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e("LOGIN", "Respuesta no JSON: $errorBody")
                }
                
            }
        }
    }
    return result
}

fun isEmpty(email: String, password: String):Boolean{
    if(!email.isEmpty() && !password.isEmpty()) return true
    else return false
}