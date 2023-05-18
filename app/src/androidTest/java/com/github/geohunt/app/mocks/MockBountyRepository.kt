package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.repository.ChallengeRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.BountiesRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.BountyClaimRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.TeamsRepositoryInterface
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.User
import java.time.LocalDateTime

open class MockBountyRepository : BountiesRepositoryInterface {
    var name: String = "<Bounty-Name>"
    var teamRepo = MockTeamRepository()
    var mockChallenge = MockChallengeRepository()

    override suspend fun createBounty(
        name: String,
        startingDate: LocalDateTime,
        expirationDate: LocalDateTime,
        location: Location
    ): Bounty {
        TODO("Not yet implemented")
    }

    override fun getTeamRepository(bountyId: String): TeamsRepositoryInterface {
        if (bountyId == "1") {
            return teamRepo
        }
        else {
            throw NoSuchElementException()
        }
    }

    override fun getChallengeRepository(bountyId: String): ChallengeRepositoryInterface {
        return mockChallenge
    }

    override fun getClaimRepository(bountyId: String): BountyClaimRepositoryInterface {
        TODO("Not yet implemented")
    }

    override suspend fun getBountyCreatedBy(user: User): List<Bounty> {
        return if (user.id == "1") {
            listOf(getBountyById("1"))
        } else {
            listOf()
        }
    }

    override suspend fun renameBounty(bounty: Bounty, name: String) {
        if (bounty.bid == "1") {
            this.name = name
        }
    }

    override suspend fun getBounties(): List<Bounty> {
        return listOf(
            getBountyById("1")
        )
    }

    override suspend fun getBountyById(bid: String): Bounty {
        return if (bid == "1") {
            Bounty(
                "1",
                name,
                "1",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5),
                Location(0.0, 0.0)
            )
        }
        else {
            throw NoSuchElementException()
        }
    }
}