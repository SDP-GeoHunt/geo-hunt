package com.github.geohunt.app.maps.marker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.github.geohunt.app.R
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberMarkerState


/**
 * Displays provided markers on the map
 *
 * @param markers The list of markers to display
 */
@Composable
fun DisplayMarkers(markers: List<Marker>) {
    markers.forEach { challenge ->
        MarkerInfoWindow (
            state = rememberMarkerState(position = challenge.coordinates),
            title = challenge.title,
            snippet = challenge.expiryDate.toString(),
            tag = challenge.title,
        ) {
            MarkerInfoWindowContent(challenge)
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
    challenge: Marker
){
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
            //The image displayed at the top of the info window
            if (challenge.image != "") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("Marker image")
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current).data(data = challenge.image)
                                .apply(block = fun ImageRequest.Builder.() {
                                    crossfade(true)
                                    allowHardware(false)
                                    scale(Scale.FILL)
                                }).build()
                        ),
                        contentDescription = "Marker Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(70.dp)
                            .align(Alignment.CenterHorizontally)
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

            //The middle text containing the title of the challenge
            Text(
                text = challenge.title,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("Marker title"),
                style = MaterialTheme.typography.displayMedium,
            )

            //The bottom text containing the expiry date of the challenge
            Text(
                text = challenge.expiryDate.toString(),
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
