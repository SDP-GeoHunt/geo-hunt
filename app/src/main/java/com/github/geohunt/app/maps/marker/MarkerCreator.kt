package com.github.geohunt.app.maps.marker

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberMarkerState
import java.time.LocalDateTime
import java.time.Month

//Hardcoded list used to test correct display of events on the map
private val mockBitmap: Bitmap = Bitmap.createBitmap(IntArray(120*120){ Color.CYAN}, 90, 90, Bitmap.Config.ARGB_8888)
private val mockChallengeDatabase : List<MarkerData> = listOf(
    MarkerData("Event 1", mockBitmap, LatLng(46.51958, 6.56398), LocalDateTime.of(2023, Month.MAY, 1, 19, 39, 12)),
    MarkerData("Event 2", mockBitmap, LatLng(46.52064, 6.56780), LocalDateTime.of(2023, Month.MAY, 2, 12, 24, 35)),
    MarkerData("Event 3", mockBitmap, LatLng(46.51881, 6.56779), LocalDateTime.of(2023, Month.MAY, 3, 16, 12, 12))
)

/**
 * Adds the data from the database to the map as markers
 */
@Composable
fun DisplayMarkers() {
    mockChallengeDatabase.forEach{ challenge ->
        MarkerInfoWindowContent(
            state = rememberMarkerState(position = challenge.coordinates),
            title = challenge.title,
            snippet = challenge.expiryDate.toString(),
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)//challenge.image
            //image = challenge.image
        )

//TODO add displaying of the image in the marker when clicked
        /*val marker = map.addMarker(
            MarkerOptions()
                .title(challenge.title)
                .position(challenge.coordinates)
        )
        marker?.tag = challenge*/
    }
}