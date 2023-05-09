package com.github.geohunt.app.data.repository.bounties

import com.github.geohunt.app.data.exceptions.BountyNotFoundException
import com.github.geohunt.app.data.network.firebase.models.FirebaseBountyMetadata
import com.github.geohunt.app.data.repository.*
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.User
import com.github.geohunt.app.model.points.GaussianPointCalculator
import com.github.geohunt.app.model.points.PointCalculator
import com.github.geohunt.app.utility.DataPool
import com.github.geohunt.app.utility.DateUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class BountiesRepository(
    private val userRepository: UserRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
    private val imageRepository: ImageRepository,
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage,
    private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO
) : BountiesRepositoryInterface {

    private val bountiesMetadataRef = database.getReference("bounties/meta")
    private val bountiesByUidRef = database.getReference("bounties/byUser")
    private val bountiesTeam = database.getReference("bounties/teams")

    private val teamsRepositories = DataPool<String, TeamsRepository> { bid ->
        TeamsRepository(
            bountiesTeam.child(bid),
            userRepository,
            ioDispatcher = ioDispatcher
        )
    }

    private val challengesRepositories = DataPool<String, ChallengeRepository> { bid ->
        ChallengeRepository(
            userRepository = userRepository,
            authRepository = authRepository,
            bounty = database.getReference("bounties/$bid"),
            imageRepository = imageRepository,
            ioDispatcher = ioDispatcher,
            database = database
        )
    }

    private val claimRepositories = DataPool<String, BountyClaimRepository> { bid ->
        BountyClaimRepository(
            bountyReference = database.getReference("bounties/$bid"),
            bid = bid,
            teamRepository = teamsRepositories.get(bid),
            imageRepository = imageRepository,
            ioDispatcher = ioDispatcher
        )
    }

    private fun FirebaseBountyMetadata.asExternalModel(bid: String) = Bounty(
        bid = bid,
        adminUid = adminUid!!,
        startingDate = DateUtils.localFromUtcIso8601(startingDate!!),
        expirationDate = DateUtils.localFromUtcIso8601(expirationDate!!),
        location = location!!
    )

    private fun getRefByBid(bid: String) : DatabaseReference {
        val coarseHash = bid.substring(0, Location.COARSE_HASH_SIZE)
        val elementId = bid.substring(Location.COARSE_HASH_SIZE)
        return bountiesMetadataRef.child(coarseHash).child(elementId)
    }

    override suspend fun createBounty(
        startingDate: LocalDateTime,
        expirationDate: LocalDateTime,
        location: Location
    ): Bounty {
        authRepository.requireLoggedIn()

        return withContext(ioDispatcher) {
            val currentUser = userRepository.getCurrentUser()

            val coarseHash = location.getCoarseHash()
            val bountyRef = bountiesMetadataRef.child(coarseHash).push()
            val bid = coarseHash + bountyRef.key!!

            val metadata = FirebaseBountyMetadata(
                adminUid = currentUser.id,
                startingDate = DateUtils.utcIso8601FromLocal(startingDate),
                expirationDate = DateUtils.utcIso8601FromLocal(expirationDate),
                location = location
            )

            // Update the metadata first (to ensure bounties is created before referenced)
            bountyRef.child("metadata").setValue(metadata).await()

            // Update the bounties for the current user
            bountiesByUidRef.child(currentUser.id).child(bid).setValue(true).await()

            // Finally return the newly created challenge
            metadata.asExternalModel(bid)
        }
    }

    override fun getTeamRepository(bountyId: String) : TeamsRepositoryInterface = teamsRepositories.get(bountyId)

    override fun getChallengeRepository(bountyId: String): ChallengeRepositoryInterface = challengesRepositories.get(bountyId)

    override fun getClaimRepository(bountyId: String): BountyClaimRepositoryInterface = claimRepositories.get(bountyId)

    override suspend fun getBountyCreatedBy(user: User): List<Bounty> = withContext(ioDispatcher) {
        bountiesByUidRef.child(user.id)
            .get()
            .await().run {
                val bountyIds = (getValue<Map<String, Boolean>>()
                    ?: emptyMap()).mapNotNull { (key, value) -> key.takeIf { value } }

                bountyIds.map {
                    async { getBountyById(it) }
                }.awaitAll()
            }
    }

    override suspend fun getBounties(): List<Bounty> = withContext(ioDispatcher) {
        bountiesMetadataRef.get()
            .await()
            .run {
                children.flatMap { quadrantRef ->
                    quadrantRef.children.mapNotNull {
                        it.getValue<FirebaseBountyMetadata>()?.asExternalModel(quadrantRef.key!! + it.key!!)
                    }
                }
            }
    }

    override suspend fun getBountyById(bid: String): Bounty = withContext(ioDispatcher) {
        getRefByBid(bid).child("metadata").get().await().run {
            (getValue<FirebaseBountyMetadata>() ?: throw BountyNotFoundException(bid))
                .asExternalModel(bid)
        }
    }
}