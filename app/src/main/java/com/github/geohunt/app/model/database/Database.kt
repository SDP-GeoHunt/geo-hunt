package com.github.geohunt.app.model.database

import android.app.Activity
import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.utility.Singleton
import com.google.android.gms.tasks.Task
import java.time.LocalDateTime

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
     * Retrieve a challenge with a given ID and return a [LazyRef] upon completion
     * 
     * @param cid the challenge unique identifier
     * @return A [LazyRef] linked to the result of the operation
     */
    fun getChallengeById(cid: String): LazyRef<Challenge>

    /**
     * Retrieve a list of challenges surrounding a particular location. Notice that the exact number of
     * challenges may depend on the fetched region
     *
     * @param location the location where we should search for challenges
     */
    fun getNearbyChallenge(location: Location): Task<List<Challenge>>

    /**
     * Returns the followers of the user with the given user id.
     *
     * @param uid The user ID.
     * @return A map where keys that are mapped to true indicates that it is a follower.
     */
    fun getFollowersOf(uid: String): Task<Map<String, Boolean>>

    /**
     * Makes the first user with the given uid follow the second user.
     */
    suspend fun follow(follower: String, followee: String)

    /**
     * Makes the first user with the given uid unfollow the second user.
     */
    suspend fun unfollow(follower: String, followee: String)

     * Inserts a new user into the database
     */
    fun insertNewUser(user: User): Task<Void>

    fun getUser(uid: String): LazyRef<User>

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


