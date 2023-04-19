package com.github.geohunt.app.maps.marker

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.MarkerState
import java.time.LocalDateTime

data class Marker(
    val markerPosition: LatLng,
    val markerTitle: String,
    val markerSnippet: String,
    val image: Bitmap,
    val expiryDate: LocalDateTime,
    val state : MarkerState = MarkerState(position = markerPosition),
    ) : ClusterItem {
    override fun getPosition(): LatLng =
        markerPosition

    override fun getTitle(): String =
        markerTitle

    override fun getSnippet(): String =
        markerSnippet
}
