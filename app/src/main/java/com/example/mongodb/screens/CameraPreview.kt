package com.example.mongodb.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.mongodb.R
import java.io.File
import java.io.FileOutputStream
import com.example.mongodb.SecurePrefs
import java.util.UUID

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.layout.onSizeChanged

import androidx.compose.ui.unit.IntSize


import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

import android.graphics.Bitmap.Config


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun Confirmacion(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    destination: String
) {
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    val EncryptedSharedPreferences = SecurePrefs(LocalContext.current)
    var circleCenter by remember { mutableStateOf(Offset(540f, 1100f)) }
    val density = LocalDensity.current
    val circleRadius = with(density) { 190.dp.toPx() }
    val currentUserData = EncryptedSharedPreferences.getCurrentUserData()
    val name = currentUserData.userName
    val context = LocalContext.current
    val uri = when {
        sharedViewModel.imageBitmap != null -> {
            val file = bitmapToFile(context, sharedViewModel.imageBitmap!!, name)
            file.toUri() // Convierte el File a Uri
        }
        sharedViewModel.imageUri != null -> {
            sharedViewModel.imageUri!! // Ya es Uri
        }
        else -> null
    }
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

            // Aquí apilas la imagen y el círculo juntos
            Box(
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxWidth()
                    .onSizeChanged { boxSize = it }
                    .pointerInput(boxSize) {  // Incluye boxSize para re-evaluar cuando cambie tamaño
                        detectDragGestures { change, dragAmount ->
                            change.consume()

                            val radiusPx = circleRadius

                            val minY = radiusPx
                            val maxY = boxSize.height.toFloat() - radiusPx

                            // Actualiza solo Y dentro del rango del Box
                            circleCenter = circleCenter.copy(
                                y = (circleCenter.y + dragAmount.y).coerceIn(minY, maxY)
                            )
                        }
                    }
            ) {
                // Imagen de fondo
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Círculo encima
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.White,
                        radius = circleRadius,
                        center = circleCenter,
                        style = Stroke(width = 4f)
                    )
                }
            }

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
            if (uri != null) {
                BotonAceptar(navController, destination,uri,context)
            }
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
    destination: String,
    uri: Uri,
    context: Context

) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Button(
            onClick = {

                navController.navigate(destination)
                val bitMap = uriToBitmap(context,uri)
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

fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
    // Crear archivo temporal
    val nombreArchivo = UUID.randomUUID()
    val file = File(context.cacheDir, "${fileName}_${nombreArchivo}.jpg")
    file.createNewFile()

    // Escribir Bitmap al archivo
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()

    return file
}


fun uriToBitmap(context: Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    } else {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
}

fun recortarCirculo(bitmap: Bitmap, center: Offset, radius: Float): Bitmap {
    val diameter = (radius * 2).toInt()
    val left = (center.x - radius).toInt().coerceIn(0, bitmap.width - diameter)
    val top = (center.y - radius).toInt().coerceIn(0, bitmap.height - diameter)

    // Recorte cuadrado alrededor del círculo
    val squareBitmap = Bitmap.createBitmap(bitmap, left, top, diameter, diameter)

    // Recorte circular
    val output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val path = Path().apply {
        addCircle(radius, radius, radius, Path.Direction.CCW)
    }

    canvas.clipPath(path)
    canvas.drawBitmap(squareBitmap, 0f, 0f, paint)

    return output
}



