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
import android.widget.Toast
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import com.example.mongodb.model.CurrentUserData
import com.example.mongodb.model.RefreshTokenRequest
import com.example.mongodb.network.RetrofitClient
import com.google.ai.client.generativeai.common.shared.Part
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.Multipart


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
            file.toUri()
        }
        sharedViewModel.imageUri != null -> {
            sharedViewModel.imageUri!!
        }
        else -> null
    }
    val imageBitmap = when {
        sharedViewModel.imageBitmap != null -> {
           sharedViewModel.imageBitmap
        }
        sharedViewModel.imageUri != null -> {
            uriToBitmap(context, sharedViewModel.imageUri!!)
        }
        else -> null
    }

    val painter = uri?.let { rememberAsyncImagePainter(it) }


    BackHandler {
        navController.navigate("openCamera")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding()
            .navigationBarsPadding()
        ,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(
                modifier = Modifier
                    .weight(0.09f)
                    .fillMaxWidth()
                    .background(Color.Black)
                    .zIndex(1f)
            )


            // Aquí apilas la imagen y el círculo juntos
            Box(
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxSize()
                    .onSizeChanged {
                        boxSize = it

            }
                    .draggable(

                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { deltaY ->
                            val minY = circleRadius
                            val maxY = boxSize.height.toFloat() -  circleRadius
                            circleCenter = circleCenter.copy(
                                y = (circleCenter.y + deltaY).coerceIn(minY, maxY)
                            )
                        }
                    )


            ) {
                // Imagen de fondo
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            ,
                        alpha = 1f

                    )
                }

                // Círculo encima (Canvas se mantiene separado)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                    ) {
                    drawCircle(
                        color = Color.Black,
                        radius = circleRadius,
                        center = circleCenter,
                        style = Stroke(width = 4f),

                    )
                }

            }

            Spacer(
                modifier = Modifier
                    .weight(0.06f)
                    .fillMaxWidth()
                    .background(Color.Black)
                    .zIndex(1f)
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
                    tint = Color.Gray,
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
                BotonAceptar(navController, destination,uri,context,circleCenter, circleRadius,currentUserData)
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

                navController.navigate("profileScreen")
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
    context: Context,
    center: Offset,
    radius: Float,
    currentUserData: CurrentUserData

) {
    val fileName = currentUserData.userName
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Button(
            onClick = {

                navController.navigate("profileScreen") {
                    popUpTo(0){inclusive = true}
                }
                val bitMap = uriToBitmap(context,uri)
                val cropedImage = recortarCuadro(bitMap,center,radius)
                val finalImage = bitmapToFile(context,cropedImage,fileName)
                val multipartBody = prepareImagePart(finalImage)
                sendImage(multipartBody,context,currentUserData)
                
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

fun sendImage(multipart: MultipartBody.Part, context: Context, currentUserData: CurrentUserData){
    val id = currentUserData.id
    val encryptedSharedPreferences = SecurePrefs(context)

    val refreshToken = encryptedSharedPreferences.getRefreshToken()

     CoroutineScope(Dispatchers.IO).launch{
         try {
             val response = RetrofitClient.getInstance(context).updateAvatar(id,multipart)
             withContext(Dispatchers.Main) {
                 if(response.isSuccessful){
                     Toast.makeText(context, "Avatar actualizado", Toast.LENGTH_SHORT).show()
                     refreshToken?.let {
                         RefreshTokenRequest(
                             it
                         )
                     }?.let { RetrofitClient.getInstance(context).refreshToken(it) }
                 }else{
                     Toast.makeText(context, "Hubo problemas, intente de nuevo", Toast.LENGTH_SHORT).show()
                 }
             }
         }catch(e : Exception){
             Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
         }
     }
}

fun prepareImagePart(file: File): MultipartBody.Part {
    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("image", file.name, requestFile)
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


fun recortarCuadro(bitmap: Bitmap, center: Offset, radius: Float): Bitmap {
    val diameter = (radius * 2).toInt()

    val maxLeft = maxOf(0, bitmap.width - diameter)
    val maxTop = maxOf(0, bitmap.height - diameter)

    val left = (center.x - radius).toInt().coerceIn(0, maxLeft)
    val top = (center.y - radius).toInt().coerceIn(0, maxTop)

    // Ajustar ancho y alto para que no se salga del bitmap
    val width = minOf(diameter, bitmap.width - left)
    val height = minOf(diameter, bitmap.height - top)

    return Bitmap.createBitmap(bitmap, left, top, width, height)
}


