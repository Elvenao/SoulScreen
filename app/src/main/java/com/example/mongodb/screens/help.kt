package com.example.mongodb.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mongodb.ui.theme.DarkCyan

@Composable
fun help(navController: NavController) {
    BackHandler {
        navController.navigate("UIPrincipal") {
            popUpTo("UIPrincipal") { inclusive = true }
        }
    }

    Box(
        Modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primary
                        ),
                    contentAlignment = Alignment.CenterStart,

                    ) {

                    IconButton(onClick = {
                        navController.navigate("UIPrincipal")
                    },Modifier.padding(start = 30.dp)) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew, // Usa Icons.Default.Menu si prefieres
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(30.dp).align(Alignment.CenterStart)
                        )
                    }
                    Text(
                        "Ayuda",
                        fontSize = 26.sp,
                        modifier = Modifier.align(Alignment.Center),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface

                    )

                }
            }

            item {
                Row(Modifier.padding(20.dp)) {
                    Box(Modifier.padding(9.dp)) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Abrir menú",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Box(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 15.dp)
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("El botón ")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = DarkCyan)) {
                                    append("menú")
                                }
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append(" al ser presionado desplegará un conjunto de opciones las cuales son: perfil, configuracion, ayuda, contacto a soporte")
                                }
                            },
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }

            item { HorizontalDivider() }

            item {
                Row(Modifier.padding(10.dp)) {
                    Box(Modifier.padding(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "UserIcon",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(24.dp)
                        )
                    }
                    Box(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 15.dp)
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("Este boton te llevara a tu perfil donde podrás ver tu información y tu contenido")
                                }
                            },
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }

            item { HorizontalDivider() }

            item {
                Row(Modifier.padding(10.dp)) {
                    Box(Modifier.padding(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Icon",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(24.dp)
                        )
                    }
                    Box(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 15.dp)
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("El botón de ")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = DarkCyan)) {
                                    append("Configuración ")
                                }
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("te llevara a una ventana donde podras personalizar la app, editar tu perfil y demas opciones")
                                }
                            },
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }

            item { HorizontalDivider() }

            item {
                Row(Modifier.padding(10.dp)) {
                    Box(Modifier.padding(13.dp)) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Help Icon",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(24.dp)
                        )
                    }
                    Box(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 15.dp)
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("El botón de")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = DarkCyan)) {
                                    append("Ayuda ")
                                }
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("abre está ventana la cual indica como funciona la aplicación.")
                                }
                            },
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }

            item { HorizontalDivider() }


            item {
                Row(Modifier.padding(20.dp)) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Support Icon",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(24.dp)
                        )
                    }
                    Box(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 15.dp)
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("El botón de")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold , color = MaterialTheme.colorScheme.onSurface)) {
                                    append("Contacta con Soporte")
                                }
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append(" al ser presionado te dira que le mandes un mensaje a Emilio")
                                }
                            },
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }

            item { HorizontalDivider() }

            item {
                Row(Modifier.padding(20.dp)) {
                    Box {
                        Text(
                            text = "Cerrar Sesión",
                            fontSize = 15.sp,
                            color = Color.Red,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                    Box(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 15.dp)
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("El botón ")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Red)) {
                                    append("Cerrar Sesión")
                                }
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("cerrara tu sesión actual y te llevara a la pantalla de inicio de sesión permitiendote cambiar de cuenta")
                                }
                            },
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }

            item { HorizontalDivider() }

            item {
                Row(Modifier.padding(20.dp)) {
                    Box(modifier = Modifier.size(50.dp)) {
                        FloatingActionButton(
                            onClick = {

                            },
                            backgroundColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Nuevo post")
                        }
                    }
                    Box(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 20.dp)
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("El botón ")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = DarkCyan)) {
                                    append("+")
                                }
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append(" al ser presionado te llevara a una nueva panralla para crear un post, en esta pantalla te pedira un")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = DarkCyan)) {
                                    append("Titulo del Post")
                                }
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append(" en el cual pondras un encabezado corto para tu post. En ")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = DarkCyan)) {
                                    append("¿A que contenido te refieres?")
                                }
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append(" podras buscar el contenido multimedia sobre el que trata tu post. En ")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = DarkCyan)) {
                                    append("Contenido del Post")
                                }
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append(" es donde ira el texto general de tu post, es decir donde van tus ideas. Y finalmente ")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = DarkCyan)) {
                                    append("Tipo de Post")
                                }
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append(" es donde seleccionaras una categoria para tu post dentro de las opciones que tenemos para ti ")
                                }
                            },
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }

            item { HorizontalDivider() }


            item {
                Row(Modifier.padding(20.dp)) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Posts",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(24.dp)
                        )
                    }
                    Box(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 15.dp)
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("El botón ")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = DarkCyan)) {
                                    append("Home  / Posts")
                                }
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append(" es el inicio de nuestra aplicacion y donde podras ver los posts mas recientes.")
                                }
                            },
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }

            item { HorizontalDivider() }


            item {
                Row(Modifier.padding(20.dp)) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Explorar",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(24.dp)
                        )
                    }
                    Box(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 15.dp)
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("El botón ")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = DarkCyan)) {
                                    append("Explorar")
                                }
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append(" es un area donde podras realizar busquedas de diferentes post y contenido multimedia de nuestra aplicación.")
                                }
                            },
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }

            item { HorizontalDivider() }


            item {
                Row(Modifier.padding(20.dp)) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.AssignmentTurnedIn,
                            contentDescription = "Mis gustos",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(24.dp)
                        )
                    }
                    Box(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 15.dp)
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append("El botón ")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = DarkCyan)) {
                                    append("Mis gustos")
                                }
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append(" es el area donde podras ver tu contenido favorito.")
                                }
                            },
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }


        }
    }
}