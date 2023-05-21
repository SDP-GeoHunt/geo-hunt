package com.github.geohunt.app.ui.components.challengecreation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.ui.components.utils.PhotoLocationPipeline

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

