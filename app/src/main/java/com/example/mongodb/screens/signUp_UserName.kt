package com.example.mongodb.screens

import android.content.Context
import android.widget.Toast
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import com.example.mongodb.model.UserNameRequest
import com.example.mongodb.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

@Composable
fun signUp_UserName(navController: NavController, nombre: String, apellidos: String, birthDate: String){
    var userName by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val isRepeated = remember { mutableStateOf(false) }
    var repeteadUserName = rememberSaveable { mutableStateOf("") }
    Box(Modifier.fillMaxSize().background(Color.Black)){
        Box(Modifier.fillMaxSize()){
            Column(modifier = Modifier.align(Alignment.TopStart).padding(start = 20.dp, top = 20.dp, end = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.align(Alignment.Start)) {
                    Text(
                        "¿Como quieres que te llamen?",
                        fontSize = 27.sp,
                        textAlign = TextAlign.Left,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                }
                Row(Modifier.align(Alignment.Start)) {
                    Text(
                        "Ingresa tu nombre de Usuario",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Left,
                        color = Color.White,
                    )
                }
                if(isRepeated.value){
                    Row(Modifier.align(Alignment.Start)) {
                        Text(repeteadUserName.value, color = Red)
                    }
                }

                Row(Modifier.align(Alignment.Start).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = "@$userName",
                        onValueChange = {
                            val input = it.removePrefix("@")               // Elimina cualquier arroba que ya esté
                                .replace("@", "")            // Elimina arrobas adicionales en el resto
                            userName = input
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                        label = { Text("Usuario", color = Color.White) },
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
                Row(Modifier.align(Alignment.Start).fillMaxWidth()){
                    Button(onClick = {
                        checkUserName(context,userName,navController,nombre,apellidos,birthDate, isRepeated,repeteadUserName)

                    },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Cyan,
                            disabledContainerColor = DarkCyan
                        ),
                        enabled = userName.isNotEmpty()
                    ) {
                        Text("Siguiente", fontSize = 15.sp, color = Color.Black)
                    }
                }

            }
        }
    }
}

fun checkUserName(context: Context, userName: String,navController: NavController, nombre:String, apellidos: String,birthDate: String, isRepeated: MutableState<Boolean>, message: MutableState<String>){

    CoroutineScope(Dispatchers.IO).launch {
        val userNameDto = UserNameRequest(userName)
        val response = RetrofitClient.instance.repeteadUserName(userNameDto)

        withContext(Dispatchers.Main){
            val repeteadUserNameResponse = response.body()
            if(repeteadUserNameResponse != null && repeteadUserNameResponse.success){
                navController.navigate("signUp_Email/${nombre}/${apellidos}/${birthDate}/${userName}")
                isRepeated.value = false
            }else if(repeteadUserNameResponse?.message == "Nombre de usuario ya registrado"){
                isRepeated.value = true
                message.value = "El nombre de usuario @${userName} ya esta en uso"
            }
        }
    }

}