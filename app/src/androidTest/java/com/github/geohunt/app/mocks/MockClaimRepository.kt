package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.repository.ClaimRepositoryInterface
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import com.github.geohunt.app.model.Location
import java.time.LocalDateTime

open class MockClaimRepository(val listClaim : List<Claim> = listOf()) : ClaimRepositoryInterface {

    override suspend fun getClaimId(uid: String): List<String> {
        return listClaim.map { it.id }
    }

    override suspend fun doesClaim(challenge: Challenge): Boolean {
        return false
    }

    override suspend fun getScore(user: User): Long {
        return 0
    }

    override suspend fun getClaims(uid: String): List<Claim> {
        return listClaim
    }

    override suspend fun getChallengeClaims(challenge: Challenge): List<Claim> {
        return listClaim.filter { it.parentChallengeId == challenge.id }
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
