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
import com.github.geohunt.app.ui.components.utils.PhotoLocationPipeline
import com.github.geohunt.app.utility.*
import com.ireward.htmlcompose.HtmlText

@Composable
fun CreateNewChallenge(
    onFailure: (Throwable) -> Unit = {},
    onSuccess: (Challenge) -> Unit = {},
    viewModel: CreateChallengeViewModel = viewModel(factory = CreateChallengeViewModel.Factory)
) {
    DisposableEffect(viewModel) {
        onDispose {
            viewModel.reset()
        }
    }

    PhotoLocationPipeline(
        submitCallback = { localPicture, location ->
            viewModel.create(location, localPicture, onFailure, onSuccess)
        },
        onFailure = onFailure,
        buttonText = stringResource(R.string.create_challenge_button)
    ) {
        if (viewModel.displaySetting) {
            val expirationDate = viewModel.expirationDate.collectAsState()
            val difficulty = viewModel.selectedDifficulty.collectAsState()

            ChallengeSettings(
                difficulty = difficulty.value,
                setDifficultyCallback = viewModel::withDifficulty,
                expirationDate = expirationDate.value,
                setExpirationDate = viewModel::withExpirationDate
            )

            Spacer(Modifier.height(15.dp))
        }

        true
    }
}

