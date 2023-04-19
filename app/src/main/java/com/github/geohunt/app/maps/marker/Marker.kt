package com.github.geohunt.app.maps.marker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import com.github.geohunt.app.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import java.time.LocalDateTime

data class Marker(
    val markerPosition: LatLng,
    val markerTitle: String,
    val markerSnippet: String,
    val image: Bitmap,
    val expiryDate: LocalDateTime,
    val state : MarkerState = MarkerState(position = markerPosition)
    ) : ClusterItem {
    override fun getPosition(): LatLng =
        markerPosition

    override fun getTitle(): String =
        markerTitle

    override fun getSnippet(): String =
        markerSnippet
}
