package com.github.geohunt.app.data.repository

import android.net.Uri
import com.github.geohunt.app.data.exceptions.ClaimNotFoundException
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.network.firebase.models.FirebaseClaim
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import com.github.geohunt.app.utility.DateUtils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await

/**
 * Contains methods for claim/unclaim a challenge, as well as retrieving the list
 * of claim for a particular challenge or a particular user
 */
class ClaimRepository(
    private val authRepository: AuthRepository,
    private val imageRepository: ImageRepository,
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private fun getChallengeRefFromId(id: String) {
        database.getReference("claims/$id")
    }

    private fun FirebaseClaim.asExternalModel(id: String): Claim = Claim(
        id = id,
        parentChallengeId = cid!!,
        claimerId = uid!!,
        claimDate = DateUtils.localFromUtcIso8601(time!!),
        distance = distance,
        awardedPoints = awardedPoints,
        photoUrl = photoUrl!!
    )

    /**
     * Retrieve a list of all claims id for a specific user, useful when lazy loading
     */
    suspend fun getClaimsIdByUser(user: User) : List<String> =
        database.getReference("claimsByUser/${user.id}")
            .get()
            .await()
            .run {
                children.mapNotNull { it.getValue(String::class.java) }
            }

    /**
     * Retrieve the score for a given user
     */
    suspend fun getScoreFromUser(user: User) : Long =
        getClaimsByUser(user).fold(0L) { acc, claim -> acc + claim.awardedPoints }

    /**
     * Get all claims of a specific user [user]. If one of his claim is not within the database
     * due to some internal issues then throws [ClaimNotFoundException]. Notice that this function
     * does not check whether the provided user exists or not !!
     */
    suspend fun getClaimsByUser(user: User) : List<Claim> =
        getClaimsIdByUser(user).run {
            map { claimId ->
                database.getReference("claims/$claimId").get().asDeferred()
            }.awaitAll().zip(this).map {
                if (!it.first.exists()) {
                    throw ClaimNotFoundException(it.second)
                }
                it.first.getValue(FirebaseClaim::class.java)!!.asExternalModel(it.second)
            }
        }

    /**
     * Retrieve a list of all claims associated with the current challenges
     */
    suspend fun getClaimsByChallenge(challenge: Challenge): List<Claim> =
        database.getReference("claims/${challenge.id}")
            .get()
            .await()
            .run {
                children.mapNotNull {
                        val value = it.getValue(FirebaseClaim::class.java)
                        if (value != null) it.key!! to value
                        else null
                    }
                    .map {
                        it.second.asExternalModel("${challenge.id}/${it.first}")
                    }
            }

    /**
     * Claim a specific challenge with the given photo and location
     *
     * The claim will be claimed on the behalf of the currently logged used. If there
     * is none, throws a [UserNotLoggedInException]
     */
    @Throws(UserNotLoggedInException::class)
    suspend fun claimChallenge(
        photo: LocalPicture,
        location: Location,
        challenge: Challenge
    ): Claim {
        authRepository.requireLoggedIn()

        val currentUser = authRepository.getCurrentUser()

        val claimRef = database.getReference("claims/${challenge.id}").push()
        val claimByUser = database.getReference("claimsByUser/${currentUser.id}").push() // Notice that the key here make no sense
        val claimId = challenge.id + "/" + claimRef.key!!

        // First upload the image to Firebase storage
        // This ensures that the database doesn't contain nonexistent image data
        val photoUrl: Uri = imageRepository.uploadClaimPhoto(photo, claimId)

        // Upload the entry to Firebase's Realtime Database
        val claimEntry = FirebaseClaim(
            currentUser.id,
            time = DateUtils.utcIso8601Now(),
            photoUrl = photoUrl.toString(),
            cid = challenge.id,
            location = location,
            distance = 0,
            awardedPoints = 0
        )

        // Upload the entry
        awaitAll(
            claimRef.setValue(claimEntry).asDeferred(),
            claimByUser.setValue(claimId).asDeferred()
        )

        // Finally convert to external model
        return claimEntry.asExternalModel(claimId)
    }
}
