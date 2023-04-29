package com.github.geohunt.app.data.repository

import com.github.geohunt.app.sensor.SharedLocationManager
import kotlinx.coroutines.CoroutineScope

class LocationRepository constructor(private val sharedLocationManager: SharedLocationManager) {
    /**
     * Observable flow for location updates
     */
    fun getLocations(coroutineScope: CoroutineScope) = sharedLocationManager.locationFlow(coroutineScope)
}
