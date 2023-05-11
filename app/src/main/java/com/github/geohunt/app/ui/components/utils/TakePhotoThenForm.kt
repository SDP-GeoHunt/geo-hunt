package com.github.geohunt.app.ui.components

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.sensor.RequireCameraPermission
import com.github.geohunt.app.utility.createImageFile
import java.io.File

@Composable
fun WithPhoto(
    onFailure: (Throwable) -> Unit = {},
    content: @Composable (File) -> Unit
) {
    val context = LocalContext.current
    val file = remember { context.createImageFile() }
    val uri = remember {
        FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
    }
    var photoFile by remember {
        mutableStateOf<File?>(null)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        Log.i("GeoHunt", "Returning from camera")
        if (!it) {
            @Suppress("ThrowableNotThrown")
            onFailure(RuntimeException("Failed to take photo at ${file.absolutePath}"))
        }
        else {
            photoFile = file
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            photoFile = null
        }
    }

    RequireCameraPermission {
        LaunchedEffect(Unit) {
            cameraLauncher.launch(uri)
        }
    }

    if (photoFile != null) {
        content(photoFile!!)
    }
}
