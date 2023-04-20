package com.github.geohunt.app.maps

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.github.geohunt.app.maps.marker.Marker
import com.github.geohunt.app.maps.marker.MarkerDisplay
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.time.LocalDateTime
import java.time.Month

private val mockBitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
private val epflCoordinates = LatLng(46.51958, 6.56398)
private var challengeDatabase: List<Marker> = mutableListOf()

/*
 * Displays the Google Map and its content
 *
 * @param modifier the modifier
 * @param cameraPosition the camera position
 * @param content the content
 */
@Composable
fun GoogleMapDisplay(
    modifier: Modifier = Modifier,
    cameraPosition: CameraPosition = CameraPosition(epflCoordinates, 10f, 0f, 0f),
    content: @Composable () -> Unit = {}
) {
    val uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }
    val mapProperties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }
    val mapVisible by remember { mutableStateOf(true) }

    if (mapVisible) {
        GoogleMap(
            modifier = modifier,
            cameraPositionState = rememberCameraPositionState {
                position = cameraPosition
            },
            properties = mapProperties,
            uiSettings = uiSettings,
        ) {
            loadChallenges(listOf())
            val markers = challengeDatabase

            MarkerDisplay(items = markers)

            content()
        }
    }
}

/*
 * Loads the challenges from the database
 * If the database is empty, it creates a list of mock markers
 *
 * @param markers a list of markers
 */
fun loadChallenges(markers: List<Marker>){
    challengeDatabase = markers.ifEmpty {
        createListOfMockMarkers()
    }
}

/*
 * Creates a list of mock markers that are used
 * to populate the map with markers
 *
 * @return a list of mock markers
 */
private fun createListOfMockMarkers(): List<Marker> {
    val mockChallengeDatabase =  mutableListOf<Marker>()

    for (i in 1..100) {
        mockChallengeDatabase.add(Marker(
            LatLng(46.51958 + i * 0.01, 6.56398 + i * 0.01),
            "Event $i",
            "Expires on 1 May 2024 at 19:39",
            mockBitmap,
            LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12))
        )
    }

    return mockChallengeDatabase
}