package com.github.geohunt.app.model

import com.github.geohunt.app.model.api.BoredActivity
import com.github.geohunt.app.model.api.BoredApi
import com.github.geohunt.app.model.persistence.BoredDao
import java.util.concurrent.atomic.AtomicBoolean

/**
 * The {@link BoredRepository} acts as a single source of truth (SSOT) for Bored activities, and makes
 * the connection between the network calls and the cache.
 *
 * @param api The Bored API used to make network calls.
 * @param cache The database where fetched activities should be stored and retrieved when offline.
 */
class BoredRepository(private val api: BoredApi, private val cache: BoredDao) {
    private val isCached: AtomicBoolean = AtomicBoolean(false)

    /**
     * Fetches a random activity from the Bored API, or tries to fetch a cached activity from the
     * Bored database if the network call was unsuccessful.
     */
    suspend fun getActivity(): BoredActivity? {
        return try {
            remoteFetch()
        } catch (e: Exception) {
            localFetch()
        }
    }

    /**
     * Returns true if the last fetched item was cached.
     */
    fun isCached(): Boolean = isCached.get()

    /**
     * Fetches a random activity from the Bored API, or falls back to the local cache if the
     * network is unavailable.
     */
    private suspend fun remoteFetch(): BoredActivity? {
        val response = api.getActivity().execute()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                isCached.set(false)
                cache.addActivity(body)
            }

            return body
        } else {
            return localFetch()
        }
    }

    /**
     * Returns a cached activity from the database, or null if the transaction fails.
     */
    private suspend fun localFetch(): BoredActivity? {
        return try {
            val activity = cache.getRandomActivity()
            isCached.set(true)
            activity
        } catch (e: Exception) {
            null
        }
    }
}