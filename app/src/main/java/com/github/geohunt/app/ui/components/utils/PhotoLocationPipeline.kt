package com.github.geohunt.app.ui.components.utils

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
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
import com.github.geohunt.app.R
import com.github.geohunt.app.sensor.RequireFineLocationPermissions
import com.github.geohunt.app.ui.components.WithPhoto
import com.github.geohunt.app.utility.createImageFile
import com.ireward.htmlcompose.HtmlText

@Composable
private fun <T> PhotoLocationPipelineForm(
    bitmap: Bitmap,
    viewModel: PhotoPipelineBaseViewModel<T>,
    onFailure: (Throwable) -> Unit,
    onSuccess: (T) -> Unit,
    form: @Composable () -> Unit,
    createResourceStringId: Int
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
                    contentDescription = "Photo"
                )

                Spacer(Modifier.height(15.dp))

                form()

                Spacer(Modifier.height(15.dp))

                HtmlText(
                    text = stringResource(id = R.string.challenge_create_agree_community_link),
                    modifier = Modifier.padding(25.dp, 0.dp),
                    linkClicked = { url ->
                        uriHandler.openUri(url)
                    }
                )

                Spacer(Modifier.height(15.dp))

                CreateNewButton(baseViewModel = viewModel,
                    onFailure = onFailure,
                    onSuccess = onSuccess,
                    createResourceStringId = createResourceStringId)
            }
        }
    }
    else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
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
private fun <T> CreateNewButton(
    baseViewModel: PhotoPipelineBaseViewModel<T>,
    onFailure: (Throwable) -> Unit,
    onSuccess: (T) -> Unit,
    createResourceStringId: Int
) {
    val context = LocalContext.current
    val state = baseViewModel.submittingState.collectAsState()

    if (state.value == PhotoPipelineBaseViewModel.State.CREATING) {
        CircularProgressIndicator()
    }
    else {
        Button(
            onClick = {
                baseViewModel.create(
                    { suffix ->
                        context.createImageFile(suffix)
                    },
                    onFailure = onFailure,
                    onSuccess = onSuccess
                )
            },
            enabled = state.value == PhotoPipelineBaseViewModel.State.READY_TO_CREATE
        ) {
            Text(stringResource(createResourceStringId))
        }
    }
}

@Composable
fun <T> PhotoLocationPipeline(
    baseViewModel: PhotoPipelineBaseViewModel<T>,
    onFailure: (Throwable) -> Unit,
    onSuccess: (T) -> Unit,
    createResourceStringId: Int,
    form: @Composable () -> Unit
) {
    val photoState = baseViewModel.photoState.collectAsState()
    DisposableEffect(Unit) {
        onDispose {
            baseViewModel.reset()
        }
    }

    WithPhoto(onFailure) { file ->
        RequireFineLocationPermissions {
            LaunchedEffect(baseViewModel, file) {
                baseViewModel.withPhoto(file, onFailure)
                baseViewModel.startLocationUpdate(onFailure)
            }

            if (photoState.value != null) {
                PhotoLocationPipelineForm(
                    bitmap = photoState.value!!,
                    viewModel = baseViewModel,
                    onFailure = onFailure,
                    onSuccess = onSuccess,
                    createResourceStringId = createResourceStringId,
                    form = form
                )
            }
        }
    }
}