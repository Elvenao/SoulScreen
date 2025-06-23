package com.example.mongodb.screens

import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import java.io.File
import java.io.FileOutputStream


class SharedViewModel : ViewModel() {
    var imageBitmap by mutableStateOf<Bitmap?>(null)
    var imageUri by mutableStateOf<Uri?>(null)
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun CameraUI(nav: NavController, destination : String, sharedViewModel: SharedViewModel) {
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    sharedViewModel.imageBitmap = null
    sharedViewModel.imageUri = null

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            sharedViewModel.imageUri = it
            nav.navigate("takenPhoto/${destination}") // Navega a otra pantalla
        }
    }
    fun addPhoto(bitmap: Bitmap) {
        
        sharedViewModel.imageBitmap = bitmap
        

        nav.navigate("takenPhoto/${destination}")
    }

    BackHandler {
        nav.navigate("profileScreen")
    }

    val context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
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

            CameraPreview(
                controller = controller,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.85f) // 80% del alto
            )

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
                    nav.navigate("profileScreen"){
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
                .padding(end = 16.dp, bottom = 36.dp) // Espaciado desde los bordes
                .size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        else
                            CameraSelector.DEFAULT_BACK_CAMERA
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch camera",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart) // ⬅️ Esquina inferior izquierda
                .padding(start = 16.dp, bottom = 36.dp) // Espaciado desde los bordes
                .size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            // Galería
            IconButton(
                onClick = {
                    launcher.launch("image/*") // ✅ Esto abre la galería
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Collections,
                    contentDescription = "Select Image",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        // Botón de tomar foto (centrado abajo o a la derecha según orientación)
        Box(
            modifier = Modifier
                .then(
                    if (isLandscape())
                        Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 24.dp)
                    else
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 64.dp)
                ),
            contentAlignment = Alignment.Center
        ) {



            // Tomar foto
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        takePhoto(controller, context) { bitmap -> addPhoto(bitmap) }
                    },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take photo",
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }


    }
}

@Composable
fun isLandscape(): Boolean {
    val configuration = LocalContext.current.resources.configuration
    return configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
}


fun takePhoto(
    controller: LifecycleCameraController,
    context: Context,
    onPhotoTaken: (Bitmap) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }

                val originalBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                val width = 1400
                val height = 1400
                val centerX = (originalBitmap.width - width) / 2
                val centerY = (originalBitmap.height - height) / 2

                val croppedBitmap = Bitmap.createBitmap(
                    originalBitmap,
                    centerX.coerceAtLeast(0),
                    centerY.coerceAtLeast(0),
                    width.coerceAtMost(originalBitmap.width),
                    height.coerceAtMost(originalBitmap.height)
                )

                onPhotoTaken(croppedBitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Couldn't take photo: ", exception)
            }
        }
    )
}
@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}

@Composable
fun AbrirGaleria(onImageSelected: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImageSelected(it)
        }
    }

    Button(onClick = {
        launcher.launch("image/*") // Esto abre la galería
    }) {
        Text("Seleccionar imagen de galería")
    }
}


