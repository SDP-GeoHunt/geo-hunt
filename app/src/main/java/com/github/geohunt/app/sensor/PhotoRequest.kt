package com.github.geohunt.app.sensor

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material.icons.sharp.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.github.geohunt.app.ui.*
import com.github.geohunt.app.ui.components.LinkText
import com.github.geohunt.app.ui.components.LinkTextData
import com.github.geohunt.app.utility.BitmapUtils
import com.github.geohunt.app.utility.toCompletableFuture
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PhotoRequest {
    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_$timeStamp", /* prefix */
            ".jpg", /* suffix */
            storageDir
        )
    }

    private fun takePhoto(
        context: Context,
        imageCapture: ImageCapture,
        executor: Executor,
        onImageCaptured: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        // Retrieve the file where the photo will be located after the operation
        lateinit var photoFile: File
        try {
            photoFile = createImageFile(context)
        } catch (e: IOException) {
            onError(e)
            return
        }

        // This is where all of the magic happen
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    BitmapUtils.loadFromFile(photoFile).toCompletableFuture(context.findActivity())
                        .thenApply {
                            onImageCaptured(it)
                        }
                        .exceptionally {
                            onError(it)
                        }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("GeoHunt", "Failed to take the photo due to exception $exception")
                    onError(exception)
                }
            })
    }

    private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(this).also { cameraProvider ->
                cameraProvider.addListener({
                    continuation.resume(cameraProvider.get())
                }, ContextCompat.getMainExecutor(this))
            }
        }

    @Composable
    fun CameraView(onImageCaptured: (Bitmap) -> Unit, onError: (Throwable) -> Unit) {
        // First and foremost define some utility variable
        val lensFacing = CameraSelector.LENS_FACING_BACK
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val executor: Executor = context.findActivity().mainExecutor
        val cameraPermissionState = rememberPermissionsState(Manifest.permission.CAMERA)
//        val cameraX = cameraPermissionState.
        val privatePolicyUrl = stringResource(com.github.geohunt.app.R.string.privacy_policy_url)
        val openPrivacyPolicyIntent = remember {
            Intent(Intent.ACTION_VIEW, Uri.parse(privatePolicyUrl))
        }

        val preview = Preview.Builder().build()
        val previewView = remember { PreviewView(context) }
        val imageCapture = remember { ImageCapture.Builder().build() }
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        if (cameraPermissionState.allAreGranted) {
            // Create the camera provider
            LaunchedEffect(null) {
                val cameraProvider = context.getCameraProvider()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                preview.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Finally create the photo application
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

                IconButton(
                    modifier = Modifier.padding(bottom = 20.dp),
                    onClick = {
                        Log.i("GeoHunt", "Take photo button was pressed")
                        takePhoto(
                            context = context,
                            imageCapture = imageCapture,
                            executor = executor,
                            onImageCaptured = onImageCaptured,
                            onError = onError
                        )
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Sharp.Lens,
                            contentDescription = "Take picture",
                            tint = Color.White,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(1.dp)
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }
                )
            }
        }
        else {
            LaunchedEffect(null) {
                cameraPermissionState.requestPermissions()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp, 0.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.verticalScroll(state = rememberScrollState())
                ) {
                    Icon(
                        Icons.Sharp.Warning,
                        contentDescription = "Error Icon",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(85.dp),
                        tint = MaterialTheme.colors.error)

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(text = "Requires permission to function",
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.h1)

                    Spacer(modifier = Modifier.height(5.dp))

                    LinkText(listOf(
                        LinkTextData("GeoHunt allows users to take pictures of their environment" +
                                " and challenge others to find the location. To use this feature, the" +
                                " app needs access to the device's camera to capture pictures. " +
                                "Rest assured that we take user privacy seriously and only use" +
                                " the camera feature in accordance with our "),
                        LinkTextData("privacy policy",
                            tag = "",
                            annotation = "",
                            onClick = {
                                context.startActivity(openPrivacyPolicyIntent)
                            }),
                        LinkTextData(".")
                    ))
                    Text(
                        text = "",
                        color = MaterialTheme.colors.primary,
                    )
                }
            }
        }

    }
}

