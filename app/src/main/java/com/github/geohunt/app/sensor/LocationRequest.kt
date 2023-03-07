package com.github.geohunt.app.sensor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
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
interface LocationManagerState {
    /**
     * The location result of the request or null if the requests
     * did not finished or was not completed successfully
     */
    val lastLocation : Location?

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
 * Create a static state that holds the state of the location request
 */
@Composable
fun rememberLocationRequestState() : LocationManagerState {
    val locationManager = LocationManagerAndroidImplementation(LocalContext.current)
    locationManager.multiplePermissionState = rememberPermissionsState(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    return locationManager
}

private class LocationManagerAndroidImplementation(private val context : Context,
                                           @Volatile override var lastLocation: Location? = null
) : LocationManagerState
{
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
                        val location = Location(it.latitude, it.longitude)
                        lastLocation = location
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


