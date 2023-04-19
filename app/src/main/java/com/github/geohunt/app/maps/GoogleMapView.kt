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
private val mockChallengeDatabase : List<Marker> = listOf<Marker>(
    Marker(
        LatLng(46.51958, 6.56398),
        "Event 1",
        "Expires on 1 May 2024 at 19:39",
        mockBitmap,
        LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12)
    ),
    Marker(
        LatLng(46.519, 6.563),
        "Event 2",
        "Expires on 1 May 2024 at 19:39",
        mockBitmap,
        LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12)
    ),
    Marker(
        LatLng(46.5192, 6.5632),
        "Event 3",
        "Expires on 1 May 2024 at 19:39",
        mockBitmap,
        LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12)
    ),
    Marker(
        LatLng(46.5193, 6.5633),
        "Event 4",
        "Expires on 1 May 2024 at 19:39",
        mockBitmap,
        LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12)
    ),
)

@Composable
fun createListOfMockMarkers(): List<Marker> {
    val mockChallengeDatabase = remember { mutableStateListOf<Marker>()}

        //remember { mutableStateListOf<Marker>() }

    LaunchedEffect(Unit) {
        for (i in 1..100) {
            mockChallengeDatabase.add(Marker(
                LatLng(46.51958 + i * 0.01, 6.56398 + i * 0.01),
                "Event $i",
                "Expires on 1 May 2024 at 19:39",
                mockBitmap,
                LocalDateTime.of(2024, Month.MAY, 1, 19, 39, 12)
            )
            )
        }
    }

    return mockChallengeDatabase
}

@Composable
fun GoogleMapView(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    content: @Composable () -> Unit = {}
) {
    val uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }
    val mapProperties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }
    val mapVisible by remember { mutableStateOf(true) }

    if (mapVisible) {

        GoogleMap(
            modifier = modifier,
            cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(LatLng(46.51958, 6.56398), 15f)
            },

            //cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
        ) {
            val markers = createListOfMockMarkers()

            MarkerDisplay(items = markers)

            content()
        }
    }
}
