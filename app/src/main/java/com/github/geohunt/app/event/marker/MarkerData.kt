package com.github.geohunt.app.event.marker

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

data class MarkerData(
        val title: String,
        val image: Bitmap,
        val coordinates: LatLng,
        val expiryDate: LocalDateTime) {
}
