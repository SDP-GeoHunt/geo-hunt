package com.github.geohunt.app.domain

import com.github.geohunt.app.data.repository.ActiveHuntsRepository
import com.github.geohunt.app.data.repository.ClaimRepository
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.ui.components.buttons.ChallengeHuntState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking

class GetChallengeHuntStateUseCaseTest {
    @Test
    fun huntStateChecksClaimStateFirst() = runTest {
        val mockClaim: ClaimRepository = mock {
            onBlocking { doesClaim(any()) } doReturn true
        }
        val mockActiveHunts: ActiveHuntsRepository = mock()

        val useCase = GetChallengeHuntStateUseCase(mockClaim, mockActiveHunts)
        val challenge: Challenge = mock()
        useCase.getChallengeHuntState(challenge).first()

        verifyBlocking(mockClaim) { doesClaim(challenge) }
    }

    @Test
    fun huntStateIsClaimedOnClaimedChallenge() = runTest {
        val mockClaim: ClaimRepository = mock {
            onBlocking { doesClaim(any()) } doReturn true
        }
        val mockActiveHunts: ActiveHuntsRepository = mock()
        val useCase = GetChallengeHuntStateUseCase(mockClaim, mockActiveHunts)

        assertThat(useCase.getChallengeHuntState(mock()).first(), equalTo(ChallengeHuntState.CLAIMED))
    }

    @Test
    fun huntStateChecksActiveHuntsOnUnclaimedChallenge() = runTest {
        val mockClaim: ClaimRepository = mock {
            onBlocking { doesClaim(any()) } doReturn false
        }
        val mockActiveHunts: ActiveHuntsRepository = mock {
            onBlocking { isHunting(any()) } doReturn flowOf(true)
        }

        val useCase = GetChallengeHuntStateUseCase(mockClaim, mockActiveHunts)
        val challenge: Challenge = mock()
        useCase.getChallengeHuntState(challenge).first()

        verifyBlocking(mockActiveHunts) { isHunting(challenge) }
    }

    @Test
    fun huntStateIsHuntedOnHuntedChallenge() = runTest {
        val mockClaim: ClaimRepository = mock {
            onBlocking { doesClaim(any()) } doReturn false
        }
        val mockActiveHunts: ActiveHuntsRepository = mock {
            onBlocking { isHunting(any()) } doReturn flowOf(true)
        }

        val useCase = GetChallengeHuntStateUseCase(mockClaim, mockActiveHunts)

        assertThat(useCase.getChallengeHuntState(mock()).first(), equalTo(ChallengeHuntState.HUNTED))
    }

    @Test
    fun huntStateIsNotHuntedOnUnhuntedChallenge() = runTest {
        val mockClaim: ClaimRepository = mock {
            onBlocking { doesClaim(any()) } doReturn false
        }
        val mockActiveHunts: ActiveHuntsRepository = mock {
            onBlocking { isHunting(any()) } doReturn flowOf(false)
        }

        val useCase = GetChallengeHuntStateUseCase(mockClaim, mockActiveHunts)

        assertThat(useCase.getChallengeHuntState(mock()).first(), equalTo(ChallengeHuntState.NOT_HUNTED))
    }
}