package com.github.geohunt.app.model.database.firebase

import android.graphics.Bitmap
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.utility.localFromUtcIso6801
import com.github.geohunt.app.utility.localNullableFromUtcIso6801
import com.github.geohunt.app.utility.toCompletableFuture
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

data class FirebaseChallenge(
    override val cid: String,
    override val author: LazyRef<User>,
    override val thumbnail: LazyRef<Bitmap>,
    override val publishedDate: LocalDateTime,
    override val expirationDate: LocalDateTime?,
    override val correctLocation: Location,
    override val claims: List<LazyRef<Claim>>,
) : Challenge {
    override val coarseLocation: Location
        get() = correctLocation.getCoarseLocation()
}

class FirebaseChallengeRef(
    override val id: String,
    private val database: FirebaseDatabase
) : BaseLazyRef<Challenge>() {

    override fun fetchValue(): CompletableFuture<Challenge> {
        val coarseHash = id.substring(0, Location.COARSE_HASH_SIZE)
        val elementId = id.substring(Location.COARSE_HASH_SIZE)
        return database.dbChallengeRef
            .child(coarseHash).child(elementId).get()
            .toCompletableFuture(database.activity)
            .thenApply {
                val challengeEntry = it.getValue(ChallengeEntry::class.java)!!

                FirebaseChallenge(
                    cid = id,
                    author = database.getUserRefById(challengeEntry.authorId!!),
                    thumbnail = database.getThumbnailRefById(id),
                    publishedDate = localFromUtcIso6801(challengeEntry.publishedDate!!),
                    expirationDate = localNullableFromUtcIso6801(challengeEntry.expirationDate!!),
                    correctLocation =  challengeEntry.location!!,
                    claims = challengeEntry.claims!!.map(database::getClaimRefById)
                )
            }
    }

}

/**
 * A data storage class that defines challenges as represented within the database
 */
internal data class ChallengeEntry(
    val authorId: String? = null,
    val publishedDate: String? = null,
    val expirationDate: String? = null,
    val claims: List<String>? = null,
    val location: Location? = null
)
