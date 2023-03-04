package com.github.geohunt.app.sensor

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.database.models.Location
import com.github.geohunt.app.utility.map
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlin.math.roundToInt

class ForegroundLocationRequest(private val activity: Activity) {

    private var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    private var cancellationTokenSource = CancellationTokenSource()

    @Composable
    fun getCurrentLocationRequester() : () -> Task<Location> {
        val token = cancellationTokenSource.token
        val context = LocalContext.current

        val task = attachPermissionToTaskLaunch(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("GeoHunt", "Check for the application permission failed");
                throw IllegalStateException("Check for application permission failed")
            }
            else {
                fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, token)
            }
        }

        return {
            task().map {
                // Purposefully deteriorate the location result for debug mode (database currently public)
                if (BuildConfig.DEBUG) {
                    val latitude = (it.latitude * 15.0).roundToInt().toDouble() / 15.0
                    val longitude = (it.longitude * 15.0).roundToInt().toDouble() / 15.0
                    Location(latitude, longitude)
                }
                else {
                    Location(it.latitude, it.longitude)
                }
            }
        }
    }

    fun cancel() {
        cancellationTokenSource.cancel()
    }
}
