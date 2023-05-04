package com.github.geohunt.app.mocks

import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.data.repository.ChallengeRepositoryInterface
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import com.github.geohunt.app.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime

open class MockChallengeRepository(private val mockedChallenge: Challenge = MockChallenge()): ChallengeRepositoryInterface {
    override suspend fun getChallenge(id: String): Challenge {
        return mockedChallenge
    }

    override fun getSectorChallenges(sector: String): Flow<List<Challenge>> {
        return flowOf(listOf())
    }

    override suspend fun getAuthor(challenge: Challenge): User {
        return MockAuthRepository.defaultLoggedUser
    }

    override fun getChallengePhoto(challenge: Challenge): String {
        return ""
    }

    override fun getPosts(userId: String): Flow<List<Challenge>> {
        return flowOf(listOf())
    }

    override suspend fun getClaims(challenge: Challenge): List<Claim> {
        return listOf()
    }

    override suspend fun createChallenge(
        photo: LocalPicture,
        location: Location,
        difficulty: Challenge.Difficulty,
        expirationDate: LocalDateTime?,
        description: String?,
    ): Challenge {
        TODO("Not yet implemented")
    }

    override fun getClaimsFromUser(uid: String): List<Claim> {
        return listOf()
    }
}