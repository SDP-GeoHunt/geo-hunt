package com.github.geohunt.app.model.database

import android.app.Activity
import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.database.api.*
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
        difficulty: Challenge.Difficulty,
        expirationDate: LocalDateTime? = null,
        description: String? = null
    ): Task<Challenge>

    fun submitClaim(
        thumbnail: Bitmap,
        challenge: Challenge,
        location: Location,
    ): Task<Claim>

    /**
     * Retrieve a challenge with a given ID and the corresponding [LazyRef]. Notice that this operation
     * won't fail if the given element does not exists in the database. The failure will happend upon
     * fetching the returned [LazyRef]
     * 
     * @param cid the challenge unique identifier
     * @return A [LazyRef] linked to the result of the operation
     */
    fun getChallengeById(cid: String): LazyRef<Challenge>

    /**
     * Retrieve an image with a given ID and the corresponding [LazyRef]. Notice that this operation
     * won't fail if the given element does not exists in the database. The failure will happend upon
     * fetching the returned [LazyRef]
     *
     * @param iid the image id, this may depend for image type
     * @return A [LazyRef] linked to the result of the operation
     */
    fun getImageById(iid: String): LazyRef<Bitmap>

    /**
     * Retrieve a user with a specific ID and return the corresponding [LazyRef]. Notice that this operation
     * won't fail if the given element does not exists in the database. The failure will happen upon
     * fetching the returned [LazyRef]
     */
    fun getUserById(uid: String): LiveLazyRef<User>

    /**
     * Retrieve a [LazyRef] to the claim with the provided [cid] as id. Notice that this operation
     * won't fail if the given element does not exists in the database, instead the failure will happen
     * upon fetching it.
     */
    fun getClaimById(cid: String): LazyRef<Claim>

    /**
     * Get a list of nearby challenges to a specific location
     *
     * @param location the location we are interested in
     * @return [Task] a task completed once the operation succeeded (or failed successfully)
     */
    fun getNearbyChallenge(location: Location): Task<List<Challenge>>

    /**
     * Returns the followers of the user with the given user id.
     *
     * @param uid The user ID.
     * @return A map where keys that are mapped to true indicates that it is a follower.
     */
    fun getFollowersOf(uid: String): Task<List<LazyRef<User>>>

    /**
     * Get the top @p n users with maximum scores
     *
     * @param n the number of user to retrieve at most. Notice that it is possible
     *          that the actual number if smaller than that
     */
    fun getTopNUsers(n: Int) : Task<List<LazyRef<User>>>

    /**
     * Makes the first user with the given uid follow the second user.
     */
    suspend fun follow(follower: String, followee: String)

    /**
     * Makes the first user with the given uid unfollow the second user.
     */
    suspend fun unfollow(follower: String, followee: String)

    suspend fun joinHunt(cid: String)

    suspend fun leaveHunt(cid: String)

    /**
     * Inserts a new user into the database
     *
     * @param user the user to be inserted into the database
     */
    fun insertNewUser(user: User): Task<Void>

    /**
     * Updates user with the according data
     */
    fun updateUser(user: EditedUser): Task<Void?>


    /**
     * Point-Of-Interest user is a special user that does not have any information within the database
     *
     * It represent the user attach to any "public" challenge such as Point-Of-Interest. This design
     * choice was made to ease the process of registering such challenges in the database without having
     * to think too much onto it
     */
    fun getPOIUserID() : String

    /**
     * Inserts a new like for the chosen challenge for a given user
     */
    suspend fun insertUserLike(uid: String, cid: String)

    /**
     * Removes a like for the chosen challenge for a given user
     */
    suspend fun removeUserLike(uid: String, cid: String)

    /**
     * Returns true if the user with the given user ID has liked the challenge with the given challenge ID
     */
    fun doesUserLike(uid: String, cid: String): LazyRef<Boolean>


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


