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
    override val awardedPoints: Long
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
        val cid = id.split("!!")[0]
        val claimId = id.split("!!")[1]

        return database.dbClaimRef
            .child(cid).child(claimId).get()
            .thenMap { dataSnapshot ->
                if (!dataSnapshot.exists()) {
                    throw RuntimeException("Claim $id was not found in the database")
                }

                val claimEntry = dataSnapshot.getValue(ClaimEntry::class.java)!!
                FirebaseClaim(
                    id = id,
                    user = database.getUserById(claimEntry.user!!),
                    time = DateUtils.localFromUtcIso8601(claimEntry.time),
                    challenge = database.getChallengeById(claimEntry.cid!!),
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
    var time: String = "null",
    var cid: String? = null,
    var location: Location? = null,
    val distance: Long? = null,
)
