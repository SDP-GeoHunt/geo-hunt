package com.github.geohunt.app.ui.components.buttons

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.theme.geoHuntRed

/**
 * Creates the "Hunt" button.
 *
 * This is shown to users who are not hunting the challenge, and did not claim it yet.
 *
 * @param onClick Click handler.
 * @param enabled Whether the button is enabled. The button should be disabled when the user is hunting.
 */
@Composable
fun HuntButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = geoHuntRed
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.target_arrow),
            contentDescription = "Hunt",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(stringResource(id = R.string.join_hunt))
    }
}

/**
 * Creates the "Claim" button.
 *
 * This button is shown to the user only if he is already hunting the challenge, and did not claim
 * it yet.
 *
 * @param onClick Click handler.
 * @param enabled Whether the button is enabled. The button should be disabled when the user is claiming.
 */
@Composable
fun ClaimButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = geoHuntRed
        )
    ) {
        Icon(
            Icons.Default.AutoAwesome,
            contentDescription = "Claim",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(stringResource(id = R.string.claim))
    }
}

/**
 * Creates the "Claimed" indicator. This is shown to users that already claimed the challenge,
 * and the button should open their claim.
 *
 * @param onClick The click handler.
 */
@Composable
fun ClaimedButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = "Checkmark"
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(stringResource(id = R.string.claimed))
    }
}

/**
 * Represents the state of a challenge with regards to the current user.
 *
 * This is used to show the appropriate button to the user.
 */
enum class ChallengeHuntState {
    UNKNOWN, // Used when loading the challenge hunt state
    NOT_HUNTED,
    HUNTED,
    CLAIMED
}

/**
 * Displays an adaptive button that shows the appropriate action with regards to the current
 * [ChallengeHuntState] of the user, i.e. shows "Claimed" when the challenge is claimed,
 * "Claim" when the user is hunting, and "Hunt" when the user is not hunting the challenge.
 *
 * @param state The current hunt state of the challenge.
 * @param onHunt On clicking the [HuntButton].
 * @param onClaim On clicking the [ClaimButton].
 * @param showClaim On clicking the [ClaimedButton].
 * @param isBusy Whether the view model is busy hunting/claiming the challenge and the buttons should
 *               be disabled.
 */
@Composable
fun HuntClaimButton(
    state: ChallengeHuntState,
    onHunt: () -> Unit,
    onClaim: () -> Unit,
    showClaim: () -> Unit = { /* TODO Create a claim page */ },
    isBusy: () -> Boolean = { false }
) {
    when (state) {
        ChallengeHuntState.UNKNOWN -> HuntButton(onClick = {}, enabled = false)
        ChallengeHuntState.NOT_HUNTED -> HuntButton(onClick = onHunt, enabled = !isBusy())
        ChallengeHuntState.HUNTED -> ClaimButton(onClick = onClaim, enabled = !isBusy())
        ChallengeHuntState.CLAIMED -> ClaimedButton(onClick = showClaim)
    }
}