package com.github.geohunt.app.model.database.firebase

import android.graphics.Bitmap
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.utility.*
import com.google.android.gms.tasks.Task
import java.time.LocalDateTime

/**
 * Internal implementation of a claim in the firebase context
 */
data class FirebaseClaim(
    override val id: String,
    override val challenge: LazyRef<Challenge>,
    override val user: LazyRef<User>,
    override val time: LocalDateTime,
    override val location: Location,
) : Claim {
    override val image: LazyRef<Bitmap>
        get() = TODO("Not yet implemented")
    override val distance: Long
        get() = TODO("Not yet implemented")
}

/**
 * Internal implementation of the claim LazyRef in the context of firebase
 */
internal class FirebaseClaimRef(
    override val id: String,
    private val database: FirebaseDatabase
) : BaseLazyRef<Claim>() {

    override fun fetchValue(): Task<Claim> {
        // Retrieve the coarseHash and elementId
        val coarseHash = id.substring(0, Location.COARSE_HASH_SIZE)
        val elementId = id.substring(Location.COARSE_HASH_SIZE)

        return database.dbClaimRef
            .child(coarseHash).child(elementId).get()
            .thenMap {
                if (!it.exists()) {
                    throw RuntimeException("The claim with $id was not find in the database")
                }

                val claimEntry = it.getValue(ClaimEntry::class.java)!!
                FirebaseClaim(
                    id = id,
                    user = database.getUserById(claimEntry.user!!),
                    time = DateUtils.localFromUtcIso8601(claimEntry.time!!),
                    challenge = database.getChallengeById(claimEntry.challenge?.cid!!),
                    location =  claimEntry.location!!,
                )
            }
    }

}

/**
 * A data storage class that defines claims as represented within the database
 */
internal data class ClaimEntry(
    var user: String? = null,
    var time: String? = null,
    var challenge: Challenge? = null,
    var location: Location? = null,
    val distance: Long? = null,
)
