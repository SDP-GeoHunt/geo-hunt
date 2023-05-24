package com.github.geohunt.app.ui.components.utils

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.sensor.RequireFineLocationPermissions
import com.github.geohunt.app.ui.components.CircleLoadingAnimation
import com.github.geohunt.app.ui.components.utils.viewmodels.PhotoLocationPipelineViewModel
import com.ireward.htmlcompose.HtmlText

@Composable
private fun PhotoLocationPipelineInternal(
    settingDrawer: @Composable ColumnScope.() -> Boolean,
    buttonText: String,
    bitmap: Bitmap,
    location: State<Location?>,
    onButtonPressed: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val painter = remember(bitmap) { BitmapPainter(bitmap.asImageBitmap()) }
    var buttonHasBeenPressed by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()) {
        if (location.value == null) {
            Box(modifier = Modifier
                .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height)
                .fillMaxSize(0.5f))
            {
                CircleLoadingAnimation(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
        else {
            Image(
                painter = painter,
                contentDescription = "Photo just taken",
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height)
                    .fillMaxSize(0.5f),
            )
        }

        Spacer(Modifier.height(15.dp))

        val enabled = settingDrawer()

        HtmlText(
            text = stringResource(id = R.string.agree_community_html),
            modifier = Modifier.padding(15.dp, 0.dp),
            linkClicked = { url ->
                uriHandler.openUri(url)
            }
        )

        Spacer(Modifier.height(15.dp))
        
        Button(
            onClick = {
                buttonHasBeenPressed = true
                onButtonPressed()
            },
            enabled = location.value != null && enabled && !buttonHasBeenPressed,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .align(Alignment.CenterHorizontally),
        ) {
            if (!buttonHasBeenPressed) {
                Text(text = buttonText)
            }
            else {
                CircleLoadingAnimation(
                    size = 18.dp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

        Spacer(Modifier.height(15.dp))
    }
}

@Composable
fun PhotoLocationPipeline(
    submitCallback: (LocalPicture, Location) -> Unit,
    buttonText: String,
    onFailure: (Throwable) -> Unit = {},
    viewModel: PhotoLocationPipelineViewModel = viewModel(factory = PhotoLocationPipelineViewModel.Factory),
    settingDrawer: @Composable ColumnScope.() -> Boolean = { true },
) {
    val location = viewModel.location.collectAsState()

    DisposableEffect(viewModel) {
        onDispose {
            viewModel.reset()
        }
    }

    WithPhoto(onFailure = onFailure) { bitmap, localPicture ->
        RequireFineLocationPermissions {
            viewModel.startLocationUpdate(onFailure)

            PhotoLocationPipelineInternal(
                settingDrawer = settingDrawer,
                buttonText = buttonText,
                bitmap = bitmap,
                location = location,
                onButtonPressed = {
                    submitCallback(localPicture, location.value!!)
                }
            )
        }
    }
}
