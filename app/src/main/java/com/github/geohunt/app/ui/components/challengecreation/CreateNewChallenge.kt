package com.github.geohunt.app.ui.components.challengecreation

import android.Manifest
import android.graphics.Bitmap
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
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.sensor.rememberPermissionsState
import com.github.geohunt.app.utility.*

@Composable
fun CreateChallengeForm(
    bitmap: Bitmap,
    viewModel: CreateChallengeViewModel,
    onFailure: (Throwable) -> Unit,
    onSuccess: (Challenge) -> Unit
) {
    val bitmapPainter = remember(bitmap) { BitmapPainter(bitmap.asImageBitmap()) }
    val locationPermission = rememberPermissionsState(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(viewModel) {
        locationPermission.requestPermissions()
            .thenAccept {
                viewModel.startLocationUpdate(onFailure)
            }
            .thenApply { }
            .exceptionally(onFailure)
    }

    val location = viewModel.location.collectAsState()

    if (location.value != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = bitmapPainter,
                    modifier = Modifier
                        .aspectRatio(bitmapPainter.intrinsicSize.width / bitmapPainter.intrinsicSize.height)
                        .fillMaxSize(0.5f),
                    contentDescription = "Photo just taken of the challenge"
                )

                Spacer(Modifier.height(15.dp))

                ChallengeSettings(viewModel)

                Spacer(Modifier.height(15.dp))

//TODO: This code is going to change with i18n PR anyway so...
//                LinkText(listOf(
//                    LinkTextData("By creating a challenge, you agree to GeoHunt's "),
//                    LinkTextData(
//                        text = "Community Guidelines",
//                        tag = "",
//                        annotation = "",
//                        onClick = {
//                            context.startActivity(openCommunityGuidelinesIntent)
//                        }
//                    )
//                ), style = Typography.h5)

                Spacer(Modifier.height(15.dp))

                CreateNewChallengeButton(viewModel = viewModel,
                    onFailure = onFailure,
                    onSuccess = onSuccess)

            }
        }
    }
}

@Composable
private fun CreateNewChallengeButton(
    viewModel: CreateChallengeViewModel,
    onFailure: (Throwable) -> Unit,
    onSuccess: (Challenge) -> Unit
) {
    val context = LocalContext.current
    val state = viewModel.submittingState.collectAsState()

    if (state.value == CreateChallengeViewModel.State.CREATING) {
        CircularProgressIndicator()
    }
    else {
        Button(
            onClick = {
                viewModel.create(
                    { suffix ->
                        context.createImageFile(suffix)
                    },
                    onFailure = onFailure,
                    onSuccess = onSuccess
                )
            },
            enabled = state.value == CreateChallengeViewModel.State.READY_TO_CREATE
        ) {
            Text(text = "Create challenge")
        }
    }
}


@Composable
fun CreateNewChallenge(
    onFailure: (Throwable) -> Unit = {},
    onSuccess: (Challenge) -> Unit = {},
    viewModel: CreateChallengeViewModel = viewModel(factory = CreateChallengeViewModel.Factory)
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
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        Log.i("GeoHunt", "Returning from camera")
        if (!it) {
            @Suppress("ThrowableNotThrown")
            onFailure(RuntimeException("Failed to take photo at ${file.absolutePath}"))
        }
        else {
            viewModel.withPhoto(file, onFailure)
        }
    }

    val permission = rememberPermissionsState(
        Manifest.permission.CAMERA
    )

    val photoState = viewModel.photoState.collectAsState()

    if (photoState.value != null) {
        CreateChallengeForm(
            bitmap = photoState.value!!,
            viewModel = viewModel,
            onFailure = onFailure,
            onSuccess = onSuccess
        )
    }
    else {
        LaunchedEffect(viewModel) {
            permission.requestPermissions()
                .thenApply {
                    cameraLauncher.launch(uri)
                }
                .exceptionally(onFailure)
        }
    }
}
