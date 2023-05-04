package com.github.geohunt.app.maps

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.maps.marker.Marker
import com.github.geohunt.app.maps.marker.MarkerDisplay
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.ui.screens.home.HomeViewModel
import com.github.geohunt.app.ui.screens.maps.MapsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.flow.forEach
import java.time.LocalDateTime
import java.time.Month
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

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
            Log.d("MAPS SEEN CHALLENGES RAD", radius.toString())
            //val radius = 30.0 //TODO change this value later
            val location = Location(latitude, longitude)
            val neighboringSectors = location.getNeighboringSectors(radius)

            val markersList = remember { mutableStateListOf<Marker>() }

            viewModel.retrieveChallengesMultiHash(neighboringSectors)

            val challenges = viewModel.challenges.collectAsStateWithLifecycle()


            challenges.value?.forEach {
                Log.d("MAPS SEEN CHALLENGES", it.toString())

                val marker = Marker(
                    markerPosition = LatLng(it.location?.latitude ?: 0.0, it.location?.longitude ?: 0.0),
                    markerTitle = it.id ?: "",
                    markerSnippet = it.description ?: "",
                    //TODO use string url a bit later
                    image = mockBitmap,
                    expiryDate = it.expirationDate ?: LocalDateTime.of(2024, Month.MAY, 1, 19, 0),
                )

                markersList.add(marker)
            }

            MarkerDisplay(items = markersList)

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
