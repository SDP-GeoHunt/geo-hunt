package com.github.geohunt.app.model.database.firebase

import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.utility.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import java.time.LocalDateTime

data class FirebaseChallenge(
    override val cid: String,
    override val author: LazyRef<User>,
    override val thumbnail: LazyRef<Bitmap>,
    override val publishedDate: LocalDateTime,
    override val expirationDate: LocalDateTime?,
    override val correctLocation: Location,
    override val claims: List<LazyRef<Claim>>,
    override val description: String?,
    override val difficulty: Challenge.Difficulty,
    override var likes: List<LazyRef<User>>,
    override val numberOfActiveHunters: Int
) : Challenge {
    override val coarseLocation: Location
        get() = correctLocation.getCoarseLocation()
}

class FirebaseChallengeRef(
    override val id: String,
    private val database: FirebaseDatabase,
    private val challenge : Challenge? = null
) : BaseLazyRef<Challenge>() {

    override fun fetchValue(): Task<Challenge> {
        return if (challenge != null) {
            Tasks.forResult(challenge)
        } else {
            database.dbChallengeRef
                .getChallengeRefFromId(id).get()
                .thenMap {
                    it.buildChallenge(database, id)
                }
        }
    }

}

/**
 * From the [DatabaseReference] representing the root of challenge, retrieve the reference for the challenge
 * with a specific @p cid
 *
 * @param cid the id of the challenge to be retrieved
 * @return [DatabaseReference] corresponding reference
 */
internal fun DatabaseReference.getChallengeRefFromId(cid: String) : DatabaseReference
{
    return child(cid.substring(0, Location.COARSE_HASH_SIZE))
        .child(cid.substring(Location.COARSE_HASH_SIZE))
}

internal fun DataSnapshot.buildChallenge(database: FirebaseDatabase, cid: String) : FirebaseChallenge
{
    // If the data does not exists, throw exception
    if (!exists()) {
        throw NotFoundException("The required reference was not found")
    }

    // Convert the dataSnapshot to the challenge entry
    val challengeEntry = getValue(ChallengeEntry::class.java)!!

    // Finally create the challenge object
    return FirebaseChallenge(
        cid = cid,
        author = database.getUserById(challengeEntry.authorId!!),
        thumbnail = database.getChallengeThumbnailById(cid),
        publishedDate = DateUtils.localFromUtcIso8601(challengeEntry.publishedDate!!),
        expirationDate = DateUtils.localNullableFromUtcIso8601(challengeEntry.expirationDate!!),
        correctLocation =  challengeEntry.location!!,
        description = challengeEntry.description,
        difficulty = Challenge.Difficulty.valueOf(challengeEntry.difficulty),
        claims = challengeEntry.claims.mapNotNull { (id, doesClaim) -> database.getClaimById(id).takeIf { doesClaim } },
        likes = challengeEntry.likes.mapNotNull { (id, doesLike) -> database.getUserById(id).takeIf { doesLike } },
        numberOfActiveHunters = challengeEntry.numberOfActiveHunters
    )
}

/**
 * A data storage class that defines challenges as represented within the database
 */
internal data class ChallengeEntry(
    var authorId: String? = null,
    var publishedDate: String? = null,
    var expirationDate: String? = null,
    var location: Location? = null,
    val description: String? = null,
    var difficulty: String = "MEDIUM",
    var claims: Map<String, Boolean> = emptyMap(),
    var likes: Map<String, Boolean> = emptyMap(),
    var numberOfActiveHunters: Int = 0,
)
