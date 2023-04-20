package com.github.geohunt.app.maps.marker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R

/**
 * Displays the window containing challenge information
 * that is represented on the map by the provided marker
 *
 * @param marker The marker to display the challenge information for
 */
@Composable
fun MarkerInfoBox(marker: Marker) {
    Box(
        modifier = Modifier.testTag("MarkerInfoBox")
            .background(
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(35.dp, 35.dp, 35.dp, 35.dp)

            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //The image displayed at the top of the info window
            Image(
                painter = painterResource(id = R.drawable.radar_icon),
                contentDescription = "Radar Icon",
                modifier = Modifier
                    .size(90.dp)
                    .padding(top = 16.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            //The middle text containing the title of the challenge
            Text(
                text = marker.title,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(),
                style = MaterialTheme.typography.displayMedium,
            )

            //The bottom text containing the expiry date of the challenge
            Text(
                text = marker.expiryDate.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.headlineSmall,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
