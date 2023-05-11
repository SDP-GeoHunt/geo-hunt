package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.ChallengeRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.BountiesRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.BountyClaimRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.TeamsRepositoryInterface
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.User
import java.time.LocalDateTime

open class MockBountiesRepositories(): BountiesRepositoryInterface {
    override suspend fun createBounty(
        startingDate: LocalDateTime,
        expirationDate: LocalDateTime,
        location: Location,
    ): Bounty {
        TODO("Not yet implemented")
    }

    override fun getTeamRepository(bountyId: String): TeamsRepositoryInterface {
        TODO("Not yet implemented")
    }

    override fun getChallengeRepository(bountyId: String): ChallengeRepositoryInterface {
        TODO("Not yet implemented")
    }

    override fun getClaimRepository(bountyId: String): BountyClaimRepositoryInterface {
        TODO("Not yet implemented")
    }

    override suspend fun getBountyCreatedBy(user: User): List<Bounty> {
        TODO("Not yet implemented")
    }

    override suspend fun getBounties(): List<Bounty> {
        TODO("Not yet implemented")
    }

    override suspend fun getBountyById(bid: String): Bounty {
        TODO("Not yet implemented")
    }

}