package com.github.geohunt.app.ui.components.bounties

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.sensor.RequireCameraPermission
import com.github.geohunt.app.sensor.RequireFineLocationPermissions
import com.github.geohunt.app.utility.createImageFile
import com.ireward.htmlcompose.HtmlText

/**
 * The form to submit a claim for a bounty.
 *
 * @param onClaim The callback to call when the claim is submitted.
 */
@Composable
fun BountyChallengeSubmitClaimForm(
    bitmap: Bitmap,
    state: BountyClaimViewModel.State,
    onClaim: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val bitmapPainter = remember(bitmap) { BitmapPainter(bitmap.asImageBitmap()) }

    if (state == BountyClaimViewModel.State.READY_TO_CLAIM ||
        state == BountyClaimViewModel.State.CLAIMING) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
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
                    contentDescription = stringResource(id = R.string.photo_of_the_claim_taken),
                )

                Spacer(Modifier.height(15.dp))

                HtmlText(
                    text = stringResource(id = R.string.claim_create_agree_community_link),
                    modifier = Modifier.padding(25.dp, 0.dp),
                    linkClicked = { url ->
                        uriHandler.openUri(url)
                    }
                )

                Spacer(Modifier.height(15.dp))

                ClaimBountyChallengeButton(onClaim = onClaim, state = state)
            }
        }
    }
    else {
        Column {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(0.8f)
                    .testTag("CircularProgressIndicator")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Awaiting location",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .testTag("AwaitingLocationText"),
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * The button to submit a claim for a bounty.
 *
 * @param state The state of the claim.
 * @param onClaim The callback to call when the claim is submitted.
 */
@Composable
fun ClaimBountyChallengeButton(
    state: BountyClaimViewModel.State,
    onClaim: () -> Unit
) {
    LocalContext.current

    if (state == BountyClaimViewModel.State.CLAIMING) {
        CircularProgressIndicator()
    }
    else {
        Button(
            onClick = {
                onClaim()
            },
            enabled = state == BountyClaimViewModel.State.READY_TO_CLAIM
        ) {
            Text(stringResource(R.string.claim_bounty_challenge_button))
        }
    }
}

/**
 * The screen to claim a bounty.
 *
 * @param bid The id of the bounty to claim.
 * @param onClaimSubmitted The callback to call when the claim is submitted.
 * @param onFailure The callback to call when an error occurs.
 */
@Composable
fun BountyClaimChallenge(
    bid: String,
    cid: String,
    onClaimSubmitted: (Claim) -> Unit = {},
    onFailure: (Throwable) -> Unit = {},
    viewModel: BountyClaimViewModel = viewModel(factory = BountyClaimViewModel.getFactory(bid))
) {
    val state = viewModel.submittingState.collectAsState()
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
        if (!it) {
            onFailure(RuntimeException("Failed to take photo at ${file.absolutePath}"))
        }
        else {
            viewModel.withPhoto(file, onFailure)
        }
    }

    LaunchedEffect(cid, viewModel) {
        viewModel.start(cid, onFailure)
    }

    DisposableEffect(cid, Unit) {
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

            BountyChallengeSubmitClaimForm(
                bitmap = photoState.value!!,
                state = state.value,
                onClaim = {
                    viewModel.claim(
                        { suffix ->
                            context.createImageFile(suffix)
                        },
                        onFailure = onFailure,
                        onSuccess = onClaimSubmitted
                    )
                }
            )
        }
    }
}
