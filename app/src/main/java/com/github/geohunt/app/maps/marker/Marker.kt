package com.github.geohunt.app.maps.marker

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.MarkerState
import java.time.LocalDateTime

/* TODO UPDATE DOC LATER
 * Data class representing a marker that represents
 * a challenge on the map
 *
 * @param markerPosition the position of the marker on the map
 * @param markerTitle the title of the marker
 * @param markerSnippet the snippet of the marker
 * @param image the image of the given challenge
 * @param expiryDate the date at which the challenge expires
 * @param state the state of the marker
 */
data class Marker(
    val title: String,
    val image: Bitmap,
    val coordinates: LatLng,
    val expiryDate: LocalDateTime
    )
