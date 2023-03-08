package com.github.geohunt.app.ui.components

import android.Manifest
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.sensor.PhotoRequest
import com.github.geohunt.app.sensor.rememberLocationRequestState
import com.github.geohunt.app.sensor.rememberPermissionsState
import com.github.geohunt.app.utility.onException

@Composable
private fun CreateChallengeForm(
    bitmap: Bitmap,
    database: Database,
    onChallengeCreated: (Challenge) -> Unit,
    onFailure: (Throwable) -> Unit
) {
    val bitmapPainter = remember { BitmapPainter(bitmap.asImageBitmap()) }
    val locationRequest = rememberLocationRequestState()
    val locationPermission = rememberPermissionsState(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val dropdownExpanded = remember { mutableStateOf(false) }


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
                          ).thenApply(onChallengeCreated).exceptionally(onFailure)
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
    val photo = remember(null) {
        mutableStateOf<Bitmap?>(null)
    }

    if (photo.value != null) {
        CreateChallengeForm(photo.value!!, database, onChallengeCreated, onFailure)
    } else {
        val photoRequest = PhotoRequest()

        photoRequest.CameraView(onImageCaptured = {
            photo.value = it
        }, onError = onFailure)
    }
}
