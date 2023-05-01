package com.github.geohunt.app.data.repository

import android.net.Uri
import android.util.Log
import com.github.geohunt.app.data.exceptions.ChallengeNotFoundException
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.network.firebase.models.FirebaseChallenge
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.utility.DateUtils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

/**
 * Contains methods related to the creation and retrieval of challenges.
 *
 * @see [Challenge]
 * @see [FirebaseChallenge]
 */
class ChallengeRepository(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository,
    private val authRepository: AuthRepository,
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val challenges = database.getReference("challenges")

    /**
     * Converts the [FirebaseChallenge] model to the external model, ready for use in the UI layer.
     */
    private fun FirebaseChallenge.asExternalModel(id: String): Challenge = Challenge(
        id = id,
        authorId = authorId,
        photoUrl = photoUrl,
        location = location,
        publishedDate = DateUtils.localFromUtcIso8601(publishedDate),
        expirationDate = DateUtils.localNullableFromUtcIso8601(expirationDate),
        difficulty = Challenge.Difficulty.valueOf(difficulty),
        description = description
    )

    /**
     * Fetches challenge with the given id on Firebase's Realtime Database.
     *
     * If there are no such challenge, throws a [ChallengeNotFoundException].
     *
     * @param id The unique id of the challenge, composed of a combination of the coarse hash and
     *           a randomly generated id.
     * @return The queried challenge.
     */
    suspend fun getChallenge(id: String): Challenge {
        val coarseHash = id.substring(0, Location.COARSE_HASH_SIZE)
        val elementId = id.substring(Location.COARSE_HASH_SIZE)

        return withContext(ioDispatcher) {
            // TODO: Remove following two debug lines
            val auto = challenges.get().await()
            Log.i("GeoHunt", auto.toString())

            challenges
                .child(coarseHash)
                .child(elementId)
                .get()
                .await()
                .getValue(FirebaseChallenge::class.java)
                ?.asExternalModel(id) ?: throw ChallengeNotFoundException(id)
        }
    }

    /**
     * Returns the author of the challenge.
     */
    suspend fun getAuthor(challenge: Challenge): User = userRepository.getUser(challenge.authorId)

    /**
     * Returns the URL of the challenge photo stored on Firebase Storage.
     */
    fun getChallengePhoto(challenge: Challenge): String = challenge.photoUrl

    @Deprecated("Should use the ClaimRepository::getClaims method instead")
    suspend fun getClaims(challenge: Challenge): List<Claim> {
        TODO()
    }

    /**
     * Creates a challenge with the given photo, location and options.
     *
     * The challenge will be created on behalf of the currently logged in user. If there are none,
     * throws a [UserNotLoggedInException].
     */
    @Throws(UserNotLoggedInException::class)
    suspend fun createChallenge(
        photo: LocalPicture,
        location: Location,
        difficulty: Challenge.Difficulty = Challenge.Difficulty.MEDIUM,
        expirationDate: LocalDateTime? = null,
        description: String? = null
    ): Challenge {
        authRepository.requireLoggedIn()

        val currentUser = authRepository.getCurrentUser()

        val coarseHash = location.getCoarseHash()
        val challengeRef = challenges.child(coarseHash).push()
        val challengeId = coarseHash + challengeRef.key!!

        // First upload the image to Firebase Storage
        // This ensures that the database doesn't contain nonexistent image data
        val photoUrl: Uri = imageRepository.uploadChallengePhoto(photo, coarseHash, challengeRef.key!!)

        // Upload the entry to Firebase's Realtime Database
        val challengeEntry = FirebaseChallenge(
            authorId = currentUser.id,
            photoUrl = photoUrl.toString(),
            publishedDate = DateUtils.utcIso8601Now(),
            expirationDate = DateUtils.utcIso8601FromLocalNullable(expirationDate),
            difficulty = difficulty.toString(),
            location = location,
            description = description
        )
        challengeRef.setValue(challengeEntry).await()

        return challengeEntry.asExternalModel(challengeId)
    }
}
