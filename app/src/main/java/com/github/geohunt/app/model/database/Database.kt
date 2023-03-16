package com.github.geohunt.app.model.database

import android.app.Activity
import android.graphics.Bitmap
import android.provider.ContactsContract.Data
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.utility.Singleton
import com.google.android.gms.tasks.Task
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

/**
 * Interface representing the API used to communicate with the remote database and the application
 */
interface Database {
    /**
     * Enables the current logged users to create a new challenge with the given parameters
     *
     * @param thumbnail the image that will be displayed with the challenge
     * @param location the location where the user took the picture
     * @param expirationDate the date at which the challenge will expire otherwise null
     */
    fun createChallenge(
        thumbnail: Bitmap,
        location: Location,
        expirationDate: LocalDateTime? = null
    ): Task<Challenge>

    /**
     * Retrieve a challenge with a given ID and return a [Task] upon completion
     * 
     * @param cid the challenge unique identifier
     * @return A [Task] linked to the result of the operation
     */
    fun getChallengeById(cid: String): Task<Challenge>

    /**
     * Retrieve a list of challenges surrounding a particular location. Notice that the exact number of
     * challenges may depend on the fetched region
     *
     * @param location the location where we should search for challenges
     */
    fun getNearbyChallenge(location: Location): Task<List<Challenge>>

    companion object {

        /**
         * A Singleton instance of a factory function that creates a  Database instance
         * for a given Android Activity. This factory method is used by [createDatabaseHandle]
         * 
         * @param Activity the Android Activity class for which a Database instance will be created
         * @return a Database instance created using the FirebaseDatabase constructor
         */
        val databaseFactory = Singleton<(Activity) -> Database> {
            FirebaseDatabase(it)
        }

        /**
         * Returns a Database instance for a given Android Activity. The function uses the `databaseFactory`
         * Singleton instance to create or retrieve a `Database` instance for the given activity.
         *
         * @param activity the Android Activity for which a Database instance will be created or retrieved
         * @return a `Database` instance created or retrieved using the `databaseFactory` Singleton instance
         */
        fun createDatabaseHandle(activity: Activity) : Database {
            return databaseFactory.get()(activity)
        }
    }
}


