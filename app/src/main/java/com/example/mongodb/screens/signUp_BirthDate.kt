package com.example.mongodb.screens

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mongodb.ui.theme.DarkCyan
import okhttp3.internal.wait
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Composable
fun signUp_BirthDate(navController: NavController, nombre: String, apellidos: String){
    val fechaSeleccionada = remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize().background(Color.Black)){
        Scaffold(
            topBar = {
                MyTopBar(
                    onBackClick = { navController.navigate("signUp"){
                        popUpTo(0){inclusive = true}
                    } },
                    onSkipClick = {

                    } ,
                    true,
                    false,
                    false,
                    null,
                    null
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Column(modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.align(Alignment.Start)) {
                        Text(
                            "¿Cuantos años tienes?",
                            fontSize = 27.sp,
                            textAlign = TextAlign.Left,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                    }
                    Row(Modifier.align(Alignment.Start)) {
                        Text(
                            "Ingresa tu fecha de nacimiento.",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Left,
                            color = Color.White,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DatePickerFieldToModal(fechaSeleccionada)
                        Log.d("s",fechaSeleccionada.value)
                    }
                    Row(Modifier.align(Alignment.Start).fillMaxWidth()){
                        Button(onClick = {
                            navController.navigate("signUp_UserName/${nombre}/${apellidos}/${fechaSeleccionada.value}")
                        },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Cyan,
                                disabledContainerColor = DarkCyan
                            ),
                            enabled = fechaSeleccionada.value.isNotEmpty(),


                            ) {
                            Text("Siguiente", fontSize = 15.sp, color = Color.Black)
                        }
                    }

                }
            }
        }

    }
}

fun convertMillisToDate(millis: Long, dateBirth: MutableState<String>): String {
    val utcTimeZone = TimeZone.getTimeZone("UTC")

    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).apply {
        timeZone = utcTimeZone
    }

    val anotherFormatter = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).apply {
        timeZone = utcTimeZone
    }

    dateBirth.value = anotherFormatter.format(Date(millis))
    return formatter.format(Date(millis))
}

@Composable
fun DatePickerFieldToModal(dateBirth: MutableState<String>) {
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate?.let { convertMillisToDate(it,dateBirth) } ?: "",
        onValueChange = { },
        shape = RoundedCornerShape(16.dp),
        label = { Text("Fecha de Nacimiento", color = Color.White)},
        placeholder = { Text("MM/DD/YYYY", color = Color.White) },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date", tint= Color.White)
        },
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Cyan,
            unfocusedBorderColor = Color.Gray,
            textColor = Color.White,
            cursorColor = Color.Cyan
        ),
        textStyle = TextStyle(
            fontSize = 15.sp,
            color = Color.White
        ),
    )

    if (showModal) {
        DatePickerModal(
            onDateSelected = { selectedDate = it },
            onDismiss = { showModal = false }

        )
        
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    AlertDialog(
        
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                    onDismiss()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        text = {
            DatePicker(state = datePickerState)
        }
    )
}


