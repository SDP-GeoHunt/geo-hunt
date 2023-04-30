@file:OptIn(ExperimentalPermissionsApi::class)

package com.github.geohunt.app.ui.components.challengecreation

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Paint.Align
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.sensor.RequireCameraPermission
import com.github.geohunt.app.sensor.RequireFineLocationPermissions
import com.github.geohunt.app.sensor.rememberPermissionsState
import com.github.geohunt.app.utility.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
fun CreateChallengeForm(
    bitmap: Bitmap,
    viewModel: CreateChallengeViewModel,
    onFailure: (Throwable) -> Unit,
    onSuccess: (Challenge) -> Unit
) {
    val bitmapPainter = remember(bitmap) { BitmapPainter(bitmap.asImageBitmap()) }

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
    } else {
        Column {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(0.8f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Awaiting location",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Center
            )
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
    val photoState = viewModel.photoState.collectAsState()
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

    DisposableEffect(Unit) {
        onDispose {
            viewModel.reset()
        }
    }

    RequireCameraPermission {
        LaunchedEffect(viewModel) {
            cameraLauncher.launch(uri)
        }
    }

    if (photoState.value != null) {
        RequireFineLocationPermissions {
            LaunchedEffect(viewModel) {
                viewModel.startLocationUpdate(onFailure)
            }

            CreateChallengeForm(
                bitmap = photoState.value!!,
                viewModel = viewModel,
                onFailure = onFailure,
                onSuccess = onSuccess
            )
        }
    }
}
