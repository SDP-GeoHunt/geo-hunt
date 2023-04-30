package com.github.geohunt.app.sensor

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequirePermissions(
    permissions: List<String>,
    withoutPermission: @Composable (MultiplePermissionsState) -> Unit,
    withPermission: @Composable () -> Unit
) {
    val permissionState = rememberMultiplePermissionsState(permissions)

    if (permissionState.allPermissionsGranted) {
        withPermission()
    } else {
        Column {

        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequireCameraPermission(withPermission: @Composable () -> Unit) {
    RequirePermissions(
        permissions = listOf(Manifest.permission.CAMERA),
        withoutPermission = { locationPermissionsState ->
            val textToShow = "GeoHunt requires permission to the camera in order to use this feature"
            val buttonText = "Request camera permission"

            ShowPermissionRequestPage(locationPermissionsState, textToShow, buttonText)
        },
        withPermission
    )
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequireFineLocationPermissions(withPermission: @Composable () -> Unit) {
    RequirePermissions(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        withoutPermission = { locationPermissionsState ->
            val textToShow = "GeoHunt requires the precise location of this device in order to use this feature"
            val buttonText = "Request location permission"

            ShowPermissionRequestPage(locationPermissionsState, textToShow, buttonText)

        }, withPermission
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ShowPermissionRequestPage(
    locationPermissionsState: MultiplePermissionsState,
    textToShow: String,
    buttonText: String
) {
    Column {
        LaunchedEffect(locationPermissionsState) {
            if (!locationPermissionsState.allPermissionsGranted) {
                locationPermissionsState.launchMultiplePermissionRequest()
            }
        }

        Text(text = textToShow)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
            Text(buttonText)
        }
    }
}
