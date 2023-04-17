package com.github.geohunt.app.maps.marker

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.time.LocalDateTime

class Marker(private val position: LatLng,
             private val title: String = "",
             private val snippet: String,
             val image: Bitmap,
             val expiryDate: LocalDateTime) : ClusterItem {

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }
}