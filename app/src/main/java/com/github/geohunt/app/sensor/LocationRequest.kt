package com.github.geohunt.app.sensor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.ui.findActivity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.cancellation.CancellationException

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
    fun launchLocationRequest() : CompletableFuture<Location>
}


/**
 * Create a static state that holds an instance of [LocationRequestState]
 */
@Composable
fun rememberLocationRequestState() : LocationRequestState {
    val locationManager = LocationRequestAndroidImplementation(LocalContext.current)
    locationManager.lastLocation = remember { mutableStateOf(null) }
    locationManager.multiplePermissionState = rememberPermissionsState(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    return locationManager
}

/**
 * Default implementation for the [LocationRequestState]
 */
private class LocationRequestAndroidImplementation(private val context : Context) : LocationRequestState
{
    override lateinit var lastLocation : MutableState<Location?>

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context.findActivity())
    private val cancellationTokenSource = CancellationTokenSource()
    private var currentFuture : CompletableFuture<Location>? = null
    internal var multiplePermissionState : MultiplePermissionState? = null

    override fun launchLocationRequest(): CompletableFuture<Location> {
        if (currentFuture != null) {
            return currentFuture!!
        }

        val activity = context.findActivity()
        val future = CompletableFuture<Location>()
        currentFuture = future

        multiplePermissionState!!.launchPermissionRequest().thenRun {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                future.completeExceptionally(IllegalStateException("Permission race condition detected"))
            }
            else
            {
                fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
                    .addOnSuccessListener(activity) {
                        var location = Location(it.latitude, it.longitude)
                        // The server is currently public and therefore we should maybe protect our location :)
                        if (BuildConfig.DEBUG) {
                            location = location.getCoarseLocation()
                        }
                        lastLocation.value = location
                        future.complete(location)
                    }
                    .addOnFailureListener(activity) {
                        future.completeExceptionally(it)
                    }
                    .addOnCanceledListener(activity) {
                        future.completeExceptionally(CancellationException())
                    }
            }
        }

        return future
    }
}


