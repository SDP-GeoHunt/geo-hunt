package com.github.geohunt.app.maps.marker

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

@Deprecated("Use Marker instead")
data class MarkerData(
        val title: String,
        val image: Bitmap,
        val coordinates: LatLng,
        val expiryDate: LocalDateTime)
