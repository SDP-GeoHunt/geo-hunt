package com.github.geohunt.app.model.database

import android.app.Activity
import android.graphics.Bitmap
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.model.LazyRef
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
        expirationDate: LocalDateTime? = null
    ): Task<Challenge>

    /**
     * Submit a claim on a certain challenge
     *
     * @param thumbnail the thumbnail of the challenge
     * @param challenge the challenge we are submitting to
     * @param location the location we are at when submitting the challenge (what's going to
     * be used to compute the score)
     */
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
     * won't fail if the given element does not exists in the database. The failure will happend upon
     * fetching the returned [LazyRef]
     */
    fun getUserById(uid: String): LazyRef<User>


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
     * for a given user ID. This function allows you to execute a code block with
     * the LoggedUserContext context, which add contextual tools for logged users (follow/unfollow).
     *
     * @param uid The user ID of the logged-in user.
     * @param callback The code block to execute with the LoggedUserContext instance.
     * @return The result of executing the code block.
     * @see LoggedUserContext
     */
    fun <R> loggedAs(uid: String, callback: LoggedUserContext.() -> R) : R

    /**
     * Same as the above [logged], but uses the [Authenticator] instance to retrieve the currently
     * logged user
     *
     * @param callback The code block to execute with the LoggedUserContext instance.
     * @return The result of executing the code block.
     * @see LoggedUserContext
     */
    fun <R> logged(callback: LoggedUserContext.() -> R) : R {
        val user = Authenticator.authInstance.get().user
            ?: throw IllegalStateException("Cannot use loggedAs method because no user currently logged user")
        return loggedAs(user.uid, callback)
    }

    /**
     * Inserts a new user into the database
     *
     * @param user the user to be inserted into the database
     */
    fun insertNewUser(user: User): Task<Void>

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
        fun createDatabaseHandle(activity: Activity): Database {
            return databaseFactory.get()(activity)
        }
    }
}


