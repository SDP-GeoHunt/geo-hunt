package com.github.geohunt.app.ui.components.bounties

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.repository.ImageRepository
import com.github.geohunt.app.data.repository.bounties.BountyClaimRepository
import com.github.geohunt.app.data.repository.bounties.TeamsRepository
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.sensor.LocationRequestState
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun BountyChallengeClaimButton(
    challenge: Challenge,
    bountyReference: DatabaseReference,
    bid: String,
    teamRepository: TeamsRepository,
    imageRepository: ImageRepository,
    locationRequestState: LocationRequestState, // Pass in LocationRequestState
    photo: LocalPicture,
) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val claimRepository = BountyClaimRepository(
        bountyReference = bountyReference,
        bid = bid,
        teamRepository = teamRepository,
        imageRepository = imageRepository,
    )

    Button(
        onClick = {
            coroutineScope.launch {
                try {
                    isLoading = true

                    // Request the user's location
                    val locationDeferred = coroutineScope.async { locationRequestState.requestLocation().get() }

                    try {
                        // Call the suspending function within a coroutine
                        claimRepository.claimChallenge(photo, challenge, locationDeferred.await())
                    } catch (e: UserNotLoggedInException) {
                        // Handle the exception as appropriate
                        Log.e("Location Request", "Failed to get location", e)
                    } finally {
                        isLoading = false
                    }
                } catch (e: Exception) {
                    // Handle any other exceptions
                    Log.e("BountyChallengeClaimView", "Error", e)
                    isLoading = false
                }
            }
        },
        modifier = Modifier.padding(16.dp)
    ) {
        // Display the text based on the isLoading state
        Text(if (isLoading) "Loading..." else "Claim Challenge")
    }
}

