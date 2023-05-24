package com.github.geohunt.app.maps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.maps.marker.DisplayMarkers
import com.github.geohunt.app.maps.marker.Marker
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.sensor.RequireFineLocationPermissions
import com.github.geohunt.app.ui.components.CircleLoadingAnimation
import com.github.geohunt.app.ui.components.challenge.ChallengeView
import com.github.geohunt.app.ui.screens.maps.MapsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.compose.widgets.ScaleBar
import java.time.LocalDateTime
import java.time.Month
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

/*
 * Displays the Google Map and its content
 *
 * @param modifier the modifier
 * @param cameraPosition the camera position
 * @param viewModel the view model used to access challenges from the database
 */
@Composable
fun GoogleMapDisplay(
    modifier: Modifier = Modifier,
    viewModel: MapsViewModel = viewModel(factory = MapsViewModel.Factory),
    setCameraPosition: CameraPosition? = null,
) {
    val uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }
    val mapProperties by remember { mutableStateOf(MapProperties(
        mapType = MapType.NORMAL,
        isMyLocationEnabled = true,
        isBuildingEnabled = true,
        isIndoorEnabled = true,
    )) }
    val mapVisible by remember { mutableStateOf(true) }

    val loc = remember { mutableStateOf<Location?>(null) }
    val curLoc = viewModel.currentLocation.collectAsState()

    DisposableEffect(viewModel) {
        onDispose {
            viewModel.reset()
        }
    }

    RequireFineLocationPermissions {
    }

    viewModel.startLocationUpdate()

    val showChallengeView = remember { mutableStateOf(false) }
    val cid = remember { mutableStateOf("") }

    if (showChallengeView.value) {
        val fnClaimHuntCallback: (String) -> Unit = {}

        ChallengeView(
            cid = cid.value,
            fnViewImageCallback = fnClaimHuntCallback,
            fnClaimHuntCallback = fnClaimHuntCallback,
            fnGoBackBtn = { showChallengeView.value = false }
        )
    } else {

        if (curLoc.value == null) {
            CircleLoadingAnimation(
                modifier = Modifier
                    .fillMaxSize()
            )
        } else {
            loc.value = curLoc.value

            var cameraPosition =
                CameraPosition(LatLng(loc.value!!.latitude, loc.value!!.longitude), 12f, 0f, 0f)

            if (setCameraPosition != null)
                cameraPosition = setCameraPosition

            val cameraPositionState = rememberCameraPositionState { position = cameraPosition }

            if (mapVisible) {

                GoogleMap(
                    modifier = modifier,
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = uiSettings,
                ) {
                    val coordinateCenter = cameraPositionState.position.target
                    val zoom = cameraPositionState.position.zoom.coerceAtLeast(12f)
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
                            id = it.id,
                            image = it.photoUrl,
                            coordinates = LatLng(it.location.latitude, it.location.longitude),
                            expiryDate = it.expirationDate ?: LocalDateTime.of(
                                2024,
                                Month.MAY,
                                1,
                                19,
                                0
                            ),
                        )
                        markers.add(marker)
                    }

                    DisplayMarkers(
                        markers = markers,
                        showChallengeView = showChallengeView,
                        challengeId = cid,
                    )
                }

                ScaleBar(
                    modifier = Modifier
                        .padding(top = 5.dp, end = 15.dp),
                    cameraPositionState = cameraPositionState
                )
            }
        }
    }
}

