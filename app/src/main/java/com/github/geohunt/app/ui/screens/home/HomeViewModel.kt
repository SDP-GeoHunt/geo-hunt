package com.github.geohunt.app.ui.screens.home

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.*
import com.github.geohunt.app.data.repository.bounties.BountiesRepositoryInterface
import com.github.geohunt.app.domain.GetUserFeedUseCase
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.User
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.ui.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeViewModel(
    override val authRepository: AuthRepositoryInterface,
    private val getUserFeedUseCase: GetUserFeedUseCase,
    private val challengeRepository: ChallengeRepositoryInterface,
    private val bountiesRepository: BountiesRepositoryInterface
): AuthViewModel(authRepository) {
    private val _challengeFeed: MutableStateFlow<List<Challenge>?> = MutableStateFlow(null)
    val challengeFeed: StateFlow<List<Challenge>?> = _challengeFeed.asStateFlow()

    // Bounties
    private val _bountyList: MutableStateFlow<List<Bounty>?> = MutableStateFlow(null)
    val bountyList = _bountyList.asStateFlow()

    // Bounties challenges
    private val _bountyChallenges: MutableStateFlow<Map<String, List<Challenge>>> = MutableStateFlow(mapOf())
    val bountyChallenges = _bountyChallenges.asStateFlow()

    // Bounties teams number
    private val _nbParticipating: MutableStateFlow<Map<String, Int>> = MutableStateFlow(mapOf())
    val nbParticipating = _nbParticipating.asStateFlow()

    // Refreshing state
    private val _areBountiesRefreshing = MutableStateFlow(true)
    val areBountiesRefreshing = _areBountiesRefreshing.asStateFlow()

    private val authorCache: MutableMap<Challenge, MutableStateFlow<User?>> = mutableMapOf()

    init {
        fetchChallengeFeed()
        refreshBounties()
    }

    private fun fetchChallengeFeed() {
        viewModelScope.launch {
            /*val followList = getUserFeedUseCase.getFollowList()

            getUserFeedUseCase.getFollowFeed(followList).collect {
                _challengeFeed.value = it
            }*/

            getUserFeedUseCase.getDiscoverFeed(Location(46.51958, 6.56398)).collect {
                _challengeFeed.value = it
            }
        }
    }

    fun refreshBounties() {
        viewModelScope.launch {
            _areBountiesRefreshing.value = true
            /*val bountyList = bountiesRepository.getBounties()
            // For each bounties, get the challenges to show them
            bountyList.forEach {
                // Fetch challenges
                _bountyChallenges.putIfAbsent(it.bid, MutableStateFlow(null))
                _bountyChallenges[it.bid]!!.value = bountiesRepository.getChallengeRepository(it).getChallenges()

                // Fetch the number of people participating
                _nbParticipating.putIfAbsent(it.bid, MutableStateFlow(null))
                _nbParticipating[it.bid]!!.value = bountiesRepository.getTeamRepository(it)
                    .getTeams()
                    .first()
                    .sumOf { it.membersUid.size }

            }
            _bountyList.value = bountyList*/
            // MOCK
            val mockedChallengelIst =listOf(
                Challenge("1", "1", "https://picsum.photos/600/600", Location(1.0, 1.0), description = "caca", publishedDate = LocalDateTime.now(), difficulty = Challenge.Difficulty.EASY, expirationDate = null),
                Challenge("1", "1", "https://picsum.photos/600/800", Location(1.0, 1.0), description = "caca", publishedDate = LocalDateTime.now(), difficulty = Challenge.Difficulty.EASY, expirationDate = null),
                Challenge("1", "1", "https://picsum.photos/600/1000", Location(1.0, 1.0), description = "caca", publishedDate = LocalDateTime.now(), difficulty = Challenge.Difficulty.EASY, expirationDate = null),
            )
            _bountyChallenges.value = mapOf("1" to mockedChallengelIst)
            _bountyList.value = listOf(Bounty("1", "1", LocalDateTime.MIN, LocalDateTime.MAX, location = Location(1.0, 1.0)))
            _nbParticipating.value = mapOf("1" to 69)

            _areBountiesRefreshing.value = false
        }
    }

    fun getChallengePhoto(challenge: Challenge): String = challengeRepository.getChallengePhoto(challenge)

    fun getAuthor(challenge: Challenge): StateFlow<User?> {
        if (!authorCache.contains(challenge)) {
            authorCache[challenge] = MutableStateFlow(null)
            viewModelScope.launch {
                authorCache[challenge]!!.value = challengeRepository.getAuthor(challenge)
            }
        }

        return authorCache[challenge]!!.asStateFlow()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                HomeViewModel(
                    container.auth,
                    container.feedUseCase,
                    container.challenges,
                    container.bounties
                )
            }
        }
    }
}