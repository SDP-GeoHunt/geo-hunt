package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.repository.ClaimRepositoryInterface
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import com.github.geohunt.app.model.database.api.Location
import java.time.LocalDateTime

open class MockClaimRepository : ClaimRepositoryInterface {

    override suspend fun getClaimIdByUser(user: User): List<String> {
        return emptyList()
    }

    override suspend fun doesClaims(challenge: Challenge): Boolean {
        return false
    }

    override suspend fun getScoreFromUser(user: User): Long {
        return 0
    }

    override suspend fun getClaimsByUser(user: User): List<Claim> {
        return emptyList()
    }

    override suspend fun getClaimsByChallenge(challenge: Challenge): List<Claim> {
        return emptyList()
    }

    override suspend fun claimChallenge(
        photo: LocalPicture,
        location: Location,
        challenge: Challenge
    ): Claim {
        return Claim(
            id = "new-claim-id",
            parentChallengeId = challenge.id,
            claimerId = "dn2",
            photoUrl = "http://10.0.2.2:9199/geohunt-1.appspot.com/images/challenges-images.png",
            claimDate = LocalDateTime.now(),
            distance = 10,
            awardedPoints = 4835
        )
    }

}
