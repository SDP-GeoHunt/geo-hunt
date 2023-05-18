package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.repository.bounties.BountyClaimRepositoryInterface
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.Team
import java.time.LocalDateTime

class MockBountyClaimRepository : BountyClaimRepositoryInterface {

    override suspend fun claimChallenge(
        photo: LocalPicture,
        challenge: Challenge,
        location: Location
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

    override suspend fun getClaimById(id: String): Claim {
        // return a default claim, you can customize it
        return Claim(
            id = "some-claim-id",
            parentChallengeId = "challenge-id",
            claimerId = "claimer-id",
            photoUrl = "http://10.0.2.2:9199/geohunt-1.appspot.com/images/claim-image.png",
            claimDate = LocalDateTime.now(),
            distance = 10,
            awardedPoints = 5000
        )
    }

    override suspend fun getClaimsOf(team: Team): List<Claim> {
        // return an empty list or a list of default claims
        return emptyList()
    }
}
