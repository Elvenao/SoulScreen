package com.example.mongodb.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mongodb.ui.theme.DefaultFont
import com.example.mongodb.ui.theme.MonospaceFont
import com.example.mongodb.ui.theme.SansSerifFont
import com.example.mongodb.ui.theme.SerifFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun config(navController: NavController, onFontSelected: (FontFamily) -> Unit) {
    BackHandler {
        navController.navigate("UIPrincipal") {
            popUpTo("UIPrincipal") { inclusive = true }
        }
    }

    var expanded by remember { mutableStateOf(false) }
    val fontOptions: List<Pair<String, FontFamily>> = listOf(
        "Default" to DefaultFont,
        "Monospace" to MonospaceFont,
        "Serif" to SerifFont,
        "Sans Serif" to SansSerifFont
    )
    var selectedFontName by remember { mutableStateOf("Default") }

    Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(
                title = {
                    androidx.compose.material.Text(
                        "Configuración",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(80.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                "Selecciona una tipografía:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(selectedFontName)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    fontOptions.forEach { (name, font) ->
                        DropdownMenuItem(
                            text = { Text(name, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                selectedFontName = name
                                expanded = false
                                onFontSelected(font)
                            }
                        )
                    }
                }
            }
        }
    }
}
