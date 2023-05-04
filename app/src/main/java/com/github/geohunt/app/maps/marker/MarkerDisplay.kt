package com.github.geohunt.app.maps.marker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.theme.geoHuntRed
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.*
import com.google.maps.android.compose.clustering.Clustering

/**
 * Displays provided markers on the map
 *
 * @ param markers The list of markers to display
 */
@Composable
fun DisplayMarkers(markers: List<Marker>) {

    markers.forEach { challenge ->
        MarkerInfoWindowContent(
            state = rememberMarkerState(position = challenge.coordinates),
            title = challenge.title,
            snippet = challenge.expiryDate.toString(),
            icon = BitmapDescriptorFactory.defaultMarker(geoHuntRed.red)
        ) {
            Box(
                modifier = Modifier
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
                        contentScale = ContentScale.Crop)

                    Spacer(modifier = Modifier.height(16.dp))

                    //The middle text containing the title of the challenge
                    Text(
                        text = challenge.title,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.displayMedium,
                    )

                    //The bottom text containing the expiry date of the challenge
                    Text(
                        text = challenge.expiryDate.toString(),
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
    }
}
