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
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.utility.*
import com.github.geohunt.app.utility.DateUtils.localFromUtcIso8601
import com.github.geohunt.app.utility.DateUtils.utcIso8601FromLocalNullable
import com.github.geohunt.app.utility.DateUtils.utcIso8601Now
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import okhttp3.internal.toImmutableList
import java.io.File
import java.time.LocalDateTime

class FirebaseDatabase(activity: Activity) : Database {
    private val database = FirebaseSingletons.database.get()
    private val storage = FirebaseSingletons.storage.get()
    private val currentUser : String = "8b8b0392-ba8b-11ed-afa1-0242ac120002"

    // Database references
    internal val dbChallengeRef = database.child("challenges")
    internal val dbUserRef = database.child("users")
    internal val dbLikesRef = database.child("likes")

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
                claims = listOf(),
                likes = 0,
            )
        }
    }

    /**
     * Inserts a new user with empty information into the database.
     * This does not take into consideration profile picture, etc.
     * If the user already exists, it will override the user. Use with caution.
     */
    override fun insertNewUser(user: User): Task<Void> {
        val userEntry = UserEntry(user.uid, user.displayName, listOf(), listOf(),0)

        return dbUserRef.child(user.uid).setValue(userEntry)
    }

    /**
     * Returns a lazy ref for a user
     */
    override fun getUser(uid: String): LazyRef<User> {
        return getUserRefById(uid)
    }

    /**
     * Retrieve a challenge with a given ID and return a [LazyRef] upon completion
     * 
     * @param cid the challenge unique identifier
     * @return A [LazyRef] linked to the result of the operation
     */
    override fun getChallengeById(cid: String): LazyRef<Challenge> {
        return challengeRefById.get(cid)
    }

    override fun getImageById(iid: String): LazyRef<Bitmap> {
        return imageRefById.get(iid)
    }

    @Deprecated("Should user getChallengeById instead")
    internal fun getChallengeRefById(cid: String): FirebaseChallengeRef {
        return challengeRefById.get(cid)
    }

    internal fun getUserRefById(uid: String): FirebaseUserRef {
        return userRefById.get(uid)
    }

    internal fun getThumbnailRefById(cid: String) : FirebaseBitmapRef {
        return imageRefById.get(FirebaseBitmapRef.getImageIdFromChallengeId(cid))
    }

    internal fun getProfilePicture(uid: String): FirebaseBitmapRef {
        return imageRefById.get(FirebaseBitmapRef.getImageIdFromUserId(uid))
    }

    internal fun getClaimRefById(id: String) : LazyRef<Claim> {
        TODO()
    }

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

    override fun getLikesOf(uid: String): LazyRef<List<Challenge>> {
        return object : BaseLazyRef<List<Challenge>>() {
            override fun fetchValue(): Task<List<Challenge>> {
                return dbLikesRef.child(uid)
                    .get()
                    .thenMap {
                        val xs = it.children.map { child ->
                            val cid = child.key!!
                            val challenge = child.buildChallenge(this@FirebaseDatabase, cid)
                            challengeRefById.register(
                                cid,
                                FirebaseChallengeRef(cid, this@FirebaseDatabase, challenge)
                            )
                            challenge
                        }
                        xs
                    }
            }
            override val id: String = uid
        }
    }

    override fun insertUserLike(uid: String, cid: String): Task<Void> {
        //Increase the number of likes of the challenge by one
        getChallengeById(cid).value?.let {
            val challengeLikes = it.likes
            val dbChallengeRef = dbChallengeRef.child(cid)
            dbChallengeRef.child("likes").setValue(challengeLikes + 1)
        }

        //Add the challenge to the user's liked challenges
        return dbLikesRef.child(uid).child(cid).setValue(true)
    }

    override fun removeUserLike(uid: String, cid: String): Task<Void> {
        //Decrease the number of likes of the challenge by one
        getChallengeById(cid).value?.let {
            val challengeLikes = it.likes
            val dbChallengeRef = dbChallengeRef.child(cid)
            dbChallengeRef.child("likes").setValue(challengeLikes - 1)
        }

        //Remove the challenge from the user's liked challenges
        return dbLikesRef.child(uid).child(cid).removeValue()
    }

    override fun isUserLiked(uid: String, cid: String): Task<Boolean> {
        //Check if the challenge is in the user's liked challenges
        return dbLikesRef.child(uid).child(cid).get().thenMap {
            it.exists()
        }
    }
}


