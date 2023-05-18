package com.github.geohunt.app.data.repository

import com.github.geohunt.app.model.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface LocationRepositoryInterface {
    /**
     * Observable flow for location updates
     */
    fun getLocations(coroutineScope: CoroutineScope): Flow<Location>
}