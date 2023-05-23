package com.github.geohunt.app.data.repository

import com.github.geohunt.app.model.Location
import com.github.geohunt.app.sensor.SharedLocationManager
import com.github.geohunt.app.utility.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class LocationRepository constructor(private val sharedLocationManager: SharedLocationManager) :
    LocationRepositoryInterface {
    /**
     * Observable flow for location updates
     */
    override fun getLocations(coroutineScope: CoroutineScope) =
        DefaultLocationFlow.get() ?: sharedLocationManager.locationFlow(coroutineScope)

    companion object {
        val DefaultLocationFlow = Singleton<Flow<Location>?>(null)
    }
}
