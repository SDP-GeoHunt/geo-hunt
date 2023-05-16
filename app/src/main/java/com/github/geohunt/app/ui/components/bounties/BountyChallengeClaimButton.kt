package com.github.geohunt.app.ui.components.bounties

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.sensor.LocationRequestState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

typealias ClaimChallengeFunction = suspend (LocalPicture, Challenge, Location) -> Unit

@Composable
fun BountyChallengeClaimButton(
    challenge: Challenge,
    locationRequestState: LocationRequestState,
    photo: LocalPicture,
    claimChallengeFunction: ClaimChallengeFunction,
    isClaimed: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var isClaimedRemember by remember { mutableStateOf(isClaimed) }

    Button(
        onClick = {
            if (!isClaimedRemember) {
                coroutineScope.launch {
                    try {
                        isLoading = true

                        // Request the user's location
                        val locationDeferred = coroutineScope.async { locationRequestState.requestLocation().get() }

                        try {
                            // Call the suspending function within a coroutine
                            claimChallengeFunction(photo, challenge, locationDeferred.await())
                        } catch (e: UserNotLoggedInException) {
                            // Handle the exception as appropriate
                            Log.e("Location Request", "Failed to get location", e)
                        } finally {
                            isLoading = false
                            isClaimedRemember = true
                        }
                    } catch (e: Exception) {
                        // Handle any other exceptions
                        Log.e("BountyChallengeClaimView", "Error", e)
                        isLoading = false
                    }
                }
            }
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = if (isClaimed) Color.Gray else Color.Red),
        enabled = !isClaimedRemember,
        modifier = Modifier.padding(16.dp)
    ) {
        // Display the text based on the isLoading state
        Text(if (isLoading) "Loading..." else if (isClaimedRemember) "Challenge Claimed" else "Claim Challenge")
    }
}
