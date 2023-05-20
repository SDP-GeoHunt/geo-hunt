package com.github.geohunt.app.sensor

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.utility.Singleton
import com.github.geohunt.app.utility.findActivity
import com.github.geohunt.app.utility.toCompletableFuture
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.concurrent.CompletableFuture

/**
 * State of a location request
 */
interface LocationRequestState {
    /**
     * The location result of the request or null if the requests
     * did not finished or was not completed successfully
     */
    val lastLocation : MutableState<Location?>

    /**
     * Launch the request for the location
     *
     * This should always be triggered from non-composable scope, for example, from a side-effect
     * or non-composable callback. Otherwise this will result in an IllegalStateException
     *
     * Launch the location request and await the result
     */
    fun requestLocation() : CompletableFuture<Location>
}


/**
 * Default implementation for the [LocationRequestState]
 */
private class LocationRequestAndroidImplementation(private val context : Context,
                                                   override val lastLocation : MutableState<Location?>,
                                                   val multiplePermissionState : MultiplePermissionState) : LocationRequestState
{
    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context.findActivity())
    private val cancellationTokenSource = CancellationTokenSource()
    private var currentFuture : CompletableFuture<Location>? = null

    /**
     * Request the location after having check that the corresponding permissions were granted
     */
    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    private fun requestLocationWithPermissions(activity: Activity) {
        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .toCompletableFuture(activity)
            .thenApply {
                var location = Location(it.latitude, it.longitude)
                // The server is currently public and therefore we should maybe protect our location :)
                if (BuildConfig.DEBUG) {
                    location = location.getCoarseLocation()
                }
                lastLocation.value = location
                currentFuture!!.complete(location)
            }
            .exceptionally(currentFuture!!::completeExceptionally)
    }

    override fun requestLocation(): CompletableFuture<Location> {
        if (currentFuture != null) {
            return currentFuture!!
        }

        val activity = context.findActivity()
        val future = CompletableFuture<Location>()
        currentFuture = future

        multiplePermissionState.requestPermissions().thenRun {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                future.completeExceptionally(IllegalStateException("Permission race condition detected"))
            }
            else {
                requestLocationWithPermissions(activity)
            }
        }

        return future
    }
}


