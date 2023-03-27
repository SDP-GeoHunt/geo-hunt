package com.github.geohunt.app.model.database.firebase

import android.app.Activity
import android.graphics.Bitmap
import com.github.geohunt.app.R
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.DataPool
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.utility.DateUtils.localFromUtcIso8601
import com.github.geohunt.app.utility.DateUtils.utcIso8601FromLocalNullable
import com.github.geohunt.app.utility.DateUtils.utcIso8601Now
import com.github.geohunt.app.utility.thenMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await
import java.io.File
import java.time.LocalDateTime

class FirebaseDatabase(activity: Activity) : Database {
    private val database = FirebaseSingletons.database.get()
    private val storage = FirebaseSingletons.storage.get()
    private val currentUser : String = "8b8b0392-ba8b-11ed-afa1-0242ac120002"

    // Database references
    internal val dbUserRef = database.child("users")
    internal val dbChallengeRef = database.child("challenges")
    internal val dbFollowsRef = database.child("follows")

    // Storage references
    internal val storageImagesRef = storage.child("images")

    // Local Folders
    internal val localImageFolder : File  =  activity.getExternalFilesDir("images")!!

    // Create the object pool in order to save some memory
    private val userRefById = DataPool<String, FirebaseUserRef> {
        FirebaseUserRef(
            id = it
        )
    }

    private val challengeRefById = DataPool<String, FirebaseChallengeRef> {
        FirebaseChallengeRef(it, this)
    }

    private val imageRefById = DataPool<String, FirebaseBitmapRef> {
        FirebaseBitmapRef(it, this)
    }

    init {
        if (!localImageFolder.exists()) {
            localImageFolder.mkdirs()
        }
    }

    /**
     * Creates a new Challenge and stores it to the Firebase Database and Storage.
     * Notice that the given bitmap should not have more pixel than
     * [R.integer.maximum_number_of_pixel_per_photo] otherwise this function will throw
     * an [IllegalArgumentException]
     *
     * @param thumbnail the thumbnail of the challenge
     * @param location the location of the challenge
     * @param expirationDate the expiration date of the challenge, can be null
     * @return a Task that will complete with the created Challenge
     */
    override fun createChallenge(
        thumbnail: Bitmap,
        location: Location,
        expirationDate: LocalDateTime?
    ): Task<Challenge> {
        // Requirements
        require(thumbnail.width * thumbnail.height < R.integer.maximum_number_of_pixel_per_photo)

        // State variable
        val coarseHash = location.getCoarseHash()
        val dbChallengeRef = dbChallengeRef.child(coarseHash).push()
        val challengeId = coarseHash + dbChallengeRef.key!!

        val challengeEntry = ChallengeEntry(
            authorId = currentUser,
            publishedDate = utcIso8601Now(),
            expirationDate = utcIso8601FromLocalNullable(expirationDate),
            claims = listOf(),
            location = location
        )

        // Get the reference to the thumbnail Bitmap and set the value
        val thumbnailBitmap = getThumbnailRefById(challengeId)
        thumbnailBitmap.value = thumbnail

        // Create both jobs (update database, update storage)
        val submitToDatabaseTask = dbChallengeRef.setValue(challengeEntry)
        val submitToStorageTask = thumbnailBitmap.saveToLocalStorageThenSubmit()

        // Finally make the completable task that succeed if both task succeeded
        return Tasks.whenAll(submitToDatabaseTask, submitToStorageTask).thenMap {
            FirebaseChallenge(
                cid = challengeId,
                author = getUserRefById(currentUser),
                thumbnail = thumbnailBitmap,
                publishedDate = localFromUtcIso8601(challengeEntry.publishedDate!!),
                expirationDate = expirationDate,
                correctLocation = location,
                claims = listOf()
            )
        }
    }

    /**
     * Retrieve a challenge with a given ID and return a [LazyRef] upon completion
     * 
     * @param cid the challenge unique identifier
     * @return A [LazyRef] linked to the result of the operation
     */
    override fun getChallengeById(cid: String): LazyRef<Challenge> {
        return getChallengeRefById(cid)
    }

    internal fun getChallengeRefById(cid: String): FirebaseChallengeRef {
        return challengeRefById.get(cid)
    }

    internal fun getUserRefById(uid: String): FirebaseUserRef {
        return userRefById.get(uid)
    }

    internal fun getThumbnailRefById(cid: String) : FirebaseBitmapRef {
        return imageRefById.get(FirebaseBitmapRef.getImageIdFromChallengeId(cid))
    }

    internal fun getClaimRefById(id: String) : LazyRef<Claim> {
        TODO()
    }

    override fun getNearbyChallenge(location: Location): Task<List<Challenge>> {
        TODO()
    }

    override fun getFollowersOf(uid: String): LazyRef<List<FirebaseUserRef>> {
        return object : BaseLazyRef<List<FirebaseUserRef>>() {
            override fun fetchValue(): Task<List<FirebaseUserRef>> {
                return dbFollowsRef.child(uid)
                    .get()
                    .thenMap { list ->
                        (list as List<*>).map { FirebaseUserRef(it as String) }
                    }
            }

            override val id: String = uid
        }
    }

    /**
     * Makes the first user with the given UID follow the second user.
     *
     * @note To ensure easier data querying, the data must be synchronized at 3 places in the database :
     *       - In the follower's follow list, the followee should be added,
     *       - The followee's follow counter should be incremented,
     *       - The follower should be added to the followee's list in the `follows` relationship.
     */
    override suspend fun follow(follower: String, followee: String) {
        val followerListRef = dbUserRef.child(follower).child("followList")
        val counterRef = dbUserRef.child(followee).child("followCounter")
        val follows = dbFollowsRef.child(followee)

        val followerList = followerListRef.get().await().getValue<List<*>>()?.filterIsInstance<String>()
        val followedCounter = counterRef.get().await().getValue<Int>()
        val followsPairs = follows.get().await().getValue<List<*>>()?.filterIsInstance<String>()

        Tasks.whenAll(
            // Update the follower's follow list
            followerListRef.setValue((followerList ?: emptyList()) + followee),

            // Update the follow counter of the followee
            counterRef.setValue((followedCounter ?: 0) + 1),

            // Update the follows relationship
            follows.setValue((followsPairs ?: emptyList()) + follower)
        ).await()
    }
}


