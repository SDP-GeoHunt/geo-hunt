package com.github.geohunt.app.maps.marker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.github.geohunt.app.R
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberMarkerState
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Displays provided markers on the map
 *
 * @param markers The list of markers to display
 */
@Composable
fun DisplayMarkers(
    markers: List<Marker>,
    showChallengeView: MutableState<Boolean>,
    challengeId: MutableState<String>,
) {
    markers.forEach { challenge ->
            MarkerInfoWindow(
                state = rememberMarkerState(position = challenge.coordinates),
                title = challenge.id,
                snippet = challenge.expiryDate.toString(),
                onInfoWindowClick = {
                    challengeId.value = challenge.id
                    showChallengeView.value = true
                },
                tag = challenge.id,
            ) {
                val imageLoaded = remember { mutableStateOf(false) }
                MarkerInfoWindowContent(challenge, imageLoaded)
            }
        }
    }


/**
 * The content of the info window that is displayed when a marker is clicked
 *
 * @param challenge The challenge that the marker represents
 */
@Composable
fun MarkerInfoWindowContent(
    challenge: Marker,
    imageLoaded: MutableState<Boolean> = remember { mutableStateOf(false) }
){
   // val imageLoaded = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(35.dp, 35.dp, 35.dp, 35.dp)
            )
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            //The image displayed at the top of the info window
            if (challenge.image != "") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("Marker image")
                ) {
                    val imagePainter = ImageRequest.Builder(LocalContext.current)
                            .data(data = challenge.image)
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                                allowHardware(false)
                                scale(Scale.FILL)
                                listener(
                                    onSuccess = { _, _ ->
                                        imageLoaded.value = true
                                    }
                                )
                            }).build()

                    AsyncImage(
                        model = imagePainter,
                        modifier = Modifier
                            .size(160.dp)
                            .align(Alignment.CenterHorizontally)
                            .testTag("Marker image"),
                        contentDescription = if (imageLoaded.value) "Marker Image" else "Loading",
                    )


                }
            } else {
                Image(
                    painter = painterResource(id = R.drawable.radar_icon),
                    contentDescription = "Marker Image",
                    modifier = Modifier
                        .size(90.dp)
                        .padding(top = 16.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            //The bottom text containing the expiry date of the challenge
            Text(
                text = getExpiryString(challenge.expiryDate),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                    .testTag("Marker expiry date")
                    .fillMaxWidth(),
                style = MaterialTheme.typography.headlineSmall,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Returns a string that represents the time until the challenge expires
 *
 * @param expiryDate The date that the challenge expires
 *
 * @return A string that represents the time until the challenge expires
 */
fun getExpiryString(
    expiryDate: LocalDateTime,
    currentDate: LocalDateTime = LocalDateTime.now()
): String {
    val diffInMinutes = ChronoUnit.MINUTES.between(currentDate, expiryDate)
    if (diffInMinutes < 60) {
        return "Expires in $diffInMinutes minute(s)"
    }

    val diffInHours = ChronoUnit.HOURS.between(currentDate, expiryDate)
    if (diffInHours < 24) {
        return "Expires in $diffInHours hour(s)"
    }

    val diffInDays = ChronoUnit.DAYS.between(currentDate, expiryDate)
    if (diffInDays < 30) {
        return "Expires in $diffInDays day(s)"
    }

    val diffInMonths = ChronoUnit.MONTHS.between(currentDate, expiryDate)
    if (diffInMonths < 12) {
        return "Expires in $diffInMonths month(s)"
    }

    val diffInYears = ChronoUnit.YEARS.between(currentDate, expiryDate)
    return "Expires in $diffInYears year(s)"
}