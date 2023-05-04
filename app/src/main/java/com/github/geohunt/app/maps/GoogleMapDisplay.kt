package com.github.geohunt.app.maps

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.maps.marker.DisplayMarkers
import com.github.geohunt.app.maps.marker.Marker
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.ui.screens.maps.MapsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.time.LocalDateTime
import java.time.Month
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

private val mockBitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
private val epflCoordinates = LatLng(46.51958, 6.56398)

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
    viewModel: MapsViewModel = viewModel(factory = MapsViewModel.Factory),
    content: @Composable () -> Unit = {}
) {
    val uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }
    val mapProperties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }
    val mapVisible by remember { mutableStateOf(true) }
    val cameraPositionState = rememberCameraPositionState {
        position = cameraPosition
    }

    if (mapVisible) {
        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
        ) {
            val coordinateCenter = cameraPositionState.position.target
            val zoom = cameraPositionState.position.zoom
            val latitude = coordinateCenter.latitude
            val longitude = coordinateCenter.longitude

            val radius = 38000 / 2.0.pow((zoom - 3).toDouble()) * cos(latitude * PI / 180)
            val location = Location(latitude, longitude)
            val neighboringSectors = location.getNeighboringSectors(radius)

            //val markersList = mutableListOf<Marker>()
            val markers = remember { mutableStateListOf<Marker>() }

            viewModel.updateFetchableChallenges(neighboringSectors)

            val challenges = viewModel.challenges.collectAsStateWithLifecycle()


            challenges.value?.forEach {
                //Log.d("MAPS SEEN CHALLENGES", it.toString())

                val marker = Marker(
                    title = it.id,
                    //TODO use string url a bit later
                    image = mockBitmap,
                    coordinates = LatLng(it.location.latitude, it.location.longitude),
                    expiryDate = it.expirationDate ?: LocalDateTime.of(2024, Month.MAY, 1, 19, 0),
                )

                markers.add(marker)
            }

            DisplayMarkers(markers = markers)

            content()
        }
    }
}
