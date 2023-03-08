package com.github.geohunt.app.model.database.firebase

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.utility.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask.TaskSnapshot
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

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
    private val userRefById = DataPool<String, FirebaseUserRef> {
        FirebaseUserRef(
            id = it
        )
    }

    private val challengeRefById = DataPool<String, FirebaseChallengeRef> {
        FirebaseChallengeRef(
            id = it,
            database = this
        )
    }

    private val imageRefById = DataPool<String, FirebaseBitmapRef> {
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

    override fun createChallenge(
        thumbnail: Bitmap,
        location: Location,
        expirationDate: LocalDateTime?
    ): CompletableFuture<Challenge> {
        // State variable
        val coarseHash = location.getCoarseHash()
        val dbChallengeRef = dbChallengeRef.child(coarseHash).push()
        val challengeId = coarseHash + dbChallengeRef.key!!

        val challengeEntry = ChallengeEntry(
            authorId = currentUser,
            publishedDate = utcIso6801Now(),
            expirationDate = utcIso6801FromLocalNullable(expirationDate),
            claims = listOf(),
            location = location
        )

        Log.i("GeoHunt", "challengeEntry is $challengeEntry")

        val publishedDate = localFromUtcIso6801(challengeEntry.publishedDate!!)
        val thumbnailBitmap = getThumbnailRefById(challengeId)

        thumbnailBitmap.value = thumbnail

        // Create both jobs (update database, update storage)
        val dbFuture = dbChallengeRef.setValue(challengeEntry).toCompletableFuture(activity)
        val storageFuture = thumbnailBitmap.saveToLocalStorageThenSubmit(activity)

        // Finally make the completable task that succeed if both task succeeded
        return dbFuture.thenCombine(storageFuture) { _: Void, _: TaskSnapshot ->
            FirebaseChallenge(
                cid = challengeId,
                author = getUserRefById(currentUser),
                thumbnail = thumbnailBitmap,
                publishedDate = publishedDate,
                expirationDate = expirationDate,
                correctLocation = location,
                claims = listOf()
            )
        }
    }

    override fun getChallengeById(cid: String): CompletableFuture<Challenge> {
        throw NotImplementedError()
    }

    internal fun getChallengeRefById(cid: String): FirebaseChallengeRef {
        return challengeRefById[cid]
    }

    internal fun getUserRefById(uid: String): FirebaseUserRef {
        return userRefById[uid]
    }

    internal fun getThumbnailRefById(cid: String) : FirebaseBitmapRef {
        return imageRefById[FirebaseBitmapRef.getImageIdFromChallengeId(cid)]
    }

    internal fun getClaimRefById(id: String) : LazyRef<Claim> {
        TODO()
    }

    override fun getNearbyChallenge(location: Location): CompletableFuture<List<Challenge>> {
        TODO()
    }
}


