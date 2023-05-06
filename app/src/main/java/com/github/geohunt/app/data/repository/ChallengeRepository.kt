package com.github.geohunt.app.data.repository

import android.net.Uri
import com.github.geohunt.app.data.exceptions.ChallengeNotFoundException
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.network.firebase.models.FirebaseChallenge
import com.github.geohunt.app.data.network.firebase.toList
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.utility.DateUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

/**
 * Contains methods related to the creation and retrieval of challenges.
 *
 * @param bounty This is the reference to the bounty, if it is related to a bounty.
 *
 * @see [Challenge]
 * @see [FirebaseChallenge]
 */
class ChallengeRepository(
    private val userRepository: UserRepositoryInterface,
    private val imageRepository: ImageRepository,
    private val authRepository: AuthRepositoryInterface,
    database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    bounty: DatabaseReference? = null
): ChallengeRepositoryInterface {
    private val challenges = (bounty ?: database.reference).child("challenges")
    private val posts = (bounty ?: database.reference).child("posts")

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
        difficulty = if (difficulty.isEmpty()) Challenge.Difficulty.MEDIUM else Challenge.Difficulty.valueOf(difficulty),
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
    @Throws(ChallengeNotFoundException::class)
    override suspend fun getChallenge(id: String): Challenge {
        val coarseHash = id.substring(0, Location.COARSE_HASH_SIZE)
        val elementId = id.substring(Location.COARSE_HASH_SIZE)

        return withContext(ioDispatcher) {
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
     * Returns all challenges in the given sector.
     *
     * @param sector The sector hash, as returned by [Location.getCoarseHash]
     */
    override fun getSectorChallenges(sector: String): Flow<List<Challenge>> =
        challenges.child(sector)
            .snapshots
            .map {
                it.children.mapNotNull { challenge ->
                    challenge.getValue(FirebaseChallenge::class.java)?.asExternalModel(challenge.key!!)
                }
            }
            .flowOn(ioDispatcher)

    /**
     * Returns the author of the challenge.
     */
    override suspend fun getAuthor(challenge: Challenge): User = userRepository.getUser(challenge.authorId)

    /**
     * Returns the URL of the challenge photo stored on Firebase Storage.
     */
    override fun getChallengePhoto(challenge: Challenge): String = challenge.photoUrl

    /**
     * Returns the lists of posted challenges of the user with the given ID.
     */
    override fun getPosts(userId: String): Flow<List<Challenge>> {
        return posts
            .child(userId)
            .snapshots
            .map { it
                .toList()
                .map { id -> getChallenge(id) }
            }
    }

    @Deprecated("Should use the ClaimRepository::getClaims method instead")
    override suspend fun getClaims(challenge: Challenge): List<Claim> {
        TODO()
    }

    /**
     * Creates a challenge with the given photo, location and options.
     *
     * The challenge will be created on behalf of the currently logged in user. If there are none,
     * throws a [UserNotLoggedInException].
     */
    @Throws(UserNotLoggedInException::class)
    override suspend fun createChallenge(
        photo: LocalPicture,
        location: Location,
        difficulty: Challenge.Difficulty,
        expirationDate: LocalDateTime?,
        description: String?
    ): Challenge {
        authRepository.requireLoggedIn()

        @Suppress("DEPRECATION") val currentUser = authRepository.getCurrentUser()

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

        withContext(ioDispatcher) {
            challengeRef.setValue(challengeEntry).await()

            // Add the post to the list of the user posts
            posts
                .child(currentUser.id)
                .child(challengeId)
                .setValue(true)
                .await()
        }

        return challengeEntry.asExternalModel(challengeId)
    }

    /**
     * Returns all the claims done by a specific user id
     *
     * @param uid The user id
     */
    override fun getClaimsFromUser(uid: String): List<Claim> {
        // TODO
        return listOf()
    }

    override suspend fun getChallenges(): List<Challenge> {
        return challenges.get().await().run {
            children.flatMap { quadrantRef ->
                quadrantRef.children.mapNotNull {
                    it.getValue(FirebaseChallenge::class.java)?.asExternalModel(it.key!!)
                }
            }
        }
    }
}
