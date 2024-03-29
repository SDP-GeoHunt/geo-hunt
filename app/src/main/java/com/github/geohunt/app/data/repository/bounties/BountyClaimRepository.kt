package com.github.geohunt.app.data.repository.bounties

import android.net.Uri
import com.github.geohunt.app.data.exceptions.ClaimNotFoundException
import com.github.geohunt.app.data.exceptions.auth.UserNotLoggedInException
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.network.firebase.models.FirebaseClaim
import com.github.geohunt.app.data.network.firebase.models.asExternalModel
import com.github.geohunt.app.data.repository.ImageRepository
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.model.points.PointCalculator
import com.github.geohunt.app.utility.DateUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await

class BountyClaimRepository(
    bountyReference: DatabaseReference,
    private val bid: String,
    private val teamRepository: TeamsRepository,
    private val imageRepository: ImageRepository,
    private val pointCalculatorMap: Map<Challenge.Difficulty, PointCalculator> = PointCalculator.defaultCalculators,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BountyClaimRepositoryInterface {
    private val claims = bountyReference.child("claims")
    private val claimIdByTeamId = bountyReference.child("claimIdByTeamId")

    // WARNING to maintainer:
    //  The claim-id must NEVER be used in the path of json-tree because
    //  it contains a '/'

    @Throws(UserNotLoggedInException::class)
    override suspend fun claimChallenge(
        photo: LocalPicture,
        challenge: Challenge,
        location: Location
    ): Claim = withContext(ioDispatcher) {
        val currentTeam = teamRepository.getUserTeam().first()!!


        val claimRef = claims.child(challenge.id).push()
        val claimId = challenge.id + "/" + claimRef.key!!

        // First upload the image to Firebase storage
        // This ensures that the database doesn't contain nonexistent image data
        val photoUrl : Uri = imageRepository.uploadBountyClaimPhoto(photo, claimId, bid)

        // Compute the distance to the target
        val distance = location.distanceTo(challenge.location)

        // Upload the entry to Firebase's Realtime Database
        val claimEntry = FirebaseClaim(
            currentTeam.teamId,

            time = DateUtils.utcIso8601Now(),
            photoUrl = photoUrl.toString(),
            cid = challenge.id,
            location = location,
            distance = distance.toLong(),
            awardedPoints = pointCalculatorMap[challenge.difficulty]!!.computePoints(distance)
        )

        // Update the score
        teamRepository.atomicScoreAddAndAssign(currentTeam, claimEntry.awardedPoints)

        // Upload the entry
        awaitAll(
            claimRef.setValue(claimEntry).asDeferred(),

            claimIdByTeamId.child(currentTeam.teamId)
                .child(challenge.id)
                .setValue(claimId).asDeferred()
        )

        // Finally convert to external model
        claimEntry.asExternalModel(claimId)
    }

    override suspend fun getClaimById(id: String): Claim = withContext(ioDispatcher) {
        (claims.child(id).get()
            .await()
            .getValue(FirebaseClaim::class.java) ?: throw ClaimNotFoundException(id))
            .asExternalModel(id)
    }

    override suspend fun getClaimsOf(team: Team): List<Claim> = withContext(ioDispatcher) {
        claimIdByTeamId.child(team.teamId)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(String::class.java) }
            .map { async { getClaimById(it) } }
            .awaitAll()
    }

    override fun getRealtimeClaimsOf(team: Team): Flow<List<Claim>> =
        claimIdByTeamId.child(team.teamId)
            .snapshots
            .map { list ->
                list.children
                    .mapNotNull { it.getValue(String::class.java) }
                    .map { getClaimById(it) }
            }
            .flowOn(ioDispatcher)
}