package com.github.geohunt.app.model.database.api

import android.app.Activity
import android.graphics.Bitmap
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.LiveLazyRef
import com.github.geohunt.app.model.database.api.*
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.utility.Singleton
import com.google.android.gms.tasks.Task

/**
 * Interface representing the API used to communicate with the remote database and the application
 */
interface Database {
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
     * Creates a LoggedUserContext instance linked to the current database instance
     * for a given user ID. This function allows you to execute operations that are
     * specific to logged user (e.g. follow/unfollow, like/unlike...). Notice that this
     * function utilize [Authenticator] in the backend to get the currently logged user
     *
     * @param callback The code block to execute with the LoggedUserContext instance.
     * @return The result of executing the code block.
     * @see LoggedUserContext
     */
    fun <R> logged(callback: LoggedUserContext.() -> R) : R {
        return getLoggedContext().withContext(callback)
    }

    /**
     * Retrieve the [LoggedUserContext] specific to the currently logged user. This method
     * uses internally [Authenticator] to retrieve the current user
     *
     * @throws IllegalStateException if there is no user currently logged
     * @return LoggedUserContext corresponding with the currently logged user
     * @see LoggedUserContext
     * @see Authenticator
     */
    fun getLoggedContext() : LoggedUserContext

    /**
     * Inserts a new user into the database
     *
     * @param user the user to be inserted into the database
     */
    fun insertNewUser(user: User): Task<Void>

    /**
     * Point-Of-Interest user is a special user that does not have any information within the database
     *
     * It represent the user attach to any "public" challenge such as Point-Of-Interest. This design
     * choice was made to ease the process of registering such challenges in the database without having
     * to think too much onto it
     */
    fun getPOIUserID() : String

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


