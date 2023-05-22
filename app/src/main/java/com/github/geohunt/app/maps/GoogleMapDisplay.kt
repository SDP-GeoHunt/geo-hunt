package com.github.geohunt.app.maps

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.maps.marker.DisplayMarkers
import com.github.geohunt.app.maps.marker.Marker
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.ui.screens.maps.MapsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.time.LocalDateTime
import java.time.Month
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

private val epflCoordinates = LatLng(46.51958, 6.56398)

/*
 * Displays the Google Map and its content
 *
 * @param modifier the modifier
 * @param cameraPosition the camera position
 * @param viewModel the view model used to access challenges from the database
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
    val mapProperties by remember { mutableStateOf(MapProperties(
        mapType = MapType.NORMAL,
        isMyLocationEnabled = true,
        isBuildingEnabled = true,
        isIndoorEnabled = true,
    )) }
    val mapVisible by remember { mutableStateOf(true) }


    //var cameraPosition: CameraPosition = CameraPosition(epflCoordinates, 10f, 0f, 0f)

    val cameraPositionState = rememberCameraPositionState { position = cameraPosition }

    /*val loc = remember { mutableStateOf<Location?>(null) }
    val curLoc = viewModel.currentLocation.collectAsStateWithLifecycle()

    DisposableEffect(viewModel) {
        onDispose {
            viewModel.reset()
        }
    }

    RequireFineLocationPermissions {
        LaunchedEffect(viewModel) {
            viewModel.startLocationUpdate()
        }
    }

    if (curLoc.value == null) {
        Box(modifier = Modifier
            .aspectRatio(1f)
            .fillMaxSize(0.5f))
        {
            CircleLoadingAnimation()
        }
    }

    loc.value = curLoc.value

    cameraPosition = CameraPosition(LatLng(loc.value!!.latitude, loc.value!!.longitude), 10f, 0f, 0f)
    cameraPositionState.position = cameraPosition*/

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

            val markers = remember { mutableStateListOf<Marker>() }
            viewModel.updateFetchableChallenges(neighboringSectors)
            val challenges = viewModel.challenges.collectAsStateWithLifecycle()
            challenges.value?.forEach {
                val marker = Marker(
                    title = it.id,
                    image = it.photoUrl,
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

