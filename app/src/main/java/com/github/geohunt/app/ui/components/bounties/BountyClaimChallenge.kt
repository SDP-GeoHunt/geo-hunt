package com.github.geohunt.app.ui.components.bounties

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.ui.components.utils.PhotoLocationPipeline

/**
 * The screen to claim a bounty.
 *
 * @param bid The id of the bounty to claim.
 * @param onSuccess The callback to call when the claim is submitted.
 * @param onFailure The callback to call when an error occurs.
 */
@Composable
fun BountyClaimChallenge(
    bid: String,
    cid: String,
    onSuccess: (Claim) -> Unit = {},
    onFailure: (Throwable) -> Unit = {},
    viewModel: BountyClaimViewModel = viewModel(factory = BountyClaimViewModel.getFactory(bid))
) {
    val challenge = viewModel.challenge.collectAsState()

    LaunchedEffect(cid, viewModel) {
        viewModel.start(cid, onFailure)
    }

    DisposableEffect(viewModel) {
        onDispose {
            viewModel.reset()
        }
    }

    PhotoLocationPipeline(
        submitCallback = { localPicture, location ->
            viewModel.claim(location, localPicture, onFailure, onSuccess)
        },
        buttonText = stringResource(id = R.string.claim_challenge_button),
        onFailure = onFailure
    ) {
        challenge.value != null
    }
}
