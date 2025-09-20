package com.subhajeet.cameraapp

import android.content.Context
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun Camera() {

    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    //We are taking lifecycle because if cameraApp goes to background or gets off ,we can properly manage the application.

    val previewView = remember{ PreviewView(context)}
    // previewView is a normal android view where we can see  the live camera feed that comes on background.

    val cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    //Choosing lens , means through which lens I want to capture image ,as there are two lens front and back
    //so here we specify that which lens is selected currently

    val imageCapture = remember{ ImageCapture.Builder().build()}
    //Creating state for image capture

    var camera by remember { mutableStateOf<Camera?>(null) }

    var isFlashOn by remember { mutableStateOf(false) }
    //State For implementing flash light

    val preview = remember { Preview.Builder().build() }
    //Making preview which will display the actual camera preview

    LaunchedEffect(cameraSelector) {
        val cameraProvider = context.getCameraProvider()

        try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

            preview.setSurfaceProvider ( previewView.surfaceProvider )

        }catch (e:Exception){
            Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ){
        AndroidView(
            factory = {previewView},
            modifier = Modifier.fillMaxSize()
        )
    }

}

//Making a cameraProvider Function which we get through cameraX
private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->

    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

    cameraProviderFuture.addListener(
        {
            continuation.resume(cameraProviderFuture.get())
        },ContextCompat.getMainExecutor(this)
    )
}