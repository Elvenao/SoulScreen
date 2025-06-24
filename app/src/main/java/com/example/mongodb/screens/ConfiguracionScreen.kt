package com.example.mongodb.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mongodb.loadFontConfig
import com.example.mongodb.saveFontConfig
import com.example.mongodb.ui.theme.DefaultFont
import com.example.mongodb.ui.theme.MonospaceFont
import com.example.mongodb.ui.theme.SansSerifFont
import com.example.mongodb.ui.theme.SerifFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun config(
    navController: NavController,
    onFontSelected: (FontFamily) -> Unit,
    onFontScaleSelected: (Float) -> Unit
) {
    val context = LocalContext.current

    // Cargar la configuración guardada
    val (savedFontName, savedFontScale) = loadFontConfig(context)

    val fontOptions: List<Pair<String, FontFamily>> = listOf(
        "Default" to DefaultFont,
        "Monospace" to MonospaceFont,
        "Serif" to SerifFont,
        "Sans Serif" to SansSerifFont
    )
    val fontScaleOptions = listOf(
        "Pequeña" to 0.85f,
        "Normal" to 1.0f,
        "Grande" to 1.2f
    )

    var expanded by remember { mutableStateOf(false) }
    var fontSizeExpanded by remember { mutableStateOf(false) }

    // ✅ Estado sincronizado con lo guardado
    var selectedFontName by remember { mutableStateOf(savedFontName) }
    var selectedFontScaleName by remember {
        mutableStateOf(
            fontScaleOptions.firstOrNull { it.second == savedFontScale }?.first ?: "Normal"
        )
    }

    BackHandler {
        navController.navigate("UIPrincipal") {
            popUpTo("UIPrincipal") { inclusive = true }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Configuración",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
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
                .verticalScroll(rememberScrollState())
        ) {
            // Selección de fuente
            Text("Selecciona una tipografía:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(selectedFontName, color = MaterialTheme.colorScheme.onSurface)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    fontOptions.forEach { (name, font) ->
                        DropdownMenuItem(
                            text = { Text(name, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                selectedFontName = name
                                expanded = false
                                onFontSelected(font)
                                saveFontConfig(context, name, fontScaleOptions.firstOrNull { it.first == selectedFontScaleName }?.second ?: 1.0f)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(15.dp))

            // Selección de tamaño de fuente
            Text("Tamaño de fuente:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                OutlinedButton(onClick = { fontSizeExpanded = true }) {
                    Text(selectedFontScaleName, color = MaterialTheme.colorScheme.onSurface)
                }
                DropdownMenu(expanded = fontSizeExpanded, onDismissRequest = { fontSizeExpanded = false }) {
                    fontScaleOptions.forEach { (name, scale) ->
                        DropdownMenuItem(
                            text = { Text(name, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                selectedFontScaleName = name
                                fontSizeExpanded = false
                                onFontScaleSelected(scale)
                                saveFontConfig(context, selectedFontName, scale)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(15.dp))

            //Editar Perfil
            Text("Editar Perfil:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Box{
                Button(
                    onClick = {
                        navController.navigate("changeUsername")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Cambiar Usuario", color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box{
                Button(
                    onClick = {
                        navController.navigate("changePassword")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Cambiar Contraseña", color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box{
                Button(
                    onClick = {
                        navController.navigate("changeEmail")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Cambiar Email", color = MaterialTheme.colorScheme.onSurface)
                }
            }


        }
    }
}
