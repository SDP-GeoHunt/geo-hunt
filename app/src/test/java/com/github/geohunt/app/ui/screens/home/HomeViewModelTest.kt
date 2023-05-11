package com.github.geohunt.app.ui.screens.home

import com.github.geohunt.app.data.repository.ChallengeRepositoryInterface
import com.github.geohunt.app.data.repository.bounties.TeamsRepositoryInterface
import com.github.geohunt.app.domain.GetUserFeedUseCase
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockBountiesRepositories
import com.github.geohunt.app.mocks.MockChallengeRepository
import com.github.geohunt.app.mocks.MockFollowRepository
import com.github.geohunt.app.mocks.MockTeamRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.Team
import com.github.geohunt.app.test_utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class HomeViewModelTest {
    
    class BountiesTest {
        @OptIn(ExperimentalCoroutinesApi::class)
        @get:Rule
        var mainCoroutineRule = MainCoroutineRule()


        private fun createFakeViewModel(): HomeViewModel {
            val authRep = MockAuthRepository()
            val userRep = MockUserRepository()
            val chalRep = MockChallengeRepository()
            val followRep = MockFollowRepository()


            val bountiesRep = object: MockBountiesRepositories() {
                override suspend fun getBounties(): List<Bounty> {
                    return listOf(
                        Bounty("1", "1", LocalDateTime.MIN, LocalDateTime.MAX, Location(0.0, 0.0)),
                        Bounty("2", "2", LocalDateTime.MIN, LocalDateTime.MAX, Location(0.0, 0.0)),
                        Bounty("3", "3", LocalDateTime.MIN, LocalDateTime.MAX, Location(0.0, 0.0))
                    )
                }

                override fun getChallengeRepository(bountyId: String): ChallengeRepositoryInterface {
                    return MockChallengeRepository()
                }

                override fun getTeamRepository(bountyId: String): TeamsRepositoryInterface {
                    return MockTeamRepository(listOf(Team("1", listOf("1", "2", "3"), "1", 1000)))
                }
            }

            val getUserFeedUseCase = GetUserFeedUseCase(authRep, chalRep, followRep)
            return HomeViewModel(
                authRep,
                userRep,
                getUserFeedUseCase,
                chalRep,
                bountiesRep
            )
        }
        
        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        fun fetchesCorrectlyBounties() = runTest {
            val model = createFakeViewModel()
            val bountyList = model.bountyList.filter { it != null }.first()!!

            assert(bountyList.size == 3)
            // The above waits for the bounty list to be updated. Normally,
            // the bounty challenges should contain every information at this point
            val bountyChallenges = model.bountyChallenges
                .filter { it.isNotEmpty() }
                .first()
            // Assert all bounties have an entry in the bounty challenges
            assert(bountyList.all { bountyChallenges.containsKey(it.bid) })
            // Verify the entry (MockChallengeRepository contains a single challenge)
            assert(bountyChallenges.all { it.value.size == 1 })

            val nbParticipating = model.nbParticipating.first()

            // Number of participating is correct
            // 1. defined
            assert(bountyList.all { nbParticipating.containsKey(it.bid) })
            // 2. contains 1
            assert(nbParticipating.all { it.value == 3 })

            // Authors are properly fetched
            val authors = model.bountyAuthors.first()
            assert(authors.size == 3) // 3 bounties
            assert(authors.all { it.value.id == it.key }) // bountyId = adminUid
        }
    }
}