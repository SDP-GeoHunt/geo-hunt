package com.github.geohunt.app.ui.components.utils

import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.sensor.RequireCameraPermission
import com.github.geohunt.app.ui.components.CircleLoadingAnimation
import com.github.geohunt.app.ui.components.utils.viewmodels.WithPhotoViewModel
import com.github.geohunt.app.utility.createImageFile

@Composable
fun WithPhoto(
    onFailure: (Throwable) -> Unit,
    viewModel: WithPhotoViewModel = viewModel(factory = WithPhotoViewModel.Factory),
    content: @Composable (Bitmap, LocalPicture) -> Unit,
) {
    val context = LocalContext.current
    val fileImageState = viewModel.imageState.collectAsState()
    val bitmapImageState = viewModel.bitmapState.collectAsState()
    val file = remember { context.createImageFile() }
    val uri = remember {
        FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        Log.i("GeoHunt", "Returning from camera with ${if (it) "success" else "failure"}")
        if (!it) {
            @Suppress("ThrowableNotThrown")
            onFailure(RuntimeException("Failed to take photo at ${file.absolutePath}"))
        }
        else {
            viewModel.withPhotoFile(
                { suffix ->
                    context.createImageFile(suffix)
                },
                file, onFailure)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.reset()
        }
    }

    if (fileImageState.value == null || bitmapImageState.value == null) {
        RequireCameraPermission {
            LaunchedEffect(viewModel) {
                viewModel.reset()
                cameraLauncher.launch(uri)
            }
        }

        Box(modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .fillMaxSize()) {
            CircleLoadingAnimation(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
    else {
        val imageFile = fileImageState.value!!
        val bitmap = bitmapImageState.value!!
        content(bitmap, remember(imageFile) { LocalPicture(imageFile) })
    }
}
