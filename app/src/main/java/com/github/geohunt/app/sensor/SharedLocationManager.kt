package com.github.geohunt.app.sensor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import com.github.geohunt.app.model.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn

class SharedLocationManager(
    private val context: Context
) {
    private var mFusedLocationClient : FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var mLocationRequest : LocationRequest = LocationRequest.Builder(5000)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .setGranularity(Granularity.GRANULARITY_FINE)
        .setIntervalMillis(5000)
        .build()

    @SuppressLint("MissingPermission")
    private fun _locationUpdates(coroutineScope: CoroutineScope) = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                if (result.lastLocation != null) {
                    trySend(Location(result.lastLocation!!.latitude, result.lastLocation!!.longitude))
                }
            }
        }

        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.e("GeoHunt", "No permission provided for fine location")
            return@callbackFlow
        }

        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            callback,
            Looper.getMainLooper()
        ).addOnFailureListener { e ->
            close(e)
        }

        awaitClose {
            mFusedLocationClient.removeLocationUpdates(callback)
        }
    }.shareIn(coroutineScope, replay = 0, started = SharingStarted.WhileSubscribed())

    fun locationFlow(coroutineScope: CoroutineScope) : Flow<Location> {
        return _locationUpdates(coroutineScope)
    }
}