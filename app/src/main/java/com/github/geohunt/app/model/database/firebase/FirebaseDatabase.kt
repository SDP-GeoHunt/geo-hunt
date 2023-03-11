package com.github.geohunt.app.model.database.firebase

import android.app.Activity
import android.graphics.Bitmap
import com.github.geohunt.app.R
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.utility.*
import com.github.geohunt.app.utility.DateUtils.localFromUtcIso8601
import com.github.geohunt.app.utility.DateUtils.utcIso8601FromLocalNullable
import com.github.geohunt.app.utility.DateUtils.utcIso8601Now
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.time.LocalDateTime

class FirebaseDatabase(internal val activity: Activity) : Database {
    private val database = Firebase.database.reference
    private val storage = Firebase.storage("gs://geohunt-1.appspot.com").reference
    private val currentUser : String = "8b8b0392-ba8b-11ed-afa1-0242ac120002"

    // Database references
    internal val dbChallengeRef = database.child("challenges")

    // Storage references
    internal val storageImagesRef = storage.child("images")

    // Local Folders
    internal val localImageFolder : File  =  activity.getExternalFilesDir("images")!!

    // Create the object pool in order to save some memory
    private val userRefById = HashMap<String, FirebaseUserRef>().withDefault {
        FirebaseUserRef(
            id = it
        )
    }

    private val challengeRefById = HashMap<String, FirebaseChallengeRef>().withDefault {
        FirebaseChallengeRef(
            id = it,
            database = this
        )
    }

    private val imageRefById = HashMap<String, FirebaseBitmapRef>().withDefault {
        FirebaseBitmapRef(
            id = it,
            database = this
        )
    }

    init {
        if (!localImageFolder.exists()) {
            localImageFolder.mkdirs()
        }
    }

    /**
     * Creates a new Challenge and stores it to the Firebase Database and Storage.
     * Notice that the given bitmap should not have more pixel than
     * [R.integer.maximum_number_of_pixel_per_photo] overwise this function will throw
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

    override fun getChallengeById(cid: String): Task<Challenge> {
        throw NotImplementedError()
    }

    internal fun getChallengeRefById(cid: String): FirebaseChallengeRef {
        return challengeRefById.getValue(cid)
    }

    internal fun getUserRefById(uid: String): FirebaseUserRef {
        return userRefById.getValue(uid)
    }

    internal fun getThumbnailRefById(cid: String) : FirebaseBitmapRef {
        return imageRefById.getValue(FirebaseBitmapRef.getImageIdFromChallengeId(cid))
    }

    internal fun getClaimRefById(id: String) : LazyRef<Claim> {
        TODO()
    }

    override fun getNearbyChallenge(location: Location): Task<List<Challenge>> {
        TODO()
    }
}


