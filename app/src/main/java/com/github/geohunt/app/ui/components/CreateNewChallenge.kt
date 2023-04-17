package com.github.geohunt.app.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.sensor.rememberLocationRequestState
import com.github.geohunt.app.sensor.rememberPermissionsState
import com.github.geohunt.app.ui.theme.Typography
import com.github.geohunt.app.utility.*
import kotlinx.coroutines.tasks.asTask
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateChallengeForm(
    bitmap: Bitmap,
    database: Database,
    onChallengeCreated: (Challenge) -> Unit,
    onFailure: (Throwable) -> Unit
) {
    var currentlySubmitting by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context.findActivity()
    val bitmapPainter = remember { BitmapPainter(bitmap.asImageBitmap()) }
    val locationRequest = rememberLocationRequestState()
    val communityGuidelinesUrl = stringResource(R.string.community_guidelines_url)
    val openCommunityGuidelinesIntent = remember {
        Intent(Intent.ACTION_VIEW, Uri.parse(communityGuidelinesUrl))
    }
    val locationPermission = rememberPermissionsState(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(true) {
        locationPermission.requestPermissions()
            .thenCompose {
                locationRequest.requestLocation()
            }
            .thenApply {  }
            .exceptionally(onFailure)
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

            Image(
                painter = bitmapPainter,
                modifier = Modifier
                    .aspectRatio(bitmapPainter.intrinsicSize.width / bitmapPainter.intrinsicSize.height)
                    .fillMaxSize(),
                contentDescription = "Photo just taken of the challenge"
            )

            Spacer(Modifier.height(25.dp))

            LinkText(listOf(
                LinkTextData("By creating a challenge, you agree to GeoHunt's "),
                LinkTextData(
                    text = "Community Guidelines",
                    tag = "",
                    annotation = "",
                    onClick = {
                        context.startActivity(openCommunityGuidelinesIntent)
                    }
                )
            ), style = Typography.h5)

            Spacer(Modifier.height(15.dp))

            if (currentlySubmitting) {
                CircularProgressIndicator()
            }
            else {
                Button(
                    onClick = {
                        currentlySubmitting = true
                        database.createChallenge(
                            thumbnail = bitmap,
                            location = locationRequest.lastLocation.value!!,
                            expirationDate = null
                        ).toCompletableFuture(activity)
                            .thenApply(onChallengeCreated)
                            .thenApply { }
                            .exceptionally(onFailure)
                    },
                    enabled = (locationRequest.lastLocation.value != null) && !currentlySubmitting
                ) {
                    Text("Create challenge")
                }
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
        Log.i("GeoHunt", "Returning from camera")
        if (!it) {
            onFailure(RuntimeException("Failed to take the photo"))
        }
        else {
            BitmapUtils.loadFromFileAsync(file).asTask()
                .thenDo {  bitmap ->
                    Log.i("GeoHunt", "Resizing the bitmap...")
                    BitmapUtils.resizeBitmapToFitAsync(bitmap, R.integer.maximum_number_of_pixel_per_photo).asTask()
                }
                .toCompletableFuture(context.findActivity())
                .thenAccept { bitmap ->
                    Log.i("GeoHunt", "Updating bitmapResult value")
                    bitmapResult.value = bitmap
                }
                .thenApply {  }
                .exceptionally(onFailure)
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
