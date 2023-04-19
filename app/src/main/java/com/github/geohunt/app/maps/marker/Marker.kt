package com.github.geohunt.app.maps.marker

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.time.LocalDateTime

data class Marker(
    val markerPosition: LatLng,
    val markerTitle: String,
    val markerSnippet: String,
    val image: Bitmap,
    val expiryDate: LocalDateTime,
) : ClusterItem {
    override fun getPosition(): LatLng =
        markerPosition

    override fun getTitle(): String =
        markerTitle

    override fun getSnippet(): String =
        markerSnippet
}
