package com.github.geohunt.app.ui.screens.home

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.geohunt.app.data.repository.ActiveHuntsRepositoryInterface
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.AuthRepositoryInterface
import com.github.geohunt.app.data.repository.ChallengeRepositoryInterface
import com.github.geohunt.app.data.repository.FollowRepository
import com.github.geohunt.app.data.repository.LocationRepositoryInterface
import com.github.geohunt.app.domain.GetChallengeHuntStateUseCase
import com.github.geohunt.app.domain.GetUserFeedUseCase
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.AuthViewModel
import com.github.geohunt.app.ui.components.appbar.HomeScreenFeed
import com.github.geohunt.app.ui.components.buttons.ChallengeHuntState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * Represents a scrolling feed of challenges.
 */
class Feed(
    private val challengeFlow: Flow<List<Challenge>?>,
    private val coroutineScope: CoroutineScope,
    private val activeHuntsRepository: ActiveHuntsRepositoryInterface,
    val challengeRepository: ChallengeRepositoryInterface,
    private val getChallengeHuntStateUseCase: GetChallengeHuntStateUseCase
) {
    /**
     * Inner state representation of a challenge.
     *
     * Holds state about all UI elements relevant to the challenge, e.g. author, hunt state, etc.
     */
    private class ChallengeData(
        val _author: MutableStateFlow<User?> = MutableStateFlow(null),
        val _huntState: MutableStateFlow<ChallengeHuntState> = MutableStateFlow(ChallengeHuntState.UNKNOWN),
        val _isBusy: MutableStateFlow<Boolean> = MutableStateFlow(false)
    ) {
        val author: StateFlow<User?>
            get() = _author.asStateFlow()

        val huntState: StateFlow<ChallengeHuntState>
            get() = _huntState.asStateFlow()

        val isBusy: StateFlow<Boolean>
            get() = _isBusy.asStateFlow()
    }

    init {
        coroutineScope.launch {
            challengeFlow.collect {
                _challenges.value = it
            }
        }
    }

    private val _challenges: MutableStateFlow<List<Challenge>?> = MutableStateFlow(null)
    val challenges: StateFlow<List<Challenge>?> = _challenges.asStateFlow()

    private val challengeCache: MutableMap<Challenge, ChallengeData> = mutableMapOf()

    private fun fetchChallengeData(challenge: Challenge) {
        challengeCache[challenge] = ChallengeData()
        coroutineScope.launch {
            challengeCache[challenge]!!._author.value = challengeRepository.getAuthor(challenge)

            getChallengeHuntStateUseCase.getChallengeHuntState(challenge).collect {
                challengeCache[challenge]!!._huntState.value = it
            }
        }
    }

    fun getAuthor(challenge: Challenge): StateFlow<User?> {
        if (!challengeCache.containsKey(challenge)) {
            fetchChallengeData(challenge)
        }

        return challengeCache[challenge]!!.author
    }

    fun getChallengeHuntState(challenge: Challenge): StateFlow<ChallengeHuntState> {
        if (!challengeCache.containsKey(challenge)) {
            fetchChallengeData(challenge)
        }

        return challengeCache[challenge]!!.huntState
    }

    fun isBusy(challenge: Challenge): StateFlow<Boolean> {
        return challengeCache[challenge]!!.isBusy
    }

    fun hunt(challenge: Challenge) {
        challengeCache[challenge]!!._isBusy.value = true

        coroutineScope.launch {
            activeHuntsRepository.joinHunt(challenge)
            challengeCache[challenge]!!._isBusy.value = false
        }
    }

    fun leaveHunt(challenge: Challenge) {
        challengeCache[challenge]!!._isBusy.value = true
        coroutineScope.launch {
            activeHuntsRepository.leaveHunt(challenge)
            challengeCache[challenge]!!._isBusy.value = false
        }
    }
}

class HomeFeedViewModel(
    override val authRepository: AuthRepositoryInterface,
    private val getUserFeedUseCase: GetUserFeedUseCase,
    private val getChallengeHuntStateUseCase: GetChallengeHuntStateUseCase,
    private val challengeRepository: ChallengeRepositoryInterface,
    private val locationRepository: LocationRepositoryInterface,
    private val followRepository: FollowRepository,
    private val activeHuntsRepository: ActiveHuntsRepositoryInterface
): AuthViewModel(authRepository) {
    private val _userLocation: MutableStateFlow<Location?> = MutableStateFlow(null)
    val userLocation: StateFlow<Location?> = _userLocation.asStateFlow()

    private fun fetchLocation() {
        viewModelScope.launch {
            locationRepository.getLocations(viewModelScope).collect {
                _userLocation.value = it
            }
        }
    }

    init {
        fetchLocation()
    }

    private val feeds: MutableMap<HomeScreenFeed, Feed> = mutableMapOf()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFeed(feed: HomeScreenFeed): Feed {
        if (!feeds.contains(feed)) {
            feeds[feed] = when(feed) {
                HomeScreenFeed.Home -> Feed(
                    challengeFlow = followRepository.getFollowList().flatMapLatest { getUserFeedUseCase.getFollowFeed(it) },
                    coroutineScope = viewModelScope,
                    activeHuntsRepository, challengeRepository, getChallengeHuntStateUseCase
                )

                HomeScreenFeed.Discover -> Feed(
                    challengeFlow = userLocation.flatMapLatest {
                        if (it != null) getUserFeedUseCase.getDiscoverFeed(it) else flowOf(null)
                    },
                    coroutineScope = viewModelScope,
                    activeHuntsRepository, challengeRepository, getChallengeHuntStateUseCase
                )

                HomeScreenFeed.Bounties -> throw UnsupportedOperationException()
            }
        }

        return feeds[feed]!!
    }

    fun follow(user: User) {
        viewModelScope.launch {
            followRepository.follow(user)
        }
    }

    fun unfollow(user: User) {
        viewModelScope.launch {
            followRepository.unfollow(user)
        }
    }

    fun isFollowing(user: User?): Flow<Boolean> {
        return if (user != null) followRepository.doesFollow(user) else flowOf(false)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val container = AppContainer.getInstance(application)

                HomeFeedViewModel(
                    container.auth,
                    container.feedUseCase,
                    container.huntStateUseCase,
                    container.challenges,
                    container.location,
                    container.follow,
                    container.activeHunts
                )
            }
        }
    }
}