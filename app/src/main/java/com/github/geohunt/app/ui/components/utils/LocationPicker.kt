package com.github.geohunt.app.ui.components.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Location
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
private fun LocationDialog(
    location: Location?,
    setLocation: (Location) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp, 5.dp),
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.pick_location_popup_title),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(0.dp, 3.dp)
                )

                val mapState = location?.run {
                    rememberMarkerState(position = LatLng(location.latitude, location.longitude))
                }

                GoogleMap(
                    modifier = Modifier
                        .testTag("gg-map-component")
                        .padding(2.dp, 5.dp)
                        .weight(1.0f),
                    properties = remember { MapProperties(isMyLocationEnabled = true) },
                    onMapClick = {
                        setLocation(Location(it.latitude, it.longitude))
                        mapState?.apply {
                            this.position = it
                        }
                        onDismissRequest()
                    },
                ) {
                    mapState?.apply {
                        Marker(
                            state = this
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LocationPicker(location: Location?, setLocation: (Location) -> Unit) {
    var showLocationDialog by remember {
        mutableStateOf(false)
    }

    TextField(value = location?.toString() ?: stringResource(R.string.location_picker_placeholder),
        onValueChange = {},
        readOnly = true,
        enabled = false,
        modifier = Modifier
            .testTag("location-picker-field")
            .clickable { showLocationDialog = true })

    if (showLocationDialog) {
        LocationDialog(location = location, setLocation = setLocation) {
            showLocationDialog = false
        }
    }
}
