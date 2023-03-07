package com.github.geohunt.app.model.database

import android.app.Activity
import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
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
    ): CompletableFuture<Challenge>

    @Deprecated("No longer used, users must privilege the use of LazyRef<Users> which does this in the background")
    fun getChallengeById(cid: String): CompletableFuture<Challenge>

    /**
     * Retrieve a list of challenges surrounding a particular location. Notice that the exact number of
     * challenges may depend on the fetched region
     *
     * @param location the location where we should search for challenges
     */
    fun getNearbyChallenge(location: Location): CompletableFuture<List<Challenge>>
}

/**
 * Utility class to create a new database handle (this is used for testing purpose
 * to replace the database instance with a mock one using mockito)
 */
object DatabaseFactory {

    /**
     * Create a new instance of a database
     */
    fun createDatabaseHandle(activity: Activity): Database {
        return FirebaseDatabase(activity)
    }
}

