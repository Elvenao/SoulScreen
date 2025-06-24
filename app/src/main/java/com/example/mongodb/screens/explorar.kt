package com.example.mongodb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun explorar(navController: NavController){
    val busqueda = remember { mutableStateOf("") }
     Box(Modifier.background(MaterialTheme.colors.background)){


         Scaffold(
             topBar = {
                 MyTopBar(
                     onBackClick = { navController.navigate("Posts"){
                         popUpTo(0){inclusive = true}
                     } },
                     onSkipClick = {

                     } ,
                     true,
                     false,
                     false,
                     null,
                     null,
                     true
                 )
             }
         ) { innerPadding ->
             Column(
                 modifier = Modifier
                     .padding(innerPadding)
                     .imePadding()
                     .fillMaxSize()
                     .background(MaterialTheme.colors.onBackground)
             ) {
                 Column(
                     modifier = Modifier
                         .background(MaterialTheme.colors.onBackground)
                 ) {
                     Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 8.dp, end = 8.dp), horizontalArrangement = Arrangement.Center) {
                         OutlinedTextField(
                             value = busqueda.value,
                             onValueChange = {busqueda.value = it},
                             singleLine = true,
                             colors = TextFieldDefaults.outlinedTextFieldColors(
                                 backgroundColor = MaterialTheme.colors.onPrimary,
                                 focusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                                 unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                                 textColor =  androidx.compose.material3.MaterialTheme.colorScheme.surface,
                                 cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
                             ),
                             modifier = Modifier.fillMaxWidth(),
                             shape = RoundedCornerShape(
                                 topStart = 12.dp,
                                 topEnd = 12.dp,
                                 bottomEnd = 12.dp,
                                 bottomStart = 12.dp
                             )

                             )
                     }

                 }
             }
         }
     }
}