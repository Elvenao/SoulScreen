package com.example.mongodb.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mongodb.R


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun Confirmacion(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    destination: String
) {
    BackHandler {
        navController.navigate("openCamera")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding()
            .navigationBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(
                modifier = Modifier
                    .weight(0.09f)
                    .fillMaxWidth()
                    .background(Color.Black)
            )

            sharedViewModel.imageBitmap?.let { FullscreenPhoto(it, modifier = Modifier.fillMaxWidth()
                .weight(0.85f)) }

            Spacer(
                modifier = Modifier
                    .weight(0.06f)
                    .fillMaxWidth()
                    .background(Color.Black)
            )
        }

        // Botón de cambiar cámara (superior izquierda)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart) // ⬅️ Esquina inferior izquierda
                .padding(top = 64.dp) // Espaciado desde los bordes
                .size(70.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    navController.navigate("profileScreen"){
                        popUpTo(0){inclusive = true}
                    }
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Camera",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd) // ⬅️ Esquina inferior izquierda
                .padding(end = 16.dp, bottom = 58.dp) // Espaciado desde los bordes
                .width(200.dp)
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            BotonAceptar(navController, destination)
        }





    }


}


@Composable
fun BotonRechazar(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    destination: String,
    id: String,
    productName: String,
    price: String,
    description: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Rechazar", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        Button(
            onClick = {
                sharedViewModel.imageBitmap = null

                navController.navigate("${destination}/${id}/${productName}/${price}/${description}")
            },
            modifier = Modifier
                .width(140.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Rechazar",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun BotonAceptar(
    navController: NavController,
    destination: String

) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Button(
            onClick = {
                navController.navigate(destination)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Listo", color = Color.Black, fontSize = 20.sp)
        }
    }
}

@Composable
fun FullscreenPhoto(bitmap: Bitmap, modifier : Modifier = Modifier) {
    Image(
        bitmap = bitmap.asImageBitmap(),         // Convierte el Bitmap
        contentDescription = "Foto tomada",
        contentScale = ContentScale.Crop,        // Para que llene sin deformar
        modifier = modifier       // Ocupa toda la pantalla
    )
}

