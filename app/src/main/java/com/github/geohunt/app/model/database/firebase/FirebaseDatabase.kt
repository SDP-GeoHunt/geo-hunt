package com.github.geohunt.app.model.database.firebase

import android.app.Activity
import android.graphics.Bitmap
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.model.DataPool
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Database
import com.github.geohunt.app.model.database.api.*
import com.github.geohunt.app.utility.thenMap
import com.github.geohunt.app.utility.toMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import okhttp3.internal.toImmutableList
import java.io.File

class FirebaseDatabase(activity: Activity) : Database {
    private val database = FirebaseSingletons.database.get()
    private val storage = FirebaseSingletons.storage.get()

    // Database references
    internal val dbUserRef = database.child("users")
    internal val dbChallengeRef = database.child("challenges")
    internal val dbFollowersRef = database.child("followers")

    internal val dbClaimRef = database.child("claims")

    // Storage references
    internal val storageImagesRef = storage.child("images")

    // Local Folders
    internal val localImageFolder : File = activity.getExternalFilesDir("images")!!

    // Create the object pool in order to save some memory
    private val userRefById = DataPool<String, FirebaseUserRef> {
        FirebaseUserRef(it, this)
    }

    private val challengeRefById = DataPool<String, FirebaseChallengeRef> {
        FirebaseChallengeRef(it, this)
    }

    private val imageRefById = DataPool<String, FirebaseBitmapRef> {
        FirebaseBitmapRef(it, this)
    }

    private val loggedUserContextByUserId = DataPool<String, FirebaseLoggedUserContext> { uid ->
        FirebaseLoggedUserContext(this, getUserById(uid))
    }

    init {
        if (!localImageFolder.exists()) {
            localImageFolder.mkdirs()
        }
    }

    /**
     * Inserts a new user with empty information into the database.
     * This does not take into consideration profile picture, etc.
     * If the user already exists, it will override the user. Use with caution.
     */
    override fun insertNewUser(user: User): Task<Void> {
        val userEntry = UserEntry(user.displayName)
        return dbUserRef.child(user.uid).setValue(userEntry)
    }

    /**
     * Retrieve a challenge with a given ID and the corresponding [LazyRef]. Notice that this operation
     * won't fail if the given element does not exists in the database. The failure will happend upon
     * fetching the returned [LazyRef]
     *
     * @param cid the challenge unique identifier
     * @return A [LazyRef] linked to the result of the operation
     */
    override fun getChallengeById(cid: String): LazyRef<Challenge> {
        return challengeRefById.get(cid)
    }

    /**
     * Retrieve an image with a given ID and the corresponding [LazyRef]. Notice that this operation
     * won't fail if the given element does not exists in the database. The failure will happend upon
     * fetching the returned [LazyRef]
     *
     * @param iid the image id, this may depend for image type
     * @return A [LazyRef] linked to the result of the operation
     */
    override fun getImageById(iid: String): LazyRef<Bitmap> {
        return imageRefById.get(iid)
    }

    /**
     * Retrieve an image with a given ID and the corresponding [LazyRef]. Notice that this operation
     * won't fail if the given element does not exists in the database. The failure will happend upon
     * fetching the returned [LazyRef]
     *
     * @param iid the image id, this may depend for image type
     * @return A [LazyRef] linked to the result of the operation
     */
    override fun getUserById(uid: String): LazyRef<User> {
        return userRefById.get(uid)
    }



    internal fun getThumbnailRefById(cid: String) : FirebaseBitmapRef {
        return imageRefById.get(FirebaseBitmapRef.getImageIdFromChallengeId(cid))
    }

    internal fun getProfilePicture(uid: String): FirebaseBitmapRef {
        return imageRefById.get(FirebaseBitmapRef.getImageIdFromUserId(uid))
    }

    @Deprecated("all getFooRefById should be replaced by getFooById")
    internal fun getClaimRefById(id: String) : LazyRef<Claim> {
        TODO()
    }

    /**
     * Get a list of nearby challenges to a specific location
     *
     * @param location the location we are interested in
     * @return [Task] a task completed once the operation succeeded (or failed successfully)
     */
    override fun getNearbyChallenge(location: Location): Task<List<Challenge>> {
        val listOfQuadrant = listOf("3cc359ec")

        val xs = listOfQuadrant.map { cid ->
            dbChallengeRef.child(cid).get()
                .thenMap {
                    val challenge = it.buildChallenge(this, cid)
                    challengeRefById.register(cid, FirebaseChallengeRef(cid, this, challenge))
                    challenge
                }
        }

        return Tasks.whenAllSuccess<Challenge>(xs)
            .thenMap { it.toImmutableList() }
    }

    override fun getFollowersOf(uid: String): Task<List<LazyRef<User>>> {
        return dbFollowersRef.child(uid)
            .get()
            .thenMap { snapshot ->
                snapshot.toMap<Boolean>()
                    .mapNotNull { (id, doesFollow) -> getUserById(id).takeIf { doesFollow } }
            }
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
    override fun getLoggedContext(): LoggedUserContext {
        val user = Authenticator.authInstance.get().user
            ?: throw IllegalStateException("Attempted to get the logged-context however no user currently logged")

        return loggedUserContextByUserId.get(user.uid)
    }
}


