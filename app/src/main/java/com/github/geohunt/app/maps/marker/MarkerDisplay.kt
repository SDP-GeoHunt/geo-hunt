package com.github.geohunt.app.maps.marker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
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
import com.google.maps.android.compose.*
import com.google.maps.android.compose.clustering.Clustering

/*
 * Displays the markers on the map
 * and clusters them when they are close to each other
 *
 * @param items The list of markers to display
 */
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MarkerDisplay(items: List<Marker>) {
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
                    MarkerInfoBox(marker = marker)
                }
            }
            Image(
                painter = painterResource(id = R.drawable.marker),
                contentDescription = "Marker Icon for ${marker.title}",
                modifier = Modifier
                    .size(50.dp)
                    .alpha(if (selectedMarker == marker) 0f else 1f)
                    // Clickable modifier to make the marker clickable for the tests
                    .clickable(onClick = {})
            )
        },

        // Render the clustered markers
        clusterContent = { cluster ->
            val clusterSize = cluster.size

            Surface(
                modifier = Modifier.size(determineClusterSize(clusterSize)).testTag("Cluster for $clusterSize markers"),
                shape = CircleShape,
                color = determineClusterColor(clusterSize),
                contentColor = Color.Black,
                border = BorderStroke(4.dp, Color.Green)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "%,d".format(clusterSize),
                        fontSize = determineFontSize(clusterSize),
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
