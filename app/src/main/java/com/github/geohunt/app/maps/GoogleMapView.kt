package com.github.geohunt.app.maps

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.github.geohunt.app.maps.marker.DisplayMarkers
import com.google.maps.android.compose.*

@Composable
fun GoogleMapView(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    content: @Composable () -> Unit = {}
) {
    val uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }
    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    val mapVisible by remember { mutableStateOf(true) }

    if (mapVisible) {
        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
        ) {
            DisplayMarkers()

            content()
        }
    }
}
