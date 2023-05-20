package com.github.geohunt.app.ui.components.challengecreation

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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.sensor.RequireCameraPermission
import com.github.geohunt.app.sensor.RequireFineLocationPermissions
import com.github.geohunt.app.utility.*
import com.ireward.htmlcompose.HtmlText

@Composable
fun CreateChallengeForm(
    bitmap: Bitmap,
    viewModel: CreateChallengeViewModel,
    onFailure: (Throwable) -> Unit,
    onSuccess: (Challenge) -> Unit
) {
    val uriHandler = LocalUriHandler.current
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

                if (viewModel.displaySetting) {
                    val difficulty = viewModel.selectedDifficulty.collectAsState()
                    val expirationDate = viewModel.expirationDate.collectAsState()

                    ChallengeSettings(
                        difficulty = difficulty.value,
                        setDifficultyCallback = viewModel::withDifficulty,
                        expirationDate = expirationDate.value,
                        setExpirationDate = viewModel::withExpirationDate
                    )

                    Spacer(Modifier.height(15.dp))
                }

                HtmlText(
                    text = stringResource(id = R.string.challenge_create_agree_community_link),
                    modifier = Modifier.padding(25.dp, 0.dp),
                    linkClicked = { url ->
                        uriHandler.openUri(url)
                    }
                )

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
            Text(stringResource(R.string.create_challenge_button))
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
