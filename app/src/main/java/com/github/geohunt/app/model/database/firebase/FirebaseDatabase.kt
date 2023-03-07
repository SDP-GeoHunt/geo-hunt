package com.github.geohunt.app.model.database.firebase

import android.app.Activity
import android.graphics.Bitmap
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.database.models.ChallengeVisibility
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.utility.localFromUtcIso6801
import com.github.geohunt.app.utility.toCompletableFuture
import com.github.geohunt.app.utility.utcIso6801Now
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.concurrent.CompletableFuture

class FirebaseDatabase(private val activity: Activity) : Database {
    private val database = Firebase.database.reference
    private val storage = Firebase.storage("gs://geohunt-1.appspot.com").reference
    private val currentUser : String = "8b8b0392-ba8b-11ed-afa1-0242ac120002"

    private val challengeThumbnailRef = storage.child("images").child("challenges")


    override fun createChallenge(bitmap: Bitmap, visibility: ChallengeVisibility, location: Location) : CompletableFuture<Challenge>
    {
        val coarseHash = Location.getCoarseHash(location)
        val dbChallengeRef = database.child("challenges").child(coarseHash).push()
        val cid = coarseHash + dbChallengeRef.key!!
        val publishedDateStr = utcIso6801Now()
        val thumbnail = FirebasePicture(cid, challengeThumbnailRef)

        val challengeEntry = ChallengeEntry(
            uid = currentUser,
            published = publishedDateStr,
            visibility = visibility.name,
            location = location
        )

        return dbChallengeRef.setValue(challengeEntry)
            .toCompletableFuture(activity)
            .thenCompose {
                thumbnail.save()
            }
            .thenApply {
                FirebaseChallenge(
                    cid = cid,
                    uid = currentUser,
                    thumbnail = thumbnail,
                    published = localFromUtcIso6801(publishedDateStr),
                    visibility = visibility
                )
            }
    }

    override fun getChallengeById(cid: String): CompletableFuture<Challenge> {
        throw NotImplementedError()
    }

    override fun getNearbyChallenge(location: Location): CompletableFuture<List<Challenge>> {
        throw NotImplementedError()
    }
}

private data class ChallengeEntry(
    val uid: String ?= null,
    val published: String ?= null,
    val visibility: String ?= null,
    val location: Location?= null
)
