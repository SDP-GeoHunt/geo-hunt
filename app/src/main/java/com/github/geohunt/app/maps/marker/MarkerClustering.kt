package com.github.geohunt.app.maps.marker

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
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
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.compose.*
import com.google.maps.android.compose.clustering.Clustering

//TODO doc
@Composable
fun DisplayMarkerInfo(marker: Marker) {
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

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MarkerClustering(items: List<Marker>) {
    var selectedMarker: Marker? by remember { mutableStateOf(null) }

    val isMarkerClicked = remember { mutableStateOf(false) }

    Clustering(
        items = items,

        // When clicked, update the selected marker
        // and set the markerClicked state to true
        onClusterItemClick = { marker ->
            selectedMarker = marker
            isMarkerClicked.value = true

            true
        },

        // Render the non clustered markers
        // and info window if the marker is clicked
        clusterItemContent = {marker ->
            if (isMarkerClicked.value) {
                if (selectedMarker == marker) {
                    DisplayMarkerInfo(marker = marker)
                }
            }
            Image(
                painter = painterResource(id = R.drawable.marker),
                contentDescription = "Marker Icon",
                modifier = Modifier
                    .size(90.dp)
                    .alpha(if (selectedMarker == marker) 0f else 1f)
            )
        },

        // Render the clustered markers
        clusterContent = { cluster ->
            val clusterSize = cluster.size

            Surface(
                modifier = Modifier.size(determineClusterSize(clusterSize)),
                shape = CircleShape,
                color = determineClusterColor(clusterSize),
                contentColor = Color.Black,
                border = BorderStroke(4.dp, Color.Green)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "%,d".format(clusterSize),
                        fontSize = determineFontSize(clusterSize),
                        //fontSize = determineFontSize(clusterSize),
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                        )
                }
            }
        }
    )
}

/*
 * Determines the color of the cluster based on the size of the cluster
 *
 * @param clusterSize The size of the cluster
 */
private fun determineClusterColor(clusterSize: Int): Color {
    return when {
        clusterSize < 10 -> Color.Green
        clusterSize < 100 -> Color.Yellow
        else -> geoHuntRed
    }
}

/*
 * Determines the diameter of the cluster based on the size of the cluster
 *
 * @param clusterSize The size of the cluster
 */
private fun determineClusterSize(clusterSize: Int): Dp {
    return when {
        clusterSize < 10 -> 40.dp
        clusterSize < 100 -> 60.dp
        else -> 80.dp
    }
}

/*
 * Determines the font size of the cluster based on the size of the cluster
 *
 * @param clusterSize The size of the cluster
 */
private fun determineFontSize(clusterSize: Int): TextUnit {
    return when {
        clusterSize < 10 -> 16.sp
        clusterSize < 100 -> 20.sp
        else -> 26.sp
    }
}
