package com.github.geohunt.app.data.repository

import com.github.geohunt.app.sensor.SharedLocationManager

class LocationRepository constructor(
    private val sharedLocationManager: SharedLocationManager
) {
    /**
     * Observable flow for location updates
     */
    fun getLocations() = sharedLocationManager.locationFlow()
}
