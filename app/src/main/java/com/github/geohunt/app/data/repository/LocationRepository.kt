package com.github.geohunt.app.data.repository

import com.github.geohunt.app.BuildConfig
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.sensor.SharedLocationManager
import com.github.geohunt.app.utility.Singleton
import com.github.geohunt.app.utility.quantize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRepository constructor(private val sharedLocationManager: SharedLocationManager) {
    /**
     * Observable flow for location updates
     */
    fun getLocations(coroutineScope: CoroutineScope) =
        (DefaultLocationFlow.get() ?: sharedLocationManager.locationFlow(coroutineScope).map { location ->
            if (BuildConfig.DEBUG) {
                Location(location.latitude.quantize(0.1), location.longitude.quantize(0.1))
            }
            else {
                location
            }
        })

    companion object {
        val DefaultLocationFlow = Singleton<Flow<Location>?>(null)
    }
}
