package com.example.mongodb.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.toLowerCase
import androidx.media3.common.util.UnstableApi
import com.example.mongodb.SecurePrefs
import com.example.mongodb.model.LoginRequest
import com.example.mongodb.model.Usuario
import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun signUp_Email(navController: NavController, nombre: String, apellidos: String, birthDate: String, userName : String){
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordCheck by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordVisible2 by remember { mutableStateOf(false) }
    val isRepeated = remember { mutableStateOf(false) }
    var repeteadUserName = rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

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
                if(isRepeated.value){
                    Row(Modifier.align(Alignment.Start)) {
                        Text(
                            repeteadUserName.value,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Left,
                            color = Color.Red,
                        )
                    }
                }
                Row(Modifier.align(Alignment.Start).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {    newText ->
                            // Eliminar todos los espacios antes de asignar
                            email = newText.replace(" ", "") },
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
                        ),
                        singleLine = true,
                    )

                    
                }
                if(password.length < 8){
                    Row(Modifier.align(Alignment.Start).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Contraseña demasiado corta", color = Color.White)
                    }
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
                if(passwordCheck.length < 8){
                    Row(Modifier.align(Alignment.Start).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Contraseña demasiado corta", color = Color.White)
                    }
                }
                Row(Modifier.align(Alignment.Start).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = passwordCheck,
                        onValueChange = {passwordCheck = it},
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                        label = { Text("Confirmar contraseña", color = Color.White)},
                        visualTransformation = if (passwordVisible2) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (passwordVisible2) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val description = if (passwordVisible2) "Ocultar contraseña" else "Mostrar contraseña"

                            IconButton(onClick = { passwordVisible2 = !passwordVisible2 }) {
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
                        signUpRequest(nombre,apellidos,birthDate,userName,email,password,navController, context,isRepeated,repeteadUserName)
                    },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Cyan,
                            disabledContainerColor = DarkCyan
                        ),
                        enabled = isEmptyEmail(email,password,passwordCheck)
                        ) {
                        Text("Siguiente", fontSize = 15.sp, color = Color.Black)
                    }
                }

            }
        }
    }
}



fun isEmptyEmail(email: String, password1 :String, password2 :String): Boolean{
    return email.isNotEmpty() && password1.isNotEmpty() && password2.isNotEmpty() && password1.length >= 8 && password2.length >= 8  && email.contains("@")
}

fun signUpRequest(nombre: String, apellidos: String, birthDate: String, userName: String, email: String, password: String, navController: NavController, context: Context, isRepeated: MutableState<Boolean>, message: MutableState<String>){
    val emailLowerCase = email.lowercase(Locale.ROOT)
    val today = LocalDate.now()
    val nombre = nombre + " " + apellidos
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val joininDate = today.format(formatter)
    val genre = emptyList<String>()
    val usuario = Usuario(null,userName,nombre,"",genre,birthDate,joininDate,password,emailLowerCase,"/Images/no_photo.jpg")
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitClient.instance.signUp(usuario)
            withContext(Dispatchers.Main){
                val signUpResponse = response.body()
                if(signUpResponse != null && signUpResponse.success ){
                    Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()
                    PrimerIniciarSesion(email,password,context,navController)
                    isRepeated.value = false
                }else if(signUpResponse?.message == "Correo electronico ya registrado") {
                    Log.d("GH","GHla")
                    isRepeated.value = true
                    message.value = "El email ${email} ya esta en uso"
                    Toast.makeText(context, signUpResponse.message, Toast.LENGTH_SHORT).show()

                } else{
                    if (signUpResponse != null) {
                        Toast.makeText(context, signUpResponse.message, Toast.LENGTH_SHORT).show()
                        Log.d("Error primario" , signUpResponse.message)
                    }
                }
            }

        }catch (e : Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error de red o servidor: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                Log.e("SignUp", "Error: ${e.localizedMessage}")
            }
        }
    }
}

@OptIn(UnstableApi::class)
fun PrimerIniciarSesion(email: String, password: String, context: Context, navController: NavController): Boolean{
    val loginRequest = LoginRequest(email,password)
    var errorMessage = ""
    var result: Boolean = false
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitClient.instance.logIn(loginRequest)

            withContext(Dispatchers.Main) {

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val securePrefs = SecurePrefs(context)

                    if (loginResponse != null && loginResponse.success) {
                        // Inicio de sesión exitoso
                        // Aquí podrías guardar el token, navegar a otra pantalla, etc.

                        Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()
                        navController.navigate("categoryChoosing") {
                            popUpTo(0) { inclusive = true }
                        }
                        errorMessage = ""
                        if(loginResponse.accessToken != null){
                            securePrefs.saveAccessToken(loginResponse.accessToken)
                            androidx.media3.common.util.Log.d("AccessToken",loginResponse.accessToken)
                        }
                        if(loginResponse.refreshToken != null){
                            securePrefs.saveRefreshToken(loginResponse.refreshToken)
                            androidx.media3.common.util.Log.d("RefreshToken",loginResponse.refreshToken)
                        }


                        result = true
                    }else{
                        errorMessage = loginResponse?.message ?: "Error desconocido"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    errorMessage = "Credenciales incorrectas"

                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    response.body()?.let { androidx.media3.common.util.Log.e("error", it.message) }
                }

            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                androidx.media3.common.util.Log.e("LOGIN", "Excepción: ${e.message}")
                // Captura la respuesta cruda si existe
                if (e is HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    androidx.media3.common.util.Log.e("LOGIN", "Respuesta no JSON: $errorBody")
                }
                errorMessage = "Error en la conexion"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

            }
        }
    }
    return result
}