package com.github.geohunt.app.ui.components

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.sensor.rememberLocationRequestState
import com.github.geohunt.app.sensor.rememberPermissionsState
import com.github.geohunt.app.ui.findActivity
import com.github.geohunt.app.utility.BitmapUtils
import com.github.geohunt.app.utility.onException
import com.github.geohunt.app.utility.toCompletableFuture
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

private fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
}

@Composable
private fun CreateChallengeForm(
    bitmap: Bitmap,
    database: Database,
    onChallengeCreated: (Challenge) -> Unit,
    onFailure: (Throwable) -> Unit
) {
    val activity = LocalContext.current.findActivity()
    val bitmapPainter = remember { BitmapPainter(bitmap.asImageBitmap()) }
    val locationRequest = rememberLocationRequestState()
    val locationPermission = rememberPermissionsState(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(true) {
        locationPermission.requestPermissions()
            .thenCompose {
                locationRequest.launchLocationRequest()
            }
            .onException(onFailure)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(text = "Create new Challenge",
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h1)

            Spacer(Modifier.height(25.dp))

            Image(painter = bitmapPainter, contentDescription = "Photo just taken of the challenge")

            Spacer(Modifier.height(25.dp))

            Button(
                onClick = {
                          database.createChallenge(
                              thumbnail = bitmap,
                              location = locationRequest.lastLocation.value!!,
                              expirationDate = null
                          ).toCompletableFuture(activity)
                              .thenApply(onChallengeCreated)
                              .onException(onFailure)
                },
                enabled = (locationRequest.lastLocation.value != null)
            ) {
                Text("Create challenge")
            }
        }
    }
}

@Composable
fun CreateNewChallenge(
    database: Database,
    onChallengeCreated: (Challenge) -> Unit = {},
    onFailure: (Throwable) -> Unit = {}
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
    val bitmapResult = remember { mutableStateOf<Bitmap?>(null) }
    val permissions = rememberPermissionsState(Manifest.permission.CAMERA)
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        if (!it) {
            onFailure(RuntimeException("Failed to take the photo"))
        }
        else {
            BitmapUtils.loadFromFile(file)
                .toCompletableFuture(context.findActivity())
                .thenAccept { bitmap ->
                    bitmapResult.value = bitmap
                }
                .onException(onFailure)
        }
    }

    if (bitmapResult.value != null) {
        CreateChallengeForm(bitmapResult.value!!, database, onChallengeCreated, onFailure)
    } else {
        LaunchedEffect(null) {
            permissions.requestPermissions()
                .thenApply {
                    cameraLauncher.launch(uri)
                }
                .exceptionally(onFailure)
        }
    }
}
