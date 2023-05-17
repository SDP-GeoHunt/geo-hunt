package com.github.geohunt.app.ui.components.bounties

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.ui.components.teamprogress.ConfirmHuntAlert
import com.github.geohunt.app.ui.components.utils.SkeletonLoading
import com.github.geohunt.app.ui.components.utils.SkeletonLoadingImage
import com.github.geohunt.app.ui.theme.geoHuntRed
import com.github.geohunt.app.utility.quantize

@Composable
fun BountyChallengeCard(
    challenge: Challenge?,
    numberOfHunters: Int,
    onHunt: (Challenge) -> Unit,
    currentLocation: Location?,
    modifier: Modifier = Modifier
) {
    val showAlertDialog = remember { mutableStateOf(false) }
    ConfirmHuntAlert(
        showAlert = showAlertDialog.value,
        onConfirm = {
            if (challenge != null) {
                onHunt(challenge)
                showAlertDialog.value = false
            }
        },
        onCancel = {
            showAlertDialog.value = false
        }
    )

    Card(modifier = modifier) {
        BoxWithConstraints {
            SkeletonLoadingImage(
                url = challenge?.photoUrl,
                width = maxWidth,
                height = 200.dp,
                modifier = Modifier.clip(CardDefaults.shape),
                contentDescription = "Challenge image"
            )
        }

        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.distance),
                        modifier = Modifier.size(24.dp),
                        contentDescription = "Location icon"
                    )

                    SkeletonLoading(
                        value = challenge,
                        width = 80.dp,
                        height = 20.dp
                    ) {
                        val distance = when(currentLocation) {
                            null -> "Unknown distance"
                            else -> currentLocation.distanceTo(it.location).quantize(0.1).toString() + " km"
                        }

                        Text(distance, style = MaterialTheme.typography.labelMedium)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.target_arrow),
                        modifier = Modifier.size(24.dp),
                        contentDescription = "Hunters icon"
                    )

                    SkeletonLoading(
                        value = challenge,
                        width = 120.dp,
                        height = 20.dp
                    ) {
                        Text(
                            when(numberOfHunters) {
                                0 -> "Not hunted yet."
                                else -> "Hunted by $numberOfHunters members"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1.0f))

            Button(
                onClick = {
                    if (challenge != null) {
                        if (numberOfHunters > 0) {
                            showAlertDialog.value = true
                        } else {
                            onHunt(challenge)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = geoHuntRed,
                    contentColor = Color.White
                ),
                enabled = challenge != null,
                modifier = Modifier.width(width = 110.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.target_arrow
                    ),
                    contentDescription = "Hunt icon",
                    tint = Color.White
                )

                Spacer(Modifier.weight(1.0f))

                Text("Hunt")
            }
        }
    }
}